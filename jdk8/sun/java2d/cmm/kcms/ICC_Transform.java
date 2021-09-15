package sun.java2d.cmm.kcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ICC_Transform implements ColorTransform {
   long ID;

   long getID() {
      return this.ID;
   }

   public void finalize() {
      CMM.checkStatus(CMM.cmmFreeTransform(this.ID));
   }

   public int getNumInComponents() {
      int[] var1 = new int[2];
      CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, var1));
      return var1[0];
   }

   public int getNumOutComponents() {
      int[] var1 = new int[2];
      CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, var1));
      return var1[1];
   }

   public void colorConvert(BufferedImage var1, BufferedImage var2) {
      CMMImageLayout var3 = this.getImageLayout(var1);
      CMMImageLayout var4;
      if (var3 != null) {
         var4 = this.getImageLayout(var2);
         if (var4 != null) {
            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
               return;
            }
         }
      }

      WritableRaster var5 = var1.getRaster();
      WritableRaster var6 = var2.getRaster();
      ColorModel var7 = var1.getColorModel();
      ColorModel var8 = var2.getColorModel();
      int var9 = var1.getWidth();
      int var10 = var1.getHeight();
      int var11 = var7.getNumColorComponents();
      int var12 = var8.getNumColorComponents();
      byte var13 = 8;
      float var14 = 255.0F;

      int var15;
      for(var15 = 0; var15 < var11; ++var15) {
         if (var7.getComponentSize(var15) > 8) {
            var13 = 16;
            var14 = 65535.0F;
         }
      }

      for(var15 = 0; var15 < var12; ++var15) {
         if (var8.getComponentSize(var15) > 8) {
            var13 = 16;
            var14 = 65535.0F;
         }
      }

      float[] var40 = new float[var11];
      float[] var16 = new float[var11];
      ColorSpace var17 = var7.getColorSpace();

      for(int var18 = 0; var18 < var11; ++var18) {
         var40[var18] = var17.getMinValue(var18);
         var16[var18] = var14 / (var17.getMaxValue(var18) - var40[var18]);
      }

      var17 = var8.getColorSpace();
      float[] var41 = new float[var12];
      float[] var19 = new float[var12];

      for(int var20 = 0; var20 < var12; ++var20) {
         var41[var20] = var17.getMinValue(var20);
         var19[var20] = (var17.getMaxValue(var20) - var41[var20]) / var14;
      }

      boolean var42 = var8.hasAlpha();
      boolean var21 = var7.hasAlpha() && var42;
      float[] var22;
      if (var42) {
         var22 = new float[var12 + 1];
      } else {
         var22 = new float[var12];
      }

      Object var25;
      float[] var26;
      float[] var27;
      int var28;
      pelArrayInfo var29;
      int var30;
      int var31;
      int var32;
      if (var13 == 8) {
         byte[] var23 = new byte[var9 * var11];
         byte[] var24 = new byte[var9 * var12];
         var27 = null;
         if (var21) {
            var27 = new float[var9];
         }

         var29 = new pelArrayInfo(this, var23, var24);

         try {
            var3 = new CMMImageLayout(var23, var29.nPels, var29.nSrc);
            var4 = new CMMImageLayout(var24, var29.nPels, var29.nDest);
         } catch (CMMImageLayout.ImageLayoutException var39) {
            throw new CMMException("Unable to convert images");
         }

         for(var30 = 0; var30 < var10; ++var30) {
            var25 = null;
            var26 = null;
            var28 = 0;

            for(var31 = 0; var31 < var9; ++var31) {
               var25 = var5.getDataElements(var31, var30, var25);
               var26 = var7.getNormalizedComponents(var25, var26, 0);

               for(var32 = 0; var32 < var11; ++var32) {
                  var23[var28++] = (byte)((int)((var26[var32] - var40[var32]) * var16[var32] + 0.5F));
               }

               if (var21) {
                  var27[var31] = var26[var11];
               }
            }

            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
            }

            var25 = null;
            var28 = 0;

            for(var31 = 0; var31 < var9; ++var31) {
               for(var32 = 0; var32 < var12; ++var32) {
                  var22[var32] = (float)(var24[var28++] & 255) * var19[var32] + var41[var32];
               }

               if (var21) {
                  var22[var12] = var27[var31];
               } else if (var42) {
                  var22[var12] = 1.0F;
               }

               var25 = var8.getDataElements((float[])var22, 0, var25);
               var6.setDataElements(var31, var30, var25);
            }
         }
      } else {
         short[] var43 = new short[var9 * var11];
         short[] var44 = new short[var9 * var12];
         var27 = null;
         if (var21) {
            var27 = new float[var9];
         }

         var29 = new pelArrayInfo(this, var43, var44);

         try {
            var3 = new CMMImageLayout(var43, var29.nPels, var29.nSrc);
            var4 = new CMMImageLayout(var44, var29.nPels, var29.nDest);
         } catch (CMMImageLayout.ImageLayoutException var37) {
            throw new CMMException("Unable to convert images");
         }

         for(var30 = 0; var30 < var10; ++var30) {
            var25 = null;
            var26 = null;
            var28 = 0;

            for(var31 = 0; var31 < var9; ++var31) {
               var25 = var5.getDataElements(var31, var30, var25);
               var26 = var7.getNormalizedComponents(var25, var26, 0);

               for(var32 = 0; var32 < var11; ++var32) {
                  var43[var28++] = (short)((int)((var26[var32] - var40[var32]) * var16[var32] + 0.5F));
               }

               if (var21) {
                  var27[var31] = var26[var11];
               }
            }

            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
            }

            var25 = null;
            var28 = 0;

            for(var31 = 0; var31 < var9; ++var31) {
               for(var32 = 0; var32 < var12; ++var32) {
                  var22[var32] = (float)(var44[var28++] & '\uffff') * var19[var32] + var41[var32];
               }

               if (var21) {
                  var22[var12] = var27[var31];
               } else if (var42) {
                  var22[var12] = 1.0F;
               }

               var25 = var8.getDataElements((float[])var22, 0, var25);
               var6.setDataElements(var31, var30, var25);
            }
         }
      }

   }

   private CMMImageLayout getImageLayout(BufferedImage var1) {
      try {
         ComponentColorModel var2;
         switch(var1.getType()) {
         case 1:
         case 2:
         case 4:
            return new CMMImageLayout(var1);
         case 3:
         case 7:
         case 8:
         case 9:
         default:
            ColorModel var18 = var1.getColorModel();
            SampleModel var3;
            int var5;
            int var6;
            if (var18 instanceof DirectColorModel) {
               var3 = var1.getSampleModel();
               if (!(var3 instanceof SinglePixelPackedSampleModel)) {
                  return null;
               } else if (var18.getTransferType() != 3) {
                  return null;
               } else if (var18.hasAlpha() && var18.isAlphaPremultiplied()) {
                  return null;
               } else {
                  DirectColorModel var19 = (DirectColorModel)var18;
                  var5 = var19.getRedMask();
                  var6 = var19.getGreenMask();
                  int var7 = var19.getBlueMask();
                  int var8 = var19.getAlphaMask();
                  int var12 = -1;
                  int var11 = -1;
                  int var10 = -1;
                  int var9 = -1;
                  int var13 = 0;
                  byte var14 = 3;
                  if (var8 != 0) {
                     var14 = 4;
                  }

                  int var15 = 0;

                  for(int var16 = -16777216; var15 < 4; var16 >>>= 8) {
                     if (var5 == var16) {
                        var9 = var15;
                        ++var13;
                     } else if (var6 == var16) {
                        var10 = var15;
                        ++var13;
                     } else if (var7 == var16) {
                        var11 = var15;
                        ++var13;
                     } else if (var8 == var16) {
                        var12 = var15;
                        ++var13;
                     }

                     ++var15;
                  }

                  if (var13 != var14) {
                     return null;
                  }

                  return new CMMImageLayout(var1, (SinglePixelPackedSampleModel)var3, var9, var10, var11, var12);
               }
            } else {
               if (var18 instanceof ComponentColorModel) {
                  var3 = var1.getSampleModel();
                  if (!(var3 instanceof ComponentSampleModel)) {
                     return null;
                  }

                  if (var18.hasAlpha() && var18.isAlphaPremultiplied()) {
                     return null;
                  }

                  int var4 = var18.getNumComponents();
                  if (var3.getNumBands() != var4) {
                     return null;
                  }

                  var5 = var18.getTransferType();
                  if (var5 == 0) {
                     for(var6 = 0; var6 < var4; ++var6) {
                        if (var18.getComponentSize(var6) != 8) {
                           return null;
                        }
                     }
                  } else {
                     if (var5 != 1) {
                        return null;
                     }

                     for(var6 = 0; var6 < var4; ++var6) {
                        if (var18.getComponentSize(var6) != 16) {
                           return null;
                        }
                     }
                  }

                  ComponentColorModel var20 = (ComponentColorModel)var18;
                  if (var20.getClass() != ComponentColorModel.class && !this.checkMinMaxScaling(var20)) {
                     return null;
                  }

                  return new CMMImageLayout(var1, (ComponentSampleModel)var3);
               }

               return null;
            }
         case 5:
         case 6:
            var2 = (ComponentColorModel)var1.getColorModel();
            if (var2.getClass() != ComponentColorModel.class && !this.checkMinMaxScaling(var2)) {
               return null;
            }

            return new CMMImageLayout(var1);
         case 10:
            var2 = (ComponentColorModel)var1.getColorModel();
            if (var2.getComponentSize(0) != 8) {
               return null;
            } else {
               if (var2.getClass() != ComponentColorModel.class && !this.checkMinMaxScaling(var2)) {
                  return null;
               }

               return new CMMImageLayout(var1);
            }
         case 11:
            var2 = (ComponentColorModel)var1.getColorModel();
            if (var2.getComponentSize(0) != 16) {
               return null;
            } else {
               return var2.getClass() != ComponentColorModel.class && !this.checkMinMaxScaling(var2) ? null : new CMMImageLayout(var1);
            }
         }
      } catch (CMMImageLayout.ImageLayoutException var17) {
         throw new CMMException("Unable to convert image");
      }
   }

   private boolean checkMinMaxScaling(ComponentColorModel var1) {
      int var5 = var1.getNumComponents();
      int var6 = var1.getNumColorComponents();
      int[] var7 = var1.getComponentSize();
      boolean var8 = var1.hasAlpha();
      float[] var2;
      float[] var3;
      float var4;
      int var10;
      switch(var1.getTransferType()) {
      case 0:
         byte[] var14 = new byte[var5];

         for(var10 = 0; var10 < var6; ++var10) {
            var14[var10] = 0;
         }

         if (var8) {
            var14[var6] = (byte)((1 << var7[var6]) - 1);
         }

         var2 = var1.getNormalizedComponents(var14, (float[])null, 0);

         for(var10 = 0; var10 < var6; ++var10) {
            var14[var10] = (byte)((1 << var7[var10]) - 1);
         }

         var3 = var1.getNormalizedComponents(var14, (float[])null, 0);
         var4 = 256.0F;
         break;
      case 1:
         short[] var9 = new short[var5];

         for(var10 = 0; var10 < var6; ++var10) {
            var9[var10] = 0;
         }

         if (var8) {
            var9[var6] = (short)((byte)((1 << var7[var6]) - 1));
         }

         var2 = var1.getNormalizedComponents(var9, (float[])null, 0);

         for(var10 = 0; var10 < var6; ++var10) {
            var9[var10] = (short)((byte)((1 << var7[var10]) - 1));
         }

         var3 = var1.getNormalizedComponents(var9, (float[])null, 0);
         var4 = 65536.0F;
         break;
      default:
         return false;
      }

      ColorSpace var15 = var1.getColorSpace();

      for(var10 = 0; var10 < var6; ++var10) {
         float var11 = var15.getMinValue(var10);
         float var12 = var15.getMaxValue(var10);
         float var13 = (var12 - var11) / var4;
         var11 -= var2[var10];
         if (var11 < 0.0F) {
            var11 = -var11;
         }

         var12 -= var3[var10];
         if (var12 < 0.0F) {
            var12 = -var12;
         }

         if (var11 > var13 || var12 > var13) {
            return false;
         }
      }

      return true;
   }

   public void colorConvert(Raster var1, WritableRaster var2, float[] var3, float[] var4, float[] var5, float[] var6) {
      SampleModel var9 = var1.getSampleModel();
      SampleModel var10 = var2.getSampleModel();
      int var11 = var1.getTransferType();
      int var12 = var2.getTransferType();
      boolean var13;
      if (var11 != 4 && var11 != 5) {
         var13 = false;
      } else {
         var13 = true;
      }

      boolean var14;
      if (var12 != 4 && var12 != 5) {
         var14 = false;
      } else {
         var14 = true;
      }

      int var15 = var1.getWidth();
      int var16 = var1.getHeight();
      int var17 = var1.getNumBands();
      int var18 = var2.getNumBands();
      float[] var19 = new float[var17];
      float[] var20 = new float[var18];
      float[] var21 = new float[var17];
      float[] var22 = new float[var18];

      int var23;
      for(var23 = 0; var23 < var17; ++var23) {
         if (var13) {
            var19[var23] = 65535.0F / (var4[var23] - var3[var23]);
            var21[var23] = var3[var23];
         } else {
            if (var11 == 2) {
               var19[var23] = 2.0000305F;
            } else {
               var19[var23] = 65535.0F / (float)((1 << var9.getSampleSize(var23)) - 1);
            }

            var21[var23] = 0.0F;
         }
      }

      for(var23 = 0; var23 < var18; ++var23) {
         if (var14) {
            var20[var23] = (var6[var23] - var5[var23]) / 65535.0F;
            var22[var23] = var5[var23];
         } else {
            if (var12 == 2) {
               var20[var23] = 0.49999237F;
            } else {
               var20[var23] = (float)((1 << var10.getSampleSize(var23)) - 1) / 65535.0F;
            }

            var22[var23] = 0.0F;
         }
      }

      var23 = var1.getMinY();
      int var24 = var2.getMinY();
      short[] var28 = new short[var15 * var17];
      short[] var29 = new short[var15 * var18];
      pelArrayInfo var31 = new pelArrayInfo(this, var28, var29);

      CMMImageLayout var7;
      CMMImageLayout var8;
      try {
         var7 = new CMMImageLayout(var28, var31.nPels, var31.nSrc);
         var8 = new CMMImageLayout(var29, var31.nPels, var31.nDest);
      } catch (CMMImageLayout.ImageLayoutException var37) {
         throw new CMMException("Unable to convert rasters");
      }

      for(int var32 = 0; var32 < var16; ++var24) {
         int var25 = var1.getMinX();
         int var30 = 0;

         float var27;
         int var33;
         int var34;
         for(var33 = 0; var33 < var15; ++var25) {
            for(var34 = 0; var34 < var17; ++var34) {
               var27 = var1.getSampleFloat(var25, var23, var34);
               var28[var30++] = (short)((int)((var27 - var21[var34]) * var19[var34] + 0.5F));
            }

            ++var33;
         }

         synchronized(this) {
            CMM.checkStatus(CMM.cmmColorConvert(this.ID, var7, var8));
         }

         int var26 = var2.getMinX();
         var30 = 0;

         for(var33 = 0; var33 < var15; ++var26) {
            for(var34 = 0; var34 < var18; ++var34) {
               var27 = (float)(var29[var30++] & '\uffff') * var20[var34] + var22[var34];
               var2.setSample(var26, var24, var34, var27);
            }

            ++var33;
         }

         ++var32;
         ++var23;
      }

   }

   public void colorConvert(Raster var1, WritableRaster var2) {
      CMMImageLayout var3 = this.getImageLayout(var1);
      CMMImageLayout var4;
      if (var3 != null) {
         var4 = this.getImageLayout((Raster)var2);
         if (var4 != null) {
            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
               return;
            }
         }
      }

      SampleModel var5 = var1.getSampleModel();
      SampleModel var6 = var2.getSampleModel();
      int var7 = var1.getTransferType();
      int var8 = var2.getTransferType();
      int var9 = var1.getWidth();
      int var10 = var1.getHeight();
      int var11 = var1.getNumBands();
      int var12 = var2.getNumBands();
      byte var13 = 8;
      float var14 = 255.0F;

      int var15;
      for(var15 = 0; var15 < var11; ++var15) {
         if (var5.getSampleSize(var15) > 8) {
            var13 = 16;
            var14 = 65535.0F;
         }
      }

      for(var15 = 0; var15 < var12; ++var15) {
         if (var6.getSampleSize(var15) > 8) {
            var13 = 16;
            var14 = 65535.0F;
         }
      }

      float[] var36 = new float[var11];
      float[] var16 = new float[var12];

      int var17;
      for(var17 = 0; var17 < var11; ++var17) {
         if (var7 == 2) {
            var36[var17] = var14 / 32767.0F;
         } else {
            var36[var17] = var14 / (float)((1 << var5.getSampleSize(var17)) - 1);
         }
      }

      for(var17 = 0; var17 < var12; ++var17) {
         if (var8 == 2) {
            var16[var17] = 32767.0F / var14;
         } else {
            var16[var17] = (float)((1 << var6.getSampleSize(var17)) - 1) / var14;
         }
      }

      var17 = var1.getMinY();
      int var18 = var2.getMinY();
      int var19;
      int var20;
      int var21;
      int var24;
      pelArrayInfo var25;
      int var26;
      int var27;
      int var28;
      if (var13 == 8) {
         byte[] var22 = new byte[var9 * var11];
         byte[] var23 = new byte[var9 * var12];
         var25 = new pelArrayInfo(this, var22, var23);

         try {
            var3 = new CMMImageLayout(var22, var25.nPels, var25.nSrc);
            var4 = new CMMImageLayout(var23, var25.nPels, var25.nDest);
         } catch (CMMImageLayout.ImageLayoutException var35) {
            throw new CMMException("Unable to convert rasters");
         }

         for(var26 = 0; var26 < var10; ++var18) {
            var19 = var1.getMinX();
            var24 = 0;

            for(var27 = 0; var27 < var9; ++var19) {
               for(var28 = 0; var28 < var11; ++var28) {
                  var21 = var1.getSample(var19, var17, var28);
                  var22[var24++] = (byte)((int)((float)var21 * var36[var28] + 0.5F));
               }

               ++var27;
            }

            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
            }

            var20 = var2.getMinX();
            var24 = 0;

            for(var27 = 0; var27 < var9; ++var20) {
               for(var28 = 0; var28 < var12; ++var28) {
                  var21 = (int)((float)(var23[var24++] & 255) * var16[var28] + 0.5F);
                  var2.setSample(var20, var18, var28, var21);
               }

               ++var27;
            }

            ++var26;
            ++var17;
         }
      } else {
         short[] var37 = new short[var9 * var11];
         short[] var38 = new short[var9 * var12];
         var25 = new pelArrayInfo(this, var37, var38);

         try {
            var3 = new CMMImageLayout(var37, var25.nPels, var25.nSrc);
            var4 = new CMMImageLayout(var38, var25.nPels, var25.nDest);
         } catch (CMMImageLayout.ImageLayoutException var33) {
            throw new CMMException("Unable to convert rasters");
         }

         for(var26 = 0; var26 < var10; ++var18) {
            var19 = var1.getMinX();
            var24 = 0;

            for(var27 = 0; var27 < var9; ++var19) {
               for(var28 = 0; var28 < var11; ++var28) {
                  var21 = var1.getSample(var19, var17, var28);
                  var37[var24++] = (short)((int)((float)var21 * var36[var28] + 0.5F));
               }

               ++var27;
            }

            synchronized(this) {
               CMM.checkStatus(CMM.cmmColorConvert(this.ID, var3, var4));
            }

            var20 = var2.getMinX();
            var24 = 0;

            for(var27 = 0; var27 < var9; ++var20) {
               for(var28 = 0; var28 < var12; ++var28) {
                  var21 = (int)((float)(var38[var24++] & '\uffff') * var16[var28] + 0.5F);
                  var2.setSample(var20, var18, var28, var21);
               }

               ++var27;
            }

            ++var26;
            ++var17;
         }
      }

   }

   private CMMImageLayout getImageLayout(Raster var1) {
      SampleModel var2 = var1.getSampleModel();
      if (!(var2 instanceof ComponentSampleModel)) {
         return null;
      } else {
         int var3 = var1.getNumBands();
         int var4 = var2.getTransferType();
         int var5;
         if (var4 == 0) {
            for(var5 = 0; var5 < var3; ++var5) {
               if (var2.getSampleSize(var5) != 8) {
                  return null;
               }
            }
         } else {
            if (var4 != 1) {
               return null;
            }

            for(var5 = 0; var5 < var3; ++var5) {
               if (var2.getSampleSize(var5) != 16) {
                  return null;
               }
            }
         }

         try {
            return new CMMImageLayout(var1, (ComponentSampleModel)var2);
         } catch (CMMImageLayout.ImageLayoutException var6) {
            throw new CMMException("Unable to convert raster");
         }
      }
   }

   public short[] colorConvert(short[] var1, short[] var2) {
      pelArrayInfo var3 = new pelArrayInfo(this, var1, var2);
      short[] var6;
      if (var2 != null) {
         var6 = var2;
      } else {
         var6 = new short[var3.destSize];
      }

      CMMImageLayout var4;
      CMMImageLayout var5;
      try {
         var4 = new CMMImageLayout(var1, var3.nPels, var3.nSrc);
         var5 = new CMMImageLayout(var6, var3.nPels, var3.nDest);
      } catch (CMMImageLayout.ImageLayoutException var11) {
         throw new CMMException("Unable to convert data");
      }

      synchronized(this) {
         CMM.checkStatus(CMM.cmmColorConvert(this.ID, var4, var5));
         return var6;
      }
   }

   public byte[] colorConvert(byte[] var1, byte[] var2) {
      pelArrayInfo var3 = new pelArrayInfo(this, var1, var2);
      byte[] var6;
      if (var2 != null) {
         var6 = var2;
      } else {
         var6 = new byte[var3.destSize];
      }

      CMMImageLayout var4;
      CMMImageLayout var5;
      try {
         var4 = new CMMImageLayout(var1, var3.nPels, var3.nSrc);
         var5 = new CMMImageLayout(var6, var3.nPels, var3.nDest);
      } catch (CMMImageLayout.ImageLayoutException var11) {
         throw new CMMException("Unable to convert data");
      }

      synchronized(this) {
         CMM.checkStatus(CMM.cmmColorConvert(this.ID, var4, var5));
         return var6;
      }
   }

   static {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

   }
}
