package java.awt;

import java.awt.geom.Dimension2D;
import java.beans.Transient;
import java.io.Serializable;

public class Dimension extends Dimension2D implements Serializable {
   public int width;
   public int height;
   private static final long serialVersionUID = 4723952579491349524L;

   private static native void initIDs();

   public Dimension() {
      this(0, 0);
   }

   public Dimension(Dimension var1) {
      this(var1.width, var1.height);
   }

   public Dimension(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public double getWidth() {
      return (double)this.width;
   }

   public double getHeight() {
      return (double)this.height;
   }

   public void setSize(double var1, double var3) {
      this.width = (int)Math.ceil(var1);
      this.height = (int)Math.ceil(var3);
   }

   @Transient
   public Dimension getSize() {
      return new Dimension(this.width, this.height);
   }

   public void setSize(Dimension var1) {
      this.setSize(var1.width, var1.height);
   }

   public void setSize(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Dimension)) {
         return false;
      } else {
         Dimension var2 = (Dimension)var1;
         return this.width == var2.width && this.height == var2.height;
      }
   }

   public int hashCode() {
      int var1 = this.width + this.height;
      return var1 * (var1 + 1) / 2 + this.width;
   }

   public String toString() {
      return this.getClass().getName() + "[width=" + this.width + ",height=" + this.height + "]";
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }
}
