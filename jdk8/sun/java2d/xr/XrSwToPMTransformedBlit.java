package sun.java2d.xr;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

class XrSwToPMTransformedBlit extends TransformBlit {
   TransformBlit pmToSurfaceBlit;

   XrSwToPMTransformedBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
      this.pmToSurfaceBlit = new XRPMTransformedBlit(var2, var2);
   }

   public void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      try {
         SunToolkit.awtLock();
         XRSurfaceData var13 = XRPMBlitLoops.cacheToTmpSurface(var1, (XRSurfaceData)var2, var11, var12, var7, var8);
         this.pmToSurfaceBlit.Transform(var13, var2, var3, var4, var5, var6, 0, 0, var9, var10, var11, var12);
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
