package sun.lwawt.macosx;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import sun.awt.SunHints;
import sun.font.CStrike;
import sun.font.FontStrike;
import sun.java2d.OSXSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.TextPipe;

public class CTextPipe implements TextPipe {
   public native void doDrawString(SurfaceData var1, long var2, String var4, double var5, double var7);

   public native void doDrawGlyphs(SurfaceData var1, long var2, GlyphVector var4, float var5, float var6);

   public native void doUnicodes(SurfaceData var1, long var2, char[] var4, int var5, int var6, float var7, float var8);

   public native void doOneUnicode(SurfaceData var1, long var2, char var4, float var5, float var6);

   long getNativeStrikePtr(SunGraphics2D var1) {
      FontStrike var2 = var1.getFontInfo().fontStrike;
      return !(var2 instanceof CStrike) ? 0L : ((CStrike)var2).getNativeStrikePtr();
   }

   void drawGlyphVectorAsShape(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      int var5 = var2.getNumGlyphs();

      for(int var6 = 0; var6 < var5; ++var6) {
         Shape var7 = var2.getGlyphOutline(var6, var3, var4);
         var1.fill(var7);
      }

   }

   void drawTextAsShape(SunGraphics2D var1, String var2, double var3, double var5) {
      Object var7 = var1.getRenderingHint(SunHints.KEY_ANTIALIASING);
      FontRenderContext var8 = var1.getFontRenderContext();
      var1.setRenderingHint(SunHints.KEY_ANTIALIASING, var8.isAntiAliased() ? SunHints.VALUE_ANTIALIAS_ON : SunHints.VALUE_ANTIALIAS_OFF);
      Font var9 = var1.getFont();
      GlyphVector var10 = var9.createGlyphVector(var8, var2);
      int var11 = var10.getNumGlyphs();

      for(int var12 = 0; var12 < var11; ++var12) {
         Shape var13 = var10.getGlyphOutline(var12, (float)var3, (float)var5);
         var1.fill(var13);
      }

      var1.setRenderingHint(SunHints.KEY_ANTIALIASING, var7);
   }

   public void drawString(SunGraphics2D var1, String var2, double var3, double var5) {
      long var7 = this.getNativeStrikePtr(var1);
      if (OSXSurfaceData.IsSimpleColor(var1.paint) && var7 != 0L) {
         OSXSurfaceData var9 = (OSXSurfaceData)var1.getSurfaceData();
         var9.drawString(this, var1, var7, var2, var3, var5);
      } else {
         this.drawTextAsShape(var1, var2, var3, var5);
      }

   }

   public void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      Font var5 = var1.getFont();
      var1.setFont(var2.getFont());
      long var6 = this.getNativeStrikePtr(var1);
      if (OSXSurfaceData.IsSimpleColor(var1.paint) && var6 != 0L) {
         OSXSurfaceData var8 = (OSXSurfaceData)var1.getSurfaceData();
         var8.drawGlyphs(this, var1, var6, var2, var3, var4);
      } else {
         this.drawGlyphVectorAsShape(var1, var2, var3, var4);
      }

      var1.setFont(var5);
   }

   public void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6) {
      long var7 = this.getNativeStrikePtr(var1);
      if (OSXSurfaceData.IsSimpleColor(var1.paint) && var7 != 0L) {
         OSXSurfaceData var9 = (OSXSurfaceData)var1.getSurfaceData();
         var9.drawUnicodes(this, var1, var7, var2, var3, var4, (float)var5, (float)var6);
      } else {
         this.drawTextAsShape(var1, new String(var2, var3, var4), (double)var5, (double)var6);
      }

   }

   public CTextPipe traceWrap() {
      return new CTextPipe.Tracer();
   }

   public static class Tracer extends CTextPipe {
      void doDrawString(SurfaceData var1, long var2, String var4, float var5, float var6) {
         GraphicsPrimitive.tracePrimitive("QuartzDrawString");
         super.doDrawString(var1, var2, var4, (double)var5, (double)var6);
      }

      public void doDrawGlyphs(SurfaceData var1, long var2, GlyphVector var4, float var5, float var6) {
         GraphicsPrimitive.tracePrimitive("QuartzDrawGlyphs");
         super.doDrawGlyphs(var1, var2, var4, var5, var6);
      }

      public void doUnicodes(SurfaceData var1, long var2, char[] var4, int var5, int var6, float var7, float var8) {
         GraphicsPrimitive.tracePrimitive("QuartzDrawUnicodes");
         super.doUnicodes(var1, var2, var4, var5, var6, var7, var8);
      }

      public void doOneUnicode(SurfaceData var1, long var2, char var4, float var5, float var6) {
         GraphicsPrimitive.tracePrimitive("QuartzDrawUnicode");
         super.doOneUnicode(var1, var2, var4, var5, var6);
      }
   }
}
