package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public abstract class ColorModel implements Transparency {
   private long pData;
   protected int pixel_bits;
   int[] nBits;
   int transparency = 3;
   boolean supportsAlpha = true;
   boolean isAlphaPremultiplied = false;
   int numComponents = -1;
   int numColorComponents = -1;
   ColorSpace colorSpace = ColorSpace.getInstance(1000);
   int colorSpaceType = 5;
   int maxBits;
   boolean is_sRGB = true;
   protected int transferType;
   private static boolean loaded = false;
   private static ColorModel RGBdefault;
   static byte[] l8Tos8;
   static byte[] s8Tol8;
   static byte[] l16Tos8;
   static short[] s8Tol16;
   static Map<ICC_ColorSpace, byte[]> g8Tos8Map;
   static Map<ICC_ColorSpace, byte[]> lg16Toog8Map;
   static Map<ICC_ColorSpace, byte[]> g16Tos8Map;
   static Map<ICC_ColorSpace, short[]> lg16Toog16Map;

   static void loadLibraries() {
      if (!loaded) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.loadLibrary("awt");
               return null;
            }
         });
         loaded = true;
      }

   }

   private static native void initIDs();

   public static ColorModel getRGBdefault() {
      if (RGBdefault == null) {
         RGBdefault = new DirectColorModel(32, 16711680, 65280, 255, -16777216);
      }

      return RGBdefault;
   }

   public ColorModel(int var1) {
      this.pixel_bits = var1;
      if (var1 < 1) {
         throw new IllegalArgumentException("Number of bits must be > 0");
      } else {
         this.numComponents = 4;
         this.numColorComponents = 3;
         this.maxBits = var1;
         this.transferType = getDefaultTransferType(var1);
      }
   }

   protected ColorModel(int var1, int[] var2, ColorSpace var3, boolean var4, boolean var5, int var6, int var7) {
      this.colorSpace = var3;
      this.colorSpaceType = var3.getType();
      this.numColorComponents = var3.getNumComponents();
      this.numComponents = this.numColorComponents + (var4 ? 1 : 0);
      this.supportsAlpha = var4;
      if (var2.length < this.numComponents) {
         throw new IllegalArgumentException("Number of color/alpha components should be " + this.numComponents + " but length of bits array is " + var2.length);
      } else if (var6 >= 1 && var6 <= 3) {
         if (!this.supportsAlpha) {
            this.isAlphaPremultiplied = false;
            this.transparency = 1;
         } else {
            this.isAlphaPremultiplied = var5;
            this.transparency = var6;
         }

         this.nBits = (int[])var2.clone();
         this.pixel_bits = var1;
         if (var1 <= 0) {
            throw new IllegalArgumentException("Number of pixel bits must be > 0");
         } else {
            this.maxBits = 0;

            for(int var8 = 0; var8 < var2.length; ++var8) {
               if (var2[var8] < 0) {
                  throw new IllegalArgumentException("Number of bits must be >= 0");
               }

               if (this.maxBits < var2[var8]) {
                  this.maxBits = var2[var8];
               }
            }

            if (this.maxBits == 0) {
               throw new IllegalArgumentException("There must be at least one component with > 0 pixel bits.");
            } else {
               if (var3 != ColorSpace.getInstance(1000)) {
                  this.is_sRGB = false;
               }

               this.transferType = var7;
            }
         }
      } else {
         throw new IllegalArgumentException("Unknown transparency: " + var6);
      }
   }

   public final boolean hasAlpha() {
      return this.supportsAlpha;
   }

   public final boolean isAlphaPremultiplied() {
      return this.isAlphaPremultiplied;
   }

   public final int getTransferType() {
      return this.transferType;
   }

   public int getPixelSize() {
      return this.pixel_bits;
   }

   public int getComponentSize(int var1) {
      if (this.nBits == null) {
         throw new NullPointerException("Number of bits array is null.");
      } else {
         return this.nBits[var1];
      }
   }

   public int[] getComponentSize() {
      return this.nBits != null ? (int[])this.nBits.clone() : null;
   }

   public int getTransparency() {
      return this.transparency;
   }

   public int getNumComponents() {
      return this.numComponents;
   }

   public int getNumColorComponents() {
      return this.numColorComponents;
   }

   public abstract int getRed(int var1);

   public abstract int getGreen(int var1);

   public abstract int getBlue(int var1);

   public abstract int getAlpha(int var1);

   public int getRGB(int var1) {
      return this.getAlpha(var1) << 24 | this.getRed(var1) << 16 | this.getGreen(var1) << 8 | this.getBlue(var1) << 0;
   }

   public int getRed(Object var1) {
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      switch(this.transferType) {
      case 0:
         byte[] var4 = (byte[])((byte[])var1);
         var7 = var4[0] & 255;
         var8 = var4.length;
         break;
      case 1:
         short[] var5 = (short[])((short[])var1);
         var7 = var5[0] & '\uffff';
         var8 = var5.length;
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var6 = (int[])((int[])var1);
         var7 = var6[0];
         var8 = var6.length;
      }

      if (var8 == 1) {
         return this.getRed(var7);
      } else {
         throw new UnsupportedOperationException("This method is not supported by this color model");
      }
   }

   public int getGreen(Object var1) {
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      switch(this.transferType) {
      case 0:
         byte[] var4 = (byte[])((byte[])var1);
         var7 = var4[0] & 255;
         var8 = var4.length;
         break;
      case 1:
         short[] var5 = (short[])((short[])var1);
         var7 = var5[0] & '\uffff';
         var8 = var5.length;
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var6 = (int[])((int[])var1);
         var7 = var6[0];
         var8 = var6.length;
      }

      if (var8 == 1) {
         return this.getGreen(var7);
      } else {
         throw new UnsupportedOperationException("This method is not supported by this color model");
      }
   }

   public int getBlue(Object var1) {
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      switch(this.transferType) {
      case 0:
         byte[] var4 = (byte[])((byte[])var1);
         var7 = var4[0] & 255;
         var8 = var4.length;
         break;
      case 1:
         short[] var5 = (short[])((short[])var1);
         var7 = var5[0] & '\uffff';
         var8 = var5.length;
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var6 = (int[])((int[])var1);
         var7 = var6[0];
         var8 = var6.length;
      }

      if (var8 == 1) {
         return this.getBlue(var7);
      } else {
         throw new UnsupportedOperationException("This method is not supported by this color model");
      }
   }

   public int getAlpha(Object var1) {
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      switch(this.transferType) {
      case 0:
         byte[] var4 = (byte[])((byte[])var1);
         var7 = var4[0] & 255;
         var8 = var4.length;
         break;
      case 1:
         short[] var5 = (short[])((short[])var1);
         var7 = var5[0] & '\uffff';
         var8 = var5.length;
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var6 = (int[])((int[])var1);
         var7 = var6[0];
         var8 = var6.length;
      }

      if (var8 == 1) {
         return this.getAlpha(var7);
      } else {
         throw new UnsupportedOperationException("This method is not supported by this color model");
      }
   }

   public int getRGB(Object var1) {
      return this.getAlpha(var1) << 24 | this.getRed(var1) << 16 | this.getGreen(var1) << 8 | this.getBlue(var1) << 0;
   }

   public Object getDataElements(int var1, Object var2) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
   }

   public int[] getComponents(int var1, int[] var2, int var3) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
   }

   public int[] getComponents(Object var1, int[] var2, int var3) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
   }

   public int[] getUnnormalizedComponents(float[] var1, int var2, int[] var3, int var4) {
      if (this.colorSpace == null) {
         throw new UnsupportedOperationException("This method is not supported by this color model.");
      } else if (this.nBits == null) {
         throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
      } else if (var1.length - var2 < this.numComponents) {
         throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents);
      } else {
         if (var3 == null) {
            var3 = new int[var4 + this.numComponents];
         }

         if (this.supportsAlpha && this.isAlphaPremultiplied) {
            float var7 = var1[var2 + this.numColorComponents];

            for(int var6 = 0; var6 < this.numColorComponents; ++var6) {
               var3[var4 + var6] = (int)(var1[var2 + var6] * (float)((1 << this.nBits[var6]) - 1) * var7 + 0.5F);
            }

            var3[var4 + this.numColorComponents] = (int)(var7 * (float)((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
         } else {
            for(int var5 = 0; var5 < this.numComponents; ++var5) {
               var3[var4 + var5] = (int)(var1[var2 + var5] * (float)((1 << this.nBits[var5]) - 1) + 0.5F);
            }
         }

         return var3;
      }
   }

   public float[] getNormalizedComponents(int[] var1, int var2, float[] var3, int var4) {
      if (this.colorSpace == null) {
         throw new UnsupportedOperationException("This method is not supported by this color model.");
      } else if (this.nBits == null) {
         throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
      } else if (var1.length - var2 < this.numComponents) {
         throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents);
      } else {
         if (var3 == null) {
            var3 = new float[this.numComponents + var4];
         }

         if (this.supportsAlpha && this.isAlphaPremultiplied) {
            float var7 = (float)var1[var2 + this.numColorComponents];
            var7 /= (float)((1 << this.nBits[this.numColorComponents]) - 1);
            int var6;
            if (var7 != 0.0F) {
               for(var6 = 0; var6 < this.numColorComponents; ++var6) {
                  var3[var4 + var6] = (float)var1[var2 + var6] / (var7 * (float)((1 << this.nBits[var6]) - 1));
               }
            } else {
               for(var6 = 0; var6 < this.numColorComponents; ++var6) {
                  var3[var4 + var6] = 0.0F;
               }
            }

            var3[var4 + this.numColorComponents] = var7;
         } else {
            for(int var5 = 0; var5 < this.numComponents; ++var5) {
               var3[var4 + var5] = (float)var1[var2 + var5] / (float)((1 << this.nBits[var5]) - 1);
            }
         }

         return var3;
      }
   }

   public int getDataElement(int[] var1, int var2) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
   }

   public Object getDataElements(int[] var1, int var2, Object var3) {
      throw new UnsupportedOperationException("This method has not been implemented for this color model.");
   }

   public int getDataElement(float[] var1, int var2) {
      int[] var3 = this.getUnnormalizedComponents(var1, var2, (int[])null, 0);
      return this.getDataElement((int[])var3, 0);
   }

   public Object getDataElements(float[] var1, int var2, Object var3) {
      int[] var4 = this.getUnnormalizedComponents(var1, var2, (int[])null, 0);
      return this.getDataElements((int[])var4, 0, var3);
   }

   public float[] getNormalizedComponents(Object var1, float[] var2, int var3) {
      int[] var4 = this.getComponents(var1, (int[])null, 0);
      return this.getNormalizedComponents(var4, 0, var2, var3);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ColorModel)) {
         return false;
      } else {
         ColorModel var2 = (ColorModel)var1;
         if (this == var2) {
            return true;
         } else if (this.supportsAlpha == var2.hasAlpha() && this.isAlphaPremultiplied == var2.isAlphaPremultiplied() && this.pixel_bits == var2.getPixelSize() && this.transparency == var2.getTransparency() && this.numComponents == var2.getNumComponents()) {
            int[] var3 = var2.getComponentSize();
            if (this.nBits != null && var3 != null) {
               for(int var4 = 0; var4 < this.numComponents; ++var4) {
                  if (this.nBits[var4] != var3[var4]) {
                     return false;
                  }
               }

               return true;
            } else {
               return this.nBits == null && var3 == null;
            }
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      boolean var1 = false;
      int var3 = (this.supportsAlpha ? 2 : 3) + (this.isAlphaPremultiplied ? 4 : 5) + this.pixel_bits * 6 + this.transparency * 7 + this.numComponents * 8;
      if (this.nBits != null) {
         for(int var2 = 0; var2 < this.numComponents; ++var2) {
            var3 += this.nBits[var2] * (var2 + 9);
         }
      }

      return var3;
   }

   public final ColorSpace getColorSpace() {
      return this.colorSpace;
   }

   public ColorModel coerceData(WritableRaster var1, boolean var2) {
      throw new UnsupportedOperationException("This method is not supported by this color model");
   }

   public boolean isCompatibleRaster(Raster var1) {
      throw new UnsupportedOperationException("This method has not been implemented for this ColorModel.");
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      throw new UnsupportedOperationException("This method is not supported by this color model");
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      throw new UnsupportedOperationException("This method is not supported by this color model");
   }

   public boolean isCompatibleSampleModel(SampleModel var1) {
      throw new UnsupportedOperationException("This method is not supported by this color model");
   }

   public void finalize() {
   }

   public WritableRaster getAlphaRaster(WritableRaster var1) {
      return null;
   }

   public String toString() {
      return new String("ColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied);
   }

   static int getDefaultTransferType(int var0) {
      if (var0 <= 8) {
         return 0;
      } else if (var0 <= 16) {
         return 1;
      } else {
         return var0 <= 32 ? 3 : 32;
      }
   }

   static boolean isLinearRGBspace(ColorSpace var0) {
      return var0 == CMSManager.LINEAR_RGBspace;
   }

   static boolean isLinearGRAYspace(ColorSpace var0) {
      return var0 == CMSManager.GRAYspace;
   }

   static byte[] getLinearRGB8TosRGB8LUT() {
      if (l8Tos8 == null) {
         l8Tos8 = new byte[256];

         for(int var2 = 0; var2 <= 255; ++var2) {
            float var0 = (float)var2 / 255.0F;
            float var1;
            if (var0 <= 0.0031308F) {
               var1 = var0 * 12.92F;
            } else {
               var1 = 1.055F * (float)Math.pow((double)var0, 0.4166666666666667D) - 0.055F;
            }

            l8Tos8[var2] = (byte)Math.round(var1 * 255.0F);
         }
      }

      return l8Tos8;
   }

   static byte[] getsRGB8ToLinearRGB8LUT() {
      if (s8Tol8 == null) {
         s8Tol8 = new byte[256];

         for(int var2 = 0; var2 <= 255; ++var2) {
            float var0 = (float)var2 / 255.0F;
            float var1;
            if (var0 <= 0.04045F) {
               var1 = var0 / 12.92F;
            } else {
               var1 = (float)Math.pow((double)((var0 + 0.055F) / 1.055F), 2.4D);
            }

            s8Tol8[var2] = (byte)Math.round(var1 * 255.0F);
         }
      }

      return s8Tol8;
   }

   static byte[] getLinearRGB16TosRGB8LUT() {
      if (l16Tos8 == null) {
         l16Tos8 = new byte[65536];

         for(int var2 = 0; var2 <= 65535; ++var2) {
            float var0 = (float)var2 / 65535.0F;
            float var1;
            if (var0 <= 0.0031308F) {
               var1 = var0 * 12.92F;
            } else {
               var1 = 1.055F * (float)Math.pow((double)var0, 0.4166666666666667D) - 0.055F;
            }

            l16Tos8[var2] = (byte)Math.round(var1 * 255.0F);
         }
      }

      return l16Tos8;
   }

   static short[] getsRGB8ToLinearRGB16LUT() {
      if (s8Tol16 == null) {
         s8Tol16 = new short[256];

         for(int var2 = 0; var2 <= 255; ++var2) {
            float var0 = (float)var2 / 255.0F;
            float var1;
            if (var0 <= 0.04045F) {
               var1 = var0 / 12.92F;
            } else {
               var1 = (float)Math.pow((double)((var0 + 0.055F) / 1.055F), 2.4D);
            }

            s8Tol16[var2] = (short)Math.round(var1 * 65535.0F);
         }
      }

      return s8Tol16;
   }

   static byte[] getGray8TosRGB8LUT(ICC_ColorSpace var0) {
      if (isLinearGRAYspace(var0)) {
         return getLinearRGB8TosRGB8LUT();
      } else {
         byte[] var1;
         if (g8Tos8Map != null) {
            var1 = (byte[])g8Tos8Map.get(var0);
            if (var1 != null) {
               return var1;
            }
         }

         var1 = new byte[256];

         for(int var2 = 0; var2 <= 255; ++var2) {
            var1[var2] = (byte)var2;
         }

         ColorTransform[] var9 = new ColorTransform[2];
         PCMM var3 = CMSManager.getModule();
         ICC_ColorSpace var4 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
         var9[0] = var3.createTransform(var0.getProfile(), -1, 1);
         var9[1] = var3.createTransform(var4.getProfile(), -1, 2);
         ColorTransform var5 = var3.createTransform(var9);
         byte[] var6 = var5.colorConvert((byte[])var1, (byte[])null);
         int var7 = 0;

         for(int var8 = 2; var7 <= 255; var8 += 3) {
            var1[var7] = var6[var8];
            ++var7;
         }

         if (g8Tos8Map == null) {
            g8Tos8Map = Collections.synchronizedMap(new WeakHashMap(2));
         }

         g8Tos8Map.put(var0, var1);
         return var1;
      }
   }

   static byte[] getLinearGray16ToOtherGray8LUT(ICC_ColorSpace var0) {
      if (lg16Toog8Map != null) {
         byte[] var1 = (byte[])lg16Toog8Map.get(var0);
         if (var1 != null) {
            return var1;
         }
      }

      short[] var8 = new short[65536];

      for(int var2 = 0; var2 <= 65535; ++var2) {
         var8[var2] = (short)var2;
      }

      ColorTransform[] var9 = new ColorTransform[2];
      PCMM var3 = CMSManager.getModule();
      ICC_ColorSpace var4 = (ICC_ColorSpace)ColorSpace.getInstance(1003);
      var9[0] = var3.createTransform(var4.getProfile(), -1, 1);
      var9[1] = var3.createTransform(var0.getProfile(), -1, 2);
      ColorTransform var5 = var3.createTransform(var9);
      var8 = var5.colorConvert((short[])var8, (short[])null);
      byte[] var6 = new byte[65536];

      for(int var7 = 0; var7 <= 65535; ++var7) {
         var6[var7] = (byte)((int)((float)(var8[var7] & '\uffff') * 0.0038910506F + 0.5F));
      }

      if (lg16Toog8Map == null) {
         lg16Toog8Map = Collections.synchronizedMap(new WeakHashMap(2));
      }

      lg16Toog8Map.put(var0, var6);
      return var6;
   }

   static byte[] getGray16TosRGB8LUT(ICC_ColorSpace var0) {
      if (isLinearGRAYspace(var0)) {
         return getLinearRGB16TosRGB8LUT();
      } else {
         if (g16Tos8Map != null) {
            byte[] var1 = (byte[])g16Tos8Map.get(var0);
            if (var1 != null) {
               return var1;
            }
         }

         short[] var9 = new short[65536];

         for(int var2 = 0; var2 <= 65535; ++var2) {
            var9[var2] = (short)var2;
         }

         ColorTransform[] var10 = new ColorTransform[2];
         PCMM var3 = CMSManager.getModule();
         ICC_ColorSpace var4 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
         var10[0] = var3.createTransform(var0.getProfile(), -1, 1);
         var10[1] = var3.createTransform(var4.getProfile(), -1, 2);
         ColorTransform var5 = var3.createTransform(var10);
         var9 = var5.colorConvert((short[])var9, (short[])null);
         byte[] var6 = new byte[65536];
         int var7 = 0;

         for(int var8 = 2; var7 <= 65535; var8 += 3) {
            var6[var7] = (byte)((int)((float)(var9[var8] & '\uffff') * 0.0038910506F + 0.5F));
            ++var7;
         }

         if (g16Tos8Map == null) {
            g16Tos8Map = Collections.synchronizedMap(new WeakHashMap(2));
         }

         g16Tos8Map.put(var0, var6);
         return var6;
      }
   }

   static short[] getLinearGray16ToOtherGray16LUT(ICC_ColorSpace var0) {
      short[] var1;
      if (lg16Toog16Map != null) {
         var1 = (short[])lg16Toog16Map.get(var0);
         if (var1 != null) {
            return var1;
         }
      }

      var1 = new short[65536];

      for(int var2 = 0; var2 <= 65535; ++var2) {
         var1[var2] = (short)var2;
      }

      ColorTransform[] var7 = new ColorTransform[2];
      PCMM var3 = CMSManager.getModule();
      ICC_ColorSpace var4 = (ICC_ColorSpace)ColorSpace.getInstance(1003);
      var7[0] = var3.createTransform(var4.getProfile(), -1, 1);
      var7[1] = var3.createTransform(var0.getProfile(), -1, 2);
      ColorTransform var5 = var3.createTransform(var7);
      short[] var6 = var5.colorConvert((short[])var1, (short[])null);
      if (lg16Toog16Map == null) {
         lg16Toog16Map = Collections.synchronizedMap(new WeakHashMap(2));
      }

      lg16Toog16Map.put(var0, var6);
      return var6;
   }

   static {
      loadLibraries();
      initIDs();
      l8Tos8 = null;
      s8Tol8 = null;
      l16Tos8 = null;
      s8Tol16 = null;
      g8Tos8Map = null;
      lg16Toog8Map = null;
      g16Tos8Map = null;
      lg16Toog16Map = null;
   }
}
