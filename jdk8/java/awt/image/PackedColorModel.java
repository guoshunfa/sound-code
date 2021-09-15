package java.awt.image;

import java.awt.color.ColorSpace;

public abstract class PackedColorModel extends ColorModel {
   int[] maskArray;
   int[] maskOffsets;
   float[] scaleFactors;

   public PackedColorModel(ColorSpace var1, int var2, int[] var3, int var4, boolean var5, int var6, int var7) {
      super(var2, createBitsArray(var3, var4), var1, var4 != 0, var5, var6, var7);
      if (var2 >= 1 && var2 <= 32) {
         this.maskArray = new int[this.numComponents];
         this.maskOffsets = new int[this.numComponents];
         this.scaleFactors = new float[this.numComponents];

         for(int var8 = 0; var8 < this.numColorComponents; ++var8) {
            this.DecomposeMask(var3[var8], var8, var1.getName(var8));
         }

         if (var4 != 0) {
            this.DecomposeMask(var4, this.numColorComponents, "alpha");
            if (this.nBits[this.numComponents - 1] == 1) {
               this.transparency = 2;
            }
         }

      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 32.");
      }
   }

   public PackedColorModel(ColorSpace var1, int var2, int var3, int var4, int var5, int var6, boolean var7, int var8, int var9) {
      super(var2, createBitsArray(var3, var4, var5, var6), var1, var6 != 0, var7, var8, var9);
      if (var1.getType() != 5) {
         throw new IllegalArgumentException("ColorSpace must be TYPE_RGB.");
      } else {
         this.maskArray = new int[this.numComponents];
         this.maskOffsets = new int[this.numComponents];
         this.scaleFactors = new float[this.numComponents];
         this.DecomposeMask(var3, 0, "red");
         this.DecomposeMask(var4, 1, "green");
         this.DecomposeMask(var5, 2, "blue");
         if (var6 != 0) {
            this.DecomposeMask(var6, 3, "alpha");
            if (this.nBits[3] == 1) {
               this.transparency = 2;
            }
         }

      }
   }

   public final int getMask(int var1) {
      return this.maskArray[var1];
   }

   public final int[] getMasks() {
      return (int[])((int[])this.maskArray.clone());
   }

   private void DecomposeMask(int var1, int var2, String var3) {
      int var4 = 0;
      int var5 = this.nBits[var2];
      this.maskArray[var2] = var1;
      if (var1 != 0) {
         while((var1 & 1) == 0) {
            var1 >>>= 1;
            ++var4;
         }
      }

      if (var4 + var5 > this.pixel_bits) {
         throw new IllegalArgumentException(var3 + " mask " + Integer.toHexString(this.maskArray[var2]) + " overflows pixel (expecting " + this.pixel_bits + " bits");
      } else {
         this.maskOffsets[var2] = var4;
         if (var5 == 0) {
            this.scaleFactors[var2] = 256.0F;
         } else {
            this.scaleFactors[var2] = 255.0F / (float)((1 << var5) - 1);
         }

      }
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      return new SinglePixelPackedSampleModel(this.transferType, var1, var2, this.maskArray);
   }

   public boolean isCompatibleSampleModel(SampleModel var1) {
      if (!(var1 instanceof SinglePixelPackedSampleModel)) {
         return false;
      } else if (this.numComponents != var1.getNumBands()) {
         return false;
      } else if (var1.getTransferType() != this.transferType) {
         return false;
      } else {
         SinglePixelPackedSampleModel var2 = (SinglePixelPackedSampleModel)var1;
         int[] var3 = var2.getBitMasks();
         if (var3.length != this.maskArray.length) {
            return false;
         } else {
            int var4 = (int)((1L << DataBuffer.getDataTypeSize(this.transferType)) - 1L);

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if ((var4 & var3[var5]) != (var4 & this.maskArray[var5])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public WritableRaster getAlphaRaster(WritableRaster var1) {
      if (!this.hasAlpha()) {
         return null;
      } else {
         int var2 = var1.getMinX();
         int var3 = var1.getMinY();
         int[] var4 = new int[]{var1.getNumBands() - 1};
         return var1.createWritableChild(var2, var3, var1.getWidth(), var1.getHeight(), var2, var3, var4);
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PackedColorModel)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         PackedColorModel var2 = (PackedColorModel)var1;
         int var3 = var2.getNumComponents();
         if (var3 != this.numComponents) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (this.maskArray[var4] != var2.getMask(var4)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static final int[] createBitsArray(int[] var0, int var1) {
      int var2 = var0.length;
      int var3 = var1 == 0 ? 0 : 1;
      int[] var4 = new int[var2 + var3];

      for(int var5 = 0; var5 < var2; ++var5) {
         var4[var5] = countBits(var0[var5]);
         if (var4[var5] < 0) {
            throw new IllegalArgumentException("Noncontiguous color mask (" + Integer.toHexString(var0[var5]) + "at index " + var5);
         }
      }

      if (var1 != 0) {
         var4[var2] = countBits(var1);
         if (var4[var2] < 0) {
            throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(var1));
         }
      }

      return var4;
   }

   private static final int[] createBitsArray(int var0, int var1, int var2, int var3) {
      int[] var4 = new int[3 + (var3 == 0 ? 0 : 1)];
      var4[0] = countBits(var0);
      var4[1] = countBits(var1);
      var4[2] = countBits(var2);
      if (var4[0] < 0) {
         throw new IllegalArgumentException("Noncontiguous red mask (" + Integer.toHexString(var0));
      } else if (var4[1] < 0) {
         throw new IllegalArgumentException("Noncontiguous green mask (" + Integer.toHexString(var1));
      } else if (var4[2] < 0) {
         throw new IllegalArgumentException("Noncontiguous blue mask (" + Integer.toHexString(var2));
      } else {
         if (var3 != 0) {
            var4[3] = countBits(var3);
            if (var4[3] < 0) {
               throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(var3));
            }
         }

         return var4;
      }
   }

   private static final int countBits(int var0) {
      int var1 = 0;
      if (var0 != 0) {
         while((var0 & 1) == 0) {
            var0 >>>= 1;
         }

         while((var0 & 1) == 1) {
            var0 >>>= 1;
            ++var1;
         }

         return var0 != 0 ? -1 : var1;
      } else {
         return var0 != 0 ? -1 : var1;
      }
   }
}
