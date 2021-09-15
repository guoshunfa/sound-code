package java.awt;

import java.io.Serializable;

public class Insets implements Cloneable, Serializable {
   public int top;
   public int left;
   public int bottom;
   public int right;
   private static final long serialVersionUID = -2272572637695466749L;

   public Insets(int var1, int var2, int var3, int var4) {
      this.top = var1;
      this.left = var2;
      this.bottom = var3;
      this.right = var4;
   }

   public void set(int var1, int var2, int var3, int var4) {
      this.top = var1;
      this.left = var2;
      this.bottom = var3;
      this.right = var4;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Insets)) {
         return false;
      } else {
         Insets var2 = (Insets)var1;
         return this.top == var2.top && this.left == var2.left && this.bottom == var2.bottom && this.right == var2.right;
      }
   }

   public int hashCode() {
      int var1 = this.left + this.bottom;
      int var2 = this.right + this.top;
      int var3 = var1 * (var1 + 1) / 2 + this.left;
      int var4 = var2 * (var2 + 1) / 2 + this.top;
      int var5 = var3 + var4;
      return var5 * (var5 + 1) / 2 + var4;
   }

   public String toString() {
      return this.getClass().getName() + "[top=" + this.top + ",left=" + this.left + ",bottom=" + this.bottom + ",right=" + this.right + "]";
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   private static native void initIDs();

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }
}
