package sun.java2d.xr;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.DrawImage;

public class XRDrawImage extends DrawImage {
   protected void renderImageXform(SunGraphics2D var1, Image var2, AffineTransform var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      SurfaceData var10 = var1.surfaceData;
      SurfaceData var11 = var10.getSourceSurfaceData(var2, 4, var1.imageComp, var9);
      if (var1.composite instanceof AlphaComposite) {
         int var12 = ((AlphaComposite)var1.composite).getRule();
         float var13 = ((AlphaComposite)var1.composite).getAlpha();
         if (var11 != null && !isBgOperation(var11, var9) && var4 <= 2 && (XRUtils.isMaskEvaluated(XRUtils.j2dAlphaCompToXR(var12)) || XRUtils.isTransformQuadrantRotated(var3) && var13 == 1.0F)) {
            SurfaceType var14 = var11.getSurfaceType();
            SurfaceType var15 = var10.getSurfaceType();
            TransformBlit var16 = TransformBlit.getFromCache(var14, var1.imageComp, var15);
            if (var16 != null) {
               var16.Transform(var11, var10, var1.composite, var1.getCompClip(), var3, var4, var5, var6, 0, 0, var7 - var5, var8 - var6);
               return;
            }
         }
      }

      super.renderImageXform(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
