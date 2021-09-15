package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformHelper;

public class DrawImage implements DrawImagePipe {
   private static final double MAX_TX_ERROR = 1.0E-4D;

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5) {
      int var6 = var2.getWidth((ImageObserver)null);
      int var7 = var2.getHeight((ImageObserver)null);
      if (isSimpleTranslate(var1)) {
         return this.renderImageCopy(var1, var2, var5, var3 + var1.transX, var4 + var1.transY, 0, 0, var6, var7);
      } else {
         AffineTransform var8 = var1.transform;
         if ((var3 | var4) != 0) {
            var8 = new AffineTransform(var8);
            var8.translate((double)var3, (double)var4);
         }

         this.transformImage(var1, var2, var8, var1.interpolationType, 0, 0, var6, var7, var5);
         return true;
      }
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      if (isSimpleTranslate(var1)) {
         return this.renderImageCopy(var1, var2, var9, var3 + var1.transX, var4 + var1.transY, var5, var6, var7, var8);
      } else {
         this.scaleImage(var1, var2, var3, var4, var3 + var7, var4 + var8, var5, var6, var5 + var7, var6 + var8, var9);
         return true;
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7) {
      int var8 = var2.getWidth((ImageObserver)null);
      int var9 = var2.getHeight((ImageObserver)null);
      if (var5 > 0 && var6 > 0 && isSimpleTranslate(var1)) {
         double var10 = (double)(var3 + var1.transX);
         double var12 = (double)(var4 + var1.transY);
         double var14 = var10 + (double)var5;
         double var16 = var12 + (double)var6;
         if (this.renderImageScale(var1, var2, var7, var1.interpolationType, 0, 0, var8, var9, var10, var12, var14, var16)) {
            return true;
         }
      }

      AffineTransform var18 = var1.transform;
      if ((var3 | var4) != 0 || var5 != var8 || var6 != var9) {
         var18 = new AffineTransform(var18);
         var18.translate((double)var3, (double)var4);
         var18.scale((double)var5 / (double)var8, (double)var6 / (double)var9);
      }

      this.transformImage(var1, var2, var18, var1.interpolationType, 0, 0, var8, var9, var7);
      return true;
   }

   protected void transformImage(SunGraphics2D var1, Image var2, int var3, int var4, AffineTransform var5, int var6) {
      int var7 = var5.getType();
      int var8 = var2.getWidth((ImageObserver)null);
      int var9 = var2.getHeight((ImageObserver)null);
      boolean var10;
      if (var1.transformState > 2 || var7 != 0 && var7 != 1) {
         if (var1.transformState <= 3 && (var7 & 120) == 0) {
            double[] var17 = new double[]{0.0D, 0.0D, (double)var8, (double)var9};
            var5.transform((double[])var17, 0, (double[])var17, 0, 2);
            var17[0] += (double)var3;
            var17[1] += (double)var4;
            var17[2] += (double)var3;
            var17[3] += (double)var4;
            var1.transform.transform((double[])var17, 0, (double[])var17, 0, 2);
            if (this.tryCopyOrScale(var1, var2, 0, 0, var8, var9, (Color)null, var6, var17)) {
               return;
            }

            var10 = false;
         } else {
            var10 = true;
         }
      } else {
         double var11 = var5.getTranslateX();
         double var13 = var5.getTranslateY();
         var11 += var1.transform.getTranslateX();
         var13 += var1.transform.getTranslateY();
         int var15 = (int)Math.floor(var11 + 0.5D);
         int var16 = (int)Math.floor(var13 + 0.5D);
         if (var6 == 1 || closeToInteger(var15, var11) && closeToInteger(var16, var13)) {
            this.renderImageCopy(var1, var2, (Color)null, var3 + var15, var4 + var16, 0, 0, var8, var9);
            return;
         }

         var10 = false;
      }

      AffineTransform var18 = new AffineTransform(var1.transform);
      var18.translate((double)var3, (double)var4);
      var18.concatenate(var5);
      if (var10) {
         this.transformImage(var1, var2, var18, var6, 0, 0, var8, var9, (Color)null);
      } else {
         this.renderImageXform(var1, var2, var18, var6, 0, 0, var8, var9, (Color)null);
      }

   }

   protected void transformImage(SunGraphics2D var1, Image var2, AffineTransform var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      double[] var10 = new double[6];
      var10[2] = (double)(var7 - var5);
      var10[3] = var10[5] = (double)(var8 - var6);
      var3.transform((double[])var10, 0, (double[])var10, 0, 3);
      if (Math.abs(var10[0] - var10[4]) >= 1.0E-4D || Math.abs(var10[3] - var10[5]) >= 1.0E-4D || !this.tryCopyOrScale(var1, var2, var5, var6, var7, var8, var9, var4, var10)) {
         this.renderImageXform(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   protected boolean tryCopyOrScale(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7, int var8, double[] var9) {
      double var10 = var9[0];
      double var12 = var9[1];
      double var14 = var9[2];
      double var16 = var9[3];
      double var18 = var14 - var10;
      double var20 = var16 - var12;
      if (var10 >= -2.147483648E9D && var10 <= 2.147483647E9D && var12 >= -2.147483648E9D && var12 <= 2.147483647E9D && var14 >= -2.147483648E9D && var14 <= 2.147483647E9D && var16 >= -2.147483648E9D && var16 <= 2.147483647E9D) {
         if (closeToInteger(var5 - var3, var18) && closeToInteger(var6 - var4, var20)) {
            int var22 = (int)Math.floor(var10 + 0.5D);
            int var23 = (int)Math.floor(var12 + 0.5D);
            if (var8 == 1 || closeToInteger(var22, var10) && closeToInteger(var23, var12)) {
               this.renderImageCopy(var1, var2, var7, var22, var23, var3, var4, var5 - var3, var6 - var4);
               return true;
            }
         }

         return var18 > 0.0D && var20 > 0.0D && this.renderImageScale(var1, var2, var7, var8, var3, var4, var5, var6, var10, var12, var14, var16);
      } else {
         return false;
      }
   }

   BufferedImage makeBufferedImage(Image var1, Color var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = var6 - var4;
      int var9 = var7 - var5;
      BufferedImage var10 = new BufferedImage(var8, var9, var3);
      SunGraphics2D var11 = (SunGraphics2D)var10.createGraphics();
      var11.setComposite(AlphaComposite.Src);
      var10.setAccelerationPriority(0.0F);
      if (var2 != null) {
         var11.setColor(var2);
         var11.fillRect(0, 0, var8, var9);
         var11.setComposite(AlphaComposite.SrcOver);
      }

      var11.copyImage(var1, 0, 0, var4, var5, var8, var9, (Color)null, (ImageObserver)null);
      var11.dispose();
      return var10;
   }

   protected void renderImageXform(SunGraphics2D var1, Image var2, AffineTransform var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      AffineTransform var10;
      try {
         var10 = var3.createInverse();
      } catch (NoninvertibleTransformException var39) {
         return;
      }

      double[] var11 = new double[8];
      var11[2] = var11[6] = (double)(var7 - var5);
      var11[5] = var11[7] = (double)(var8 - var6);
      var3.transform((double[])var11, 0, (double[])var11, 0, 4);
      double var16;
      double var12 = var16 = var11[0];
      double var18;
      double var14 = var18 = var11[1];

      for(int var20 = 2; var20 < var11.length; var20 += 2) {
         double var21 = var11[var20];
         if (var12 > var21) {
            var12 = var21;
         } else if (var16 < var21) {
            var16 = var21;
         }

         var21 = var11[var20 + 1];
         if (var14 > var21) {
            var14 = var21;
         } else if (var18 < var21) {
            var18 = var21;
         }
      }

      Region var41 = var1.getCompClip();
      int var42 = Math.max((int)Math.floor(var12), var41.lox);
      int var22 = Math.max((int)Math.floor(var14), var41.loy);
      int var23 = Math.min((int)Math.ceil(var16), var41.hix);
      int var24 = Math.min((int)Math.ceil(var18), var41.hiy);
      if (var23 > var42 && var24 > var22) {
         SurfaceData var25 = var1.surfaceData;
         SurfaceData var26 = var25.getSourceSurfaceData((Image)var2, 4, var1.imageComp, var9);
         if (var26 == null) {
            var2 = this.getBufferedImage((Image)var2);
            var26 = var25.getSourceSurfaceData((Image)var2, 4, var1.imageComp, var9);
            if (var26 == null) {
               return;
            }
         }

         if (isBgOperation(var26, var9)) {
            var2 = this.makeBufferedImage((Image)var2, var9, 1, var5, var6, var7, var8);
            var7 -= var5;
            var8 -= var6;
            var6 = 0;
            var5 = 0;
            var26 = var25.getSourceSurfaceData((Image)var2, 4, var1.imageComp, var9);
         }

         SurfaceType var27 = var26.getSurfaceType();
         TransformHelper var28 = TransformHelper.getFromCache(var27);
         if (var28 == null) {
            int var29 = var26.getTransparency() == 1 ? 1 : 2;
            BufferedImage var40 = this.makeBufferedImage((Image)var2, (Color)null, var29, var5, var6, var7, var8);
            var7 -= var5;
            var8 -= var6;
            var6 = 0;
            var5 = 0;
            var26 = var25.getSourceSurfaceData(var40, 4, var1.imageComp, (Color)null);
            var27 = var26.getSurfaceType();
            var28 = TransformHelper.getFromCache(var27);
         }

         SurfaceType var43 = var25.getSurfaceType();
         if (var1.compositeState <= 1) {
            MaskBlit var30 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, var1.imageComp, var43);
            if (var30.getNativePrim() != 0L) {
               var28.Transform(var30, var26, var25, var1.composite, var41, var10, var4, var5, var6, var7, var8, var42, var22, var23, var24, (int[])null, 0, 0);
               return;
            }
         }

         int var44 = var23 - var42;
         int var31 = var24 - var22;
         BufferedImage var32 = new BufferedImage(var44, var31, 3);
         SurfaceData var33 = SurfaceData.getPrimarySurfaceData(var32);
         SurfaceType var34 = var33.getSurfaceType();
         MaskBlit var35 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, var34);
         int[] var36 = new int[var31 * 2 + 2];
         var28.Transform(var35, var26, var33, AlphaComposite.Src, (Region)null, var10, var4, var5, var6, var7, var8, 0, 0, var44, var31, var36, var42, var22);
         Region var37 = Region.getInstance(var42, var22, var23, var24, var36);
         var41 = var41.getIntersection(var37);
         Blit var38 = Blit.getFromCache(var34, var1.imageComp, var43);
         var38.Blit(var33, var25, var1.composite, var41, 0, 0, var42, var22, var44, var31);
      }
   }

   protected boolean renderImageCopy(SunGraphics2D var1, Image var2, Color var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      Region var10 = var1.getCompClip();
      SurfaceData var11 = var1.surfaceData;
      int var12 = 0;

      while(true) {
         SurfaceData var13 = var11.getSourceSurfaceData(var2, 0, var1.imageComp, var3);
         if (var13 == null) {
            return false;
         }

         try {
            SurfaceType var14 = var13.getSurfaceType();
            SurfaceType var15 = var11.getSurfaceType();
            this.blitSurfaceData(var1, var10, var13, var11, var14, var15, var6, var7, var4, var5, var8, var9, var3);
            return true;
         } catch (NullPointerException var16) {
            if (!SurfaceData.isNull(var11) && !SurfaceData.isNull(var13)) {
               throw var16;
            }

            return false;
         } catch (InvalidPipeException var17) {
            ++var12;
            var10 = var1.getCompClip();
            var11 = var1.surfaceData;
            if (SurfaceData.isNull(var11) || SurfaceData.isNull(var13) || var12 > 1) {
               return false;
            }
         }
      }
   }

   protected boolean renderImageScale(SunGraphics2D var1, Image var2, Color var3, int var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      if (var4 != 1) {
         return false;
      } else {
         Region var17 = var1.getCompClip();
         SurfaceData var18 = var1.surfaceData;
         int var19 = 0;

         while(true) {
            SurfaceData var20 = var18.getSourceSurfaceData(var2, 3, var1.imageComp, var3);
            if (var20 != null && !isBgOperation(var20, var3)) {
               try {
                  SurfaceType var21 = var20.getSurfaceType();
                  SurfaceType var22 = var18.getSurfaceType();
                  return this.scaleSurfaceData(var1, var17, var20, var18, var21, var22, var5, var6, var7, var8, var9, var11, var13, var15);
               } catch (NullPointerException var23) {
                  if (!SurfaceData.isNull(var18)) {
                     throw var23;
                  }

                  return false;
               } catch (InvalidPipeException var24) {
                  ++var19;
                  var17 = var1.getCompClip();
                  var18 = var1.surfaceData;
                  if (!SurfaceData.isNull(var18) && !SurfaceData.isNull(var20) && var19 <= 1) {
                     continue;
                  }

                  return false;
               }
            }

            return false;
         }
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11) {
      boolean var20 = false;
      boolean var21 = false;
      boolean var22 = false;
      boolean var23 = false;
      int var12;
      int var16;
      if (var9 > var7) {
         var12 = var9 - var7;
         var16 = var7;
      } else {
         var20 = true;
         var12 = var7 - var9;
         var16 = var9;
      }

      int var13;
      int var17;
      if (var10 > var8) {
         var13 = var10 - var8;
         var17 = var8;
      } else {
         var21 = true;
         var13 = var8 - var10;
         var17 = var10;
      }

      int var14;
      int var18;
      if (var5 > var3) {
         var14 = var5 - var3;
         var18 = var3;
      } else {
         var14 = var3 - var5;
         var22 = true;
         var18 = var5;
      }

      int var15;
      int var19;
      if (var6 > var4) {
         var15 = var6 - var4;
         var19 = var4;
      } else {
         var15 = var4 - var6;
         var23 = true;
         var19 = var6;
      }

      if (var12 > 0 && var13 > 0) {
         if (var20 == var22 && var21 == var23 && isSimpleTranslate(var1)) {
            double var24 = (double)(var18 + var1.transX);
            double var26 = (double)(var19 + var1.transY);
            double var28 = var24 + (double)var14;
            double var30 = var26 + (double)var15;
            if (this.renderImageScale(var1, var2, var11, var1.interpolationType, var16, var17, var16 + var12, var17 + var13, var24, var26, var28, var30)) {
               return true;
            }
         }

         AffineTransform var32 = new AffineTransform(var1.transform);
         var32.translate((double)var3, (double)var4);
         double var25 = (double)(var5 - var3) / (double)(var9 - var7);
         double var27 = (double)(var6 - var4) / (double)(var10 - var8);
         var32.scale(var25, var27);
         var32.translate((double)(var16 - var7), (double)(var17 - var8));
         int var29 = SurfaceManager.getImageScale(var2);
         int var33 = var2.getWidth((ImageObserver)null) * var29;
         int var31 = var2.getHeight((ImageObserver)null) * var29;
         var12 += var16;
         var13 += var17;
         if (var12 > var33) {
            var12 = var33;
         }

         if (var13 > var31) {
            var13 = var31;
         }

         if (var16 < 0) {
            var32.translate((double)(-var16), 0.0D);
            var16 = 0;
         }

         if (var17 < 0) {
            var32.translate(0.0D, (double)(-var17));
            var17 = 0;
         }

         if (var16 < var12 && var17 < var13) {
            this.transformImage(var1, var2, var32, var1.interpolationType, var16, var17, var12, var13, var11);
            return true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static boolean closeToInteger(int var0, double var1) {
      return Math.abs(var1 - (double)var0) < 1.0E-4D;
   }

   public static boolean isSimpleTranslate(SunGraphics2D var0) {
      int var1 = var0.transformState;
      if (var1 <= 1) {
         return true;
      } else if (var1 >= 3) {
         return false;
      } else {
         return var0.interpolationType == 1;
      }
   }

   protected static boolean isBgOperation(SurfaceData var0, Color var1) {
      return var0 == null || var1 != null && var0.getTransparency() != 1;
   }

   protected BufferedImage getBufferedImage(Image var1) {
      return var1 instanceof BufferedImage ? (BufferedImage)var1 : ((VolatileImage)var1).getSnapshot();
   }

   private ColorModel getTransformColorModel(SunGraphics2D var1, BufferedImage var2, AffineTransform var3) {
      ColorModel var4 = var2.getColorModel();
      Object var5 = var4;
      if (var3.isIdentity()) {
         return var4;
      } else {
         int var6 = var3.getType();
         boolean var7 = (var6 & 56) != 0;
         if (!var7 && var6 != 1 && var6 != 0) {
            double[] var8 = new double[4];
            var3.getMatrix(var8);
            var7 = var8[0] != (double)((int)var8[0]) || var8[3] != (double)((int)var8[3]);
         }

         if (var1.renderHint != 2) {
            if (var4 instanceof IndexColorModel) {
               WritableRaster var13 = var2.getRaster();
               IndexColorModel var9 = (IndexColorModel)var4;
               if (var7 && var4.getTransparency() == 1) {
                  if (var13 instanceof BytePackedRaster) {
                     var5 = ColorModel.getRGBdefault();
                  } else {
                     double[] var10 = new double[6];
                     var3.getMatrix(var10);
                     if (var10[1] != 0.0D || var10[2] != 0.0D || var10[4] != 0.0D || var10[5] != 0.0D) {
                        int var11 = var9.getMapSize();
                        if (var11 < 256) {
                           int[] var12 = new int[var11 + 1];
                           var9.getRGBs(var12);
                           var12[var11] = 0;
                           var5 = new IndexColorModel(var9.getPixelSize(), var11 + 1, var12, 0, true, var11, 0);
                        } else {
                           var5 = ColorModel.getRGBdefault();
                        }
                     }
                  }
               }
            } else if (var7 && var4.getTransparency() == 1) {
               var5 = ColorModel.getRGBdefault();
            }
         } else if (var4 instanceof IndexColorModel || var7 && var4.getTransparency() == 1) {
            var5 = ColorModel.getRGBdefault();
         }

         return (ColorModel)var5;
      }
   }

   protected void blitSurfaceData(SunGraphics2D var1, Region var2, SurfaceData var3, SurfaceData var4, SurfaceType var5, SurfaceType var6, int var7, int var8, int var9, int var10, int var11, int var12, Color var13) {
      if (var11 > 0 && var12 > 0) {
         CompositeType var14 = var1.imageComp;
         if (CompositeType.SrcOverNoEa.equals(var14) && (var3.getTransparency() == 1 || var13 != null && var13.getTransparency() == 1)) {
            var14 = CompositeType.SrcNoEa;
         }

         if (!isBgOperation(var3, var13)) {
            Blit var15 = Blit.getFromCache(var5, var14, var6);
            var15.Blit(var3, var4, var1.composite, var2, var7, var8, var9, var10, var11, var12);
         } else {
            BlitBg var16 = BlitBg.getFromCache(var5, var14, var6);
            var16.BlitBg(var3, var4, var1.composite, var2, var13.getRGB(), var7, var8, var9, var10, var11, var12);
         }

      }
   }

   protected boolean scaleSurfaceData(SunGraphics2D var1, Region var2, SurfaceData var3, SurfaceData var4, SurfaceType var5, SurfaceType var6, int var7, int var8, int var9, int var10, double var11, double var13, double var15, double var17) {
      CompositeType var19 = var1.imageComp;
      if (CompositeType.SrcOverNoEa.equals(var19) && var3.getTransparency() == 1) {
         var19 = CompositeType.SrcNoEa;
      }

      ScaledBlit var20 = ScaledBlit.getFromCache(var5, var19, var6);
      if (var20 != null) {
         var20.Scale(var3, var4, var1.composite, var2, var7, var8, var9, var10, var11, var13, var15, var17);
         return true;
      } else {
         return false;
      }
   }

   protected static boolean imageReady(ToolkitImage var0, ImageObserver var1) {
      if (var0.hasError()) {
         if (var1 != null) {
            var1.imageUpdate(var0, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5, ImageObserver var6) {
      if (!(var2 instanceof ToolkitImage)) {
         return this.copyImage(var1, var2, var3, var4, var5);
      } else {
         ToolkitImage var7 = (ToolkitImage)var2;
         if (!imageReady(var7, var6)) {
            return false;
         } else {
            ImageRepresentation var8 = var7.getImageRep();
            return var8.drawToBufImage(var1, var7, var3, var4, var5, var6);
         }
      }
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9, ImageObserver var10) {
      if (!(var2 instanceof ToolkitImage)) {
         return this.copyImage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         ToolkitImage var11 = (ToolkitImage)var2;
         if (!imageReady(var11, var10)) {
            return false;
         } else {
            ImageRepresentation var12 = var11.getImageRep();
            return var12.drawToBufImage(var1, var11, var3, var4, var3 + var7, var4 + var8, var5, var6, var5 + var7, var6 + var8, var9, var10);
         }
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7, ImageObserver var8) {
      if (!(var2 instanceof ToolkitImage)) {
         return this.scaleImage(var1, var2, var3, var4, var5, var6, var7);
      } else {
         ToolkitImage var9 = (ToolkitImage)var2;
         if (!imageReady(var9, var8)) {
            return false;
         } else {
            ImageRepresentation var10 = var9.getImageRep();
            return var10.drawToBufImage(var1, var9, var3, var4, var5, var6, var7, var8);
         }
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11, ImageObserver var12) {
      if (!(var2 instanceof ToolkitImage)) {
         return this.scaleImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         ToolkitImage var13 = (ToolkitImage)var2;
         if (!imageReady(var13, var12)) {
            return false;
         } else {
            ImageRepresentation var14 = var13.getImageRep();
            return var14.drawToBufImage(var1, var13, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
         }
      }
   }

   public boolean transformImage(SunGraphics2D var1, Image var2, AffineTransform var3, ImageObserver var4) {
      if (!(var2 instanceof ToolkitImage)) {
         this.transformImage(var1, var2, 0, 0, var3, var1.interpolationType);
         return true;
      } else {
         ToolkitImage var5 = (ToolkitImage)var2;
         if (!imageReady(var5, var4)) {
            return false;
         } else {
            ImageRepresentation var6 = var5.getImageRep();
            return var6.drawToBufImage(var1, var5, var3, var4);
         }
      }
   }

   public void transformImage(SunGraphics2D var1, BufferedImage var2, BufferedImageOp var3, int var4, int var5) {
      if (var3 != null) {
         if (var3 instanceof AffineTransformOp) {
            AffineTransformOp var6 = (AffineTransformOp)var3;
            this.transformImage(var1, var2, var4, var5, var6.getTransform(), var6.getInterpolationType());
            return;
         }

         var2 = var3.filter(var2, (BufferedImage)null);
      }

      this.copyImage(var1, var2, var4, var5, (Color)null);
   }
}
