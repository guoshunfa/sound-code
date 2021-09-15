package sun.java2d.pisces;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.pipe.AATileGenerator;

final class PiscesTileGenerator implements AATileGenerator {
   public static final int TILE_SIZE = 32;
   private static final Map<Integer, byte[]> alphaMapsCache = new ConcurrentHashMap();
   PiscesCache cache;
   int x;
   int y;
   final int maxalpha;
   private final int maxTileAlphaSum;
   byte[] alphaMap;

   public PiscesTileGenerator(Renderer var1, int var2) {
      this.cache = var1.getCache();
      this.x = this.cache.bboxX0;
      this.y = this.cache.bboxY0;
      this.alphaMap = getAlphaMap(var2);
      this.maxalpha = var2;
      this.maxTileAlphaSum = 1024 * var2;
   }

   private static byte[] buildAlphaMap(int var0) {
      byte[] var1 = new byte[var0 + 1];
      int var2 = var0 >> 2;

      for(int var3 = 0; var3 <= var0; ++var3) {
         var1[var3] = (byte)((var3 * 255 + var2) / var0);
      }

      return var1;
   }

   public static byte[] getAlphaMap(int var0) {
      if (!alphaMapsCache.containsKey(var0)) {
         alphaMapsCache.put(var0, buildAlphaMap(var0));
      }

      return (byte[])alphaMapsCache.get(var0);
   }

   public void getBbox(int[] var1) {
      var1[0] = this.cache.bboxX0;
      var1[1] = this.cache.bboxY0;
      var1[2] = this.cache.bboxX1;
      var1[3] = this.cache.bboxY1;
   }

   public int getTileWidth() {
      return 32;
   }

   public int getTileHeight() {
      return 32;
   }

   public int getTypicalAlpha() {
      int var1 = this.cache.alphaSumInTile(this.x, this.y);
      return var1 == 0 ? 0 : (var1 == this.maxTileAlphaSum ? 255 : 128);
   }

   public void nextTile() {
      if ((this.x += 32) >= this.cache.bboxX1) {
         this.x = this.cache.bboxX0;
         this.y += 32;
      }

   }

   public void getAlpha(byte[] var1, int var2, int var3) {
      int var4 = this.x;
      int var5 = var4 + 32;
      int var6 = this.y;
      int var7 = var6 + 32;
      if (var5 > this.cache.bboxX1) {
         var5 = this.cache.bboxX1;
      }

      if (var7 > this.cache.bboxY1) {
         var7 = this.cache.bboxY1;
      }

      var6 -= this.cache.bboxY0;
      var7 -= this.cache.bboxY0;
      int var8 = var2;

      for(int var9 = var6; var9 < var7; ++var9) {
         int[] var10 = this.cache.rowAARLE[var9];

         assert var10 != null;

         int var11 = this.cache.minTouched(var9);
         if (var11 > var5) {
            var11 = var5;
         }

         int var12;
         for(var12 = var4; var12 < var11; ++var12) {
            var1[var8++] = 0;
         }

         for(var12 = 2; var11 < var5 && var12 < var10[1]; var12 += 2) {
            byte var14 = 0;

            assert var10[1] > 2;

            byte var13;
            int var20;
            try {
               var13 = this.alphaMap[var10[var12]];
               var20 = var10[var12 + 1];

               assert var20 > 0;
            } catch (RuntimeException var19) {
               System.out.println("maxalpha = " + this.maxalpha);
               System.out.println("tile[" + var4 + ", " + var6 + " => " + var5 + ", " + var7 + "]");
               System.out.println("cx = " + var11 + ", cy = " + var9);
               System.out.println("idx = " + var8 + ", pos = " + var12);
               System.out.println("len = " + var14);
               System.out.print(this.cache.toString());
               var19.printStackTrace();
               throw var19;
            }

            int var15 = var11;
            var11 += var20;
            int var16 = var11;
            if (var15 < var4) {
               var15 = var4;
            }

            if (var11 > var5) {
               var16 = var5;
            }

            var20 = var16 - var15;

            while(true) {
               --var20;
               if (var20 < 0) {
                  break;
               }

               try {
                  var1[var8++] = var13;
               } catch (RuntimeException var18) {
                  System.out.println("maxalpha = " + this.maxalpha);
                  System.out.println("tile[" + var4 + ", " + var6 + " => " + var5 + ", " + var7 + "]");
                  System.out.println("cx = " + var11 + ", cy = " + var9);
                  System.out.println("idx = " + var8 + ", pos = " + var12);
                  System.out.println("rx0 = " + var15 + ", rx1 = " + var16);
                  System.out.println("len = " + var20);
                  System.out.print(this.cache.toString());
                  var18.printStackTrace();
                  throw var18;
               }
            }
         }

         if (var11 < var4) {
            var11 = var4;
         }

         while(var11 < var5) {
            var1[var8++] = 0;
            ++var11;
         }

         var8 += var3 - (var5 - var4);
      }

      this.nextTile();
   }

   static String hex(int var0, int var1) {
      String var2;
      for(var2 = Integer.toHexString(var0); var2.length() < var1; var2 = "0" + var2) {
      }

      return var2.substring(0, var1);
   }

   public void dispose() {
   }
}
