package sun.java2d.xr;

public class GrowableRectArray extends GrowableIntArray {
   private static final int RECT_SIZE = 4;

   public GrowableRectArray(int var1) {
      super(4, var1);
   }

   public final void pushRectValues(int var1, int var2, int var3, int var4) {
      int var5 = this.size;
      this.size += 4;
      if (this.size >= this.array.length) {
         this.growArray();
      }

      this.array[var5] = var1;
      this.array[var5 + 1] = var2;
      this.array[var5 + 2] = var3;
      this.array[var5 + 3] = var4;
   }

   public final void setX(int var1, int var2) {
      this.array[this.getCellIndex(var1)] = var2;
   }

   public final void setY(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 1] = var2;
   }

   public final void setWidth(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 2] = var2;
   }

   public final void setHeight(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 3] = var2;
   }

   public final int getX(int var1) {
      return this.array[this.getCellIndex(var1)];
   }

   public final int getY(int var1) {
      return this.array[this.getCellIndex(var1) + 1];
   }

   public final int getWidth(int var1) {
      return this.array[this.getCellIndex(var1) + 2];
   }

   public final int getHeight(int var1) {
      return this.array[this.getCellIndex(var1) + 3];
   }

   public final void translateRects(int var1, int var2) {
      for(int var3 = 0; var3 < this.getSize(); ++var3) {
         this.setX(var3, this.getX(var3) + var1);
         this.setY(var3, this.getY(var3) + var2);
      }

   }
}
