package sun.font;

import java.nio.ByteBuffer;
import java.util.Locale;

public class TrueTypeGlyphMapper extends CharToGlyphMapper {
   static final char REVERSE_SOLIDUS = '\\';
   static final char JA_YEN = '¥';
   static final char JA_FULLWIDTH_TILDE_CHAR = '～';
   static final char JA_WAVE_DASH_CHAR = '〜';
   static final boolean isJAlocale;
   private final boolean needsJAremapping;
   private boolean remapJAWaveDash;
   TrueTypeFont font;
   CMap cmap;
   int numGlyphs;

   public TrueTypeGlyphMapper(TrueTypeFont var1) {
      this.font = var1;

      try {
         this.cmap = CMap.initialize(var1);
      } catch (Exception var3) {
         this.cmap = null;
      }

      if (this.cmap == null) {
         this.handleBadCMAP();
      }

      this.missingGlyph = 0;
      ByteBuffer var2 = var1.getTableBuffer(1835104368);
      if (var2 != null && var2.capacity() >= 6) {
         this.numGlyphs = var2.getChar(4);
      } else {
         this.handleBadCMAP();
      }

      if (FontUtilities.isSolaris && isJAlocale && var1.supportsJA()) {
         this.needsJAremapping = true;
         if (FontUtilities.isSolaris8 && this.getGlyphFromCMAP(12316) == this.missingGlyph) {
            this.remapJAWaveDash = true;
         }
      } else {
         this.needsJAremapping = false;
      }

   }

   public int getNumGlyphs() {
      return this.numGlyphs;
   }

   private char getGlyphFromCMAP(int var1) {
      try {
         char var2 = this.cmap.getGlyph(var1);
         if (var2 >= this.numGlyphs && var2 < '\ufffe') {
            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().warning(this.font + " out of range glyph id=" + Integer.toHexString(var2) + " for char " + Integer.toHexString(var1));
            }

            return (char)this.missingGlyph;
         } else {
            return var2;
         }
      } catch (Exception var3) {
         this.handleBadCMAP();
         return (char)this.missingGlyph;
      }
   }

   private void handleBadCMAP() {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().severe("Null Cmap for " + this.font + "substituting for this font");
      }

      SunFontManager.getInstance().deRegisterBadFont(this.font);
      this.cmap = CMap.theNullCmap;
   }

   private final char remapJAChar(char var1) {
      switch(var1) {
      case '\\':
         return '¥';
      case '〜':
         if (this.remapJAWaveDash) {
            return '～';
         }
      default:
         return var1;
      }
   }

   private final int remapJAIntChar(int var1) {
      switch(var1) {
      case 92:
         return 165;
      case 12316:
         if (this.remapJAWaveDash) {
            return 65374;
         }
      default:
         return var1;
      }
   }

   public int charToGlyph(char var1) {
      if (this.needsJAremapping) {
         var1 = this.remapJAChar(var1);
      }

      char var2 = this.getGlyphFromCMAP(var1);
      if (this.font.checkUseNatives() && var2 < this.font.glyphToCharMap.length) {
         this.font.glyphToCharMap[var2] = var1;
      }

      return var2;
   }

   public int charToGlyph(int var1) {
      if (this.needsJAremapping) {
         var1 = this.remapJAIntChar(var1);
      }

      char var2 = this.getGlyphFromCMAP(var1);
      if (this.font.checkUseNatives() && var2 < this.font.glyphToCharMap.length) {
         this.font.glyphToCharMap[var2] = (char)var1;
      }

      return var2;
   }

   public void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         if (this.needsJAremapping) {
            var3[var4] = this.getGlyphFromCMAP(this.remapJAIntChar(var2[var4]));
         } else {
            var3[var4] = this.getGlyphFromCMAP(var2[var4]);
         }

         if (this.font.checkUseNatives() && var3[var4] < this.font.glyphToCharMap.length) {
            this.font.glyphToCharMap[var3[var4]] = (char)var2[var4];
         }
      }

   }

   public void charsToGlyphs(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         char var5;
         if (this.needsJAremapping) {
            var5 = this.remapJAChar(var2[var4]);
         } else {
            var5 = var2[var4];
         }

         if (var5 >= '\ud800' && var5 <= '\udbff' && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               int var7 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4] = this.getGlyphFromCMAP(var7);
               ++var4;
               var3[var4] = 65535;
               continue;
            }
         }

         var3[var4] = this.getGlyphFromCMAP(var5);
         if (this.font.checkUseNatives() && var3[var4] < this.font.glyphToCharMap.length) {
            this.font.glyphToCharMap[var3[var4]] = (char)var5;
         }
      }

   }

   public boolean charsToGlyphsNS(int var1, char[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         int var5;
         if (this.needsJAremapping) {
            var5 = this.remapJAChar(var2[var4]);
         } else {
            var5 = var2[var4];
         }

         if (var5 >= 55296 && var5 <= 56319 && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               var5 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4 + 1] = 65535;
            }
         }

         var3[var4] = this.getGlyphFromCMAP(var5);
         if (this.font.checkUseNatives() && var3[var4] < this.font.glyphToCharMap.length) {
            this.font.glyphToCharMap[var3[var4]] = (char)var5;
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

   boolean hasSupplementaryChars() {
      return this.cmap instanceof CMap.CMapFormat8 || this.cmap instanceof CMap.CMapFormat10 || this.cmap instanceof CMap.CMapFormat12;
   }

   static {
      isJAlocale = Locale.JAPAN.equals(Locale.getDefault());
   }
}
