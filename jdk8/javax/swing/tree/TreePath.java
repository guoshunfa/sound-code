package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class TreePath implements Serializable {
   private TreePath parentPath;
   private Object lastPathComponent;

   @ConstructorProperties({"path"})
   public TreePath(Object[] var1) {
      if (var1 != null && var1.length != 0) {
         this.lastPathComponent = var1[var1.length - 1];
         if (this.lastPathComponent == null) {
            throw new IllegalArgumentException("Last path component must be non-null");
         } else {
            if (var1.length > 1) {
               this.parentPath = new TreePath(var1, var1.length - 1);
            }

         }
      } else {
         throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
      }
   }

   public TreePath(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("path in TreePath must be non null.");
      } else {
         this.lastPathComponent = var1;
         this.parentPath = null;
      }
   }

   protected TreePath(TreePath var1, Object var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("path in TreePath must be non null.");
      } else {
         this.parentPath = var1;
         this.lastPathComponent = var2;
      }
   }

   protected TreePath(Object[] var1, int var2) {
      this.lastPathComponent = var1[var2 - 1];
      if (this.lastPathComponent == null) {
         throw new IllegalArgumentException("Path elements must be non-null");
      } else {
         if (var2 > 1) {
            this.parentPath = new TreePath(var1, var2 - 1);
         }

      }
   }

   protected TreePath() {
   }

   public Object[] getPath() {
      int var1 = this.getPathCount();
      Object[] var2 = new Object[var1--];

      for(TreePath var3 = this; var3 != null; var3 = var3.getParentPath()) {
         var2[var1--] = var3.getLastPathComponent();
      }

      return var2;
   }

   public Object getLastPathComponent() {
      return this.lastPathComponent;
   }

   public int getPathCount() {
      int var1 = 0;

      for(TreePath var2 = this; var2 != null; var2 = var2.getParentPath()) {
         ++var1;
      }

      return var1;
   }

   public Object getPathComponent(int var1) {
      int var2 = this.getPathCount();
      if (var1 >= 0 && var1 < var2) {
         TreePath var3 = this;

         for(int var4 = var2 - 1; var4 != var1; --var4) {
            var3 = var3.getParentPath();
         }

         return var3.getLastPathComponent();
      } else {
         throw new IllegalArgumentException("Index " + var1 + " is out of the specified range");
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof TreePath) {
         TreePath var2 = (TreePath)var1;
         if (this.getPathCount() != var2.getPathCount()) {
            return false;
         } else {
            for(TreePath var3 = this; var3 != null; var3 = var3.getParentPath()) {
               if (!var3.getLastPathComponent().equals(var2.getLastPathComponent())) {
                  return false;
               }

               var2 = var2.getParentPath();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getLastPathComponent().hashCode();
   }

   public boolean isDescendant(TreePath var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         int var2 = this.getPathCount();
         int var3 = var1.getPathCount();
         if (var3 < var2) {
            return false;
         } else {
            while(var3-- > var2) {
               var1 = var1.getParentPath();
            }

            return this.equals(var1);
         }
      }
   }

   public TreePath pathByAddingChild(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("Null child not allowed");
      } else {
         return new TreePath(this, var1);
      }
   }

   public TreePath getParentPath() {
      return this.parentPath;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("[");
      int var2 = 0;

      for(int var3 = this.getPathCount(); var2 < var3; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.getPathComponent(var2));
      }

      var1.append("]");
      return var1.toString();
   }
}
