package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import javax.swing.event.TreeModelEvent;

public abstract class AbstractLayoutCache implements RowMapper {
   protected AbstractLayoutCache.NodeDimensions nodeDimensions;
   protected TreeModel treeModel;
   protected TreeSelectionModel treeSelectionModel;
   protected boolean rootVisible;
   protected int rowHeight;

   public void setNodeDimensions(AbstractLayoutCache.NodeDimensions var1) {
      this.nodeDimensions = var1;
   }

   public AbstractLayoutCache.NodeDimensions getNodeDimensions() {
      return this.nodeDimensions;
   }

   public void setModel(TreeModel var1) {
      this.treeModel = var1;
   }

   public TreeModel getModel() {
      return this.treeModel;
   }

   public void setRootVisible(boolean var1) {
      this.rootVisible = var1;
   }

   public boolean isRootVisible() {
      return this.rootVisible;
   }

   public void setRowHeight(int var1) {
      this.rowHeight = var1;
   }

   public int getRowHeight() {
      return this.rowHeight;
   }

   public void setSelectionModel(TreeSelectionModel var1) {
      if (this.treeSelectionModel != null) {
         this.treeSelectionModel.setRowMapper((RowMapper)null);
      }

      this.treeSelectionModel = var1;
      if (this.treeSelectionModel != null) {
         this.treeSelectionModel.setRowMapper(this);
      }

   }

   public TreeSelectionModel getSelectionModel() {
      return this.treeSelectionModel;
   }

   public int getPreferredHeight() {
      int var1 = this.getRowCount();
      if (var1 > 0) {
         Rectangle var2 = this.getBounds(this.getPathForRow(var1 - 1), (Rectangle)null);
         if (var2 != null) {
            return var2.y + var2.height;
         }
      }

      return 0;
   }

   public int getPreferredWidth(Rectangle var1) {
      int var2 = this.getRowCount();
      if (var2 > 0) {
         TreePath var3;
         int var4;
         if (var1 == null) {
            var3 = this.getPathForRow(0);
            var4 = Integer.MAX_VALUE;
         } else {
            var3 = this.getPathClosestTo(var1.x, var1.y);
            var4 = var1.height + var1.y;
         }

         Enumeration var5 = this.getVisiblePathsFrom(var3);
         if (var5 != null && var5.hasMoreElements()) {
            Rectangle var6 = this.getBounds((TreePath)var5.nextElement(), (Rectangle)null);
            int var7;
            if (var6 != null) {
               var7 = var6.x + var6.width;
               if (var6.y >= var4) {
                  return var7;
               }
            } else {
               var7 = 0;
            }

            while(var6 != null && var5.hasMoreElements()) {
               var6 = this.getBounds((TreePath)var5.nextElement(), var6);
               if (var6 != null && var6.y < var4) {
                  var7 = Math.max(var7, var6.x + var6.width);
               } else {
                  var6 = null;
               }
            }

            return var7;
         }
      }

      return 0;
   }

   public abstract boolean isExpanded(TreePath var1);

   public abstract Rectangle getBounds(TreePath var1, Rectangle var2);

   public abstract TreePath getPathForRow(int var1);

   public abstract int getRowForPath(TreePath var1);

   public abstract TreePath getPathClosestTo(int var1, int var2);

   public abstract Enumeration<TreePath> getVisiblePathsFrom(TreePath var1);

   public abstract int getVisibleChildCount(TreePath var1);

   public abstract void setExpandedState(TreePath var1, boolean var2);

   public abstract boolean getExpandedState(TreePath var1);

   public abstract int getRowCount();

   public abstract void invalidateSizes();

   public abstract void invalidatePathBounds(TreePath var1);

   public abstract void treeNodesChanged(TreeModelEvent var1);

   public abstract void treeNodesInserted(TreeModelEvent var1);

   public abstract void treeNodesRemoved(TreeModelEvent var1);

   public abstract void treeStructureChanged(TreeModelEvent var1);

   public int[] getRowsForPaths(TreePath[] var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length;
         int[] var3 = new int[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = this.getRowForPath(var1[var4]);
         }

         return var3;
      }
   }

   protected Rectangle getNodeDimensions(Object var1, int var2, int var3, boolean var4, Rectangle var5) {
      AbstractLayoutCache.NodeDimensions var6 = this.getNodeDimensions();
      return var6 != null ? var6.getNodeDimensions(var1, var2, var3, var4, var5) : null;
   }

   protected boolean isFixedRowHeight() {
      return this.rowHeight > 0;
   }

   public abstract static class NodeDimensions {
      public abstract Rectangle getNodeDimensions(Object var1, int var2, int var3, boolean var4, Rectangle var5);
   }
}
