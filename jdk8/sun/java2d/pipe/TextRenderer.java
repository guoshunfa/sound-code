package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class TextRenderer extends GlyphListPipe {
   CompositePipe outpipe;

   public TextRenderer(CompositePipe var1) {
      this.outpipe = var1;
   }

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      int var3 = var2.getNumGlyphs();
      Region var4 = var1.getCompClip();
      int var5 = var4.getLoX();
      int var6 = var4.getLoY();
      int var7 = var4.getHiX();
      int var8 = var4.getHiY();
      Object var9 = null;

      try {
         int[] var10 = var2.getBounds();
         Rectangle var11 = new Rectangle(var10[0], var10[1], var10[2] - var10[0], var10[3] - var10[1]);
         Shape var12 = var1.untransformShape(var11);
         var9 = this.outpipe.startSequence(var1, var12, var11, var10);

         for(int var13 = 0; var13 < var3; ++var13) {
            var2.setGlyphIndex(var13);
            int[] var14 = var2.getMetrics();
            int var15 = var14[0];
            int var16 = var14[1];
            int var17 = var14[2];
            int var18 = var15 + var17;
            int var19 = var16 + var14[3];
            int var20 = 0;
            if (var15 < var5) {
               var20 = var5 - var15;
               var15 = var5;
            }

            if (var16 < var6) {
               var20 += (var6 - var16) * var17;
               var16 = var6;
            }

            if (var18 > var7) {
               var18 = var7;
            }

            if (var19 > var8) {
               var19 = var8;
            }

            if (var18 > var15 && var19 > var16 && this.outpipe.needTile(var9, var15, var16, var18 - var15, var19 - var16)) {
               byte[] var21 = var2.getGrayBits();
               this.outpipe.renderPathTile(var9, var21, var20, var17, var15, var16, var18 - var15, var19 - var16);
            } else {
               this.outpipe.skipTile(var9, var15, var16);
            }
         }
      } finally {
         if (var9 != null) {
            this.outpipe.endSequence(var9);
         }

      }

   }
}
