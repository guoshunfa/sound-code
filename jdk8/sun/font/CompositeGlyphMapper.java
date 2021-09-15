package sun.font;

public class CompositeGlyphMapper extends CharToGlyphMapper {
   public static final int SLOTMASK = -16777216;
   public static final int GLYPHMASK = 16777215;
   public static final int NBLOCKS = 216;
   public static final int BLOCKSZ = 256;
   public static final int MAXUNICODE = 55296;
   CompositeFont font;
   CharToGlyphMapper[] slotMappers;
   int[][] glyphMaps;
   private boolean hasExcludes;

   public CompositeGlyphMapper(CompositeFont var1) {
      this.font = var1;
      this.initMapper();
      this.hasExcludes = var1.exclusionRanges != null && var1.maxIndices != null;
   }

   public final int compositeGlyphCode(int var1, int var2) {
      return var1 << 24 | var2 & 16777215;
   }

   private final void initMapper() {
      if (this.missingGlyph == -1) {
         if (this.glyphMaps == null) {
            this.glyphMaps = new int[216][];
         }

         this.slotMappers = new CharToGlyphMapper[this.font.numSlots];
         this.missingGlyph = this.font.getSlotFont(0).getMissingGlyphCode();
         this.missingGlyph = this.compositeGlyphCode(0, this.missingGlyph);
      }

   }

   private int getCachedGlyphCode(int var1) {
      if (var1 >= 55296) {
         return -1;
      } else {
         int[] var2;
         return (var2 = this.glyphMaps[var1 >> 8]) == null ? -1 : var2[var1 & 255];
      }
   }

   private void setCachedGlyphCode(int var1, int var2) {
      if (var1 < 55296) {
         int var3 = var1 >> 8;
         if (this.glyphMaps[var3] == null) {
            this.glyphMaps[var3] = new int[256];

            for(int var4 = 0; var4 < 256; ++var4) {
               this.glyphMaps[var3][var4] = -1;
            }
         }

         this.glyphMaps[var3][var1 & 255] = var2;
      }
   }

   private final CharToGlyphMapper getSlotMapper(int var1) {
      CharToGlyphMapper var2 = this.slotMappers[var1];
      if (var2 == null) {
         var2 = this.font.getSlotFont(var1).getMapper();
         this.slotMappers[var1] = var2;
      }

      return var2;
   }

   private final int convertToGlyph(int var1) {
      for(int var2 = 0; var2 < this.font.numSlots; ++var2) {
         if (!this.hasExcludes || !this.font.isExcludedChar(var2, var1)) {
            CharToGlyphMapper var3 = this.getSlotMapper(var2);
            int var4 = var3.charToGlyph(var1);
            if (var4 != var3.getMissingGlyphCode()) {
               var4 = this.compositeGlyphCode(var2, var4);
               this.setCachedGlyphCode(var1, var4);
               return var4;
            }
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
      int var2 = this.getCachedGlyphCode(var1);
      if (var2 == -1) {
         var2 = this.convertToGlyph(var1);
      }

      return var2;
   }

   public int charToGlyph(int var1, int var2) {
      if (var2 >= 0) {
         CharToGlyphMapper var3 = this.getSlotMapper(var2);
         int var4 = var3.charToGlyph(var1);
         if (var4 != var3.getMissingGlyphCode()) {
            return this.compositeGlyphCode(var2, var4);
         }
      }

      return this.charToGlyph(var1);
   }

   public int charToGlyph(char var1) {
      int var2 = this.getCachedGlyphCode(var1);
      if (var2 == -1) {
         var2 = this.convertToGlyph(var1);
      }

      return var2;
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

         int var7 = var3[var4] = this.getCachedGlyphCode(var5);
         if (var7 == -1) {
            var3[var4] = this.convertToGlyph(var5);
         }

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
               int var8 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               int var7 = var3[var4] = this.getCachedGlyphCode(var8);
               if (var7 == -1) {
                  var3[var4] = this.convertToGlyph(var8);
               }

               ++var4;
               var3[var4] = 65535;
               continue;
            }
         }

         int var9 = var3[var4] = this.getCachedGlyphCode(var5);
         if (var9 == -1) {
            var3[var4] = this.convertToGlyph(var5);
         }
      }

   }

   public void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         int var5 = var2[var4];
         var3[var4] = this.getCachedGlyphCode(var5);
         if (var3[var4] == -1) {
            var3[var4] = this.convertToGlyph(var5);
         }
      }

   }
}
