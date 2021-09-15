package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

class EffectUtils {
   static void clearImage(BufferedImage var0) {
      Graphics2D var1 = var0.createGraphics();
      var1.setComposite(AlphaComposite.Clear);
      var1.fillRect(0, 0, var0.getWidth(), var0.getHeight());
      var1.dispose();
   }

   static BufferedImage gaussianBlur(BufferedImage var0, BufferedImage var1, int var2) {
      int var3 = var0.getWidth();
      int var4 = var0.getHeight();
      if (var1 == null || var1.getWidth() != var3 || var1.getHeight() != var4 || var0.getType() != var1.getType()) {
         var1 = createColorModelCompatibleImage(var0);
      }

      float[] var5 = createGaussianKernel(var2);
      if (var0.getType() == 2) {
         int[] var6 = new int[var3 * var4];
         int[] var7 = new int[var3 * var4];
         getPixels(var0, 0, 0, var3, var4, (int[])var6);
         blur(var6, var7, var3, var4, var5, var2);
         blur(var7, var6, var4, var3, var5, var2);
         setPixels(var1, 0, 0, var3, var4, (int[])var6);
      } else {
         if (var0.getType() != 10) {
            throw new IllegalArgumentException("EffectUtils.gaussianBlur() src image is not a supported type, type=[" + var0.getType() + "]");
         }

         byte[] var8 = new byte[var3 * var4];
         byte[] var9 = new byte[var3 * var4];
         getPixels(var0, 0, 0, var3, var4, (byte[])var8);
         blur(var8, var9, var3, var4, var5, var2);
         blur(var9, var8, var4, var3, var5, var2);
         setPixels(var1, 0, 0, var3, var4, (byte[])var8);
      }

      return var1;
   }

   private static void blur(int[] var0, int[] var1, int var2, int var3, float[] var4, int var5) {
      for(int var14 = 0; var14 < var3; ++var14) {
         int var15 = var14;
         int var16 = var14 * var2;

         for(int var17 = 0; var17 < var2; ++var17) {
            float var9 = 0.0F;
            float var8 = 0.0F;
            float var7 = 0.0F;
            float var6 = 0.0F;

            for(int var18 = -var5; var18 <= var5; ++var18) {
               int var19 = var17 + var18;
               if (var19 < 0 || var19 >= var2) {
                  var19 = (var17 + var2) % var2;
               }

               int var20 = var0[var16 + var19];
               float var21 = var4[var5 + var18];
               var6 += var21 * (float)(var20 >> 24 & 255);
               var7 += var21 * (float)(var20 >> 16 & 255);
               var8 += var21 * (float)(var20 >> 8 & 255);
               var9 += var21 * (float)(var20 & 255);
            }

            int var10 = (int)(var6 + 0.5F);
            int var11 = (int)(var7 + 0.5F);
            int var12 = (int)(var8 + 0.5F);
            int var13 = (int)(var9 + 0.5F);
            var1[var15] = (var10 > 255 ? 255 : var10) << 24 | (var11 > 255 ? 255 : var11) << 16 | (var12 > 255 ? 255 : var12) << 8 | (var13 > 255 ? 255 : var13);
            var15 += var3;
         }
      }

   }

   static void blur(byte[] var0, byte[] var1, int var2, int var3, float[] var4, int var5) {
      for(int var8 = 0; var8 < var3; ++var8) {
         int var9 = var8;
         int var10 = var8 * var2;

         for(int var11 = 0; var11 < var2; ++var11) {
            float var6 = 0.0F;

            for(int var12 = -var5; var12 <= var5; ++var12) {
               int var13 = var11 + var12;
               if (var13 < 0 || var13 >= var2) {
                  var13 = (var11 + var2) % var2;
               }

               int var14 = var0[var10 + var13] & 255;
               float var15 = var4[var5 + var12];
               var6 += var15 * (float)var14;
            }

            int var7 = (int)(var6 + 0.5F);
            var1[var9] = (byte)(var7 > 255 ? 255 : var7);
            var9 += var3;
         }
      }

   }

   static float[] createGaussianKernel(int var0) {
      if (var0 < 1) {
         throw new IllegalArgumentException("Radius must be >= 1");
      } else {
         float[] var1 = new float[var0 * 2 + 1];
         float var2 = (float)var0 / 3.0F;
         float var3 = 2.0F * var2 * var2;
         float var4 = (float)Math.sqrt((double)var3 * 3.141592653589793D);
         float var5 = 0.0F;

         int var6;
         for(var6 = -var0; var6 <= var0; ++var6) {
            float var7 = (float)(var6 * var6);
            int var8 = var6 + var0;
            var1[var8] = (float)Math.exp((double)(-var7 / var3)) / var4;
            var5 += var1[var8];
         }

         for(var6 = 0; var6 < var1.length; ++var6) {
            var1[var6] /= var5;
         }

         return var1;
      }
   }

   static byte[] getPixels(BufferedImage var0, int var1, int var2, int var3, int var4, byte[] var5) {
      if (var3 != 0 && var4 != 0) {
         if (var5 == null) {
            var5 = new byte[var3 * var4];
         } else if (var5.length < var3 * var4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
         }

         int var6 = var0.getType();
         if (var6 == 10) {
            WritableRaster var7 = var0.getRaster();
            return (byte[])((byte[])var7.getDataElements(var1, var2, var3, var4, var5));
         } else {
            throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
         }
      } else {
         return new byte[0];
      }
   }

   static void setPixels(BufferedImage var0, int var1, int var2, int var3, int var4, byte[] var5) {
      if (var5 != null && var3 != 0 && var4 != 0) {
         if (var5.length < var3 * var4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
         } else {
            int var6 = var0.getType();
            if (var6 == 10) {
               WritableRaster var7 = var0.getRaster();
               var7.setDataElements(var1, var2, var3, var4, var5);
            } else {
               throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
            }
         }
      }
   }

   public static int[] getPixels(BufferedImage var0, int var1, int var2, int var3, int var4, int[] var5) {
      if (var3 != 0 && var4 != 0) {
         if (var5 == null) {
            var5 = new int[var3 * var4];
         } else if (var5.length < var3 * var4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
         }

         int var6 = var0.getType();
         if (var6 != 2 && var6 != 1) {
            return var0.getRGB(var1, var2, var3, var4, var5, 0, var3);
         } else {
            WritableRaster var7 = var0.getRaster();
            return (int[])((int[])var7.getDataElements(var1, var2, var3, var4, var5));
         }
      } else {
         return new int[0];
      }
   }

   public static void setPixels(BufferedImage var0, int var1, int var2, int var3, int var4, int[] var5) {
      if (var5 != null && var3 != 0 && var4 != 0) {
         if (var5.length < var3 * var4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
         } else {
            int var6 = var0.getType();
            if (var6 != 2 && var6 != 1) {
               var0.setRGB(var1, var2, var3, var4, var5, 0, var3);
            } else {
               WritableRaster var7 = var0.getRaster();
               var7.setDataElements(var1, var2, var3, var4, var5);
            }

         }
      }
   }

   public static BufferedImage createColorModelCompatibleImage(BufferedImage var0) {
      ColorModel var1 = var0.getColorModel();
      return new BufferedImage(var1, var1.createCompatibleWritableRaster(var0.getWidth(), var0.getHeight()), var1.isAlphaPremultiplied(), (Hashtable)null);
   }

   public static BufferedImage createCompatibleTranslucentImage(int var0, int var1) {
      return isHeadless() ? new BufferedImage(var0, var1, 2) : getGraphicsConfiguration().createCompatibleImage(var0, var1, 3);
   }

   private static boolean isHeadless() {
      return GraphicsEnvironment.isHeadless();
   }

   private static GraphicsConfiguration getGraphicsConfiguration() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
   }
}
