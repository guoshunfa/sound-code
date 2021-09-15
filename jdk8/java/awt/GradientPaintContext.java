package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import sun.awt.image.IntegerComponentRaster;

class GradientPaintContext implements PaintContext {
   static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
   static ColorModel xbgrmodel = new DirectColorModel(24, 255, 65280, 16711680);
   static ColorModel cachedModel;
   static WeakReference<Raster> cached;
   double x1;
   double y1;
   double dx;
   double dy;
   boolean cyclic;
   int[] interp;
   Raster saved;
   ColorModel model;

   static synchronized Raster getCachedRaster(ColorModel var0, int var1, int var2) {
      if (var0 == cachedModel && cached != null) {
         Raster var3 = (Raster)cached.get();
         if (var3 != null && var3.getWidth() >= var1 && var3.getHeight() >= var2) {
            cached = null;
            return var3;
         }
      }

      return var0.createCompatibleWritableRaster(var1, var2);
   }

   static synchronized void putCachedRaster(ColorModel var0, Raster var1) {
      if (cached != null) {
         Raster var2 = (Raster)cached.get();
         if (var2 != null) {
            int var3 = var2.getWidth();
            int var4 = var2.getHeight();
            int var5 = var1.getWidth();
            int var6 = var1.getHeight();
            if (var3 >= var5 && var4 >= var6) {
               return;
            }

            if (var3 * var4 >= var5 * var6) {
               return;
            }
         }
      }

      cachedModel = var0;
      cached = new WeakReference(var1);
   }

   public GradientPaintContext(ColorModel var1, Point2D var2, Point2D var3, AffineTransform var4, Color var5, Color var6, boolean var7) {
      Point2D.Double var8 = new Point2D.Double(1.0D, 0.0D);
      Point2D.Double var9 = new Point2D.Double(0.0D, 1.0D);

      try {
         AffineTransform var10 = var4.createInverse();
         var10.deltaTransform(var8, var8);
         var10.deltaTransform(var9, var9);
      } catch (NoninvertibleTransformException var30) {
         var8.setLocation(0.0D, 0.0D);
         var9.setLocation(0.0D, 0.0D);
      }

      double var31 = var3.getX() - var2.getX();
      double var12 = var3.getY() - var2.getY();
      double var14 = var31 * var31 + var12 * var12;
      if (var14 <= Double.MIN_VALUE) {
         this.dx = 0.0D;
         this.dy = 0.0D;
      } else {
         this.dx = (var8.getX() * var31 + var8.getY() * var12) / var14;
         this.dy = (var9.getX() * var31 + var9.getY() * var12) / var14;
         if (var7) {
            this.dx %= 1.0D;
            this.dy %= 1.0D;
         } else if (this.dx < 0.0D) {
            var2 = var3;
            Color var17 = var5;
            var5 = var6;
            var6 = var17;
            this.dx = -this.dx;
            this.dy = -this.dy;
         }
      }

      Point2D var16 = var4.transform(var2, (Point2D)null);
      this.x1 = var16.getX();
      this.y1 = var16.getY();
      this.cyclic = var7;
      int var32 = var5.getRGB();
      int var18 = var6.getRGB();
      int var19 = var32 >> 24 & 255;
      int var20 = var32 >> 16 & 255;
      int var21 = var32 >> 8 & 255;
      int var22 = var32 & 255;
      int var23 = (var18 >> 24 & 255) - var19;
      int var24 = (var18 >> 16 & 255) - var20;
      int var25 = (var18 >> 8 & 255) - var21;
      int var26 = (var18 & 255) - var22;
      if (var19 == 255 && var23 == 0) {
         this.model = xrgbmodel;
         if (var1 instanceof DirectColorModel) {
            DirectColorModel var27 = (DirectColorModel)var1;
            int var28 = var27.getAlphaMask();
            if ((var28 == 0 || var28 == 255) && var27.getRedMask() == 255 && var27.getGreenMask() == 65280 && var27.getBlueMask() == 16711680) {
               this.model = xbgrmodel;
               var28 = var20;
               var20 = var22;
               var22 = var28;
               var28 = var24;
               var24 = var26;
               var26 = var28;
            }
         }
      } else {
         this.model = ColorModel.getRGBdefault();
      }

      this.interp = new int[var7 ? 513 : 257];

      for(int var33 = 0; var33 <= 256; ++var33) {
         float var34 = (float)var33 / 256.0F;
         int var29 = (int)((float)var19 + (float)var23 * var34) << 24 | (int)((float)var20 + (float)var24 * var34) << 16 | (int)((float)var21 + (float)var25 * var34) << 8 | (int)((float)var22 + (float)var26 * var34);
         this.interp[var33] = var29;
         if (var7) {
            this.interp[512 - var33] = var29;
         }
      }

   }

   public void dispose() {
      if (this.saved != null) {
         putCachedRaster(this.model, this.saved);
         this.saved = null;
      }

   }

   public ColorModel getColorModel() {
      return this.model;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      double var5 = ((double)var1 - this.x1) * this.dx + ((double)var2 - this.y1) * this.dy;
      Raster var7 = this.saved;
      if (var7 == null || var7.getWidth() < var3 || var7.getHeight() < var4) {
         var7 = getCachedRaster(this.model, var3, var4);
         this.saved = var7;
      }

      IntegerComponentRaster var8 = (IntegerComponentRaster)var7;
      int var9 = var8.getDataOffset(0);
      int var10 = var8.getScanlineStride() - var3;
      int[] var11 = var8.getDataStorage();
      if (this.cyclic) {
         this.cycleFillRaster(var11, var9, var10, var3, var4, var5, this.dx, this.dy);
      } else {
         this.clipFillRaster(var11, var9, var10, var3, var4, var5, this.dx, this.dy);
      }

      var8.markDirty();
      return var7;
   }

   void cycleFillRaster(int[] var1, int var2, int var3, int var4, int var5, double var6, double var8, double var10) {
      var6 %= 2.0D;
      int var12 = (int)(var6 * 1.073741824E9D) << 1;
      int var13 = (int)(-var8 * -2.147483648E9D);
      int var14 = (int)(-var10 * -2.147483648E9D);

      while(true) {
         --var5;
         if (var5 < 0) {
            return;
         }

         int var15 = var12;

         for(int var16 = var4; var16 > 0; --var16) {
            var1[var2++] = this.interp[var15 >>> 23];
            var15 += var13;
         }

         var2 += var3;
         var12 += var14;
      }
   }

   void clipFillRaster(int[] var1, int var2, int var3, int var4, int var5, double var6, double var8, double var10) {
      while(true) {
         --var5;
         if (var5 < 0) {
            return;
         }

         double var12 = var6;
         int var14 = var4;
         int var15;
         if (var6 <= 0.0D) {
            var15 = this.interp[0];

            do {
               var1[var2++] = var15;
               var12 += var8;
               --var14;
            } while(var14 > 0 && var12 <= 0.0D);
         }

         while(var12 < 1.0D) {
            --var14;
            if (var14 < 0) {
               break;
            }

            var1[var2++] = this.interp[(int)(var12 * 256.0D)];
            var12 += var8;
         }

         if (var14 > 0) {
            var15 = this.interp[256];

            do {
               var1[var2++] = var15;
               --var14;
            } while(var14 > 0);
         }

         var2 += var3;
         var6 += var10;
      }
   }
}
