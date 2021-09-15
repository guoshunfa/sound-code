package java.awt;

import java.awt.image.ColorModel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public abstract class GraphicsDevice {
   private Window fullScreenWindow;
   private AppContext fullScreenAppContext;
   private final Object fsAppContextLock = new Object();
   private Rectangle windowedModeBounds;
   public static final int TYPE_RASTER_SCREEN = 0;
   public static final int TYPE_PRINTER = 1;
   public static final int TYPE_IMAGE_BUFFER = 2;

   protected GraphicsDevice() {
   }

   public abstract int getType();

   public abstract String getIDstring();

   public abstract GraphicsConfiguration[] getConfigurations();

   public abstract GraphicsConfiguration getDefaultConfiguration();

   public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate var1) {
      GraphicsConfiguration[] var2 = this.getConfigurations();
      return var1.getBestConfiguration(var2);
   }

   public boolean isFullScreenSupported() {
      return false;
   }

   public void setFullScreenWindow(Window var1) {
      GraphicsConfiguration var5;
      if (var1 != null) {
         if (var1.getShape() != null) {
            var1.setShape((Shape)null);
         }

         if (var1.getOpacity() < 1.0F) {
            var1.setOpacity(1.0F);
         }

         if (!var1.isOpaque()) {
            Color var2 = var1.getBackground();
            var2 = new Color(var2.getRed(), var2.getGreen(), var2.getBlue(), 255);
            var1.setBackground(var2);
         }

         var5 = var1.getGraphicsConfiguration();
         if (var5 != null && var5.getDevice() != this && var5.getDevice().getFullScreenWindow() == var1) {
            var5.getDevice().setFullScreenWindow((Window)null);
         }
      }

      if (this.fullScreenWindow != null && this.windowedModeBounds != null) {
         if (this.windowedModeBounds.width == 0) {
            this.windowedModeBounds.width = 1;
         }

         if (this.windowedModeBounds.height == 0) {
            this.windowedModeBounds.height = 1;
         }

         this.fullScreenWindow.setBounds(this.windowedModeBounds);
      }

      synchronized(this.fsAppContextLock) {
         if (var1 == null) {
            this.fullScreenAppContext = null;
         } else {
            this.fullScreenAppContext = AppContext.getAppContext();
         }

         this.fullScreenWindow = var1;
      }

      if (this.fullScreenWindow != null) {
         this.windowedModeBounds = this.fullScreenWindow.getBounds();
         var5 = this.getDefaultConfiguration();
         Rectangle var3 = var5.getBounds();
         if (SunToolkit.isDispatchThreadForAppContext(this.fullScreenWindow)) {
            this.fullScreenWindow.setGraphicsConfiguration(var5);
         }

         this.fullScreenWindow.setBounds(var3.x, var3.y, var3.width, var3.height);
         this.fullScreenWindow.setVisible(true);
         this.fullScreenWindow.toFront();
      }

   }

   public Window getFullScreenWindow() {
      Window var1 = null;
      synchronized(this.fsAppContextLock) {
         if (this.fullScreenAppContext == AppContext.getAppContext()) {
            var1 = this.fullScreenWindow;
         }

         return var1;
      }
   }

   public boolean isDisplayChangeSupported() {
      return false;
   }

   public void setDisplayMode(DisplayMode var1) {
      throw new UnsupportedOperationException("Cannot change display mode");
   }

   public DisplayMode getDisplayMode() {
      GraphicsConfiguration var1 = this.getDefaultConfiguration();
      Rectangle var2 = var1.getBounds();
      ColorModel var3 = var1.getColorModel();
      return new DisplayMode(var2.width, var2.height, var3.getPixelSize(), 0);
   }

   public DisplayMode[] getDisplayModes() {
      return new DisplayMode[]{this.getDisplayMode()};
   }

   public int getAvailableAcceleratedMemory() {
      return -1;
   }

   public boolean isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency var1) {
      switch(var1) {
      case PERPIXEL_TRANSPARENT:
         return isWindowShapingSupported();
      case TRANSLUCENT:
         return isWindowOpacitySupported();
      case PERPIXEL_TRANSLUCENT:
         return this.isWindowPerpixelTranslucencySupported();
      default:
         return false;
      }
   }

   static boolean isWindowShapingSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return !(var0 instanceof SunToolkit) ? false : ((SunToolkit)var0).isWindowShapingSupported();
   }

   static boolean isWindowOpacitySupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return !(var0 instanceof SunToolkit) ? false : ((SunToolkit)var0).isWindowOpacitySupported();
   }

   boolean isWindowPerpixelTranslucencySupported() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (!(var1 instanceof SunToolkit)) {
         return false;
      } else if (!((SunToolkit)var1).isWindowTranslucencySupported()) {
         return false;
      } else {
         return this.getTranslucencyCapableGC() != null;
      }
   }

   GraphicsConfiguration getTranslucencyCapableGC() {
      GraphicsConfiguration var1 = this.getDefaultConfiguration();
      if (var1.isTranslucencyCapable()) {
         return var1;
      } else {
         GraphicsConfiguration[] var2 = this.getConfigurations();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].isTranslucencyCapable()) {
               return var2[var3];
            }
         }

         return null;
      }
   }

   public static enum WindowTranslucency {
      PERPIXEL_TRANSPARENT,
      TRANSLUCENT,
      PERPIXEL_TRANSLUCENT;
   }
}
