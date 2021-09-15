package sun.java2d.xr;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class XRPMScaledBlit extends ScaledBlit {
   public XRPMScaledBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
   }

   public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      try {
         SunToolkit.awtLock();
         XRSurfaceData var17 = (XRSurfaceData)var2;
         var17.validateAsDestination((SunGraphics2D)null, var4);
         XRSurfaceData var18 = (XRSurfaceData)var1;
         var17.maskBuffer.validateCompositeState(var3, (AffineTransform)null, (Paint)null, (SunGraphics2D)null);
         double var19 = (var13 - var9) / (double)(var7 - var5);
         double var21 = (var15 - var11) / (double)(var8 - var6);
         var5 = (int)((double)var5 * var19);
         int var10000 = (int)((double)var7 * var19);
         var6 = (int)((double)var6 * var21);
         var10000 = (int)((double)var8 * var21);
         var9 = Math.ceil(var9 - 0.5D);
         var11 = Math.ceil(var11 - 0.5D);
         var13 = Math.ceil(var13 - 0.5D);
         var15 = Math.ceil(var15 - 0.5D);
         AffineTransform var23 = AffineTransform.getScaleInstance(1.0D / var19, 1.0D / var21);
         var18.validateAsSource(var23, 0, 0);
         var17.maskBuffer.compositeBlit(var18, var17, var5, var6, (int)var9, (int)var11, (int)(var13 - var9), (int)(var15 - var11));
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
