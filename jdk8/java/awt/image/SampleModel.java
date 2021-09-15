package java.awt.image;

public abstract class SampleModel {
   protected int width;
   protected int height;
   protected int numBands;
   protected int dataType;

   private static native void initIDs();

   public SampleModel(int var1, int var2, int var3, int var4) {
      long var5 = (long)var2 * (long)var3;
      if (var2 > 0 && var3 > 0) {
         if (var5 >= 2147483647L) {
            throw new IllegalArgumentException("Dimensions (width=" + var2 + " height=" + var3 + ") are too large");
         } else if (var1 < 0 || var1 > 5 && var1 != 32) {
            throw new IllegalArgumentException("Unsupported dataType: " + var1);
         } else if (var4 <= 0) {
            throw new IllegalArgumentException("Number of bands must be > 0");
         } else {
            this.dataType = var1;
            this.width = var2;
            this.height = var3;
            this.numBands = var4;
         }
      } else {
         throw new IllegalArgumentException("Width (" + var2 + ") and height (" + var3 + ") must be > 0");
      }
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final int getNumBands() {
      return this.numBands;
   }

   public abstract int getNumDataElements();

   public final int getDataType() {
      return this.dataType;
   }

   public int getTransferType() {
      return this.dataType;
   }

   public int[] getPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      int[] var5;
      if (var3 != null) {
         var5 = var3;
      } else {
         var5 = new int[this.numBands];
      }

      for(int var6 = 0; var6 < this.numBands; ++var6) {
         var5[var6] = this.getSample(var1, var2, var6, var4);
      }

      return var5;
   }

   public abstract Object getDataElements(int var1, int var2, Object var3, DataBuffer var4);

   public Object getDataElements(int var1, int var2, int var3, int var4, Object var5, DataBuffer var6) {
      int var7 = this.getTransferType();
      int var8 = this.getNumDataElements();
      int var9 = 0;
      Object var10 = null;
      int var11 = var1 + var3;
      int var12 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var11 >= 0 && var11 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var12 >= 0 && var12 <= this.height) {
         int var23;
         int var28;
         int var30;
         int var32;
         switch(var7) {
         case 0:
            byte[] var14;
            if (var5 == null) {
               var14 = new byte[var8 * var3 * var4];
            } else {
               var14 = (byte[])((byte[])var5);
            }

            for(int var26 = var2; var26 < var12; ++var26) {
               for(int var27 = var1; var27 < var11; ++var27) {
                  var10 = this.getDataElements(var27, var26, var10, var6);
                  byte[] var13 = (byte[])((byte[])var10);

                  for(var28 = 0; var28 < var8; ++var28) {
                     var14[var9++] = var13[var28];
                  }
               }
            }

            var5 = var14;
            break;
         case 1:
         case 2:
            short[] var15;
            if (var5 == null) {
               var15 = new short[var8 * var3 * var4];
            } else {
               var15 = (short[])((short[])var5);
            }

            for(var28 = var2; var28 < var12; ++var28) {
               for(int var29 = var1; var29 < var11; ++var29) {
                  var10 = this.getDataElements(var29, var28, var10, var6);
                  short[] var16 = (short[])((short[])var10);

                  for(var30 = 0; var30 < var8; ++var30) {
                     var15[var9++] = var16[var30];
                  }
               }
            }

            var5 = var15;
            break;
         case 3:
            int[] var17;
            if (var5 == null) {
               var17 = new int[var8 * var3 * var4];
            } else {
               var17 = (int[])((int[])var5);
            }

            for(var30 = var2; var30 < var12; ++var30) {
               for(int var31 = var1; var31 < var11; ++var31) {
                  var10 = this.getDataElements(var31, var30, var10, var6);
                  int[] var18 = (int[])((int[])var10);

                  for(var32 = 0; var32 < var8; ++var32) {
                     var17[var9++] = var18[var32];
                  }
               }
            }

            var5 = var17;
            break;
         case 4:
            float[] var19;
            if (var5 == null) {
               var19 = new float[var8 * var3 * var4];
            } else {
               var19 = (float[])((float[])var5);
            }

            for(var32 = var2; var32 < var12; ++var32) {
               for(int var33 = var1; var33 < var11; ++var33) {
                  var10 = this.getDataElements(var33, var32, var10, var6);
                  float[] var20 = (float[])((float[])var10);

                  for(var23 = 0; var23 < var8; ++var23) {
                     var19[var9++] = var20[var23];
                  }
               }
            }

            var5 = var19;
            break;
         case 5:
            double[] var21;
            if (var5 == null) {
               var21 = new double[var8 * var3 * var4];
            } else {
               var21 = (double[])((double[])var5);
            }

            for(var23 = var2; var23 < var12; ++var23) {
               for(int var24 = var1; var24 < var11; ++var24) {
                  var10 = this.getDataElements(var24, var23, var10, var6);
                  double[] var22 = (double[])((double[])var10);

                  for(int var25 = 0; var25 < var8; ++var25) {
                     var21[var9++] = var22[var25];
                  }
               }
            }

            var5 = var21;
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public abstract void setDataElements(int var1, int var2, Object var3, DataBuffer var4);

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5, DataBuffer var6) {
      int var7 = 0;
      Object var8 = null;
      int var9 = this.getTransferType();
      int var10 = this.getNumDataElements();
      int var11 = var1 + var3;
      int var12 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var11 >= 0 && var11 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var12 >= 0 && var12 <= this.height) {
         int var23;
         int var28;
         int var30;
         int var32;
         switch(var9) {
         case 0:
            byte[] var13 = (byte[])((byte[])var5);
            byte[] var14 = new byte[var10];

            for(int var26 = var2; var26 < var12; ++var26) {
               for(int var27 = var1; var27 < var11; ++var27) {
                  for(var28 = 0; var28 < var10; ++var28) {
                     var14[var28] = var13[var7++];
                  }

                  this.setDataElements(var27, var26, var14, var6);
               }
            }

            return;
         case 1:
         case 2:
            short[] var15 = (short[])((short[])var5);
            short[] var16 = new short[var10];

            for(var28 = var2; var28 < var12; ++var28) {
               for(int var29 = var1; var29 < var11; ++var29) {
                  for(var30 = 0; var30 < var10; ++var30) {
                     var16[var30] = var15[var7++];
                  }

                  this.setDataElements(var29, var28, var16, var6);
               }
            }

            return;
         case 3:
            int[] var17 = (int[])((int[])var5);
            int[] var18 = new int[var10];

            for(var30 = var2; var30 < var12; ++var30) {
               for(int var31 = var1; var31 < var11; ++var31) {
                  for(var32 = 0; var32 < var10; ++var32) {
                     var18[var32] = var17[var7++];
                  }

                  this.setDataElements(var31, var30, var18, var6);
               }
            }

            return;
         case 4:
            float[] var19 = (float[])((float[])var5);
            float[] var20 = new float[var10];

            for(var32 = var2; var32 < var12; ++var32) {
               for(int var33 = var1; var33 < var11; ++var33) {
                  for(var23 = 0; var23 < var10; ++var23) {
                     var20[var23] = var19[var7++];
                  }

                  this.setDataElements(var33, var32, var20, var6);
               }
            }

            return;
         case 5:
            double[] var21 = (double[])((double[])var5);
            double[] var22 = new double[var10];

            for(var23 = var2; var23 < var12; ++var23) {
               for(int var24 = var1; var24 < var11; ++var24) {
                  for(int var25 = 0; var25 < var10; ++var25) {
                     var22[var25] = var21[var7++];
                  }

                  this.setDataElements(var24, var23, var22, var6);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public float[] getPixel(int var1, int var2, float[] var3, DataBuffer var4) {
      float[] var5;
      if (var3 != null) {
         var5 = var3;
      } else {
         var5 = new float[this.numBands];
      }

      for(int var6 = 0; var6 < this.numBands; ++var6) {
         var5[var6] = this.getSampleFloat(var1, var2, var6, var4);
      }

      return var5;
   }

   public double[] getPixel(int var1, int var2, double[] var3, DataBuffer var4) {
      double[] var5;
      if (var3 != null) {
         var5 = var3;
      } else {
         var5 = new double[this.numBands];
      }

      for(int var6 = 0; var6 < this.numBands; ++var6) {
         var5[var6] = this.getSampleDouble(var1, var2, var6, var4);
      }

      return var5;
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         int[] var7;
         if (var5 != null) {
            var7 = var5;
         } else {
            var7 = new int[this.numBands * var3 * var4];
         }

         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               for(int var13 = 0; var13 < this.numBands; ++var13) {
                  var7[var8++] = this.getSample(var12, var11, var13, var6);
               }
            }
         }

         return var7;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public float[] getPixels(int var1, int var2, int var3, int var4, float[] var5, DataBuffer var6) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         float[] var7;
         if (var5 != null) {
            var7 = var5;
         } else {
            var7 = new float[this.numBands * var3 * var4];
         }

         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               for(int var13 = 0; var13 < this.numBands; ++var13) {
                  var7[var8++] = this.getSampleFloat(var12, var11, var13, var6);
               }
            }
         }

         return var7;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public double[] getPixels(int var1, int var2, int var3, int var4, double[] var5, DataBuffer var6) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         double[] var7;
         if (var5 != null) {
            var7 = var5;
         } else {
            var7 = new double[this.numBands * var3 * var4];
         }

         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               for(int var13 = 0; var13 < this.numBands; ++var13) {
                  var7[var8++] = this.getSampleDouble(var12, var11, var13, var6);
               }
            }
         }

         return var7;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public abstract int getSample(int var1, int var2, int var3, DataBuffer var4);

   public float getSampleFloat(int var1, int var2, int var3, DataBuffer var4) {
      float var5 = (float)this.getSample(var1, var2, var3, var4);
      return var5;
   }

   public double getSampleDouble(int var1, int var2, int var3, DataBuffer var4) {
      double var5 = (double)this.getSample(var1, var2, var3, var4);
      return var5;
   }

   public int[] getSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      int var9 = 0;
      int var10 = var1 + var3;
      int var11 = var2 + var4;
      if (var1 >= 0 && var10 >= var1 && var10 <= this.width && var2 >= 0 && var11 >= var2 && var11 <= this.height) {
         int[] var8;
         if (var6 != null) {
            var8 = var6;
         } else {
            var8 = new int[var3 * var4];
         }

         for(int var12 = var2; var12 < var11; ++var12) {
            for(int var13 = var1; var13 < var10; ++var13) {
               var8[var9++] = this.getSample(var13, var12, var5, var7);
            }
         }

         return var8;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public float[] getSamples(int var1, int var2, int var3, int var4, int var5, float[] var6, DataBuffer var7) {
      int var9 = 0;
      int var10 = var1 + var3;
      int var11 = var2 + var4;
      if (var1 >= 0 && var10 >= var1 && var10 <= this.width && var2 >= 0 && var11 >= var2 && var11 <= this.height) {
         float[] var8;
         if (var6 != null) {
            var8 = var6;
         } else {
            var8 = new float[var3 * var4];
         }

         for(int var12 = var2; var12 < var11; ++var12) {
            for(int var13 = var1; var13 < var10; ++var13) {
               var8[var9++] = this.getSampleFloat(var13, var12, var5, var7);
            }
         }

         return var8;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
      }
   }

   public double[] getSamples(int var1, int var2, int var3, int var4, int var5, double[] var6, DataBuffer var7) {
      int var9 = 0;
      int var10 = var1 + var3;
      int var11 = var2 + var4;
      if (var1 >= 0 && var10 >= var1 && var10 <= this.width && var2 >= 0 && var11 >= var2 && var11 <= this.height) {
         double[] var8;
         if (var6 != null) {
            var8 = var6;
         } else {
            var8 = new double[var3 * var4];
         }

         for(int var12 = var2; var12 < var11; ++var12) {
            for(int var13 = var1; var13 < var10; ++var13) {
               var8[var9++] = this.getSampleDouble(var13, var12, var5, var7);
            }
         }

         return var8;
      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
      }
   }

   public void setPixel(int var1, int var2, int[] var3, DataBuffer var4) {
      for(int var5 = 0; var5 < this.numBands; ++var5) {
         this.setSample(var1, var2, var5, var3[var5], var4);
      }

   }

   public void setPixel(int var1, int var2, float[] var3, DataBuffer var4) {
      for(int var5 = 0; var5 < this.numBands; ++var5) {
         this.setSample(var1, var2, var5, var3[var5], var4);
      }

   }

   public void setPixel(int var1, int var2, double[] var3, DataBuffer var4) {
      for(int var5 = 0; var5 < this.numBands; ++var5) {
         this.setSample(var1, var2, var5, var3[var5], var4);
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5, DataBuffer var6) {
      int var7 = 0;
      int var8 = var1 + var3;
      int var9 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var8 >= 0 && var8 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var9 >= 0 && var9 <= this.height) {
         for(int var10 = var2; var10 < var9; ++var10) {
            for(int var11 = var1; var11 < var8; ++var11) {
               for(int var12 = 0; var12 < this.numBands; ++var12) {
                  this.setSample(var11, var10, var12, var5[var7++], var6);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, float[] var5, DataBuffer var6) {
      int var7 = 0;
      int var8 = var1 + var3;
      int var9 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var8 >= 0 && var8 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var9 >= 0 && var9 <= this.height) {
         for(int var10 = var2; var10 < var9; ++var10) {
            for(int var11 = var1; var11 < var8; ++var11) {
               for(int var12 = 0; var12 < this.numBands; ++var12) {
                  this.setSample(var11, var10, var12, var5[var7++], var6);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, double[] var5, DataBuffer var6) {
      int var7 = 0;
      int var8 = var1 + var3;
      int var9 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var8 >= 0 && var8 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var9 >= 0 && var9 <= this.height) {
         for(int var10 = var2; var10 < var9; ++var10) {
            for(int var11 = var1; var11 < var8; ++var11) {
               for(int var12 = 0; var12 < this.numBands; ++var12) {
                  this.setSample(var11, var10, var12, var5[var7++], var6);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public abstract void setSample(int var1, int var2, int var3, int var4, DataBuffer var5);

   public void setSample(int var1, int var2, int var3, float var4, DataBuffer var5) {
      int var6 = (int)var4;
      this.setSample(var1, var2, var3, var6, var5);
   }

   public void setSample(int var1, int var2, int var3, double var4, DataBuffer var6) {
      int var7 = (int)var4;
      this.setSample(var1, var2, var3, var7, var6);
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, int[] var6, DataBuffer var7) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               this.setSample(var12, var11, var5, var6[var8++], var7);
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, float[] var6, DataBuffer var7) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               this.setSample(var12, var11, var5, var6[var8++], var7);
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public void setSamples(int var1, int var2, int var3, int var4, int var5, double[] var6, DataBuffer var7) {
      int var8 = 0;
      int var9 = var1 + var3;
      int var10 = var2 + var4;
      if (var1 >= 0 && var1 < this.width && var3 <= this.width && var9 >= 0 && var9 <= this.width && var2 >= 0 && var2 < this.height && var4 <= this.height && var10 >= 0 && var10 <= this.height) {
         for(int var11 = var2; var11 < var10; ++var11) {
            for(int var12 = var1; var12 < var9; ++var12) {
               this.setSample(var12, var11, var5, var6[var8++], var7);
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
      }
   }

   public abstract SampleModel createCompatibleSampleModel(int var1, int var2);

   public abstract SampleModel createSubsetSampleModel(int[] var1);

   public abstract DataBuffer createDataBuffer();

   public abstract int[] getSampleSize();

   public abstract int getSampleSize(int var1);

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
