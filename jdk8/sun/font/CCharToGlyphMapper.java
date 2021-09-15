package sun.font;

import java.util.HashMap;

public class CCharToGlyphMapper extends CharToGlyphMapper {
   private CCharToGlyphMapper.Cache cache = new CCharToGlyphMapper.Cache();
   CFont fFont;
   int numGlyphs = -1;

   private static native int countGlyphs(long var0);

   public CCharToGlyphMapper(CFont var1) {
      this.fFont = var1;
      this.missingGlyph = 0;
   }

   public int getNumGlyphs() {
      if (this.numGlyphs == -1) {
         this.numGlyphs = countGlyphs(this.fFont.getNativeFontPtr());
      }

      return this.numGlyphs;
   }

   public boolean canDisplay(char var1) {
      int var2 = this.charToGlyph(var1);
      return var2 != this.missingGlyph;
   }

   public boolean canDisplay(int var1) {
      int var2 = this.charToGlyph(var1);
      return var2 != this.missingGlyph;
   }

   public synchronized boolean charsToGlyphsNS(int var1, char[] var2, int[] var3) {
      this.charsToGlyphs(var1, var2, var3);

      for(int var4 = 0; var4 < var1; ++var4) {
         int var5 = var2[var4];
         if (var5 >= 55296 && var5 <= 56319 && var4 < var1 - 1) {
            char var6 = var2[var4 + 1];
            if (var6 >= '\udc00' && var6 <= '\udfff') {
               var5 = (var5 - '\ud800') * 1024 + var6 - '\udc00' + 65536;
               var3[var4 + 1] = 65535;
            }
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

   public synchronized int charToGlyph(char var1) {
      int var2 = this.cache.get(var1);
      if (var2 != 0) {
         return var2;
      } else {
         char[] var3 = new char[]{var1};
         int[] var4 = new int[1];
         nativeCharsToGlyphs(this.fFont.getNativeFontPtr(), 1, var3, var4);
         this.cache.put(var1, var4[0]);
         return var4[0];
      }
   }

   public synchronized int charToGlyph(int var1) {
      if (var1 >= 65536) {
         int[] var2 = new int[2];
         char[] var3 = new char[2];
         int var4 = var1 - 65536;
         var3[0] = (char)((var4 >>> 10) + '\ud800');
         var3[1] = (char)(var4 % 1024 + '\udc00');
         this.charsToGlyphs(2, (char[])var3, var2);
         return var2[0];
      } else {
         return this.charToGlyph((char)var1);
      }
   }

   public synchronized void charsToGlyphs(int var1, char[] var2, int[] var3) {
      this.cache.get(var1, var2, var3);
   }

   public synchronized void charsToGlyphs(int var1, int[] var2, int[] var3) {
      for(int var4 = 0; var4 < var1; ++var4) {
         var3[var4] = this.charToGlyph(var2[var4]);
      }

   }

   private static native void nativeCharsToGlyphs(long var0, int var2, char[] var3, int[] var4);

   private class Cache {
      private static final int FIRST_LAYER_SIZE = 256;
      private static final int SECOND_LAYER_SIZE = 16384;
      private final int[] firstLayerCache = new int[256];
      private CCharToGlyphMapper.Cache.SparseBitShiftingTwoLayerArray secondLayerCache;
      private HashMap<Integer, Integer> generalCache;

      Cache() {
         this.firstLayerCache[1] = 1;
      }

      public synchronized int get(int var1) {
         if (var1 < 256) {
            return this.firstLayerCache[var1];
         } else if (var1 < 16384) {
            return this.secondLayerCache == null ? 0 : this.secondLayerCache.get(var1);
         } else if (this.generalCache == null) {
            return 0;
         } else {
            Integer var2 = (Integer)this.generalCache.get(var1);
            return var2 == null ? 0 : var2;
         }
      }

      public synchronized void put(int var1, int var2) {
         if (var1 < 256) {
            this.firstLayerCache[var1] = var2;
         } else if (var1 < 16384) {
            if (this.secondLayerCache == null) {
               this.secondLayerCache = new CCharToGlyphMapper.Cache.SparseBitShiftingTwoLayerArray(16384, 7);
            }

            this.secondLayerCache.put(var1, var2);
         } else {
            if (this.generalCache == null) {
               this.generalCache = new HashMap();
            }

            this.generalCache.put(var1, var2);
         }
      }

      public synchronized void get(int var1, char[] var2, int[] var3) {
         int var4 = 0;
         char[] var5 = null;
         int[] var6 = null;

         int var8;
         int var13;
         for(int var7 = 0; var7 < var1; ++var7) {
            var8 = var2[var7];
            if (var8 >= 55296 && var8 <= 56319 && var7 < var1 - 1) {
               char var9 = var2[var7 + 1];
               if (var9 >= '\udc00' && var9 <= '\udfff') {
                  var8 = (var8 - '\ud800') * 1024 + var9 - '\udc00' + 65536;
               }
            }

            var13 = this.get(var8);
            if (var13 != 0 && var13 != -1) {
               var3[var7] = var13;
               if (var8 >= 65536) {
                  var3[var7 + 1] = 65535;
                  ++var7;
               }
            } else {
               var3[var7] = 0;
               this.put(var8, -1);
               if (var5 == null) {
                  var5 = new char[var2.length];
                  var6 = new int[var2.length];
               }

               var5[var4] = var2[var7];
               var6[var4] = var7;
               if (var8 >= 65536) {
                  ++var4;
                  ++var7;
                  var5[var4] = var2[var7];
               }

               ++var4;
            }
         }

         if (var4 != 0) {
            int[] var12 = new int[var4];
            CCharToGlyphMapper.nativeCharsToGlyphs(CCharToGlyphMapper.this.fFont.getNativeFontPtr(), var4, var5, var12);

            for(var8 = 0; var8 < var4; ++var8) {
               var13 = var6[var8];
               int var10 = var5[var8];
               if (var10 >= 55296 && var10 <= 56319 && var8 < var4 - 1) {
                  char var11 = var2[var8 + 1];
                  if (var11 >= '\udc00' && var11 <= '\udfff') {
                     var10 = (var10 - '\ud800') * 1024 + var11 - '\udc00' + 65536;
                  }
               }

               var3[var13] = var12[var8];
               this.put(var10, var3[var13]);
               if (var10 >= 65536) {
                  ++var8;
                  var3[var13 + 1] = 65535;
               }
            }

         }
      }

      private class SparseBitShiftingTwoLayerArray {
         final int[][] cache;
         final int shift;
         final int secondLayerLength;

         public SparseBitShiftingTwoLayerArray(int var2, int var3) {
            this.shift = var3;
            this.cache = new int[1 << var3][];
            this.secondLayerLength = var2 >> var3;
         }

         public int get(int var1) {
            int var2 = var1 >> this.shift;
            int[] var3 = this.cache[var2];
            return var3 == null ? 0 : var3[var1 - var2 * (1 << this.shift)];
         }

         public void put(int var1, int var2) {
            int var3 = var1 >> this.shift;
            int[] var4 = this.cache[var3];
            if (var4 == null) {
               this.cache[var3] = var4 = new int[this.secondLayerLength];
            }

            var4[var1 - var3 * (1 << this.shift)] = var2;
         }
      }
   }
}
