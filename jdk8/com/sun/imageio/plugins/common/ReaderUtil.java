package com.sun.imageio.plugins.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class ReaderUtil {
   private static void computeUpdatedPixels(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9, int var10) {
      boolean var11 = false;
      int var12 = -1;
      int var13 = -1;
      int var14 = -1;

      for(int var15 = 0; var15 < var7; ++var15) {
         int var16 = var6 + var15 * var8;
         if (var16 >= var0 && (var16 - var0) % var5 == 0) {
            if (var16 >= var0 + var1) {
               break;
            }

            int var17 = var2 + (var16 - var0) / var5;
            if (var17 >= var3) {
               if (var17 > var4) {
                  break;
               }

               if (!var11) {
                  var12 = var17;
                  var11 = true;
               } else if (var13 == -1) {
                  var13 = var17;
               }

               var14 = var17;
            }
         }
      }

      var9[var10] = var12;
      if (!var11) {
         var9[var10 + 2] = 0;
      } else {
         var9[var10 + 2] = var14 - var12 + 1;
      }

      var9[var10 + 4] = Math.max(var13 - var12, 1);
   }

   public static int[] computeUpdatedPixels(Rectangle var0, Point var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      int[] var14 = new int[6];
      computeUpdatedPixels(var0.x, var0.width, var1.x, var2, var4, var6, var8, var10, var12, var14, 0);
      computeUpdatedPixels(var0.y, var0.height, var1.y, var3, var5, var7, var9, var11, var13, var14, 1);
      return var14;
   }

   public static int readMultiByteInteger(ImageInputStream var0) throws IOException {
      byte var1 = var0.readByte();

      int var2;
      for(var2 = var1 & 127; (var1 & 128) == 128; var2 |= var1 & 127) {
         var2 <<= 7;
         var1 = var0.readByte();
      }

      return var2;
   }
}
