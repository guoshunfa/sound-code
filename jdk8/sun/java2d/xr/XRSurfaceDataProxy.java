package sun.java2d.xr;

import java.awt.Color;
import java.awt.Image;
import java.awt.Transparency;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public class XRSurfaceDataProxy extends SurfaceDataProxy implements Transparency {
   XRGraphicsConfig xrgc;
   int transparency;

   public static SurfaceDataProxy createProxy(SurfaceData var0, XRGraphicsConfig var1) {
      return (SurfaceDataProxy)(var0 instanceof XRSurfaceData ? UNCACHED : new XRSurfaceDataProxy(var1, var0.getTransparency()));
   }

   public XRSurfaceDataProxy(XRGraphicsConfig var1) {
      this.xrgc = var1;
   }

   public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      if (var2 == null) {
         try {
            var2 = XRSurfaceData.createData(this.xrgc, var3, var4, this.xrgc.getColorModel(), (Image)null, 0L, this.getTransparency());
         } catch (OutOfMemoryError var6) {
         }
      }

      return (SurfaceData)var2;
   }

   public XRSurfaceDataProxy(XRGraphicsConfig var1, int var2) {
      this.xrgc = var1;
      this.transparency = var2;
   }

   public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
      return var4 == null || this.transparency == 3;
   }

   public int getTransparency() {
      return this.transparency;
   }
}
