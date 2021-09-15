package sun.font;

public final class CCompositeGlyphMapper extends CompositeGlyphMapper {
   private CompositeFont font;
   private CharToGlyphMapper[] slotMappers;

   public CCompositeGlyphMapper(CompositeFont var1) {
      super(var1);
      this.font = var1;
      this.slotMappers = new CharToGlyphMapper[this.font.numSlots];
      this.missingGlyph = 0;
   }

   private CharToGlyphMapper getSlotMapper(int var1) {
      CharToGlyphMapper var2 = this.slotMappers[var1];
      if (var2 == null) {
         var2 = this.font.getSlotFont(var1).getMapper();
         this.slotMappers[var1] = var2;
      }

      return var2;
   }

   public boolean canDisplay(char var1) {
      int var2 = this.charToGlyph(var1);
      return var2 != this.missingGlyph;
   }

   private int convertToGlyph(int var1) {
      for(int var2 = 0; var2 < this.font.numSlots; ++var2) {
         CharToGlyphMapper var3 = this.getSlotMapper(var2);
         int var4 = var3.charToGlyph(var1);
         if (var4 > 0) {
            var4 = this.compositeGlyphCode(var2, var4);
            return var4;
         }
      }

      return this.missingGlyph;
   }

   public int getNumGlyphs() {
      int var1 = 0;

      for(int var2 = 0; var2 < 1; ++var2) {
         CharToGlyphMapper var3 = this.slotMappers[var2];
         if (var3 == null) {
            var3 = this.font.getSlotFont(var2).getMapper();
            this.slotMappers[var2] = var3;
         }

         var1 += var3.getNumGlyphs();
      }

      return var1;
   }

   public int charToGlyph(int var1) {
      return this.convertToGlyph(var1);
   }

   public int charToGlyph(char var1) {
      return this.convertToGlyph(var1);
   }

   public boolean charsToGlyphsNS(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         int var5 = var2[var4];
         if (var5 >= 55296 && var5 <= 56319 && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               var5 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4 + 1] = 65535;
            }
         }

         var3[var4] = this.convertToGlyph(var5);
         if (var5 >= 768) {
            if (FontUtilities.isComplexCharCode(var5)) {
               return true;
            }

            if (var5 >= 65536) {
               ++var4;
            }
         }
      }

      return false;
   }

   public void charsToGlyphs(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var2[var4];
         if (var5 >= '\ud800' && var5 <= '\udbff' && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               int var7 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4] = this.convertToGlyph(var7);
               ++var4;
               var3[var4] = 65535;
               continue;
            }
         }

         var3[var4] = this.convertToGlyph(var5);
      }

   }

   public void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         var3[var4] = this.convertToGlyph(var2[var4]);
      }

   }
}
