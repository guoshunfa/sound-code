package sun.java2d.pipe;

import java.awt.font.GlyphVector;
import sun.java2d.SunGraphics2D;

public interface TextPipe {
   void drawString(SunGraphics2D var1, String var2, double var3, double var5);

   void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4);

   void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6);
}
