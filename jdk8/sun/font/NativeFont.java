package sun.font;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class NativeFont extends PhysicalFont {
   String encoding;
   private int numGlyphs = -1;
   boolean isBitmapDelegate;
   PhysicalFont delegateFont;

   public NativeFont(String var1, boolean var2) throws FontFormatException {
      super(var1, (Object)null);
      this.isBitmapDelegate = var2;
      if (GraphicsEnvironment.isHeadless()) {
         throw new FontFormatException("Native font in headless toolkit");
      } else {
         this.fontRank = 5;
         this.initNames();
         if (this.getNumGlyphs() == 0) {
            throw new FontFormatException("Couldn't locate font" + var1);
         }
      }
   }

   private void initNames() throws FontFormatException {
      int[] var1 = new int[14];
      int var2 = 1;
      int var3 = 1;
      String var4 = this.platName.toLowerCase(Locale.ENGLISH);
      if (var4.startsWith("-")) {
         while(var3 != -1 && var2 < 14) {
            var3 = var4.indexOf(45, var3);
            if (var3 != -1) {
               var1[var2++] = var3++;
            }
         }
      }

      if (var2 == 14 && var3 != -1) {
         String var5 = var4.substring(var1[1] + 1, var1[2]);
         StringBuilder var6 = new StringBuilder(var5);
         char var7 = Character.toUpperCase(var6.charAt(0));
         var6.replace(0, 1, String.valueOf(var7));

         for(int var8 = 1; var8 < var6.length() - 1; ++var8) {
            if (var6.charAt(var8) == ' ') {
               var7 = Character.toUpperCase(var6.charAt(var8 + 1));
               var6.replace(var8 + 1, var8 + 2, String.valueOf(var7));
            }
         }

         this.familyName = var6.toString();
         String var11 = var4.substring(var1[2] + 1, var1[3]);
         String var9 = var4.substring(var1[3] + 1, var1[4]);
         String var10 = null;
         if (var11.indexOf("bold") >= 0 || var11.indexOf("demi") >= 0) {
            this.style |= 1;
            var10 = "Bold";
         }

         if (!var9.equals("i") && var9.indexOf("italic") < 0) {
            if (var9.equals("o") || var9.indexOf("oblique") >= 0) {
               this.style |= 2;
               if (var10 == null) {
                  var10 = "Oblique";
               } else {
                  var10 = var10 + " Oblique";
               }
            }
         } else {
            this.style |= 2;
            if (var10 == null) {
               var10 = "Italic";
            } else {
               var10 = var10 + " Italic";
            }
         }

         if (var10 == null) {
            this.fullName = this.familyName;
         } else {
            this.fullName = this.familyName + " " + var10;
         }

         this.encoding = var4.substring(var1[12] + 1);
         if (this.encoding.startsWith("-")) {
            this.encoding = var4.substring(var1[13] + 1);
         }

         if (this.encoding.indexOf("fontspecific") >= 0) {
            if (var5.indexOf("dingbats") >= 0) {
               this.encoding = "dingbats";
            } else if (var5.indexOf("symbol") >= 0) {
               this.encoding = "symbol";
            } else {
               this.encoding = "iso8859-1";
            }
         }

      } else {
         throw new FontFormatException("Bad native name " + this.platName);
      }
   }

   static boolean hasExternalBitmaps(String var0) {
      StringBuilder var1 = new StringBuilder(var0);

      for(int var2 = var1.indexOf("-0-"); var2 >= 0; var2 = var1.indexOf("-0-", var2)) {
         var1.replace(var2 + 1, var2 + 2, "*");
      }

      String var3 = var1.toString();
      Object var4 = null;

      byte[] var7;
      try {
         var7 = var3.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var6) {
         var7 = var3.getBytes();
      }

      return haveBitmapFonts(var7);
   }

   public static boolean fontExists(String var0) {
      Object var1 = null;

      byte[] var4;
      try {
         var4 = var0.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var3) {
         var4 = var0.getBytes();
      }

      return fontExists(var4);
   }

   private static native boolean haveBitmapFonts(byte[] var0);

   private static native boolean fontExists(byte[] var0);

   public CharToGlyphMapper getMapper() {
      if (this.mapper == null) {
         if (this.isBitmapDelegate) {
            this.mapper = new NativeGlyphMapper(this);
         } else {
            SunFontManager var1 = SunFontManager.getInstance();
            this.delegateFont = var1.getDefaultPhysicalFont();
            this.mapper = this.delegateFont.getMapper();
         }
      }

      return this.mapper;
   }

   FontStrike createStrike(FontStrikeDesc var1) {
      if (this.isBitmapDelegate) {
         return new NativeStrike(this, var1);
      } else {
         if (this.delegateFont == null) {
            SunFontManager var2 = SunFontManager.getInstance();
            this.delegateFont = var2.getDefaultPhysicalFont();
         }

         if (this.delegateFont instanceof NativeFont) {
            return new NativeStrike((NativeFont)this.delegateFont, var1);
         } else {
            FontStrike var3 = this.delegateFont.createStrike(var1);
            return new DelegateStrike(this, var1, var3);
         }
      }
   }

   public Rectangle2D getMaxCharBounds(FontRenderContext var1) {
      return null;
   }

   native StrikeMetrics getFontMetrics(long var1);

   native float getGlyphAdvance(long var1, int var3);

   Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) {
      return new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) {
      return null;
   }

   native long getGlyphImage(long var1, int var3);

   native long getGlyphImageNoDefault(long var1, int var3);

   void getGlyphMetrics(long var1, int var3, Point2D.Float var4) {
      throw new RuntimeException("this should be called on the strike");
   }

   public GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) {
      return null;
   }

   private native int countGlyphs(byte[] var1, int var2);

   public int getNumGlyphs() {
      if (this.numGlyphs == -1) {
         byte[] var1 = this.getPlatformNameBytes(8);
         this.numGlyphs = this.countGlyphs(var1, 8);
      }

      return this.numGlyphs;
   }

   PhysicalFont getDelegateFont() {
      if (this.delegateFont == null) {
         SunFontManager var1 = SunFontManager.getInstance();
         this.delegateFont = var1.getDefaultPhysicalFont();
      }

      return this.delegateFont;
   }

   byte[] getPlatformNameBytes(int var1) {
      int[] var2 = new int[14];
      int var3 = 1;
      int var4 = 1;

      while(var4 != -1 && var3 < 14) {
         var4 = this.platName.indexOf(45, var4);
         if (var4 != -1) {
            var2[var3++] = var4++;
         }
      }

      String var5 = Integer.toString(Math.abs(var1) * 10);
      StringBuilder var6 = new StringBuilder(this.platName);
      var6.replace(var2[11] + 1, var2[12], "*");
      var6.replace(var2[9] + 1, var2[10], "72");
      var6.replace(var2[8] + 1, var2[9], "72");
      var6.replace(var2[7] + 1, var2[8], var5);
      var6.replace(var2[6] + 1, var2[7], "*");
      if (var2[0] == 0 && var2[1] == 1) {
         var6.replace(var2[0] + 1, var2[1], "*");
      }

      String var7 = var6.toString();
      Object var8 = null;

      byte[] var11;
      try {
         var11 = var7.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var10) {
         var11 = var7.getBytes();
      }

      return var11;
   }

   public String toString() {
      return " ** Native Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " nativeName=" + this.platName;
   }
}
