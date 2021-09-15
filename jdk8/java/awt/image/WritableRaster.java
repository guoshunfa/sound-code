package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class WritableRaster extends Raster {
   protected WritableRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (WritableRaster)null);
   }

   protected WritableRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (WritableRaster)null);
   }

   protected WritableRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, WritableRaster var5) {
      super(var1, var2, var3, var4, var5);
   }

   public WritableRaster getWritableParent() {
      return (WritableRaster)this.parent;
   }

   public WritableRaster createWritableTranslatedChild(int var1, int var2) {
      return this.createWritableChild(this.minX, this.minY, this.width, this.height, var1, var2, (int[])null);
   }

   public WritableRaster createWritableChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      if (var1 < this.minX) {
         throw new RasterFormatException("parentX lies outside raster");
      } else if (var2 < this.minY) {
         throw new RasterFormatException("parentY lies outside raster");
      } else if (var1 + var3 >= var1 && var1 + var3 <= this.width + this.minX) {
         if (var2 + var4 >= var2 && var2 + var4 <= this.height + this.minY) {
            SampleModel var8;
            if (var7 != null) {
               var8 = this.sampleModel.createSubsetSampleModel(var7);
            } else {
               var8 = this.sampleModel;
            }

            int var9 = var5 - var1;
            int var10 = var6 - var2;
            return new WritableRaster(var8, this.getDataBuffer(), new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(parentY + height) is outside raster");
         }
      } else {
         throw new RasterFormatException("(parentX + width) is outside raster");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      this.sampleModel.setDataElements(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public void setDataElements(int var1, int var2, Raster var3) {
      int var4 = var1 + var3.getMinX();
      int var5 = var2 + var3.getMinY();
      int var6 = var3.getWidth();
      int var7 = var3.getHeight();
      if (var4 >= this.minX && var5 >= this.minY && var4 + var6 <= this.minX + this.width && var5 + var7 <= this.minY + this.height) {
         int var8 = var3.getMinX();
         int var9 = var3.getMinY();
         Object var10 = null;

         for(int var11 = 0; var11 < var7; ++var11) {
            var10 = var3.getDataElements(var8, var9 + var11, var6, 1, var10);
            this.setDataElements(var4, var5 + var11, var6, 1, var10);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      this.sampleModel.setDataElements(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public void setRect(Raster var1) {
      this.setRect(0, 0, var1);
   }

   public void setRect(int var1, int var2, Raster var3) {
      int var4 = var3.getWidth();
      int var5 = var3.getHeight();
      int var6 = var3.getMinX();
      int var7 = var3.getMinY();
      int var8 = var1 + var6;
      int var9 = var2 + var7;
      int var10;
      if (var8 < this.minX) {
         var10 = this.minX - var8;
         var4 -= var10;
         var6 += var10;
         var8 = this.minX;
      }

      if (var9 < this.minY) {
         var10 = this.minY - var9;
         var5 -= var10;
         var7 += var10;
         var9 = this.minY;
      }

      if (var8 + var4 > this.minX + this.width) {
         var4 = this.minX + this.width - var8;
      }

      if (var9 + var5 > this.minY + this.height) {
         var5 = this.minY + this.height - var9;
      }

      if (var4 > 0 && var5 > 0) {
         switch(var3.getSampleModel().getDataType()) {
         case 0:
         case 1:
         case 2:
         case 3:
            int[] var14 = null;

            for(int var15 = 0; var15 < var5; ++var15) {
               var14 = var3.getPixels(var6, var7 + var15, var4, 1, (int[])var14);
               this.setPixels(var8, var9 + var15, var4, 1, (int[])var14);
            }

            return;
         case 4:
            float[] var11 = null;

            for(int var16 = 0; var16 < var5; ++var16) {
               var11 = var3.getPixels(var6, var7 + var16, var4, 1, (float[])var11);
               this.setPixels(var8, var9 + var16, var4, 1, (float[])var11);
            }

            return;
         case 5:
            double[] var12 = null;

            for(int var13 = 0; var13 < var5; ++var13) {
               var12 = var3.getPixels(var6, var7 + var13, var4, 1, (double[])var12);
               this.setPixels(var8, var9 + var13, var4, 1, (double[])var12);
            }
         }

      }
   }

   public void setPixel(int var1, int var2, int[] var3) {
      this.sampleModel.setPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public void setPixel(int var1, int var2, float[] var3) {
      this.sampleModel.setPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public void setPixel(int var1, int var2, double[] var3) {
      this.sampleModel.setPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5) {
      this.sampleModel.setPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public void setPixels(int var1, int var2, int var3, int var4, float[] var5) {
      this.sampleModel.setPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public void setPixels(int var1, int var2, int var3, int var4, double[] var5) {
      this.sampleModel.setPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public void setSample(int var1, int var2, int var3, int var4) {
      this.sampleModel.setSample(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, this.dataBuffer);
   }

   public void setSample(int var1, int var2, int var3, float var4) {
      this.sampleModel.setSample(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, this.dataBuffer);
   }

   public void setSample(int var1, int var2, int var3, double var4) {
      this.sampleModel.setSample(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, this.dataBuffer);
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      this.sampleModel.setSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, float[] var6) {
      this.sampleModel.setSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, double[] var6) {
      this.sampleModel.setSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }
}
