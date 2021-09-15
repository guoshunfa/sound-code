package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

class SetFillSpansANY extends FillSpans {
   SetFillSpansANY() {
      super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
   }

   public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
      PixelWriter var4 = GeneralRenderer.createSolidPixelWriter(var1, var2);
      int[] var5 = new int[4];

      while(var3.nextSpan(var5)) {
         GeneralRenderer.doSetRect(var2, var4, var5[0], var5[1], var5[2], var5[3]);
      }

   }
}
