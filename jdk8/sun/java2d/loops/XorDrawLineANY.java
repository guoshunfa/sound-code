package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawLineANY extends DrawLine {
   XorDrawLineANY() {
      super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawLine(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      PixelWriter var7 = GeneralRenderer.createXorPixelWriter(var1, var2);
      if (var4 >= var6) {
         GeneralRenderer.doDrawLine(var2, var7, (int[])null, var1.getCompClip(), var5, var6, var3, var4);
      } else {
         GeneralRenderer.doDrawLine(var2, var7, (int[])null, var1.getCompClip(), var3, var4, var5, var6);
      }

   }
}
