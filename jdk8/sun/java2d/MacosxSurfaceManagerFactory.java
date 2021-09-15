package sun.java2d;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.opengl.CGLVolatileSurfaceManager;

public class MacosxSurfaceManagerFactory extends SurfaceManagerFactory {
   public VolatileSurfaceManager createVolatileManager(SunVolatileImage var1, Object var2) {
      return new CGLVolatileSurfaceManager(var1, var2);
   }
}
