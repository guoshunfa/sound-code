package sun.font;

import java.awt.Composite;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.FontInfo;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.GlyphListPipe;
import sun.java2d.pipe.Region;
import sun.java2d.x11.X11SurfaceData;

public class X11TextRenderer extends GlyphListPipe {
   public void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      FontRenderContext var5 = var2.getFontRenderContext();
      FontInfo var6 = var1.getGVFontInfo(var2.getFont(), var5);
      SurfaceData var10000;
      switch(var6.aaHint) {
      case 1:
         super.drawGlyphVector(var1, var2, var3, var4);
         return;
      case 2:
         var10000 = var1.surfaceData;
         SurfaceData.aaTextRenderer.drawGlyphVector(var1, var2, var3, var4);
         return;
      case 3:
      case 5:
      default:
         return;
      case 4:
      case 6:
         var10000 = var1.surfaceData;
         SurfaceData.lcdTextRenderer.drawGlyphVector(var1, var2, var3, var4);
      }
   }

   native void doDrawGlyphList(long var1, long var3, Region var5, GlyphList var6);

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      SunToolkit.awtLock();

      try {
         X11SurfaceData var3 = (X11SurfaceData)var1.surfaceData;
         Region var4 = var1.getCompClip();
         long var5 = var3.getRenderGC(var4, 0, (Composite)null, var1.pixel);
         this.doDrawGlyphList(var3.getNativeOps(), var5, var4, var2);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public X11TextRenderer traceWrap() {
      return new X11TextRenderer.Tracer();
   }

   public static class Tracer extends X11TextRenderer {
      void doDrawGlyphList(long var1, long var3, Region var5, GlyphList var6) {
         GraphicsPrimitive.tracePrimitive("X11DrawGlyphs");
         super.doDrawGlyphList(var1, var3, var5, var6);
      }
   }
}
