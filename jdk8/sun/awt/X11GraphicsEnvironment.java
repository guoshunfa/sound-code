package sun.awt;

import java.awt.AWTError;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;
import sun.java2d.UnixSurfaceManagerFactory;
import sun.java2d.xr.XRSurfaceData;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class X11GraphicsEnvironment extends SunGraphicsEnvironment {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11GraphicsEnvironment");
   private static final PlatformLogger screenLog = PlatformLogger.getLogger("sun.awt.screen.X11GraphicsEnvironment");
   private static Boolean xinerState;
   private static boolean glxAvailable;
   private static boolean glxVerbose;
   private static boolean xRenderVerbose;
   private static boolean xRenderAvailable;
   private Boolean isDisplayLocal;

   private static native boolean initGLX();

   public static boolean isGLXAvailable() {
      return glxAvailable;
   }

   public static boolean isGLXVerbose() {
      return glxVerbose;
   }

   private static native boolean initXRender(boolean var0, boolean var1);

   public static boolean isXRenderAvailable() {
      return xRenderAvailable;
   }

   public static boolean isXRenderVerbose() {
      return xRenderVerbose;
   }

   private static native int checkShmExt();

   private static native String getDisplayString();

   private static native void initDisplay(boolean var0);

   protected native int getNumScreens();

   protected GraphicsDevice makeScreenDevice(int var1) {
      return new X11GraphicsDevice(var1);
   }

   protected native int getDefaultScreenNum();

   public GraphicsDevice getDefaultScreenDevice() {
      GraphicsDevice[] var1 = this.getScreenDevices();
      if (var1.length == 0) {
         throw new AWTError("no screen devices");
      } else {
         int var2 = this.getDefaultScreenNum();
         return var1[0 < var2 && var2 < var1.length ? var2 : 0];
      }
   }

   public boolean isDisplayLocal() {
      if (this.isDisplayLocal == null) {
         SunToolkit.awtLock();

         try {
            if (this.isDisplayLocal == null) {
               this.isDisplayLocal = _isDisplayLocal();
            }
         } finally {
            SunToolkit.awtUnlock();
         }
      }

      return this.isDisplayLocal;
   }

   private static boolean _isDisplayLocal() {
      if (isHeadless()) {
         return true;
      } else {
         String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.remote")));
         if (var0 != null) {
            return var0.equals("false");
         } else {
            int var1 = checkShmExt();
            if (var1 != -1) {
               return var1 == 1;
            } else {
               String var2 = getDisplayString();
               int var3 = var2.indexOf(58);
               final String var4 = var2.substring(0, var3);
               if (var3 <= 0) {
                  return true;
               } else {
                  Boolean var5 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        InetAddress[] var1 = null;
                        Enumeration var2 = null;
                        Enumeration var3 = null;

                        try {
                           var3 = NetworkInterface.getNetworkInterfaces();
                           var1 = InetAddress.getAllByName(var4);
                           if (var1 == null) {
                              return Boolean.FALSE;
                           }
                        } catch (UnknownHostException var5) {
                           System.err.println("Unknown host: " + var4);
                           return Boolean.FALSE;
                        } catch (SocketException var6) {
                           System.err.println(var6.getMessage());
                           return Boolean.FALSE;
                        }

                        while(var3.hasMoreElements()) {
                           var2 = ((NetworkInterface)var3.nextElement()).getInetAddresses();

                           while(var2.hasMoreElements()) {
                              for(int var4x = 0; var4x < var1.length; ++var4x) {
                                 if (var2.nextElement().equals(var1[var4x])) {
                                    return Boolean.TRUE;
                                 }
                              }
                           }
                        }

                        return Boolean.FALSE;
                     }
                  });
                  return var5;
               }
            }
         }
      }
   }

   public String getDefaultFontFaceName() {
      return null;
   }

   private static native boolean pRunningXinerama();

   private static native Point getXineramaCenterPoint();

   public Point getCenterPoint() {
      if (this.runningXinerama()) {
         Point var1 = getXineramaCenterPoint();
         if (var1 != null) {
            return var1;
         }
      }

      return super.getCenterPoint();
   }

   public Rectangle getMaximumWindowBounds() {
      return this.runningXinerama() ? this.getXineramaWindowBounds() : super.getMaximumWindowBounds();
   }

   public boolean runningXinerama() {
      if (xinerState == null) {
         xinerState = pRunningXinerama();
         if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
            screenLog.finer("Running Xinerama: " + xinerState);
         }
      }

      return xinerState;
   }

   protected Rectangle getXineramaWindowBounds() {
      Point var1 = this.getCenterPoint();
      GraphicsDevice[] var4 = this.getScreenDevices();
      Rectangle var5 = null;
      Rectangle var2 = getUsableBounds(var4[0]);

      for(int var6 = 0; var6 < var4.length; ++var6) {
         Rectangle var3 = getUsableBounds(var4[var6]);
         if (var5 == null && var3.width / 2 + var3.x > var1.x - 1 && var3.height / 2 + var3.y > var1.y - 1 && var3.width / 2 + var3.x < var1.x + 1 && var3.height / 2 + var3.y < var1.y + 1) {
            var5 = var3;
         }

         var2 = var2.union(var3);
      }

      if (var2.width / 2 + var2.x > var1.x - 1 && var2.height / 2 + var2.y > var1.y - 1 && var2.width / 2 + var2.x < var1.x + 1 && var2.height / 2 + var2.y < var1.y + 1) {
         if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
            screenLog.finer("Video Wall: center point is at center of all displays.");
         }

         return var2;
      } else if (var5 != null) {
         if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
            screenLog.finer("Center point at center of a particular monitor, but not of the entire virtual display.");
         }

         return var5;
      } else {
         if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
            screenLog.finer("Center point is somewhere strange - return union of all bounds.");
         }

         return var2;
      }
   }

   public void paletteChanged() {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            System.loadLibrary("awt");
            if (!GraphicsEnvironment.isHeadless()) {
               boolean var1 = false;
               String var2 = System.getProperty("sun.java2d.opengl");
               if (var2 != null) {
                  if (!var2.equals("true") && !var2.equals("t")) {
                     if (var2.equals("True") || var2.equals("T")) {
                        var1 = true;
                        X11GraphicsEnvironment.glxVerbose = true;
                     }
                  } else {
                     var1 = true;
                  }
               }

               boolean var3 = true;
               boolean var4 = false;
               String var5 = System.getProperty("sun.java2d.xrender");
               if (var5 != null) {
                  if (!var5.equals("false") && !var5.equals("f")) {
                     if (var5.equals("True") || var5.equals("T")) {
                        var3 = true;
                        X11GraphicsEnvironment.xRenderVerbose = true;
                     }
                  } else {
                     var3 = false;
                  }

                  if (var5.equalsIgnoreCase("t") || var5.equalsIgnoreCase("true")) {
                     var4 = true;
                  }
               }

               X11GraphicsEnvironment.initDisplay(var1);
               if (var1) {
                  X11GraphicsEnvironment.glxAvailable = X11GraphicsEnvironment.initGLX();
                  if (X11GraphicsEnvironment.glxVerbose && !X11GraphicsEnvironment.glxAvailable) {
                     System.out.println("Could not enable OpenGL pipeline (GLX 1.3 not available)");
                  }
               }

               if (var3) {
                  X11GraphicsEnvironment.xRenderAvailable = X11GraphicsEnvironment.initXRender(X11GraphicsEnvironment.xRenderVerbose, var4);
                  if (X11GraphicsEnvironment.xRenderVerbose && !X11GraphicsEnvironment.xRenderAvailable) {
                     System.out.println("Could not enable XRender pipeline");
                  }
               }

               if (X11GraphicsEnvironment.xRenderAvailable) {
                  XRSurfaceData.initXRSurfaceData();
               }
            }

            return null;
         }
      });
      SurfaceManagerFactory.setInstance(new UnixSurfaceManagerFactory());
   }
}
