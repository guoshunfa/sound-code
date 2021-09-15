package sun.font;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.concurrent.ConcurrentHashMap;

abstract class Underline {
   private static final float DEFAULT_THICKNESS = 1.0F;
   private static final boolean USE_THICKNESS = true;
   private static final boolean IGNORE_THICKNESS = false;
   private static final ConcurrentHashMap<Object, Underline> UNDERLINES = new ConcurrentHashMap(6);
   private static final Underline[] UNDERLINE_LIST;

   abstract void drawUnderline(Graphics2D var1, float var2, float var3, float var4, float var5);

   abstract float getLowerDrawLimit(float var1);

   abstract Shape getUnderlineShape(float var1, float var2, float var3, float var4);

   static Underline getUnderline(Object var0) {
      return var0 == null ? null : (Underline)UNDERLINES.get(var0);
   }

   static Underline getUnderline(int var0) {
      return var0 < 0 ? null : UNDERLINE_LIST[var0];
   }

   static {
      Underline[] var0 = new Underline[6];
      var0[0] = new Underline.StandardUnderline(0.0F, 1.0F, (float[])null, true);
      UNDERLINES.put(TextAttribute.UNDERLINE_ON, var0[0]);
      var0[1] = new Underline.StandardUnderline(1.0F, 1.0F, (float[])null, false);
      UNDERLINES.put(TextAttribute.UNDERLINE_LOW_ONE_PIXEL, var0[1]);
      var0[2] = new Underline.StandardUnderline(1.0F, 2.0F, (float[])null, false);
      UNDERLINES.put(TextAttribute.UNDERLINE_LOW_TWO_PIXEL, var0[2]);
      var0[3] = new Underline.StandardUnderline(1.0F, 1.0F, new float[]{1.0F, 1.0F}, false);
      UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DOTTED, var0[3]);
      var0[4] = new Underline.IMGrayUnderline();
      UNDERLINES.put(TextAttribute.UNDERLINE_LOW_GRAY, var0[4]);
      var0[5] = new Underline.StandardUnderline(1.0F, 1.0F, new float[]{4.0F, 4.0F}, false);
      UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DASHED, var0[5]);
      UNDERLINE_LIST = var0;
   }

   private static class IMGrayUnderline extends Underline {
      private BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[]{1.0F, 1.0F}, 0.0F);

      IMGrayUnderline() {
      }

      void drawUnderline(Graphics2D var1, float var2, float var3, float var4, float var5) {
         Stroke var6 = var1.getStroke();
         var1.setStroke(this.stroke);
         Line2D.Float var7 = new Line2D.Float(var3, var5, var4, var5);
         var1.draw(var7);
         ++var7.y1;
         ++var7.y2;
         ++var7.x1;
         var1.draw(var7);
         var1.setStroke(var6);
      }

      float getLowerDrawLimit(float var1) {
         return 2.0F;
      }

      Shape getUnderlineShape(float var1, float var2, float var3, float var4) {
         GeneralPath var5 = new GeneralPath();
         Line2D.Float var6 = new Line2D.Float(var2, var4, var3, var4);
         var5.append(this.stroke.createStrokedShape(var6), false);
         ++var6.y1;
         ++var6.y2;
         ++var6.x1;
         var5.append(this.stroke.createStrokedShape(var6), false);
         return var5;
      }
   }

   private static final class StandardUnderline extends Underline {
      private float shift;
      private float thicknessMultiplier;
      private float[] dashPattern;
      private boolean useThickness;
      private BasicStroke cachedStroke;

      StandardUnderline(float var1, float var2, float[] var3, boolean var4) {
         this.shift = var1;
         this.thicknessMultiplier = var2;
         this.dashPattern = var3;
         this.useThickness = var4;
         this.cachedStroke = null;
      }

      private BasicStroke createStroke(float var1) {
         return this.dashPattern == null ? new BasicStroke(var1, 0, 0) : new BasicStroke(var1, 0, 0, 10.0F, this.dashPattern, 0.0F);
      }

      private float getLineThickness(float var1) {
         return this.useThickness ? var1 * this.thicknessMultiplier : 1.0F * this.thicknessMultiplier;
      }

      private Stroke getStroke(float var1) {
         float var2 = this.getLineThickness(var1);
         BasicStroke var3 = this.cachedStroke;
         if (var3 == null || var3.getLineWidth() != var2) {
            var3 = this.createStroke(var2);
            this.cachedStroke = var3;
         }

         return var3;
      }

      void drawUnderline(Graphics2D var1, float var2, float var3, float var4, float var5) {
         Stroke var6 = var1.getStroke();
         var1.setStroke(this.getStroke(var2));
         var1.draw(new Line2D.Float(var3, var5 + this.shift, var4, var5 + this.shift));
         var1.setStroke(var6);
      }

      float getLowerDrawLimit(float var1) {
         return this.shift + this.getLineThickness(var1);
      }

      Shape getUnderlineShape(float var1, float var2, float var3, float var4) {
         Stroke var5 = this.getStroke(var1);
         Line2D.Float var6 = new Line2D.Float(var2, var4 + this.shift, var3, var4 + this.shift);
         return var5.createStrokedShape(var6);
      }
   }
}
