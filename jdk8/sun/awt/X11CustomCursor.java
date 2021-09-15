package sun.awt;

import java.awt.Image;
import java.awt.Point;
import java.util.Arrays;

public abstract class X11CustomCursor extends CustomCursor {
   public X11CustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException {
      super(var1, var2, var3);
   }

   protected void createNativeCursor(Image var1, int[] var2, int var3, int var4, int var5, int var6) {
      int[] var7 = new int[var2.length];

      int var8;
      for(var8 = 0; var8 < var2.length; ++var8) {
         if ((var2[var8] & -16777216) == 0) {
            var7[var8] = -1;
         } else {
            var7[var8] = var2[var8] & 16777215;
         }
      }

      Arrays.sort(var7);
      var8 = 0;
      int var9 = 16777215;

      class CCount implements Comparable {
         int color;
         int count;

         public CCount(int var2, int var3) {
            this.color = var2;
            this.count = var3;
         }

         public int compareTo(Object var1) {
            return ((CCount)var1).count - this.count;
         }
      }

      CCount[] var10 = new CCount[var2.length];
      int var11 = 0;

      int var12;
      for(var12 = 0; var11 < var2.length; ++var11) {
         if (var7[var11] != -1) {
            var10[var12++] = new CCount(var7[var11], 1);
            break;
         }
      }

      int var13;
      for(var13 = var11 + 1; var13 < var2.length; ++var13) {
         if (var7[var13] != var10[var12 - 1].color) {
            var10[var12++] = new CCount(var7[var13], 1);
         } else {
            ++var10[var12 - 1].count;
         }
      }

      Arrays.sort((Object[])var10, 0, var12);
      if (var12 > 0) {
         var8 = var10[0].color;
      }

      var13 = var8 >> 16 & 255;
      int var14 = var8 >> 8 & 255;
      int var15 = var8 >> 0 & 255;
      int var16 = 0;
      int var17 = 0;
      int var18 = 0;

      int var19;
      int var20;
      int var21;
      int var22;
      for(var19 = 1; var19 < var12; ++var19) {
         var20 = var10[var19].color >> 16 & 255;
         var21 = var10[var19].color >> 8 & 255;
         var22 = var10[var19].color >> 0 & 255;
         var16 += var10[var19].count * var20;
         var17 += var10[var19].count * var21;
         var18 += var10[var19].count * var22;
      }

      var19 = var2.length - (var12 > 0 ? var10[0].count : 0);
      if (var19 > 0) {
         var16 = var16 / var19 - var13;
         var17 = var17 / var19 - var14;
         var18 = var18 / var19 - var15;
      }

      var16 = (var16 * var16 + var17 * var17 + var18 * var18) / 2;

      int var23;
      for(var20 = 1; var20 < var12; ++var20) {
         var21 = var10[var20].color >> 16 & 255;
         var22 = var10[var20].color >> 8 & 255;
         var23 = var10[var20].color >> 0 & 255;
         if ((var21 - var13) * (var21 - var13) + (var22 - var14) * (var22 - var14) + (var23 - var15) * (var23 - var15) >= var16) {
            var9 = var10[var20].color;
            break;
         }
      }

      var20 = var9 >> 16 & 255;
      var21 = var9 >> 8 & 255;
      var22 = var9 >> 0 & 255;
      var23 = (var3 + 7) / 8;
      int var24 = var23 * var4;
      byte[] var25 = new byte[var24];
      byte[] var26 = new byte[var24];

      for(int var27 = 0; var27 < var3; ++var27) {
         int var28 = 1 << var27 % 8;

         for(int var29 = 0; var29 < var4; ++var29) {
            int var30 = var29 * var3 + var27;
            int var31 = var29 * var23 + var27 / 8;
            if ((var2[var30] & -16777216) != 0) {
               var26[var31] = (byte)(var26[var31] | var28);
            }

            int var32 = var2[var30] >> 16 & 255;
            int var33 = var2[var30] >> 8 & 255;
            int var34 = var2[var30] >> 0 & 255;
            if ((var32 - var13) * (var32 - var13) + (var33 - var14) * (var33 - var14) + (var34 - var15) * (var34 - var15) <= (var32 - var20) * (var32 - var20) + (var33 - var21) * (var33 - var21) + (var34 - var22) * (var34 - var22)) {
               var25[var31] = (byte)(var25[var31] | var28);
            }
         }
      }

      this.createCursor(var25, var26, 8 * var23, var4, var8, var9, var5, var6);
   }

   protected abstract void createCursor(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8);
}
