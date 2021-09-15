package com.sun.awt;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public final class AWTUtilities {
   private AWTUtilities() {
   }

   public static boolean isTranslucencySupported(AWTUtilities.Translucency var0) {
      switch(var0) {
      case PERPIXEL_TRANSPARENT:
         return isWindowShapingSupported();
      case TRANSLUCENT:
         return isWindowOpacitySupported();
      case PERPIXEL_TRANSLUCENT:
         return isWindowTranslucencySupported();
      default:
         return false;
      }
   }

   private static boolean isWindowOpacitySupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return !(var0 instanceof SunToolkit) ? false : ((SunToolkit)var0).isWindowOpacitySupported();
   }

   public static void setWindowOpacity(Window var0, float var1) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else {
         AWTAccessor.getWindowAccessor().setOpacity(var0, var1);
      }
   }

   public static float getWindowOpacity(Window var0) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else {
         return AWTAccessor.getWindowAccessor().getOpacity(var0);
      }
   }

   public static boolean isWindowShapingSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return !(var0 instanceof SunToolkit) ? false : ((SunToolkit)var0).isWindowShapingSupported();
   }

   public static Shape getWindowShape(Window var0) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else {
         return AWTAccessor.getWindowAccessor().getShape(var0);
      }
   }

   public static void setWindowShape(Window var0, Shape var1) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else {
         AWTAccessor.getWindowAccessor().setShape(var0, var1);
      }
   }

   private static boolean isWindowTranslucencySupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      if (!(var0 instanceof SunToolkit)) {
         return false;
      } else if (!((SunToolkit)var0).isWindowTranslucencySupported()) {
         return false;
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         if (isTranslucencyCapable(var1.getDefaultScreenDevice().getDefaultConfiguration())) {
            return true;
         } else {
            GraphicsDevice[] var2 = var1.getScreenDevices();

            for(int var3 = 0; var3 < var2.length; ++var3) {
               GraphicsConfiguration[] var4 = var2[var3].getConfigurations();

               for(int var5 = 0; var5 < var4.length; ++var5) {
                  if (isTranslucencyCapable(var4[var5])) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static void setWindowOpaque(Window var0, boolean var1) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else if (!var1 && !isTranslucencySupported(AWTUtilities.Translucency.PERPIXEL_TRANSLUCENT)) {
         throw new UnsupportedOperationException("The PERPIXEL_TRANSLUCENT translucency kind is not supported");
      } else {
         AWTAccessor.getWindowAccessor().setOpaque(var0, var1);
      }
   }

   public static boolean isWindowOpaque(Window var0) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else {
         return var0.isOpaque();
      }
   }

   public static boolean isTranslucencyCapable(GraphicsConfiguration var0) {
      if (var0 == null) {
         throw new NullPointerException("The gc argument should not be null");
      } else {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         return !(var1 instanceof SunToolkit) ? false : ((SunToolkit)var1).isTranslucencyCapable(var0);
      }
   }

   public static void setComponentMixingCutoutShape(Component var0, Shape var1) {
      if (var0 == null) {
         throw new NullPointerException("The component argument should not be null.");
      } else {
         AWTAccessor.getComponentAccessor().setMixingCutoutShape(var0, var1);
      }
   }

   public static enum Translucency {
      PERPIXEL_TRANSPARENT,
      TRANSLUCENT,
      PERPIXEL_TRANSLUCENT;
   }
}
