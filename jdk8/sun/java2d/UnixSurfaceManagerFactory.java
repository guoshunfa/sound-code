package sun.java2d;

import java.awt.GraphicsConfiguration;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.opengl.GLXGraphicsConfig;
import sun.java2d.opengl.GLXVolatileSurfaceManager;
import sun.java2d.x11.X11VolatileSurfaceManager;
import sun.java2d.xr.XRGraphicsConfig;
import sun.java2d.xr.XRVolatileSurfaceManager;

public class UnixSurfaceManagerFactory extends SurfaceManagerFactory {
   public VolatileSurfaceManager createVolatileManager(SunVolatileImage var1, Object var2) {
      GraphicsConfiguration var3 = var1.getGraphicsConfig();
      if (var3 instanceof GLXGraphicsConfig) {
         return new GLXVolatileSurfaceManager(var1, var2);
      } else {
         return (VolatileSurfaceManager)(var3 instanceof XRGraphicsConfig ? new XRVolatileSurfaceManager(var1, var2) : new X11VolatileSurfaceManager(var1, var2));
      }
   }
}
