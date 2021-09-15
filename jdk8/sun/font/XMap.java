package sun.font;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;

class XMap {
   private static HashMap xMappers = new HashMap();
   char[] convertedGlyphs;
   static final int SINGLE_BYTE = 1;
   static final int DOUBLE_BYTE = 2;
   private static final char SURR_MIN = '\ud800';
   private static final char SURR_MAX = '\udfff';

   static synchronized XMap getXMapper(String var0) {
      XMap var1 = (XMap)xMappers.get(var0);
      if (var1 == null) {
         var1 = getXMapperInternal(var0);
         xMappers.put(var0, var1);
      }

      return var1;
   }

   private static XMap getXMapperInternal(String var0) {
      String var1 = null;
      byte var2 = 1;
      char var3 = '\uffff';
      short var4 = 0;
      boolean var5 = false;
      boolean var6 = false;
      if (var0.equals("dingbats")) {
         var1 = "sun.awt.motif.X11Dingbats";
         var4 = 9985;
         var3 = 10174;
      } else if (var0.equals("symbol")) {
         var1 = "sun.awt.Symbol";
         var4 = 913;
         var3 = 8943;
      } else if (var0.equals("iso8859-1")) {
         var3 = 255;
      } else if (var0.equals("iso8859-2")) {
         var1 = "ISO8859_2";
      } else if (var0.equals("jisx0208.1983-0")) {
         var1 = "sun.awt.motif.X11JIS0208";
         var2 = 2;
      } else if (var0.equals("jisx0201.1976-0")) {
         var1 = "sun.awt.motif.X11JIS0201";
         var5 = true;
         var6 = true;
      } else if (var0.equals("jisx0212.1990-0")) {
         var1 = "sun.awt.motif.X11JIS0212";
         var2 = 2;
      } else if (var0.equals("iso8859-4")) {
         var1 = "ISO8859_4";
      } else if (var0.equals("iso8859-5")) {
         var1 = "ISO8859_5";
      } else if (var0.equals("koi8-r")) {
         var1 = "KOI8_R";
      } else if (var0.equals("ansi-1251")) {
         var1 = "windows-1251";
      } else if (var0.equals("iso8859-6")) {
         var1 = "ISO8859_6";
      } else if (var0.equals("iso8859-7")) {
         var1 = "ISO8859_7";
      } else if (var0.equals("iso8859-8")) {
         var1 = "ISO8859_8";
      } else if (var0.equals("iso8859-9")) {
         var1 = "ISO8859_9";
      } else if (var0.equals("iso8859-13")) {
         var1 = "ISO8859_13";
      } else if (var0.equals("iso8859-15")) {
         var1 = "ISO8859_15";
      } else if (var0.equals("ksc5601.1987-0")) {
         var1 = "sun.awt.motif.X11KSC5601";
         var2 = 2;
      } else if (var0.equals("ksc5601.1992-3")) {
         var1 = "sun.awt.motif.X11Johab";
         var2 = 2;
      } else if (var0.equals("ksc5601.1987-1")) {
         var1 = "EUC_KR";
         var2 = 2;
      } else if (var0.equals("cns11643-1")) {
         var1 = "sun.awt.motif.X11CNS11643P1";
         var2 = 2;
      } else if (var0.equals("cns11643-2")) {
         var1 = "sun.awt.motif.X11CNS11643P2";
         var2 = 2;
      } else if (var0.equals("cns11643-3")) {
         var1 = "sun.awt.motif.X11CNS11643P3";
         var2 = 2;
      } else if (var0.equals("gb2312.1980-0")) {
         var1 = "sun.awt.motif.X11GB2312";
         var2 = 2;
      } else if (var0.indexOf("big5") >= 0) {
         var1 = "Big5";
         var2 = 2;
         var5 = true;
      } else if (var0.equals("tis620.2533-0")) {
         var1 = "TIS620";
      } else if (var0.equals("gbk-0")) {
         var1 = "sun.awt.motif.X11GBK";
         var2 = 2;
      } else if (var0.indexOf("sun.unicode-0") >= 0) {
         var1 = "sun.awt.motif.X11SunUnicode_0";
         var2 = 2;
      } else if (var0.indexOf("gb18030.2000-1") >= 0) {
         var1 = "sun.awt.motif.X11GB18030_1";
         var2 = 2;
      } else if (var0.indexOf("gb18030.2000-0") >= 0) {
         var1 = "sun.awt.motif.X11GB18030_0";
         var2 = 2;
      } else if (var0.indexOf("hkscs") >= 0) {
         var1 = "sun.awt.HKSCS";
         var2 = 2;
      }

      return new XMap(var1, var4, var3, var2, var5, var6);
   }

   private XMap(String var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      CharsetEncoder var7 = null;
      if (var1 != null) {
         try {
            if (var1.startsWith("sun.awt")) {
               var7 = ((Charset)Class.forName(var1).newInstance()).newEncoder();
            } else {
               var7 = Charset.forName(var1).newEncoder();
            }
         } catch (Exception var16) {
            var16.printStackTrace();
         }
      }

      int var8;
      if (var7 == null) {
         this.convertedGlyphs = new char[256];

         for(var8 = 0; var8 < 256; ++var8) {
            this.convertedGlyphs[var8] = (char)var8;
         }

      } else {
         var8 = var3 - var2 + 1;
         byte[] var9 = new byte[var8 * var4];
         char[] var10 = new char[var8];

         int var11;
         for(var11 = 0; var11 < var8; ++var11) {
            var10[var11] = (char)(var2 + var11);
         }

         var11 = 0;
         if (var4 > 1 && var2 < 256) {
            var11 = 256 - var2;
         }

         byte[] var12 = new byte[var4];

         int var18;
         try {
            boolean var13 = false;
            boolean var14 = false;
            int var19;
            if (var11 < 55296 && var11 + var8 > 57343) {
               var18 = '\ud800' - var11;
               var19 = var18 * var4;
               var7.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(var12).encode(CharBuffer.wrap(var10, var11, var18), ByteBuffer.wrap(var9, var11 * var4, var19), true);
               var11 = 57344;
            }

            var18 = var8 - var11;
            var19 = var18 * var4;
            var7.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(var12).encode(CharBuffer.wrap(var10, var11, var18), ByteBuffer.wrap(var9, var11 * var4, var19), true);
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         this.convertedGlyphs = new char[65536];

         for(var18 = 0; var18 < var8; ++var18) {
            if (var4 == 1) {
               this.convertedGlyphs[var18 + var2] = (char)(var9[var18] & 255);
            } else {
               this.convertedGlyphs[var18 + var2] = (char)(((var9[var18 * 2] & 255) << 8) + (var9[var18 * 2 + 1] & 255));
            }
         }

         var8 = var6 ? 128 : 256;
         if (var5 && this.convertedGlyphs.length >= 256) {
            for(int var17 = 0; var17 < var8; ++var17) {
               if (this.convertedGlyphs[var17] == 0) {
                  this.convertedGlyphs[var17] = (char)var17;
               }
            }
         }

      }
   }
}
