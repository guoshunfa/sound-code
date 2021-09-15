package sun.java2d.pipe;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.FontInfo;

public abstract class GlyphListPipe implements TextPipe {
   public void drawString(SunGraphics2D var1, String var2, double var3, double var5) {
      FontInfo var7 = var1.getFontInfo();
      if (var7.pixelHeight > 100) {
         SurfaceData.outlineTextRenderer.drawString(var1, var2, var3, var5);
      } else {
         float var8;
         float var9;
         if (var1.transformState >= 3) {
            double[] var10 = new double[]{var3 + (double)var7.originX, var5 + (double)var7.originY};
            var1.transform.transform((double[])var10, 0, (double[])var10, 0, 1);
            var8 = (float)var10[0];
            var9 = (float)var10[1];
         } else {
            var8 = (float)(var3 + (double)var7.originX + (double)var1.transX);
            var9 = (float)(var5 + (double)var7.originY + (double)var1.transY);
         }

         GlyphList var12 = GlyphList.getInstance();
         if (var12.setFromString(var7, var2, var8, var9)) {
            this.drawGlyphList(var1, var12);
            var12.dispose();
         } else {
            var12.dispose();
            TextLayout var11 = new TextLayout(var2, var1.getFont(), var1.getFontRenderContext());
            var11.draw(var1, (float)var3, (float)var5);
         }

      }
   }

   public void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6) {
      FontInfo var7 = var1.getFontInfo();
      if (var7.pixelHeight > 100) {
         SurfaceData.outlineTextRenderer.drawChars(var1, var2, var3, var4, var5, var6);
      } else {
         float var8;
         float var9;
         if (var1.transformState >= 3) {
            double[] var10 = new double[]{(double)((float)var5 + var7.originX), (double)((float)var6 + var7.originY)};
            var1.transform.transform((double[])var10, 0, (double[])var10, 0, 1);
            var8 = (float)var10[0];
            var9 = (float)var10[1];
         } else {
            var8 = (float)var5 + var7.originX + (float)var1.transX;
            var9 = (float)var6 + var7.originY + (float)var1.transY;
         }

         GlyphList var12 = GlyphList.getInstance();
         if (var12.setFromChars(var7, var2, var3, var4, var8, var9)) {
            this.drawGlyphList(var1, var12);
            var12.dispose();
         } else {
            var12.dispose();
            TextLayout var11 = new TextLayout(new String(var2, var3, var4), var1.getFont(), var1.getFontRenderContext());
            var11.draw(var1, (float)var5, (float)var6);
         }

      }
   }

   public void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      FontRenderContext var5 = var2.getFontRenderContext();
      FontInfo var6 = var1.getGVFontInfo(var2.getFont(), var5);
      if (var6.pixelHeight > 100) {
         SurfaceData.outlineTextRenderer.drawGlyphVector(var1, var2, var3, var4);
      } else {
         if (var1.transformState >= 3) {
            double[] var7 = new double[]{(double)var3, (double)var4};
            var1.transform.transform((double[])var7, 0, (double[])var7, 0, 1);
            var3 = (float)var7[0];
            var4 = (float)var7[1];
         } else {
            var3 += (float)var1.transX;
            var4 += (float)var1.transY;
         }

         GlyphList var8 = GlyphList.getInstance();
         var8.setFromGlyphVector(var6, var2, var3, var4);
         this.drawGlyphList(var1, var8, var6.aaHint);
         var8.dispose();
      }
   }

   protected abstract void drawGlyphList(SunGraphics2D var1, GlyphList var2);

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2, int var3) {
      this.drawGlyphList(var1, var2);
   }
}
