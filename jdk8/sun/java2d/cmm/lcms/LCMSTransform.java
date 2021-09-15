package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class LCMSTransform implements ColorTransform {
   long ID;
   private int inFormatter = 0;
   private boolean isInIntPacked = false;
   private int outFormatter = 0;
   private boolean isOutIntPacked = false;
   ICC_Profile[] profiles;
   LCMSProfile[] lcmsProfiles;
   int renderType;
   int transformType;
   private int numInComponents = -1;
   private int numOutComponents = -1;
   private Object disposerReferent = new Object();

   public LCMSTransform(ICC_Profile var1, int var2, int var3) {
      this.profiles = new ICC_Profile[1];
      this.profiles[0] = var1;
      this.lcmsProfiles = new LCMSProfile[1];
      this.lcmsProfiles[0] = LCMS.getProfileID(var1);
      this.renderType = var2 == -1 ? 0 : var2;
      this.transformType = var3;
      this.numInComponents = this.profiles[0].getNumComponents();
      this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
   }

   public LCMSTransform(ColorTransform[] var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         var2 += ((LCMSTransform)var1[var3]).profiles.length;
      }

      this.profiles = new ICC_Profile[var2];
      this.lcmsProfiles = new LCMSProfile[var2];
      var3 = 0;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         LCMSTransform var5 = (LCMSTransform)var1[var4];
         System.arraycopy(var5.profiles, 0, this.profiles, var3, var5.profiles.length);
         System.arraycopy(var5.lcmsProfiles, 0, this.lcmsProfiles, var3, var5.lcmsProfiles.length);
         var3 += var5.profiles.length;
      }

      this.renderType = ((LCMSTransform)var1[0]).renderType;
      this.numInComponents = this.profiles[0].getNumComponents();
      this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
   }

   public int getNumInComponents() {
      return this.numInComponents;
   }

   public int getNumOutComponents() {
      return this.numOutComponents;
   }

   private synchronized void doTransform(LCMSImageLayout var1, LCMSImageLayout var2) {
      if (this.ID == 0L || this.inFormatter != var1.pixelType || this.isInIntPacked != var1.isIntPacked || this.outFormatter != var2.pixelType || this.isOutIntPacked != var2.isIntPacked) {
         if (this.ID != 0L) {
            this.disposerReferent = new Object();
         }

         this.inFormatter = var1.pixelType;
         this.isInIntPacked = var1.isIntPacked;
         this.outFormatter = var2.pixelType;
         this.isOutIntPacked = var2.isIntPacked;
         this.ID = LCMS.createTransform(this.lcmsProfiles, this.renderType, this.inFormatter, this.isInIntPacked, this.outFormatter, this.isOutIntPacked, this.disposerReferent);
      }

      LCMS.colorConvert(this, var1, var2);
   }

   public void colorConvert(BufferedImage var1, BufferedImage var2) {
      LCMSImageLayout var3;
      LCMSImageLayout var4;
      try {
         if (!var2.getColorModel().hasAlpha()) {
            var4 = LCMSImageLayout.createImageLayout(var2);
            if (var4 != null) {
               var3 = LCMSImageLayout.createImageLayout(var1);
               if (var3 != null) {
                  this.doTransform(var3, var4);
                  return;
               }
            }
         }
      } catch (LCMSImageLayout.ImageLayoutException var34) {
         throw new CMMException("Unable to convert images");
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

      float[] var35 = new float[var11];
      float[] var16 = new float[var11];
      ColorSpace var17 = var7.getColorSpace();

      for(int var18 = 0; var18 < var11; ++var18) {
         var35[var18] = var17.getMinValue(var18);
         var16[var18] = var14 / (var17.getMaxValue(var18) - var35[var18]);
      }

      var17 = var8.getColorSpace();
      float[] var36 = new float[var12];
      float[] var19 = new float[var12];

      for(int var20 = 0; var20 < var12; ++var20) {
         var36[var20] = var17.getMinValue(var20);
         var19[var20] = (var17.getMaxValue(var20) - var36[var20]) / var14;
      }

      boolean var37 = var8.hasAlpha();
      boolean var21 = var7.hasAlpha() && var37;
      float[] var22;
      if (var37) {
         var22 = new float[var12 + 1];
      } else {
         var22 = new float[var12];
      }

      Object var25;
      float[] var26;
      float[] var27;
      int var28;
      int var29;
      int var30;
      int var31;
      if (var13 == 8) {
         byte[] var23 = new byte[var9 * var11];
         byte[] var24 = new byte[var9 * var12];
         var27 = null;
         if (var21) {
            var27 = new float[var9];
         }

         try {
            var3 = new LCMSImageLayout(var23, var23.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents());
            var4 = new LCMSImageLayout(var24, var24.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents());
         } catch (LCMSImageLayout.ImageLayoutException var33) {
            throw new CMMException("Unable to convert images");
         }

         for(var29 = 0; var29 < var10; ++var29) {
            var25 = null;
            var26 = null;
            var28 = 0;

            for(var30 = 0; var30 < var9; ++var30) {
               var25 = var5.getDataElements(var30, var29, var25);
               var26 = var7.getNormalizedComponents(var25, var26, 0);

               for(var31 = 0; var31 < var11; ++var31) {
                  var23[var28++] = (byte)((int)((var26[var31] - var35[var31]) * var16[var31] + 0.5F));
               }

               if (var21) {
                  var27[var30] = var26[var11];
               }
            }

            this.doTransform(var3, var4);
            var25 = null;
            var28 = 0;

            for(var30 = 0; var30 < var9; ++var30) {
               for(var31 = 0; var31 < var12; ++var31) {
                  var22[var31] = (float)(var24[var28++] & 255) * var19[var31] + var36[var31];
               }

               if (var21) {
                  var22[var12] = var27[var30];
               } else if (var37) {
                  var22[var12] = 1.0F;
               }

               var25 = var8.getDataElements((float[])var22, 0, var25);
               var6.setDataElements(var30, var29, var25);
            }
         }
      } else {
         short[] var38 = new short[var9 * var11];
         short[] var39 = new short[var9 * var12];
         var27 = null;
         if (var21) {
            var27 = new float[var9];
         }

         try {
            var3 = new LCMSImageLayout(var38, var38.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
            var4 = new LCMSImageLayout(var39, var39.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
         } catch (LCMSImageLayout.ImageLayoutException var32) {
            throw new CMMException("Unable to convert images");
         }

         for(var29 = 0; var29 < var10; ++var29) {
            var25 = null;
            var26 = null;
            var28 = 0;

            for(var30 = 0; var30 < var9; ++var30) {
               var25 = var5.getDataElements(var30, var29, var25);
               var26 = var7.getNormalizedComponents(var25, var26, 0);

               for(var31 = 0; var31 < var11; ++var31) {
                  var38[var28++] = (short)((int)((var26[var31] - var35[var31]) * var16[var31] + 0.5F));
               }

               if (var21) {
                  var27[var30] = var26[var11];
               }
            }

            this.doTransform(var3, var4);
            var25 = null;
            var28 = 0;

            for(var30 = 0; var30 < var9; ++var30) {
               for(var31 = 0; var31 < var12; ++var31) {
                  var22[var31] = (float)(var39[var28++] & '\uffff') * var19[var31] + var36[var31];
               }

               if (var21) {
                  var22[var12] = var27[var30];
               } else if (var37) {
                  var22[var12] = 1.0F;
               }

               var25 = var8.getDataElements((float[])var22, 0, var25);
               var6.setDataElements(var30, var29, var25);
            }
         }
      }

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

      LCMSImageLayout var7;
      LCMSImageLayout var8;
      try {
         var7 = new LCMSImageLayout(var28, var28.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
         var8 = new LCMSImageLayout(var29, var29.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
      } catch (LCMSImageLayout.ImageLayoutException var34) {
         throw new CMMException("Unable to convert rasters");
      }

      for(int var31 = 0; var31 < var16; ++var24) {
         int var25 = var1.getMinX();
         int var30 = 0;

         float var27;
         int var32;
         int var33;
         for(var32 = 0; var32 < var15; ++var25) {
            for(var33 = 0; var33 < var17; ++var33) {
               var27 = var1.getSampleFloat(var25, var23, var33);
               var28[var30++] = (short)((int)((var27 - var21[var33]) * var19[var33] + 0.5F));
            }

            ++var32;
         }

         this.doTransform(var7, var8);
         int var26 = var2.getMinX();
         var30 = 0;

         for(var32 = 0; var32 < var15; ++var26) {
            for(var33 = 0; var33 < var18; ++var33) {
               var27 = (float)(var29[var30++] & '\uffff') * var20[var33] + var22[var33];
               var2.setSample(var26, var24, var33, var27);
            }

            ++var32;
         }

         ++var31;
         ++var23;
      }

   }

   public void colorConvert(Raster var1, WritableRaster var2) {
      LCMSImageLayout var4 = LCMSImageLayout.createImageLayout((Raster)var2);
      LCMSImageLayout var3;
      if (var4 != null) {
         var3 = LCMSImageLayout.createImageLayout(var1);
         if (var3 != null) {
            this.doTransform(var3, var4);
            return;
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

      float[] var30 = new float[var11];
      float[] var16 = new float[var12];

      int var17;
      for(var17 = 0; var17 < var11; ++var17) {
         if (var7 == 2) {
            var30[var17] = var14 / 32767.0F;
         } else {
            var30[var17] = var14 / (float)((1 << var5.getSampleSize(var17)) - 1);
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
      int var25;
      int var26;
      int var27;
      if (var13 == 8) {
         byte[] var22 = new byte[var9 * var11];
         byte[] var23 = new byte[var9 * var12];

         try {
            var3 = new LCMSImageLayout(var22, var22.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents());
            var4 = new LCMSImageLayout(var23, var23.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents());
         } catch (LCMSImageLayout.ImageLayoutException var29) {
            throw new CMMException("Unable to convert rasters");
         }

         for(var25 = 0; var25 < var10; ++var18) {
            var19 = var1.getMinX();
            var24 = 0;

            for(var26 = 0; var26 < var9; ++var19) {
               for(var27 = 0; var27 < var11; ++var27) {
                  var21 = var1.getSample(var19, var17, var27);
                  var22[var24++] = (byte)((int)((float)var21 * var30[var27] + 0.5F));
               }

               ++var26;
            }

            this.doTransform(var3, var4);
            var20 = var2.getMinX();
            var24 = 0;

            for(var26 = 0; var26 < var9; ++var20) {
               for(var27 = 0; var27 < var12; ++var27) {
                  var21 = (int)((float)(var23[var24++] & 255) * var16[var27] + 0.5F);
                  var2.setSample(var20, var18, var27, var21);
               }

               ++var26;
            }

            ++var25;
            ++var17;
         }
      } else {
         short[] var31 = new short[var9 * var11];
         short[] var32 = new short[var9 * var12];

         try {
            var3 = new LCMSImageLayout(var31, var31.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
            var4 = new LCMSImageLayout(var32, var32.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
         } catch (LCMSImageLayout.ImageLayoutException var28) {
            throw new CMMException("Unable to convert rasters");
         }

         for(var25 = 0; var25 < var10; ++var18) {
            var19 = var1.getMinX();
            var24 = 0;

            for(var26 = 0; var26 < var9; ++var19) {
               for(var27 = 0; var27 < var11; ++var27) {
                  var21 = var1.getSample(var19, var17, var27);
                  var31[var24++] = (short)((int)((float)var21 * var30[var27] + 0.5F));
               }

               ++var26;
            }

            this.doTransform(var3, var4);
            var20 = var2.getMinX();
            var24 = 0;

            for(var26 = 0; var26 < var9; ++var20) {
               for(var27 = 0; var27 < var12; ++var27) {
                  var21 = (int)((float)(var32[var24++] & '\uffff') * var16[var27] + 0.5F);
                  var2.setSample(var20, var18, var27, var21);
               }

               ++var26;
            }

            ++var25;
            ++var17;
         }
      }

   }

   public short[] colorConvert(short[] var1, short[] var2) {
      if (var2 == null) {
         var2 = new short[var1.length / this.getNumInComponents() * this.getNumOutComponents()];
      }

      try {
         LCMSImageLayout var3 = new LCMSImageLayout(var1, var1.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
         LCMSImageLayout var4 = new LCMSImageLayout(var2, var2.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
         this.doTransform(var3, var4);
         return var2;
      } catch (LCMSImageLayout.ImageLayoutException var5) {
         throw new CMMException("Unable to convert data");
      }
   }

   public byte[] colorConvert(byte[] var1, byte[] var2) {
      if (var2 == null) {
         var2 = new byte[var1.length / this.getNumInComponents() * this.getNumOutComponents()];
      }

      try {
         LCMSImageLayout var3 = new LCMSImageLayout(var1, var1.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents());
         LCMSImageLayout var4 = new LCMSImageLayout(var2, var2.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents());
         this.doTransform(var3, var4);
         return var2;
      } catch (LCMSImageLayout.ImageLayoutException var5) {
         throw new CMMException("Unable to convert data");
      }
   }

   static {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

   }
}
