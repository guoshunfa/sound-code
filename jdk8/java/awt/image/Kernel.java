package java.awt.image;

public class Kernel implements Cloneable {
   private int width;
   private int height;
   private int xOrigin;
   private int yOrigin;
   private float[] data;

   private static native void initIDs();

   public Kernel(int var1, int var2, float[] var3) {
      this.width = var1;
      this.height = var2;
      this.xOrigin = var1 - 1 >> 1;
      this.yOrigin = var2 - 1 >> 1;
      int var4 = var1 * var2;
      if (var3.length < var4) {
         throw new IllegalArgumentException("Data array too small (is " + var3.length + " and should be " + var4);
      } else {
         this.data = new float[var4];
         System.arraycopy(var3, 0, this.data, 0, var4);
      }
   }

   public final int getXOrigin() {
      return this.xOrigin;
   }

   public final int getYOrigin() {
      return this.yOrigin;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final float[] getKernelData(float[] var1) {
      if (var1 == null) {
         var1 = new float[this.data.length];
      } else if (var1.length < this.data.length) {
         throw new IllegalArgumentException("Data array too small (should be " + this.data.length + " but is " + var1.length + " )");
      }

      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return var1;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
