package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawRectANY extends DrawRect {
   XorDrawRectANY() {
      super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      PixelWriter var7 = GeneralRenderer.createXorPixelWriter(var1, var2);
      GeneralRenderer.doDrawRect(var7, var1, var2, var3, var4, var5, var6);
   }
}
