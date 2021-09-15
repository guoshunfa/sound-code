package java.awt.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import sun.awt.image.ImagingLib;

public class AffineTransformOp implements BufferedImageOp, RasterOp {
   private AffineTransform xform;
   RenderingHints hints;
   public static final int TYPE_NEAREST_NEIGHBOR = 1;
   public static final int TYPE_BILINEAR = 2;
   public static final int TYPE_BICUBIC = 3;
   int interpolationType = 1;

   public AffineTransformOp(AffineTransform var1, RenderingHints var2) {
      this.validateTransform(var1);
      this.xform = (AffineTransform)var1.clone();
      this.hints = var2;
      if (var2 != null) {
         Object var3 = var2.get(RenderingHints.KEY_INTERPOLATION);
         if (var3 == null) {
            var3 = var2.get(RenderingHints.KEY_RENDERING);
            if (var3 == RenderingHints.VALUE_RENDER_SPEED) {
               this.interpolationType = 1;
            } else if (var3 == RenderingHints.VALUE_RENDER_QUALITY) {
               this.interpolationType = 2;
            }
         } else if (var3 == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
            this.interpolationType = 1;
         } else if (var3 == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
            this.interpolationType = 2;
         } else if (var3 == RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
            this.interpolationType = 3;
         }
      } else {
         this.interpolationType = 1;
      }

   }

   public AffineTransformOp(AffineTransform var1, int var2) {
      this.validateTransform(var1);
      this.xform = (AffineTransform)var1.clone();
      switch(var2) {
      case 1:
      case 2:
      case 3:
         this.interpolationType = var2;
         return;
      default:
         throw new IllegalArgumentException("Unknown interpolation type: " + var2);
      }
   }

   public final int getInterpolationType() {
      return this.interpolationType;
   }

   public final BufferedImage filter(BufferedImage var1, BufferedImage var2) {
      if (var1 == null) {
         throw new NullPointerException("src image is null");
      } else if (var1 == var2) {
         throw new IllegalArgumentException("src image cannot be the same as the dst image");
      } else {
         boolean var3 = false;
         ColorModel var4 = var1.getColorModel();
         BufferedImage var6 = var2;
         if (var2 == null) {
            var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
            var6 = var2;
         } else {
            ColorModel var5 = var2.getColorModel();
            if (var4.getColorSpace().getType() != var5.getColorSpace().getType()) {
               int var7 = this.xform.getType();
               AffineTransform var10001 = this.xform;
               AffineTransform var10002 = this.xform;
               boolean var8 = (var7 & (24 | 32)) != 0;
               if (!var8) {
                  var10001 = this.xform;
                  if (var7 != 1) {
                     var10001 = this.xform;
                     if (var7 != 0) {
                        double[] var9 = new double[4];
                        this.xform.getMatrix(var9);
                        var8 = var9[0] != (double)((int)var9[0]) || var9[3] != (double)((int)var9[3]);
                     }
                  }
               }

               if (var8 && var4.getTransparency() == 1) {
                  ColorConvertOp var19 = new ColorConvertOp(this.hints);
                  BufferedImage var10 = null;
                  int var11 = var1.getWidth();
                  int var12 = var1.getHeight();
                  if (var5.getTransparency() == 1) {
                     var10 = new BufferedImage(var11, var12, 2);
                  } else {
                     WritableRaster var13 = var5.createCompatibleWritableRaster(var11, var12);
                     var10 = new BufferedImage(var5, var13, var5.isAlphaPremultiplied(), (Hashtable)null);
                  }

                  var1 = var19.filter(var1, var10);
               } else {
                  var3 = true;
                  var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
               }
            }
         }

         if (this.interpolationType != 1 && var2.getColorModel() instanceof IndexColorModel) {
            var2 = new BufferedImage(var2.getWidth(), var2.getHeight(), 2);
         }

         if (ImagingLib.filter((BufferedImageOp)this, (BufferedImage)var1, (BufferedImage)var2) == null) {
            throw new ImagingOpException("Unable to transform src image");
         } else {
            if (var3) {
               ColorConvertOp var17 = new ColorConvertOp(this.hints);
               var17.filter(var2, var6);
            } else if (var6 != var2) {
               Graphics2D var18 = var6.createGraphics();

               try {
                  var18.setComposite(AlphaComposite.Src);
                  var18.drawImage(var2, 0, 0, (ImageObserver)null);
               } finally {
                  var18.dispose();
               }
            }

            return var6;
         }
      }
   }

   public final WritableRaster filter(Raster var1, WritableRaster var2) {
      if (var1 == null) {
         throw new NullPointerException("src image is null");
      } else {
         if (var2 == null) {
            var2 = this.createCompatibleDestRaster(var1);
         }

         if (var1 == var2) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
         } else if (var1.getNumBands() != var2.getNumBands()) {
            throw new IllegalArgumentException("Number of src bands (" + var1.getNumBands() + ") does not match number of  dst bands (" + var2.getNumBands() + ")");
         } else if (ImagingLib.filter((RasterOp)this, (Raster)var1, (WritableRaster)var2) == null) {
            throw new ImagingOpException("Unable to transform src image");
         } else {
            return var2;
         }
      }
   }

   public final Rectangle2D getBounds2D(BufferedImage var1) {
      return this.getBounds2D((Raster)var1.getRaster());
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      float[] var4 = new float[]{0.0F, 0.0F, (float)var2, 0.0F, (float)var2, (float)var3, 0.0F, (float)var3};
      this.xform.transform((float[])var4, 0, (float[])var4, 0, 4);
      float var5 = var4[0];
      float var6 = var4[1];
      float var7 = var4[0];
      float var8 = var4[1];

      for(int var9 = 2; var9 < 8; var9 += 2) {
         if (var4[var9] > var5) {
            var5 = var4[var9];
         } else if (var4[var9] < var7) {
            var7 = var4[var9];
         }

         if (var4[var9 + 1] > var6) {
            var6 = var4[var9 + 1];
         } else if (var4[var9 + 1] < var8) {
            var8 = var4[var9 + 1];
         }
      }

      return new Rectangle2D.Float(var7, var8, var5 - var7, var6 - var8);
   }

   public BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2) {
      Rectangle var4 = this.getBounds2D(var1).getBounds();
      int var5 = var4.x + var4.width;
      int var6 = var4.y + var4.height;
      if (var5 <= 0) {
         throw new RasterFormatException("Transformed width (" + var5 + ") is less than or equal to 0.");
      } else if (var6 <= 0) {
         throw new RasterFormatException("Transformed height (" + var6 + ") is less than or equal to 0.");
      } else {
         BufferedImage var3;
         if (var2 == null) {
            ColorModel var7 = var1.getColorModel();
            if (this.interpolationType == 1 || !(var7 instanceof IndexColorModel) && var7.getTransparency() != 1) {
               var3 = new BufferedImage(var7, var1.getRaster().createCompatibleWritableRaster(var5, var6), var7.isAlphaPremultiplied(), (Hashtable)null);
            } else {
               var3 = new BufferedImage(var5, var6, 2);
            }
         } else {
            var3 = new BufferedImage(var2, var2.createCompatibleWritableRaster(var5, var6), var2.isAlphaPremultiplied(), (Hashtable)null);
         }

         return var3;
      }
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      Rectangle2D var2 = this.getBounds2D(var1);
      return var1.createCompatibleWritableRaster((int)var2.getX(), (int)var2.getY(), (int)var2.getWidth(), (int)var2.getHeight());
   }

   public final Point2D getPoint2D(Point2D var1, Point2D var2) {
      return this.xform.transform(var1, var2);
   }

   public final AffineTransform getTransform() {
      return (AffineTransform)this.xform.clone();
   }

   public final RenderingHints getRenderingHints() {
      if (this.hints == null) {
         Object var1;
         switch(this.interpolationType) {
         case 1:
            var1 = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            break;
         case 2:
            var1 = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            break;
         case 3:
            var1 = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            break;
         default:
            throw new InternalError("Unknown interpolation type " + this.interpolationType);
         }

         this.hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, var1);
      }

      return this.hints;
   }

   void validateTransform(AffineTransform var1) {
      if (Math.abs(var1.getDeterminant()) <= Double.MIN_VALUE) {
         throw new ImagingOpException("Unable to invert transform " + var1);
      }
   }
}
