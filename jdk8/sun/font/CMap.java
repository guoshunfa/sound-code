package sun.font;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

abstract class CMap {
   static final short ShiftJISEncoding = 2;
   static final short GBKEncoding = 3;
   static final short Big5Encoding = 4;
   static final short WansungEncoding = 5;
   static final short JohabEncoding = 6;
   static final short MSUnicodeSurrogateEncoding = 10;
   static final char noSuchChar = '�';
   static final int SHORTMASK = 65535;
   static final int INTMASK = -1;
   static final char[][] converterMaps = new char[7][];
   char[] xlat;
   public static final CMap.NullCMapClass theNullCmap = new CMap.NullCMapClass();

   static CMap initialize(TrueTypeFont var0) {
      CMap var1 = null;
      boolean var4 = true;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      boolean var13 = false;
      ByteBuffer var14 = var0.getTableBuffer(1668112752);
      int var15 = var0.getTableSize(1668112752);
      short var16 = var14.getShort(2);

      for(int var17 = 0; var17 < var16; ++var17) {
         var14.position(var17 * 8 + 4);
         short var3 = var14.getShort();
         if (var3 == 3) {
            var13 = true;
            short var18 = var14.getShort();
            int var2 = var14.getInt();
            switch(var18) {
            case 0:
               var5 = var2;
               break;
            case 1:
               var6 = var2;
               break;
            case 2:
               var7 = var2;
               break;
            case 3:
               var8 = var2;
               break;
            case 4:
               var9 = var2;
               break;
            case 5:
               var10 = var2;
               break;
            case 6:
               var11 = var2;
            case 7:
            case 8:
            case 9:
            default:
               break;
            case 10:
               var12 = var2;
            }
         }
      }

      if (var13) {
         if (var12 != 0) {
            var1 = createCMap(var14, var12, (char[])null);
         } else if (var5 != 0) {
            var1 = createCMap(var14, var5, (char[])null);
         } else if (var6 != 0) {
            var1 = createCMap(var14, var6, (char[])null);
         } else if (var7 != 0) {
            var1 = createCMap(var14, var7, getConverterMap((short)2));
         } else if (var8 != 0) {
            var1 = createCMap(var14, var8, getConverterMap((short)3));
         } else if (var9 != 0) {
            if (FontUtilities.isSolaris && var0.platName != null && (var0.platName.startsWith("/usr/openwin/lib/locale/zh_CN.EUC/X11/fonts/TrueType") || var0.platName.startsWith("/usr/openwin/lib/locale/zh_CN/X11/fonts/TrueType") || var0.platName.startsWith("/usr/openwin/lib/locale/zh/X11/fonts/TrueType"))) {
               var1 = createCMap(var14, var9, getConverterMap((short)3));
            } else {
               var1 = createCMap(var14, var9, getConverterMap((short)4));
            }
         } else if (var10 != 0) {
            var1 = createCMap(var14, var10, getConverterMap((short)5));
         } else if (var11 != 0) {
            var1 = createCMap(var14, var11, getConverterMap((short)6));
         }
      } else {
         var1 = createCMap(var14, var14.getInt(8), (char[])null);
      }

      return var1;
   }

   static char[] getConverter(short var0) {
      char var1 = '耀';
      char var2 = '\uffff';
      String var3;
      switch(var0) {
      case 2:
         var1 = '腀';
         var2 = 'ﳼ';
         var3 = "SJIS";
         break;
      case 3:
         var1 = '腀';
         var2 = 'ﺠ';
         var3 = "GBK";
         break;
      case 4:
         var1 = 'ꅀ';
         var2 = '\ufefe';
         var3 = "Big5";
         break;
      case 5:
         var1 = 'ꆡ';
         var2 = 'ﻞ';
         var3 = "EUC_KR";
         break;
      case 6:
         var1 = '腁';
         var2 = '\ufdfe';
         var3 = "Johab";
         break;
      default:
         return null;
      }

      try {
         char[] var4 = new char[65536];

         for(int var5 = 0; var5 < 65536; ++var5) {
            var4[var5] = '�';
         }

         byte[] var12 = new byte[(var2 - var1 + 1) * 2];
         char[] var6 = new char[var2 - var1 + 1];
         int var7 = 0;
         int var9;
         if (var0 == 2) {
            for(var9 = var1; var9 <= var2; ++var9) {
               int var8 = var9 >> 8 & 255;
               if (var8 >= 161 && var8 <= 223) {
                  var12[var7++] = -1;
                  var12[var7++] = -1;
               } else {
                  var12[var7++] = (byte)var8;
                  var12[var7++] = (byte)(var9 & 255);
               }
            }
         } else {
            for(var9 = var1; var9 <= var2; ++var9) {
               var12[var7++] = (byte)(var9 >> 8 & 255);
               var12[var7++] = (byte)(var9 & 255);
            }
         }

         Charset.forName(var3).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("\u0000").decode(ByteBuffer.wrap(var12, 0, var12.length), CharBuffer.wrap((char[])var6, 0, var6.length), true);

         for(var9 = 32; var9 <= 126; ++var9) {
            var4[var9] = (char)var9;
         }

         if (var0 == 2) {
            for(var9 = 161; var9 <= 223; ++var9) {
               var4[var9] = (char)(var9 - 161 + '｡');
            }
         }

         System.arraycopy(var6, 0, var4, var1, var6.length);
         char[] var13 = new char[65536];

         for(int var10 = 0; var10 < 65536; ++var10) {
            if (var4[var10] != '�') {
               var13[var4[var10]] = (char)var10;
            }
         }

         return var13;
      } catch (Exception var11) {
         var11.printStackTrace();
         return null;
      }
   }

   static char[] getConverterMap(short var0) {
      if (converterMaps[var0] == null) {
         converterMaps[var0] = getConverter(var0);
      }

      return converterMaps[var0];
   }

   static CMap createCMap(ByteBuffer var0, int var1, char[] var2) {
      char var3 = var0.getChar(var1);
      long var4;
      if (var3 < '\b') {
         var4 = (long)var0.getChar(var1 + 2);
      } else {
         var4 = (long)(var0.getInt(var1 + 4) & -1);
      }

      if ((long)var1 + var4 > (long)var0.capacity() && FontUtilities.isLogging()) {
         FontUtilities.getLogger().warning("Cmap subtable overflows buffer.");
      }

      switch(var3) {
      case '\u0000':
         return new CMap.CMapFormat0(var0, var1);
      case '\u0001':
      case '\u0003':
      case '\u0005':
      case '\u0007':
      case '\t':
      case '\u000b':
      default:
         throw new RuntimeException("Cmap format unimplemented: " + var0.getChar(var1));
      case '\u0002':
         return new CMap.CMapFormat2(var0, var1, var2);
      case '\u0004':
         return new CMap.CMapFormat4(var0, var1, var2);
      case '\u0006':
         return new CMap.CMapFormat6(var0, var1, var2);
      case '\b':
         return new CMap.CMapFormat8(var0, var1, var2);
      case '\n':
         return new CMap.CMapFormat10(var0, var1, var2);
      case '\f':
         return new CMap.CMapFormat12(var0, var1, var2);
      }
   }

   abstract char getGlyph(int var1);

   final int getControlCodeGlyph(int var1, boolean var2) {
      if (var1 < 16) {
         switch(var1) {
         case 9:
         case 10:
         case 13:
            return 65535;
         case 11:
         case 12:
         }
      } else if (var1 >= 8204) {
         if (var1 <= 8207 || var1 >= 8232 && var1 <= 8238 || var1 >= 8298 && var1 <= 8303) {
            return 65535;
         }

         if (var2 && var1 >= 65535) {
            return 0;
         }
      }

      return -1;
   }

   static class NullCMapClass extends CMap {
      char getGlyph(int var1) {
         return '\u0000';
      }
   }

   static class CMapFormat12 extends CMap {
      int numGroups;
      int highBit = 0;
      int power;
      int extra;
      long[] startCharCode;
      long[] endCharCode;
      int[] startGlyphID;

      CMapFormat12(ByteBuffer var1, int var2, char[] var3) {
         if (var3 != null) {
            throw new RuntimeException("xlat array for cmap fmt=12");
         } else {
            this.numGroups = var1.getInt(var2 + 12);
            this.startCharCode = new long[this.numGroups];
            this.endCharCode = new long[this.numGroups];
            this.startGlyphID = new int[this.numGroups];
            var1.position(var2 + 16);
            var1 = var1.slice();
            IntBuffer var4 = var1.asIntBuffer();

            int var5;
            for(var5 = 0; var5 < this.numGroups; ++var5) {
               this.startCharCode[var5] = (long)(var4.get() & -1);
               this.endCharCode[var5] = (long)(var4.get() & -1);
               this.startGlyphID[var5] = var4.get() & -1;
            }

            var5 = this.numGroups;
            if (var5 >= 65536) {
               var5 >>= 16;
               this.highBit += 16;
            }

            if (var5 >= 256) {
               var5 >>= 8;
               this.highBit += 8;
            }

            if (var5 >= 16) {
               var5 >>= 4;
               this.highBit += 4;
            }

            if (var5 >= 4) {
               var5 >>= 2;
               this.highBit += 2;
            }

            if (var5 >= 2) {
               var5 >>= 1;
               ++this.highBit;
            }

            this.power = 1 << this.highBit;
            this.extra = this.numGroups - this.power;
         }
      }

      char getGlyph(int var1) {
         int var2 = this.getControlCodeGlyph(var1, false);
         if (var2 >= 0) {
            return (char)var2;
         } else {
            int var3 = this.power;
            int var4 = 0;
            if (this.startCharCode[this.extra] <= (long)var1) {
               var4 = this.extra;
            }

            while(var3 > 1) {
               var3 >>= 1;
               if (this.startCharCode[var4 + var3] <= (long)var1) {
                  var4 += var3;
               }
            }

            if (this.startCharCode[var4] <= (long)var1 && this.endCharCode[var4] >= (long)var1) {
               return (char)((int)((long)this.startGlyphID[var4] + ((long)var1 - this.startCharCode[var4])));
            } else {
               return '\u0000';
            }
         }
      }
   }

   static class CMapFormat10 extends CMap {
      long firstCode;
      int entryCount;
      char[] glyphIdArray;

      CMapFormat10(ByteBuffer var1, int var2, char[] var3) {
         this.firstCode = (long)(var1.getInt() & -1);
         this.entryCount = var1.getInt() & -1;
         var1.position(var2 + 20);
         CharBuffer var4 = var1.asCharBuffer();
         this.glyphIdArray = new char[this.entryCount];

         for(int var5 = 0; var5 < this.entryCount; ++var5) {
            this.glyphIdArray[var5] = var4.get();
         }

      }

      char getGlyph(int var1) {
         if (this.xlat != null) {
            throw new RuntimeException("xlat array for cmap fmt=10");
         } else {
            int var2 = (int)((long)var1 - this.firstCode);
            return var2 >= 0 && var2 < this.entryCount ? this.glyphIdArray[var2] : '\u0000';
         }
      }
   }

   static class CMapFormat8 extends CMap {
      byte[] is32 = new byte[8192];
      int nGroups;
      int[] startCharCode;
      int[] endCharCode;
      int[] startGlyphID;

      CMapFormat8(ByteBuffer var1, int var2, char[] var3) {
         var1.position(12);
         var1.get(this.is32);
         this.nGroups = var1.getInt();
         this.startCharCode = new int[this.nGroups];
         this.endCharCode = new int[this.nGroups];
         this.startGlyphID = new int[this.nGroups];
      }

      char getGlyph(int var1) {
         if (this.xlat != null) {
            throw new RuntimeException("xlat array for cmap fmt=8");
         } else {
            return '\u0000';
         }
      }
   }

   static class CMapFormat6 extends CMap {
      char firstCode;
      char entryCount;
      char[] glyphIdArray;

      CMapFormat6(ByteBuffer var1, int var2, char[] var3) {
         var1.position(var2 + 6);
         CharBuffer var4 = var1.asCharBuffer();
         this.firstCode = var4.get();
         this.entryCount = var4.get();
         this.glyphIdArray = new char[this.entryCount];

         for(int var5 = 0; var5 < this.entryCount; ++var5) {
            this.glyphIdArray[var5] = var4.get();
         }

      }

      char getGlyph(int var1) {
         int var2 = this.getControlCodeGlyph(var1, true);
         if (var2 >= 0) {
            return (char)var2;
         } else {
            if (this.xlat != null) {
               var1 = this.xlat[var1];
            }

            var1 -= this.firstCode;
            return var1 >= 0 && var1 < this.entryCount ? this.glyphIdArray[var1] : '\u0000';
         }
      }
   }

   static class CMapFormat2 extends CMap {
      char[] subHeaderKey = new char[256];
      char[] firstCodeArray;
      char[] entryCountArray;
      short[] idDeltaArray;
      char[] idRangeOffSetArray;
      char[] glyphIndexArray;

      CMapFormat2(ByteBuffer var1, int var2, char[] var3) {
         this.xlat = var3;
         char var4 = var1.getChar(var2 + 2);
         var1.position(var2 + 6);
         CharBuffer var5 = var1.asCharBuffer();
         char var6 = 0;

         int var7;
         for(var7 = 0; var7 < 256; ++var7) {
            this.subHeaderKey[var7] = var5.get();
            if (this.subHeaderKey[var7] > var6) {
               var6 = this.subHeaderKey[var7];
            }
         }

         var7 = (var6 >> 3) + 1;
         this.firstCodeArray = new char[var7];
         this.entryCountArray = new char[var7];
         this.idDeltaArray = new short[var7];
         this.idRangeOffSetArray = new char[var7];

         int var8;
         for(var8 = 0; var8 < var7; ++var8) {
            this.firstCodeArray[var8] = var5.get();
            this.entryCountArray[var8] = var5.get();
            this.idDeltaArray[var8] = (short)var5.get();
            this.idRangeOffSetArray[var8] = var5.get();
         }

         var8 = (var4 - 518 - var7 * 8) / 2;
         this.glyphIndexArray = new char[var8];

         for(int var9 = 0; var9 < var8; ++var9) {
            this.glyphIndexArray[var9] = var5.get();
         }

      }

      char getGlyph(int var1) {
         int var2 = this.getControlCodeGlyph(var1, true);
         if (var2 >= 0) {
            return (char)var2;
         } else {
            if (this.xlat != null) {
               var1 = this.xlat[var1];
            }

            char var3 = (char)(var1 >> 8);
            char var4 = (char)(var1 & 255);
            int var5 = this.subHeaderKey[var3] >> 3;
            char var6;
            if (var5 != 0) {
               var6 = var4;
            } else {
               var6 = var3;
               if (var3 == 0) {
                  var6 = var4;
               }
            }

            char var7 = this.firstCodeArray[var5];
            if (var6 < var7) {
               return '\u0000';
            } else {
               var6 -= var7;
               if (var6 < this.entryCountArray[var5]) {
                  int var8 = (this.idRangeOffSetArray.length - var5) * 8 - 6;
                  int var9 = (this.idRangeOffSetArray[var5] - var8) / 2;
                  char var10 = this.glyphIndexArray[var9 + var6];
                  if (var10 != 0) {
                     var10 = (char)(var10 + this.idDeltaArray[var5]);
                     return var10;
                  }
               }

               return '\u0000';
            }
         }
      }
   }

   static class CMapFormat0 extends CMap {
      byte[] cmap;

      CMapFormat0(ByteBuffer var1, int var2) {
         char var3 = var1.getChar(var2 + 2);
         this.cmap = new byte[var3 - 6];
         var1.position(var2 + 6);
         var1.get(this.cmap);
      }

      char getGlyph(int var1) {
         if (var1 < 256) {
            if (var1 < 16) {
               switch(var1) {
               case 9:
               case 10:
               case 13:
                  return '\uffff';
               case 11:
               case 12:
               }
            }

            return (char)(255 & this.cmap[var1]);
         } else {
            return '\u0000';
         }
      }
   }

   static class CMapFormat4 extends CMap {
      int segCount;
      int entrySelector;
      int rangeShift;
      char[] endCount;
      char[] startCount;
      short[] idDelta;
      char[] idRangeOffset;
      char[] glyphIds;

      CMapFormat4(ByteBuffer var1, int var2, char[] var3) {
         this.xlat = var3;
         var1.position(var2);
         CharBuffer var4 = var1.asCharBuffer();
         var4.get();
         int var5 = var4.get();
         if (var2 + var5 > var1.capacity()) {
            var5 = var1.capacity() - var2;
         }

         var4.get();
         this.segCount = var4.get() / 2;
         char var6 = var4.get();
         this.entrySelector = var4.get();
         this.rangeShift = var4.get() / 2;
         this.startCount = new char[this.segCount];
         this.endCount = new char[this.segCount];
         this.idDelta = new short[this.segCount];
         this.idRangeOffset = new char[this.segCount];

         int var7;
         for(var7 = 0; var7 < this.segCount; ++var7) {
            this.endCount[var7] = var4.get();
         }

         var4.get();

         for(var7 = 0; var7 < this.segCount; ++var7) {
            this.startCount[var7] = var4.get();
         }

         for(var7 = 0; var7 < this.segCount; ++var7) {
            this.idDelta[var7] = (short)var4.get();
         }

         for(var7 = 0; var7 < this.segCount; ++var7) {
            char var8 = var4.get();
            this.idRangeOffset[var7] = (char)(var8 >> 1 & '\uffff');
         }

         var7 = (this.segCount * 8 + 16) / 2;
         var4.position(var7);
         int var10 = var5 / 2 - var7;
         this.glyphIds = new char[var10];

         for(int var9 = 0; var9 < var10; ++var9) {
            this.glyphIds[var9] = var4.get();
         }

      }

      char getGlyph(int var1) {
         boolean var2 = false;
         char var3 = 0;
         int var4 = this.getControlCodeGlyph(var1, true);
         if (var4 >= 0) {
            return (char)var4;
         } else {
            if (this.xlat != null) {
               var1 = this.xlat[var1];
            }

            int var5 = 0;
            int var6 = this.startCount.length;

            int var9;
            for(var9 = this.startCount.length >> 1; var5 < var6; var9 = var5 + var6 >> 1) {
               if (this.endCount[var9] < var1) {
                  var5 = var9 + 1;
               } else {
                  var6 = var9;
               }
            }

            if (var1 >= this.startCount[var9] && var1 <= this.endCount[var9]) {
               char var7 = this.idRangeOffset[var9];
               if (var7 == 0) {
                  var3 = (char)(var1 + this.idDelta[var9]);
               } else {
                  int var8 = var7 - this.segCount + var9 + (var1 - this.startCount[var9]);
                  var3 = this.glyphIds[var8];
                  if (var3 != 0) {
                     var3 = (char)(var3 + this.idDelta[var9]);
                  }
               }
            }

            if (var3 != 0) {
            }

            return var3;
         }
      }
   }
}
