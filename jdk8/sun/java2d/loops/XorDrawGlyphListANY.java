package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

class XorDrawGlyphListANY extends DrawGlyphList {
   XorDrawGlyphListANY() {
      super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
      PixelWriter var4 = GeneralRenderer.createXorPixelWriter(var1, var2);
      GeneralRenderer.doDrawGlyphList(var2, var4, var3, var1.getCompClip());
   }
}
