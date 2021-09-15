package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.SunWritableRaster;

abstract class TexturePaintContext implements PaintContext {
   public static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
   public static ColorModel argbmodel = ColorModel.getRGBdefault();
   ColorModel colorModel;
   int bWidth;
   int bHeight;
   int maxWidth;
   WritableRaster outRas;
   double xOrg;
   double yOrg;
   double incXAcross;
   double incYAcross;
   double incXDown;
   double incYDown;
   int colincx;
   int colincy;
   int colincxerr;
   int colincyerr;
   int rowincx;
   int rowincy;
   int rowincxerr;
   int rowincyerr;
   private static WeakReference<Raster> xrgbRasRef;
   private static WeakReference<Raster> argbRasRef;
   private static WeakReference<Raster> byteRasRef;

   public static PaintContext getContext(BufferedImage var0, AffineTransform var1, RenderingHints var2, Rectangle var3) {
      WritableRaster var4 = var0.getRaster();
      ColorModel var5 = var0.getColorModel();
      int var6 = var3.width;
      Object var7 = var2.get(RenderingHints.KEY_INTERPOLATION);
      boolean var8 = var7 == null ? var2.get(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY : var7 != RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (!(var4 instanceof IntegerInterleavedRaster) || var8 && !isFilterableDCM(var5)) {
         if (var4 instanceof ByteInterleavedRaster) {
            ByteInterleavedRaster var10 = (ByteInterleavedRaster)var4;
            if (var10.getNumDataElements() == 1 && var10.getPixelStride() == 1) {
               if (!var8) {
                  return new TexturePaintContext.Byte(var10, var5, var1, var6);
               }

               if (isFilterableICM(var5)) {
                  return new TexturePaintContext.ByteFilter(var10, var5, var1, var6);
               }
            }
         }
      } else {
         IntegerInterleavedRaster var9 = (IntegerInterleavedRaster)var4;
         if (var9.getNumDataElements() == 1 && var9.getPixelStride() == 1) {
            return new TexturePaintContext.Int(var9, var5, var1, var6, var8);
         }
      }

      return new TexturePaintContext.Any(var4, var5, var1, var6, var8);
   }

   public static boolean isFilterableICM(ColorModel var0) {
      if (var0 instanceof IndexColorModel) {
         IndexColorModel var1 = (IndexColorModel)var0;
         if (var1.getMapSize() <= 256) {
            return true;
         }
      }

      return false;
   }

   public static boolean isFilterableDCM(ColorModel var0) {
      if (!(var0 instanceof DirectColorModel)) {
         return false;
      } else {
         DirectColorModel var1 = (DirectColorModel)var0;
         return isMaskOK(var1.getAlphaMask(), true) && isMaskOK(var1.getRedMask(), false) && isMaskOK(var1.getGreenMask(), false) && isMaskOK(var1.getBlueMask(), false);
      }
   }

   public static boolean isMaskOK(int var0, boolean var1) {
      if (var1 && var0 == 0) {
         return true;
      } else {
         return var0 == 255 || var0 == 65280 || var0 == 16711680 || var0 == -16777216;
      }
   }

   public static ColorModel getInternedColorModel(ColorModel var0) {
      if (xrgbmodel != var0 && !xrgbmodel.equals(var0)) {
         return argbmodel != var0 && !argbmodel.equals(var0) ? var0 : argbmodel;
      } else {
         return xrgbmodel;
      }
   }

   TexturePaintContext(ColorModel var1, AffineTransform var2, int var3, int var4, int var5) {
      this.colorModel = getInternedColorModel(var1);
      this.bWidth = var3;
      this.bHeight = var4;
      this.maxWidth = var5;

      try {
         var2 = var2.createInverse();
      } catch (NoninvertibleTransformException var7) {
         var2.setToScale(0.0D, 0.0D);
      }

      this.incXAcross = mod(var2.getScaleX(), (double)var3);
      this.incYAcross = mod(var2.getShearY(), (double)var4);
      this.incXDown = mod(var2.getShearX(), (double)var3);
      this.incYDown = mod(var2.getScaleY(), (double)var4);
      this.xOrg = var2.getTranslateX();
      this.yOrg = var2.getTranslateY();
      this.colincx = (int)this.incXAcross;
      this.colincy = (int)this.incYAcross;
      this.colincxerr = fractAsInt(this.incXAcross);
      this.colincyerr = fractAsInt(this.incYAcross);
      this.rowincx = (int)this.incXDown;
      this.rowincy = (int)this.incYDown;
      this.rowincxerr = fractAsInt(this.incXDown);
      this.rowincyerr = fractAsInt(this.incYDown);
   }

   static int fractAsInt(double var0) {
      return (int)(var0 % 1.0D * 2.147483647E9D);
   }

   static double mod(double var0, double var2) {
      var0 %= var2;
      if (var0 < 0.0D) {
         var0 += var2;
         if (var0 >= var2) {
            var0 = 0.0D;
         }
      }

      return var0;
   }

   public void dispose() {
      dropRaster(this.colorModel, this.outRas);
   }

   public ColorModel getColorModel() {
      return this.colorModel;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      if (this.outRas == null || this.outRas.getWidth() < var3 || this.outRas.getHeight() < var4) {
         this.outRas = this.makeRaster(var4 == 1 ? Math.max(var3, this.maxWidth) : var3, var4);
      }

      double var5 = mod(this.xOrg + (double)var1 * this.incXAcross + (double)var2 * this.incXDown, (double)this.bWidth);
      double var7 = mod(this.yOrg + (double)var1 * this.incYAcross + (double)var2 * this.incYDown, (double)this.bHeight);
      this.setRaster((int)var5, (int)var7, fractAsInt(var5), fractAsInt(var7), var3, var4, this.bWidth, this.bHeight, this.colincx, this.colincxerr, this.colincy, this.colincyerr, this.rowincx, this.rowincxerr, this.rowincy, this.rowincyerr);
      SunWritableRaster.markDirty(this.outRas);
      return this.outRas;
   }

   static synchronized WritableRaster makeRaster(ColorModel var0, Raster var1, int var2, int var3) {
      WritableRaster var4;
      if (xrgbmodel == var0) {
         if (xrgbRasRef != null) {
            var4 = (WritableRaster)xrgbRasRef.get();
            if (var4 != null && var4.getWidth() >= var2 && var4.getHeight() >= var3) {
               xrgbRasRef = null;
               return var4;
            }
         }

         if (var2 <= 32 && var3 <= 32) {
            var3 = 32;
            var2 = 32;
         }
      } else if (argbmodel == var0) {
         if (argbRasRef != null) {
            var4 = (WritableRaster)argbRasRef.get();
            if (var4 != null && var4.getWidth() >= var2 && var4.getHeight() >= var3) {
               argbRasRef = null;
               return var4;
            }
         }

         if (var2 <= 32 && var3 <= 32) {
            var3 = 32;
            var2 = 32;
         }
      }

      return var1 != null ? var1.createCompatibleWritableRaster(var2, var3) : var0.createCompatibleWritableRaster(var2, var3);
   }

   static synchronized void dropRaster(ColorModel var0, Raster var1) {
      if (var1 != null) {
         if (xrgbmodel == var0) {
            xrgbRasRef = new WeakReference(var1);
         } else if (argbmodel == var0) {
            argbRasRef = new WeakReference(var1);
         }

      }
   }

   static synchronized WritableRaster makeByteRaster(Raster var0, int var1, int var2) {
      if (byteRasRef != null) {
         WritableRaster var3 = (WritableRaster)byteRasRef.get();
         if (var3 != null && var3.getWidth() >= var1 && var3.getHeight() >= var2) {
            byteRasRef = null;
            return var3;
         }
      }

      if (var1 <= 32 && var2 <= 32) {
         var2 = 32;
         var1 = 32;
      }

      return var0.createCompatibleWritableRaster(var1, var2);
   }

   static synchronized void dropByteRaster(Raster var0) {
      if (var0 != null) {
         byteRasRef = new WeakReference(var0);
      }
   }

   public abstract WritableRaster makeRaster(int var1, int var2);

   public abstract void setRaster(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16);

   public static int blend(int[] var0, int var1, int var2) {
      var1 >>>= 19;
      var2 >>>= 19;
      int var6 = 0;
      int var5 = 0;
      int var4 = 0;
      int var3 = 0;

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = var0[var7];
         var1 = 4096 - var1;
         if ((var7 & 1) == 0) {
            var2 = 4096 - var2;
         }

         int var9 = var1 * var2;
         if (var9 != 0) {
            var3 += (var8 >>> 24) * var9;
            var4 += (var8 >>> 16 & 255) * var9;
            var5 += (var8 >>> 8 & 255) * var9;
            var6 += (var8 & 255) * var9;
         }
      }

      return var3 + 8388608 >>> 24 << 24 | var4 + 8388608 >>> 24 << 16 | var5 + 8388608 >>> 24 << 8 | var6 + 8388608 >>> 24;
   }

   static class Any extends TexturePaintContext {
      WritableRaster srcRas;
      boolean filter;

      public Any(WritableRaster var1, ColorModel var2, AffineTransform var3, int var4, boolean var5) {
         super(var2, var3, var1.getWidth(), var1.getHeight(), var4);
         this.srcRas = var1;
         this.filter = var5;
      }

      public WritableRaster makeRaster(int var1, int var2) {
         return makeRaster(this.colorModel, this.srcRas, var1, var2);
      }

      public void setRaster(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16) {
         Object var17 = null;
         int var18 = var1;
         int var19 = var2;
         int var20 = var3;
         int var21 = var4;
         WritableRaster var22 = this.srcRas;
         WritableRaster var23 = this.outRas;
         int[] var24 = this.filter ? new int[4] : null;

         for(int var25 = 0; var25 < var6; ++var25) {
            var1 = var18;
            var2 = var19;
            var3 = var20;
            var4 = var21;

            for(int var26 = 0; var26 < var5; ++var26) {
               var17 = var22.getDataElements(var1, var2, var17);
               if (this.filter) {
                  int var27;
                  if ((var27 = var1 + 1) >= var7) {
                     var27 = 0;
                  }

                  int var28;
                  if ((var28 = var2 + 1) >= var8) {
                     var28 = 0;
                  }

                  var24[0] = this.colorModel.getRGB(var17);
                  var17 = var22.getDataElements(var27, var2, var17);
                  var24[1] = this.colorModel.getRGB(var17);
                  var17 = var22.getDataElements(var1, var28, var17);
                  var24[2] = this.colorModel.getRGB(var17);
                  var17 = var22.getDataElements(var27, var28, var17);
                  var24[3] = this.colorModel.getRGB(var17);
                  int var29 = TexturePaintContext.blend(var24, var3, var4);
                  var17 = this.colorModel.getDataElements(var29, var17);
               }

               var23.setDataElements(var26, var25, var17);
               if ((var3 += var10) < 0) {
                  var3 &= Integer.MAX_VALUE;
                  ++var1;
               }

               if ((var1 += var9) >= var7) {
                  var1 -= var7;
               }

               if ((var4 += var12) < 0) {
                  var4 &= Integer.MAX_VALUE;
                  ++var2;
               }

               if ((var2 += var11) >= var8) {
                  var2 -= var8;
               }
            }

            if ((var20 += var14) < 0) {
               var20 &= Integer.MAX_VALUE;
               ++var18;
            }

            if ((var18 += var13) >= var7) {
               var18 -= var7;
            }

            if ((var21 += var16) < 0) {
               var21 &= Integer.MAX_VALUE;
               ++var19;
            }

            if ((var19 += var15) >= var8) {
               var19 -= var8;
            }
         }

      }
   }

   static class ByteFilter extends TexturePaintContext {
      ByteInterleavedRaster srcRas;
      int[] inPalette = new int[256];
      byte[] inData;
      int inOff;
      int inSpan;
      int[] outData;
      int outOff;
      int outSpan;

      public ByteFilter(ByteInterleavedRaster var1, ColorModel var2, AffineTransform var3, int var4) {
         super(var2.getTransparency() == 1 ? xrgbmodel : argbmodel, var3, var1.getWidth(), var1.getHeight(), var4);
         ((IndexColorModel)var2).getRGBs(this.inPalette);
         this.srcRas = var1;
         this.inData = var1.getDataStorage();
         this.inSpan = var1.getScanlineStride();
         this.inOff = var1.getDataOffset(0);
      }

      public WritableRaster makeRaster(int var1, int var2) {
         WritableRaster var3 = makeRaster(this.colorModel, (Raster)null, var1, var2);
         IntegerInterleavedRaster var4 = (IntegerInterleavedRaster)var3;
         this.outData = var4.getDataStorage();
         this.outSpan = var4.getScanlineStride();
         this.outOff = var4.getDataOffset(0);
         return var3;
      }

      public void setRaster(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16) {
         byte[] var17 = this.inData;
         int[] var18 = this.outData;
         int var19 = this.outOff;
         int var20 = this.inSpan;
         int var21 = this.inOff;
         int var22 = this.outSpan;
         int var23 = var1;
         int var24 = var2;
         int var25 = var3;
         int var26 = var4;
         int[] var27 = new int[4];

         for(int var28 = 0; var28 < var6; ++var28) {
            var1 = var23;
            var2 = var24;
            var3 = var25;
            var4 = var26;

            for(int var29 = 0; var29 < var5; ++var29) {
               int var30;
               if ((var30 = var1 + 1) >= var7) {
                  var30 = 0;
               }

               int var31;
               if ((var31 = var2 + 1) >= var8) {
                  var31 = 0;
               }

               var27[0] = this.inPalette[255 & var17[var21 + var1 + var20 * var2]];
               var27[1] = this.inPalette[255 & var17[var21 + var30 + var20 * var2]];
               var27[2] = this.inPalette[255 & var17[var21 + var1 + var20 * var31]];
               var27[3] = this.inPalette[255 & var17[var21 + var30 + var20 * var31]];
               var18[var19 + var29] = TexturePaintContext.blend(var27, var3, var4);
               if ((var3 += var10) < 0) {
                  var3 &= Integer.MAX_VALUE;
                  ++var1;
               }

               if ((var1 += var9) >= var7) {
                  var1 -= var7;
               }

               if ((var4 += var12) < 0) {
                  var4 &= Integer.MAX_VALUE;
                  ++var2;
               }

               if ((var2 += var11) >= var8) {
                  var2 -= var8;
               }
            }

            if ((var25 += var14) < 0) {
               var25 &= Integer.MAX_VALUE;
               ++var23;
            }

            if ((var23 += var13) >= var7) {
               var23 -= var7;
            }

            if ((var26 += var16) < 0) {
               var26 &= Integer.MAX_VALUE;
               ++var24;
            }

            if ((var24 += var15) >= var8) {
               var24 -= var8;
            }

            var19 += var22;
         }

      }
   }

   static class Byte extends TexturePaintContext {
      ByteInterleavedRaster srcRas;
      byte[] inData;
      int inOff;
      int inSpan;
      byte[] outData;
      int outOff;
      int outSpan;

      public Byte(ByteInterleavedRaster var1, ColorModel var2, AffineTransform var3, int var4) {
         super(var2, var3, var1.getWidth(), var1.getHeight(), var4);
         this.srcRas = var1;
         this.inData = var1.getDataStorage();
         this.inSpan = var1.getScanlineStride();
         this.inOff = var1.getDataOffset(0);
      }

      public WritableRaster makeRaster(int var1, int var2) {
         WritableRaster var3 = makeByteRaster(this.srcRas, var1, var2);
         ByteInterleavedRaster var4 = (ByteInterleavedRaster)var3;
         this.outData = var4.getDataStorage();
         this.outSpan = var4.getScanlineStride();
         this.outOff = var4.getDataOffset(0);
         return var3;
      }

      public void dispose() {
         dropByteRaster(this.outRas);
      }

      public void setRaster(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16) {
         byte[] var17 = this.inData;
         byte[] var18 = this.outData;
         int var19 = this.outOff;
         int var20 = this.inSpan;
         int var21 = this.inOff;
         int var22 = this.outSpan;
         boolean var23 = var9 == 1 && var10 == 0 && var11 == 0 && var12 == 0;
         int var24 = var1;
         int var25 = var2;
         int var26 = var3;
         int var27 = var4;
         if (var23) {
            var22 -= var5;
         }

         for(int var28 = 0; var28 < var6; ++var28) {
            int var29;
            if (var23) {
               var29 = var21 + var25 * var20 + var7;
               var1 = var7 - var24;
               var19 += var5;
               int var30;
               if (var7 >= 32) {
                  var30 = var5;

                  while(var30 > 0) {
                     int var31 = var30 < var1 ? var30 : var1;
                     System.arraycopy(var17, var29 - var1, var18, var19 - var30, var31);
                     var30 -= var31;
                     if ((var1 -= var31) == 0) {
                        var1 = var7;
                     }
                  }
               } else {
                  for(var30 = var5; var30 > 0; --var30) {
                     var18[var19 - var30] = var17[var29 - var1];
                     --var1;
                     if (var1 == 0) {
                        var1 = var7;
                     }
                  }
               }
            } else {
               var1 = var24;
               var2 = var25;
               var3 = var26;
               var4 = var27;

               for(var29 = 0; var29 < var5; ++var29) {
                  var18[var19 + var29] = var17[var21 + var2 * var20 + var1];
                  if ((var3 += var10) < 0) {
                     var3 &= Integer.MAX_VALUE;
                     ++var1;
                  }

                  if ((var1 += var9) >= var7) {
                     var1 -= var7;
                  }

                  if ((var4 += var12) < 0) {
                     var4 &= Integer.MAX_VALUE;
                     ++var2;
                  }

                  if ((var2 += var11) >= var8) {
                     var2 -= var8;
                  }
               }
            }

            if ((var26 += var14) < 0) {
               var26 &= Integer.MAX_VALUE;
               ++var24;
            }

            if ((var24 += var13) >= var7) {
               var24 -= var7;
            }

            if ((var27 += var16) < 0) {
               var27 &= Integer.MAX_VALUE;
               ++var25;
            }

            if ((var25 += var15) >= var8) {
               var25 -= var8;
            }

            var19 += var22;
         }

      }
   }

   static class Int extends TexturePaintContext {
      IntegerInterleavedRaster srcRas;
      int[] inData;
      int inOff;
      int inSpan;
      int[] outData;
      int outOff;
      int outSpan;
      boolean filter;

      public Int(IntegerInterleavedRaster var1, ColorModel var2, AffineTransform var3, int var4, boolean var5) {
         super(var2, var3, var1.getWidth(), var1.getHeight(), var4);
         this.srcRas = var1;
         this.inData = var1.getDataStorage();
         this.inSpan = var1.getScanlineStride();
         this.inOff = var1.getDataOffset(0);
         this.filter = var5;
      }

      public WritableRaster makeRaster(int var1, int var2) {
         WritableRaster var3 = makeRaster(this.colorModel, this.srcRas, var1, var2);
         IntegerInterleavedRaster var4 = (IntegerInterleavedRaster)var3;
         this.outData = var4.getDataStorage();
         this.outSpan = var4.getScanlineStride();
         this.outOff = var4.getDataOffset(0);
         return var3;
      }

      public void setRaster(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16) {
         int[] var17 = this.inData;
         int[] var18 = this.outData;
         int var19 = this.outOff;
         int var20 = this.inSpan;
         int var21 = this.inOff;
         int var22 = this.outSpan;
         boolean var23 = this.filter;
         boolean var24 = var9 == 1 && var10 == 0 && var11 == 0 && var12 == 0 && !var23;
         int var25 = var1;
         int var26 = var2;
         int var27 = var3;
         int var28 = var4;
         if (var24) {
            var22 -= var5;
         }

         int[] var29 = var23 ? new int[4] : null;

         for(int var30 = 0; var30 < var6; ++var30) {
            int var31;
            int var32;
            int var33;
            if (var24) {
               var31 = var21 + var26 * var20 + var7;
               var1 = var7 - var25;
               var19 += var5;
               if (var7 >= 32) {
                  var32 = var5;

                  while(var32 > 0) {
                     var33 = var32 < var1 ? var32 : var1;
                     System.arraycopy(var17, var31 - var1, var18, var19 - var32, var33);
                     var32 -= var33;
                     if ((var1 -= var33) == 0) {
                        var1 = var7;
                     }
                  }
               } else {
                  for(var32 = var5; var32 > 0; --var32) {
                     var18[var19 - var32] = var17[var31 - var1];
                     --var1;
                     if (var1 == 0) {
                        var1 = var7;
                     }
                  }
               }
            } else {
               var1 = var25;
               var2 = var26;
               var3 = var27;
               var4 = var28;

               for(var31 = 0; var31 < var5; ++var31) {
                  if (var23) {
                     if ((var32 = var1 + 1) >= var7) {
                        var32 = 0;
                     }

                     if ((var33 = var2 + 1) >= var8) {
                        var33 = 0;
                     }

                     var29[0] = var17[var21 + var2 * var20 + var1];
                     var29[1] = var17[var21 + var2 * var20 + var32];
                     var29[2] = var17[var21 + var33 * var20 + var1];
                     var29[3] = var17[var21 + var33 * var20 + var32];
                     var18[var19 + var31] = TexturePaintContext.blend(var29, var3, var4);
                  } else {
                     var18[var19 + var31] = var17[var21 + var2 * var20 + var1];
                  }

                  if ((var3 += var10) < 0) {
                     var3 &= Integer.MAX_VALUE;
                     ++var1;
                  }

                  if ((var1 += var9) >= var7) {
                     var1 -= var7;
                  }

                  if ((var4 += var12) < 0) {
                     var4 &= Integer.MAX_VALUE;
                     ++var2;
                  }

                  if ((var2 += var11) >= var8) {
                     var2 -= var8;
                  }
               }
            }

            if ((var27 += var14) < 0) {
               var27 &= Integer.MAX_VALUE;
               ++var25;
            }

            if ((var25 += var13) >= var7) {
               var25 -= var7;
            }

            if ((var28 += var16) < 0) {
               var28 &= Integer.MAX_VALUE;
               ++var26;
            }

            if ((var26 += var15) >= var8) {
               var26 -= var8;
            }

            var19 += var22;
         }

      }
   }
}
