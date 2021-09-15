package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ByteInterleavedRaster extends ByteComponentRaster {
   boolean inOrder;
   int dbOffset;
   int dbOffsetPacked;
   boolean packed;
   int[] bitMasks;
   int[] bitOffsets;
   private int maxX;
   private int maxY;

   public ByteInterleavedRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (ByteInterleavedRaster)null);
   }

   public ByteInterleavedRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (ByteInterleavedRaster)null);
   }

   private boolean isInterleaved(ComponentSampleModel var1) {
      int var2 = this.sampleModel.getNumBands();
      if (var2 == 1) {
         return true;
      } else {
         int[] var3 = var1.getBankIndices();

         for(int var4 = 0; var4 < var2; ++var4) {
            if (var3[var4] != 0) {
               return false;
            }
         }

         int[] var9 = var1.getBandOffsets();
         int var5 = var9[0];
         int var6 = var5;

         for(int var7 = 1; var7 < var2; ++var7) {
            int var8 = var9[var7];
            if (var8 < var5) {
               var5 = var8;
            }

            if (var8 > var6) {
               var6 = var8;
            }
         }

         if (var6 - var5 >= var1.getPixelStride()) {
            return false;
         } else {
            return true;
         }
      }
   }

   public ByteInterleavedRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, ByteInterleavedRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.packed = false;
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferByte)) {
         throw new RasterFormatException("ByteInterleavedRasters must have byte DataBuffers");
      } else {
         DataBufferByte var6 = (DataBufferByte)var2;
         this.data = stealData(var6, 0);
         int var7 = var3.x - var4.x;
         int var8 = var3.y - var4.y;
         int[] var10000;
         if (!(var1 instanceof PixelInterleavedSampleModel) && (!(var1 instanceof ComponentSampleModel) || !this.isInterleaved((ComponentSampleModel)var1))) {
            if (!(var1 instanceof SinglePixelPackedSampleModel)) {
               throw new RasterFormatException("ByteInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or interleaved ComponentSampleModel.  Sample model is " + var1);
            }

            SinglePixelPackedSampleModel var11 = (SinglePixelPackedSampleModel)var1;
            this.packed = true;
            this.bitMasks = var11.getBitMasks();
            this.bitOffsets = var11.getBitOffsets();
            this.scanlineStride = var11.getScanlineStride();
            this.pixelStride = 1;
            this.dataOffsets = new int[1];
            this.dataOffsets[0] = var6.getOffset();
            var10000 = this.dataOffsets;
            var10000[0] += var7 * this.pixelStride + var8 * this.scanlineStride;
         } else {
            ComponentSampleModel var9 = (ComponentSampleModel)var1;
            this.scanlineStride = var9.getScanlineStride();
            this.pixelStride = var9.getPixelStride();
            this.dataOffsets = var9.getBandOffsets();

            for(int var10 = 0; var10 < this.getNumDataElements(); ++var10) {
               var10000 = this.dataOffsets;
               var10000[var10] += var7 * this.pixelStride + var8 * this.scanlineStride;
            }
         }

         this.bandOffset = this.dataOffsets[0];
         this.dbOffsetPacked = var2.getOffset() - this.sampleModelTranslateY * this.scanlineStride - this.sampleModelTranslateX * this.pixelStride;
         this.dbOffset = this.dbOffsetPacked - (var7 * this.pixelStride + var8 * this.scanlineStride);
         this.inOrder = false;
         if (this.numDataElements == this.pixelStride) {
            this.inOrder = true;

            for(int var12 = 1; var12 < this.numDataElements; ++var12) {
               if (this.dataOffsets[var12] - this.dataOffsets[0] != var12) {
                  this.inOrder = false;
                  break;
               }
            }
         }

         this.verify();
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

   public byte[] getDataStorage() {
      return this.data;
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         byte[] var4;
         if (var3 == null) {
            var4 = new byte[this.numDataElements];
         } else {
            var4 = (byte[])((byte[])var3);
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
      return this.getByteData(var1, var2, var3, var4, (byte[])((byte[])var5));
   }

   public byte[] getByteData(int var1, int var2, int var3, int var4, int var5, byte[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var6 == null) {
            var6 = new byte[var3 * var4];
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

   public byte[] getByteData(int var1, int var2, int var3, int var4, byte[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var5 == null) {
            var5 = new byte[this.numDataElements * var3 * var4];
         }

         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var8 = 0;
         int var10;
         int var11;
         if (this.inOrder) {
            var6 += this.dataOffsets[0];
            var11 = var3 * this.pixelStride;
            if (this.scanlineStride == var11) {
               System.arraycopy(this.data, var6, var5, var8, var11 * var4);
            } else {
               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  System.arraycopy(this.data, var6, var5, var8, var11);
                  var8 += var11;
                  ++var10;
               }
            }
         } else {
            int var7;
            int var9;
            if (this.numDataElements == 1) {
               var6 += this.dataOffsets[0];

               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  var7 = var6;

                  for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                     var5[var8++] = this.data[var7];
                     ++var9;
                  }

                  ++var10;
               }
            } else if (this.numDataElements == 2) {
               var6 += this.dataOffsets[0];
               var11 = this.dataOffsets[1] - this.dataOffsets[0];

               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  var7 = var6;

                  for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                     var5[var8++] = this.data[var7];
                     var5[var8++] = this.data[var7 + var11];
                     ++var9;
                  }

                  ++var10;
               }
            } else {
               int var12;
               if (this.numDataElements == 3) {
                  var6 += this.dataOffsets[0];
                  var11 = this.dataOffsets[1] - this.dataOffsets[0];
                  var12 = this.dataOffsets[2] - this.dataOffsets[0];

                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        var5[var8++] = this.data[var7];
                        var5[var8++] = this.data[var7 + var11];
                        var5[var8++] = this.data[var7 + var12];
                        ++var9;
                     }

                     ++var10;
                  }
               } else if (this.numDataElements == 4) {
                  var6 += this.dataOffsets[0];
                  var11 = this.dataOffsets[1] - this.dataOffsets[0];
                  var12 = this.dataOffsets[2] - this.dataOffsets[0];
                  int var13 = this.dataOffsets[3] - this.dataOffsets[0];

                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        var5[var8++] = this.data[var7];
                        var5[var8++] = this.data[var7 + var11];
                        var5[var8++] = this.data[var7 + var12];
                        var5[var8++] = this.data[var7 + var13];
                        ++var9;
                     }

                     ++var10;
                  }
               } else {
                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        for(var11 = 0; var11 < this.numDataElements; ++var11) {
                           var5[var8++] = this.data[this.dataOffsets[var11] + var7];
                        }

                        ++var9;
                     }

                     ++var10;
                  }
               }
            }
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         byte[] var4 = (byte[])((byte[])var3);
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
      int var4 = var3.getMinX();
      int var5 = var3.getMinY();
      int var6 = var1 + var4;
      int var7 = var2 + var5;
      int var8 = var3.getWidth();
      int var9 = var3.getHeight();
      if (var6 >= this.minX && var7 >= this.minY && var6 + var8 <= this.maxX && var7 + var9 <= this.maxY) {
         this.setDataElements(var6, var7, var4, var5, var8, var9, var3);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   private void setDataElements(int var1, int var2, int var3, int var4, int var5, int var6, Raster var7) {
      if (var5 > 0 && var6 > 0) {
         int var8 = var7.getMinX();
         int var9 = var7.getMinY();
         Object var10 = null;
         if (var7 instanceof ByteInterleavedRaster) {
            ByteInterleavedRaster var11 = (ByteInterleavedRaster)var7;
            byte[] var12 = var11.getDataStorage();
            if (this.inOrder && var11.inOrder && this.pixelStride == var11.pixelStride) {
               int var13 = var11.getDataOffset(0);
               int var14 = var11.getScanlineStride();
               int var15 = var11.getPixelStride();
               int var16 = var13 + (var4 - var9) * var14 + (var3 - var8) * var15;
               int var17 = this.dataOffsets[0] + (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
               int var18 = var5 * this.pixelStride;

               for(int var19 = 0; var19 < var6; ++var19) {
                  System.arraycopy(var12, var16, this.data, var17, var18);
                  var16 += var14;
                  var17 += this.scanlineStride;
               }

               this.markDirty();
               return;
            }
         }

         for(int var20 = 0; var20 < var6; ++var20) {
            var10 = var7.getDataElements(var8, var9 + var20, var5, 1, var10);
            this.setDataElements(var1, var2 + var20, var5, 1, var10);
         }

      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      this.putByteData(var1, var2, var3, var4, (byte[])((byte[])var5));
   }

   public void putByteData(int var1, int var2, int var3, int var4, int var5, byte[] var6) {
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

   public void putByteData(int var1, int var2, int var3, int var4, byte[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var6 = (var2 - this.minY) * this.scanlineStride + (var1 - this.minX) * this.pixelStride;
         int var8 = 0;
         int var10;
         int var11;
         if (this.inOrder) {
            var6 += this.dataOffsets[0];
            var11 = var3 * this.pixelStride;
            if (var11 == this.scanlineStride) {
               System.arraycopy(var5, 0, this.data, var6, var11 * var4);
            } else {
               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  System.arraycopy(var5, var8, this.data, var6, var11);
                  var8 += var11;
                  ++var10;
               }
            }
         } else {
            int var7;
            int var9;
            if (this.numDataElements == 1) {
               var6 += this.dataOffsets[0];

               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  var7 = var6;

                  for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                     this.data[var7] = var5[var8++];
                     ++var9;
                  }

                  ++var10;
               }
            } else if (this.numDataElements == 2) {
               var6 += this.dataOffsets[0];
               var11 = this.dataOffsets[1] - this.dataOffsets[0];

               for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                  var7 = var6;

                  for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                     this.data[var7] = var5[var8++];
                     this.data[var7 + var11] = var5[var8++];
                     ++var9;
                  }

                  ++var10;
               }
            } else {
               int var12;
               if (this.numDataElements == 3) {
                  var6 += this.dataOffsets[0];
                  var11 = this.dataOffsets[1] - this.dataOffsets[0];
                  var12 = this.dataOffsets[2] - this.dataOffsets[0];

                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        this.data[var7] = var5[var8++];
                        this.data[var7 + var11] = var5[var8++];
                        this.data[var7 + var12] = var5[var8++];
                        ++var9;
                     }

                     ++var10;
                  }
               } else if (this.numDataElements == 4) {
                  var6 += this.dataOffsets[0];
                  var11 = this.dataOffsets[1] - this.dataOffsets[0];
                  var12 = this.dataOffsets[2] - this.dataOffsets[0];
                  int var13 = this.dataOffsets[3] - this.dataOffsets[0];

                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        this.data[var7] = var5[var8++];
                        this.data[var7 + var11] = var5[var8++];
                        this.data[var7 + var12] = var5[var8++];
                        this.data[var7 + var13] = var5[var8++];
                        ++var9;
                     }

                     ++var10;
                  }
               } else {
                  for(var10 = 0; var10 < var4; var6 += this.scanlineStride) {
                     var7 = var6;

                     for(var9 = 0; var9 < var3; var7 += this.pixelStride) {
                        for(var11 = 0; var11 < this.numDataElements; ++var11) {
                           this.data[this.dataOffsets[var11] + var7] = var5[var8++];
                        }

                        ++var9;
                     }

                     ++var10;
                  }
               }
            }
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int getSample(int var1, int var2, int var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         int var4;
         if (this.packed) {
            var4 = var2 * this.scanlineStride + var1 + this.dbOffsetPacked;
            byte var5 = this.data[var4];
            return (var5 & this.bitMasks[var3]) >>> this.bitOffsets[var3];
         } else {
            var4 = var2 * this.scanlineStride + var1 * this.pixelStride + this.dbOffset;
            return this.data[var4 + this.dataOffsets[var3]] & 255;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, int var4) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         int var5;
         if (this.packed) {
            var5 = var2 * this.scanlineStride + var1 + this.dbOffsetPacked;
            int var6 = this.bitMasks[var3];
            byte var7 = this.data[var5];
            var7 = (byte)(var7 & ~var6);
            var7 = (byte)(var7 | var4 << this.bitOffsets[var3] & var6);
            this.data[var5] = var7;
         } else {
            var5 = var2 * this.scanlineStride + var1 * this.pixelStride + this.dbOffset;
            this.data[var5 + this.dataOffsets[var3]] = (byte)var4;
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getSamples(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int[] var7;
         if (var6 != null) {
            var7 = var6;
         } else {
            var7 = new int[var3 * var4];
         }

         int var8 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var9 = 0;
         int var10;
         int var11;
         int var12;
         if (this.packed) {
            var8 += this.dbOffsetPacked;
            var10 = this.bitMasks[var5];
            var11 = this.bitOffsets[var5];

            for(var12 = 0; var12 < var4; ++var12) {
               int var13 = var8;

               for(int var14 = 0; var14 < var3; ++var14) {
                  byte var15 = this.data[var13++];
                  var7[var9++] = (var15 & var10) >>> var11;
               }

               var8 += this.scanlineStride;
            }
         } else {
            var8 += this.dbOffset + this.dataOffsets[var5];

            for(var10 = 0; var10 < var4; ++var10) {
               var11 = var8;

               for(var12 = 0; var12 < var3; ++var12) {
                  var7[var9++] = this.data[var11] & 255;
                  var11 += this.pixelStride;
               }

               var8 += this.scanlineStride;
            }
         }

         return var7;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var7 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var8 = 0;
         int var9;
         int var10;
         int var11;
         if (this.packed) {
            var7 += this.dbOffsetPacked;
            var9 = this.bitMasks[var5];

            for(var10 = 0; var10 < var4; ++var10) {
               var11 = var7;

               for(int var12 = 0; var12 < var3; ++var12) {
                  byte var13 = this.data[var11];
                  var13 = (byte)(var13 & ~var9);
                  int var14 = var6[var8++];
                  var13 = (byte)(var13 | var14 << this.bitOffsets[var5] & var9);
                  this.data[var11++] = var13;
               }

               var7 += this.scanlineStride;
            }
         } else {
            var7 += this.dbOffset + this.dataOffsets[var5];

            for(var9 = 0; var9 < var4; ++var9) {
               var10 = var7;

               for(var11 = 0; var11 < var3; ++var11) {
                  this.data[var10] = (byte)var6[var8++];
                  var10 += this.pixelStride;
               }

               var7 += this.scanlineStride;
            }
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int[] var6;
         if (var5 != null) {
            var6 = var5;
         } else {
            var6 = new int[var3 * var4 * this.numBands];
         }

         int var7 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var8 = 0;
         int var9;
         int var10;
         int var12;
         if (this.packed) {
            var7 += this.dbOffsetPacked;

            for(var9 = 0; var9 < var4; ++var9) {
               for(var10 = 0; var10 < var3; ++var10) {
                  byte var11 = this.data[var7 + var10];

                  for(var12 = 0; var12 < this.numBands; ++var12) {
                     var6[var8++] = (var11 & this.bitMasks[var12]) >>> this.bitOffsets[var12];
                  }
               }

               var7 += this.scanlineStride;
            }
         } else {
            var7 += this.dbOffset;
            var9 = this.dataOffsets[0];
            int var16;
            if (this.numBands == 1) {
               for(var10 = 0; var10 < var4; ++var10) {
                  var16 = var7 + var9;

                  for(var12 = 0; var12 < var3; ++var12) {
                     var6[var8++] = this.data[var16] & 255;
                     var16 += this.pixelStride;
                  }

                  var7 += this.scanlineStride;
               }
            } else {
               int var13;
               if (this.numBands == 2) {
                  var10 = this.dataOffsets[1] - var9;

                  for(var16 = 0; var16 < var4; ++var16) {
                     var12 = var7 + var9;

                     for(var13 = 0; var13 < var3; ++var13) {
                        var6[var8++] = this.data[var12] & 255;
                        var6[var8++] = this.data[var12 + var10] & 255;
                        var12 += this.pixelStride;
                     }

                     var7 += this.scanlineStride;
                  }
               } else {
                  int var14;
                  if (this.numBands == 3) {
                     var10 = this.dataOffsets[1] - var9;
                     var16 = this.dataOffsets[2] - var9;

                     for(var12 = 0; var12 < var4; ++var12) {
                        var13 = var7 + var9;

                        for(var14 = 0; var14 < var3; ++var14) {
                           var6[var8++] = this.data[var13] & 255;
                           var6[var8++] = this.data[var13 + var10] & 255;
                           var6[var8++] = this.data[var13 + var16] & 255;
                           var13 += this.pixelStride;
                        }

                        var7 += this.scanlineStride;
                     }
                  } else if (this.numBands == 4) {
                     var10 = this.dataOffsets[1] - var9;
                     var16 = this.dataOffsets[2] - var9;
                     var12 = this.dataOffsets[3] - var9;

                     for(var13 = 0; var13 < var4; ++var13) {
                        var14 = var7 + var9;

                        for(int var15 = 0; var15 < var3; ++var15) {
                           var6[var8++] = this.data[var14] & 255;
                           var6[var8++] = this.data[var14 + var10] & 255;
                           var6[var8++] = this.data[var14 + var16] & 255;
                           var6[var8++] = this.data[var14 + var12] & 255;
                           var14 += this.pixelStride;
                        }

                        var7 += this.scanlineStride;
                     }
                  } else {
                     for(var10 = 0; var10 < var4; ++var10) {
                        var16 = var7;

                        for(var12 = 0; var12 < var3; ++var12) {
                           for(var13 = 0; var13 < this.numBands; ++var13) {
                              var6[var8++] = this.data[var16 + this.dataOffsets[var13]] & 255;
                           }

                           var16 += this.pixelStride;
                        }

                        var7 += this.scanlineStride;
                     }
                  }
               }
            }
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var6 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var7 = 0;
         int var8;
         int var9;
         int var10;
         int var11;
         int var12;
         if (this.packed) {
            var6 += this.dbOffsetPacked;

            for(var8 = 0; var8 < var4; ++var8) {
               for(var9 = 0; var9 < var3; ++var9) {
                  var10 = 0;

                  for(var11 = 0; var11 < this.numBands; ++var11) {
                     var12 = var5[var7++];
                     var10 |= var12 << this.bitOffsets[var11] & this.bitMasks[var11];
                  }

                  this.data[var6 + var9] = (byte)var10;
               }

               var6 += this.scanlineStride;
            }
         } else {
            var6 += this.dbOffset;
            var8 = this.dataOffsets[0];
            if (this.numBands == 1) {
               for(var9 = 0; var9 < var4; ++var9) {
                  var10 = var6 + var8;

                  for(var11 = 0; var11 < var3; ++var11) {
                     this.data[var10] = (byte)var5[var7++];
                     var10 += this.pixelStride;
                  }

                  var6 += this.scanlineStride;
               }
            } else if (this.numBands == 2) {
               var9 = this.dataOffsets[1] - var8;

               for(var10 = 0; var10 < var4; ++var10) {
                  var11 = var6 + var8;

                  for(var12 = 0; var12 < var3; ++var12) {
                     this.data[var11] = (byte)var5[var7++];
                     this.data[var11 + var9] = (byte)var5[var7++];
                     var11 += this.pixelStride;
                  }

                  var6 += this.scanlineStride;
               }
            } else {
               int var13;
               if (this.numBands == 3) {
                  var9 = this.dataOffsets[1] - var8;
                  var10 = this.dataOffsets[2] - var8;

                  for(var11 = 0; var11 < var4; ++var11) {
                     var12 = var6 + var8;

                     for(var13 = 0; var13 < var3; ++var13) {
                        this.data[var12] = (byte)var5[var7++];
                        this.data[var12 + var9] = (byte)var5[var7++];
                        this.data[var12 + var10] = (byte)var5[var7++];
                        var12 += this.pixelStride;
                     }

                     var6 += this.scanlineStride;
                  }
               } else if (this.numBands == 4) {
                  var9 = this.dataOffsets[1] - var8;
                  var10 = this.dataOffsets[2] - var8;
                  var11 = this.dataOffsets[3] - var8;

                  for(var12 = 0; var12 < var4; ++var12) {
                     var13 = var6 + var8;

                     for(int var14 = 0; var14 < var3; ++var14) {
                        this.data[var13] = (byte)var5[var7++];
                        this.data[var13 + var9] = (byte)var5[var7++];
                        this.data[var13 + var10] = (byte)var5[var7++];
                        this.data[var13 + var11] = (byte)var5[var7++];
                        var13 += this.pixelStride;
                     }

                     var6 += this.scanlineStride;
                  }
               } else {
                  for(var9 = 0; var9 < var4; ++var9) {
                     var10 = var6;

                     for(var11 = 0; var11 < var3; ++var11) {
                        for(var12 = 0; var12 < this.numBands; ++var12) {
                           this.data[var10 + this.dataOffsets[var12]] = (byte)var5[var7++];
                        }

                        var10 += this.pixelStride;
                     }

                     var6 += this.scanlineStride;
                  }
               }
            }
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setRect(int var1, int var2, Raster var3) {
      if (!(var3 instanceof ByteInterleavedRaster)) {
         super.setRect(var1, var2, var3);
      } else {
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

         if (var8 + var4 > this.maxX) {
            var4 = this.maxX - var8;
         }

         if (var9 + var5 > this.maxY) {
            var5 = this.maxY - var9;
         }

         this.setDataElements(var8, var9, var6, var7, var4, var5, var3);
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
            return new ByteInterleavedRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
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
         return new ByteInterleavedRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   public String toString() {
      return new String("ByteInterleavedRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements + " dataOff[0] = " + this.dataOffsets[0]);
   }
}
