package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class FixedHeightLayoutCache extends AbstractLayoutCache {
   private FixedHeightLayoutCache.FHTreeStateNode root;
   private int rowCount;
   private Rectangle boundsBuffer = new Rectangle();
   private Hashtable<TreePath, FixedHeightLayoutCache.FHTreeStateNode> treePathMapping = new Hashtable();
   private FixedHeightLayoutCache.SearchInfo info = new FixedHeightLayoutCache.SearchInfo();
   private Stack<Stack<TreePath>> tempStacks = new Stack();

   public FixedHeightLayoutCache() {
      this.setRowHeight(1);
   }

   public void setModel(TreeModel var1) {
      super.setModel(var1);
      this.rebuild(false);
   }

   public void setRootVisible(boolean var1) {
      if (this.isRootVisible() != var1) {
         super.setRootVisible(var1);
         if (this.root != null) {
            if (var1) {
               ++this.rowCount;
               this.root.adjustRowBy(1);
            } else {
               --this.rowCount;
               this.root.adjustRowBy(-1);
            }

            this.visibleNodesChanged();
         }
      }

   }

   public void setRowHeight(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("FixedHeightLayoutCache only supports row heights greater than 0");
      } else {
         if (this.getRowHeight() != var1) {
            super.setRowHeight(var1);
            this.visibleNodesChanged();
         }

      }
   }

   public int getRowCount() {
      return this.rowCount;
   }

   public void invalidatePathBounds(TreePath var1) {
   }

   public void invalidateSizes() {
      this.visibleNodesChanged();
   }

   public boolean isExpanded(TreePath var1) {
      if (var1 == null) {
         return false;
      } else {
         FixedHeightLayoutCache.FHTreeStateNode var2 = this.getNodeForPath(var1, true, false);
         return var2 != null && var2.isExpanded();
      }
   }

   public Rectangle getBounds(TreePath var1, Rectangle var2) {
      if (var1 == null) {
         return null;
      } else {
         FixedHeightLayoutCache.FHTreeStateNode var3 = this.getNodeForPath(var1, true, false);
         if (var3 != null) {
            return this.getBounds(var3, -1, var2);
         } else {
            TreePath var4 = var1.getParentPath();
            var3 = this.getNodeForPath(var4, true, false);
            if (var3 != null && var3.isExpanded()) {
               int var5 = this.treeModel.getIndexOfChild(var4.getLastPathComponent(), var1.getLastPathComponent());
               if (var5 != -1) {
                  return this.getBounds(var3, var5, var2);
               }
            }

            return null;
         }
      }
   }

   public TreePath getPathForRow(int var1) {
      return var1 >= 0 && var1 < this.getRowCount() && this.root.getPathForRow(var1, this.getRowCount(), this.info) ? this.info.getPath() : null;
   }

   public int getRowForPath(TreePath var1) {
      if (var1 != null && this.root != null) {
         FixedHeightLayoutCache.FHTreeStateNode var2 = this.getNodeForPath(var1, true, false);
         if (var2 != null) {
            return var2.getRow();
         } else {
            TreePath var3 = var1.getParentPath();
            var2 = this.getNodeForPath(var3, true, false);
            return var2 != null && var2.isExpanded() ? var2.getRowToModelIndex(this.treeModel.getIndexOfChild(var3.getLastPathComponent(), var1.getLastPathComponent())) : -1;
         }
      } else {
         return -1;
      }
   }

   public TreePath getPathClosestTo(int var1, int var2) {
      if (this.getRowCount() == 0) {
         return null;
      } else {
         int var3 = this.getRowContainingYLocation(var2);
         return this.getPathForRow(var3);
      }
   }

   public int getVisibleChildCount(TreePath var1) {
      FixedHeightLayoutCache.FHTreeStateNode var2 = this.getNodeForPath(var1, true, false);
      return var2 == null ? 0 : var2.getTotalChildCount();
   }

   public Enumeration<TreePath> getVisiblePathsFrom(TreePath var1) {
      if (var1 == null) {
         return null;
      } else {
         FixedHeightLayoutCache.FHTreeStateNode var2 = this.getNodeForPath(var1, true, false);
         if (var2 != null) {
            return new FixedHeightLayoutCache.VisibleFHTreeStateNodeEnumeration(var2);
         } else {
            TreePath var3 = var1.getParentPath();
            var2 = this.getNodeForPath(var3, true, false);
            return var2 != null && var2.isExpanded() ? new FixedHeightLayoutCache.VisibleFHTreeStateNodeEnumeration(var2, this.treeModel.getIndexOfChild(var3.getLastPathComponent(), var1.getLastPathComponent())) : null;
         }
      }
   }

   public void setExpandedState(TreePath var1, boolean var2) {
      if (var2) {
         this.ensurePathIsExpanded(var1, true);
      } else if (var1 != null) {
         TreePath var3 = var1.getParentPath();
         FixedHeightLayoutCache.FHTreeStateNode var4;
         if (var3 != null) {
            var4 = this.getNodeForPath(var3, false, true);
            if (var4 != null) {
               var4.makeVisible();
            }
         }

         var4 = this.getNodeForPath(var1, true, false);
         if (var4 != null) {
            var4.collapse(true);
         }
      }

   }

   public boolean getExpandedState(TreePath var1) {
      FixedHeightLayoutCache.FHTreeStateNode var2 = this.getNodeForPath(var1, true, false);
      return var2 != null ? var2.isVisible() && var2.isExpanded() : false;
   }

   public void treeNodesChanged(TreeModelEvent var1) {
      if (var1 != null) {
         FixedHeightLayoutCache.FHTreeStateNode var3 = this.getNodeForPath(SwingUtilities2.getTreePath(var1, this.getModel()), false, false);
         int[] var2 = var1.getChildIndices();
         if (var3 != null) {
            int var4;
            if (var2 != null && (var4 = var2.length) > 0) {
               Object var5 = var3.getUserObject();

               for(int var6 = 0; var6 < var4; ++var6) {
                  FixedHeightLayoutCache.FHTreeStateNode var7 = var3.getChildAtModelIndex(var2[var6]);
                  if (var7 != null) {
                     var7.setUserObject(this.treeModel.getChild(var5, var2[var6]));
                  }
               }

               if (var3.isVisible() && var3.isExpanded()) {
                  this.visibleNodesChanged();
               }
            } else if (var3 == this.root && var3.isVisible() && var3.isExpanded()) {
               this.visibleNodesChanged();
            }
         }
      }

   }

   public void treeNodesInserted(TreeModelEvent var1) {
      if (var1 != null) {
         FixedHeightLayoutCache.FHTreeStateNode var3 = this.getNodeForPath(SwingUtilities2.getTreePath(var1, this.getModel()), false, false);
         int[] var2 = var1.getChildIndices();
         int var4;
         if (var3 != null && var2 != null && (var4 = var2.length) > 0) {
            boolean var5 = var3.isVisible() && var3.isExpanded();

            for(int var6 = 0; var6 < var4; ++var6) {
               var3.childInsertedAtModelIndex(var2[var6], var5);
            }

            if (var5 && this.treeSelectionModel != null) {
               this.treeSelectionModel.resetRowSelection();
            }

            if (var3.isVisible()) {
               this.visibleNodesChanged();
            }
         }
      }

   }

   public void treeNodesRemoved(TreeModelEvent var1) {
      if (var1 != null) {
         TreePath var4 = SwingUtilities2.getTreePath(var1, this.getModel());
         FixedHeightLayoutCache.FHTreeStateNode var5 = this.getNodeForPath(var4, false, false);
         int[] var2 = var1.getChildIndices();
         int var3;
         if (var5 != null && var2 != null && (var3 = var2.length) > 0) {
            Object[] var6 = var1.getChildren();
            boolean var7 = var5.isVisible() && var5.isExpanded();

            for(int var8 = var3 - 1; var8 >= 0; --var8) {
               var5.removeChildAtModelIndex(var2[var8], var7);
            }

            if (var7) {
               if (this.treeSelectionModel != null) {
                  this.treeSelectionModel.resetRowSelection();
               }

               if (this.treeModel.getChildCount(var5.getUserObject()) == 0 && var5.isLeaf()) {
                  var5.collapse(false);
               }

               this.visibleNodesChanged();
            } else if (var5.isVisible()) {
               this.visibleNodesChanged();
            }
         }
      }

   }

   public void treeStructureChanged(TreeModelEvent var1) {
      if (var1 != null) {
         TreePath var2 = SwingUtilities2.getTreePath(var1, this.getModel());
         FixedHeightLayoutCache.FHTreeStateNode var3 = this.getNodeForPath(var2, false, false);
         if (var3 == this.root || var3 == null && (var2 == null && this.treeModel != null && this.treeModel.getRoot() == null || var2 != null && var2.getPathCount() <= 1)) {
            this.rebuild(true);
         } else if (var3 != null) {
            FixedHeightLayoutCache.FHTreeStateNode var6 = (FixedHeightLayoutCache.FHTreeStateNode)var3.getParent();
            boolean var4 = var3.isExpanded();
            boolean var5 = var3.isVisible();
            int var7 = var6.getIndex(var3);
            var3.collapse(false);
            var6.remove(var7);
            if (var5 && var4) {
               int var8 = var3.getRow();
               var6.resetChildrenRowsFrom(var8, var7, var3.getChildIndex());
               var3 = this.getNodeForPath(var2, false, true);
               var3.expand();
            }

            if (this.treeSelectionModel != null && var5 && var4) {
               this.treeSelectionModel.resetRowSelection();
            }

            if (var5) {
               this.visibleNodesChanged();
            }
         }
      }

   }

   private void visibleNodesChanged() {
   }

   private Rectangle getBounds(FixedHeightLayoutCache.FHTreeStateNode var1, int var2, Rectangle var3) {
      boolean var4;
      int var5;
      int var6;
      Object var7;
      if (var2 == -1) {
         var6 = var1.getRow();
         var7 = var1.getUserObject();
         var4 = var1.isExpanded();
         var5 = var1.getLevel();
      } else {
         var6 = var1.getRowToModelIndex(var2);
         var7 = this.treeModel.getChild(var1.getUserObject(), var2);
         var4 = false;
         var5 = var1.getLevel() + 1;
      }

      Rectangle var8 = this.getNodeDimensions(var7, var6, var5, var4, this.boundsBuffer);
      if (var8 == null) {
         return null;
      } else {
         if (var3 == null) {
            var3 = new Rectangle();
         }

         var3.x = var8.x;
         var3.height = this.getRowHeight();
         var3.y = var6 * var3.height;
         var3.width = var8.width;
         return var3;
      }
   }

   private void adjustRowCountBy(int var1) {
      this.rowCount += var1;
   }

   private void addMapping(FixedHeightLayoutCache.FHTreeStateNode var1) {
      this.treePathMapping.put(var1.getTreePath(), var1);
   }

   private void removeMapping(FixedHeightLayoutCache.FHTreeStateNode var1) {
      this.treePathMapping.remove(var1.getTreePath());
   }

   private FixedHeightLayoutCache.FHTreeStateNode getMapping(TreePath var1) {
      return (FixedHeightLayoutCache.FHTreeStateNode)this.treePathMapping.get(var1);
   }

   private void rebuild(boolean var1) {
      this.treePathMapping.clear();
      Object var2;
      if (this.treeModel != null && (var2 = this.treeModel.getRoot()) != null) {
         this.root = this.createNodeForValue(var2, 0);
         this.root.path = new TreePath(var2);
         this.addMapping(this.root);
         if (this.isRootVisible()) {
            this.rowCount = 1;
            this.root.row = 0;
         } else {
            this.rowCount = 0;
            this.root.row = -1;
         }

         this.root.expand();
      } else {
         this.root = null;
         this.rowCount = 0;
      }

      if (var1 && this.treeSelectionModel != null) {
         this.treeSelectionModel.clearSelection();
      }

      this.visibleNodesChanged();
   }

   private int getRowContainingYLocation(int var1) {
      return this.getRowCount() == 0 ? -1 : Math.max(0, Math.min(this.getRowCount() - 1, var1 / this.getRowHeight()));
   }

   private boolean ensurePathIsExpanded(TreePath var1, boolean var2) {
      if (var1 != null) {
         if (this.treeModel.isLeaf(var1.getLastPathComponent())) {
            var1 = var1.getParentPath();
            var2 = true;
         }

         if (var1 != null) {
            FixedHeightLayoutCache.FHTreeStateNode var3 = this.getNodeForPath(var1, false, true);
            if (var3 != null) {
               var3.makeVisible();
               if (var2) {
                  var3.expand();
               }

               return true;
            }
         }
      }

      return false;
   }

   private FixedHeightLayoutCache.FHTreeStateNode createNodeForValue(Object var1, int var2) {
      return new FixedHeightLayoutCache.FHTreeStateNode(var1, var2, -1);
   }

   private FixedHeightLayoutCache.FHTreeStateNode getNodeForPath(TreePath var1, boolean var2, boolean var3) {
      if (var1 != null) {
         FixedHeightLayoutCache.FHTreeStateNode var4 = this.getMapping(var1);
         if (var4 != null) {
            return var2 && !var4.isVisible() ? null : var4;
         } else if (var2) {
            return null;
         } else {
            Stack var5;
            if (this.tempStacks.size() == 0) {
               var5 = new Stack();
            } else {
               var5 = (Stack)this.tempStacks.pop();
            }

            try {
               var5.push(var1);
               var1 = var1.getParentPath();

               for(var4 = null; var1 != null; var1 = var1.getParentPath()) {
                  var4 = this.getMapping(var1);
                  if (var4 != null) {
                     while(var4 != null && var5.size() > 0) {
                        var1 = (TreePath)var5.pop();
                        var4 = var4.createChildFor(var1.getLastPathComponent());
                     }

                     FixedHeightLayoutCache.FHTreeStateNode var6 = var4;
                     return var6;
                  }

                  var5.push(var1);
               }
            } finally {
               var5.removeAllElements();
               this.tempStacks.push(var5);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private class VisibleFHTreeStateNodeEnumeration implements Enumeration<TreePath> {
      protected FixedHeightLayoutCache.FHTreeStateNode parent;
      protected int nextIndex;
      protected int childCount;

      protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache.FHTreeStateNode var2) {
         this(var2, -1);
      }

      protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache.FHTreeStateNode var2, int var3) {
         this.parent = var2;
         this.nextIndex = var3;
         this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
      }

      public boolean hasMoreElements() {
         return this.parent != null;
      }

      public TreePath nextElement() {
         if (!this.hasMoreElements()) {
            throw new NoSuchElementException("No more visible paths");
         } else {
            TreePath var1;
            if (this.nextIndex == -1) {
               var1 = this.parent.getTreePath();
            } else {
               FixedHeightLayoutCache.FHTreeStateNode var2 = this.parent.getChildAtModelIndex(this.nextIndex);
               if (var2 == null) {
                  var1 = this.parent.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.parent.getUserObject(), this.nextIndex));
               } else {
                  var1 = var2.getTreePath();
               }
            }

            this.updateNextObject();
            return var1;
         }
      }

      protected void updateNextObject() {
         if (!this.updateNextIndex()) {
            this.findNextValidParent();
         }

      }

      protected boolean findNextValidParent() {
         if (this.parent == FixedHeightLayoutCache.this.root) {
            this.parent = null;
            return false;
         } else {
            while(this.parent != null) {
               FixedHeightLayoutCache.FHTreeStateNode var1 = (FixedHeightLayoutCache.FHTreeStateNode)this.parent.getParent();
               if (var1 != null) {
                  this.nextIndex = this.parent.childIndex;
                  this.parent = var1;
                  this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
                  if (this.updateNextIndex()) {
                     return true;
                  }
               } else {
                  this.parent = null;
               }
            }

            return false;
         }
      }

      protected boolean updateNextIndex() {
         if (this.nextIndex == -1 && !this.parent.isExpanded()) {
            return false;
         } else if (this.childCount == 0) {
            return false;
         } else if (++this.nextIndex >= this.childCount) {
            return false;
         } else {
            FixedHeightLayoutCache.FHTreeStateNode var1 = this.parent.getChildAtModelIndex(this.nextIndex);
            if (var1 != null && var1.isExpanded()) {
               this.parent = var1;
               this.nextIndex = -1;
               this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(var1.getUserObject());
            }

            return true;
         }
      }
   }

   private class SearchInfo {
      protected FixedHeightLayoutCache.FHTreeStateNode node;
      protected boolean isNodeParentNode;
      protected int childIndex;

      private SearchInfo() {
      }

      protected TreePath getPath() {
         if (this.node == null) {
            return null;
         } else {
            return this.isNodeParentNode ? this.node.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.node.getUserObject(), this.childIndex)) : this.node.path;
         }
      }

      // $FF: synthetic method
      SearchInfo(Object var2) {
         this();
      }
   }

   private class FHTreeStateNode extends DefaultMutableTreeNode {
      protected boolean isExpanded;
      protected int childIndex;
      protected int childCount;
      protected int row;
      protected TreePath path;

      public FHTreeStateNode(Object var2, int var3, int var4) {
         super(var2);
         this.childIndex = var3;
         this.row = var4;
      }

      public void setParent(MutableTreeNode var1) {
         super.setParent(var1);
         if (var1 != null) {
            this.path = ((FixedHeightLayoutCache.FHTreeStateNode)var1).getTreePath().pathByAddingChild(this.getUserObject());
            FixedHeightLayoutCache.this.addMapping(this);
         }

      }

      public void remove(int var1) {
         FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var1);
         var2.removeFromMapping();
         super.remove(var1);
      }

      public void setUserObject(Object var1) {
         super.setUserObject(var1);
         if (this.path != null) {
            FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
            if (var2 != null) {
               this.resetChildrenPaths(var2.getTreePath());
            } else {
               this.resetChildrenPaths((TreePath)null);
            }
         }

      }

      public int getChildIndex() {
         return this.childIndex;
      }

      public TreePath getTreePath() {
         return this.path;
      }

      public FixedHeightLayoutCache.FHTreeStateNode getChildAtModelIndex(int var1) {
         for(int var2 = this.getChildCount() - 1; var2 >= 0; --var2) {
            if (((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var2)).childIndex == var1) {
               return (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var2);
            }
         }

         return null;
      }

      public boolean isVisible() {
         FixedHeightLayoutCache.FHTreeStateNode var1 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         if (var1 == null) {
            return true;
         } else {
            return var1.isExpanded() && var1.isVisible();
         }
      }

      public int getRow() {
         return this.row;
      }

      public int getRowToModelIndex(int var1) {
         int var3 = this.getRow() + 1;
         int var5 = 0;

         for(int var6 = this.getChildCount(); var5 < var6; ++var5) {
            FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var5);
            if (var2.childIndex >= var1) {
               if (var2.childIndex == var1) {
                  return var2.row;
               }

               if (var5 == 0) {
                  return this.getRow() + 1 + var1;
               }

               return var2.row - (var2.childIndex - var1);
            }
         }

         return this.getRow() + 1 + this.getTotalChildCount() - (this.childCount - var1);
      }

      public int getTotalChildCount() {
         if (!this.isExpanded()) {
            return 0;
         } else {
            FixedHeightLayoutCache.FHTreeStateNode var1 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
            int var2;
            if (var1 != null && (var2 = var1.getIndex(this)) + 1 < var1.getChildCount()) {
               FixedHeightLayoutCache.FHTreeStateNode var5 = (FixedHeightLayoutCache.FHTreeStateNode)var1.getChildAt(var2 + 1);
               return var5.row - this.row - (var5.childIndex - this.childIndex);
            } else {
               int var3 = this.childCount;

               for(int var4 = this.getChildCount() - 1; var4 >= 0; --var4) {
                  var3 += ((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var4)).getTotalChildCount();
               }

               return var3;
            }
         }
      }

      public boolean isExpanded() {
         return this.isExpanded;
      }

      public int getVisibleLevel() {
         return FixedHeightLayoutCache.this.isRootVisible() ? this.getLevel() : this.getLevel() - 1;
      }

      protected void resetChildrenPaths(TreePath var1) {
         FixedHeightLayoutCache.this.removeMapping(this);
         if (var1 == null) {
            this.path = new TreePath(this.getUserObject());
         } else {
            this.path = var1.pathByAddingChild(this.getUserObject());
         }

         FixedHeightLayoutCache.this.addMapping(this);

         for(int var2 = this.getChildCount() - 1; var2 >= 0; --var2) {
            ((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var2)).resetChildrenPaths(this.path);
         }

      }

      protected void removeFromMapping() {
         if (this.path != null) {
            FixedHeightLayoutCache.this.removeMapping(this);

            for(int var1 = this.getChildCount() - 1; var1 >= 0; --var1) {
               ((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var1)).removeFromMapping();
            }
         }

      }

      protected FixedHeightLayoutCache.FHTreeStateNode createChildFor(Object var1) {
         int var2 = FixedHeightLayoutCache.this.treeModel.getIndexOfChild(this.getUserObject(), var1);
         if (var2 < 0) {
            return null;
         } else {
            FixedHeightLayoutCache.FHTreeStateNode var4 = FixedHeightLayoutCache.this.createNodeForValue(var1, var2);
            int var5;
            if (this.isVisible()) {
               var5 = this.getRowToModelIndex(var2);
            } else {
               var5 = -1;
            }

            var4.row = var5;
            int var6 = 0;

            for(int var7 = this.getChildCount(); var6 < var7; ++var6) {
               FixedHeightLayoutCache.FHTreeStateNode var3 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var6);
               if (var3.childIndex > var2) {
                  this.insert(var4, var6);
                  return var4;
               }
            }

            this.add(var4);
            return var4;
         }
      }

      protected void adjustRowBy(int var1) {
         this.row += var1;
         if (this.isExpanded) {
            for(int var2 = this.getChildCount() - 1; var2 >= 0; --var2) {
               ((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var2)).adjustRowBy(var1);
            }
         }

      }

      protected void adjustRowBy(int var1, int var2) {
         if (this.isExpanded) {
            for(int var3 = this.getChildCount() - 1; var3 >= var2; --var3) {
               ((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var3)).adjustRowBy(var1);
            }
         }

         FixedHeightLayoutCache.FHTreeStateNode var4 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         if (var4 != null) {
            var4.adjustRowBy(var1, var4.getIndex(this) + 1);
         }

      }

      protected void didExpand() {
         int var1 = this.setRowAndChildren(this.row);
         FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         int var3 = var1 - this.row - 1;
         if (var2 != null) {
            var2.adjustRowBy(var3, var2.getIndex(this) + 1);
         }

         FixedHeightLayoutCache.this.adjustRowCountBy(var3);
      }

      protected int setRowAndChildren(int var1) {
         this.row = var1;
         if (!this.isExpanded()) {
            return this.row + 1;
         } else {
            int var2 = this.row + 1;
            int var3 = 0;
            int var5 = this.getChildCount();

            for(int var6 = 0; var6 < var5; ++var6) {
               FixedHeightLayoutCache.FHTreeStateNode var4 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var6);
               var2 += var4.childIndex - var3;
               var3 = var4.childIndex + 1;
               if (var4.isExpanded) {
                  var2 = var4.setRowAndChildren(var2);
               } else {
                  var4.row = var2++;
               }
            }

            return var2 + this.childCount - var3;
         }
      }

      protected void resetChildrenRowsFrom(int var1, int var2, int var3) {
         int var4 = var1;
         int var5 = var3;
         int var7 = this.getChildCount();

         FixedHeightLayoutCache.FHTreeStateNode var6;
         for(int var8 = var2; var8 < var7; ++var8) {
            var6 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var8);
            var4 += var6.childIndex - var5;
            var5 = var6.childIndex + 1;
            if (var6.isExpanded) {
               var4 = var6.setRowAndChildren(var4);
            } else {
               var6.row = var4++;
            }
         }

         var4 += this.childCount - var5;
         var6 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         if (var6 != null) {
            var6.resetChildrenRowsFrom(var4, var6.getIndex(this) + 1, this.childIndex + 1);
         } else {
            FixedHeightLayoutCache.this.rowCount = var4;
         }

      }

      protected void makeVisible() {
         FixedHeightLayoutCache.FHTreeStateNode var1 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         if (var1 != null) {
            var1.expandParentAndReceiver();
         }

      }

      protected void expandParentAndReceiver() {
         FixedHeightLayoutCache.FHTreeStateNode var1 = (FixedHeightLayoutCache.FHTreeStateNode)this.getParent();
         if (var1 != null) {
            var1.expandParentAndReceiver();
         }

         this.expand();
      }

      protected void expand() {
         if (!this.isExpanded && !this.isLeaf()) {
            boolean var1 = this.isVisible();
            this.isExpanded = true;
            this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.getUserObject());
            if (var1) {
               this.didExpand();
            }

            if (var1 && FixedHeightLayoutCache.this.treeSelectionModel != null) {
               FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
            }
         }

      }

      protected void collapse(boolean var1) {
         if (this.isExpanded) {
            if (this.isVisible() && var1) {
               int var2 = this.getTotalChildCount();
               this.isExpanded = false;
               FixedHeightLayoutCache.this.adjustRowCountBy(-var2);
               this.adjustRowBy(-var2, 0);
            } else {
               this.isExpanded = false;
            }

            if (var1 && this.isVisible() && FixedHeightLayoutCache.this.treeSelectionModel != null) {
               FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
            }
         }

      }

      public boolean isLeaf() {
         TreeModel var1 = FixedHeightLayoutCache.this.getModel();
         return var1 != null ? var1.isLeaf(this.getUserObject()) : true;
      }

      protected void addNode(FixedHeightLayoutCache.FHTreeStateNode var1) {
         boolean var2 = false;
         int var3 = var1.getChildIndex();
         int var4 = 0;

         for(int var5 = this.getChildCount(); var4 < var5; ++var4) {
            if (((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var4)).getChildIndex() > var3) {
               var2 = true;
               this.insert(var1, var4);
               var4 = var5;
            }
         }

         if (!var2) {
            this.add(var1);
         }

      }

      protected void removeChildAtModelIndex(int var1, boolean var2) {
         FixedHeightLayoutCache.FHTreeStateNode var3 = this.getChildAtModelIndex(var1);
         int var4;
         if (var3 != null) {
            var4 = var3.getRow();
            int var5 = this.getIndex(var3);
            var3.collapse(false);
            this.remove(var5);
            this.adjustChildIndexs(var5, -1);
            --this.childCount;
            if (var2) {
               this.resetChildrenRowsFrom(var4, var5, var1);
            }
         } else {
            var4 = this.getChildCount();

            for(int var6 = 0; var6 < var4; ++var6) {
               FixedHeightLayoutCache.FHTreeStateNode var7 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var6);
               if (var7.childIndex >= var1) {
                  if (var2) {
                     this.adjustRowBy(-1, var6);
                     FixedHeightLayoutCache.this.adjustRowCountBy(-1);
                  }

                  while(var6 < var4) {
                     --((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var6)).childIndex;
                     ++var6;
                  }

                  --this.childCount;
                  return;
               }
            }

            if (var2) {
               this.adjustRowBy(-1, var4);
               FixedHeightLayoutCache.this.adjustRowCountBy(-1);
            }

            --this.childCount;
         }

      }

      protected void adjustChildIndexs(int var1, int var2) {
         int var3 = var1;

         for(int var4 = this.getChildCount(); var3 < var4; ++var3) {
            FixedHeightLayoutCache.FHTreeStateNode var10000 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var3);
            var10000.childIndex += var2;
         }

      }

      protected void childInsertedAtModelIndex(int var1, boolean var2) {
         int var4 = this.getChildCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            FixedHeightLayoutCache.FHTreeStateNode var3 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var5);
            if (var3.childIndex >= var1) {
               if (var2) {
                  this.adjustRowBy(1, var5);
                  FixedHeightLayoutCache.this.adjustRowCountBy(1);
               }

               while(var5 < var4) {
                  ++((FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var5)).childIndex;
                  ++var5;
               }

               ++this.childCount;
               return;
            }
         }

         if (var2) {
            this.adjustRowBy(1, var4);
            FixedHeightLayoutCache.this.adjustRowCountBy(1);
         }

         ++this.childCount;
      }

      protected boolean getPathForRow(int var1, int var2, FixedHeightLayoutCache.SearchInfo var3) {
         if (this.row == var1) {
            var3.node = this;
            var3.isNodeParentNode = false;
            var3.childIndex = this.childIndex;
            return true;
         } else {
            FixedHeightLayoutCache.FHTreeStateNode var5 = null;
            int var6 = 0;

            for(int var7 = this.getChildCount(); var6 < var7; ++var6) {
               FixedHeightLayoutCache.FHTreeStateNode var4 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var6);
               if (var4.row > var1) {
                  if (var6 == 0) {
                     var3.node = this;
                     var3.isNodeParentNode = true;
                     var3.childIndex = var1 - this.row - 1;
                     return true;
                  }

                  int var8 = 1 + var4.row - (var4.childIndex - var5.childIndex);
                  if (var1 < var8) {
                     return var5.getPathForRow(var1, var8, var3);
                  }

                  var3.node = this;
                  var3.isNodeParentNode = true;
                  var3.childIndex = var1 - var8 + var5.childIndex + 1;
                  return true;
               }

               var5 = var4;
            }

            if (var5 != null) {
               var6 = var2 - (this.childCount - var5.childIndex) + 1;
               if (var1 < var6) {
                  return var5.getPathForRow(var1, var6, var3);
               } else {
                  var3.node = this;
                  var3.isNodeParentNode = true;
                  var3.childIndex = var1 - var6 + var5.childIndex + 1;
                  return true;
               }
            } else {
               var6 = var1 - this.row - 1;
               if (var6 >= this.childCount) {
                  return false;
               } else {
                  var3.node = this;
                  var3.isNodeParentNode = true;
                  var3.childIndex = var6;
                  return true;
               }
            }
         }
      }

      protected int getCountTo(int var1) {
         int var3 = var1 + 1;
         int var4 = 0;

         for(int var5 = this.getChildCount(); var4 < var5; ++var4) {
            FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var4);
            if (var2.childIndex >= var1) {
               var4 = var5;
            } else {
               var3 += var2.getTotalChildCount();
            }
         }

         if (this.parent != null) {
            return var3 + ((FixedHeightLayoutCache.FHTreeStateNode)this.getParent()).getCountTo(this.childIndex);
         } else if (!FixedHeightLayoutCache.this.isRootVisible()) {
            return var3 - 1;
         } else {
            return var3;
         }
      }

      protected int getNumExpandedChildrenTo(int var1) {
         int var3 = var1;
         int var4 = 0;

         for(int var5 = this.getChildCount(); var4 < var5; ++var4) {
            FixedHeightLayoutCache.FHTreeStateNode var2 = (FixedHeightLayoutCache.FHTreeStateNode)this.getChildAt(var4);
            if (var2.childIndex >= var1) {
               return var3;
            }

            var3 += var2.getTotalChildCount();
         }

         return var3;
      }

      protected void didAdjustTree() {
      }
   }
}
