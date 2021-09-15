package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class AlphaColorPipe implements CompositePipe, ParallelogramPipe {
   public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
      return var1;
   }

   public boolean needTile(Object var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   public void renderPathTile(Object var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      SunGraphics2D var9 = (SunGraphics2D)var1;
      var9.alphafill.MaskFill(var9, var9.getSurfaceData(), var9.composite, var5, var6, var7, var8, var2, var3, var4);
   }

   public void skipTile(Object var1, int var2, int var3) {
   }

   public void endSequence(Object var1) {
   }

   public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      var1.alphafill.FillAAPgram(var1, var1.getSurfaceData(), var1.composite, var10, var12, var14, var16, var18, var20);
   }

   public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
      var1.alphafill.DrawAAPgram(var1, var1.getSurfaceData(), var1.composite, var10, var12, var14, var16, var18, var20, var22, var24);
   }
}
