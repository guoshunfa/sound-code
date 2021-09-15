package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

final class OGLGeneralTransformedBlit extends TransformBlit {
   private final TransformBlit performop;
   private WeakReference<SurfaceData> srcTmp;

   OGLGeneralTransformedBlit(TransformBlit var1) {
      super(SurfaceType.Any, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
      this.performop = var1;
   }

   public synchronized void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      Blit var13 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
      SurfaceData var14 = this.srcTmp != null ? (SurfaceData)this.srcTmp.get() : null;
      var1 = convertFrom(var13, var1, var7, var8, var11, var12, var14, 3);
      this.performop.Transform(var1, var2, var3, var4, var5, var6, 0, 0, var9, var10, var11, var12);
      if (var1 != var14) {
         this.srcTmp = new WeakReference(var1);
      }

   }
}
