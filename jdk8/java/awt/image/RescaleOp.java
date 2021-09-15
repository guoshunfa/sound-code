package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import sun.awt.image.ImagingLib;

public class RescaleOp implements BufferedImageOp, RasterOp {
   float[] scaleFactors;
   float[] offsets;
   int length = 0;
   RenderingHints hints;
   private int srcNbits;
   private int dstNbits;

   public RescaleOp(float[] var1, float[] var2, RenderingHints var3) {
      this.length = var1.length;
      if (this.length > var2.length) {
         this.length = var2.length;
      }

      this.scaleFactors = new float[this.length];
      this.offsets = new float[this.length];

      for(int var4 = 0; var4 < this.length; ++var4) {
         this.scaleFactors[var4] = var1[var4];
         this.offsets[var4] = var2[var4];
      }

      this.hints = var3;
   }

   public RescaleOp(float var1, float var2, RenderingHints var3) {
      this.length = 1;
      this.scaleFactors = new float[1];
      this.offsets = new float[1];
      this.scaleFactors[0] = var1;
      this.offsets[0] = var2;
      this.hints = var3;
   }

   public final float[] getScaleFactors(float[] var1) {
      if (var1 == null) {
         return (float[])((float[])this.scaleFactors.clone());
      } else {
         System.arraycopy(this.scaleFactors, 0, var1, 0, Math.min(this.scaleFactors.length, var1.length));
         return var1;
      }
   }

   public final float[] getOffsets(float[] var1) {
      if (var1 == null) {
         return (float[])((float[])this.offsets.clone());
      } else {
         System.arraycopy(this.offsets, 0, var1, 0, Math.min(this.offsets.length, var1.length));
         return var1;
      }
   }

   public final int getNumFactors() {
      return this.length;
   }

   private ByteLookupTable createByteLut(float[] var1, float[] var2, int var3, int var4) {
      byte[][] var5 = new byte[var1.length][var4];

      for(int var6 = 0; var6 < var1.length; ++var6) {
         float var7 = var1[var6];
         float var8 = var2[var6];
         byte[] var9 = var5[var6];

         for(int var10 = 0; var10 < var4; ++var10) {
            int var11 = (int)((float)var10 * var7 + var8);
            if ((var11 & -256) != 0) {
               if (var11 < 0) {
                  var11 = 0;
               } else {
                  var11 = 255;
               }
            }

            var9[var10] = (byte)var11;
         }
      }

      return new ByteLookupTable(0, var5);
   }

   private ShortLookupTable createShortLut(float[] var1, float[] var2, int var3, int var4) {
      short[][] var5 = new short[var1.length][var4];

      for(int var6 = 0; var6 < var1.length; ++var6) {
         float var7 = var1[var6];
         float var8 = var2[var6];
         short[] var9 = var5[var6];

         for(int var10 = 0; var10 < var4; ++var10) {
            int var11 = (int)((float)var10 * var7 + var8);
            if ((var11 & -65536) != 0) {
               if (var11 < 0) {
                  var11 = 0;
               } else {
                  var11 = 65535;
               }
            }

            var9[var10] = (short)var11;
         }
      }

      return new ShortLookupTable(0, var5);
   }

   private boolean canUseLookup(Raster var1, Raster var2) {
      int var3 = var1.getDataBuffer().getDataType();
      if (var3 != 0 && var3 != 1) {
         return false;
      } else {
         SampleModel var4 = var2.getSampleModel();
         this.dstNbits = var4.getSampleSize(0);
         if (this.dstNbits != 8 && this.dstNbits != 16) {
            return false;
         } else {
            int var6;
            for(int var5 = 1; var5 < var1.getNumBands(); ++var5) {
               var6 = var4.getSampleSize(var5);
               if (var6 != this.dstNbits) {
                  return false;
               }
            }

            SampleModel var8 = var1.getSampleModel();
            this.srcNbits = var8.getSampleSize(0);
            if (this.srcNbits > 16) {
               return false;
            } else {
               for(var6 = 1; var6 < var1.getNumBands(); ++var6) {
                  int var7 = var8.getSampleSize(var6);
                  if (var7 != this.srcNbits) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public final BufferedImage filter(BufferedImage var1, BufferedImage var2) {
      ColorModel var3 = var1.getColorModel();
      int var5 = var3.getNumColorComponents();
      if (var3 instanceof IndexColorModel) {
         throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
      } else if (this.length != 1 && this.length != var5 && this.length != var3.getNumComponents()) {
         throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
      } else {
         boolean var6 = false;
         if (this.length > var5 && var3.hasAlpha()) {
            this.length = var5 + 1;
         }

         int var7 = var1.getWidth();
         int var8 = var1.getHeight();
         ColorModel var4;
         if (var2 == null) {
            var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
            var4 = var3;
         } else {
            if (var7 != var2.getWidth()) {
               throw new IllegalArgumentException("Src width (" + var7 + ") not equal to dst width (" + var2.getWidth() + ")");
            }

            if (var8 != var2.getHeight()) {
               throw new IllegalArgumentException("Src height (" + var8 + ") not equal to dst height (" + var2.getHeight() + ")");
            }

            var4 = var2.getColorModel();
            if (var3.getColorSpace().getType() != var4.getColorSpace().getType()) {
               var6 = true;
               var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
            }
         }

         if (ImagingLib.filter((BufferedImageOp)this, (BufferedImage)var1, (BufferedImage)var2) == null) {
            WritableRaster var10 = var1.getRaster();
            WritableRaster var11 = var2.getRaster();
            int var12;
            int var13;
            if (var3.hasAlpha() && (var5 - 1 == this.length || this.length == 1)) {
               var12 = var10.getMinX();
               var13 = var10.getMinY();
               int[] var14 = new int[var5 - 1];

               for(int var15 = 0; var15 < var5 - 1; var14[var15] = var15++) {
               }

               var10 = var10.createWritableChild(var12, var13, var10.getWidth(), var10.getHeight(), var12, var13, var14);
            }

            if (var4.hasAlpha()) {
               var12 = var11.getNumBands();
               if (var12 - 1 == this.length || this.length == 1) {
                  var13 = var11.getMinX();
                  int var18 = var11.getMinY();
                  int[] var19 = new int[var5 - 1];

                  for(int var16 = 0; var16 < var5 - 1; var19[var16] = var16++) {
                  }

                  var11 = var11.createWritableChild(var13, var18, var11.getWidth(), var11.getHeight(), var13, var18, var19);
               }
            }

            this.filter((Raster)var10, (WritableRaster)var11);
         }

         if (var6) {
            ColorConvertOp var17 = new ColorConvertOp(this.hints);
            var17.filter(var2, var2);
         }

         return var2;
      }
   }

   public final WritableRaster filter(Raster var1, WritableRaster var2) {
      int var3 = var1.getNumBands();
      int var4 = var1.getWidth();
      int var5 = var1.getHeight();
      int[] var6 = null;
      byte var7 = 0;
      boolean var8 = false;
      if (var2 == null) {
         var2 = this.createCompatibleDestRaster(var1);
      } else {
         if (var5 != var2.getHeight() || var4 != var2.getWidth()) {
            throw new IllegalArgumentException("Width or height of Rasters do not match");
         }

         if (var3 != var2.getNumBands()) {
            throw new IllegalArgumentException("Number of bands in src " + var3 + " does not equal number of bands in dest " + var2.getNumBands());
         }
      }

      if (this.length != 1 && this.length != var1.getNumBands()) {
         throw new IllegalArgumentException("Number of scaling constants does not equal the number of of bands in the src raster");
      } else if (ImagingLib.filter((RasterOp)this, (Raster)var1, (WritableRaster)var2) != null) {
         return var2;
      } else {
         int var9;
         int var10;
         if (this.canUseLookup(var1, var2)) {
            var9 = 1 << this.srcNbits;
            var10 = 1 << this.dstNbits;
            LookupOp var12;
            if (var10 == 256) {
               ByteLookupTable var11 = this.createByteLut(this.scaleFactors, this.offsets, var3, var9);
               var12 = new LookupOp(var11, this.hints);
               var12.filter(var1, var2);
            } else {
               ShortLookupTable var24 = this.createShortLut(this.scaleFactors, this.offsets, var3, var9);
               var12 = new LookupOp(var24, this.hints);
               var12.filter(var1, var2);
            }
         } else {
            if (this.length > 1) {
               var7 = 1;
            }

            var9 = var1.getMinX();
            var10 = var1.getMinY();
            int var25 = var2.getMinX();
            int var26 = var2.getMinY();
            int[] var16 = new int[var3];
            int[] var17 = new int[var3];
            SampleModel var18 = var2.getSampleModel();

            int var19;
            for(var19 = 0; var19 < var3; ++var19) {
               int var15 = var18.getSampleSize(var19);
               var16[var19] = (1 << var15) - 1;
               var17[var19] = ~var16[var19];
            }

            for(int var20 = 0; var20 < var5; ++var26) {
               int var14 = var25;
               int var13 = var9;

               for(int var21 = 0; var21 < var4; ++var14) {
                  var6 = var1.getPixel(var13, var10, var6);
                  int var23 = 0;

                  for(int var22 = 0; var22 < var3; var23 += var7) {
                     var19 = (int)((float)var6[var22] * this.scaleFactors[var23] + this.offsets[var23]);
                     if ((var19 & var17[var22]) != 0) {
                        if (var19 < 0) {
                           var19 = 0;
                        } else {
                           var19 = var16[var22];
                        }
                     }

                     var6[var22] = var19;
                     ++var22;
                  }

                  var2.setPixel(var14, var26, var6);
                  ++var21;
                  ++var13;
               }

               ++var20;
               ++var10;
            }
         }

         return var2;
      }
   }

   public final Rectangle2D getBounds2D(BufferedImage var1) {
      return this.getBounds2D((Raster)var1.getRaster());
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      return var1.getBounds();
   }

   public BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2) {
      BufferedImage var3;
      if (var2 == null) {
         ColorModel var4 = var1.getColorModel();
         var3 = new BufferedImage(var4, var1.getRaster().createCompatibleWritableRaster(), var4.isAlphaPremultiplied(), (Hashtable)null);
      } else {
         int var6 = var1.getWidth();
         int var5 = var1.getHeight();
         var3 = new BufferedImage(var2, var2.createCompatibleWritableRaster(var6, var5), var2.isAlphaPremultiplied(), (Hashtable)null);
      }

      return var3;
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      return var1.createCompatibleWritableRaster(var1.getWidth(), var1.getHeight());
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
