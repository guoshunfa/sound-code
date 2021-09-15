package sun.java2d.xr;

import java.awt.Composite;
import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class XrSwToPMBlit extends Blit {
   Blit pmToSurfaceBlit;

   XrSwToPMBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
      this.pmToSurfaceBlit = new XRPMBlit(var2, var2);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      if (CompositeType.SrcOverNoEa.equals(var3) && var1.getTransparency() == 1) {
         Blit var15 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, var2.getSurfaceType());
         var15.Blit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else {
         try {
            SunToolkit.awtLock();
            XRSurfaceData var11 = XRPMBlitLoops.cacheToTmpSurface(var1, (XRSurfaceData)var2, var9, var10, var5, var6);
            this.pmToSurfaceBlit.Blit(var11, var2, var3, var4, 0, 0, var7, var8, var9, var10);
         } finally {
            SunToolkit.awtUnlock();
         }
      }

   }
}
