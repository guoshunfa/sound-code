package java.awt.image;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.util.Arrays;

public class ComponentColorModel extends ColorModel {
   private boolean signed;
   private boolean is_sRGB_stdScale;
   private boolean is_LinearRGB_stdScale;
   private boolean is_LinearGray_stdScale;
   private boolean is_ICCGray_stdScale;
   private byte[] tosRGB8LUT;
   private byte[] fromsRGB8LUT8;
   private short[] fromsRGB8LUT16;
   private byte[] fromLinearGray16ToOtherGray8LUT;
   private short[] fromLinearGray16ToOtherGray16LUT;
   private boolean needScaleInit;
   private boolean noUnnorm;
   private boolean nonStdScale;
   private float[] min;
   private float[] diffMinMax;
   private float[] compOffset;
   private float[] compScale;

   public ComponentColorModel(ColorSpace var1, int[] var2, boolean var3, boolean var4, int var5, int var6) {
      super(bitsHelper(var6, var1, var3), bitsArrayHelper(var2, var6, var1, var3), var1, var3, var4, var5, var6);
      switch(var6) {
      case 0:
      case 1:
      case 3:
         this.signed = false;
         this.needScaleInit = true;
         break;
      case 2:
         this.signed = true;
         this.needScaleInit = true;
         break;
      case 4:
      case 5:
         this.signed = true;
         this.needScaleInit = false;
         this.noUnnorm = true;
         this.nonStdScale = false;
         break;
      default:
         throw new IllegalArgumentException("This constructor is not compatible with transferType " + var6);
      }

      this.setupLUTs();
   }

   public ComponentColorModel(ColorSpace var1, boolean var2, boolean var3, int var4, int var5) {
      this(var1, (int[])null, var2, var3, var4, var5);
   }

   private static int bitsHelper(int var0, ColorSpace var1, boolean var2) {
      int var3 = DataBuffer.getDataTypeSize(var0);
      int var4 = var1.getNumComponents();
      if (var2) {
         ++var4;
      }

      return var3 * var4;
   }

   private static int[] bitsArrayHelper(int[] var0, int var1, ColorSpace var2, boolean var3) {
      switch(var1) {
      case 0:
      case 1:
      case 3:
         if (var0 != null) {
            return var0;
         }
      case 2:
      default:
         int var4 = DataBuffer.getDataTypeSize(var1);
         int var5 = var2.getNumComponents();
         if (var3) {
            ++var5;
         }

         int[] var6 = new int[var5];

         for(int var7 = 0; var7 < var5; ++var7) {
            var6[var7] = var4;
         }

         return var6;
      }
   }

   private void setupLUTs() {
      if (this.is_sRGB) {
         this.is_sRGB_stdScale = true;
         this.nonStdScale = false;
      } else if (ColorModel.isLinearRGBspace(this.colorSpace)) {
         this.is_LinearRGB_stdScale = true;
         this.nonStdScale = false;
         if (this.transferType == 0) {
            this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
            this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
         } else {
            this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
            this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
         }
      } else if (this.colorSpaceType == 6 && this.colorSpace instanceof ICC_ColorSpace && this.colorSpace.getMinValue(0) == 0.0F && this.colorSpace.getMaxValue(0) == 1.0F) {
         ICC_ColorSpace var2 = (ICC_ColorSpace)this.colorSpace;
         this.is_ICCGray_stdScale = true;
         this.nonStdScale = false;
         this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
         if (ColorModel.isLinearGRAYspace(var2)) {
            this.is_LinearGray_stdScale = true;
            if (this.transferType == 0) {
               this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(var2);
            } else {
               this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(var2);
            }
         } else if (this.transferType == 0) {
            this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(var2);
            this.fromLinearGray16ToOtherGray8LUT = ColorModel.getLinearGray16ToOtherGray8LUT(var2);
         } else {
            this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(var2);
            this.fromLinearGray16ToOtherGray16LUT = ColorModel.getLinearGray16ToOtherGray16LUT(var2);
         }
      } else if (this.needScaleInit) {
         this.nonStdScale = false;

         int var1;
         for(var1 = 0; var1 < this.numColorComponents; ++var1) {
            if (this.colorSpace.getMinValue(var1) != 0.0F || this.colorSpace.getMaxValue(var1) != 1.0F) {
               this.nonStdScale = true;
               break;
            }
         }

         if (this.nonStdScale) {
            this.min = new float[this.numColorComponents];
            this.diffMinMax = new float[this.numColorComponents];

            for(var1 = 0; var1 < this.numColorComponents; ++var1) {
               this.min[var1] = this.colorSpace.getMinValue(var1);
               this.diffMinMax[var1] = this.colorSpace.getMaxValue(var1) - this.min[var1];
            }
         }
      }

   }

   private void initScale() {
      this.needScaleInit = false;
      if (!this.nonStdScale && !this.signed) {
         this.noUnnorm = false;
      } else {
         this.noUnnorm = true;
      }

      float[] var1;
      float[] var2;
      int var4;
      short[] var5;
      switch(this.transferType) {
      case 0:
         byte[] var6 = new byte[this.numComponents];

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var6[var4] = 0;
         }

         if (this.supportsAlpha) {
            var6[this.numColorComponents] = (byte)((1 << this.nBits[this.numColorComponents]) - 1);
         }

         var1 = this.getNormalizedComponents(var6, (float[])null, 0);

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var6[var4] = (byte)((1 << this.nBits[var4]) - 1);
         }

         var2 = this.getNormalizedComponents(var6, (float[])null, 0);
         break;
      case 1:
         var5 = new short[this.numComponents];

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var5[var4] = 0;
         }

         if (this.supportsAlpha) {
            var5[this.numColorComponents] = (short)((1 << this.nBits[this.numColorComponents]) - 1);
         }

         var1 = this.getNormalizedComponents(var5, (float[])null, 0);

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var5[var4] = (short)((1 << this.nBits[var4]) - 1);
         }

         var2 = this.getNormalizedComponents(var5, (float[])null, 0);
         break;
      case 2:
         var5 = new short[this.numComponents];

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var5[var4] = 0;
         }

         if (this.supportsAlpha) {
            var5[this.numColorComponents] = 32767;
         }

         var1 = this.getNormalizedComponents(var5, (float[])null, 0);

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var5[var4] = 32767;
         }

         var2 = this.getNormalizedComponents(var5, (float[])null, 0);
         break;
      case 3:
         int[] var3 = new int[this.numComponents];

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var3[var4] = 0;
         }

         if (this.supportsAlpha) {
            var3[this.numColorComponents] = (1 << this.nBits[this.numColorComponents]) - 1;
         }

         var1 = this.getNormalizedComponents(var3, (float[])null, 0);

         for(var4 = 0; var4 < this.numColorComponents; ++var4) {
            var3[var4] = (1 << this.nBits[var4]) - 1;
         }

         var2 = this.getNormalizedComponents(var3, (float[])null, 0);
         break;
      default:
         var2 = null;
         var1 = null;
      }

      this.nonStdScale = false;

      int var7;
      for(var7 = 0; var7 < this.numColorComponents; ++var7) {
         if (var1[var7] != 0.0F || var2[var7] != 1.0F) {
            this.nonStdScale = true;
            break;
         }
      }

      if (this.nonStdScale) {
         this.noUnnorm = true;
         this.is_sRGB_stdScale = false;
         this.is_LinearRGB_stdScale = false;
         this.is_LinearGray_stdScale = false;
         this.is_ICCGray_stdScale = false;
         this.compOffset = new float[this.numColorComponents];
         this.compScale = new float[this.numColorComponents];

         for(var7 = 0; var7 < this.numColorComponents; ++var7) {
            this.compOffset[var7] = var1[var7];
            this.compScale[var7] = 1.0F / (var2[var7] - var1[var7]);
         }
      }

   }

   private int getRGBComponent(int var1, int var2) {
      if (this.numComponents > 1) {
         throw new IllegalArgumentException("More than one component per pixel");
      } else if (this.signed) {
         throw new IllegalArgumentException("Component value is signed");
      } else {
         if (this.needScaleInit) {
            this.initScale();
         }

         Object var3 = null;
         switch(this.transferType) {
         case 0:
            byte[] var7 = new byte[]{(byte)var1};
            var3 = var7;
            break;
         case 1:
            short[] var6 = new short[]{(short)var1};
            var3 = var6;
         case 2:
         default:
            break;
         case 3:
            int[] var4 = new int[]{var1};
            var3 = var4;
         }

         float[] var8 = this.getNormalizedComponents(var3, (float[])null, 0);
         float[] var5 = this.colorSpace.toRGB(var8);
         return (int)(var5[var2] * 255.0F + 0.5F);
      }
   }

   public int getRed(int var1) {
      return this.getRGBComponent(var1, 0);
   }

   public int getGreen(int var1) {
      return this.getRGBComponent(var1, 1);
   }

   public int getBlue(int var1) {
      return this.getRGBComponent(var1, 2);
   }

   public int getAlpha(int var1) {
      if (!this.supportsAlpha) {
         return 255;
      } else if (this.numComponents > 1) {
         throw new IllegalArgumentException("More than one component per pixel");
      } else if (this.signed) {
         throw new IllegalArgumentException("Component value is signed");
      } else {
         return (int)((float)var1 / (float)((1 << this.nBits[0]) - 1) * 255.0F + 0.5F);
      }
   }

   public int getRGB(int var1) {
      if (this.numComponents > 1) {
         throw new IllegalArgumentException("More than one component per pixel");
      } else if (this.signed) {
         throw new IllegalArgumentException("Component value is signed");
      } else {
         return this.getAlpha(var1) << 24 | this.getRed(var1) << 16 | this.getGreen(var1) << 8 | this.getBlue(var1) << 0;
      }
   }

   private int extractComponent(Object var1, int var2, int var3) {
      boolean var4 = this.supportsAlpha && this.isAlphaPremultiplied;
      int var5 = 0;
      int var7 = (1 << this.nBits[var2]) - 1;
      int var6;
      float var10;
      float var16;
      switch(this.transferType) {
      case 0:
         byte[] var15 = (byte[])((byte[])var1);
         var6 = var15[var2] & var7;
         var3 = 8;
         if (var4) {
            var5 = var15[this.numColorComponents] & var7;
         }
         break;
      case 1:
         short[] var18 = (short[])((short[])var1);
         var6 = var18[var2] & var7;
         if (var4) {
            var5 = var18[this.numColorComponents] & var7;
         }
         break;
      case 2:
         short[] var14 = (short[])((short[])var1);
         var16 = (float)((1 << var3) - 1);
         if (var4) {
            short var20 = var14[this.numColorComponents];
            if (var20 != 0) {
               return (int)((float)var14[var2] / (float)var20 * var16 + 0.5F);
            }

            return 0;
         }

         return (int)((float)var14[var2] / 32767.0F * var16 + 0.5F);
      case 3:
         int[] var19 = (int[])((int[])var1);
         var6 = var19[var2];
         if (var4) {
            var5 = var19[this.numColorComponents];
         }
         break;
      case 4:
         float[] var13 = (float[])((float[])var1);
         var16 = (float)((1 << var3) - 1);
         if (var4) {
            var10 = var13[this.numColorComponents];
            if (var10 != 0.0F) {
               return (int)(var13[var2] / var10 * var16 + 0.5F);
            }

            return 0;
         }

         return (int)(var13[var2] * var16 + 0.5F);
      case 5:
         double[] var8 = (double[])((double[])var1);
         double var9 = (double)((1 << var3) - 1);
         if (var4) {
            double var11 = var8[this.numColorComponents];
            if (var11 != 0.0D) {
               return (int)(var8[var2] / var11 * var9 + 0.5D);
            }

            return 0;
         }

         return (int)(var8[var2] * var9 + 0.5D);
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }

      float var17;
      if (var4) {
         if (var5 != 0) {
            var17 = (float)((1 << var3) - 1);
            var16 = (float)var6 / (float)var7;
            var10 = (float)((1 << this.nBits[this.numColorComponents]) - 1) / (float)var5;
            return (int)(var16 * var10 * var17 + 0.5F);
         } else {
            return 0;
         }
      } else if (this.nBits[var2] != var3) {
         var17 = (float)((1 << var3) - 1);
         var16 = (float)var6 / (float)var7;
         return (int)(var16 * var17 + 0.5F);
      } else {
         return var6;
      }
   }

   private int getRGBComponent(Object var1, int var2) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.is_sRGB_stdScale) {
         return this.extractComponent(var1, var2, 8);
      } else {
         int var5;
         if (this.is_LinearRGB_stdScale) {
            var5 = this.extractComponent(var1, var2, 16);
            return this.tosRGB8LUT[var5] & 255;
         } else if (this.is_ICCGray_stdScale) {
            var5 = this.extractComponent(var1, 0, 16);
            return this.tosRGB8LUT[var5] & 255;
         } else {
            float[] var3 = this.getNormalizedComponents(var1, (float[])null, 0);
            float[] var4 = this.colorSpace.toRGB(var3);
            return (int)(var4[var2] * 255.0F + 0.5F);
         }
      }
   }

   public int getRed(Object var1) {
      return this.getRGBComponent(var1, 0);
   }

   public int getGreen(Object var1) {
      return this.getRGBComponent(var1, 1);
   }

   public int getBlue(Object var1) {
      return this.getRGBComponent(var1, 2);
   }

   public int getAlpha(Object var1) {
      if (!this.supportsAlpha) {
         return 255;
      } else {
         boolean var2 = false;
         int var3 = this.numColorComponents;
         int var4 = (1 << this.nBits[var3]) - 1;
         int var11;
         switch(this.transferType) {
         case 0:
            byte[] var8 = (byte[])((byte[])var1);
            var11 = var8[var3] & var4;
            break;
         case 1:
            short[] var9 = (short[])((short[])var1);
            var11 = var9[var3] & var4;
            break;
         case 2:
            short[] var5 = (short[])((short[])var1);
            var11 = (int)((float)var5[var3] / 32767.0F * 255.0F + 0.5F);
            return var11;
         case 3:
            int[] var10 = (int[])((int[])var1);
            var11 = var10[var3];
            break;
         case 4:
            float[] var6 = (float[])((float[])var1);
            var11 = (int)(var6[var3] * 255.0F + 0.5F);
            return var11;
         case 5:
            double[] var7 = (double[])((double[])var1);
            var11 = (int)(var7[var3] * 255.0D + 0.5D);
            return var11;
         default:
            throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
         }

         return this.nBits[var3] == 8 ? var11 : (int)((float)var11 / (float)((1 << this.nBits[var3]) - 1) * 255.0F + 0.5F);
      }
   }

   public int getRGB(Object var1) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (!this.is_sRGB_stdScale && !this.is_LinearRGB_stdScale) {
         if (this.colorSpaceType == 6) {
            int var4 = this.getRed(var1);
            return this.getAlpha(var1) << 24 | var4 << 16 | var4 << 8 | var4;
         } else {
            float[] var2 = this.getNormalizedComponents(var1, (float[])null, 0);
            float[] var3 = this.colorSpace.toRGB(var2);
            return this.getAlpha(var1) << 24 | (int)(var3[0] * 255.0F + 0.5F) << 16 | (int)(var3[1] * 255.0F + 0.5F) << 8 | (int)(var3[2] * 255.0F + 0.5F) << 0;
         }
      } else {
         return this.getAlpha(var1) << 24 | this.getRed(var1) << 16 | this.getGreen(var1) << 8 | this.getBlue(var1);
      }
   }

   public Object getDataElements(int var1, Object var2) {
      int var3 = var1 >> 16 & 255;
      int var4 = var1 >> 8 & 255;
      int var5 = var1 & 255;
      if (this.needScaleInit) {
         this.initScale();
      }

      int var6;
      int var10;
      int var11;
      int var12;
      float var14;
      float var15;
      if (this.signed) {
         float[] var9;
         switch(this.transferType) {
         case 2:
            short[] var16;
            if (var2 == null) {
               var16 = new short[this.numComponents];
            } else {
               var16 = (short[])((short[])var2);
            }

            if (!this.is_sRGB_stdScale && !this.is_LinearRGB_stdScale) {
               if (this.is_LinearGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var15 = (0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5) / 65535.0F;
                  var14 = 32767.0F;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var16[1] = (short)((int)((float)var6 * 128.49803F + 0.5F));
                     if (this.isAlphaPremultiplied) {
                        var14 = (float)var6 * var14 * 0.003921569F;
                     }
                  }

                  var16[0] = (short)((int)(var15 * var14 + 0.5F));
               } else if (this.is_ICCGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var11 = (int)(0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5 + 0.5F);
                  var11 = this.fromLinearGray16ToOtherGray16LUT[var11] & '\uffff';
                  var14 = 0.49999237F;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var16[1] = (short)((int)((float)var6 * 128.49803F + 0.5F));
                     if (this.isAlphaPremultiplied) {
                        var14 = (float)var6 * var14 * 0.003921569F;
                     }
                  }

                  var16[0] = (short)((int)((float)var11 * var14 + 0.5F));
               } else {
                  var14 = 0.003921569F;
                  var9 = new float[]{(float)var3 * var14, (float)var4 * var14, (float)var5 * var14};
                  var9 = this.colorSpace.fromRGB(var9);
                  if (this.nonStdScale) {
                     for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                        var9[var10] = (var9[var10] - this.compOffset[var10]) * this.compScale[var10];
                        if (var9[var10] < 0.0F) {
                           var9[var10] = 0.0F;
                        }

                        if (var9[var10] > 1.0F) {
                           var9[var10] = 1.0F;
                        }
                     }
                  }

                  var14 = 32767.0F;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var16[this.numColorComponents] = (short)((int)((float)var6 * 128.49803F + 0.5F));
                     if (this.isAlphaPremultiplied) {
                        var14 *= (float)var6 * 0.003921569F;
                     }
                  }

                  for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                     var16[var10] = (short)((int)(var9[var10] * var14 + 0.5F));
                  }
               }
            } else {
               var14 = 128.49803F;
               if (this.is_LinearRGB_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var14 = 0.49999237F;
               }

               if (this.supportsAlpha) {
                  var6 = var1 >> 24 & 255;
                  var16[3] = (short)((int)((float)var6 * 128.49803F + 0.5F));
                  if (this.isAlphaPremultiplied) {
                     var14 = (float)var6 * var14 * 0.003921569F;
                  }
               }

               var16[0] = (short)((int)((float)var3 * var14 + 0.5F));
               var16[1] = (short)((int)((float)var4 * var14 + 0.5F));
               var16[2] = (short)((int)((float)var5 * var14 + 0.5F));
            }

            return var16;
         case 3:
         default:
            break;
         case 4:
            float[] var13;
            if (var2 == null) {
               var13 = new float[this.numComponents];
            } else {
               var13 = (float[])((float[])var2);
            }

            if (!this.is_sRGB_stdScale && !this.is_LinearRGB_stdScale) {
               if (this.is_LinearGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var13[0] = (0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5) / 65535.0F;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var13[1] = (float)var6 * 0.003921569F;
                     if (this.isAlphaPremultiplied) {
                        var13[0] *= var13[1];
                     }
                  }
               } else if (this.is_ICCGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var11 = (int)(0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5 + 0.5F);
                  var13[0] = (float)(this.fromLinearGray16ToOtherGray16LUT[var11] & '\uffff') / 65535.0F;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var13[1] = (float)var6 * 0.003921569F;
                     if (this.isAlphaPremultiplied) {
                        var13[0] *= var13[1];
                     }
                  }
               } else {
                  var9 = new float[3];
                  var14 = 0.003921569F;
                  var9[0] = (float)var3 * var14;
                  var9[1] = (float)var4 * var14;
                  var9[2] = (float)var5 * var14;
                  var9 = this.colorSpace.fromRGB(var9);
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var13[this.numColorComponents] = (float)var6 * var14;
                     if (this.isAlphaPremultiplied) {
                        var14 *= (float)var6;

                        for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                           var9[var10] *= var14;
                        }
                     }
                  }

                  for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                     var13[var10] = var9[var10];
                  }
               }
            } else {
               if (this.is_LinearRGB_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var14 = 1.5259022E-5F;
               } else {
                  var14 = 0.003921569F;
               }

               if (this.supportsAlpha) {
                  var6 = var1 >> 24 & 255;
                  var13[3] = (float)var6 * 0.003921569F;
                  if (this.isAlphaPremultiplied) {
                     var14 *= var13[3];
                  }
               }

               var13[0] = (float)var3 * var14;
               var13[1] = (float)var4 * var14;
               var13[2] = (float)var5 * var14;
            }

            return var13;
         case 5:
            double[] var7;
            if (var2 == null) {
               var7 = new double[this.numComponents];
            } else {
               var7 = (double[])((double[])var2);
            }

            if (!this.is_sRGB_stdScale && !this.is_LinearRGB_stdScale) {
               if (this.is_LinearGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var7[0] = (0.2125D * (double)var3 + 0.7154D * (double)var4 + 0.0721D * (double)var5) / 65535.0D;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var7[1] = (double)var6 * 0.00392156862745098D;
                     if (this.isAlphaPremultiplied) {
                        var7[0] *= var7[1];
                     }
                  }
               } else if (this.is_ICCGray_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var12 = (int)(0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5 + 0.5F);
                  var7[0] = (double)(this.fromLinearGray16ToOtherGray16LUT[var12] & '\uffff') / 65535.0D;
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var7[1] = (double)var6 * 0.00392156862745098D;
                     if (this.isAlphaPremultiplied) {
                        var7[0] *= var7[1];
                     }
                  }
               } else {
                  var14 = 0.003921569F;
                  var9 = new float[]{(float)var3 * var14, (float)var4 * var14, (float)var5 * var14};
                  var9 = this.colorSpace.fromRGB(var9);
                  if (this.supportsAlpha) {
                     var6 = var1 >> 24 & 255;
                     var7[this.numColorComponents] = (double)var6 * 0.00392156862745098D;
                     if (this.isAlphaPremultiplied) {
                        var14 *= (float)var6;

                        for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                           var9[var10] *= var14;
                        }
                     }
                  }

                  for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                     var7[var10] = (double)var9[var10];
                  }
               }
            } else {
               double var8;
               if (this.is_LinearRGB_stdScale) {
                  var3 = this.fromsRGB8LUT16[var3] & '\uffff';
                  var4 = this.fromsRGB8LUT16[var4] & '\uffff';
                  var5 = this.fromsRGB8LUT16[var5] & '\uffff';
                  var8 = 1.5259021896696422E-5D;
               } else {
                  var8 = 0.00392156862745098D;
               }

               if (this.supportsAlpha) {
                  var6 = var1 >> 24 & 255;
                  var7[3] = (double)var6 * 0.00392156862745098D;
                  if (this.isAlphaPremultiplied) {
                     var8 *= var7[3];
                  }
               }

               var7[0] = (double)var3 * var8;
               var7[1] = (double)var4 * var8;
               var7[2] = (double)var5 * var8;
            }

            return var7;
         }
      }

      int[] var17;
      if (this.transferType == 3 && var2 != null) {
         var17 = (int[])((int[])var2);
      } else {
         var17 = new int[this.numComponents];
      }

      if (!this.is_sRGB_stdScale && !this.is_LinearRGB_stdScale) {
         if (this.is_LinearGray_stdScale) {
            var3 = this.fromsRGB8LUT16[var3] & '\uffff';
            var4 = this.fromsRGB8LUT16[var4] & '\uffff';
            var5 = this.fromsRGB8LUT16[var5] & '\uffff';
            var14 = (0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5) / 65535.0F;
            if (this.supportsAlpha) {
               var6 = var1 >> 24 & 255;
               if (this.nBits[1] == 8) {
                  var17[1] = var6;
               } else {
                  var17[1] = (int)((float)var6 * 0.003921569F * (float)((1 << this.nBits[1]) - 1) + 0.5F);
               }

               if (this.isAlphaPremultiplied) {
                  var14 *= (float)var6 * 0.003921569F;
               }
            }

            var17[0] = (int)(var14 * (float)((1 << this.nBits[0]) - 1) + 0.5F);
         } else if (this.is_ICCGray_stdScale) {
            var3 = this.fromsRGB8LUT16[var3] & '\uffff';
            var4 = this.fromsRGB8LUT16[var4] & '\uffff';
            var5 = this.fromsRGB8LUT16[var5] & '\uffff';
            var12 = (int)(0.2125F * (float)var3 + 0.7154F * (float)var4 + 0.0721F * (float)var5 + 0.5F);
            var15 = (float)(this.fromLinearGray16ToOtherGray16LUT[var12] & '\uffff') / 65535.0F;
            if (this.supportsAlpha) {
               var6 = var1 >> 24 & 255;
               if (this.nBits[1] == 8) {
                  var17[1] = var6;
               } else {
                  var17[1] = (int)((float)var6 * 0.003921569F * (float)((1 << this.nBits[1]) - 1) + 0.5F);
               }

               if (this.isAlphaPremultiplied) {
                  var15 *= (float)var6 * 0.003921569F;
               }
            }

            var17[0] = (int)(var15 * (float)((1 << this.nBits[0]) - 1) + 0.5F);
         } else {
            float[] var19 = new float[3];
            var15 = 0.003921569F;
            var19[0] = (float)var3 * var15;
            var19[1] = (float)var4 * var15;
            var19[2] = (float)var5 * var15;
            var19 = this.colorSpace.fromRGB(var19);
            if (this.nonStdScale) {
               for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                  var19[var10] = (var19[var10] - this.compOffset[var10]) * this.compScale[var10];
                  if (var19[var10] < 0.0F) {
                     var19[var10] = 0.0F;
                  }

                  if (var19[var10] > 1.0F) {
                     var19[var10] = 1.0F;
                  }
               }
            }

            if (this.supportsAlpha) {
               var6 = var1 >> 24 & 255;
               if (this.nBits[this.numColorComponents] == 8) {
                  var17[this.numColorComponents] = var6;
               } else {
                  var17[this.numColorComponents] = (int)((float)var6 * var15 * (float)((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
               }

               if (this.isAlphaPremultiplied) {
                  var15 *= (float)var6;

                  for(var10 = 0; var10 < this.numColorComponents; ++var10) {
                     var19[var10] *= var15;
                  }
               }
            }

            for(var10 = 0; var10 < this.numColorComponents; ++var10) {
               var17[var10] = (int)(var19[var10] * (float)((1 << this.nBits[var10]) - 1) + 0.5F);
            }
         }
      } else {
         byte var18;
         if (this.is_LinearRGB_stdScale) {
            if (this.transferType == 0) {
               var3 = this.fromsRGB8LUT8[var3] & 255;
               var4 = this.fromsRGB8LUT8[var4] & 255;
               var5 = this.fromsRGB8LUT8[var5] & 255;
               var18 = 8;
               var15 = 0.003921569F;
            } else {
               var3 = this.fromsRGB8LUT16[var3] & '\uffff';
               var4 = this.fromsRGB8LUT16[var4] & '\uffff';
               var5 = this.fromsRGB8LUT16[var5] & '\uffff';
               var18 = 16;
               var15 = 1.5259022E-5F;
            }
         } else {
            var18 = 8;
            var15 = 0.003921569F;
         }

         if (this.supportsAlpha) {
            var6 = var1 >> 24 & 255;
            if (this.nBits[3] == 8) {
               var17[3] = var6;
            } else {
               var17[3] = (int)((float)var6 * 0.003921569F * (float)((1 << this.nBits[3]) - 1) + 0.5F);
            }

            if (this.isAlphaPremultiplied) {
               var15 *= (float)var6 * 0.003921569F;
               var18 = -1;
            }
         }

         if (this.nBits[0] == var18) {
            var17[0] = var3;
         } else {
            var17[0] = (int)((float)var3 * var15 * (float)((1 << this.nBits[0]) - 1) + 0.5F);
         }

         if (this.nBits[1] == var18) {
            var17[1] = var4;
         } else {
            var17[1] = (int)((float)var4 * var15 * (float)((1 << this.nBits[1]) - 1) + 0.5F);
         }

         if (this.nBits[2] == var18) {
            var17[2] = var5;
         } else {
            var17[2] = (int)((float)var5 * var15 * (float)((1 << this.nBits[2]) - 1) + 0.5F);
         }
      }

      switch(this.transferType) {
      case 0:
         byte[] var21;
         if (var2 == null) {
            var21 = new byte[this.numComponents];
         } else {
            var21 = (byte[])((byte[])var2);
         }

         for(var11 = 0; var11 < this.numComponents; ++var11) {
            var21[var11] = (byte)(255 & var17[var11]);
         }

         return var21;
      case 1:
         short[] var20;
         if (var2 == null) {
            var20 = new short[this.numComponents];
         } else {
            var20 = (short[])((short[])var2);
         }

         for(var11 = 0; var11 < this.numComponents; ++var11) {
            var20[var11] = (short)(var17[var11] & '\uffff');
         }

         return var20;
      case 2:
      default:
         throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         if (this.maxBits > 23) {
            for(var12 = 0; var12 < this.numComponents; ++var12) {
               if (var17[var12] > (1 << this.nBits[var12]) - 1) {
                  var17[var12] = (1 << this.nBits[var12]) - 1;
               }
            }
         }

         return var17;
      }
   }

   public int[] getComponents(int var1, int[] var2, int var3) {
      if (this.numComponents > 1) {
         throw new IllegalArgumentException("More than one component per pixel");
      } else {
         if (this.needScaleInit) {
            this.initScale();
         }

         if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
         } else {
            if (var2 == null) {
               var2 = new int[var3 + 1];
            }

            var2[var3 + 0] = var1 & (1 << this.nBits[0]) - 1;
            return var2;
         }
      }
   }

   public int[] getComponents(Object var1, int[] var2, int var3) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.noUnnorm) {
         throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
      } else {
         int[] var4;
         if (var1 instanceof int[]) {
            var4 = (int[])((int[])var1);
         } else {
            var4 = DataBuffer.toIntArray(var1);
            if (var4 == null) {
               throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
         }

         if (var4.length < this.numComponents) {
            throw new IllegalArgumentException("Length of pixel array < number of components in model");
         } else {
            if (var2 == null) {
               var2 = new int[var3 + this.numComponents];
            } else if (var2.length - var3 < this.numComponents) {
               throw new IllegalArgumentException("Length of components array < number of components in model");
            }

            System.arraycopy(var4, 0, var2, var3, this.numComponents);
            return var2;
         }
      }
   }

   public int[] getUnnormalizedComponents(float[] var1, int var2, int[] var3, int var4) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.noUnnorm) {
         throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
      } else {
         return super.getUnnormalizedComponents(var1, var2, var3, var4);
      }
   }

   public float[] getNormalizedComponents(int[] var1, int var2, float[] var3, int var4) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.noUnnorm) {
         throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
      } else {
         return super.getNormalizedComponents(var1, var2, var3, var4);
      }
   }

   public int getDataElement(int[] var1, int var2) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.numComponents == 1) {
         if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
         } else {
            return var1[var2 + 0];
         }
      } else {
         throw new IllegalArgumentException("This model returns " + this.numComponents + " elements in the pixel array.");
      }
   }

   public Object getDataElements(int[] var1, int var2, Object var3) {
      if (this.needScaleInit) {
         this.initScale();
      }

      if (this.noUnnorm) {
         throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
      } else if (var1.length - var2 < this.numComponents) {
         throw new IllegalArgumentException("Component array too small (should be " + this.numComponents);
      } else {
         int var5;
         switch(this.transferType) {
         case 0:
            byte[] var7;
            if (var3 == null) {
               var7 = new byte[this.numComponents];
            } else {
               var7 = (byte[])((byte[])var3);
            }

            for(var5 = 0; var5 < this.numComponents; ++var5) {
               var7[var5] = (byte)(var1[var2 + var5] & 255);
            }

            return var7;
         case 1:
            short[] var6;
            if (var3 == null) {
               var6 = new short[this.numComponents];
            } else {
               var6 = (short[])((short[])var3);
            }

            for(var5 = 0; var5 < this.numComponents; ++var5) {
               var6[var5] = (short)(var1[var2 + var5] & '\uffff');
            }

            return var6;
         case 2:
         default:
            throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
         case 3:
            int[] var4;
            if (var3 == null) {
               var4 = new int[this.numComponents];
            } else {
               var4 = (int[])((int[])var3);
            }

            System.arraycopy(var1, var2, var4, 0, this.numComponents);
            return var4;
         }
      }
   }

   public int getDataElement(float[] var1, int var2) {
      if (this.numComponents > 1) {
         throw new IllegalArgumentException("More than one component per pixel");
      } else if (this.signed) {
         throw new IllegalArgumentException("Component value is signed");
      } else {
         if (this.needScaleInit) {
            this.initScale();
         }

         Object var3 = this.getDataElements((float[])var1, var2, (Object)null);
         switch(this.transferType) {
         case 0:
            byte[] var6 = (byte[])((byte[])var3);
            return var6[0] & 255;
         case 1:
            short[] var5 = (short[])((short[])var3);
            return var5[0] & '\uffff';
         case 2:
         default:
            throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
         case 3:
            int[] var4 = (int[])((int[])var3);
            return var4[0];
         }
      }
   }

   public Object getDataElements(float[] var1, int var2, Object var3) {
      boolean var4 = this.supportsAlpha && this.isAlphaPremultiplied;
      if (this.needScaleInit) {
         this.initScale();
      }

      float[] var5;
      int var7;
      if (this.nonStdScale) {
         var5 = new float[this.numComponents];
         int var6 = 0;

         for(var7 = var2; var6 < this.numColorComponents; ++var7) {
            var5[var6] = (var1[var7] - this.compOffset[var6]) * this.compScale[var6];
            if (var5[var6] < 0.0F) {
               var5[var6] = 0.0F;
            }

            if (var5[var6] > 1.0F) {
               var5[var6] = 1.0F;
            }

            ++var6;
         }

         if (this.supportsAlpha) {
            var5[this.numColorComponents] = var1[this.numColorComponents + var2];
         }

         var2 = 0;
      } else {
         var5 = var1;
      }

      int var13;
      int var18;
      int var22;
      int var23;
      int var24;
      int var27;
      switch(this.transferType) {
      case 0:
         byte[] var16;
         if (var3 == null) {
            var16 = new byte[this.numComponents];
         } else {
            var16 = (byte[])((byte[])var3);
         }

         if (var4) {
            float var19 = var5[this.numColorComponents + var2];
            var22 = 0;

            for(var18 = var2; var22 < this.numColorComponents; ++var18) {
               var16[var22] = (byte)((int)(var5[var18] * var19 * (float)((1 << this.nBits[var22]) - 1) + 0.5F));
               ++var22;
            }

            var16[this.numColorComponents] = (byte)((int)(var19 * (float)((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
         } else {
            var7 = 0;

            for(var22 = var2; var7 < this.numComponents; ++var22) {
               var16[var7] = (byte)((int)(var5[var22] * (float)((1 << this.nBits[var7]) - 1) + 0.5F));
               ++var7;
            }
         }

         return var16;
      case 1:
         short[] var17;
         if (var3 == null) {
            var17 = new short[this.numComponents];
         } else {
            var17 = (short[])((short[])var3);
         }

         if (var4) {
            float var21 = var5[this.numColorComponents + var2];
            var18 = 0;

            for(var23 = var2; var18 < this.numColorComponents; ++var23) {
               var17[var18] = (short)((int)(var5[var23] * var21 * (float)((1 << this.nBits[var18]) - 1) + 0.5F));
               ++var18;
            }

            var17[this.numColorComponents] = (short)((int)(var21 * (float)((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
         } else {
            var22 = 0;

            for(var18 = var2; var22 < this.numComponents; ++var18) {
               var17[var22] = (short)((int)(var5[var18] * (float)((1 << this.nBits[var22]) - 1) + 0.5F));
               ++var22;
            }
         }

         return var17;
      case 2:
         short[] var20;
         if (var3 == null) {
            var20 = new short[this.numComponents];
         } else {
            var20 = (short[])((short[])var3);
         }

         if (var4) {
            float var26 = var5[this.numColorComponents + var2];
            var27 = 0;

            for(var24 = var2; var27 < this.numColorComponents; ++var24) {
               var20[var27] = (short)((int)(var5[var24] * var26 * 32767.0F + 0.5F));
               ++var27;
            }

            var20[this.numColorComponents] = (short)((int)(var26 * 32767.0F + 0.5F));
         } else {
            var23 = 0;

            for(var27 = var2; var23 < this.numComponents; ++var27) {
               var20[var23] = (short)((int)(var5[var27] * 32767.0F + 0.5F));
               ++var23;
            }
         }

         return var20;
      case 3:
         int[] var8;
         if (var3 == null) {
            var8 = new int[this.numComponents];
         } else {
            var8 = (int[])((int[])var3);
         }

         if (var4) {
            float var9 = var5[this.numColorComponents + var2];
            var23 = 0;

            for(var27 = var2; var23 < this.numColorComponents; ++var27) {
               var8[var23] = (int)(var5[var27] * var9 * (float)((1 << this.nBits[var23]) - 1) + 0.5F);
               ++var23;
            }

            var8[this.numColorComponents] = (int)(var9 * (float)((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
         } else {
            var18 = 0;

            for(var23 = var2; var18 < this.numComponents; ++var23) {
               var8[var18] = (int)(var5[var23] * (float)((1 << this.nBits[var18]) - 1) + 0.5F);
               ++var18;
            }
         }

         return var8;
      case 4:
         float[] var10;
         if (var3 == null) {
            var10 = new float[this.numComponents];
         } else {
            var10 = (float[])((float[])var3);
         }

         if (var4) {
            float var25 = var1[this.numColorComponents + var2];
            var24 = 0;

            for(var13 = var2; var24 < this.numColorComponents; ++var13) {
               var10[var24] = var1[var13] * var25;
               ++var24;
            }

            var10[this.numColorComponents] = var25;
         } else {
            var27 = 0;

            for(var24 = var2; var27 < this.numComponents; ++var24) {
               var10[var27] = var1[var24];
               ++var27;
            }
         }

         return var10;
      case 5:
         double[] var11;
         if (var3 == null) {
            var11 = new double[this.numComponents];
         } else {
            var11 = (double[])((double[])var3);
         }

         if (var4) {
            double var12 = (double)var1[this.numColorComponents + var2];
            int var14 = 0;

            for(int var15 = var2; var14 < this.numColorComponents; ++var15) {
               var11[var14] = (double)var1[var15] * var12;
               ++var14;
            }

            var11[this.numColorComponents] = var12;
         } else {
            var24 = 0;

            for(var13 = var2; var24 < this.numComponents; ++var13) {
               var11[var24] = (double)var1[var13];
               ++var24;
            }
         }

         return var11;
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }
   }

   public float[] getNormalizedComponents(Object var1, float[] var2, int var3) {
      if (var2 == null) {
         var2 = new float[this.numComponents + var3];
      }

      int var16;
      int var7;
      int var10;
      int var18;
      int var19;
      label90:
      switch(this.transferType) {
      case 0:
         byte[] var4 = (byte[])((byte[])var1);
         int var14 = 0;
         var16 = var3;

         while(true) {
            if (var14 >= this.numComponents) {
               break label90;
            }

            var2[var16] = (float)(var4[var14] & 255) / (float)((1 << this.nBits[var14]) - 1);
            ++var14;
            ++var16;
         }
      case 1:
         short[] var5 = (short[])((short[])var1);
         var16 = 0;
         var7 = var3;

         while(true) {
            if (var16 >= this.numComponents) {
               break label90;
            }

            var2[var7] = (float)(var5[var16] & '\uffff') / (float)((1 << this.nBits[var16]) - 1);
            ++var16;
            ++var7;
         }
      case 2:
         short[] var17 = (short[])((short[])var1);
         var18 = 0;
         var19 = var3;

         while(true) {
            if (var18 >= this.numComponents) {
               break label90;
            }

            var2[var19] = (float)var17[var18] / 32767.0F;
            ++var18;
            ++var19;
         }
      case 3:
         int[] var6 = (int[])((int[])var1);
         var7 = 0;
         var18 = var3;

         while(true) {
            if (var7 >= this.numComponents) {
               break label90;
            }

            var2[var18] = (float)var6[var7] / (float)((1 << this.nBits[var7]) - 1);
            ++var7;
            ++var18;
         }
      case 4:
         float[] var8 = (float[])((float[])var1);
         var19 = 0;
         var10 = var3;

         while(true) {
            if (var19 >= this.numComponents) {
               break label90;
            }

            var2[var10] = var8[var19];
            ++var19;
            ++var10;
         }
      case 5:
         double[] var9 = (double[])((double[])var1);
         var10 = 0;
         int var11 = var3;

         while(true) {
            if (var10 >= this.numComponents) {
               break label90;
            }

            var2[var11] = (float)var9[var10];
            ++var10;
            ++var11;
         }
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }

      if (this.supportsAlpha && this.isAlphaPremultiplied) {
         float var12 = var2[this.numColorComponents + var3];
         if (var12 != 0.0F) {
            float var15 = 1.0F / var12;

            for(var16 = var3; var16 < this.numColorComponents + var3; ++var16) {
               var2[var16] *= var15;
            }
         }
      }

      if (this.min != null) {
         for(int var13 = 0; var13 < this.numColorComponents; ++var13) {
            var2[var13 + var3] = this.min[var13] + this.diffMinMax[var13] * var2[var13 + var3];
         }
      }

      return var2;
   }

   public ColorModel coerceData(WritableRaster var1, boolean var2) {
      if (this.supportsAlpha && this.isAlphaPremultiplied != var2) {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         int var5 = var1.getNumBands() - 1;
         int var7 = var1.getMinX();
         int var8 = var1.getMinY();
         float var6;
         int var9;
         double[] var10;
         int var12;
         int var13;
         int var15;
         float[] var18;
         int[] var19;
         short[] var20;
         byte[] var23;
         int var26;
         if (var2) {
            float var24;
            short[] var25;
            switch(this.transferType) {
            case 0:
               var23 = null;
               byte[] var27 = null;
               var24 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var13 = 0; var13 < var4; ++var8) {
                  var9 = var7;

                  for(var26 = 0; var26 < var3; ++var9) {
                     var23 = (byte[])((byte[])var1.getDataElements(var9, var8, var23));
                     var6 = (float)(var23[var5] & 255) * var24;
                     if (var6 == 0.0F) {
                        if (var27 == null) {
                           var27 = new byte[this.numComponents];
                           Arrays.fill((byte[])var27, (byte)0);
                        }

                        var1.setDataElements(var9, var8, (Object)var27);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var23[var15] = (byte)((int)((float)(var23[var15] & 255) * var6 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var23);
                     }

                     ++var26;
                  }

                  ++var13;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 1:
               var20 = null;
               var25 = null;
               var24 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var13 = 0; var13 < var4; ++var8) {
                  var9 = var7;

                  for(var26 = 0; var26 < var3; ++var9) {
                     var20 = (short[])((short[])var1.getDataElements(var9, var8, var20));
                     var6 = (float)(var20[var5] & '\uffff') * var24;
                     if (var6 == 0.0F) {
                        if (var25 == null) {
                           var25 = new short[this.numComponents];
                           Arrays.fill((short[])var25, (short)0);
                        }

                        var1.setDataElements(var9, var8, (Object)var25);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var20[var15] = (short)((int)((float)(var20[var15] & '\uffff') * var6 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var20);
                     }

                     ++var26;
                  }

                  ++var13;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 2:
               var20 = null;
               var25 = null;
               var24 = 3.051851E-5F;

               for(var13 = 0; var13 < var4; ++var8) {
                  var9 = var7;

                  for(var26 = 0; var26 < var3; ++var9) {
                     var20 = (short[])((short[])var1.getDataElements(var9, var8, var20));
                     var6 = (float)var20[var5] * var24;
                     if (var6 == 0.0F) {
                        if (var25 == null) {
                           var25 = new short[this.numComponents];
                           Arrays.fill((short[])var25, (short)0);
                        }

                        var1.setDataElements(var9, var8, (Object)var25);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var20[var15] = (short)((int)((float)var20[var15] * var6 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var20);
                     }

                     ++var26;
                  }

                  ++var13;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 3:
               var19 = null;
               int[] var22 = null;
               var24 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var13 = 0; var13 < var4; ++var8) {
                  var9 = var7;

                  for(var26 = 0; var26 < var3; ++var9) {
                     var19 = (int[])((int[])var1.getDataElements(var9, var8, var19));
                     var6 = (float)var19[var5] * var24;
                     if (var6 == 0.0F) {
                        if (var22 == null) {
                           var22 = new int[this.numComponents];
                           Arrays.fill((int[])var22, (int)0);
                        }

                        var1.setDataElements(var9, var8, (Object)var22);
                     } else {
                        for(var15 = 0; var15 < var5; ++var15) {
                           var19[var15] = (int)((float)var19[var15] * var6 + 0.5F);
                        }

                        var1.setDataElements(var9, var8, (Object)var19);
                     }

                     ++var26;
                  }

                  ++var13;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 4:
               var18 = null;
               float[] var21 = null;

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var18 = (float[])((float[])var1.getDataElements(var9, var8, var18));
                     var6 = var18[var5];
                     if (var6 == 0.0F) {
                        if (var21 == null) {
                           var21 = new float[this.numComponents];
                           Arrays.fill(var21, 0.0F);
                        }

                        var1.setDataElements(var9, var8, (Object)var21);
                     } else {
                        for(var26 = 0; var26 < var5; ++var26) {
                           var18[var26] *= var6;
                        }

                        var1.setDataElements(var9, var8, (Object)var18);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 5:
               var10 = null;
               double[] var11 = null;

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var10 = (double[])((double[])var1.getDataElements(var9, var8, var10));
                     double var14 = var10[var5];
                     if (var14 == 0.0D) {
                        if (var11 == null) {
                           var11 = new double[this.numComponents];
                           Arrays.fill(var11, 0.0D);
                        }

                        var1.setDataElements(var9, var8, (Object)var11);
                     } else {
                        for(int var16 = 0; var16 < var5; ++var16) {
                           var10[var16] *= var14;
                        }

                        var1.setDataElements(var9, var8, (Object)var10);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            default:
               throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
         } else {
            int var28;
            float var32;
            float var33;
            switch(this.transferType) {
            case 0:
               var23 = null;
               var32 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var23 = (byte[])((byte[])var1.getDataElements(var9, var8, var23));
                     var6 = (float)(var23[var5] & 255) * var32;
                     if (var6 != 0.0F) {
                        var33 = 1.0F / var6;

                        for(var15 = 0; var15 < var5; ++var15) {
                           var23[var15] = (byte)((int)((float)(var23[var15] & 255) * var33 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var23);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 1:
               var20 = null;
               var32 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var20 = (short[])((short[])var1.getDataElements(var9, var8, var20));
                     var6 = (float)(var20[var5] & '\uffff') * var32;
                     if (var6 != 0.0F) {
                        var33 = 1.0F / var6;

                        for(var15 = 0; var15 < var5; ++var15) {
                           var20[var15] = (short)((int)((float)(var20[var15] & '\uffff') * var33 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var20);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 2:
               var20 = null;
               var32 = 3.051851E-5F;

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var20 = (short[])((short[])var1.getDataElements(var9, var8, var20));
                     var6 = (float)var20[var5] * var32;
                     if (var6 != 0.0F) {
                        var33 = 1.0F / var6;

                        for(var15 = 0; var15 < var5; ++var15) {
                           var20[var15] = (short)((int)((float)var20[var15] * var33 + 0.5F));
                        }

                        var1.setDataElements(var9, var8, (Object)var20);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 3:
               var19 = null;
               var32 = 1.0F / (float)((1 << this.nBits[var5]) - 1);

               for(var12 = 0; var12 < var4; ++var8) {
                  var9 = var7;

                  for(var13 = 0; var13 < var3; ++var9) {
                     var19 = (int[])((int[])var1.getDataElements(var9, var8, var19));
                     var6 = (float)var19[var5] * var32;
                     if (var6 != 0.0F) {
                        var33 = 1.0F / var6;

                        for(var15 = 0; var15 < var5; ++var15) {
                           var19[var15] = (int)((float)var19[var15] * var33 + 0.5F);
                        }

                        var1.setDataElements(var9, var8, (Object)var19);
                     }

                     ++var13;
                  }

                  ++var12;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 4:
               var18 = null;

               for(var28 = 0; var28 < var4; ++var8) {
                  var9 = var7;

                  for(var12 = 0; var12 < var3; ++var9) {
                     var18 = (float[])((float[])var1.getDataElements(var9, var8, var18));
                     var6 = var18[var5];
                     if (var6 != 0.0F) {
                        float var30 = 1.0F / var6;

                        for(var26 = 0; var26 < var5; ++var26) {
                           var18[var26] *= var30;
                        }

                        var1.setDataElements(var9, var8, (Object)var18);
                     }

                     ++var12;
                  }

                  ++var28;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            case 5:
               var10 = null;

               for(var28 = 0; var28 < var4; ++var8) {
                  var9 = var7;

                  for(var12 = 0; var12 < var3; ++var9) {
                     var10 = (double[])((double[])var1.getDataElements(var9, var8, var10));
                     double var29 = var10[var5];
                     if (var29 != 0.0D) {
                        double var31 = 1.0D / var29;

                        for(int var17 = 0; var17 < var5; ++var17) {
                           var10[var17] *= var31;
                        }

                        var1.setDataElements(var9, var8, (Object)var10);
                     }

                     ++var12;
                  }

                  ++var28;
               }

               return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, var2, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, var2, this.transparency, this.transferType);
            default:
               throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
         }
      } else {
         return this;
      }
   }

   public boolean isCompatibleRaster(Raster var1) {
      SampleModel var2 = var1.getSampleModel();
      if (var2 instanceof ComponentSampleModel) {
         if (var2.getNumBands() != this.getNumComponents()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.nBits.length; ++var3) {
               if (var2.getSampleSize(var3) < this.nBits[var3]) {
                  return false;
               }
            }

            return var1.getTransferType() == this.transferType;
         }
      } else {
         return false;
      }
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      int var3 = var1 * var2 * this.numComponents;
      WritableRaster var4 = null;
      switch(this.transferType) {
      case 0:
      case 1:
         var4 = Raster.createInterleavedRaster(this.transferType, var1, var2, this.numComponents, (Point)null);
         break;
      default:
         SampleModel var5 = this.createCompatibleSampleModel(var1, var2);
         DataBuffer var6 = var5.createDataBuffer();
         var4 = Raster.createWritableRaster(var5, var6, (Point)null);
      }

      return var4;
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      int[] var3 = new int[this.numComponents];

      for(int var4 = 0; var4 < this.numComponents; var3[var4] = var4++) {
      }

      switch(this.transferType) {
      case 0:
      case 1:
         return new PixelInterleavedSampleModel(this.transferType, var1, var2, this.numComponents, var1 * this.numComponents, var3);
      default:
         return new ComponentSampleModel(this.transferType, var1, var2, this.numComponents, var1 * this.numComponents, var3);
      }
   }

   public boolean isCompatibleSampleModel(SampleModel var1) {
      if (!(var1 instanceof ComponentSampleModel)) {
         return false;
      } else if (this.numComponents != var1.getNumBands()) {
         return false;
      } else {
         return var1.getTransferType() == this.transferType;
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
      if (!super.equals(var1)) {
         return false;
      } else {
         return var1.getClass() == this.getClass();
      }
   }
}
