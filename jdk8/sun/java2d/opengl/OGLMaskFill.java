package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedMaskFill;

class OGLMaskFill extends BufferedMaskFill {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new OGLMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa)};
      GraphicsPrimitiveMgr.register(var0);
   }

   protected OGLMaskFill(SurfaceType var1, CompositeType var2) {
      super(OGLRenderQueue.getInstance(), var1, var2, OGLSurfaceData.OpenGLSurface);
   }

   protected native void maskFill(int var1, int var2, int var3, int var4, int var5, int var6, int var7, byte[] var8);

   protected void validateContext(SunGraphics2D var1, Composite var2, int var3) {
      OGLSurfaceData var4;
      try {
         var4 = (OGLSurfaceData)var1.surfaceData;
      } catch (ClassCastException var6) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      OGLContext.validateContext(var4, var4, var1.getCompClip(), var2, (AffineTransform)null, var1.paint, var1, var3);
   }
}
