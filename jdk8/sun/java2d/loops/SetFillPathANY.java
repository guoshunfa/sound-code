package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class SetFillPathANY extends FillPath {
   SetFillPathANY() {
      super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
   }

   public void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Path2D.Float var5) {
      PixelWriter var6 = GeneralRenderer.createSolidPixelWriter(var1, var2);
      ProcessPath.fillPath(new PixelWriterDrawHandler(var2, var6, var1.getCompClip(), var1.strokeHint), var5, var3, var4);
   }
}
