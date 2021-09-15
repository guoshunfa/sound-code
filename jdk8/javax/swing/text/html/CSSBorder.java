package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AttributeSet;

class CSSBorder extends AbstractBorder {
   static final int COLOR = 0;
   static final int STYLE = 1;
   static final int WIDTH = 2;
   static final int TOP = 0;
   static final int RIGHT = 1;
   static final int BOTTOM = 2;
   static final int LEFT = 3;
   static final CSS.Attribute[][] ATTRIBUTES;
   static final CSS.CssValue[] PARSERS;
   static final Object[] DEFAULTS;
   final AttributeSet attrs;
   static java.util.Map<CSS.Value, CSSBorder.BorderPainter> borderPainters;

   CSSBorder(AttributeSet var1) {
      this.attrs = var1;
   }

   private Color getBorderColor(int var1) {
      Object var2 = this.attrs.getAttribute(ATTRIBUTES[0][var1]);
      CSS.ColorValue var3;
      if (var2 instanceof CSS.ColorValue) {
         var3 = (CSS.ColorValue)var2;
      } else {
         var3 = (CSS.ColorValue)this.attrs.getAttribute(CSS.Attribute.COLOR);
         if (var3 == null) {
            var3 = (CSS.ColorValue)PARSERS[0].parseCssValue(CSS.Attribute.COLOR.getDefaultValue());
         }
      }

      return var3.getValue();
   }

   private int getBorderWidth(int var1) {
      int var2 = 0;
      CSS.BorderStyle var3 = (CSS.BorderStyle)this.attrs.getAttribute(ATTRIBUTES[1][var1]);
      if (var3 != null && var3.getValue() != CSS.Value.NONE) {
         CSS.LengthValue var4 = (CSS.LengthValue)this.attrs.getAttribute(ATTRIBUTES[2][var1]);
         if (var4 == null) {
            var4 = (CSS.LengthValue)DEFAULTS[2];
         }

         var2 = (int)var4.getValue(true);
      }

      return var2;
   }

   private int[] getWidths() {
      int[] var1 = new int[4];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.getBorderWidth(var2);
      }

      return var1;
   }

   private CSS.Value getBorderStyle(int var1) {
      CSS.BorderStyle var2 = (CSS.BorderStyle)this.attrs.getAttribute(ATTRIBUTES[1][var1]);
      if (var2 == null) {
         var2 = (CSS.BorderStyle)DEFAULTS[1];
      }

      return var2.getValue();
   }

   private Polygon getBorderShape(int var1) {
      Polygon var2 = null;
      int[] var3 = this.getWidths();
      if (var3[var1] != 0) {
         var2 = new Polygon(new int[4], new int[4], 0);
         var2.addPoint(0, 0);
         var2.addPoint(-var3[(var1 + 3) % 4], -var3[var1]);
         var2.addPoint(var3[(var1 + 1) % 4], -var3[var1]);
         var2.addPoint(0, 0);
      }

      return var2;
   }

   private CSSBorder.BorderPainter getBorderPainter(int var1) {
      CSS.Value var2 = this.getBorderStyle(var1);
      return (CSSBorder.BorderPainter)borderPainters.get(var2);
   }

   static Color getAdjustedColor(Color var0, double var1) {
      double var3 = 1.0D - Math.min(Math.abs(var1), 1.0D);
      double var5 = var1 > 0.0D ? 255.0D * (1.0D - var3) : 0.0D;
      return new Color((int)((double)var0.getRed() * var3 + var5), (int)((double)var0.getGreen() * var3 + var5), (int)((double)var0.getBlue() * var3 + var5));
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      int[] var3 = this.getWidths();
      var2.set(var3[0], var3[3], var3[2], var3[1]);
      return var2;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (var2 instanceof Graphics2D) {
         Graphics2D var7 = (Graphics2D)var2.create();
         int[] var8 = this.getWidths();
         int var9 = var3 + var8[3];
         int var10 = var4 + var8[0];
         int var11 = var5 - (var8[1] + var8[3]);
         int var12 = var6 - (var8[0] + var8[2]);
         int[][] var13 = new int[][]{{var9, var10}, {var9 + var11, var10}, {var9 + var11, var10 + var12}, {var9, var10 + var12}};

         for(int var14 = 0; var14 < 4; ++var14) {
            CSS.Value var15 = this.getBorderStyle(var14);
            Polygon var16 = this.getBorderShape(var14);
            if (var15 != CSS.Value.NONE && var16 != null) {
               int var17 = var14 % 2 == 0 ? var11 : var12;
               int[] var10000 = var16.xpoints;
               var10000[2] += var17;
               var10000 = var16.xpoints;
               var10000[3] += var17;
               Color var18 = this.getBorderColor(var14);
               CSSBorder.BorderPainter var19 = this.getBorderPainter(var14);
               double var20 = (double)var14 * 3.141592653589793D / 2.0D;
               var7.setClip(var2.getClip());
               var7.translate(var13[var14][0], var13[var14][1]);
               var7.rotate(var20);
               var7.clip(var16);
               var19.paint(var16, var7, var18, var14);
               var7.rotate(-var20);
               var7.translate(-var13[var14][0], -var13[var14][1]);
            }
         }

         var7.dispose();
      }
   }

   static void registerBorderPainter(CSS.Value var0, CSSBorder.BorderPainter var1) {
      borderPainters.put(var0, var1);
   }

   static {
      ATTRIBUTES = new CSS.Attribute[][]{{CSS.Attribute.BORDER_TOP_COLOR, CSS.Attribute.BORDER_RIGHT_COLOR, CSS.Attribute.BORDER_BOTTOM_COLOR, CSS.Attribute.BORDER_LEFT_COLOR}, {CSS.Attribute.BORDER_TOP_STYLE, CSS.Attribute.BORDER_RIGHT_STYLE, CSS.Attribute.BORDER_BOTTOM_STYLE, CSS.Attribute.BORDER_LEFT_STYLE}, {CSS.Attribute.BORDER_TOP_WIDTH, CSS.Attribute.BORDER_RIGHT_WIDTH, CSS.Attribute.BORDER_BOTTOM_WIDTH, CSS.Attribute.BORDER_LEFT_WIDTH}};
      PARSERS = new CSS.CssValue[]{new CSS.ColorValue(), new CSS.BorderStyle(), new CSS.BorderWidthValue((String)null, 0)};
      DEFAULTS = new Object[]{CSS.Attribute.BORDER_COLOR, PARSERS[1].parseCssValue(CSS.Attribute.BORDER_STYLE.getDefaultValue()), PARSERS[2].parseCssValue(CSS.Attribute.BORDER_WIDTH.getDefaultValue())};
      borderPainters = new HashMap();
      registerBorderPainter(CSS.Value.NONE, new CSSBorder.NullPainter());
      registerBorderPainter(CSS.Value.HIDDEN, new CSSBorder.NullPainter());
      registerBorderPainter(CSS.Value.SOLID, new CSSBorder.SolidPainter());
      registerBorderPainter(CSS.Value.DOUBLE, new CSSBorder.DoublePainter());
      registerBorderPainter(CSS.Value.DOTTED, new CSSBorder.DottedDashedPainter(1));
      registerBorderPainter(CSS.Value.DASHED, new CSSBorder.DottedDashedPainter(3));
      registerBorderPainter(CSS.Value.GROOVE, new CSSBorder.GrooveRidgePainter(CSS.Value.GROOVE));
      registerBorderPainter(CSS.Value.RIDGE, new CSSBorder.GrooveRidgePainter(CSS.Value.RIDGE));
      registerBorderPainter(CSS.Value.INSET, new CSSBorder.InsetOutsetPainter(CSS.Value.INSET));
      registerBorderPainter(CSS.Value.OUTSET, new CSSBorder.InsetOutsetPainter(CSS.Value.OUTSET));
   }

   static class InsetOutsetPainter extends CSSBorder.ShadowLightPainter {
      CSS.Value type;

      InsetOutsetPainter(CSS.Value var1) {
         this.type = var1;
      }

      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
         var2.setColor((var4 + 1) % 4 < 2 == (this.type == CSS.Value.INSET) ? getShadowColor(var3) : getLightColor(var3));
         var2.fillPolygon(var1);
      }
   }

   static class GrooveRidgePainter extends CSSBorder.ShadowLightPainter {
      final CSS.Value type;

      GrooveRidgePainter(CSS.Value var1) {
         this.type = var1;
      }

      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
         Rectangle var5 = var1.getBounds();
         int var6 = Math.max(var5.height / 2, 1);
         int[] var7 = new int[]{var6, var6};
         Color[] var8 = (var4 + 1) % 4 < 2 == (this.type == CSS.Value.GROOVE) ? new Color[]{getShadowColor(var3), getLightColor(var3)} : new Color[]{getLightColor(var3), getShadowColor(var3)};
         this.paintStrokes(var5, var2, 1, var7, var8);
      }
   }

   abstract static class ShadowLightPainter extends CSSBorder.StrokePainter {
      static Color getShadowColor(Color var0) {
         return CSSBorder.getAdjustedColor(var0, -0.3D);
      }

      static Color getLightColor(Color var0) {
         return CSSBorder.getAdjustedColor(var0, 0.7D);
      }
   }

   static class DottedDashedPainter extends CSSBorder.StrokePainter {
      final int factor;

      DottedDashedPainter(int var1) {
         this.factor = var1;
      }

      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
         Rectangle var5 = var1.getBounds();
         int var6 = var5.height * this.factor;
         int[] var7 = new int[]{var6, var6};
         Color[] var8 = new Color[]{var3, null};
         this.paintStrokes(var5, var2, 0, var7, var8);
      }
   }

   static class DoublePainter extends CSSBorder.StrokePainter {
      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
         Rectangle var5 = var1.getBounds();
         int var6 = Math.max(var5.height / 3, 1);
         int[] var7 = new int[]{var6, var6};
         Color[] var8 = new Color[]{var3, null};
         this.paintStrokes(var5, var2, 1, var7, var8);
      }
   }

   abstract static class StrokePainter implements CSSBorder.BorderPainter {
      void paintStrokes(Rectangle var1, Graphics var2, int var3, int[] var4, Color[] var5) {
         boolean var6 = var3 == 0;
         int var7 = 0;
         int var8 = var6 ? var1.width : var1.height;

         while(var7 < var8) {
            for(int var9 = 0; var9 < var4.length && var7 < var8; ++var9) {
               int var10 = var4[var9];
               Color var11 = var5[var9];
               if (var11 != null) {
                  int var12 = var1.x + (var6 ? var7 : 0);
                  int var13 = var1.y + (var6 ? 0 : var7);
                  int var14 = var6 ? var10 : var1.width;
                  int var15 = var6 ? var1.height : var10;
                  var2.setColor(var11);
                  var2.fillRect(var12, var13, var14, var15);
               }

               var7 += var10;
            }
         }

      }
   }

   static class SolidPainter implements CSSBorder.BorderPainter {
      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
         var2.setColor(var3);
         var2.fillPolygon(var1);
      }
   }

   static class NullPainter implements CSSBorder.BorderPainter {
      public void paint(Polygon var1, Graphics var2, Color var3, int var4) {
      }
   }

   interface BorderPainter {
      void paint(Polygon var1, Graphics var2, Color var3, int var4);
   }
}
