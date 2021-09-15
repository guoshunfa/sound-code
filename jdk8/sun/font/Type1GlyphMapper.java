package sun.font;

public final class Type1GlyphMapper extends CharToGlyphMapper {
   Type1Font font;
   FontScaler scaler;

   public Type1GlyphMapper(Type1Font var1) {
      this.font = var1;
      this.initMapper();
   }

   private void initMapper() {
      this.scaler = this.font.getScaler();

      try {
         this.missingGlyph = this.scaler.getMissingGlyphCode();
      } catch (FontScalerException var4) {
         this.scaler = FontScaler.getNullScaler();

         try {
            this.missingGlyph = this.scaler.getMissingGlyphCode();
         } catch (FontScalerException var3) {
            this.missingGlyph = 0;
         }
      }

   }

   public int getNumGlyphs() {
      try {
         return this.scaler.getNumGlyphs();
      } catch (FontScalerException var2) {
         this.scaler = FontScaler.getNullScaler();
         return this.getNumGlyphs();
      }
   }

   public int getMissingGlyphCode() {
      return this.missingGlyph;
   }

   public boolean canDisplay(char var1) {
      try {
         return this.scaler.getGlyphCode(var1) != this.missingGlyph;
      } catch (FontScalerException var3) {
         this.scaler = FontScaler.getNullScaler();
         return this.canDisplay(var1);
      }
   }

   public int charToGlyph(char var1) {
      try {
         return this.scaler.getGlyphCode(var1);
      } catch (FontScalerException var3) {
         this.scaler = FontScaler.getNullScaler();
         return this.charToGlyph(var1);
      }
   }

   public int charToGlyph(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         try {
            return this.scaler.getGlyphCode((char)var1);
         } catch (FontScalerException var3) {
            this.scaler = FontScaler.getNullScaler();
            return this.charToGlyph(var1);
         }
      } else {
         return this.missingGlyph;
      }
   }

   public void charsToGlyphs(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         int var5 = var2[var4];
         if (var5 >= 55296 && var5 <= 56319 && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               var5 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4 + 1] = 65535;
            }
         }

         var3[var4] = this.charToGlyph(var5);
         if (var5 >= 65536) {
            ++var4;
         }
      }

   }

   public void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         var3[var4] = this.charToGlyph(var2[var4]);
      }

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

         var3[var4] = this.charToGlyph(var5);
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
}
