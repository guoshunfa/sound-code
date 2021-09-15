package sun.java2d.x11;

import java.awt.image.ColorModel;
import sun.awt.X11ComponentPeer;
import sun.awt.X11GraphicsConfig;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public abstract class XSurfaceData extends SurfaceData {
   static boolean isX11SurfaceDataInitialized = false;

   public static boolean isX11SurfaceDataInitialized() {
      return isX11SurfaceDataInitialized;
   }

   public static void setX11SurfaceDataInitialized() {
      isX11SurfaceDataInitialized = true;
   }

   public XSurfaceData(SurfaceType var1, ColorModel var2) {
      super(var1, var2);
   }

   protected native void initOps(X11ComponentPeer var1, X11GraphicsConfig var2, int var3);

   protected static native long XCreateGC(long var0);

   protected static native void XResetClip(long var0);

   protected static native void XSetClip(long var0, int var2, int var3, int var4, int var5, Region var6);

   protected native void flushNativeSurface();

   protected native boolean isDrawableValid();

   protected native void setInvalid();

   protected static native void XSetGraphicsExposures(long var0, boolean var2);
}
