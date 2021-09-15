package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class LCDTextRenderer extends GlyphListLoopPipe {
   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      var1.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(var1, var1.surfaceData, var2);
   }
}
