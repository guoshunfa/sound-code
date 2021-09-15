package sun.java2d.pipe;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import sun.java2d.SunGraphics2D;

public class OutlineTextRenderer implements TextPipe {
   public static final int THRESHHOLD = 100;

   public void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6) {
      String var7 = new String(var2, var3, var4);
      this.drawString(var1, var7, (double)var5, (double)var6);
   }

   public void drawString(SunGraphics2D var1, String var2, double var3, double var5) {
      if (!"".equals(var2)) {
         TextLayout var7 = new TextLayout(var2, var1.getFont(), var1.getFontRenderContext());
         Shape var8 = var7.getOutline(AffineTransform.getTranslateInstance(var3, var5));
         int var9 = var1.getFontInfo().aaHint;
         int var10 = -1;
         if (var9 != 1 && var1.antialiasHint != 2) {
            var10 = var1.antialiasHint;
            var1.antialiasHint = 2;
            var1.validatePipe();
         } else if (var9 == 1 && var1.antialiasHint != 1) {
            var10 = var1.antialiasHint;
            var1.antialiasHint = 1;
            var1.validatePipe();
         }

         var1.fill(var8);
         if (var10 != -1) {
            var1.antialiasHint = var10;
            var1.validatePipe();
         }

      }
   }

   public void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      Shape var5 = var2.getOutline(var3, var4);
      int var6 = -1;
      FontRenderContext var7 = var2.getFontRenderContext();
      boolean var8 = var7.isAntiAliased();
      if (var8 && var1.getGVFontInfo(var2.getFont(), var7).aaHint == 1) {
         var8 = false;
      }

      if (var8 && var1.antialiasHint != 2) {
         var6 = var1.antialiasHint;
         var1.antialiasHint = 2;
         var1.validatePipe();
      } else if (!var8 && var1.antialiasHint != 1) {
         var6 = var1.antialiasHint;
         var1.antialiasHint = 1;
         var1.validatePipe();
      }

      var1.fill(var5);
      if (var6 != -1) {
         var1.antialiasHint = var6;
         var1.validatePipe();
      }

   }
}
