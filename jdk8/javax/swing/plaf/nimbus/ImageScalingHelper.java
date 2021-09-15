package javax.swing.plaf.nimbus;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;

class ImageScalingHelper {
   private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
   static final int PAINT_TOP_LEFT = 1;
   static final int PAINT_TOP = 2;
   static final int PAINT_TOP_RIGHT = 4;
   static final int PAINT_LEFT = 8;
   static final int PAINT_CENTER = 16;
   static final int PAINT_RIGHT = 32;
   static final int PAINT_BOTTOM_RIGHT = 64;
   static final int PAINT_BOTTOM = 128;
   static final int PAINT_BOTTOM_LEFT = 256;
   static final int PAINT_ALL = 512;

   public static void paint(Graphics var0, int var1, int var2, int var3, int var4, Image var5, Insets var6, Insets var7, ImageScalingHelper.PaintType var8, int var9) {
      if (var5 != null && var5.getWidth((ImageObserver)null) > 0 && var5.getHeight((ImageObserver)null) > 0) {
         if (var6 == null) {
            var6 = EMPTY_INSETS;
         }

         if (var7 == null) {
            var7 = EMPTY_INSETS;
         }

         int var10 = var5.getWidth((ImageObserver)null);
         int var11 = var5.getHeight((ImageObserver)null);
         if (var8 == ImageScalingHelper.PaintType.CENTER) {
            var0.drawImage(var5, var1 + (var3 - var10) / 2, var2 + (var4 - var11) / 2, (ImageObserver)null);
         } else {
            int var13;
            int var14;
            int var16;
            int var17;
            int var18;
            int var19;
            if (var8 == ImageScalingHelper.PaintType.TILE) {
               byte var12 = 0;
               var13 = var2;

               for(var14 = var2 + var4; var13 < var14; var12 = 0) {
                  byte var15 = 0;
                  var16 = var1;

                  for(var17 = var1 + var3; var16 < var17; var15 = 0) {
                     var18 = Math.min(var17, var16 + var10 - var15);
                     var19 = Math.min(var14, var13 + var11 - var12);
                     var0.drawImage(var5, var16, var13, var18, var19, var15, var12, var15 + var18 - var16, var12 + var19 - var13, (ImageObserver)null);
                     var16 += var10 - var15;
                  }

                  var13 += var11 - var12;
               }
            } else {
               int var21 = var6.top;
               var13 = var6.left;
               var14 = var6.bottom;
               int var22 = var6.right;
               var16 = var7.top;
               var17 = var7.left;
               var18 = var7.bottom;
               var19 = var7.right;
               if (var21 + var14 > var11) {
                  var18 = var16 = var14 = var21 = Math.max(0, var11 / 2);
               }

               if (var13 + var22 > var10) {
                  var17 = var19 = var13 = var22 = Math.max(0, var10 / 2);
               }

               if (var16 + var18 > var4) {
                  var16 = var18 = Math.max(0, var4 / 2 - 1);
               }

               if (var17 + var19 > var3) {
                  var17 = var19 = Math.max(0, var3 / 2 - 1);
               }

               boolean var20 = var8 == ImageScalingHelper.PaintType.PAINT9_STRETCH;
               if ((var9 & 512) != 0) {
                  var9 = 511 & ~var9;
               }

               if ((var9 & 8) != 0) {
                  drawChunk(var5, var0, var20, var1, var2 + var16, var1 + var17, var2 + var4 - var18, 0, var21, var13, var11 - var14, false);
               }

               if ((var9 & 1) != 0) {
                  drawImage(var5, var0, var1, var2, var1 + var17, var2 + var16, 0, 0, var13, var21);
               }

               if ((var9 & 2) != 0) {
                  drawChunk(var5, var0, var20, var1 + var17, var2, var1 + var3 - var19, var2 + var16, var13, 0, var10 - var22, var21, true);
               }

               if ((var9 & 4) != 0) {
                  drawImage(var5, var0, var1 + var3 - var19, var2, var1 + var3, var2 + var16, var10 - var22, 0, var10, var21);
               }

               if ((var9 & 32) != 0) {
                  drawChunk(var5, var0, var20, var1 + var3 - var19, var2 + var16, var1 + var3, var2 + var4 - var18, var10 - var22, var21, var10, var11 - var14, false);
               }

               if ((var9 & 64) != 0) {
                  drawImage(var5, var0, var1 + var3 - var19, var2 + var4 - var18, var1 + var3, var2 + var4, var10 - var22, var11 - var14, var10, var11);
               }

               if ((var9 & 128) != 0) {
                  drawChunk(var5, var0, var20, var1 + var17, var2 + var4 - var18, var1 + var3 - var19, var2 + var4, var13, var11 - var14, var10 - var22, var11, true);
               }

               if ((var9 & 256) != 0) {
                  drawImage(var5, var0, var1, var2 + var4 - var18, var1 + var17, var2 + var4, 0, var11 - var14, var13, var11);
               }

               if ((var9 & 16) != 0) {
                  drawImage(var5, var0, var1 + var17, var2 + var16, var1 + var3 - var19, var2 + var4 - var18, var13, var21, var10 - var22, var11 - var14);
               }
            }
         }

      }
   }

   private static void drawChunk(Image var0, Graphics var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11) {
      if (var5 - var3 > 0 && var6 - var4 > 0 && var9 - var7 > 0 && var10 - var8 > 0) {
         if (var2) {
            var1.drawImage(var0, var3, var4, var5, var6, var7, var8, var9, var10, (ImageObserver)null);
         } else {
            int var12 = var9 - var7;
            int var13 = var10 - var8;
            int var14;
            int var15;
            if (var11) {
               var14 = var12;
               var15 = 0;
            } else {
               var14 = 0;
               var15 = var13;
            }

            while(var3 < var5 && var4 < var6) {
               int var16 = Math.min(var5, var3 + var12);
               int var17 = Math.min(var6, var4 + var13);
               var1.drawImage(var0, var3, var4, var16, var17, var7, var8, var7 + var16 - var3, var8 + var17 - var4, (ImageObserver)null);
               var3 += var14;
               var4 += var15;
            }
         }

      }
   }

   private static void drawImage(Image var0, Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (var4 - var2 > 0 && var5 - var3 > 0 && var8 - var6 > 0 && var9 - var7 > 0) {
         var1.drawImage(var0, var2, var3, var4, var5, var6, var7, var8, var9, (ImageObserver)null);
      }
   }

   static enum PaintType {
      CENTER,
      TILE,
      PAINT9_STRETCH,
      PAINT9_TILE;
   }
}
