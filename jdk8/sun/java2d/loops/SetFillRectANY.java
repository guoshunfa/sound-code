package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class SetFillRectANY extends FillRect {
   SetFillRectANY() {
      super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
   }

   public void FillRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      PixelWriter var7 = GeneralRenderer.createSolidPixelWriter(var1, var2);
      Region var8 = var1.getCompClip().getBoundsIntersectionXYWH(var3, var4, var5, var6);
      GeneralRenderer.doSetRect(var2, var7, var8.getLoX(), var8.getLoY(), var8.getHiX(), var8.getHiY());
   }
}
