package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ShortComponentRaster extends SunWritableRaster {
   protected int bandOffset;
   protected int[] dataOffsets;
   protected int scanlineStride;
   protected int pixelStride;
   protected short[] data;
   int type;
   private int maxX;
   private int maxY;

   private static native void initIDs();

   public ShortComponentRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (ShortComponentRaster)null);
   }

   public ShortComponentRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (ShortComponentRaster)null);
   }

   public ShortComponentRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, ShortComponentRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferUShort)) {
         throw new RasterFormatException("ShortComponentRasters must have short DataBuffers");
      } else {
         DataBufferUShort var6 = (DataBufferUShort)var2;
         this.data = stealData(var6, 0);
         if (var6.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for ShortComponentRasters must only have 1 bank.");
         } else {
            int var7 = var6.getOffset();
            int var9;
            int var10;
            int[] var10000;
            if (var1 instanceof ComponentSampleModel) {
               ComponentSampleModel var8 = (ComponentSampleModel)var1;
               this.type = 2;
               this.scanlineStride = var8.getScanlineStride();
               this.pixelStride = var8.getPixelStride();
               this.dataOffsets = var8.getBandOffsets();
               var9 = var3.x - var4.x;
               var10 = var3.y - var4.y;

               for(int var11 = 0; var11 < this.getNumDataElements(); ++var11) {
                  var10000 = this.dataOffsets;
                  var10000[var11] += var7 + var9 * this.pixelStride + var10 * this.scanlineStride;
               }
            } else {
               if (!(var1 instanceof SinglePixelPackedSampleModel)) {
                  throw new RasterFormatException("ShortComponentRasters must haveComponentSampleModel or SinglePixelPackedSampleModel");
               }

               SinglePixelPackedSampleModel var12 = (SinglePixelPackedSampleModel)var1;
               this.type = 8;
               this.scanlineStride = var12.getScanlineStride();
               this.pixelStride = 1;
               this.dataOffsets = new int[1];
               this.dataOffsets[0] = var7;
               var9 = var3.x - var4.x;
               var10 = var3.y - var4.y;
               var10000 = this.dataOffsets;
               var10000[0] += var9 + var10 * this.scanlineStride;
            }

            this.bandOffset = this.dataOffsets[0];
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
      return this.pixelStride;
   }

   public short[] getDataStorage() {
      return this.data;
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         short[] var4;
         if (var3 == null) {
            var4 = new short[this.numDataElements];
         } else {
            var4 = (short[])((short[])var3);
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
         short[] var6;
         if (var5 == null) {
            var6 = new short[var3 * var4 * this.numDataElements];
         } else {
            var6 = (short[])((short[])var5);
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

   public short[] getShortData(int var1, int var2, int var3, int var4, int var5, short[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var6 == null) {
            var6 = new short[this.numDataElements * var3 * var4];
         }

         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride + this.dataOffsets[var5];
         int var9 = 0;
         int var11;
         if (this.pixelStride == 1) {
            if (this.scanlineStride == var3) {
               System.arraycopy(this.data, var7, var6, 0, var3 * var4);
            } else {
               for(var11 = 0; var11 < var4; var7 += this.scanlineStride) {
                  System.arraycopy(this.data, var7, var6, var9, var3);
                  var9 += var3;
                  ++var11;
               }
            }
         } else {
            for(var11 = 0; var11 < var4; var7 += this.scanlineStride) {
               int var8 = var7;

               for(int var10 = 0; var10 < var3; var8 += this.pixelStride) {
                  var6[var9++] = this.data[var8];
                  ++var10;
               }

               ++var11;
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
            var5 = new short[this.numDataElements * var3 * var4];
         }

         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var8 = 0;

         for(int var10 = 0; var10 < var4; var6 += this.scanlineStride) {
            int var7 = var6;

            for(int var9 = 0; var9 < var3; var7 += this.pixelStride) {
               for(int var11 = 0; var11 < this.numDataElements; ++var11) {
                  var5[var8++] = this.data[this.dataOffsets[var11] + var7];
               }

               ++var9;
            }

            ++var10;
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         short[] var4 = (short[])((short[])var3);
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

         for(int var9 = 0; var9 < var4; ++var9) {
            var8 = var5.getDataElements(var6, var7 + var9, var3, 1, var8);
            this.setDataElements(var1, var2 + var9, var3, 1, (Object)var8);
         }

      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         short[] var6 = (short[])((short[])var5);
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

   public void putShortData(int var1, int var2, int var3, int var4, int var5, short[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var7 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride + this.dataOffsets[var5];
         int var9 = 0;
         int var11;
         if (this.pixelStride == 1) {
            if (this.scanlineStride == var3) {
               System.arraycopy(var6, 0, this.data, var7, var3 * var4);
            } else {
               for(var11 = 0; var11 < var4; var7 += this.scanlineStride) {
                  System.arraycopy(var6, var9, this.data, var7, var3);
                  var9 += var3;
                  ++var11;
               }
            }
         } else {
            for(var11 = 0; var11 < var4; var7 += this.scanlineStride) {
               int var8 = var7;

               for(int var10 = 0; var10 < var3; var8 += this.pixelStride) {
                  this.data[var8] = var6[var9++];
                  ++var10;
               }

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
         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var8 = 0;

         for(int var10 = 0; var10 < var4; var6 += this.scanlineStride) {
            int var7 = var6;

            for(int var9 = 0; var9 < var3; var7 += this.pixelStride) {
               for(int var11 = 0; var11 < this.numDataElements; ++var11) {
                  this.data[this.dataOffsets[var11] + var7] = var5[var8++];
               }

               ++var9;
            }

            ++var10;
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Raster createChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      WritableRaster var8 = this.createWritableChild(var1, var2, var3, var4, var5, var6, var7);
      return var8;
   }

   public WritableRaster createWritableChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      if (var1 < this.minX) {
         throw new RasterFormatException("x lies outside the raster");
      } else if (var2 < this.minY) {
         throw new RasterFormatException("y lies outside the raster");
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
            return new ShortComponentRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(y + height) is outside of Raster");
         }
      } else {
         throw new RasterFormatException("(x + width) is outside of Raster");
      }
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new ShortComponentRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   protected final void verify() {
      if (this.width > 0 && this.height > 0 && this.height <= Integer.MAX_VALUE / this.width) {
         int var1;
         for(var1 = 0; var1 < this.dataOffsets.length; ++var1) {
            if (this.dataOffsets[var1] < 0) {
               throw new RasterFormatException("Data offsets for band " + var1 + "(" + this.dataOffsets[var1] + ") must be >= 0");
            }
         }

         if ((long)this.minX - (long)this.sampleModelTranslateX >= 0L && (long)this.minY - (long)this.sampleModelTranslateY >= 0L) {
            if (this.scanlineStride >= 0 && this.scanlineStride <= Integer.MAX_VALUE / this.height) {
               if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length) {
                  throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
               } else {
                  var1 = (this.height - 1) * this.scanlineStride;
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
      return new String("ShortComponentRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements);
   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
   }
}
