package sun.java2d.xr;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class XRPMBlit extends Blit {
   public XRPMBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      try {
         SunToolkit.awtLock();
         XRSurfaceData var11 = (XRSurfaceData)var2;
         var11.validateAsDestination((SunGraphics2D)null, var4);
         XRSurfaceData var12 = (XRSurfaceData)var1;
         var12.validateAsSource((AffineTransform)null, 0, 0);
         var11.maskBuffer.validateCompositeState(var3, (AffineTransform)null, (Paint)null, (SunGraphics2D)null);
         var11.maskBuffer.compositeBlit(var12, var11, var5, var6, var7, var8, var9, var10);
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
