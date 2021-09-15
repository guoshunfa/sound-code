package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class GlyphListLoopPipe extends GlyphListPipe implements LoopBasedPipe {
   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2, int var3) {
      switch(var3) {
      case 1:
         var1.loops.drawGlyphListLoop.DrawGlyphList(var1, var1.surfaceData, var2);
         return;
      case 2:
         var1.loops.drawGlyphListAALoop.DrawGlyphListAA(var1, var1.surfaceData, var2);
         return;
      case 3:
      case 5:
      default:
         return;
      case 4:
      case 6:
         var1.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(var1, var1.surfaceData, var2);
      }
   }
}
