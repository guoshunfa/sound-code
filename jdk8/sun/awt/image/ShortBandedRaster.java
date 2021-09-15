package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class ShortBandedRaster extends SunWritableRaster {
   int[] dataOffsets;
   int scanlineStride;
   short[][] data;
   private int maxX;
   private int maxY;

   public ShortBandedRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (ShortBandedRaster)null);
   }

   public ShortBandedRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (ShortBandedRaster)null);
   }

   public ShortBandedRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, ShortBandedRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferUShort)) {
         throw new RasterFormatException("ShortBandedRaster must have ushort DataBuffers");
      } else {
         DataBufferUShort var6 = (DataBufferUShort)var2;
         if (!(var1 instanceof BandedSampleModel)) {
            throw new RasterFormatException("ShortBandedRasters must have BandedSampleModels");
         } else {
            BandedSampleModel var7 = (BandedSampleModel)var1;
            this.scanlineStride = var7.getScanlineStride();
            int[] var8 = var7.getBankIndices();
            int[] var9 = var7.getBandOffsets();
            int[] var10 = var6.getOffsets();
            this.dataOffsets = new int[var8.length];
            this.data = new short[var8.length][];
            int var11 = var3.x - var4.x;
            int var12 = var3.y - var4.y;

            for(int var13 = 0; var13 < var8.length; ++var13) {
               this.data[var13] = stealData(var6, var8[var13]);
               this.dataOffsets[var13] = var10[var8[var13]] + var11 + var12 * this.scanlineStride + var9[var13];
            }

            this.verify();
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
      return 1;
   }

   public short[][] getDataStorage() {
      return this.data;
   }

   public short[] getDataStorage(int var1) {
      return this.data[var1];
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         short[] var4;
         if (var3 == null) {
            var4 = new short[this.numDataElements];
         } else {
            var4 = (short[])((short[])var3);
         }

         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var6 = 0; var6 < this.numDataElements; ++var6) {
            var4[var6] = this.data[var6][this.dataOffsets[var6] + var5];
         }

         return var4;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Object getDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         short[] var6;
         if (var5 == null) {
            var6 = new short[this.numDataElements * var3 * var4];
         } else {
            var6 = (short[])((short[])var5);
         }

         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var8 = 0; var8 < this.numDataElements; ++var8) {
            int var9 = var8;
            short[] var10 = this.data[var8];
            int var11 = this.dataOffsets[var8];
            int var12 = var7;

            for(int var13 = 0; var13 < var4; var12 += this.scanlineStride) {
               int var14 = var11 + var12;

               for(int var15 = 0; var15 < var3; ++var15) {
                  var6[var9] = var10[var14++];
                  var9 += this.numDataElements;
               }

               ++var13;
            }
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public short[] getShortData(int var1, int var2, int var3, int var4, int var5, short[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var6 == null) {
            var6 = new short[this.scanlineStride * var4];
         }

         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[var5];
         if (this.scanlineStride == var3) {
            System.arraycopy(this.data[var5], var7, var6, 0, var3 * var4);
         } else {
            int var8 = 0;

            for(int var9 = 0; var9 < var4; var7 += this.scanlineStride) {
               System.arraycopy(this.data[var5], var7, var6, var8, var3);
               var8 += var3;
               ++var9;
            }
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public short[] getShortData(int var1, int var2, int var3, int var4, short[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var5 == null) {
            var5 = new short[this.numDataElements * this.scanlineStride * var4];
         }

         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var7 = 0; var7 < this.numDataElements; ++var7) {
            int var8 = var7;
            short[] var9 = this.data[var7];
            int var10 = this.dataOffsets[var7];
            int var11 = var6;

            for(int var12 = 0; var12 < var4; var11 += this.scanlineStride) {
               int var13 = var10 + var11;

               for(int var14 = 0; var14 < var3; ++var14) {
                  var5[var8] = var9[var13++];
                  var8 += this.numDataElements;
               }

               ++var12;
            }
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         short[] var4 = (short[])((short[])var3);
         int var5 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var6 = 0; var6 < this.numDataElements; ++var6) {
            this.data[var6][this.dataOffsets[var6] + var5] = var4[var6];
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

         for(int var9 = 0; var9 < var4; ++var9) {
            var8 = var5.getDataElements(var6, var7 + var9, var3, 1, var8);
            this.setDataElements(var1, var2 + var9, var3, 1, (Object)var8);
         }

      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         short[] var6 = (short[])((short[])var5);
         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var8 = 0; var8 < this.numDataElements; ++var8) {
            int var9 = var8;
            short[] var10 = this.data[var8];
            int var11 = this.dataOffsets[var8];
            int var12 = var7;

            for(int var13 = 0; var13 < var4; var12 += this.scanlineStride) {
               int var14 = var11 + var12;

               for(int var15 = 0; var15 < var3; ++var15) {
                  var10[var14++] = var6[var9];
                  var9 += this.numDataElements;
               }

               ++var13;
            }
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void putShortData(int var1, int var2, int var3, int var4, int var5, short[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) + this.dataOffsets[var5];
         int var9 = 0;
         if (this.scanlineStride == var3) {
            System.arraycopy(var6, 0, this.data[var5], var7, var3 * var4);
         } else {
            for(int var11 = 0; var11 < var4; var7 += this.scanlineStride) {
               System.arraycopy(var6, var9, this.data[var5], var7, var3);
               var9 += var3;
               ++var11;
            }
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void putShortData(int var1, int var2, int var3, int var4, short[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX);

         for(int var7 = 0; var7 < this.numDataElements; ++var7) {
            int var8 = var7;
            short[] var9 = this.data[var7];
            int var10 = this.dataOffsets[var7];
            int var11 = var6;

            for(int var12 = 0; var12 < var4; var11 += this.scanlineStride) {
               int var13 = var10 + var11;

               for(int var14 = 0; var14 < var3; ++var14) {
                  var9[var13++] = var5[var8];
                  var8 += this.numDataElements;
               }

               ++var12;
            }
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
            return new ShortBandedRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(y + height) is outside of Raster");
         }
      } else {
         throw new RasterFormatException("(x + width) is outside of Raster");
      }
   }

   public Raster createChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      return this.createWritableChild(var1, var2, var3, var4, var5, var6, var7);
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new ShortBandedRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   private void verify() {
      if (this.width > 0 && this.height > 0 && this.height <= Integer.MAX_VALUE / this.width) {
         if (this.scanlineStride >= 0 && this.scanlineStride <= Integer.MAX_VALUE / this.height) {
            if ((long)this.minX - (long)this.sampleModelTranslateX >= 0L && (long)this.minY - (long)this.sampleModelTranslateY >= 0L) {
               int var1;
               if (this.height > 1 || this.minY - this.sampleModelTranslateY > 0) {
                  for(var1 = 0; var1 < this.data.length; ++var1) {
                     if (this.scanlineStride > this.data[var1].length) {
                        throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
                     }
                  }
               }

               for(var1 = 0; var1 < this.dataOffsets.length; ++var1) {
                  if (this.dataOffsets[var1] < 0) {
                     throw new RasterFormatException("Data offsets for band " + var1 + "(" + this.dataOffsets[var1] + ") must be >= 0");
                  }
               }

               var1 = (this.height - 1) * this.scanlineStride;
               if (this.width - 1 > Integer.MAX_VALUE - var1) {
                  throw new RasterFormatException("Invalid raster dimension");
               } else {
                  int var2 = var1 + (this.width - 1);
                  int var3 = 0;

                  int var5;
                  for(var5 = 0; var5 < this.numDataElements; ++var5) {
                     if (this.dataOffsets[var5] > Integer.MAX_VALUE - var2) {
                        throw new RasterFormatException("Invalid raster dimension");
                     }

                     int var4 = var2 + this.dataOffsets[var5];
                     if (var4 > var3) {
                        var3 = var4;
                     }
                  }

                  for(var5 = 0; var5 < this.numDataElements; ++var5) {
                     if (this.data[var5].length <= var3) {
                        throw new RasterFormatException("Data array too small (should be > " + var3 + " )");
                     }
                  }

               }
            } else {
               throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
            }
         } else {
            throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
         }
      } else {
         throw new RasterFormatException("Invalid raster dimension");
      }
   }

   public String toString() {
      return new String("ShortBandedRaster: width = " + this.width + " height = " + this.height + " #numBands " + this.numBands + " #dataElements " + this.numDataElements);
   }
}
