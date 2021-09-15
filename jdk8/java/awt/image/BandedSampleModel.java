package java.awt.image;

public final class BandedSampleModel extends ComponentSampleModel {
   public BandedSampleModel(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, 1, var2, createIndicesArray(var4), createOffsetArray(var4));
   }

   public BandedSampleModel(int var1, int var2, int var3, int var4, int[] var5, int[] var6) {
      super(var1, var2, var3, 1, var4, var5, var6);
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      int[] var3;
      if (this.numBanks == 1) {
         var3 = this.orderBands(this.bandOffsets, var1 * var2);
      } else {
         var3 = new int[this.bandOffsets.length];
      }

      BandedSampleModel var4 = new BandedSampleModel(this.dataType, var1, var2, var1, this.bankIndices, var3);
      return var4;
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

         return new BandedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, var2, var3);
      }
   }

   public DataBuffer createDataBuffer() {
      Object var1 = null;
      int var2 = this.scanlineStride * this.height;
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
         break;
      default:
         throw new IllegalArgumentException("dataType is not one of the supported types.");
      }

      return (DataBuffer)var1;
   }

   public Object getDataElements(int var1, int var2, Object var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = this.getTransferType();
         int var6 = this.getNumDataElements();
         int var7 = var2 * this.scanlineStride + var1;
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

         int var6 = var2 * this.scanlineStride + var1;

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
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var7 >= 0 && var7 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var8 >= 0 && var8 <= this.height) {
         int[] var9;
         if (var5 != null) {
            var9 = var5;
         } else {
            var9 = new int[var3 * var4 * this.numBands];
         }

         for(int var10 = 0; var10 < this.numBands; ++var10) {
            int var11 = var2 * this.scanlineStride + var1 + this.bandOffsets[var10];
            int var12 = var10;
            int var13 = this.bankIndices[var10];

            for(int var14 = 0; var14 < var4; ++var14) {
               int var15 = var11;

               for(int var16 = 0; var16 < var3; ++var16) {
                  var9[var12] = var6.getElem(var13, var15++);
                  var12 += this.numBands;
               }

               var11 += this.scanlineStride;
            }
         }

         return var9;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int getSample(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         int var5 = var4.getElem(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3]);
         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public float getSampleFloat(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         float var5 = var4.getElemFloat(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3]);
         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public double getSampleDouble(int var1, int var2, int var3, DataBuffer var4) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         double var5 = var4.getElemDouble(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3]);
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

         int var9 = var2 * this.scanlineStride + var1 + this.bandOffsets[var5];
         int var10 = 0;
         int var11 = this.bankIndices[var5];

         for(int var12 = 0; var12 < var4; ++var12) {
            int var13 = var9;

            for(int var14 = 0; var14 < var3; ++var14) {
               var8[var10++] = var7.getElem(var11, var13++);
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
         int var7 = var2 * this.scanlineStride + var1;
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
         int var5 = var2 * this.scanlineStride + var1;

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
         for(int var9 = 0; var9 < this.numBands; ++var9) {
            int var10 = var2 * this.scanlineStride + var1 + this.bandOffsets[var9];
            int var11 = var9;
            int var12 = this.bankIndices[var9];

            for(int var13 = 0; var13 < var4; ++var13) {
               int var14 = var10;

               for(int var15 = 0; var15 < var3; ++var15) {
                  var6.setElem(var12, var14++, var5[var11]);
                  var11 += this.numBands;
               }

               var10 += this.scanlineStride;
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, int var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var5.setElem(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, float var4, DataBuffer var5) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var5.setElemFloat(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSample(int var1, int var2, int var3, double var4, DataBuffer var6) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height) {
         var6.setElemDouble(this.bankIndices[var3], var2 * this.scanlineStride + var1 + this.bandOffsets[var3], var4);
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.width && var2 + var4 <= this.height) {
         int var8 = var2 * this.scanlineStride + var1 + this.bandOffsets[var5];
         int var9 = 0;
         int var10 = this.bankIndices[var5];

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var8;

            for(int var13 = 0; var13 < var3; ++var13) {
               var7.setElem(var10, var12++, var6[var9++]);
            }

            var8 += this.scanlineStride;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   private static int[] createOffsetArray(int var0) {
      int[] var1 = new int[var0];

      for(int var2 = 0; var2 < var0; ++var2) {
         var1[var2] = 0;
      }

      return var1;
   }

   private static int[] createIndicesArray(int var0) {
      int[] var1 = new int[var0];

      for(int var2 = 0; var2 < var0; var1[var2] = var2++) {
      }

      return var1;
   }

   public int hashCode() {
      return super.hashCode() ^ 2;
   }
}
