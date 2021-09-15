package sun.font;

public class NativeGlyphMapper extends CharToGlyphMapper {
   NativeFont font;
   XMap xmapper;
   int numGlyphs;

   NativeGlyphMapper(NativeFont var1) {
      this.font = var1;
      this.xmapper = XMap.getXMapper(this.font.encoding);
      this.numGlyphs = var1.getNumGlyphs();
      this.missingGlyph = 0;
   }

   public int getNumGlyphs() {
      return this.numGlyphs;
   }

   public int charToGlyph(char var1) {
      return var1 >= this.xmapper.convertedGlyphs.length ? 0 : this.xmapper.convertedGlyphs[var1];
   }

   public int charToGlyph(int var1) {
      return var1 >= this.xmapper.convertedGlyphs.length ? 0 : this.xmapper.convertedGlyphs[var1];
   }

   public void charsToGlyphs(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var2[var4];
         if (var5 >= this.xmapper.convertedGlyphs.length) {
            var3[var4] = 0;
         } else {
            var3[var4] = this.xmapper.convertedGlyphs[var5];
         }
      }

   }

   public boolean charsToGlyphsNS(int var1, char[] var2, int[] var3) {
      this.charsToGlyphs(var1, var2, var3);
      return false;
   }

   public void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = (char)var2[var4];
         if (var5 >= this.xmapper.convertedGlyphs.length) {
            var3[var4] = 0;
         } else {
            var3[var4] = this.xmapper.convertedGlyphs[var5];
         }
      }

   }
}
