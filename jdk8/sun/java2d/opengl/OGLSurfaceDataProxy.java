package sun.java2d.opengl;

import java.awt.Color;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public class OGLSurfaceDataProxy extends SurfaceDataProxy {
   OGLGraphicsConfig oglgc;
   int transparency;

   public static SurfaceDataProxy createProxy(SurfaceData var0, OGLGraphicsConfig var1) {
      return (SurfaceDataProxy)(var0 instanceof OGLSurfaceData ? UNCACHED : new OGLSurfaceDataProxy(var1, var0.getTransparency()));
   }

   public OGLSurfaceDataProxy(OGLGraphicsConfig var1, int var2) {
      this.oglgc = var1;
      this.transparency = var2;
   }

   public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      if (var2 == null) {
         try {
            var2 = this.oglgc.createManagedSurface(var3, var4, this.transparency);
         } catch (OutOfMemoryError var6) {
            return null;
         }
      }

      return var2;
   }

   public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
      return var3.isDerivedFrom(CompositeType.AnyAlpha) && (var4 == null || this.transparency == 1);
   }
}
