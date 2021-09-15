package sun.awt.image;

import sun.java2d.SurfaceData;

public class BufImgVolatileSurfaceManager extends VolatileSurfaceManager {
   public BufImgVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
   }

   protected boolean isAccelerationEnabled() {
      return false;
   }

   protected SurfaceData initAcceleratedSurface() {
      return null;
   }
}
