package sun.awt;

import java.awt.AWTPermission;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.Window;
import java.util.Objects;
import sun.java2d.opengl.CGLGraphicsConfig;

public final class CGraphicsDevice extends GraphicsDevice implements DisplayChangedListener {
   private volatile int displayID;
   private volatile double xResolution;
   private volatile double yResolution;
   private volatile int scale;
   private final GraphicsConfiguration[] configs;
   private final int DEFAULT_CONFIG = 0;
   private static AWTPermission fullScreenExclusivePermission;
   private DisplayMode originalMode;

   public CGraphicsDevice(int var1) {
      this.displayID = var1;
      this.configs = new GraphicsConfiguration[]{CGLGraphicsConfig.getConfig(this, 0)};
   }

   public int getCGDisplayID() {
      return this.displayID;
   }

   public GraphicsConfiguration[] getConfigurations() {
      return (GraphicsConfiguration[])this.configs.clone();
   }

   public GraphicsConfiguration getDefaultConfiguration() {
      return this.configs[0];
   }

   public String getIDstring() {
      return "Display " + this.displayID;
   }

   public int getType() {
      return 0;
   }

   public double getXResolution() {
      return this.xResolution;
   }

   public double getYResolution() {
      return this.yResolution;
   }

   public Insets getScreenInsets() {
      return nativeGetScreenInsets(this.displayID);
   }

   public int getScaleFactor() {
      return this.scale;
   }

   public void invalidate(int var1) {
      this.displayID = var1;
   }

   public void displayChanged() {
      this.xResolution = nativeGetXResolution(this.displayID);
      this.yResolution = nativeGetYResolution(this.displayID);
      this.scale = (int)nativeGetScaleFactor(this.displayID);
   }

   public void paletteChanged() {
   }

   public synchronized void setFullScreenWindow(Window var1) {
      Window var2 = this.getFullScreenWindow();
      if (var1 != var2) {
         boolean var3 = this.isFullScreenSupported();
         if (var3 && var2 != null) {
            exitFullScreenExclusive(var2);
            if (this.originalMode != null) {
               this.setDisplayMode(this.originalMode);
               this.originalMode = null;
            }
         }

         super.setFullScreenWindow(var1);
         if (var3 && var1 != null) {
            if (this.isDisplayChangeSupported()) {
               this.originalMode = this.getDisplayMode();
            }

            enterFullScreenExclusive(var1);
         }

      }
   }

   public boolean isFullScreenSupported() {
      return isFSExclusiveModeAllowed();
   }

   private static boolean isFSExclusiveModeAllowed() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         if (fullScreenExclusivePermission == null) {
            fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
         }

         try {
            var0.checkPermission(fullScreenExclusivePermission);
         } catch (SecurityException var2) {
            return false;
         }
      }

      return true;
   }

   private static void enterFullScreenExclusive(Window var0) {
      FullScreenCapable var1 = (FullScreenCapable)var0.getPeer();
      if (var1 != null) {
         var1.enterFullScreenMode();
      }

   }

   private static void exitFullScreenExclusive(Window var0) {
      FullScreenCapable var1 = (FullScreenCapable)var0.getPeer();
      if (var1 != null) {
         var1.exitFullScreenMode();
      }

   }

   public boolean isDisplayChangeSupported() {
      return true;
   }

   public void setDisplayMode(DisplayMode var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid display mode");
      } else {
         if (!Objects.equals(var1, this.getDisplayMode())) {
            nativeSetDisplayMode(this.displayID, var1.getWidth(), var1.getHeight(), var1.getBitDepth(), var1.getRefreshRate());
            if (this.isFullScreenSupported() && this.getFullScreenWindow() != null) {
               this.getFullScreenWindow().setSize(var1.getWidth(), var1.getHeight());
            }
         }

      }
   }

   public DisplayMode getDisplayMode() {
      return nativeGetDisplayMode(this.displayID);
   }

   public DisplayMode[] getDisplayModes() {
      return nativeGetDisplayModes(this.displayID);
   }

   private static native double nativeGetScaleFactor(int var0);

   private static native void nativeSetDisplayMode(int var0, int var1, int var2, int var3, int var4);

   private static native DisplayMode nativeGetDisplayMode(int var0);

   private static native DisplayMode[] nativeGetDisplayModes(int var0);

   private static native double nativeGetXResolution(int var0);

   private static native double nativeGetYResolution(int var0);

   private static native Insets nativeGetScreenInsets(int var0);
}
