package java.awt.image;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.util.Arrays;

public class DirectColorModel extends PackedColorModel {
   private int red_mask;
   private int green_mask;
   private int blue_mask;
   private int alpha_mask;
   private int red_offset;
   private int green_offset;
   private int blue_offset;
   private int alpha_offset;
   private int red_scale;
   private int green_scale;
   private int blue_scale;
   private int alpha_scale;
   private boolean is_LinearRGB;
   private int lRGBprecision;
   private byte[] tosRGB8LUT;
   private byte[] fromsRGB8LUT8;
   private short[] fromsRGB8LUT16;

   public DirectColorModel(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, 0);
   }

   public DirectColorModel(int var1, int var2, int var3, int var4, int var5) {
      super(ColorSpace.getInstance(1000), var1, var2, var3, var4, var5, false, var5 == 0 ? 1 : 3, ColorModel.getDefaultTransferType(var1));
      this.setFields();
   }

   public DirectColorModel(ColorSpace var1, int var2, int var3, int var4, int var5, int var6, boolean var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var6 == 0 ? 1 : 3, var8);
      if (ColorModel.isLinearRGBspace(this.colorSpace)) {
         this.is_LinearRGB = true;
         if (this.maxBits <= 8) {
            this.lRGBprecision = 8;
            this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
            this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
         } else {
            this.lRGBprecision = 16;
            this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
            this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
         }
      } else if (!this.is_sRGB) {
         for(int var9 = 0; var9 < 3; ++var9) {
            if (var1.getMinValue(var9) != 0.0F || var1.getMaxValue(var9) != 1.0F) {
               throw new IllegalArgumentException("Illegal min/max RGB component value");
            }
         }
      }

      this.setFields();
   }

   public final int getRedMask() {
      return this.maskArray[0];
   }

   public final int getGreenMask() {
      return this.maskArray[1];
   }

   public final int getBlueMask() {
      return this.maskArray[2];
   }

   public final int getAlphaMask() {
      return this.supportsAlpha ? this.maskArray[3] : 0;
   }

   private float[] getDefaultRGBComponents(int var1) {
      int[] var2 = this.getComponents(var1, (int[])null, 0);
      float[] var3 = this.getNormalizedComponents(var2, 0, (float[])null, 0);
      return this.colorSpace.toRGB(var3);
   }

   private int getsRGBComponentFromsRGB(int var1, int var2) {
      int var3 = (var1 & this.maskArray[var2]) >>> this.maskOffsets[var2];
      if (this.isAlphaPremultiplied) {
         int var4 = (var1 & this.maskArray[3]) >>> this.maskOffsets[3];
         var3 = var4 == 0 ? 0 : (int)((float)var3 * this.scaleFactors[var2] * 255.0F / ((float)var4 * this.scaleFactors[3]) + 0.5F);
      } else if (this.scaleFactors[var2] != 1.0F) {
         var3 = (int)((float)var3 * this.scaleFactors[var2] + 0.5F);
      }

      return var3;
   }

   private int getsRGBComponentFromLinearRGB(int var1, int var2) {
      int var3 = (var1 & this.maskArray[var2]) >>> this.maskOffsets[var2];
      if (this.isAlphaPremultiplied) {
         float var4 = (float)((1 << this.lRGBprecision) - 1);
         int var5 = (var1 & this.maskArray[3]) >>> this.maskOffsets[3];
         var3 = var5 == 0 ? 0 : (int)((float)var3 * this.scaleFactors[var2] * var4 / ((float)var5 * this.scaleFactors[3]) + 0.5F);
      } else if (this.nBits[var2] != this.lRGBprecision) {
         if (this.lRGBprecision == 16) {
            var3 = (int)((float)var3 * this.scaleFactors[var2] * 257.0F + 0.5F);
         } else {
            var3 = (int)((float)var3 * this.scaleFactors[var2] + 0.5F);
         }
      }

      return this.tosRGB8LUT[var3] & 255;
   }

   public final int getRed(int var1) {
      if (this.is_sRGB) {
         return this.getsRGBComponentFromsRGB(var1, 0);
      } else if (this.is_LinearRGB) {
         return this.getsRGBComponentFromLinearRGB(var1, 0);
      } else {
         float[] var2 = this.getDefaultRGBComponents(var1);
         return (int)(var2[0] * 255.0F + 0.5F);
      }
   }

   public final int getGreen(int var1) {
      if (this.is_sRGB) {
         return this.getsRGBComponentFromsRGB(var1, 1);
      } else if (this.is_LinearRGB) {
         return this.getsRGBComponentFromLinearRGB(var1, 1);
      } else {
         float[] var2 = this.getDefaultRGBComponents(var1);
         return (int)(var2[1] * 255.0F + 0.5F);
      }
   }

   public final int getBlue(int var1) {
      if (this.is_sRGB) {
         return this.getsRGBComponentFromsRGB(var1, 2);
      } else if (this.is_LinearRGB) {
         return this.getsRGBComponentFromLinearRGB(var1, 2);
      } else {
         float[] var2 = this.getDefaultRGBComponents(var1);
         return (int)(var2[2] * 255.0F + 0.5F);
      }
   }

   public final int getAlpha(int var1) {
      if (!this.supportsAlpha) {
         return 255;
      } else {
         int var2 = (var1 & this.maskArray[3]) >>> this.maskOffsets[3];
         if (this.scaleFactors[3] != 1.0F) {
            var2 = (int)((float)var2 * this.scaleFactors[3] + 0.5F);
         }

         return var2;
      }
   }

   public final int getRGB(int var1) {
      if (!this.is_sRGB && !this.is_LinearRGB) {
         float[] var2 = this.getDefaultRGBComponents(var1);
         return this.getAlpha(var1) << 24 | (int)(var2[0] * 255.0F + 0.5F) << 16 | (int)(var2[1] * 255.0F + 0.5F) << 8 | (int)(var2[2] * 255.0F + 0.5F) << 0;
      } else {
         return this.getAlpha(var1) << 24 | this.getRed(var1) << 16 | this.getGreen(var1) << 8 | this.getBlue(var1) << 0;
      }
   }

   public int getRed(Object var1) {
      boolean var2 = false;
      int var6;
      switch(this.transferType) {
      case 0:
         byte[] var3 = (byte[])((byte[])var1);
         var6 = var3[0] & 255;
         break;
      case 1:
         short[] var4 = (short[])((short[])var1);
         var6 = var4[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var5 = (int[])((int[])var1);
         var6 = var5[0];
      }

      return this.getRed(var6);
   }

   public int getGreen(Object var1) {
      boolean var2 = false;
      int var6;
      switch(this.transferType) {
      case 0:
         byte[] var3 = (byte[])((byte[])var1);
         var6 = var3[0] & 255;
         break;
      case 1:
         short[] var4 = (short[])((short[])var1);
         var6 = var4[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var5 = (int[])((int[])var1);
         var6 = var5[0];
      }

      return this.getGreen(var6);
   }

   public int getBlue(Object var1) {
      boolean var2 = false;
      int var6;
      switch(this.transferType) {
      case 0:
         byte[] var3 = (byte[])((byte[])var1);
         var6 = var3[0] & 255;
         break;
      case 1:
         short[] var4 = (short[])((short[])var1);
         var6 = var4[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var5 = (int[])((int[])var1);
         var6 = var5[0];
      }

      return this.getBlue(var6);
   }

   public int getAlpha(Object var1) {
      boolean var2 = false;
      int var6;
      switch(this.transferType) {
      case 0:
         byte[] var3 = (byte[])((byte[])var1);
         var6 = var3[0] & 255;
         break;
      case 1:
         short[] var4 = (short[])((short[])var1);
         var6 = var4[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var5 = (int[])((int[])var1);
         var6 = var5[0];
      }

      return this.getAlpha(var6);
   }

   public int getRGB(Object var1) {
      boolean var2 = false;
      int var6;
      switch(this.transferType) {
      case 0:
         byte[] var3 = (byte[])((byte[])var1);
         var6 = var3[0] & 255;
         break;
      case 1:
         short[] var4 = (short[])((short[])var1);
         var6 = var4[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var5 = (int[])((int[])var1);
         var6 = var5[0];
      }

      return this.getRGB(var6);
   }

   public Object getDataElements(int var1, Object var2) {
      Object var3 = null;
      int[] var12;
      if (this.transferType == 3 && var2 != null) {
         var12 = (int[])((int[])var2);
         var12[0] = 0;
      } else {
         var12 = new int[1];
      }

      ColorModel var4 = ColorModel.getRGBdefault();
      if (this != var4 && !this.equals(var4)) {
         int var5 = var1 >> 16 & 255;
         int var6 = var1 >> 8 & 255;
         int var7 = var1 & 255;
         int var8;
         float var10;
         if (!this.is_sRGB && !this.is_LinearRGB) {
            float[] var13 = new float[3];
            var10 = 0.003921569F;
            var13[0] = (float)var5 * var10;
            var13[1] = (float)var6 * var10;
            var13[2] = (float)var7 * var10;
            var13 = this.colorSpace.fromRGB(var13);
            if (this.supportsAlpha) {
               var8 = var1 >> 24 & 255;
               if (this.isAlphaPremultiplied) {
                  var10 *= (float)var8;

                  for(int var11 = 0; var11 < 3; ++var11) {
                     var13[var11] *= var10;
                  }
               }

               if (this.nBits[3] != 8) {
                  var8 = (int)((float)var8 * 0.003921569F * (float)((1 << this.nBits[3]) - 1) + 0.5F);
                  if (var8 > (1 << this.nBits[3]) - 1) {
                     var8 = (1 << this.nBits[3]) - 1;
                  }
               }

               var12[0] = var8 << this.maskOffsets[3];
            }

            var5 = (int)(var13[0] * (float)((1 << this.nBits[0]) - 1) + 0.5F);
            var6 = (int)(var13[1] * (float)((1 << this.nBits[1]) - 1) + 0.5F);
            var7 = (int)(var13[2] * (float)((1 << this.nBits[2]) - 1) + 0.5F);
         } else {
            byte var9;
            if (this.is_LinearRGB) {
               if (this.lRGBprecision == 8) {
                  var5 = this.fromsRGB8LUT8[var5] & 255;
                  var6 = this.fromsRGB8LUT8[var6] & 255;
                  var7 = this.fromsRGB8LUT8[var7] & 255;
                  var9 = 8;
                  var10 = 0.003921569F;
               } else {
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var6 = this.fromsRGB8LUT16[var6] & '\uffff';
                  var7 = this.fromsRGB8LUT16[var7] & '\uffff';
                  var9 = 16;
                  var10 = 1.5259022E-5F;
               }
            } else {
               var9 = 8;
               var10 = 0.003921569F;
            }

            if (this.supportsAlpha) {
               var8 = var1 >> 24 & 255;
               if (this.isAlphaPremultiplied) {
                  var10 *= (float)var8 * 0.003921569F;
                  var9 = -1;
               }

               if (this.nBits[3] != 8) {
                  var8 = (int)((float)var8 * 0.003921569F * (float)((1 << this.nBits[3]) - 1) + 0.5F);
                  if (var8 > (1 << this.nBits[3]) - 1) {
                     var8 = (1 << this.nBits[3]) - 1;
                  }
               }

               var12[0] = var8 << this.maskOffsets[3];
            }

            if (this.nBits[0] != var9) {
               var5 = (int)((float)var5 * var10 * (float)((1 << this.nBits[0]) - 1) + 0.5F);
            }

            if (this.nBits[1] != var9) {
               var6 = (int)((float)var6 * var10 * (float)((1 << this.nBits[1]) - 1) + 0.5F);
            }

            if (this.nBits[2] != var9) {
               var7 = (int)((float)var7 * var10 * (float)((1 << this.nBits[2]) - 1) + 0.5F);
            }
         }

         if (this.maxBits > 23) {
            if (var5 > (1 << this.nBits[0]) - 1) {
               var5 = (1 << this.nBits[0]) - 1;
            }

            if (var6 > (1 << this.nBits[1]) - 1) {
               var6 = (1 << this.nBits[1]) - 1;
            }

            if (var7 > (1 << this.nBits[2]) - 1) {
               var7 = (1 << this.nBits[2]) - 1;
            }
         }

         var12[0] |= var5 << this.maskOffsets[0] | var6 << this.maskOffsets[1] | var7 << this.maskOffsets[2];
         switch(this.transferType) {
         case 0:
            byte[] var15;
            if (var2 == null) {
               var15 = new byte[1];
            } else {
               var15 = (byte[])((byte[])var2);
            }

            var15[0] = (byte)(255 & var12[0]);
            return var15;
         case 1:
            short[] var14;
            if (var2 == null) {
               var14 = new short[1];
            } else {
               var14 = (short[])((short[])var2);
            }

            var14[0] = (short)(var12[0] & '\uffff');
            return var14;
         case 2:
         default:
            throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
         case 3:
            return var12;
         }
      } else {
         var12[0] = var1;
         return var12;
      }
   }

   public final int[] getComponents(int var1, int[] var2, int var3) {
      if (var2 == null) {
         var2 = new int[var3 + this.numComponents];
      }

      for(int var4 = 0; var4 < this.numComponents; ++var4) {
         var2[var3 + var4] = (var1 & this.maskArray[var4]) >>> this.maskOffsets[var4];
      }

      return var2;
   }

   public final int[] getComponents(Object var1, int[] var2, int var3) {
      boolean var4 = false;
      int var8;
      switch(this.transferType) {
      case 0:
         byte[] var5 = (byte[])((byte[])var1);
         var8 = var5[0] & 255;
         break;
      case 1:
         short[] var6 = (short[])((short[])var1);
         var8 = var6[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var7 = (int[])((int[])var1);
         var8 = var7[0];
      }

      return this.getComponents(var8, var2, var3);
   }

   public final WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         int[] var3;
         if (this.supportsAlpha) {
            var3 = new int[4];
            var3[3] = this.alpha_mask;
         } else {
            var3 = new int[3];
         }

         var3[0] = this.red_mask;
         var3[1] = this.green_mask;
         var3[2] = this.blue_mask;
         if (this.pixel_bits > 16) {
            return Raster.createPackedRaster(3, var1, var2, var3, (Point)null);
         } else {
            return this.pixel_bits > 8 ? Raster.createPackedRaster(1, var1, var2, var3, (Point)null) : Raster.createPackedRaster(0, var1, var2, var3, (Point)null);
         }
      } else {
         throw new IllegalArgumentException("Width (" + var1 + ") and height (" + var2 + ") cannot be <= 0");
      }
   }

   public int getDataElement(int[] var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.numComponents; ++var4) {
         var3 |= var1[var2 + var4] << this.maskOffsets[var4] & this.maskArray[var4];
      }

      return var3;
   }

   public Object getDataElements(int[] var1, int var2, Object var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < this.numComponents; ++var5) {
         var4 |= var1[var2 + var5] << this.maskOffsets[var5] & this.maskArray[var5];
      }

      switch(this.transferType) {
      case 0:
         byte[] var8;
         if (var3 instanceof byte[]) {
            var8 = (byte[])((byte[])var3);
            var8[0] = (byte)(var4 & 255);
            return var8;
         }

         var8 = new byte[]{(byte)(var4 & 255)};
         return var8;
      case 1:
         short[] var7;
         if (var3 instanceof short[]) {
            var7 = (short[])((short[])var3);
            var7[0] = (short)(var4 & '\uffff');
            return var7;
         }

         var7 = new short[]{(short)(var4 & '\uffff')};
         return var7;
      case 2:
      default:
         throw new ClassCastException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var6;
         if (var3 instanceof int[]) {
            var6 = (int[])((int[])var3);
            var6[0] = var4;
            return var6;
         } else {
            var6 = new int[]{var4};
            return var6;
         }
      }
   }

   public final ColorModel coerceData(WritableRaster var1, boolean var2) {
      if (this.supportsAlpha && this.isAlphaPremultiplied() != var2) {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         int var5 = this.numColorComponents;
         float var7 = 1.0F / (float)((1 << this.nBits[var5]) - 1);
         int var8 = var1.getMinX();
         int var9 = var1.getMinY();
         int[] var11 = null;
         int[] var12 = null;
         float var6;
         int var10;
         int var13;
         int var14;
         if (var2) {
            int var15;
            switch(this.transferType) {
            case 0:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 == 0.0F) {
                        if (var12 == null) {
                           var12 = new int[this.numComponents];
                           Arrays.fill((int[])var12, (int)0);
                        }

                        var1.setPixel(var10, var9, var12);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var11[var15] = (int)((float)var11[var15] * var6 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }

               return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], var2, this.transferType);
            case 1:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 == 0.0F) {
                        if (var12 == null) {
                           var12 = new int[this.numComponents];
                           Arrays.fill((int[])var12, (int)0);
                        }

                        var1.setPixel(var10, var9, var12);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var11[var15] = (int)((float)var11[var15] * var6 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }

               return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], var2, this.transferType);
            case 2:
            default:
               throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            case 3:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 == 0.0F) {
                        if (var12 == null) {
                           var12 = new int[this.numComponents];
                           Arrays.fill((int[])var12, (int)0);
                        }

                        var1.setPixel(var10, var9, var12);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var11[var15] = (int)((float)var11[var15] * var6 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }
            }
         } else {
            int var16;
            float var17;
            switch(this.transferType) {
            case 0:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 != 0.0F) {
                        var17 = 1.0F / var6;

                        for(var16 = 0; var16 < var5; ++var16) {
                           var11[var16] = (int)((float)var11[var16] * var17 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }

               return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], var2, this.transferType);
            case 1:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 != 0.0F) {
                        var17 = 1.0F / var6;

                        for(var16 = 0; var16 < var5; ++var16) {
                           var11[var16] = (int)((float)var11[var16] * var17 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }

               return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], var2, this.transferType);
            case 2:
            default:
               throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            case 3:
               for(var13 = 0; var13 < var4; ++var9) {
                  var10 = var8;

                  for(var14 = 0; var14 < var3; ++var10) {
                     var11 = var1.getPixel(var10, var9, var11);
                     var6 = (float)var11[var5] * var7;
                     if (var6 != 0.0F) {
                        var17 = 1.0F / var6;

                        for(var16 = 0; var16 < var5; ++var16) {
                           var11[var16] = (int)((float)var11[var16] * var17 + 0.5F);
                        }

                        var1.setPixel(var10, var9, var11);
                     }

                     ++var14;
                  }

                  ++var13;
               }
            }
         }

         return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], var2, this.transferType);
      } else {
         return this;
      }
   }

   public boolean isCompatibleRaster(Raster var1) {
      SampleModel var2 = var1.getSampleModel();
      if (var2 instanceof SinglePixelPackedSampleModel) {
         SinglePixelPackedSampleModel var3 = (SinglePixelPackedSampleModel)var2;
         if (var3.getNumBands() != this.getNumComponents()) {
            return false;
         } else {
            int[] var4 = var3.getBitMasks();

            for(int var5 = 0; var5 < this.numComponents; ++var5) {
               if (var4[var5] != this.maskArray[var5]) {
                  return false;
               }
            }

            return var1.getTransferType() == this.transferType;
         }
      } else {
         return false;
      }
   }

   private void setFields() {
      this.red_mask = this.maskArray[0];
      this.red_offset = this.maskOffsets[0];
      this.green_mask = this.maskArray[1];
      this.green_offset = this.maskOffsets[1];
      this.blue_mask = this.maskArray[2];
      this.blue_offset = this.maskOffsets[2];
      if (this.nBits[0] < 8) {
         this.red_scale = (1 << this.nBits[0]) - 1;
      }

      if (this.nBits[1] < 8) {
         this.green_scale = (1 << this.nBits[1]) - 1;
      }

      if (this.nBits[2] < 8) {
         this.blue_scale = (1 << this.nBits[2]) - 1;
      }

      if (this.supportsAlpha) {
         this.alpha_mask = this.maskArray[3];
         this.alpha_offset = this.maskOffsets[3];
         if (this.nBits[3] < 8) {
            this.alpha_scale = (1 << this.nBits[3]) - 1;
         }
      }

   }

   public String toString() {
      return new String("DirectColorModel: rmask=" + Integer.toHexString(this.red_mask) + " gmask=" + Integer.toHexString(this.green_mask) + " bmask=" + Integer.toHexString(this.blue_mask) + " amask=" + Integer.toHexString(this.alpha_mask));
   }
}
