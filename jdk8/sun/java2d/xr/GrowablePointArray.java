package sun.java2d.xr;

public class GrowablePointArray extends GrowableIntArray {
   private static final int POINT_SIZE = 2;

   public GrowablePointArray(int var1) {
      super(2, var1);
   }

   public final int getX(int var1) {
      return this.array[this.getCellIndex(var1)];
   }

   public final int getY(int var1) {
      return this.array[this.getCellIndex(var1) + 1];
   }

   public final void setX(int var1, int var2) {
      this.array[this.getCellIndex(var1)] = var2;
   }

   public final void setY(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 1] = var2;
   }
}
