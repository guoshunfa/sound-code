package sun.java2d.xr;

public class GrowableEltArray extends GrowableIntArray {
   private static final int ELT_SIZE = 4;
   GrowableIntArray glyphs;

   public GrowableEltArray(int var1) {
      super(4, var1);
      this.glyphs = new GrowableIntArray(1, var1 * 8);
   }

   public final int getCharCnt(int var1) {
      return this.array[this.getCellIndex(var1) + 0];
   }

   public final void setCharCnt(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 0] = var2;
   }

   public final int getXOff(int var1) {
      return this.array[this.getCellIndex(var1) + 1];
   }

   public final void setXOff(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 1] = var2;
   }

   public final int getYOff(int var1) {
      return this.array[this.getCellIndex(var1) + 2];
   }

   public final void setYOff(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 2] = var2;
   }

   public final int getGlyphSet(int var1) {
      return this.array[this.getCellIndex(var1) + 3];
   }

   public final void setGlyphSet(int var1, int var2) {
      this.array[this.getCellIndex(var1) + 3] = var2;
   }

   public GrowableIntArray getGlyphs() {
      return this.glyphs;
   }

   public void clear() {
      this.glyphs.clear();
      super.clear();
   }
}
