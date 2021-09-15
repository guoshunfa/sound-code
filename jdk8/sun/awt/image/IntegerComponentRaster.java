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

public class IntegerComponentRaster extends SunWritableRaster {
   static final int TYPE_CUSTOM = 0;
   static final int TYPE_BYTE_SAMPLES = 1;
   static final int TYPE_USHORT_SAMPLES = 2;
   static final int TYPE_INT_SAMPLES = 3;
   static final int TYPE_BYTE_BANDED_SAMPLES = 4;
   static final int TYPE_USHORT_BANDED_SAMPLES = 5;
   static final int TYPE_INT_BANDED_SAMPLES = 6;
   static final int TYPE_BYTE_PACKED_SAMPLES = 7;
   static final int TYPE_USHORT_PACKED_SAMPLES = 8;
   static final int TYPE_INT_PACKED_SAMPLES = 9;
   static final int TYPE_INT_8BIT_SAMPLES = 10;
   static final int TYPE_BYTE_BINARY_SAMPLES = 11;
   protected int bandOffset;
   protected int[] dataOffsets;
   protected int scanlineStride;
   protected int pixelStride;
   protected int[] data;
   protected int numDataElems;
   int type;
   private int maxX;
   private int maxY;

   private static native void initIDs();

   public IntegerComponentRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (IntegerComponentRaster)null);
   }

   public IntegerComponentRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (IntegerComponentRaster)null);
   }

   public IntegerComponentRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, IntegerComponentRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferInt)) {
         throw new RasterFormatException("IntegerComponentRasters must haveinteger DataBuffers");
      } else {
         DataBufferInt var6 = (DataBufferInt)var2;
         if (var6.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for IntegerComponentRasters must only have 1 bank.");
         } else {
            this.data = stealData(var6, 0);
            if (var1 instanceof SinglePixelPackedSampleModel) {
               SinglePixelPackedSampleModel var7 = (SinglePixelPackedSampleModel)var1;
               int[] var8 = var7.getBitOffsets();
               boolean var9 = false;

               int var10;
               for(var10 = 1; var10 < var8.length; ++var10) {
                  if (var8[var10] % 8 != 0) {
                     var9 = true;
                  }
               }

               this.type = var9 ? 9 : 10;
               this.scanlineStride = var7.getScanlineStride();
               this.pixelStride = 1;
               this.dataOffsets = new int[1];
               this.dataOffsets[0] = var6.getOffset();
               this.bandOffset = this.dataOffsets[0];
               var10 = var3.x - var4.x;
               int var11 = var3.y - var4.y;
               int[] var10000 = this.dataOffsets;
               var10000[0] += var10 + var11 * this.scanlineStride;
               this.numDataElems = var7.getNumDataElements();
               this.verify();
            } else {
               throw new RasterFormatException("IntegerComponentRasters must have SinglePixelPackedSampleModel");
            }
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
            var4 = new int[this.numDataElements];
         } else {
            var4 = (int[])((int[])var3);
         }

         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;

         for(int var6 = 0; var6 < this.numDataElements; ++var6) {
            var4[var6] = this.data[this.dataOffsets[var6] + var5];
         }

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
            var6 = new int[this.numDataElements * var3 * var4];
         }

         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var9 = 0;

         for(int var11 = 0; var11 < var4; var7 += this.scanlineStride) {
            int var8 = var7;

            for(int var10 = 0; var10 < var3; var8 += this.pixelStride) {
               for(int var12 = 0; var12 < this.numDataElements; ++var12) {
                  var6[var9++] = this.data[this.dataOffsets[var12] + var8];
               }

               ++var10;
            }

            ++var11;
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         int[] var4 = (int[])((int[])var3);
         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;

         for(int var6 = 0; var6 < this.numDataElements; ++var6) {
            this.data[this.dataOffsets[var6] + var5] = var4[var6];
         }

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
         if (var5 instanceof IntegerComponentRaster && this.pixelStride == 1 && this.numDataElements == 1) {
            IntegerComponentRaster var9 = (IntegerComponentRaster)var5;
            if (var9.getNumDataElements() != 1) {
               throw new ArrayIndexOutOfBoundsException("Number of bands does not match");
            }

            int[] var15 = var9.getDataStorage();
            var10 = var9.getScanlineStride();
            int var11 = var9.getDataOffset(0);
            int var12 = var11;
            int var13 = this.dataOffsets[0] + (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);
            if (var9.getPixelStride() == this.pixelStride) {
               var3 *= this.pixelStride;

               for(int var14 = 0; var14 < var4; ++var14) {
                  System.arraycopy(var15, var12, this.data, var13, var3);
                  var12 += var10;
                  var13 += this.scanlineStride;
               }

               this.markDirty();
               return;
            }
         }

         Object var16 = null;

         for(var10 = 0; var10 < var4; ++var10) {
            var16 = var5.getDataElements(var6, var7 + var10, var3, 1, var16);
            this.setDataElements(var1, var2 + var10, var3, 1, (Object)var16);
         }

      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int[] var6 = (int[])((int[])var5);
         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var9 = 0;

         for(int var11 = 0; var11 < var4; var7 += this.scanlineStride) {
            int var8 = var7;

            for(int var10 = 0; var10 < var3; var8 += this.pixelStride) {
               for(int var12 = 0; var12 < this.numDataElements; ++var12) {
                  this.data[this.dataOffsets[var12] + var8] = var6[var9++];
               }

               ++var10;
            }

            ++var11;
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
            return new IntegerComponentRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
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
         return new IntegerComponentRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   protected final void verify() {
      if (this.width > 0 && this.height > 0 && this.height <= Integer.MAX_VALUE / this.width) {
         if (this.dataOffsets[0] < 0) {
            throw new RasterFormatException("Data offset (" + this.dataOffsets[0] + ") must be >= 0");
         } else if ((long)this.minX - (long)this.sampleModelTranslateX >= 0L && (long)this.minY - (long)this.sampleModelTranslateY >= 0L) {
            if (this.scanlineStride >= 0 && this.scanlineStride <= Integer.MAX_VALUE / this.height) {
               if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length) {
                  throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
               } else {
                  int var1 = (this.height - 1) * this.scanlineStride;
                  if (this.pixelStride >= 0 && this.pixelStride <= Integer.MAX_VALUE / this.width && this.pixelStride <= this.data.length) {
                     int var2 = (this.width - 1) * this.pixelStride;
                     if (var2 > Integer.MAX_VALUE - var1) {
                        throw new RasterFormatException("Incorrect raster attributes");
                     } else {
                        var2 += var1;
                        int var4 = 0;

                        for(int var5 = 0; var5 < this.numDataElements; ++var5) {
                           if (this.dataOffsets[var5] > Integer.MAX_VALUE - var2) {
                              throw new RasterFormatException("Incorrect band offset: " + this.dataOffsets[var5]);
                           }

                           int var3 = var2 + this.dataOffsets[var5];
                           if (var3 > var4) {
                              var4 = var3;
                           }
                        }

                        if (this.data.length <= var4) {
                           throw new RasterFormatException("Data array too small (should be > " + var4 + " )");
                        }
                     }
                  } else {
                     throw new RasterFormatException("Incorrect pixel stride: " + this.pixelStride);
                  }
               }
            } else {
               throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
            }
         } else {
            throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
         }
      } else {
         throw new RasterFormatException("Invalid raster dimension");
      }
   }

   public String toString() {
      return new String("IntegerComponentRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " #DataElements " + this.numDataElements + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]);
   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
   }
}
