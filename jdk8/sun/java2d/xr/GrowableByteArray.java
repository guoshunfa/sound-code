package sun.java2d.xr;

import java.util.Arrays;

public class GrowableByteArray {
   byte[] array;
   int size;
   int cellSize;

   public GrowableByteArray(int var1, int var2) {
      this.array = new byte[var2];
      this.size = 0;
      this.cellSize = var1;
   }

   private int getNextCellIndex() {
      int var1 = this.size;
      this.size += this.cellSize;
      if (this.size >= this.array.length) {
         this.growArray();
      }

      return var1;
   }

   public byte[] getArray() {
      return this.array;
   }

   public byte[] getSizedArray() {
      return Arrays.copyOf(this.array, this.getSize());
   }

   public final int getByte(int var1) {
      return this.array[this.getCellIndex(var1)];
   }

   public final int getNextIndex() {
      return this.getNextCellIndex() / this.cellSize;
   }

   protected final int getCellIndex(int var1) {
      return this.cellSize * var1;
   }

   public final void addByte(byte var1) {
      int var2 = this.getNextIndex();
      this.array[var2] = var1;
   }

   public final int getSize() {
      return this.size / this.cellSize;
   }

   public void clear() {
      this.size = 0;
   }

   protected void growArray() {
      int var1 = Math.max(this.array.length * 2, 10);
      byte[] var2 = this.array;
      this.array = new byte[var1];
      System.arraycopy(var2, 0, this.array, 0, var2.length);
   }
}
