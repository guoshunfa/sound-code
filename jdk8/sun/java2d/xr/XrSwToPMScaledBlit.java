package sun.java2d.xr;

import java.awt.Composite;
import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class XrSwToPMScaledBlit extends ScaledBlit {
   ScaledBlit pmToSurfaceBlit;

   XrSwToPMScaledBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
      this.pmToSurfaceBlit = new XRPMScaledBlit(var2, var2);
   }

   public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      int var17 = var7 - var5;
      int var18 = var8 - var6;

      try {
         SunToolkit.awtLock();
         XRSurfaceData var19 = XRPMBlitLoops.cacheToTmpSurface(var1, (XRSurfaceData)var2, var17, var18, var5, var6);
         this.pmToSurfaceBlit.Scale(var19, var2, var3, var4, 0, 0, var17, var18, var9, var11, var13, var15);
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
