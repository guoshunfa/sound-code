package sun.font;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class Decoration {
   private static final int VALUES_MASK;
   private static final Decoration PLAIN;

   private Decoration() {
   }

   public static Decoration getPlainDecoration() {
      return PLAIN;
   }

   public static Decoration getDecoration(AttributeValues var0) {
      if (var0 != null && var0.anyDefined(VALUES_MASK)) {
         var0 = var0.applyIMHighlight();
         return new Decoration.DecorationImpl(var0.getForeground(), var0.getBackground(), var0.getSwapColors(), var0.getStrikethrough(), Underline.getUnderline(var0.getUnderline()), Underline.getUnderline(var0.getInputMethodUnderline()));
      } else {
         return PLAIN;
      }
   }

   public static Decoration getDecoration(Map var0) {
      return var0 == null ? PLAIN : getDecoration(AttributeValues.fromMap(var0));
   }

   public void drawTextAndDecorations(Decoration.Label var1, Graphics2D var2, float var3, float var4) {
      var1.handleDraw(var2, var3, var4);
   }

   public Rectangle2D getVisualBounds(Decoration.Label var1) {
      return var1.handleGetVisualBounds();
   }

   public Rectangle2D getCharVisualBounds(Decoration.Label var1, int var2) {
      return var1.handleGetCharVisualBounds(var2);
   }

   Shape getOutline(Decoration.Label var1, float var2, float var3) {
      return var1.handleGetOutline(var2, var3);
   }

   // $FF: synthetic method
   Decoration(Object var1) {
      this();
   }

   static {
      VALUES_MASK = AttributeValues.getMask(EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.ESWAP_COLORS, EAttribute.ESTRIKETHROUGH, EAttribute.EUNDERLINE, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE);
      PLAIN = new Decoration();
   }

   private static final class DecorationImpl extends Decoration {
      private Paint fgPaint = null;
      private Paint bgPaint = null;
      private boolean swapColors = false;
      private boolean strikethrough = false;
      private Underline stdUnderline = null;
      private Underline imUnderline = null;

      DecorationImpl(Paint var1, Paint var2, boolean var3, boolean var4, Underline var5, Underline var6) {
         super(null);
         this.fgPaint = var1;
         this.bgPaint = var2;
         this.swapColors = var3;
         this.strikethrough = var4;
         this.stdUnderline = var5;
         this.imUnderline = var6;
      }

      private static boolean areEqual(Object var0, Object var1) {
         if (var0 == null) {
            return var1 == null;
         } else {
            return var0.equals(var1);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 == null) {
            return false;
         } else {
            Decoration.DecorationImpl var2 = null;

            try {
               var2 = (Decoration.DecorationImpl)var1;
            } catch (ClassCastException var4) {
               return false;
            }

            if (this.swapColors == var2.swapColors && this.strikethrough == var2.strikethrough) {
               if (!areEqual(this.stdUnderline, var2.stdUnderline)) {
                  return false;
               } else if (!areEqual(this.fgPaint, var2.fgPaint)) {
                  return false;
               } else {
                  return !areEqual(this.bgPaint, var2.bgPaint) ? false : areEqual(this.imUnderline, var2.imUnderline);
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         int var1 = 1;
         if (this.strikethrough) {
            var1 |= 2;
         }

         if (this.swapColors) {
            var1 |= 4;
         }

         if (this.stdUnderline != null) {
            var1 += this.stdUnderline.hashCode();
         }

         return var1;
      }

      private float getUnderlineMaxY(CoreMetrics var1) {
         float var2 = 0.0F;
         float var3;
         if (this.stdUnderline != null) {
            var3 = var1.underlineOffset;
            var3 += this.stdUnderline.getLowerDrawLimit(var1.underlineThickness);
            var2 = Math.max(var2, var3);
         }

         if (this.imUnderline != null) {
            var3 = var1.underlineOffset;
            var3 += this.imUnderline.getLowerDrawLimit(var1.underlineThickness);
            var2 = Math.max(var2, var3);
         }

         return var2;
      }

      private void drawTextAndEmbellishments(Decoration.Label var1, Graphics2D var2, float var3, float var4) {
         var1.handleDraw(var2, var3, var4);
         if (this.strikethrough || this.stdUnderline != null || this.imUnderline != null) {
            float var6 = var3 + (float)var1.getLogicalBounds().getWidth();
            CoreMetrics var7 = var1.getCoreMetrics();
            float var9;
            if (this.strikethrough) {
               Stroke var8 = var2.getStroke();
               var2.setStroke(new BasicStroke(var7.strikethroughThickness, 0, 0));
               var9 = var4 + var7.strikethroughOffset;
               var2.draw(new Line2D.Float(var3, var9, var6, var9));
               var2.setStroke(var8);
            }

            float var10 = var7.underlineOffset;
            var9 = var7.underlineThickness;
            if (this.stdUnderline != null) {
               this.stdUnderline.drawUnderline(var2, var9, var3, var6, var4 + var10);
            }

            if (this.imUnderline != null) {
               this.imUnderline.drawUnderline(var2, var9, var3, var6, var4 + var10);
            }

         }
      }

      public void drawTextAndDecorations(Decoration.Label var1, Graphics2D var2, float var3, float var4) {
         if (this.fgPaint == null && this.bgPaint == null && !this.swapColors) {
            this.drawTextAndEmbellishments(var1, var2, var3, var4);
         } else {
            Paint var5 = var2.getPaint();
            Object var6;
            Paint var7;
            if (this.swapColors) {
               var7 = this.fgPaint == null ? var5 : this.fgPaint;
               if (this.bgPaint == null) {
                  if (var7 instanceof Color) {
                     Color var8 = (Color)var7;
                     int var9 = 33 * var8.getRed() + 53 * var8.getGreen() + 14 * var8.getBlue();
                     var6 = var9 > 18500 ? Color.BLACK : Color.WHITE;
                  } else {
                     var6 = Color.WHITE;
                  }
               } else {
                  var6 = this.bgPaint;
               }
            } else {
               var6 = this.fgPaint == null ? var5 : this.fgPaint;
               var7 = this.bgPaint;
            }

            if (var7 != null) {
               Rectangle2D var10 = var1.getLogicalBounds();
               Rectangle2D.Float var11 = new Rectangle2D.Float(var3 + (float)var10.getX(), var4 + (float)var10.getY(), (float)var10.getWidth(), (float)var10.getHeight());
               var2.setPaint(var7);
               var2.fill(var11);
            }

            var2.setPaint((Paint)var6);
            this.drawTextAndEmbellishments(var1, var2, var3, var4);
            var2.setPaint(var5);
         }

      }

      public Rectangle2D getVisualBounds(Decoration.Label var1) {
         Rectangle2D var2 = var1.handleGetVisualBounds();
         if (this.swapColors || this.bgPaint != null || this.strikethrough || this.stdUnderline != null || this.imUnderline != null) {
            float var3 = 0.0F;
            Rectangle2D var4 = var1.getLogicalBounds();
            float var5 = 0.0F;
            float var6 = 0.0F;
            if (this.swapColors || this.bgPaint != null) {
               var5 = (float)var4.getY();
               var6 = var5 + (float)var4.getHeight();
            }

            var6 = Math.max(var6, this.getUnderlineMaxY(var1.getCoreMetrics()));
            Rectangle2D.Float var7 = new Rectangle2D.Float(var3, var5, (float)var4.getWidth(), var6 - var5);
            var2.add((Rectangle2D)var7);
         }

         return var2;
      }

      Shape getOutline(Decoration.Label var1, float var2, float var3) {
         if (!this.strikethrough && this.stdUnderline == null && this.imUnderline == null) {
            return var1.handleGetOutline(var2, var3);
         } else {
            CoreMetrics var4 = var1.getCoreMetrics();
            float var5 = var4.underlineThickness;
            float var6 = var4.underlineOffset;
            Rectangle2D var7 = var1.getLogicalBounds();
            float var9 = var2 + (float)var7.getWidth();
            Area var10 = null;
            Shape var11;
            if (this.stdUnderline != null) {
               var11 = this.stdUnderline.getUnderlineShape(var5, var2, var9, var3 + var6);
               var10 = new Area(var11);
            }

            if (this.strikethrough) {
               BasicStroke var15 = new BasicStroke(var4.strikethroughThickness, 0, 0);
               float var12 = var3 + var4.strikethroughOffset;
               Line2D.Float var13 = new Line2D.Float(var2, var12, var9, var12);
               Area var14 = new Area(var15.createStrokedShape(var13));
               if (var10 == null) {
                  var10 = var14;
               } else {
                  var10.add(var14);
               }
            }

            if (this.imUnderline != null) {
               var11 = this.imUnderline.getUnderlineShape(var5, var2, var9, var3 + var6);
               Area var16 = new Area(var11);
               if (var10 == null) {
                  var10 = var16;
               } else {
                  var10.add(var16);
               }
            }

            var10.add(new Area(var1.handleGetOutline(var2, var3)));
            return new GeneralPath(var10);
         }
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         var1.append(super.toString());
         var1.append("[");
         if (this.fgPaint != null) {
            var1.append("fgPaint: " + this.fgPaint);
         }

         if (this.bgPaint != null) {
            var1.append(" bgPaint: " + this.bgPaint);
         }

         if (this.swapColors) {
            var1.append(" swapColors: true");
         }

         if (this.strikethrough) {
            var1.append(" strikethrough: true");
         }

         if (this.stdUnderline != null) {
            var1.append(" stdUnderline: " + this.stdUnderline);
         }

         if (this.imUnderline != null) {
            var1.append(" imUnderline: " + this.imUnderline);
         }

         var1.append("]");
         return var1.toString();
      }
   }

   public interface Label {
      CoreMetrics getCoreMetrics();

      Rectangle2D getLogicalBounds();

      void handleDraw(Graphics2D var1, float var2, float var3);

      Rectangle2D handleGetCharVisualBounds(int var1);

      Rectangle2D handleGetVisualBounds();

      Shape handleGetOutline(float var1, float var2);
   }
}
