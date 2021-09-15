package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class PixelWriterDrawHandler extends ProcessPath.DrawHandler {
   PixelWriter pw;
   SurfaceData sData;
   Region clip;

   public PixelWriterDrawHandler(SurfaceData var1, PixelWriter var2, Region var3, int var4) {
      super(var3.getLoX(), var3.getLoY(), var3.getHiX(), var3.getHiY(), var4);
      this.sData = var1;
      this.pw = var2;
      this.clip = var3;
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      GeneralRenderer.doDrawLine(this.sData, this.pw, (int[])null, this.clip, var1, var2, var3, var4);
   }

   public void drawPixel(int var1, int var2) {
      GeneralRenderer.doSetRect(this.sData, this.pw, var1, var2, var1 + 1, var2 + 1);
   }

   public void drawScanline(int var1, int var2, int var3) {
      GeneralRenderer.doSetRect(this.sData, this.pw, var1, var3, var2 + 1, var3 + 1);
   }
}
