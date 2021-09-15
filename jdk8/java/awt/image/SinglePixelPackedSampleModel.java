package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel extends SampleModel {
   private int[] bitMasks;
   private int[] bitOffsets;
   private int[] bitSizes;
   private int maxBitSize;
   private int scanlineStride;

   private static native void initIDs();

   public SinglePixelPackedSampleModel(int var1, int var2, int var3, int[] var4) {
      this(var1, var2, var3, var2, var4);
      if (var1 != 0 && var1 != 1 && var1 != 3) {
         throw new IllegalArgumentException("Unsupported data type " + var1);
      }
   }

   public SinglePixelPackedSampleModel(int var1, int var2, int var3, int var4, int[] var5) {
      super(var1, var2, var3, var5.length);
      if (var1 != 0 && var1 != 1 && var1 != 3) {
         throw new IllegalArgumentException("Unsupported data type " + var1);
      } else {
         this.dataType = var1;
         this.bitMasks = (int[])((int[])var5.clone());
         this.scanlineStride = var4;
         this.bitOffsets = new int[this.numBands];
         this.bitSizes = new int[this.numBands];
         int var6 = (int)((1L << DataBuffer.getDataTypeSize(var1)) - 1L);
         this.maxBitSize = 0;

         for(int var7 = 0; var7 < this.numBands; ++var7) {
            int var8 = 0;
            int var9 = 0;
            int[] var10000 = this.bitMasks;
            var10000[var7] &= var6;
            int var10 = this.bitMasks[var7];
            if (var10 != 0) {
               while((var10 & 1) == 0) {
                  var10 >>>= 1;
                  ++var8;
               }

               while((var10 & 1) == 1) {
                  var10 >>>= 1;
                  ++var9;
               }

               if (var10 != 0) {
                  throw new IllegalArgumentException("Mask " + var5[var7] + " must be contiguous");
               }
            }

            this.bitOffsets[var7] = var8;
            this.bitSizes[var7] = var9;
            if (var9 > this.maxBitSize) {
               this.maxBitSize = var9;
            }
         }

      }
   }

   public int getNumDataElements() {
      return 1;
   }

   private long getBufferSize() {
      long var1 = (long)(this.scanlineStride * (this.height - 1) + this.width);
      return var1;
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      SinglePixelPackedSampleModel var3 = new SinglePixelPackedSampleModel(this.dataType, var1, var2, this.bitMasks);
      return var3;
   }

   public DataBuffer createDataBuffer() {
      Object var1 = null;
      int var2 = (int)this.getBufferSize();
      switch(this.dataType) {
      case 0:
         var1 = new DataBufferByte(var2);
         break;
      case 1:
         var1 = new DataBufferUShort(var2);
      case 2:
      default:
         break;
      case 3:
         var1 = new DataBufferInt(var2);
      }

      return (DataBuffer)var1;
   }

   public int[] getSampleSize() {
      return (int[])this.bitSizes.clone();
   }

   public int getSampleSize(int var1) {
      return this.bitSizes[var1];
   }

   public int getOffset(int var1, int var2) {
      int var3 = var2 * this.scanlineStride + var1;
      return var3;
   }

   public int[] getBitOffsets() {
      return (int[])((int[])this.bitOffsets.clone());
   }

   public int[] getBitMasks() {
      return (int[])((int[])this.bitMasks.clone());
   }

   public int getScanlineStride() {
      return this.scanlineStride;
   }

   public SampleModel createSubsetSampleModel(int[] var1) {
      if (var1.length > this.numBands) {
         throw new RasterFormatException("There are only " + this.numBands + " bands");
      } else {
         int[] var2 = new int[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = this.bitMasks[var1[var3]];
         }

         return new SinglePixelPackedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, var2);
      }
   }

   public Object getDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         switch(var5) {
         case 0:
            byte[] var6;
            if (var3 == null) {
               var6 = new byte[1];
            } else {
               var6 = (byte[])((byte[])var3);
            }

            var6[0] = (byte)var4.getElem(var2 * this.scanlineStride + var1);
            var3 = var6;
            break;
         case 1:
            short[] var7;
            if (var3 == null) {
               var7 = new short[1];
            } else {
               var7 = (short[])((short[])var3);
            }

            var7[0] = (short)var4.getElem(var2 * this.scanlineStride + var1);
            var3 = var7;
         case 2:
         default:
            break;
         case 3:
            int[] var8;
            if (var3 == null) {
               var8 = new int[1];
            } else {
               var8 = (int[])((int[])var3);
            }

            var8[0] = var4.getElem(var2 * this.scanlineStride + var1);
            var3 = var8;
         }

         return var3;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int[] var5;
         if (var3 == null) {
            var5 = new int[this.numBands];
         } else {
            var5 = var3;
         }

         int var6 = var4.getElem(var2 * this.scanlineStride + var1);

         for(int var7 = 0; var7 < this.numBands; ++var7) {
            var5[var7] = (var6 & this.bitMasks[var7]) >>> this.bitOffsets[var7];
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var7 = var1 + var3;
      int var8 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var7 >= 0 && var7 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var8 >= 0 && var8 <= this.height) {
         int[] var9;
         if (var5 != null) {
            var9 = var5;
         } else {
            var9 = new int[var3 * var4 * this.numBands];
         }

         int var10 = var2 * this.scanlineStride + var1;
         int var11 = 0;

         for(int var12 = 0; var12 < var4; ++var12) {
            for(int var13 = 0; var13 < var3; ++var13) {
               int var14 = var6.getElem(var10 + var13);

               for(int var15 = 0; var15 < this.numBands; ++var15) {
                  var9[var11++] = (var14 & this.bitMasks[var15]) >>> this.bitOffsets[var15];
               }
            }

            var10 += this.scanlineStride;
         }

         return var9;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int getSample(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = var4.getElem(var2 * this.scanlineStride + var1);
         return (var5 & this.bitMasks[var3]) >>> this.bitOffsets[var3];
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.width && var2 + var4 <= this.height) {
         int[] var8;
         if (var6 != null) {
            var8 = var6;
         } else {
            var8 = new int[var3 * var4];
         }

         int var9 = var2 * this.scanlineStride + var1;
         int var10 = 0;

         for(int var11 = 0; var11 < var4; ++var11) {
            for(int var12 = 0; var12 < var3; ++var12) {
               int var13 = var7.getElem(var9 + var12);
               var8[var10++] = (var13 & this.bitMasks[var5]) >>> this.bitOffsets[var5];
            }

            var9 += this.scanlineStride;
         }

         return var8;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         switch(var5) {
         case 0:
            byte[] var6 = (byte[])((byte[])var3);
            var4.setElem(var2 * this.scanlineStride + var1, var6[0] & 255);
            break;
         case 1:
            short[] var7 = (short[])((short[])var3);
            var4.setElem(var2 * this.scanlineStride + var1, var7[0] & '\uffff');
         case 2:
         default:
            break;
         case 3:
            int[] var8 = (int[])((int[])var3);
            var4.setElem(var2 * this.scanlineStride + var1, var8[0]);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = var2 * this.scanlineStride + var1;
         int var6 = var4.getElem(var5);

         for(int var7 = 0; var7 < this.numBands; ++var7) {
            var6 &= ~this.bitMasks[var7];
            var6 |= var3[var7] << this.bitOffsets[var7] & this.bitMasks[var7];
         }

         var4.setElem(var5, var6);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var7 = var1 + var3;
      int var8 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var7 >= 0 && var7 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var8 >= 0 && var8 <= this.height) {
         int var9 = var2 * this.scanlineStride + var1;
         int var10 = 0;

         for(int var11 = 0; var11 < var4; ++var11) {
            for(int var12 = 0; var12 < var3; ++var12) {
               int var13 = var6.getElem(var9 + var12);

               for(int var14 = 0; var14 < this.numBands; ++var14) {
                  var13 &= ~this.bitMasks[var14];
                  int var15 = var5[var10++];
                  var13 |= var15 << this.bitOffsets[var14] & this.bitMasks[var14];
               }

               var6.setElem(var9 + var12, var13);
            }

            var9 += this.scanlineStride;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, int var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var6 = var5.getElem(var2 * this.scanlineStride + var1);
         var6 &= ~this.bitMasks[var3];
         var6 |= var4 << this.bitOffsets[var3] & this.bitMasks[var3];
         var5.setElem(var2 * this.scanlineStride + var1, var6);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.width && var2 + var4 <= this.height) {
         int var8 = var2 * this.scanlineStride + var1;
         int var9 = 0;

         for(int var10 = 0; var10 < var4; ++var10) {
            for(int var11 = 0; var11 < var3; ++var11) {
               int var12 = var7.getElem(var8 + var11);
               var12 &= ~this.bitMasks[var5];
               int var13 = var6[var9++];
               var12 |= var13 << this.bitOffsets[var5] & this.bitMasks[var5];
               var7.setElem(var8 + var11, var12);
            }

            var8 += this.scanlineStride;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof SinglePixelPackedSampleModel) {
         SinglePixelPackedSampleModel var2 = (SinglePixelPackedSampleModel)var1;
         return this.width == var2.width && this.height == var2.height && this.numBands == var2.numBands && this.dataType == var2.dataType && Arrays.equals(this.bitMasks, var2.bitMasks) && Arrays.equals(this.bitOffsets, var2.bitOffsets) && Arrays.equals(this.bitSizes, var2.bitSizes) && this.maxBitSize == var2.maxBitSize && this.scanlineStride == var2.scanlineStride;
      } else {
         return false;
      }
   }

   public int hashCode() {
      boolean var1 = false;
      int var3 = this.width;
      var3 <<= 8;
      var3 ^= this.height;
      var3 <<= 8;
      var3 ^= this.numBands;
      var3 <<= 8;
      var3 ^= this.dataType;
      var3 <<= 8;

      int var2;
      for(var2 = 0; var2 < this.bitMasks.length; ++var2) {
         var3 ^= this.bitMasks[var2];
         var3 <<= 8;
      }

      for(var2 = 0; var2 < this.bitOffsets.length; ++var2) {
         var3 ^= this.bitOffsets[var2];
         var3 <<= 8;
      }

      for(var2 = 0; var2 < this.bitSizes.length; ++var2) {
         var3 ^= this.bitSizes[var2];
         var3 <<= 8;
      }

      var3 ^= this.maxBitSize;
      var3 <<= 8;
      var3 ^= this.scanlineStride;
      return var3;
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
