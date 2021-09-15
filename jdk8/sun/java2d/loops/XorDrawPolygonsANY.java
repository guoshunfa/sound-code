package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class XorDrawPolygonsANY extends DrawPolygons {
   XorDrawPolygonsANY() {
      super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawPolygons(SunGraphics2D var1, SurfaceData var2, int[] var3, int[] var4, int[] var5, int var6, int var7, int var8, boolean var9) {
      PixelWriter var10 = GeneralRenderer.createXorPixelWriter(var1, var2);
      int var11 = 0;
      Region var12 = var1.getCompClip();

      for(int var13 = 0; var13 < var6; ++var13) {
         int var14 = var5[var13];
         GeneralRenderer.doDrawPoly(var2, var10, var3, var4, var11, var14, var12, var7, var8, var9);
         var11 += var14;
      }

   }
}
