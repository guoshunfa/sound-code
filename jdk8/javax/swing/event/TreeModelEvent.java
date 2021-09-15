package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeModelEvent extends EventObject {
   protected TreePath path;
   protected int[] childIndices;
   protected Object[] children;

   public TreeModelEvent(Object var1, Object[] var2, int[] var3, Object[] var4) {
      this(var1, var2 == null ? null : new TreePath(var2), var3, var4);
   }

   public TreeModelEvent(Object var1, TreePath var2, int[] var3, Object[] var4) {
      super(var1);
      this.path = var2;
      this.childIndices = var3;
      this.children = var4;
   }

   public TreeModelEvent(Object var1, Object[] var2) {
      this(var1, var2 == null ? null : new TreePath(var2));
   }

   public TreeModelEvent(Object var1, TreePath var2) {
      super(var1);
      this.path = var2;
      this.childIndices = new int[0];
   }

   public TreePath getTreePath() {
      return this.path;
   }

   public Object[] getPath() {
      return this.path != null ? this.path.getPath() : null;
   }

   public Object[] getChildren() {
      if (this.children != null) {
         int var1 = this.children.length;
         Object[] var2 = new Object[var1];
         System.arraycopy(this.children, 0, var2, 0, var1);
         return var2;
      } else {
         return null;
      }
   }

   public int[] getChildIndices() {
      if (this.childIndices != null) {
         int var1 = this.childIndices.length;
         int[] var2 = new int[var1];
         System.arraycopy(this.childIndices, 0, var2, 0, var1);
         return var2;
      } else {
         return null;
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getClass().getName() + " " + Integer.toString(this.hashCode()));
      if (this.path != null) {
         var1.append(" path " + this.path);
      }

      int var2;
      if (this.childIndices != null) {
         var1.append(" indices [ ");

         for(var2 = 0; var2 < this.childIndices.length; ++var2) {
            var1.append(Integer.toString(this.childIndices[var2]) + " ");
         }

         var1.append("]");
      }

      if (this.children != null) {
         var1.append(" children [ ");

         for(var2 = 0; var2 < this.children.length; ++var2) {
            var1.append(this.children[var2] + " ");
         }

         var1.append("]");
      }

      return var1.toString();
   }
}
