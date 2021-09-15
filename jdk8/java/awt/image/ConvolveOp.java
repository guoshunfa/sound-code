package java.awt.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import sun.awt.image.ImagingLib;

public class ConvolveOp implements BufferedImageOp, RasterOp {
   Kernel kernel;
   int edgeHint;
   RenderingHints hints;
   public static final int EDGE_ZERO_FILL = 0;
   public static final int EDGE_NO_OP = 1;

   public ConvolveOp(Kernel var1, int var2, RenderingHints var3) {
      this.kernel = var1;
      this.edgeHint = var2;
      this.hints = var3;
   }

   public ConvolveOp(Kernel var1) {
      this.kernel = var1;
      this.edgeHint = 0;
   }

   public int getEdgeCondition() {
      return this.edgeHint;
   }

   public final Kernel getKernel() {
      return (Kernel)this.kernel.clone();
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
         if (var4 instanceof IndexColorModel) {
            IndexColorModel var7 = (IndexColorModel)var4;
            var1 = var7.convertToIntDiscrete(var1.getRaster(), false);
            var4 = var1.getColorModel();
         }

         if (var2 == null) {
            var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
            var6 = var2;
         } else {
            ColorModel var5 = var2.getColorModel();
            if (var4.getColorSpace().getType() != var5.getColorSpace().getType()) {
               var3 = true;
               var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
               var5 = var2.getColorModel();
            } else if (var5 instanceof IndexColorModel) {
               var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
               var5 = var2.getColorModel();
            }
         }

         if (ImagingLib.filter((BufferedImageOp)this, (BufferedImage)var1, (BufferedImage)var2) == null) {
            throw new ImagingOpException("Unable to convolve src image");
         } else {
            if (var3) {
               ColorConvertOp var11 = new ColorConvertOp(this.hints);
               var11.filter(var2, var6);
            } else if (var6 != var2) {
               Graphics2D var12 = var6.createGraphics();

               try {
                  var12.drawImage(var2, 0, 0, (ImageObserver)null);
               } finally {
                  var12.dispose();
               }
            }

            return var6;
         }
      }
   }

   public final WritableRaster filter(Raster var1, WritableRaster var2) {
      if (var2 == null) {
         var2 = this.createCompatibleDestRaster(var1);
      } else {
         if (var1 == var2) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
         }

         if (var1.getNumBands() != var2.getNumBands()) {
            throw new ImagingOpException("Different number of bands in src  and dst Rasters");
         }
      }

      if (ImagingLib.filter((RasterOp)this, (Raster)var1, (WritableRaster)var2) == null) {
         throw new ImagingOpException("Unable to convolve src image");
      } else {
         return var2;
      }
   }

   public BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2) {
      int var4 = var1.getWidth();
      int var5 = var1.getHeight();
      WritableRaster var6 = null;
      if (var2 == null) {
         var2 = var1.getColorModel();
         if (var2 instanceof IndexColorModel) {
            var2 = ColorModel.getRGBdefault();
         } else {
            var6 = var1.getData().createCompatibleWritableRaster(var4, var5);
         }
      }

      if (var6 == null) {
         var6 = var2.createCompatibleWritableRaster(var4, var5);
      }

      BufferedImage var3 = new BufferedImage(var2, var6, var2.isAlphaPremultiplied(), (Hashtable)null);
      return var3;
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      return var1.createCompatibleWritableRaster();
   }

   public final Rectangle2D getBounds2D(BufferedImage var1) {
      return this.getBounds2D((Raster)var1.getRaster());
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      return var1.getBounds();
   }

   public final Point2D getPoint2D(Point2D var1, Point2D var2) {
      if (var2 == null) {
         var2 = new Point2D.Float();
      }

      ((Point2D)var2).setLocation(var1.getX(), var1.getY());
      return (Point2D)var2;
   }

   public final RenderingHints getRenderingHints() {
      return this.hints;
   }
}
