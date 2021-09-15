package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

class OGLSurfaceToSurfaceTransform extends TransformBlit {
   OGLSurfaceToSurfaceTransform() {
      super(OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
   }

   public void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      OGLBlitLoops.IsoBlit(var1, var2, (BufferedImage)null, (BufferedImageOp)null, var3, var4, var5, var6, var7, var8, var7 + var11, var8 + var12, (double)var9, (double)var10, (double)(var9 + var11), (double)(var10 + var12), false);
   }
}
