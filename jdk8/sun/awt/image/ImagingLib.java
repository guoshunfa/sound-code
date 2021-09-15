package sun.awt.image;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ImagingLib {
   static boolean useLib = true;
   static boolean verbose = false;
   private static final int NUM_NATIVE_OPS = 3;
   private static final int LOOKUP_OP = 0;
   private static final int AFFINE_OP = 1;
   private static final int CONVOLVE_OP = 2;
   private static Class[] nativeOpClass = new Class[3];

   private static native boolean init();

   public static native int transformBI(BufferedImage var0, BufferedImage var1, double[] var2, int var3);

   public static native int transformRaster(Raster var0, Raster var1, double[] var2, int var3);

   public static native int convolveBI(BufferedImage var0, BufferedImage var1, Kernel var2, int var3);

   public static native int convolveRaster(Raster var0, Raster var1, Kernel var2, int var3);

   public static native int lookupByteBI(BufferedImage var0, BufferedImage var1, byte[][] var2);

   public static native int lookupByteRaster(Raster var0, Raster var1, byte[][] var2);

   private static int getNativeOpIndex(Class var0) {
      int var1 = -1;

      for(int var2 = 0; var2 < 3; ++var2) {
         if (var0 == nativeOpClass[var2]) {
            var1 = var2;
            break;
         }
      }

      return var1;
   }

   public static WritableRaster filter(RasterOp var0, Raster var1, WritableRaster var2) {
      if (!useLib) {
         return null;
      } else {
         if (var2 == null) {
            var2 = var0.createCompatibleDestRaster(var1);
         }

         WritableRaster var3 = null;
         switch(getNativeOpIndex(var0.getClass())) {
         case 0:
            LookupTable var4 = ((LookupOp)var0).getTable();
            if (var4.getOffset() != 0) {
               return null;
            }

            if (var4 instanceof ByteLookupTable) {
               ByteLookupTable var8 = (ByteLookupTable)var4;
               if (lookupByteRaster(var1, var2, var8.getTable()) > 0) {
                  var3 = var2;
               }
            }
            break;
         case 1:
            AffineTransformOp var5 = (AffineTransformOp)var0;
            double[] var6 = new double[6];
            var5.getTransform().getMatrix(var6);
            if (transformRaster(var1, var2, var6, var5.getInterpolationType()) > 0) {
               var3 = var2;
            }
            break;
         case 2:
            ConvolveOp var7 = (ConvolveOp)var0;
            if (convolveRaster(var1, var2, var7.getKernel(), var7.getEdgeCondition()) > 0) {
               var3 = var2;
            }
         }

         if (var3 != null) {
            SunWritableRaster.markDirty(var3);
         }

         return var3;
      }
   }

   public static BufferedImage filter(BufferedImageOp var0, BufferedImage var1, BufferedImage var2) {
      if (verbose) {
         System.out.println("in filter and op is " + var0 + "bufimage is " + var1 + " and " + var2);
      }

      if (!useLib) {
         return null;
      } else {
         if (var2 == null) {
            var2 = var0.createCompatibleDestImage(var1, (ColorModel)null);
         }

         BufferedImage var3 = null;
         switch(getNativeOpIndex(var0.getClass())) {
         case 0:
            LookupTable var4 = ((LookupOp)var0).getTable();
            if (var4.getOffset() != 0) {
               return null;
            }

            if (var4 instanceof ByteLookupTable) {
               ByteLookupTable var9 = (ByteLookupTable)var4;
               if (lookupByteBI(var1, var2, var9.getTable()) > 0) {
                  var3 = var2;
               }
            }
            break;
         case 1:
            AffineTransformOp var5 = (AffineTransformOp)var0;
            double[] var6 = new double[6];
            AffineTransform var7 = var5.getTransform();
            var5.getTransform().getMatrix(var6);
            if (transformBI(var1, var2, var6, var5.getInterpolationType()) > 0) {
               var3 = var2;
            }
            break;
         case 2:
            ConvolveOp var8 = (ConvolveOp)var0;
            if (convolveBI(var1, var2, var8.getKernel(), var8.getEdgeCondition()) > 0) {
               var3 = var2;
            }
         }

         if (var3 != null) {
            SunWritableRaster.markDirty((Image)var3);
         }

         return var3;
      }
   }

   static {
      PrivilegedAction var0 = new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String var1 = System.getProperty("os.arch");
            if (var1 == null || !var1.startsWith("sparc")) {
               try {
                  System.loadLibrary("mlib_image");
               } catch (UnsatisfiedLinkError var3) {
                  return Boolean.FALSE;
               }
            }

            boolean var2 = ImagingLib.init();
            return var2;
         }
      };
      useLib = (Boolean)AccessController.doPrivileged(var0);

      try {
         nativeOpClass[0] = Class.forName("java.awt.image.LookupOp");
      } catch (ClassNotFoundException var4) {
         System.err.println("Could not find class: " + var4);
      }

      try {
         nativeOpClass[1] = Class.forName("java.awt.image.AffineTransformOp");
      } catch (ClassNotFoundException var3) {
         System.err.println("Could not find class: " + var3);
      }

      try {
         nativeOpClass[2] = Class.forName("java.awt.image.ConvolveOp");
      } catch (ClassNotFoundException var2) {
         System.err.println("Could not find class: " + var2);
      }

   }
}
