package java.awt.image;

public class MultiPixelPackedSampleModel extends SampleModel {
   int pixelBitStride;
   int bitMask;
   int pixelsPerDataElement;
   int dataElementSize;
   int dataBitOffset;
   int scanlineStride;

   public MultiPixelPackedSampleModel(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (var2 * var4 + DataBuffer.getDataTypeSize(var1) - 1) / DataBuffer.getDataTypeSize(var1), 0);
      if (var1 != 0 && var1 != 1 && var1 != 3) {
         throw new IllegalArgumentException("Unsupported data type " + var1);
      }
   }

   public MultiPixelPackedSampleModel(int var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, 1);
      if (var1 != 0 && var1 != 1 && var1 != 3) {
         throw new IllegalArgumentException("Unsupported data type " + var1);
      } else {
         this.dataType = var1;
         this.pixelBitStride = var4;
         this.scanlineStride = var5;
         this.dataBitOffset = var6;
         this.dataElementSize = DataBuffer.getDataTypeSize(var1);
         this.pixelsPerDataElement = this.dataElementSize / var4;
         if (this.pixelsPerDataElement * var4 != this.dataElementSize) {
            throw new RasterFormatException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries");
         } else {
            this.bitMask = (1 << var4) - 1;
         }
      }
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      MultiPixelPackedSampleModel var3 = new MultiPixelPackedSampleModel(this.dataType, var1, var2, this.pixelBitStride);
      return var3;
   }

   public DataBuffer createDataBuffer() {
      Object var1 = null;
      int var2 = this.scanlineStride * this.height;
      switch(this.dataType) {
      case 0:
         var1 = new DataBufferByte(var2 + (this.dataBitOffset + 7) / 8);
         break;
      case 1:
         var1 = new DataBufferUShort(var2 + (this.dataBitOffset + 15) / 16);
      case 2:
      default:
         break;
      case 3:
         var1 = new DataBufferInt(var2 + (this.dataBitOffset + 31) / 32);
      }

      return (DataBuffer)var1;
   }

   public int getNumDataElements() {
      return 1;
   }

   public int[] getSampleSize() {
      int[] var1 = new int[]{this.pixelBitStride};
      return var1;
   }

   public int getSampleSize(int var1) {
      return this.pixelBitStride;
   }

   public int getOffset(int var1, int var2) {
      int var3 = var2 * this.scanlineStride;
      var3 += (var1 * this.pixelBitStride + this.dataBitOffset) / this.dataElementSize;
      return var3;
   }

   public int getBitOffset(int var1) {
      return (var1 * this.pixelBitStride + this.dataBitOffset) % this.dataElementSize;
   }

   public int getScanlineStride() {
      return this.scanlineStride;
   }

   public int getPixelBitStride() {
      return this.pixelBitStride;
   }

   public int getDataBitOffset() {
      return this.dataBitOffset;
   }

   public int getTransferType() {
      if (this.pixelBitStride > 16) {
         return 3;
      } else {
         return this.pixelBitStride > 8 ? 1 : 0;
      }
   }

   public SampleModel createSubsetSampleModel(int[] var1) {
      if (var1 != null && var1.length != 1) {
         throw new RasterFormatException("MultiPixelPackedSampleModel has only one band.");
      } else {
         SampleModel var2 = this.createCompatibleSampleModel(this.width, this.height);
         return var2;
      }
   }

   public int getSample(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height && var3 == 0) {
         int var5 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var6 = var4.getElem(var2 * this.scanlineStride + var5 / this.dataElementSize);
         int var7 = this.dataElementSize - (var5 & this.dataElementSize - 1) - this.pixelBitStride;
         return var6 >> var7 & this.bitMask;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, int var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height && var3 == 0) {
         int var6 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var7 = var2 * this.scanlineStride + var6 / this.dataElementSize;
         int var8 = this.dataElementSize - (var6 & this.dataElementSize - 1) - this.pixelBitStride;
         int var9 = var5.getElem(var7);
         var9 &= ~(this.bitMask << var8);
         var9 |= (var4 & this.bitMask) << var8;
         var5.setElem(var7, var9);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Object getDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         int var6 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var7 = this.dataElementSize - (var6 & this.dataElementSize - 1) - this.pixelBitStride;
         boolean var8 = false;
         int var12;
         switch(var5) {
         case 0:
            byte[] var9;
            if (var3 == null) {
               var9 = new byte[1];
            } else {
               var9 = (byte[])((byte[])var3);
            }

            var12 = var4.getElem(var2 * this.scanlineStride + var6 / this.dataElementSize);
            var9[0] = (byte)(var12 >> var7 & this.bitMask);
            var3 = var9;
            break;
         case 1:
            short[] var10;
            if (var3 == null) {
               var10 = new short[1];
            } else {
               var10 = (short[])((short[])var3);
            }

            var12 = var4.getElem(var2 * this.scanlineStride + var6 / this.dataElementSize);
            var10[0] = (short)(var12 >> var7 & this.bitMask);
            var3 = var10;
         case 2:
         default:
            break;
         case 3:
            int[] var11;
            if (var3 == null) {
               var11 = new int[1];
            } else {
               var11 = (int[])((int[])var3);
            }

            var12 = var4.getElem(var2 * this.scanlineStride + var6 / this.dataElementSize);
            var11[0] = var12 >> var7 & this.bitMask;
            var3 = var11;
         }

         return var3;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int[] var5;
         if (var3 != null) {
            var5 = var3;
         } else {
            var5 = new int[this.numBands];
         }

         int var6 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var7 = var4.getElem(var2 * this.scanlineStride + var6 / this.dataElementSize);
         int var8 = this.dataElementSize - (var6 & this.dataElementSize - 1) - this.pixelBitStride;
         var5[0] = var7 >> var8 & this.bitMask;
         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         int var6 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var7 = var2 * this.scanlineStride + var6 / this.dataElementSize;
         int var8 = this.dataElementSize - (var6 & this.dataElementSize - 1) - this.pixelBitStride;
         int var9 = var4.getElem(var7);
         var9 &= ~(this.bitMask << var8);
         switch(var5) {
         case 0:
            byte[] var10 = (byte[])((byte[])var3);
            var9 |= (var10[0] & 255 & this.bitMask) << var8;
            var4.setElem(var7, var9);
            break;
         case 1:
            short[] var11 = (short[])((short[])var3);
            var9 |= (var11[0] & '\uffff' & this.bitMask) << var8;
            var4.setElem(var7, var9);
         case 2:
         default:
            break;
         case 3:
            int[] var12 = (int[])((int[])var3);
            var9 |= (var12[0] & this.bitMask) << var8;
            var4.setElem(var7, var9);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.dataBitOffset + var1 * this.pixelBitStride;
         int var6 = var2 * this.scanlineStride + var5 / this.dataElementSize;
         int var7 = this.dataElementSize - (var5 & this.dataElementSize - 1) - this.pixelBitStride;
         int var8 = var4.getElem(var6);
         var8 &= ~(this.bitMask << var7);
         var8 |= (var3[0] & this.bitMask) << var7;
         var4.setElem(var6, var8);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel var2 = (MultiPixelPackedSampleModel)var1;
         return this.width == var2.width && this.height == var2.height && this.numBands == var2.numBands && this.dataType == var2.dataType && this.pixelBitStride == var2.pixelBitStride && this.bitMask == var2.bitMask && this.pixelsPerDataElement == var2.pixelsPerDataElement && this.dataElementSize == var2.dataElementSize && this.dataBitOffset == var2.dataBitOffset && this.scanlineStride == var2.scanlineStride;
      } else {
         return false;
      }
   }

   public int hashCode() {
      boolean var1 = false;
      int var2 = this.width;
      var2 <<= 8;
      var2 ^= this.height;
      var2 <<= 8;
      var2 ^= this.numBands;
      var2 <<= 8;
      var2 ^= this.dataType;
      var2 <<= 8;
      var2 ^= this.pixelBitStride;
      var2 <<= 8;
      var2 ^= this.bitMask;
      var2 <<= 8;
      var2 ^= this.pixelsPerDataElement;
      var2 <<= 8;
      var2 ^= this.dataElementSize;
      var2 <<= 8;
      var2 ^= this.dataBitOffset;
      var2 <<= 8;
      var2 ^= this.scanlineStride;
      return var2;
   }
}
