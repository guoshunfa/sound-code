package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import sun.awt.image.ImagingLib;

public class BandCombineOp implements RasterOp {
   float[][] matrix;
   int nrows = 0;
   int ncols = 0;
   RenderingHints hints;

   public BandCombineOp(float[][] var1, RenderingHints var2) {
      this.nrows = var1.length;
      this.ncols = var1[0].length;
      this.matrix = new float[this.nrows][];

      for(int var3 = 0; var3 < this.nrows; ++var3) {
         if (this.ncols > var1[var3].length) {
            throw new IndexOutOfBoundsException("row " + var3 + " too short");
         }

         this.matrix[var3] = Arrays.copyOf(var1[var3], this.ncols);
      }

      this.hints = var2;
   }

   public final float[][] getMatrix() {
      float[][] var1 = new float[this.nrows][];

      for(int var2 = 0; var2 < this.nrows; ++var2) {
         var1[var2] = Arrays.copyOf(this.matrix[var2], this.ncols);
      }

      return var1;
   }

   public WritableRaster filter(Raster var1, WritableRaster var2) {
      int var3 = var1.getNumBands();
      if (this.ncols != var3 && this.ncols != var3 + 1) {
         throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + var3 + ").");
      } else {
         if (var2 == null) {
            var2 = this.createCompatibleDestRaster(var1);
         } else if (this.nrows != var2.getNumBands()) {
            throw new IllegalArgumentException("Number of rows in the matrix (" + this.nrows + ") must be equal to the number of bands ([+1]) in dst (" + var3 + ").");
         }

         if (ImagingLib.filter((RasterOp)this, (Raster)var1, (WritableRaster)var2) != null) {
            return var2;
         } else {
            int[] var4 = null;
            int[] var5 = new int[var2.getNumBands()];
            int var7 = var1.getMinX();
            int var8 = var1.getMinY();
            int var9 = var2.getMinX();
            int var10 = var2.getMinY();
            float var6;
            int var11;
            int var12;
            int var13;
            int var14;
            int var15;
            int var16;
            if (this.ncols == var3) {
               for(var13 = 0; var13 < var1.getHeight(); ++var10) {
                  var12 = var9;
                  var11 = var7;

                  for(var14 = 0; var14 < var1.getWidth(); ++var12) {
                     var4 = var1.getPixel(var11, var8, var4);

                     for(var15 = 0; var15 < this.nrows; ++var15) {
                        var6 = 0.0F;

                        for(var16 = 0; var16 < this.ncols; ++var16) {
                           var6 += this.matrix[var15][var16] * (float)var4[var16];
                        }

                        var5[var15] = (int)var6;
                     }

                     var2.setPixel(var12, var10, var5);
                     ++var14;
                     ++var11;
                  }

                  ++var13;
                  ++var8;
               }
            } else {
               for(var13 = 0; var13 < var1.getHeight(); ++var10) {
                  var12 = var9;
                  var11 = var7;

                  for(var14 = 0; var14 < var1.getWidth(); ++var12) {
                     var4 = var1.getPixel(var11, var8, var4);

                     for(var15 = 0; var15 < this.nrows; ++var15) {
                        var6 = 0.0F;

                        for(var16 = 0; var16 < var3; ++var16) {
                           var6 += this.matrix[var15][var16] * (float)var4[var16];
                        }

                        var5[var15] = (int)(var6 + this.matrix[var15][var3]);
                     }

                     var2.setPixel(var12, var10, var5);
                     ++var14;
                     ++var11;
                  }

                  ++var13;
                  ++var8;
               }
            }

            return var2;
         }
      }
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      return var1.getBounds();
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      int var2 = var1.getNumBands();
      if (this.ncols != var2 && this.ncols != var2 + 1) {
         throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + var2 + ").");
      } else if (var1.getNumBands() == this.nrows) {
         return var1.createCompatibleWritableRaster();
      } else {
         throw new IllegalArgumentException("Don't know how to create a  compatible Raster with " + this.nrows + " bands.");
      }
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
