package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel extends SampleModel {
   protected int[] bandOffsets;
   protected int[] bankIndices;
   protected int numBands = 1;
   protected int numBanks = 1;
   protected int scanlineStride;
   protected int pixelStride;

   private static native void initIDs();

   public ComponentSampleModel(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      super(var1, var2, var3, var6.length);
      this.dataType = var1;
      this.pixelStride = var4;
      this.scanlineStride = var5;
      this.bandOffsets = (int[])((int[])var6.clone());
      this.numBands = this.bandOffsets.length;
      if (var4 < 0) {
         throw new IllegalArgumentException("Pixel stride must be >= 0");
      } else if (var5 < 0) {
         throw new IllegalArgumentException("Scanline stride must be >= 0");
      } else if (this.numBands < 1) {
         throw new IllegalArgumentException("Must have at least one band.");
      } else if (var1 >= 0 && var1 <= 5) {
         this.bankIndices = new int[this.numBands];

         for(int var7 = 0; var7 < this.numBands; ++var7) {
            this.bankIndices[var7] = 0;
         }

         this.verify();
      } else {
         throw new IllegalArgumentException("Unsupported dataType.");
      }
   }

   public ComponentSampleModel(int var1, int var2, int var3, int var4, int var5, int[] var6, int[] var7) {
      super(var1, var2, var3, var7.length);
      this.dataType = var1;
      this.pixelStride = var4;
      this.scanlineStride = var5;
      this.bandOffsets = (int[])((int[])var7.clone());
      this.bankIndices = (int[])((int[])var6.clone());
      if (var4 < 0) {
         throw new IllegalArgumentException("Pixel stride must be >= 0");
      } else if (var5 < 0) {
         throw new IllegalArgumentException("Scanline stride must be >= 0");
      } else if (var1 >= 0 && var1 <= 5) {
         int var8 = this.bankIndices[0];
         if (var8 < 0) {
            throw new IllegalArgumentException("Index of bank 0 is less than 0 (" + var8 + ")");
         } else {
            for(int var9 = 1; var9 < this.bankIndices.length; ++var9) {
               if (this.bankIndices[var9] > var8) {
                  var8 = this.bankIndices[var9];
               } else if (this.bankIndices[var9] < 0) {
                  throw new IllegalArgumentException("Index of bank " + var9 + " is less than 0 (" + var8 + ")");
               }
            }

            this.numBanks = var8 + 1;
            this.numBands = this.bandOffsets.length;
            if (this.bandOffsets.length != this.bankIndices.length) {
               throw new IllegalArgumentException("Length of bandOffsets must equal length of bankIndices.");
            } else {
               this.verify();
            }
         }
      } else {
         throw new IllegalArgumentException("Unsupported dataType.");
      }
   }

   private void verify() {
      int var1 = this.getBufferSize();
   }

   private int getBufferSize() {
      int var1 = this.bandOffsets[0];

      int var2;
      for(var2 = 1; var2 < this.bandOffsets.length; ++var2) {
         var1 = Math.max(var1, this.bandOffsets[var2]);
      }

      if (var1 >= 0 && var1 <= 2147483646) {
         if (this.pixelStride >= 0 && this.pixelStride <= Integer.MAX_VALUE / this.width) {
            if (this.scanlineStride >= 0 && this.scanlineStride <= Integer.MAX_VALUE / this.height) {
               var2 = var1 + 1;
               int var3 = this.pixelStride * (this.width - 1);
               if (var3 > Integer.MAX_VALUE - var2) {
                  throw new IllegalArgumentException("Invalid pixel stride");
               } else {
                  var2 += var3;
                  var3 = this.scanlineStride * (this.height - 1);
                  if (var3 > Integer.MAX_VALUE - var2) {
                     throw new IllegalArgumentException("Invalid scan stride");
                  } else {
                     var2 += var3;
                     return var2;
                  }
               }
            } else {
               throw new IllegalArgumentException("Invalid scanline stride");
            }
         } else {
            throw new IllegalArgumentException("Invalid pixel stride");
         }
      } else {
         throw new IllegalArgumentException("Invalid band offset");
      }
   }

   int[] orderBands(int[] var1, int var2) {
      int[] var3 = new int[var1.length];
      int[] var4 = new int[var1.length];

      int var5;
      for(var5 = 0; var5 < var3.length; var3[var5] = var5++) {
      }

      for(var5 = 0; var5 < var4.length; ++var5) {
         int var6 = var5;

         for(int var7 = var5 + 1; var7 < var4.length; ++var7) {
            if (var1[var3[var6]] > var1[var3[var7]]) {
               var6 = var7;
            }
         }

         var4[var3[var6]] = var5 * var2;
         var3[var6] = var3[var5];
      }

      return var4;
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      Object var3 = null;
      int var6 = this.bandOffsets[0];
      int var7 = this.bandOffsets[0];

      int var8;
      for(var8 = 1; var8 < this.bandOffsets.length; ++var8) {
         var6 = Math.min(var6, this.bandOffsets[var8]);
         var7 = Math.max(var7, this.bandOffsets[var8]);
      }

      var7 -= var6;
      var8 = this.bandOffsets.length;
      int var10 = Math.abs(this.pixelStride);
      int var11 = Math.abs(this.scanlineStride);
      int var12 = Math.abs(var7);
      int[] var9;
      int var13;
      if (var10 > var11) {
         if (var10 > var12) {
            if (var11 > var12) {
               var9 = new int[this.bandOffsets.length];

               for(var13 = 0; var13 < var8; ++var13) {
                  var9[var13] = this.bandOffsets[var13] - var6;
               }

               var11 = var12 + 1;
               var10 = var11 * var2;
            } else {
               var9 = this.orderBands(this.bandOffsets, var11 * var2);
               var10 = var8 * var11 * var2;
            }
         } else {
            var10 = var11 * var2;
            var9 = this.orderBands(this.bandOffsets, var10 * var1);
         }
      } else if (var10 > var12) {
         var9 = new int[this.bandOffsets.length];

         for(var13 = 0; var13 < var8; ++var13) {
            var9[var13] = this.bandOffsets[var13] - var6;
         }

         var10 = var12 + 1;
         var11 = var10 * var1;
      } else if (var11 > var12) {
         var9 = this.orderBands(this.bandOffsets, var10 * var1);
         var11 = var8 * var10 * var1;
      } else {
         var11 = var10 * var1;
         var9 = this.orderBands(this.bandOffsets, var11 * var2);
      }

      var13 = 0;
      if (this.scanlineStride < 0) {
         var13 += var11 * var2;
         var11 *= -1;
      }

      if (this.pixelStride < 0) {
         var13 += var10 * var1;
         var10 *= -1;
      }

      for(int var14 = 0; var14 < var8; ++var14) {
         var9[var14] += var13;
      }

      return new ComponentSampleModel(this.dataType, var1, var2, var10, var11, this.bankIndices, var9);
   }

   public SampleModel createSubsetSampleModel(int[] var1) {
      if (var1.length > this.bankIndices.length) {
         throw new RasterFormatException("There are only " + this.bankIndices.length + " bands");
      } else {
         int[] var2 = new int[var1.length];
         int[] var3 = new int[var1.length];

         for(int var4 = 0; var4 < var1.length; ++var4) {
            var2[var4] = this.bankIndices[var1[var4]];
            var3[var4] = this.bandOffsets[var1[var4]];
         }

         return new ComponentSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, var2, var3);
      }
   }

   public DataBuffer createDataBuffer() {
      Object var1 = null;
      int var2 = this.getBufferSize();
      switch(this.dataType) {
      case 0:
         var1 = new DataBufferByte(var2, this.numBanks);
         break;
      case 1:
         var1 = new DataBufferUShort(var2, this.numBanks);
         break;
      case 2:
         var1 = new DataBufferShort(var2, this.numBanks);
         break;
      case 3:
         var1 = new DataBufferInt(var2, this.numBanks);
         break;
      case 4:
         var1 = new DataBufferFloat(var2, this.numBanks);
         break;
      case 5:
         var1 = new DataBufferDouble(var2, this.numBanks);
      }

      return (DataBuffer)var1;
   }

   public int getOffset(int var1, int var2) {
      int var3 = var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[0];
      return var3;
   }

   public int getOffset(int var1, int var2, int var3) {
      int var4 = var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3];
      return var4;
   }

   public final int[] getSampleSize() {
      int[] var1 = new int[this.numBands];
      int var2 = this.getSampleSize(0);

      for(int var3 = 0; var3 < this.numBands; ++var3) {
         var1[var3] = var2;
      }

      return var1;
   }

   public final int getSampleSize(int var1) {
      return DataBuffer.getDataTypeSize(this.dataType);
   }

   public final int[] getBankIndices() {
      return (int[])((int[])this.bankIndices.clone());
   }

   public final int[] getBandOffsets() {
      return (int[])((int[])this.bandOffsets.clone());
   }

   public final int getScanlineStride() {
      return this.scanlineStride;
   }

   public final int getPixelStride() {
      return this.pixelStride;
   }

   public final int getNumDataElements() {
      return this.getNumBands();
   }

   public Object getDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         int var6 = this.getNumDataElements();
         int var7 = var2 * this.scanlineStride + var1 * this.pixelStride;
         switch(var5) {
         case 0:
            byte[] var8;
            if (var3 == null) {
               var8 = new byte[var6];
            } else {
               var8 = (byte[])((byte[])var3);
            }

            for(int var14 = 0; var14 < var6; ++var14) {
               var8[var14] = (byte)var4.getElem(this.bankIndices[var14], var7 + this.bandOffsets[var14]);
            }

            var3 = var8;
            break;
         case 1:
         case 2:
            short[] var9;
            if (var3 == null) {
               var9 = new short[var6];
            } else {
               var9 = (short[])((short[])var3);
            }

            for(int var15 = 0; var15 < var6; ++var15) {
               var9[var15] = (short)var4.getElem(this.bankIndices[var15], var7 + this.bandOffsets[var15]);
            }

            var3 = var9;
            break;
         case 3:
            int[] var10;
            if (var3 == null) {
               var10 = new int[var6];
            } else {
               var10 = (int[])((int[])var3);
            }

            for(int var16 = 0; var16 < var6; ++var16) {
               var10[var16] = var4.getElem(this.bankIndices[var16], var7 + this.bandOffsets[var16]);
            }

            var3 = var10;
            break;
         case 4:
            float[] var11;
            if (var3 == null) {
               var11 = new float[var6];
            } else {
               var11 = (float[])((float[])var3);
            }

            for(int var17 = 0; var17 < var6; ++var17) {
               var11[var17] = var4.getElemFloat(this.bankIndices[var17], var7 + this.bandOffsets[var17]);
            }

            var3 = var11;
            break;
         case 5:
            double[] var12;
            if (var3 == null) {
               var12 = new double[var6];
            } else {
               var12 = (double[])((double[])var3);
            }

            for(int var13 = 0; var13 < var6; ++var13) {
               var12[var13] = var4.getElemDouble(this.bankIndices[var13], var7 + this.bandOffsets[var13]);
            }

            var3 = var12;
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

         int var6 = var2 * this.scanlineStride + var1 * this.pixelStride;

         for(int var7 = 0; var7 < this.numBands; ++var7) {
            var5[var7] = var4.getElem(this.bankIndices[var7], var6 + this.bandOffsets[var7]);
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var7 = var1 + var3;
      int var8 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var7 >= 0 && var7 <= this.width && var2 >= 0 && var2 < this.height && var2 <= this.height && var8 >= 0 && var8 <= this.height) {
         int[] var9;
         if (var5 != null) {
            var9 = var5;
         } else {
            var9 = new int[var3 * var4 * this.numBands];
         }

         int var10 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var11 = 0;

         for(int var12 = 0; var12 < var4; ++var12) {
            int var13 = var10;

            for(int var14 = 0; var14 < var3; ++var14) {
               for(int var15 = 0; var15 < this.numBands; ++var15) {
                  var9[var11++] = var6.getElem(this.bankIndices[var15], var13 + this.bandOffsets[var15]);
               }

               var13 += this.pixelStride;
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
         int var5 = var4.getElem(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3]);
         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public float getSampleFloat(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         float var5 = var4.getElemFloat(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3]);
         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public double getSampleDouble(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         double var5 = var4.getElemDouble(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3]);
         return var5;
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

         int var9 = var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var5];
         int var10 = 0;

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var9;

            for(int var13 = 0; var13 < var3; ++var13) {
               var8[var10++] = var7.getElem(this.bankIndices[var5], var12);
               var12 += this.pixelStride;
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
         int var6 = this.getNumDataElements();
         int var7 = var2 * this.scanlineStride + var1 * this.pixelStride;
         switch(var5) {
         case 0:
            byte[] var8 = (byte[])((byte[])var3);

            for(int var14 = 0; var14 < var6; ++var14) {
               var4.setElem(this.bankIndices[var14], var7 + this.bandOffsets[var14], var8[var14] & 255);
            }

            return;
         case 1:
         case 2:
            short[] var9 = (short[])((short[])var3);

            for(int var15 = 0; var15 < var6; ++var15) {
               var4.setElem(this.bankIndices[var15], var7 + this.bandOffsets[var15], var9[var15] & '\uffff');
            }

            return;
         case 3:
            int[] var10 = (int[])((int[])var3);

            for(int var16 = 0; var16 < var6; ++var16) {
               var4.setElem(this.bankIndices[var16], var7 + this.bandOffsets[var16], var10[var16]);
            }

            return;
         case 4:
            float[] var11 = (float[])((float[])var3);

            for(int var17 = 0; var17 < var6; ++var17) {
               var4.setElemFloat(this.bankIndices[var17], var7 + this.bandOffsets[var17], var11[var17]);
            }

            return;
         case 5:
            double[] var12 = (double[])((double[])var3);

            for(int var13 = 0; var13 < var6; ++var13) {
               var4.setElemDouble(this.bankIndices[var13], var7 + this.bandOffsets[var13], var12[var13]);
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = var2 * this.scanlineStride + var1 * this.pixelStride;

         for(int var6 = 0; var6 < this.numBands; ++var6) {
            var4.setElem(this.bankIndices[var6], var5 + this.bandOffsets[var6], var3[var6]);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var7 = var1 + var3;
      int var8 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var7 >= 0 && var7 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var8 >= 0 && var8 <= this.height) {
         int var9 = var2 * this.scanlineStride + var1 * this.pixelStride;
         int var10 = 0;

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var9;

            for(int var13 = 0; var13 < var3; ++var13) {
               for(int var14 = 0; var14 < this.numBands; ++var14) {
                  var6.setElem(this.bankIndices[var14], var12 + this.bandOffsets[var14], var5[var10++]);
               }

               var12 += this.pixelStride;
            }

            var9 += this.scanlineStride;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, int var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var5.setElem(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, float var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var5.setElemFloat(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, double var4, DataBuffer var6) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var6.setElemDouble(this.bankIndices[var3], var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.width && var2 + var4 <= this.height) {
         int var8 = var2 * this.scanlineStride + var1 * this.pixelStride + this.bandOffsets[var5];
         int var9 = 0;

         for(int var10 = 0; var10 < var4; ++var10) {
            int var11 = var8;

            for(int var12 = 0; var12 < var3; ++var12) {
               var7.setElem(this.bankIndices[var5], var11, var6[var9++]);
               var11 += this.pixelStride;
            }

            var8 += this.scanlineStride;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ComponentSampleModel) {
         ComponentSampleModel var2 = (ComponentSampleModel)var1;
         return this.width == var2.width && this.height == var2.height && this.numBands == var2.numBands && this.dataType == var2.dataType && Arrays.equals(this.bandOffsets, var2.bandOffsets) && Arrays.equals(this.bankIndices, var2.bankIndices) && this.numBands == var2.numBands && this.numBanks == var2.numBanks && this.scanlineStride == var2.scanlineStride && this.pixelStride == var2.pixelStride;
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
      for(var2 = 0; var2 < this.bandOffsets.length; ++var2) {
         var3 ^= this.bandOffsets[var2];
         var3 <<= 8;
      }

      for(var2 = 0; var2 < this.bankIndices.length; ++var2) {
         var3 ^= this.bankIndices[var2];
         var3 <<= 8;
      }

      var3 ^= this.numBands;
      var3 <<= 8;
      var3 ^= this.numBanks;
      var3 <<= 8;
      var3 ^= this.scanlineStride;
      var3 <<= 8;
      var3 ^= this.pixelStride;
      return var3;
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
