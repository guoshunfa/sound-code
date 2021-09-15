package sun.java2d.pipe;

import sun.java2d.SunGraphics2D;

public interface PixelDrawPipe {
   void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5);

   void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5);

   void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5);

   void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4);

   void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4);
}
