package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import sun.swing.CachedPainter;

public class Paint9Painter extends CachedPainter {
   private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
   public static final int PAINT_TOP_LEFT = 1;
   public static final int PAINT_TOP = 2;
   public static final int PAINT_TOP_RIGHT = 4;
   public static final int PAINT_LEFT = 8;
   public static final int PAINT_CENTER = 16;
   public static final int PAINT_RIGHT = 32;
   public static final int PAINT_BOTTOM_RIGHT = 64;
   public static final int PAINT_BOTTOM = 128;
   public static final int PAINT_BOTTOM_LEFT = 256;
   public static final int PAINT_ALL = 512;

   public static boolean validImage(Image var0) {
      return var0 != null && var0.getWidth((ImageObserver)null) > 0 && var0.getHeight((ImageObserver)null) > 0;
   }

   public Paint9Painter(int var1) {
      super(var1);
   }

   public void paint(Component var1, Graphics var2, int var3, int var4, int var5, int var6, Image var7, Insets var8, Insets var9, Paint9Painter.PaintType var10, int var11) {
      if (var7 != null) {
         super.paint(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   protected void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6) {
      int var7 = 0;

      while(var7 < var6.length) {
         Image var8 = (Image)var6[var7++];
         Insets var9 = (Insets)var6[var7++];
         Insets var10 = (Insets)var6[var7++];
         Paint9Painter.PaintType var11 = (Paint9Painter.PaintType)var6[var7++];
         int var12 = (Integer)var6[var7++];
         this.paint9(var3, 0, 0, var4, var5, var8, var9, var10, var11, var12);
      }

   }

   protected void paint9(Graphics var1, int var2, int var3, int var4, int var5, Image var6, Insets var7, Insets var8, Paint9Painter.PaintType var9, int var10) {
      if (validImage(var6)) {
         if (var7 == null) {
            var7 = EMPTY_INSETS;
         }

         if (var8 == null) {
            var8 = EMPTY_INSETS;
         }

         int var11 = var6.getWidth((ImageObserver)null);
         int var12 = var6.getHeight((ImageObserver)null);
         if (var9 == Paint9Painter.PaintType.CENTER) {
            var1.drawImage(var6, var2 + (var4 - var11) / 2, var3 + (var5 - var12) / 2, (ImageObserver)null);
         } else {
            int var14;
            int var15;
            int var17;
            int var18;
            int var19;
            int var20;
            if (var9 == Paint9Painter.PaintType.TILE) {
               byte var13 = 0;
               var14 = var3;

               for(var15 = var3 + var5; var14 < var15; var13 = 0) {
                  byte var16 = 0;
                  var17 = var2;

                  for(var18 = var2 + var4; var17 < var18; var16 = 0) {
                     var19 = Math.min(var18, var17 + var11 - var16);
                     var20 = Math.min(var15, var14 + var12 - var13);
                     var1.drawImage(var6, var17, var14, var19, var20, var16, var13, var16 + var19 - var17, var13 + var20 - var14, (ImageObserver)null);
                     var17 += var11 - var16;
                  }

                  var14 += var12 - var13;
               }
            } else {
               int var22 = var7.top;
               var14 = var7.left;
               var15 = var7.bottom;
               int var23 = var7.right;
               var17 = var8.top;
               var18 = var8.left;
               var19 = var8.bottom;
               var20 = var8.right;
               if (var22 + var15 > var12) {
                  var19 = var17 = var15 = var22 = Math.max(0, var12 / 2);
               }

               if (var14 + var23 > var11) {
                  var18 = var20 = var14 = var23 = Math.max(0, var11 / 2);
               }

               if (var17 + var19 > var5) {
                  var17 = var19 = Math.max(0, var5 / 2 - 1);
               }

               if (var18 + var20 > var4) {
                  var18 = var20 = Math.max(0, var4 / 2 - 1);
               }

               boolean var21 = var9 == Paint9Painter.PaintType.PAINT9_STRETCH;
               if ((var10 & 512) != 0) {
                  var10 = 511 & ~var10;
               }

               if ((var10 & 8) != 0) {
                  this.drawChunk(var6, var1, var21, var2, var3 + var17, var2 + var18, var3 + var5 - var19, 0, var22, var14, var12 - var15, false);
               }

               if ((var10 & 1) != 0) {
                  this.drawImage(var6, var1, var2, var3, var2 + var18, var3 + var17, 0, 0, var14, var22);
               }

               if ((var10 & 2) != 0) {
                  this.drawChunk(var6, var1, var21, var2 + var18, var3, var2 + var4 - var20, var3 + var17, var14, 0, var11 - var23, var22, true);
               }

               if ((var10 & 4) != 0) {
                  this.drawImage(var6, var1, var2 + var4 - var20, var3, var2 + var4, var3 + var17, var11 - var23, 0, var11, var22);
               }

               if ((var10 & 32) != 0) {
                  this.drawChunk(var6, var1, var21, var2 + var4 - var20, var3 + var17, var2 + var4, var3 + var5 - var19, var11 - var23, var22, var11, var12 - var15, false);
               }

               if ((var10 & 64) != 0) {
                  this.drawImage(var6, var1, var2 + var4 - var20, var3 + var5 - var19, var2 + var4, var3 + var5, var11 - var23, var12 - var15, var11, var12);
               }

               if ((var10 & 128) != 0) {
                  this.drawChunk(var6, var1, var21, var2 + var18, var3 + var5 - var19, var2 + var4 - var20, var3 + var5, var14, var12 - var15, var11 - var23, var12, true);
               }

               if ((var10 & 256) != 0) {
                  this.drawImage(var6, var1, var2, var3 + var5 - var19, var2 + var18, var3 + var5, 0, var12 - var15, var14, var12);
               }

               if ((var10 & 16) != 0) {
                  this.drawImage(var6, var1, var2 + var18, var3 + var17, var2 + var4 - var20, var3 + var5 - var19, var14, var22, var11 - var23, var12 - var15);
               }
            }
         }

      }
   }

   private void drawImage(Image var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      if (var5 - var3 > 0 && var6 - var4 > 0 && var9 - var7 > 0 && var10 - var8 > 0) {
         var2.drawImage(var1, var3, var4, var5, var6, var7, var8, var9, var10, (ImageObserver)null);
      }
   }

   private void drawChunk(Image var1, Graphics var2, boolean var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, boolean var12) {
      if (var6 - var4 > 0 && var7 - var5 > 0 && var10 - var8 > 0 && var11 - var9 > 0) {
         if (var3) {
            var2.drawImage(var1, var4, var5, var6, var7, var8, var9, var10, var11, (ImageObserver)null);
         } else {
            int var13 = var10 - var8;
            int var14 = var11 - var9;
            int var15;
            int var16;
            if (var12) {
               var15 = var13;
               var16 = 0;
            } else {
               var15 = 0;
               var16 = var14;
            }

            while(var4 < var6 && var5 < var7) {
               int var17 = Math.min(var6, var4 + var13);
               int var18 = Math.min(var7, var5 + var14);
               var2.drawImage(var1, var4, var5, var17, var18, var8, var9, var8 + var17 - var4, var9 + var18 - var5, (ImageObserver)null);
               var4 += var15;
               var5 += var16;
            }
         }

      }
   }

   protected Image createImage(Component var1, int var2, int var3, GraphicsConfiguration var4, Object[] var5) {
      return var4 == null ? new BufferedImage(var2, var3, 2) : var4.createCompatibleImage(var2, var3, 3);
   }

   public static enum PaintType {
      CENTER,
      TILE,
      PAINT9_STRETCH,
      PAINT9_TILE;
   }
}
