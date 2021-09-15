package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BoxView;
import javax.swing.text.CompositeView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

public class BasicTextAreaUI extends BasicTextUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicTextAreaUI();
   }

   protected String getPropertyPrefix() {
      return "TextArea";
   }

   protected void installDefaults() {
      super.installDefaults();
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      if (!var1.getPropertyName().equals("lineWrap") && !var1.getPropertyName().equals("wrapStyleWord") && !var1.getPropertyName().equals("tabSize")) {
         if ("editable".equals(var1.getPropertyName())) {
            this.updateFocusTraversalKeys();
         }
      } else {
         this.modelChanged();
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return super.getPreferredSize(var1);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return super.getMinimumSize(var1);
   }

   public View create(Element var1) {
      Document var2 = var1.getDocument();
      Object var3 = var2.getProperty("i18n");
      if (var3 != null && var3.equals(Boolean.TRUE)) {
         return this.createI18N(var1);
      } else {
         JTextComponent var4 = this.getComponent();
         if (var4 instanceof JTextArea) {
            JTextArea var5 = (JTextArea)var4;
            Object var6;
            if (var5.getLineWrap()) {
               var6 = new WrappedPlainView(var1, var5.getWrapStyleWord());
            } else {
               var6 = new PlainView(var1);
            }

            return (View)var6;
         } else {
            return null;
         }
      }
   }

   View createI18N(Element var1) {
      String var2 = var1.getName();
      if (var2 != null) {
         if (var2.equals("content")) {
            return new BasicTextAreaUI.PlainParagraph(var1);
         }

         if (var2.equals("paragraph")) {
            return new BoxView(var1, 1);
         }
      }

      return null;
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      Object var4 = ((JTextComponent)var1).getDocument().getProperty("i18n");
      Insets var5 = var1.getInsets();
      if (Boolean.TRUE.equals(var4)) {
         View var9 = this.getRootView((JTextComponent)var1);
         if (var9.getViewCount() > 0) {
            var3 = var3 - var5.top - var5.bottom;
            int var7 = var5.top;
            int var8 = BasicHTML.getBaseline(var9.getView(0), var2 - var5.left - var5.right, var3);
            return var8 < 0 ? -1 : var7 + var8;
         } else {
            return -1;
         }
      } else {
         FontMetrics var6 = var1.getFontMetrics(var1.getFont());
         return var5.top + var6.getAscent();
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
   }

   static class PlainParagraph extends ParagraphView {
      PlainParagraph(Element var1) {
         super(var1);
         this.layoutPool = new BasicTextAreaUI.PlainParagraph.LogicalView(var1);
         this.layoutPool.setParent(this);
      }

      public void setParent(View var1) {
         super.setParent(var1);
         if (var1 != null) {
            this.setPropertiesFromAttributes();
         }

      }

      protected void setPropertiesFromAttributes() {
         Container var1 = this.getContainer();
         if (var1 != null && !var1.getComponentOrientation().isLeftToRight()) {
            this.setJustification(2);
         } else {
            this.setJustification(0);
         }

      }

      public int getFlowSpan(int var1) {
         Container var2 = this.getContainer();
         if (var2 instanceof JTextArea) {
            JTextArea var3 = (JTextArea)var2;
            if (!var3.getLineWrap()) {
               return Integer.MAX_VALUE;
            }
         }

         return super.getFlowSpan(var1);
      }

      protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
         SizeRequirements var3 = super.calculateMinorAxisRequirements(var1, var2);
         Container var4 = this.getContainer();
         if (var4 instanceof JTextArea) {
            JTextArea var5 = (JTextArea)var4;
            if (!var5.getLineWrap()) {
               var3.minimum = var3.preferred;
            } else {
               var3.minimum = 0;
               var3.preferred = this.getWidth();
               if (var3.preferred == Integer.MAX_VALUE) {
                  var3.preferred = 100;
               }
            }
         }

         return var3;
      }

      public void setSize(float var1, float var2) {
         if ((int)var1 != this.getWidth()) {
            this.preferenceChanged((View)null, true, true);
         }

         super.setSize(var1, var2);
      }

      static class LogicalView extends CompositeView {
         LogicalView(Element var1) {
            super(var1);
         }

         protected int getViewIndexAtPosition(int var1) {
            Element var2 = this.getElement();
            return var2.getElementCount() > 0 ? var2.getElementIndex(var1) : 0;
         }

         protected boolean updateChildren(DocumentEvent.ElementChange var1, DocumentEvent var2, ViewFactory var3) {
            return false;
         }

         protected void loadChildren(ViewFactory var1) {
            Element var2 = this.getElement();
            if (var2.getElementCount() > 0) {
               super.loadChildren(var1);
            } else {
               GlyphView var3 = new GlyphView(var2);
               this.append(var3);
            }

         }

         public float getPreferredSpan(int var1) {
            if (this.getViewCount() != 1) {
               throw new Error("One child view is assumed.");
            } else {
               View var2 = this.getView(0);
               return var2.getPreferredSpan(var1);
            }
         }

         protected void forwardUpdateToView(View var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
            var1.setParent(this);
            super.forwardUpdateToView(var1, var2, var3, var4);
         }

         public void paint(Graphics var1, Shape var2) {
         }

         protected boolean isBefore(int var1, int var2, Rectangle var3) {
            return false;
         }

         protected boolean isAfter(int var1, int var2, Rectangle var3) {
            return false;
         }

         protected View getViewAtPoint(int var1, int var2, Rectangle var3) {
            return null;
         }

         protected void childAllocation(int var1, Rectangle var2) {
         }
      }
   }
}
