package sun.font;

public abstract class CharToGlyphMapper {
   public static final int HI_SURROGATE_START = 55296;
   public static final int HI_SURROGATE_END = 56319;
   public static final int LO_SURROGATE_START = 56320;
   public static final int LO_SURROGATE_END = 57343;
   public static final int UNINITIALIZED_GLYPH = -1;
   public static final int INVISIBLE_GLYPH_ID = 65535;
   public static final int INVISIBLE_GLYPHS = 65534;
   protected int missingGlyph = -1;

   public int getMissingGlyphCode() {
      return this.missingGlyph;
   }

   public boolean canDisplay(char var1) {
      int var2 = this.charToGlyph(var1);
      return var2 != this.missingGlyph;
   }

   public boolean canDisplay(int var1) {
      int var2 = this.charToGlyph(var1);
      return var2 != this.missingGlyph;
   }

   public int charToGlyph(char var1) {
      char[] var2 = new char[1];
      int[] var3 = new int[1];
      var2[0] = var1;
      this.charsToGlyphs(1, (char[])var2, var3);
      return var3[0];
   }

   public int charToGlyph(int var1) {
      int[] var2 = new int[1];
      int[] var3 = new int[1];
      var2[0] = var1;
      this.charsToGlyphs(1, (int[])var2, var3);
      return var3[0];
   }

   public abstract int getNumGlyphs();

   public abstract void charsToGlyphs(int var1, char[] var2, int[] var3);

   public abstract boolean charsToGlyphsNS(int var1, char[] var2, int[] var3);

   public abstract void charsToGlyphs(int var1, int[] var2, int[] var3);
}
