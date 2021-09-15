package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class IntegerInterleavedRaster extends IntegerComponentRaster {
   private int maxX;
   private int maxY;

   public IntegerInterleavedRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (IntegerInterleavedRaster)null);
   }

   public IntegerInterleavedRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (IntegerInterleavedRaster)null);
   }

   public IntegerInterleavedRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, IntegerInterleavedRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferInt)) {
         throw new RasterFormatException("IntegerInterleavedRasters must haveinteger DataBuffers");
      } else {
         DataBufferInt var6 = (DataBufferInt)var2;
         this.data = stealData(var6, 0);
         if (var1 instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel var7 = (SinglePixelPackedSampleModel)var1;
            this.scanlineStride = var7.getScanlineStride();
            this.pixelStride = 1;
            this.dataOffsets = new int[1];
            this.dataOffsets[0] = var6.getOffset();
            this.bandOffset = this.dataOffsets[0];
            int var8 = var3.x - var4.x;
            int var9 = var3.y - var4.y;
            int[] var10000 = this.dataOffsets;
            var10000[0] += var8 + var9 * this.scanlineStride;
            this.numDataElems = var7.getNumDataElements();
            this.verify();
         } else {
            throw new RasterFormatException("IntegerInterleavedRasters must have SinglePixelPackedSampleModel");
         }
      }
   }

   public int[] getDataOffsets() {
      return (int[])((int[])this.dataOffsets.clone());
   }

   public int getDataOffset(int var1) {
      return this.dataOffsets[var1];
   }

   public int getScanlineStride() {
      return this.scanlineStride;
   }

   public int getPixelStride() {
      return this.pixelStride;
   }

   public int[] getDataStorage() {
      return this.data;
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         int[] var4;
         if (var3 == null) {
            var4 = new int[1];
         } else {
            var4 = (int[])((int[])var3);
         }

         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[0];
         var4[0] = this.data[var5];
         return var4;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Object getDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int[] var6;
         if (var5 instanceof int[]) {
            var6 = (int[])((int[])var5);
         } else {
            var6 = new int[var3 * var4];
         }

         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[0];
         int var8 = 0;

         for(int var9 = 0; var9 < var4; ++var9) {
            System.arraycopy(this.data, var7, var6, var8, var3);
            var8 += var3;
            var7 += this.scanlineStride;
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         int[] var4 = (int[])((int[])var3);
         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[0];
         this.data[var5] = var4[0];
         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Raster var3) {
      int var4 = var1 + var3.getMinX();
      int var5 = var2 + var3.getMinY();
      int var6 = var3.getWidth();
      int var7 = var3.getHeight();
      if (var4 >= this.minX && var5 >= this.minY && var4 + var6 <= this.maxX && var5 + var7 <= this.maxY) {
         this.setDataElements(var4, var5, var6, var7, var3);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   private void setDataElements(int var1, int var2, int var3, int var4, Raster var5) {
      if (var3 > 0 && var4 > 0) {
         int var6 = var5.getMinX();
         int var7 = var5.getMinY();
         Object var8 = null;
         int var10;
         if (var5 instanceof IntegerInterleavedRaster) {
            IntegerInterleavedRaster var16 = (IntegerInterleavedRaster)var5;
            int[] var15 = var16.getDataStorage();
            var10 = var16.getScanlineStride();
            int var11 = var16.getDataOffset(0);
            int var12 = var11;
            int var13 = this.dataOffsets[0] + (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

            for(int var14 = 0; var14 < var4; ++var14) {
               System.arraycopy(var15, var12, this.data, var13, var3);
               var12 += var10;
               var13 += this.scanlineStride;
            }

            this.markDirty();
         } else {
            Object var9 = null;

            for(var10 = 0; var10 < var4; ++var10) {
               var9 = var5.getDataElements(var6, var7 + var10, var3, 1, var9);
               this.setDataElements(var1, var2 + var10, var3, 1, (Object)var9);
            }

         }
      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int[] var6 = (int[])((int[])var5);
         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[0];
         int var8 = 0;

         for(int var9 = 0; var9 < var4; ++var9) {
            System.arraycopy(var6, var8, this.data, var7, var3);
            var8 += var3;
            var7 += this.scanlineStride;
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public WritableRaster createWritableChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      if (var1 < this.minX) {
         throw new RasterFormatException("x lies outside raster");
      } else if (var2 < this.minY) {
         throw new RasterFormatException("y lies outside raster");
      } else if (var1 + var3 >= var1 && var1 + var3 <= this.minX + this.width) {
         if (var2 + var4 >= var2 && var2 + var4 <= this.minY + this.height) {
            SampleModel var8;
            if (var7 != null) {
               var8 = this.sampleModel.createSubsetSampleModel(var7);
            } else {
               var8 = this.sampleModel;
            }

            int var9 = var5 - var1;
            int var10 = var6 - var2;
            return new IntegerInterleavedRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(y + height) is outside raster");
         }
      } else {
         throw new RasterFormatException("(x + width) is outside raster");
      }
   }

   public Raster createChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      return this.createWritableChild(var1, var2, var3, var4, var5, var6, var7);
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new IntegerInterleavedRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   public String toString() {
      return new String("IntegerInterleavedRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]);
   }
}
