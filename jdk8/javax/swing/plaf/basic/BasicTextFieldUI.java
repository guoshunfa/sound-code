package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BasicTextFieldUI extends BasicTextUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicTextFieldUI();
   }

   protected String getPropertyPrefix() {
      return "TextField";
   }

   public View create(Element var1) {
      Document var2 = var1.getDocument();
      Object var3 = var2.getProperty("i18n");
      if (Boolean.TRUE.equals(var3)) {
         String var4 = var1.getName();
         if (var4 != null) {
            if (var4.equals("content")) {
               return new GlyphView(var1);
            }

            if (var4.equals("paragraph")) {
               return new BasicTextFieldUI.I18nFieldView(var1);
            }
         }
      }

      return new FieldView(var1);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      View var4 = this.getRootView((JTextComponent)var1);
      if (var4.getViewCount() > 0) {
         Insets var5 = var1.getInsets();
         var3 = var3 - var5.top - var5.bottom;
         if (var3 > 0) {
            int var6 = var5.top;
            View var7 = var4.getView(0);
            int var8 = (int)var7.getPreferredSpan(1);
            int var9;
            if (var3 != var8) {
               var9 = var3 - var8;
               var6 += var9 / 2;
            }

            if (var7 instanceof BasicTextFieldUI.I18nFieldView) {
               var9 = BasicHTML.getBaseline(var7, var2 - var5.left - var5.right, var3);
               if (var9 < 0) {
                  return -1;
               }

               var6 += var9;
            } else {
               FontMetrics var10 = var1.getFontMetrics(var1.getFont());
               var6 += var10.getAscent();
            }

            return var6;
         }
      }

      return -1;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CENTER_OFFSET;
   }

   static class I18nFieldView extends ParagraphView {
      I18nFieldView(Element var1) {
         super(var1);
      }

      public int getFlowSpan(int var1) {
         return Integer.MAX_VALUE;
      }

      protected void setJustification(int var1) {
      }

      static boolean isLeftToRight(Component var0) {
         return var0.getComponentOrientation().isLeftToRight();
      }

      Shape adjustAllocation(Shape var1) {
         if (var1 != null) {
            Rectangle var2 = var1.getBounds();
            int var3 = (int)this.getPreferredSpan(1);
            int var4 = (int)this.getPreferredSpan(0);
            if (var2.height != var3) {
               int var5 = var2.height - var3;
               var2.y += var5 / 2;
               var2.height -= var5;
            }

            Container var13 = this.getContainer();
            if (var13 instanceof JTextField) {
               JTextField var6 = (JTextField)var13;
               BoundedRangeModel var7 = var6.getHorizontalVisibility();
               int var8 = Math.max(var4, var2.width);
               int var9 = var7.getValue();
               int var10 = Math.min(var8, var2.width - 1);
               if (var9 + var10 > var8) {
                  var9 = var8 - var10;
               }

               var7.setRangeProperties(var9, var10, var7.getMinimum(), var8, false);
               if (var4 < var2.width) {
                  int var11 = var2.width - 1 - var4;
                  int var12 = ((JTextField)var13).getHorizontalAlignment();
                  if (isLeftToRight(var13)) {
                     if (var12 == 10) {
                        var12 = 2;
                     } else if (var12 == 11) {
                        var12 = 4;
                     }
                  } else if (var12 == 10) {
                     var12 = 4;
                  } else if (var12 == 11) {
                     var12 = 2;
                  }

                  switch(var12) {
                  case 0:
                     var2.x += var11 / 2;
                     var2.width -= var11;
                     break;
                  case 4:
                     var2.x += var11;
                     var2.width -= var11;
                  }
               } else {
                  var2.width = var4;
                  var2.x -= var7.getValue();
               }
            }

            return var2;
         } else {
            return null;
         }
      }

      void updateVisibilityModel() {
         Container var1 = this.getContainer();
         if (var1 instanceof JTextField) {
            JTextField var2 = (JTextField)var1;
            BoundedRangeModel var3 = var2.getHorizontalVisibility();
            int var4 = (int)this.getPreferredSpan(0);
            int var5 = var3.getExtent();
            int var6 = Math.max(var4, var5);
            var5 = var5 == 0 ? var6 : var5;
            int var7 = var6 - var5;
            int var8 = var3.getValue();
            if (var8 + var5 > var6) {
               var8 = var6 - var5;
            }

            var7 = Math.max(0, Math.min(var7, var8));
            var3.setRangeProperties(var7, var5, 0, var6, false);
         }

      }

      public void paint(Graphics var1, Shape var2) {
         Rectangle var3 = (Rectangle)var2;
         var1.clipRect(var3.x, var3.y, var3.width, var3.height);
         super.paint(var1, this.adjustAllocation(var2));
      }

      public int getResizeWeight(int var1) {
         return var1 == 0 ? 1 : 0;
      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         return super.modelToView(var1, this.adjustAllocation(var2), var3);
      }

      public Shape modelToView(int var1, Position.Bias var2, int var3, Position.Bias var4, Shape var5) throws BadLocationException {
         return super.modelToView(var1, var2, var3, var4, this.adjustAllocation(var5));
      }

      public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
         return super.viewToModel(var1, var2, this.adjustAllocation(var3), var4);
      }

      public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         super.insertUpdate(var1, this.adjustAllocation(var2), var3);
         this.updateVisibilityModel();
      }

      public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         super.removeUpdate(var1, this.adjustAllocation(var2), var3);
         this.updateVisibilityModel();
      }
   }
}
