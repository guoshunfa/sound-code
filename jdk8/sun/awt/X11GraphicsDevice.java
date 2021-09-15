package sun.awt;

import java.awt.AWTPermission;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import sun.java2d.loops.SurfaceType;
import sun.java2d.opengl.GLXGraphicsConfig;
import sun.java2d.xr.XRGraphicsConfig;
import sun.misc.ThreadGroupUtils;

public class X11GraphicsDevice extends GraphicsDevice implements DisplayChangedListener {
   int screen;
   HashMap x11ProxyKeyMap = new HashMap();
   private static AWTPermission fullScreenExclusivePermission;
   private static Boolean xrandrExtSupported;
   private final Object configLock = new Object();
   private SunDisplayChanger topLevels = new SunDisplayChanger();
   private DisplayMode origDisplayMode;
   private boolean shutdownHookRegistered;
   GraphicsConfiguration[] configs;
   GraphicsConfiguration defaultConfig;
   HashSet doubleBufferVisuals;

   public X11GraphicsDevice(int var1) {
      this.screen = var1;
   }

   private static native void initIDs();

   public int getScreen() {
      return this.screen;
   }

   public Object getProxyKeyFor(SurfaceType var1) {
      synchronized(this.x11ProxyKeyMap) {
         Object var3 = this.x11ProxyKeyMap.get(var1);
         if (var3 == null) {
            var3 = new Object();
            this.x11ProxyKeyMap.put(var1, var3);
         }

         return var3;
      }
   }

   public native long getDisplay();

   public int getType() {
      return 0;
   }

   public String getIDstring() {
      return ":0." + this.screen;
   }

   public GraphicsConfiguration[] getConfigurations() {
      if (this.configs == null) {
         synchronized(this.configLock) {
            this.makeConfigurations();
         }
      }

      return (GraphicsConfiguration[])this.configs.clone();
   }

   private void makeConfigurations() {
      if (this.configs == null) {
         int var1 = 1;
         int var2 = this.getNumConfigs(this.screen);
         GraphicsConfiguration[] var3 = new GraphicsConfiguration[var2];
         if (this.defaultConfig == null) {
            var3[0] = this.getDefaultConfiguration();
         } else {
            var3[0] = this.defaultConfig;
         }

         boolean var4 = X11GraphicsEnvironment.isGLXAvailable();
         boolean var5 = X11GraphicsEnvironment.isXRenderAvailable();
         boolean var6 = isDBESupported();
         if (var6 && this.doubleBufferVisuals == null) {
            this.doubleBufferVisuals = new HashSet();
            this.getDoubleBufferVisuals(this.screen);
         }

         for(; var1 < var2; ++var1) {
            int var7 = this.getConfigVisualId(var1, this.screen);
            int var8 = this.getConfigDepth(var1, this.screen);
            if (var4) {
               var3[var1] = GLXGraphicsConfig.getConfig(this, var7);
            }

            if (var3[var1] == null) {
               boolean var9 = var6 && this.doubleBufferVisuals.contains(var7);
               if (var5) {
                  var3[var1] = XRGraphicsConfig.getConfig(this, var7, var8, this.getConfigColormap(var1, this.screen), var9);
               } else {
                  var3[var1] = X11GraphicsConfig.getConfig(this, var7, var8, this.getConfigColormap(var1, this.screen), var9);
               }
            }
         }

         this.configs = var3;
      }

   }

   public native int getNumConfigs(int var1);

   public native int getConfigVisualId(int var1, int var2);

   public native int getConfigDepth(int var1, int var2);

   public native int getConfigColormap(int var1, int var2);

   public static native boolean isDBESupported();

   private void addDoubleBufferVisual(int var1) {
      this.doubleBufferVisuals.add(var1);
   }

   private native void getDoubleBufferVisuals(int var1);

   public GraphicsConfiguration getDefaultConfiguration() {
      if (this.defaultConfig == null) {
         synchronized(this.configLock) {
            this.makeDefaultConfiguration();
         }
      }

      return this.defaultConfig;
   }

   private void makeDefaultConfiguration() {
      if (this.defaultConfig == null) {
         int var1 = this.getConfigVisualId(0, this.screen);
         if (X11GraphicsEnvironment.isGLXAvailable()) {
            this.defaultConfig = GLXGraphicsConfig.getConfig(this, var1);
            if (X11GraphicsEnvironment.isGLXVerbose()) {
               if (this.defaultConfig != null) {
                  System.out.print("OpenGL pipeline enabled");
               } else {
                  System.out.print("Could not enable OpenGL pipeline");
               }

               System.out.println(" for default config on screen " + this.screen);
            }
         }

         if (this.defaultConfig == null) {
            int var2 = this.getConfigDepth(0, this.screen);
            boolean var3 = false;
            if (isDBESupported() && this.doubleBufferVisuals == null) {
               this.doubleBufferVisuals = new HashSet();
               this.getDoubleBufferVisuals(this.screen);
               var3 = this.doubleBufferVisuals.contains(var1);
            }

            if (X11GraphicsEnvironment.isXRenderAvailable()) {
               if (X11GraphicsEnvironment.isXRenderVerbose()) {
                  System.out.println("XRender pipeline enabled");
               }

               this.defaultConfig = XRGraphicsConfig.getConfig(this, var1, var2, this.getConfigColormap(0, this.screen), var3);
            } else {
               this.defaultConfig = X11GraphicsConfig.getConfig(this, var1, var2, this.getConfigColormap(0, this.screen), var3);
            }
         }
      }

   }

   private static native void enterFullScreenExclusive(long var0);

   private static native void exitFullScreenExclusive(long var0);

   private static native boolean initXrandrExtension();

   private static native DisplayMode getCurrentDisplayMode(int var0);

   private static native void enumDisplayModes(int var0, ArrayList<DisplayMode> var1);

   private static native void configDisplayMode(int var0, int var1, int var2, int var3);

   private static native void resetNativeData(int var0);

   private static synchronized boolean isXrandrExtensionSupported() {
      if (xrandrExtSupported == null) {
         xrandrExtSupported = initXrandrExtension();
      }

      return xrandrExtSupported;
   }

   public boolean isFullScreenSupported() {
      boolean var1 = isXrandrExtensionSupported();
      if (var1) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            if (fullScreenExclusivePermission == null) {
               fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
            }

            try {
               var2.checkPermission(fullScreenExclusivePermission);
            } catch (SecurityException var4) {
               return false;
            }
         }
      }

      return var1;
   }

   public boolean isDisplayChangeSupported() {
      return this.isFullScreenSupported() && this.getFullScreenWindow() != null && !((X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).runningXinerama();
   }

   private static void enterFullScreenExclusive(Window var0) {
      X11ComponentPeer var1 = (X11ComponentPeer)var0.getPeer();
      if (var1 != null) {
         enterFullScreenExclusive(var1.getWindow());
         var1.setFullScreenExclusiveModeState(true);
      }

   }

   private static void exitFullScreenExclusive(Window var0) {
      X11ComponentPeer var1 = (X11ComponentPeer)var0.getPeer();
      if (var1 != null) {
         var1.setFullScreenExclusiveModeState(false);
         exitFullScreenExclusive(var1.getWindow());
      }

   }

   public synchronized void setFullScreenWindow(Window var1) {
      Window var2 = this.getFullScreenWindow();
      if (var1 != var2) {
         boolean var3 = this.isFullScreenSupported();
         if (var3 && var2 != null) {
            exitFullScreenExclusive(var2);
            if (this.isDisplayChangeSupported()) {
               this.setDisplayMode(this.origDisplayMode);
            }
         }

         super.setFullScreenWindow(var1);
         if (var3 && var1 != null) {
            if (this.origDisplayMode == null) {
               this.origDisplayMode = this.getDisplayMode();
            }

            enterFullScreenExclusive(var1);
         }

      }
   }

   private DisplayMode getDefaultDisplayMode() {
      GraphicsConfiguration var1 = this.getDefaultConfiguration();
      Rectangle var2 = var1.getBounds();
      return new DisplayMode(var2.width, var2.height, -1, 0);
   }

   public synchronized DisplayMode getDisplayMode() {
      if (this.isFullScreenSupported()) {
         DisplayMode var1 = getCurrentDisplayMode(this.screen);
         if (var1 == null) {
            var1 = this.getDefaultDisplayMode();
         }

         return var1;
      } else {
         if (this.origDisplayMode == null) {
            this.origDisplayMode = this.getDefaultDisplayMode();
         }

         return this.origDisplayMode;
      }
   }

   public synchronized DisplayMode[] getDisplayModes() {
      if (!this.isFullScreenSupported()) {
         return super.getDisplayModes();
      } else {
         ArrayList var1 = new ArrayList();
         enumDisplayModes(this.screen, var1);
         DisplayMode[] var2 = new DisplayMode[var1.size()];
         return (DisplayMode[])var1.toArray(var2);
      }
   }

   public synchronized void setDisplayMode(DisplayMode var1) {
      if (!this.isDisplayChangeSupported()) {
         super.setDisplayMode(var1);
      } else {
         Window var2 = this.getFullScreenWindow();
         if (var2 == null) {
            throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
         } else if (!this.getDisplayMode().equals(var1)) {
            if (var1 != null && (var1 = this.getMatchingDisplayMode(var1)) != null) {
               if (!this.shutdownHookRegistered) {
                  this.shutdownHookRegistered = true;
                  PrivilegedAction var3 = () -> {
                     ThreadGroup var1 = ThreadGroupUtils.getRootThreadGroup();
                     Runnable var2 = () -> {
                        Window var1 = this.getFullScreenWindow();
                        if (var1 != null) {
                           exitFullScreenExclusive(var1);
                           if (this.isDisplayChangeSupported()) {
                              this.setDisplayMode(this.origDisplayMode);
                           }
                        }

                     };
                     Thread var3 = new Thread(var1, var2, "Display-Change-Shutdown-Thread-" + this.screen);
                     var3.setContextClassLoader((ClassLoader)null);
                     Runtime.getRuntime().addShutdownHook(var3);
                     return null;
                  };
                  AccessController.doPrivileged(var3);
               }

               configDisplayMode(this.screen, var1.getWidth(), var1.getHeight(), var1.getRefreshRate());
               var2.setBounds(0, 0, var1.getWidth(), var1.getHeight());
               ((X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
            } else {
               throw new IllegalArgumentException("Invalid display mode");
            }
         }
      }
   }

   private synchronized DisplayMode getMatchingDisplayMode(DisplayMode var1) {
      if (!this.isDisplayChangeSupported()) {
         return null;
      } else {
         DisplayMode[] var2 = this.getDisplayModes();
         DisplayMode[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            DisplayMode var6 = var3[var5];
            if (var1.equals(var6) || var1.getRefreshRate() == 0 && var1.getWidth() == var6.getWidth() && var1.getHeight() == var6.getHeight() && var1.getBitDepth() == var6.getBitDepth()) {
               return var6;
            }
         }

         return null;
      }
   }

   public synchronized void displayChanged() {
      this.topLevels.notifyListeners();
   }

   public void paletteChanged() {
   }

   public void addDisplayChangedListener(DisplayChangedListener var1) {
      this.topLevels.add(var1);
   }

   public void removeDisplayChangedListener(DisplayChangedListener var1) {
      this.topLevels.remove(var1);
   }

   public String toString() {
      return "X11GraphicsDevice[screen=" + this.screen + "]";
   }

   static {
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }
}
