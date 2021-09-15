package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class VariableHeightLayoutCache extends AbstractLayoutCache {
   private Vector<Object> visibleNodes = new Vector();
   private boolean updateNodeSizes;
   private VariableHeightLayoutCache.TreeStateNode root;
   private Rectangle boundsBuffer = new Rectangle();
   private Hashtable<TreePath, VariableHeightLayoutCache.TreeStateNode> treePathMapping = new Hashtable();
   private Stack<Stack<TreePath>> tempStacks = new Stack();

   public void setModel(TreeModel var1) {
      super.setModel(var1);
      this.rebuild(false);
   }

   public void setRootVisible(boolean var1) {
      if (this.isRootVisible() != var1 && this.root != null) {
         if (var1) {
            this.root.updatePreferredSize(0);
            this.visibleNodes.insertElementAt(this.root, 0);
         } else if (this.visibleNodes.size() > 0) {
            this.visibleNodes.removeElementAt(0);
            if (this.treeSelectionModel != null) {
               this.treeSelectionModel.removeSelectionPath(this.root.getTreePath());
            }
         }

         if (this.treeSelectionModel != null) {
            this.treeSelectionModel.resetRowSelection();
         }

         if (this.getRowCount() > 0) {
            this.getNode(0).setYOrigin(0);
         }

         this.updateYLocationsFrom(0);
         this.visibleNodesChanged();
      }

      super.setRootVisible(var1);
   }

   public void setRowHeight(int var1) {
      if (var1 != this.getRowHeight()) {
         super.setRowHeight(var1);
         this.invalidateSizes();
         this.visibleNodesChanged();
      }

   }

   public void setNodeDimensions(AbstractLayoutCache.NodeDimensions var1) {
      super.setNodeDimensions(var1);
      this.invalidateSizes();
      this.visibleNodesChanged();
   }

   public void setExpandedState(TreePath var1, boolean var2) {
      if (var1 != null) {
         if (var2) {
            this.ensurePathIsExpanded(var1, true);
         } else {
            VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(var1, false, true);
            if (var3 != null) {
               var3.makeVisible();
               var3.collapse();
            }
         }
      }

   }

   public boolean getExpandedState(TreePath var1) {
      VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
      return var2 != null ? var2.isVisible() && var2.isExpanded() : false;
   }

   public Rectangle getBounds(TreePath var1, Rectangle var2) {
      VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(var1, true, false);
      if (var3 != null) {
         if (this.updateNodeSizes) {
            this.updateNodeSizes(false);
         }

         return var3.getNodeBounds(var2);
      } else {
         return null;
      }
   }

   public TreePath getPathForRow(int var1) {
      return var1 >= 0 && var1 < this.getRowCount() ? this.getNode(var1).getTreePath() : null;
   }

   public int getRowForPath(TreePath var1) {
      if (var1 == null) {
         return -1;
      } else {
         VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
         return var2 != null ? var2.getRow() : -1;
      }
   }

   public int getRowCount() {
      return this.visibleNodes.size();
   }

   public void invalidatePathBounds(TreePath var1) {
      VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
      if (var2 != null) {
         var2.markSizeInvalid();
         if (var2.isVisible()) {
            this.updateYLocationsFrom(var2.getRow());
         }
      }

   }

   public int getPreferredHeight() {
      int var1 = this.getRowCount();
      if (var1 > 0) {
         VariableHeightLayoutCache.TreeStateNode var2 = this.getNode(var1 - 1);
         return var2.getYOrigin() + var2.getPreferredHeight();
      } else {
         return 0;
      }
   }

   public int getPreferredWidth(Rectangle var1) {
      if (this.updateNodeSizes) {
         this.updateNodeSizes(false);
      }

      return this.getMaxNodeWidth();
   }

   public TreePath getPathClosestTo(int var1, int var2) {
      if (this.getRowCount() == 0) {
         return null;
      } else {
         if (this.updateNodeSizes) {
            this.updateNodeSizes(false);
         }

         int var3 = this.getRowContainingYLocation(var2);
         return this.getNode(var3).getTreePath();
      }
   }

   public Enumeration<TreePath> getVisiblePathsFrom(TreePath var1) {
      VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
      return var2 != null ? new VariableHeightLayoutCache.VisibleTreeStateNodeEnumeration(var2) : null;
   }

   public int getVisibleChildCount(TreePath var1) {
      VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
      return var2 != null ? var2.getVisibleChildCount() : 0;
   }

   public void invalidateSizes() {
      if (this.root != null) {
         this.root.deepMarkSizeInvalid();
      }

      if (!this.isFixedRowHeight() && this.visibleNodes.size() > 0) {
         this.updateNodeSizes(true);
      }

   }

   public boolean isExpanded(TreePath var1) {
      if (var1 == null) {
         return false;
      } else {
         VariableHeightLayoutCache.TreeStateNode var2 = this.getNodeForPath(var1, true, false);
         return var2 != null && var2.isExpanded();
      }
   }

   public void treeNodesChanged(TreeModelEvent var1) {
      if (var1 != null) {
         int[] var2 = var1.getChildIndices();
         VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(SwingUtilities2.getTreePath(var1, this.getModel()), false, false);
         if (var3 != null) {
            Object var4 = var3.getValue();
            var3.updatePreferredSize();
            int var5;
            if (var3.hasBeenExpanded() && var2 != null) {
               for(var5 = 0; var5 < var2.length; ++var5) {
                  VariableHeightLayoutCache.TreeStateNode var6 = (VariableHeightLayoutCache.TreeStateNode)var3.getChildAt(var2[var5]);
                  var6.setUserObject(this.treeModel.getChild(var4, var2[var5]));
                  var6.updatePreferredSize();
               }
            } else if (var3 == this.root) {
               var3.updatePreferredSize();
            }

            if (!this.isFixedRowHeight()) {
               var5 = var3.getRow();
               if (var5 != -1) {
                  this.updateYLocationsFrom(var5);
               }
            }

            this.visibleNodesChanged();
         }
      }

   }

   public void treeNodesInserted(TreeModelEvent var1) {
      if (var1 != null) {
         int[] var2 = var1.getChildIndices();
         VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(SwingUtilities2.getTreePath(var1, this.getModel()), false, false);
         if (var3 != null && var2 != null && var2.length > 0) {
            if (var3.hasBeenExpanded()) {
               int var8 = var3.getChildCount();
               Object var6 = var3.getValue();
               boolean var4 = var3 == this.root && !this.rootVisible || var3.getRow() != -1 && var3.isExpanded();

               for(int var5 = 0; var5 < var2.length; ++var5) {
                  this.createNodeAt(var3, var2[var5]);
               }

               if (var8 == 0) {
                  var3.updatePreferredSize();
               }

               if (this.treeSelectionModel != null) {
                  this.treeSelectionModel.resetRowSelection();
               }

               if (!this.isFixedRowHeight() && (var4 || var8 == 0 && var3.isVisible())) {
                  if (var3 == this.root) {
                     this.updateYLocationsFrom(0);
                  } else {
                     this.updateYLocationsFrom(var3.getRow());
                  }

                  this.visibleNodesChanged();
               } else if (var4) {
                  this.visibleNodesChanged();
               }
            } else if (this.treeModel.getChildCount(var3.getValue()) - var2.length == 0) {
               var3.updatePreferredSize();
               if (!this.isFixedRowHeight() && var3.isVisible()) {
                  this.updateYLocationsFrom(var3.getRow());
               }
            }
         }
      }

   }

   public void treeNodesRemoved(TreeModelEvent var1) {
      if (var1 != null) {
         int[] var2 = var1.getChildIndices();
         VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(SwingUtilities2.getTreePath(var1, this.getModel()), false, false);
         if (var3 != null && var2 != null && var2.length > 0) {
            if (var3.hasBeenExpanded()) {
               boolean var4 = var3 == this.root && !this.rootVisible || var3.getRow() != -1 && var3.isExpanded();

               for(int var5 = var2.length - 1; var5 >= 0; --var5) {
                  VariableHeightLayoutCache.TreeStateNode var7 = (VariableHeightLayoutCache.TreeStateNode)var3.getChildAt(var2[var5]);
                  if (var7.isExpanded()) {
                     var7.collapse(false);
                  }

                  if (var4) {
                     int var6 = var7.getRow();
                     if (var6 != -1) {
                        this.visibleNodes.removeElementAt(var6);
                     }
                  }

                  var3.remove(var2[var5]);
               }

               if (var3.getChildCount() == 0) {
                  var3.updatePreferredSize();
                  if (var3.isExpanded() && var3.isLeaf()) {
                     var3.collapse(false);
                  }
               }

               if (this.treeSelectionModel != null) {
                  this.treeSelectionModel.resetRowSelection();
               }

               if (!this.isFixedRowHeight() && (var4 || var3.getChildCount() == 0 && var3.isVisible())) {
                  if (var3 == this.root) {
                     if (this.getRowCount() > 0) {
                        this.getNode(0).setYOrigin(0);
                     }

                     this.updateYLocationsFrom(0);
                  } else {
                     this.updateYLocationsFrom(var3.getRow());
                  }

                  this.visibleNodesChanged();
               } else if (var4) {
                  this.visibleNodesChanged();
               }
            } else if (this.treeModel.getChildCount(var3.getValue()) == 0) {
               var3.updatePreferredSize();
               if (!this.isFixedRowHeight() && var3.isVisible()) {
                  this.updateYLocationsFrom(var3.getRow());
               }
            }
         }
      }

   }

   public void treeStructureChanged(TreeModelEvent var1) {
      if (var1 != null) {
         TreePath var2 = SwingUtilities2.getTreePath(var1, this.getModel());
         VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(var2, false, false);
         if (var3 == this.root || var3 == null && (var2 == null && this.treeModel != null && this.treeModel.getRoot() == null || var2 != null && var2.getPathCount() == 1)) {
            this.rebuild(true);
         } else if (var3 != null) {
            boolean var8 = var3.isExpanded();
            boolean var9 = var3.getRow() != -1;
            VariableHeightLayoutCache.TreeStateNode var7 = (VariableHeightLayoutCache.TreeStateNode)var3.getParent();
            int var4 = var7.getIndex(var3);
            if (var9 && var8) {
               var3.collapse(false);
            }

            if (var9) {
               this.visibleNodes.removeElement(var3);
            }

            var3.removeFromParent();
            this.createNodeAt(var7, var4);
            VariableHeightLayoutCache.TreeStateNode var6 = (VariableHeightLayoutCache.TreeStateNode)var7.getChildAt(var4);
            if (var9 && var8) {
               var6.expand(false);
            }

            int var10 = var6.getRow();
            if (!this.isFixedRowHeight() && var9) {
               if (var10 == 0) {
                  this.updateYLocationsFrom(var10);
               } else {
                  this.updateYLocationsFrom(var10 - 1);
               }

               this.visibleNodesChanged();
            } else if (var9) {
               this.visibleNodesChanged();
            }
         }
      }

   }

   private void visibleNodesChanged() {
   }

   private void addMapping(VariableHeightLayoutCache.TreeStateNode var1) {
      this.treePathMapping.put(var1.getTreePath(), var1);
   }

   private void removeMapping(VariableHeightLayoutCache.TreeStateNode var1) {
      this.treePathMapping.remove(var1.getTreePath());
   }

   private VariableHeightLayoutCache.TreeStateNode getMapping(TreePath var1) {
      return (VariableHeightLayoutCache.TreeStateNode)this.treePathMapping.get(var1);
   }

   private Rectangle getBounds(int var1, Rectangle var2) {
      if (this.updateNodeSizes) {
         this.updateNodeSizes(false);
      }

      return var1 >= 0 && var1 < this.getRowCount() ? this.getNode(var1).getNodeBounds(var2) : null;
   }

   private void rebuild(boolean var1) {
      this.treePathMapping.clear();
      Object var2;
      if (this.treeModel != null && (var2 = this.treeModel.getRoot()) != null) {
         this.root = this.createNodeForValue(var2);
         this.root.path = new TreePath(var2);
         this.addMapping(this.root);
         this.root.updatePreferredSize(0);
         this.visibleNodes.removeAllElements();
         if (this.isRootVisible()) {
            this.visibleNodes.addElement(this.root);
         }

         if (!this.root.isExpanded()) {
            this.root.expand();
         } else {
            Enumeration var3 = this.root.children();

            while(var3.hasMoreElements()) {
               this.visibleNodes.addElement(var3.nextElement());
            }

            if (!this.isFixedRowHeight()) {
               this.updateYLocationsFrom(0);
            }
         }
      } else {
         this.visibleNodes.removeAllElements();
         this.root = null;
      }

      if (var1 && this.treeSelectionModel != null) {
         this.treeSelectionModel.clearSelection();
      }

      this.visibleNodesChanged();
   }

   private VariableHeightLayoutCache.TreeStateNode createNodeAt(VariableHeightLayoutCache.TreeStateNode var1, int var2) {
      Object var4 = this.treeModel.getChild(var1.getValue(), var2);
      VariableHeightLayoutCache.TreeStateNode var5 = this.createNodeForValue(var4);
      var1.insert(var5, var2);
      var5.updatePreferredSize(-1);
      boolean var3 = var1 == this.root;
      if (var5 != null && var1.isExpanded() && (var1.getRow() != -1 || var3)) {
         int var6;
         if (var2 == 0) {
            if (var3 && !this.isRootVisible()) {
               var6 = 0;
            } else {
               var6 = var1.getRow() + 1;
            }
         } else if (var2 == var1.getChildCount()) {
            var6 = var1.getLastVisibleNode().getRow() + 1;
         } else {
            VariableHeightLayoutCache.TreeStateNode var7 = (VariableHeightLayoutCache.TreeStateNode)var1.getChildAt(var2 - 1);
            var6 = var7.getLastVisibleNode().getRow() + 1;
         }

         this.visibleNodes.insertElementAt(var5, var6);
      }

      return var5;
   }

   private VariableHeightLayoutCache.TreeStateNode getNodeForPath(TreePath var1, boolean var2, boolean var3) {
      if (var1 != null) {
         VariableHeightLayoutCache.TreeStateNode var4 = this.getMapping(var1);
         if (var4 != null) {
            if (var2 && !var4.isVisible()) {
               return null;
            }

            return var4;
         }

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
                     var4.getLoadedChildren(var3);
                     int var6 = this.treeModel.getIndexOfChild(var4.getUserObject(), var1.getLastPathComponent());
                     if (var6 != -1 && var6 < var4.getChildCount() && (!var2 || var4.isVisible())) {
                        var4 = (VariableHeightLayoutCache.TreeStateNode)var4.getChildAt(var6);
                     } else {
                        var4 = null;
                     }
                  }

                  VariableHeightLayoutCache.TreeStateNode var10 = var4;
                  return var10;
               }

               var5.push(var1);
            }
         } finally {
            var5.removeAllElements();
            this.tempStacks.push(var5);
         }
      }

      return null;
   }

   private void updateYLocationsFrom(int var1) {
      if (var1 >= 0 && var1 < this.getRowCount()) {
         VariableHeightLayoutCache.TreeStateNode var5 = this.getNode(var1);
         int var4 = var5.getYOrigin() + var5.getPreferredHeight();
         int var2 = var1 + 1;

         for(int var3 = this.visibleNodes.size(); var2 < var3; ++var2) {
            var5 = (VariableHeightLayoutCache.TreeStateNode)this.visibleNodes.elementAt(var2);
            var5.setYOrigin(var4);
            var4 += var5.getPreferredHeight();
         }
      }

   }

   private void updateNodeSizes(boolean var1) {
      this.updateNodeSizes = false;
      int var3 = 0;
      int var2 = 0;

      for(int var4 = this.visibleNodes.size(); var3 < var4; ++var3) {
         VariableHeightLayoutCache.TreeStateNode var5 = (VariableHeightLayoutCache.TreeStateNode)this.visibleNodes.elementAt(var3);
         var5.setYOrigin(var2);
         if (var1 || !var5.hasValidSize()) {
            var5.updatePreferredSize(var3);
         }

         var2 += var5.getPreferredHeight();
      }

   }

   private int getRowContainingYLocation(int var1) {
      if (this.isFixedRowHeight()) {
         return this.getRowCount() == 0 ? -1 : Math.max(0, Math.min(this.getRowCount() - 1, var1 / this.getRowHeight()));
      } else {
         int var2;
         if ((var2 = this.getRowCount()) <= 0) {
            return -1;
         } else {
            int var5 = 0;
            int var4 = 0;

            while(var5 < var2) {
               var4 = (var2 - var5) / 2 + var5;
               VariableHeightLayoutCache.TreeStateNode var7 = (VariableHeightLayoutCache.TreeStateNode)this.visibleNodes.elementAt(var4);
               int var6 = var7.getYOrigin();
               int var3 = var6 + var7.getPreferredHeight();
               if (var1 < var6) {
                  var2 = var4 - 1;
               } else {
                  if (var1 < var3) {
                     break;
                  }

                  var5 = var4 + 1;
               }
            }

            if (var5 == var2) {
               var4 = var5;
               if (var5 >= this.getRowCount()) {
                  var4 = this.getRowCount() - 1;
               }
            }

            return var4;
         }
      }
   }

   private void ensurePathIsExpanded(TreePath var1, boolean var2) {
      if (var1 != null) {
         if (this.treeModel.isLeaf(var1.getLastPathComponent())) {
            var1 = var1.getParentPath();
            var2 = true;
         }

         if (var1 != null) {
            VariableHeightLayoutCache.TreeStateNode var3 = this.getNodeForPath(var1, false, true);
            if (var3 != null) {
               var3.makeVisible();
               if (var2) {
                  var3.expand();
               }
            }
         }
      }

   }

   private VariableHeightLayoutCache.TreeStateNode getNode(int var1) {
      return (VariableHeightLayoutCache.TreeStateNode)this.visibleNodes.elementAt(var1);
   }

   private int getMaxNodeWidth() {
      int var1 = 0;

      for(int var3 = this.getRowCount() - 1; var3 >= 0; --var3) {
         VariableHeightLayoutCache.TreeStateNode var4 = this.getNode(var3);
         int var2 = var4.getPreferredWidth() + var4.getXOrigin();
         if (var2 > var1) {
            var1 = var2;
         }
      }

      return var1;
   }

   private VariableHeightLayoutCache.TreeStateNode createNodeForValue(Object var1) {
      return new VariableHeightLayoutCache.TreeStateNode(var1);
   }

   private class VisibleTreeStateNodeEnumeration implements Enumeration<TreePath> {
      protected VariableHeightLayoutCache.TreeStateNode parent;
      protected int nextIndex;
      protected int childCount;

      protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache.TreeStateNode var2) {
         this(var2, -1);
      }

      protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache.TreeStateNode var2, int var3) {
         this.parent = var2;
         this.nextIndex = var3;
         this.childCount = this.parent.getChildCount();
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
               VariableHeightLayoutCache.TreeStateNode var2 = (VariableHeightLayoutCache.TreeStateNode)this.parent.getChildAt(this.nextIndex);
               var1 = var2.getTreePath();
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
         if (this.parent == VariableHeightLayoutCache.this.root) {
            this.parent = null;
            return false;
         } else {
            while(this.parent != null) {
               VariableHeightLayoutCache.TreeStateNode var1 = (VariableHeightLayoutCache.TreeStateNode)this.parent.getParent();
               if (var1 != null) {
                  this.nextIndex = var1.getIndex(this.parent);
                  this.parent = var1;
                  this.childCount = this.parent.getChildCount();
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
            VariableHeightLayoutCache.TreeStateNode var1 = (VariableHeightLayoutCache.TreeStateNode)this.parent.getChildAt(this.nextIndex);
            if (var1 != null && var1.isExpanded()) {
               this.parent = var1;
               this.nextIndex = -1;
               this.childCount = var1.getChildCount();
            }

            return true;
         }
      }
   }

   private class TreeStateNode extends DefaultMutableTreeNode {
      protected int preferredWidth;
      protected int preferredHeight;
      protected int xOrigin;
      protected int yOrigin;
      protected boolean expanded;
      protected boolean hasBeenExpanded;
      protected TreePath path;

      public TreeStateNode(Object var2) {
         super(var2);
      }

      public void setParent(MutableTreeNode var1) {
         super.setParent(var1);
         if (var1 != null) {
            this.path = ((VariableHeightLayoutCache.TreeStateNode)var1).getTreePath().pathByAddingChild(this.getUserObject());
            VariableHeightLayoutCache.this.addMapping(this);
         }

      }

      public void remove(int var1) {
         VariableHeightLayoutCache.TreeStateNode var2 = (VariableHeightLayoutCache.TreeStateNode)this.getChildAt(var1);
         var2.removeFromMapping();
         super.remove(var1);
      }

      public void setUserObject(Object var1) {
         super.setUserObject(var1);
         if (this.path != null) {
            VariableHeightLayoutCache.TreeStateNode var2 = (VariableHeightLayoutCache.TreeStateNode)this.getParent();
            if (var2 != null) {
               this.resetChildrenPaths(var2.getTreePath());
            } else {
               this.resetChildrenPaths((TreePath)null);
            }
         }

      }

      public Enumeration children() {
         return !this.isExpanded() ? DefaultMutableTreeNode.EMPTY_ENUMERATION : super.children();
      }

      public boolean isLeaf() {
         return VariableHeightLayoutCache.this.getModel().isLeaf(this.getValue());
      }

      public Rectangle getNodeBounds(Rectangle var1) {
         if (var1 == null) {
            var1 = new Rectangle(this.getXOrigin(), this.getYOrigin(), this.getPreferredWidth(), this.getPreferredHeight());
         } else {
            var1.x = this.getXOrigin();
            var1.y = this.getYOrigin();
            var1.width = this.getPreferredWidth();
            var1.height = this.getPreferredHeight();
         }

         return var1;
      }

      public int getXOrigin() {
         if (!this.hasValidSize()) {
            this.updatePreferredSize(this.getRow());
         }

         return this.xOrigin;
      }

      public int getYOrigin() {
         if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
            int var1 = this.getRow();
            return var1 == -1 ? -1 : VariableHeightLayoutCache.this.getRowHeight() * var1;
         } else {
            return this.yOrigin;
         }
      }

      public int getPreferredHeight() {
         if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
            return VariableHeightLayoutCache.this.getRowHeight();
         } else {
            if (!this.hasValidSize()) {
               this.updatePreferredSize(this.getRow());
            }

            return this.preferredHeight;
         }
      }

      public int getPreferredWidth() {
         if (!this.hasValidSize()) {
            this.updatePreferredSize(this.getRow());
         }

         return this.preferredWidth;
      }

      public boolean hasValidSize() {
         return this.preferredHeight != 0;
      }

      public int getRow() {
         return VariableHeightLayoutCache.this.visibleNodes.indexOf(this);
      }

      public boolean hasBeenExpanded() {
         return this.hasBeenExpanded;
      }

      public boolean isExpanded() {
         return this.expanded;
      }

      public VariableHeightLayoutCache.TreeStateNode getLastVisibleNode() {
         VariableHeightLayoutCache.TreeStateNode var1;
         for(var1 = this; var1.isExpanded() && var1.getChildCount() > 0; var1 = (VariableHeightLayoutCache.TreeStateNode)var1.getLastChild()) {
         }

         return var1;
      }

      public boolean isVisible() {
         if (this == VariableHeightLayoutCache.this.root) {
            return true;
         } else {
            VariableHeightLayoutCache.TreeStateNode var1 = (VariableHeightLayoutCache.TreeStateNode)this.getParent();
            return var1 != null && var1.isExpanded() && var1.isVisible();
         }
      }

      public int getModelChildCount() {
         return this.hasBeenExpanded ? super.getChildCount() : VariableHeightLayoutCache.this.getModel().getChildCount(this.getValue());
      }

      public int getVisibleChildCount() {
         int var1 = 0;
         if (this.isExpanded()) {
            int var2 = this.getChildCount();
            var1 += var2;

            for(int var3 = 0; var3 < var2; ++var3) {
               var1 += ((VariableHeightLayoutCache.TreeStateNode)this.getChildAt(var3)).getVisibleChildCount();
            }
         }

         return var1;
      }

      public void toggleExpanded() {
         if (this.isExpanded()) {
            this.collapse();
         } else {
            this.expand();
         }

      }

      public void makeVisible() {
         VariableHeightLayoutCache.TreeStateNode var1 = (VariableHeightLayoutCache.TreeStateNode)this.getParent();
         if (var1 != null) {
            var1.expandParentAndReceiver();
         }

      }

      public void expand() {
         this.expand(true);
      }

      public void collapse() {
         this.collapse(true);
      }

      public Object getValue() {
         return this.getUserObject();
      }

      public TreePath getTreePath() {
         return this.path;
      }

      protected void resetChildrenPaths(TreePath var1) {
         VariableHeightLayoutCache.this.removeMapping(this);
         if (var1 == null) {
            this.path = new TreePath(this.getUserObject());
         } else {
            this.path = var1.pathByAddingChild(this.getUserObject());
         }

         VariableHeightLayoutCache.this.addMapping(this);

         for(int var2 = this.getChildCount() - 1; var2 >= 0; --var2) {
            ((VariableHeightLayoutCache.TreeStateNode)this.getChildAt(var2)).resetChildrenPaths(this.path);
         }

      }

      protected void setYOrigin(int var1) {
         this.yOrigin = var1;
      }

      protected void shiftYOriginBy(int var1) {
         this.yOrigin += var1;
      }

      protected void updatePreferredSize() {
         this.updatePreferredSize(this.getRow());
      }

      protected void updatePreferredSize(int var1) {
         Rectangle var2 = VariableHeightLayoutCache.this.getNodeDimensions(this.getUserObject(), var1, this.getLevel(), this.isExpanded(), VariableHeightLayoutCache.this.boundsBuffer);
         if (var2 == null) {
            this.xOrigin = 0;
            this.preferredWidth = this.preferredHeight = 0;
            VariableHeightLayoutCache.this.updateNodeSizes = true;
         } else if (var2.height == 0) {
            this.xOrigin = 0;
            this.preferredWidth = this.preferredHeight = 0;
            VariableHeightLayoutCache.this.updateNodeSizes = true;
         } else {
            this.xOrigin = var2.x;
            this.preferredWidth = var2.width;
            if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
               this.preferredHeight = VariableHeightLayoutCache.this.getRowHeight();
            } else {
               this.preferredHeight = var2.height;
            }
         }

      }

      protected void markSizeInvalid() {
         this.preferredHeight = 0;
      }

      protected void deepMarkSizeInvalid() {
         this.markSizeInvalid();

         for(int var1 = this.getChildCount() - 1; var1 >= 0; --var1) {
            ((VariableHeightLayoutCache.TreeStateNode)this.getChildAt(var1)).deepMarkSizeInvalid();
         }

      }

      protected Enumeration getLoadedChildren(boolean var1) {
         if (var1 && !this.hasBeenExpanded) {
            Object var3 = this.getValue();
            TreeModel var4 = VariableHeightLayoutCache.this.getModel();
            int var5 = var4.getChildCount(var3);
            this.hasBeenExpanded = true;
            int var6 = this.getRow();
            VariableHeightLayoutCache.TreeStateNode var2;
            int var7;
            if (var6 == -1) {
               for(var7 = 0; var7 < var5; ++var7) {
                  var2 = VariableHeightLayoutCache.this.createNodeForValue(var4.getChild(var3, var7));
                  this.add(var2);
                  var2.updatePreferredSize(-1);
               }
            } else {
               ++var6;

               for(var7 = 0; var7 < var5; ++var7) {
                  var2 = VariableHeightLayoutCache.this.createNodeForValue(var4.getChild(var3, var7));
                  this.add(var2);
                  var2.updatePreferredSize(var6++);
               }
            }

            return super.children();
         } else {
            return super.children();
         }
      }

      protected void didAdjustTree() {
      }

      protected void expandParentAndReceiver() {
         VariableHeightLayoutCache.TreeStateNode var1 = (VariableHeightLayoutCache.TreeStateNode)this.getParent();
         if (var1 != null) {
            var1.expandParentAndReceiver();
         }

         this.expand();
      }

      protected void expand(boolean var1) {
         if (!this.isExpanded() && !this.isLeaf()) {
            boolean var2 = VariableHeightLayoutCache.this.isFixedRowHeight();
            int var3 = this.getPreferredHeight();
            int var4 = this.getRow();
            this.expanded = true;
            this.updatePreferredSize(var4);
            int var9;
            int var10;
            if (!this.hasBeenExpanded) {
               Object var6 = this.getValue();
               TreeModel var7 = VariableHeightLayoutCache.this.getModel();
               int var8 = var7.getChildCount(var6);
               this.hasBeenExpanded = true;
               VariableHeightLayoutCache.TreeStateNode var5;
               if (var4 == -1) {
                  for(var9 = 0; var9 < var8; ++var9) {
                     var5 = VariableHeightLayoutCache.this.createNodeForValue(var7.getChild(var6, var9));
                     this.add(var5);
                     var5.updatePreferredSize(-1);
                  }
               } else {
                  var9 = var4 + 1;

                  for(var10 = 0; var10 < var8; ++var10) {
                     var5 = VariableHeightLayoutCache.this.createNodeForValue(var7.getChild(var6, var10));
                     this.add(var5);
                     var5.updatePreferredSize(var9);
                  }
               }
            }

            int var11 = var4;
            Enumeration var12 = this.preorderEnumeration();
            var12.nextElement();
            int var13;
            if (var2) {
               var13 = 0;
            } else if (this == VariableHeightLayoutCache.this.root && !VariableHeightLayoutCache.this.isRootVisible()) {
               var13 = 0;
            } else {
               var13 = this.getYOrigin() + this.getPreferredHeight();
            }

            VariableHeightLayoutCache.TreeStateNode var14;
            Vector var10000;
            if (!var2) {
               while(var12.hasMoreElements()) {
                  var14 = (VariableHeightLayoutCache.TreeStateNode)var12.nextElement();
                  if (!VariableHeightLayoutCache.this.updateNodeSizes && !var14.hasValidSize()) {
                     var14.updatePreferredSize(var11 + 1);
                  }

                  var14.setYOrigin(var13);
                  var13 += var14.getPreferredHeight();
                  var10000 = VariableHeightLayoutCache.this.visibleNodes;
                  ++var11;
                  var10000.insertElementAt(var14, var11);
               }
            } else {
               while(var12.hasMoreElements()) {
                  var14 = (VariableHeightLayoutCache.TreeStateNode)var12.nextElement();
                  var10000 = VariableHeightLayoutCache.this.visibleNodes;
                  ++var11;
                  var10000.insertElementAt(var14, var11);
               }
            }

            if (var1 && (var4 != var11 || this.getPreferredHeight() != var3)) {
               if (!var2) {
                  ++var11;
                  if (var11 < VariableHeightLayoutCache.this.getRowCount()) {
                     var10 = var13 - (this.getYOrigin() + this.getPreferredHeight()) + (this.getPreferredHeight() - var3);

                     for(var9 = VariableHeightLayoutCache.this.visibleNodes.size() - 1; var9 >= var11; --var9) {
                        ((VariableHeightLayoutCache.TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(var9)).shiftYOriginBy(var10);
                     }
                  }
               }

               this.didAdjustTree();
               VariableHeightLayoutCache.this.visibleNodesChanged();
            }

            if (VariableHeightLayoutCache.this.treeSelectionModel != null) {
               VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
            }
         }

      }

      protected void collapse(boolean var1) {
         if (this.isExpanded()) {
            Enumeration var2 = this.preorderEnumeration();
            var2.nextElement();
            int var3 = 0;
            boolean var4 = VariableHeightLayoutCache.this.isFixedRowHeight();
            int var5;
            if (var4) {
               var5 = 0;
            } else {
               var5 = this.getPreferredHeight() + this.getYOrigin();
            }

            int var6 = this.getPreferredHeight();
            int var8 = this.getRow();
            VariableHeightLayoutCache.TreeStateNode var9;
            if (!var4) {
               while(var2.hasMoreElements()) {
                  var9 = (VariableHeightLayoutCache.TreeStateNode)var2.nextElement();
                  if (var9.isVisible()) {
                     ++var3;
                     var5 = var9.getYOrigin() + var9.getPreferredHeight();
                  }
               }
            } else {
               while(var2.hasMoreElements()) {
                  var9 = (VariableHeightLayoutCache.TreeStateNode)var2.nextElement();
                  if (var9.isVisible()) {
                     ++var3;
                  }
               }
            }

            int var12;
            for(var12 = var3 + var8; var12 > var8; --var12) {
               VariableHeightLayoutCache.this.visibleNodes.removeElementAt(var12);
            }

            this.expanded = false;
            if (var8 == -1) {
               this.markSizeInvalid();
            } else if (var1) {
               this.updatePreferredSize(var8);
            }

            if (var8 != -1 && var1 && (var3 > 0 || var6 != this.getPreferredHeight())) {
               int var7 = var5 + (this.getPreferredHeight() - var6);
               if (!var4 && var8 + 1 < VariableHeightLayoutCache.this.getRowCount() && var7 != var5) {
                  int var11 = var7 - var5;
                  var12 = var8 + 1;

                  for(int var10 = VariableHeightLayoutCache.this.visibleNodes.size(); var12 < var10; ++var12) {
                     ((VariableHeightLayoutCache.TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(var12)).shiftYOriginBy(var11);
                  }
               }

               this.didAdjustTree();
               VariableHeightLayoutCache.this.visibleNodesChanged();
            }

            if (VariableHeightLayoutCache.this.treeSelectionModel != null && var3 > 0 && var8 != -1) {
               VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
            }
         }

      }

      protected void removeFromMapping() {
         if (this.path != null) {
            VariableHeightLayoutCache.this.removeMapping(this);

            for(int var1 = this.getChildCount() - 1; var1 >= 0; --var1) {
               ((VariableHeightLayoutCache.TreeStateNode)this.getChildAt(var1)).removeFromMapping();
            }
         }

      }
   }
}
