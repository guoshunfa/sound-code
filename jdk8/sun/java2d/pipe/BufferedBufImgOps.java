package sun.java2d.pipe;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import sun.java2d.SurfaceData;

public class BufferedBufImgOps {
   public static void enableBufImgOp(RenderQueue var0, SurfaceData var1, BufferedImage var2, BufferedImageOp var3) {
      if (var3 instanceof ConvolveOp) {
         enableConvolveOp(var0, var1, (ConvolveOp)var3);
      } else if (var3 instanceof RescaleOp) {
         enableRescaleOp(var0, var1, var2, (RescaleOp)var3);
      } else {
         if (!(var3 instanceof LookupOp)) {
            throw new InternalError("Unknown BufferedImageOp");
         }

         enableLookupOp(var0, var1, var2, (LookupOp)var3);
      }

   }

   public static void disableBufImgOp(RenderQueue var0, BufferedImageOp var1) {
      if (var1 instanceof ConvolveOp) {
         disableConvolveOp(var0);
      } else if (var1 instanceof RescaleOp) {
         disableRescaleOp(var0);
      } else {
         if (!(var1 instanceof LookupOp)) {
            throw new InternalError("Unknown BufferedImageOp");
         }

         disableLookupOp(var0);
      }

   }

   public static boolean isConvolveOpValid(ConvolveOp var0) {
      Kernel var1 = var0.getKernel();
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      return var2 == 3 && var3 == 3 || var2 == 5 && var3 == 5;
   }

   private static void enableConvolveOp(RenderQueue var0, SurfaceData var1, ConvolveOp var2) {
      boolean var3 = var2.getEdgeCondition() == 0;
      Kernel var4 = var2.getKernel();
      int var5 = var4.getWidth();
      int var6 = var4.getHeight();
      int var7 = var5 * var6;
      byte var8 = 4;
      int var9 = 24 + var7 * var8;
      RenderBuffer var10 = var0.getBuffer();
      var0.ensureCapacityAndAlignment(var9, 4);
      var10.putInt(120);
      var10.putLong(var1.getNativeOps());
      var10.putInt(var3 ? 1 : 0);
      var10.putInt(var5);
      var10.putInt(var6);
      var10.put(var4.getKernelData((float[])null));
   }

   private static void disableConvolveOp(RenderQueue var0) {
      RenderBuffer var1 = var0.getBuffer();
      var0.ensureCapacity(4);
      var1.putInt(121);
   }

   public static boolean isRescaleOpValid(RescaleOp var0, BufferedImage var1) {
      int var2 = var0.getNumFactors();
      ColorModel var3 = var1.getColorModel();
      if (var3 instanceof IndexColorModel) {
         throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
      } else if (var2 != 1 && var2 != var3.getNumColorComponents() && var2 != var3.getNumComponents()) {
         throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
      } else {
         int var4 = var3.getColorSpace().getType();
         if (var4 != 5 && var4 != 6) {
            return false;
         } else {
            return var2 != 2 && var2 <= 4;
         }
      }
   }

   private static void enableRescaleOp(RenderQueue var0, SurfaceData var1, BufferedImage var2, RescaleOp var3) {
      ColorModel var4 = var2.getColorModel();
      boolean var5 = var4.hasAlpha() && var4.isAlphaPremultiplied();
      int var6 = var3.getNumFactors();
      float[] var7 = var3.getScaleFactors((float[])null);
      float[] var8 = var3.getOffsets((float[])null);
      float[] var9;
      float[] var10;
      int var11;
      if (var6 == 1) {
         var9 = new float[4];
         var10 = new float[4];

         for(var11 = 0; var11 < 3; ++var11) {
            var9[var11] = var7[0];
            var10[var11] = var8[0];
         }

         var9[3] = 1.0F;
         var10[3] = 0.0F;
      } else if (var6 == 3) {
         var9 = new float[4];
         var10 = new float[4];

         for(var11 = 0; var11 < 3; ++var11) {
            var9[var11] = var7[var11];
            var10[var11] = var8[var11];
         }

         var9[3] = 1.0F;
         var10[3] = 0.0F;
      } else {
         var9 = var7;
         var10 = var8;
      }

      int var12;
      int var13;
      if (var4.getNumComponents() == 1) {
         var11 = var4.getComponentSize(0);
         var12 = (1 << var11) - 1;

         for(var13 = 0; var13 < 3; ++var13) {
            var10[var13] /= (float)var12;
         }
      } else {
         for(var11 = 0; var11 < var4.getNumComponents(); ++var11) {
            var12 = var4.getComponentSize(var11);
            var13 = (1 << var12) - 1;
            var10[var11] /= (float)var13;
         }
      }

      byte var14 = 4;
      var12 = 16 + 4 * var14 * 2;
      RenderBuffer var15 = var0.getBuffer();
      var0.ensureCapacityAndAlignment(var12, 4);
      var15.putInt(122);
      var15.putLong(var1.getNativeOps());
      var15.putInt(var5 ? 1 : 0);
      var15.put(var9);
      var15.put(var10);
   }

   private static void disableRescaleOp(RenderQueue var0) {
      RenderBuffer var1 = var0.getBuffer();
      var0.ensureCapacity(4);
      var1.putInt(123);
   }

   public static boolean isLookupOpValid(LookupOp var0, BufferedImage var1) {
      LookupTable var2 = var0.getTable();
      int var3 = var2.getNumComponents();
      ColorModel var4 = var1.getColorModel();
      if (var4 instanceof IndexColorModel) {
         throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
      } else if (var3 != 1 && var3 != var4.getNumComponents() && var3 != var4.getNumColorComponents()) {
         throw new IllegalArgumentException("Number of arrays in the  lookup table (" + var3 + ") is not compatible with the src image: " + var1);
      } else {
         int var5 = var4.getColorSpace().getType();
         if (var5 != 5 && var5 != 6) {
            return false;
         } else if (var3 != 2 && var3 <= 4) {
            int var7;
            if (var2 instanceof ByteLookupTable) {
               byte[][] var8 = ((ByteLookupTable)var2).getTable();

               for(var7 = 1; var7 < var8.length; ++var7) {
                  if (var8[var7].length > 256 || var8[var7].length != var8[var7 - 1].length) {
                     return false;
                  }
               }
            } else {
               if (!(var2 instanceof ShortLookupTable)) {
                  return false;
               }

               short[][] var6 = ((ShortLookupTable)var2).getTable();

               for(var7 = 1; var7 < var6.length; ++var7) {
                  if (var6[var7].length > 256 || var6[var7].length != var6[var7 - 1].length) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private static void enableLookupOp(RenderQueue var0, SurfaceData var1, BufferedImage var2, LookupOp var3) {
      boolean var4 = var2.getColorModel().hasAlpha() && var2.isAlphaPremultiplied();
      LookupTable var5 = var3.getTable();
      int var6 = var5.getNumComponents();
      int var7 = var5.getOffset();
      int var8;
      byte var9;
      boolean var10;
      if (var5 instanceof ShortLookupTable) {
         short[][] var11 = ((ShortLookupTable)var5).getTable();
         var8 = var11[0].length;
         var9 = 2;
         var10 = true;
      } else {
         byte[][] var18 = ((ByteLookupTable)var5).getTable();
         var8 = var18[0].length;
         var9 = 1;
         var10 = false;
      }

      int var19 = var6 * var8 * var9;
      int var12 = var19 + 3 & -4;
      int var13 = var12 - var19;
      int var14 = 32 + var12;
      RenderBuffer var15 = var0.getBuffer();
      var0.ensureCapacityAndAlignment(var14, 4);
      var15.putInt(124);
      var15.putLong(var1.getNativeOps());
      var15.putInt(var4 ? 1 : 0);
      var15.putInt(var10 ? 1 : 0);
      var15.putInt(var6);
      var15.putInt(var8);
      var15.putInt(var7);
      int var17;
      if (var10) {
         short[][] var16 = ((ShortLookupTable)var5).getTable();

         for(var17 = 0; var17 < var6; ++var17) {
            var15.put(var16[var17]);
         }
      } else {
         byte[][] var20 = ((ByteLookupTable)var5).getTable();

         for(var17 = 0; var17 < var6; ++var17) {
            var15.put(var20[var17]);
         }
      }

      if (var13 != 0) {
         var15.position((long)(var15.position() + var13));
      }

   }

   private static void disableLookupOp(RenderQueue var0) {
      RenderBuffer var1 = var0.getBuffer();
      var0.ensureCapacity(4);
      var1.putInt(125);
   }
}
