package java.awt.image;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ColorConvertOp implements BufferedImageOp, RasterOp {
   ICC_Profile[] profileList;
   ColorSpace[] CSList;
   ColorTransform thisTransform;
   ColorTransform thisRasterTransform;
   ICC_Profile thisSrcProfile;
   ICC_Profile thisDestProfile;
   RenderingHints hints;
   boolean gotProfiles;
   float[] srcMinVals;
   float[] srcMaxVals;
   float[] dstMinVals;
   float[] dstMaxVals;

   public ColorConvertOp(RenderingHints var1) {
      this.profileList = new ICC_Profile[0];
      this.hints = var1;
   }

   public ColorConvertOp(ColorSpace var1, RenderingHints var2) {
      if (var1 == null) {
         throw new NullPointerException("ColorSpace cannot be null");
      } else {
         if (var1 instanceof ICC_ColorSpace) {
            this.profileList = new ICC_Profile[1];
            this.profileList[0] = ((ICC_ColorSpace)var1).getProfile();
         } else {
            this.CSList = new ColorSpace[1];
            this.CSList[0] = var1;
         }

         this.hints = var2;
      }
   }

   public ColorConvertOp(ColorSpace var1, ColorSpace var2, RenderingHints var3) {
      if (var1 != null && var2 != null) {
         if (var1 instanceof ICC_ColorSpace && var2 instanceof ICC_ColorSpace) {
            this.profileList = new ICC_Profile[2];
            this.profileList[0] = ((ICC_ColorSpace)var1).getProfile();
            this.profileList[1] = ((ICC_ColorSpace)var2).getProfile();
            this.getMinMaxValsFromColorSpaces(var1, var2);
         } else {
            this.CSList = new ColorSpace[2];
            this.CSList[0] = var1;
            this.CSList[1] = var2;
         }

         this.hints = var3;
      } else {
         throw new NullPointerException("ColorSpaces cannot be null");
      }
   }

   public ColorConvertOp(ICC_Profile[] var1, RenderingHints var2) {
      if (var1 == null) {
         throw new NullPointerException("Profiles cannot be null");
      } else {
         this.gotProfiles = true;
         this.profileList = new ICC_Profile[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            this.profileList[var3] = var1[var3];
         }

         this.hints = var2;
      }
   }

   public final ICC_Profile[] getICC_Profiles() {
      if (!this.gotProfiles) {
         return null;
      } else {
         ICC_Profile[] var1 = new ICC_Profile[this.profileList.length];

         for(int var2 = 0; var2 < this.profileList.length; ++var2) {
            var1[var2] = this.profileList[var2];
         }

         return var1;
      }
   }

   public final BufferedImage filter(BufferedImage var1, BufferedImage var2) {
      BufferedImage var5 = null;
      if (var1.getColorModel() instanceof IndexColorModel) {
         IndexColorModel var6 = (IndexColorModel)var1.getColorModel();
         var1 = var6.convertToIntDiscrete(var1.getRaster(), true);
      }

      ColorSpace var3 = var1.getColorModel().getColorSpace();
      ColorSpace var4;
      if (var2 != null) {
         if (var2.getColorModel() instanceof IndexColorModel) {
            var5 = var2;
            var2 = null;
            var4 = null;
         } else {
            var4 = var2.getColorModel().getColorSpace();
         }
      } else {
         var4 = null;
      }

      if (this.CSList == null && var3 instanceof ICC_ColorSpace && (var2 == null || var4 instanceof ICC_ColorSpace)) {
         var2 = this.ICCBIFilter(var1, var3, var2, var4);
      } else {
         var2 = this.nonICCBIFilter(var1, var3, var2, var4);
      }

      if (var5 != null) {
         Graphics2D var10 = var5.createGraphics();

         try {
            var10.drawImage(var2, 0, 0, (ImageObserver)null);
         } finally {
            var10.dispose();
         }

         return var5;
      } else {
         return var2;
      }
   }

   private final BufferedImage ICCBIFilter(BufferedImage var1, ColorSpace var2, BufferedImage var3, ColorSpace var4) {
      int var5 = this.profileList.length;
      ICC_Profile var6 = null;
      ICC_Profile var7 = null;
      var6 = ((ICC_ColorSpace)var2).getProfile();
      if (var3 == null) {
         if (var5 == 0) {
            throw new IllegalArgumentException("Destination ColorSpace is undefined");
         }

         var7 = this.profileList[var5 - 1];
         var3 = this.createCompatibleDestImage(var1, (ColorModel)null);
      } else {
         if (var1.getHeight() != var3.getHeight() || var1.getWidth() != var3.getWidth()) {
            throw new IllegalArgumentException("Width or height of BufferedImages do not match");
         }

         var7 = ((ICC_ColorSpace)var4).getProfile();
      }

      if (var6 == var7) {
         boolean var8 = true;

         for(int var9 = 0; var9 < var5; ++var9) {
            if (var6 != this.profileList[var9]) {
               var8 = false;
               break;
            }
         }

         if (var8) {
            Graphics2D var13 = var3.createGraphics();

            try {
               var13.drawImage(var1, 0, 0, (ImageObserver)null);
            } finally {
               var13.dispose();
            }

            return var3;
         }
      }

      if (this.thisTransform == null || this.thisSrcProfile != var6 || this.thisDestProfile != var7) {
         this.updateBITransform(var6, var7);
      }

      this.thisTransform.colorConvert(var1, var3);
      return var3;
   }

   private void updateBITransform(ICC_Profile var1, ICC_Profile var2) {
      boolean var10 = false;
      boolean var11 = false;
      int var5 = this.profileList.length;
      int var6 = var5;
      if (var5 == 0 || var1 != this.profileList[0]) {
         var6 = var5 + 1;
         var10 = true;
      }

      if (var5 == 0 || var2 != this.profileList[var5 - 1] || var6 < 2) {
         ++var6;
         var11 = true;
      }

      ICC_Profile[] var3 = new ICC_Profile[var6];
      int var12 = 0;
      if (var10) {
         var3[var12++] = var1;
      }

      int var4;
      for(var4 = 0; var4 < var5; ++var4) {
         var3[var12++] = this.profileList[var4];
      }

      if (var11) {
         var3[var12] = var2;
      }

      ColorTransform[] var9 = new ColorTransform[var6];
      int var8;
      if (var3[0].getProfileClass() == 2) {
         var8 = 1;
      } else {
         var8 = 0;
      }

      byte var7 = 1;
      PCMM var13 = CMSManager.getModule();

      for(var4 = 0; var4 < var6; ++var4) {
         if (var4 == var6 - 1) {
            var7 = 2;
         } else if (var7 == 4 && var3[var4].getProfileClass() == 5) {
            var8 = 0;
            var7 = 1;
         }

         var9[var4] = var13.createTransform(var3[var4], var8, var7);
         var8 = this.getRenderingIntent(var3[var4]);
         var7 = 4;
      }

      this.thisTransform = var13.createTransform(var9);
      this.thisSrcProfile = var1;
      this.thisDestProfile = var2;
   }

   public final WritableRaster filter(Raster var1, WritableRaster var2) {
      if (this.CSList != null) {
         return this.nonICCRasterFilter(var1, var2);
      } else {
         int var3 = this.profileList.length;
         if (var3 < 2) {
            throw new IllegalArgumentException("Source or Destination ColorSpace is undefined");
         } else if (var1.getNumBands() != this.profileList[0].getNumComponents()) {
            throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
         } else {
            if (var2 == null) {
               var2 = this.createCompatibleDestRaster(var1);
            } else {
               if (var1.getHeight() != var2.getHeight() || var1.getWidth() != var2.getWidth()) {
                  throw new IllegalArgumentException("Width or height of Rasters do not match");
               }

               if (var2.getNumBands() != this.profileList[var3 - 1].getNumComponents()) {
                  throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
               }
            }

            int var4;
            if (this.thisRasterTransform == null) {
               ColorTransform[] var7 = new ColorTransform[var3];
               int var6;
               if (this.profileList[0].getProfileClass() == 2) {
                  var6 = 1;
               } else {
                  var6 = 0;
               }

               byte var5 = 1;
               PCMM var8 = CMSManager.getModule();

               for(var4 = 0; var4 < var3; ++var4) {
                  if (var4 == var3 - 1) {
                     var5 = 2;
                  } else if (var5 == 4 && this.profileList[var4].getProfileClass() == 5) {
                     var6 = 0;
                     var5 = 1;
                  }

                  var7[var4] = var8.createTransform(this.profileList[var4], var6, var5);
                  var6 = this.getRenderingIntent(this.profileList[var4]);
                  var5 = 4;
               }

               this.thisRasterTransform = var8.createTransform(var7);
            }

            var4 = var1.getTransferType();
            int var9 = var2.getTransferType();
            if (var4 != 4 && var4 != 5 && var9 != 4 && var9 != 5) {
               this.thisRasterTransform.colorConvert(var1, var2);
            } else {
               if (this.srcMinVals == null) {
                  this.getMinMaxValsFromProfiles(this.profileList[0], this.profileList[var3 - 1]);
               }

               this.thisRasterTransform.colorConvert(var1, var2, this.srcMinVals, this.srcMaxVals, this.dstMinVals, this.dstMaxVals);
            }

            return var2;
         }
      }
   }

   public final Rectangle2D getBounds2D(BufferedImage var1) {
      return this.getBounds2D((Raster)var1.getRaster());
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      return var1.getBounds();
   }

   public BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2) {
      Object var3 = null;
      if (var2 == null) {
         int var4;
         if (this.CSList == null) {
            var4 = this.profileList.length;
            if (var4 == 0) {
               throw new IllegalArgumentException("Destination ColorSpace is undefined");
            }

            ICC_Profile var5 = this.profileList[var4 - 1];
            var3 = new ICC_ColorSpace(var5);
         } else {
            var4 = this.CSList.length;
            var3 = this.CSList[var4 - 1];
         }
      }

      return this.createCompatibleDestImage(var1, var2, (ColorSpace)var3);
   }

   private BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2, ColorSpace var3) {
      int var6;
      if (var2 == null) {
         ColorModel var5 = var1.getColorModel();
         var6 = var3.getNumComponents();
         boolean var7 = var5.hasAlpha();
         if (var7) {
            ++var6;
         }

         int[] var8 = new int[var6];

         for(int var9 = 0; var9 < var6; ++var9) {
            var8[var9] = 8;
         }

         var2 = new ComponentColorModel(var3, var8, var7, var5.isAlphaPremultiplied(), var5.getTransparency(), 0);
      }

      int var10 = var1.getWidth();
      var6 = var1.getHeight();
      BufferedImage var4 = new BufferedImage((ColorModel)var2, ((ColorModel)var2).createCompatibleWritableRaster(var10, var6), ((ColorModel)var2).isAlphaPremultiplied(), (Hashtable)null);
      return var4;
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      int var2;
      if (this.CSList != null) {
         if (this.CSList.length != 2) {
            throw new IllegalArgumentException("Destination ColorSpace is undefined");
         }

         var2 = this.CSList[1].getNumComponents();
      } else {
         int var3 = this.profileList.length;
         if (var3 < 2) {
            throw new IllegalArgumentException("Destination ColorSpace is undefined");
         }

         var2 = this.profileList[var3 - 1].getNumComponents();
      }

      WritableRaster var4 = Raster.createInterleavedRaster(0, var1.getWidth(), var1.getHeight(), var2, new Point(var1.getMinX(), var1.getMinY()));
      return var4;
   }

   public final Point2D getPoint2D(Point2D var1, Point2D var2) {
      if (var2 == null) {
         var2 = new Point2D.Float();
      }

      ((Point2D)var2).setLocation(var1.getX(), var1.getY());
      return (Point2D)var2;
   }

   private int getRenderingIntent(ICC_Profile var1) {
      byte[] var2 = var1.getData(1751474532);
      byte var3 = 64;
      return (var2[var3 + 2] & 255) << 8 | var2[var3 + 3] & 255;
   }

   public final RenderingHints getRenderingHints() {
      return this.hints;
   }

   private final BufferedImage nonICCBIFilter(BufferedImage var1, ColorSpace var2, BufferedImage var3, ColorSpace var4) {
      int var5 = var1.getWidth();
      int var6 = var1.getHeight();
      ICC_ColorSpace var7 = (ICC_ColorSpace)ColorSpace.getInstance(1001);
      if (var3 == null) {
         var3 = this.createCompatibleDestImage(var1, (ColorModel)null);
         var4 = var3.getColorModel().getColorSpace();
      } else if (var6 != var3.getHeight() || var5 != var3.getWidth()) {
         throw new IllegalArgumentException("Width or height of BufferedImages do not match");
      }

      WritableRaster var8 = var1.getRaster();
      WritableRaster var9 = var3.getRaster();
      ColorModel var10 = var1.getColorModel();
      ColorModel var11 = var3.getColorModel();
      int var12 = var10.getNumColorComponents();
      int var13 = var11.getNumColorComponents();
      boolean var14 = var11.hasAlpha();
      boolean var15 = var10.hasAlpha() && var14;
      int var23;
      if (this.CSList == null && this.profileList.length != 0) {
         boolean var39;
         ICC_Profile var40;
         if (!(var2 instanceof ICC_ColorSpace)) {
            var39 = true;
            var40 = var7.getProfile();
         } else {
            var39 = false;
            var40 = ((ICC_ColorSpace)var2).getProfile();
         }

         boolean var41;
         ICC_Profile var42;
         if (!(var4 instanceof ICC_ColorSpace)) {
            var41 = true;
            var42 = var7.getProfile();
         } else {
            var41 = false;
            var42 = ((ICC_ColorSpace)var4).getProfile();
         }

         if (this.thisTransform == null || this.thisSrcProfile != var40 || this.thisDestProfile != var42) {
            this.updateBITransform(var40, var42);
         }

         float var43 = 65535.0F;
         Object var44;
         if (var39) {
            var44 = var7;
            var23 = 3;
         } else {
            var44 = var2;
            var23 = var12;
         }

         float[] var45 = new float[var23];
         float[] var46 = new float[var23];

         int var26;
         for(var26 = 0; var26 < var12; ++var26) {
            var45[var26] = ((ColorSpace)var44).getMinValue(var26);
            var46[var26] = var43 / (((ColorSpace)var44).getMaxValue(var26) - var45[var26]);
         }

         if (var41) {
            var44 = var7;
            var26 = 3;
         } else {
            var44 = var4;
            var26 = var13;
         }

         float[] var27 = new float[var26];
         float[] var28 = new float[var26];

         for(int var29 = 0; var29 < var13; ++var29) {
            var27[var29] = ((ColorSpace)var44).getMinValue(var29);
            var28[var29] = (((ColorSpace)var44).getMaxValue(var29) - var27[var29]) / var43;
         }

         int var30;
         float[] var47;
         if (var14) {
            var30 = var13 + 1 > 3 ? var13 + 1 : 3;
            var47 = new float[var30];
         } else {
            var30 = var13 > 3 ? var13 : 3;
            var47 = new float[var30];
         }

         short[] var48 = new short[var5 * var23];
         short[] var31 = new short[var5 * var26];
         float[] var34 = null;
         if (var15) {
            var34 = new float[var5];
         }

         for(int var36 = 0; var36 < var6; ++var36) {
            Object var32 = null;
            float[] var33 = null;
            int var35 = 0;

            int var37;
            int var38;
            for(var37 = 0; var37 < var5; ++var37) {
               var32 = var8.getDataElements(var37, var36, var32);
               var33 = var10.getNormalizedComponents(var32, var33, 0);
               if (var15) {
                  var34[var37] = var33[var12];
               }

               if (var39) {
                  var33 = var2.toCIEXYZ(var33);
               }

               for(var38 = 0; var38 < var23; ++var38) {
                  var48[var35++] = (short)((int)((var33[var38] - var45[var38]) * var46[var38] + 0.5F));
               }
            }

            this.thisTransform.colorConvert(var48, var31);
            var32 = null;
            var35 = 0;

            for(var37 = 0; var37 < var5; ++var37) {
               for(var38 = 0; var38 < var26; ++var38) {
                  var47[var38] = (float)(var31[var35++] & '\uffff') * var28[var38] + var27[var38];
               }

               if (var41) {
                  var33 = var2.fromCIEXYZ(var47);

                  for(var38 = 0; var38 < var13; ++var38) {
                     var47[var38] = var33[var38];
                  }
               }

               if (var15) {
                  var47[var13] = var34[var37];
               } else if (var14) {
                  var47[var13] = 1.0F;
               }

               var32 = var11.getDataElements((float[])var47, 0, var32);
               var9.setDataElements(var37, var36, var32);
            }
         }
      } else {
         int var17;
         if (this.CSList == null) {
            var17 = 0;
         } else {
            var17 = this.CSList.length;
         }

         float[] var18;
         if (var14) {
            var18 = new float[var13 + 1];
         } else {
            var18 = new float[var13];
         }

         Object var19 = null;
         Object var20 = null;
         float[] var21 = null;

         for(var23 = 0; var23 < var6; ++var23) {
            for(int var24 = 0; var24 < var5; ++var24) {
               var19 = var8.getDataElements(var24, var23, var19);
               var21 = var10.getNormalizedComponents(var19, var21, 0);
               float[] var22 = var2.toCIEXYZ(var21);

               int var25;
               for(var25 = 0; var25 < var17; ++var25) {
                  var22 = this.CSList[var25].fromCIEXYZ(var22);
                  var22 = this.CSList[var25].toCIEXYZ(var22);
               }

               var22 = var4.fromCIEXYZ(var22);

               for(var25 = 0; var25 < var13; ++var25) {
                  var18[var25] = var22[var25];
               }

               if (var15) {
                  var18[var13] = var21[var12];
               } else if (var14) {
                  var18[var13] = 1.0F;
               }

               var20 = var11.getDataElements((float[])var18, 0, var20);
               var9.setDataElements(var24, var23, var20);
            }
         }
      }

      return var3;
   }

   private final WritableRaster nonICCRasterFilter(Raster var1, WritableRaster var2) {
      if (this.CSList.length != 2) {
         throw new IllegalArgumentException("Destination ColorSpace is undefined");
      } else if (var1.getNumBands() != this.CSList[0].getNumComponents()) {
         throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
      } else {
         if (var2 == null) {
            var2 = this.createCompatibleDestRaster(var1);
         } else {
            if (var1.getHeight() != var2.getHeight() || var1.getWidth() != var2.getWidth()) {
               throw new IllegalArgumentException("Width or height of Rasters do not match");
            }

            if (var2.getNumBands() != this.CSList[1].getNumComponents()) {
               throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
            }
         }

         if (this.srcMinVals == null) {
            this.getMinMaxValsFromColorSpaces(this.CSList[0], this.CSList[1]);
         }

         SampleModel var3 = var1.getSampleModel();
         SampleModel var4 = var2.getSampleModel();
         int var7 = var1.getTransferType();
         int var8 = var2.getTransferType();
         boolean var5;
         if (var7 != 4 && var7 != 5) {
            var5 = false;
         } else {
            var5 = true;
         }

         boolean var6;
         if (var8 != 4 && var8 != 5) {
            var6 = false;
         } else {
            var6 = true;
         }

         int var9 = var1.getWidth();
         int var10 = var1.getHeight();
         int var11 = var1.getNumBands();
         int var12 = var2.getNumBands();
         float[] var13 = null;
         float[] var14 = null;
         int var15;
         if (!var5) {
            var13 = new float[var11];

            for(var15 = 0; var15 < var11; ++var15) {
               if (var7 == 2) {
                  var13[var15] = (this.srcMaxVals[var15] - this.srcMinVals[var15]) / 32767.0F;
               } else {
                  var13[var15] = (this.srcMaxVals[var15] - this.srcMinVals[var15]) / (float)((1 << var3.getSampleSize(var15)) - 1);
               }
            }
         }

         if (!var6) {
            var14 = new float[var12];

            for(var15 = 0; var15 < var12; ++var15) {
               if (var8 == 2) {
                  var14[var15] = 32767.0F / (this.dstMaxVals[var15] - this.dstMinVals[var15]);
               } else {
                  var14[var15] = (float)((1 << var4.getSampleSize(var15)) - 1) / (this.dstMaxVals[var15] - this.dstMinVals[var15]);
               }
            }
         }

         var15 = var1.getMinY();
         int var16 = var2.getMinY();
         float[] var20 = new float[var11];
         ColorSpace var22 = this.CSList[0];
         ColorSpace var23 = this.CSList[1];

         for(int var24 = 0; var24 < var10; ++var16) {
            int var17 = var1.getMinX();
            int var18 = var2.getMinX();

            for(int var25 = 0; var25 < var9; ++var18) {
               float var19;
               int var26;
               for(var26 = 0; var26 < var11; ++var26) {
                  var19 = var1.getSampleFloat(var17, var15, var26);
                  if (!var5) {
                     var19 = var19 * var13[var26] + this.srcMinVals[var26];
                  }

                  var20[var26] = var19;
               }

               float[] var21 = var22.toCIEXYZ(var20);
               var21 = var23.fromCIEXYZ(var21);

               for(var26 = 0; var26 < var12; ++var26) {
                  var19 = var21[var26];
                  if (!var6) {
                     var19 = (var19 - this.dstMinVals[var26]) * var14[var26];
                  }

                  var2.setSample(var18, var16, var26, var19);
               }

               ++var25;
               ++var17;
            }

            ++var24;
            ++var15;
         }

         return var2;
      }
   }

   private void getMinMaxValsFromProfiles(ICC_Profile var1, ICC_Profile var2) {
      int var3 = var1.getColorSpaceType();
      int var4 = var1.getNumComponents();
      this.srcMinVals = new float[var4];
      this.srcMaxVals = new float[var4];
      this.setMinMax(var3, var4, this.srcMinVals, this.srcMaxVals);
      var3 = var2.getColorSpaceType();
      var4 = var2.getNumComponents();
      this.dstMinVals = new float[var4];
      this.dstMaxVals = new float[var4];
      this.setMinMax(var3, var4, this.dstMinVals, this.dstMaxVals);
   }

   private void setMinMax(int var1, int var2, float[] var3, float[] var4) {
      if (var1 == 1) {
         var3[0] = 0.0F;
         var4[0] = 100.0F;
         var3[1] = -128.0F;
         var4[1] = 127.0F;
         var3[2] = -128.0F;
         var4[2] = 127.0F;
      } else if (var1 == 0) {
         var3[0] = var3[1] = var3[2] = 0.0F;
         var4[0] = var4[1] = var4[2] = 1.9999695F;
      } else {
         for(int var5 = 0; var5 < var2; ++var5) {
            var3[var5] = 0.0F;
            var4[var5] = 1.0F;
         }
      }

   }

   private void getMinMaxValsFromColorSpaces(ColorSpace var1, ColorSpace var2) {
      int var3 = var1.getNumComponents();
      this.srcMinVals = new float[var3];
      this.srcMaxVals = new float[var3];

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         this.srcMinVals[var4] = var1.getMinValue(var4);
         this.srcMaxVals[var4] = var1.getMaxValue(var4);
      }

      var3 = var2.getNumComponents();
      this.dstMinVals = new float[var3];
      this.dstMaxVals = new float[var3];

      for(var4 = 0; var4 < var3; ++var4) {
         this.dstMinVals[var4] = var2.getMinValue(var4);
         this.dstMaxVals[var4] = var2.getMaxValue(var4);
      }

   }

   static {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

   }
}
