package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

public class ParagraphView extends javax.swing.text.ParagraphView {
   private AttributeSet attr;
   private StyleSheet.BoxPainter painter;
   private CSS.LengthValue cssWidth;
   private CSS.LengthValue cssHeight;

   public ParagraphView(Element var1) {
      super(var1);
   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 != null) {
         this.setPropertiesFromAttributes();
      }

   }

   public AttributeSet getAttributes() {
      if (this.attr == null) {
         StyleSheet var1 = this.getStyleSheet();
         this.attr = var1.getViewAttributes(this);
      }

      return this.attr;
   }

   protected void setPropertiesFromAttributes() {
      StyleSheet var1 = this.getStyleSheet();
      this.attr = var1.getViewAttributes(this);
      this.painter = var1.getBoxPainter(this.attr);
      if (this.attr != null) {
         super.setPropertiesFromAttributes();
         this.setInsets((short)((int)this.painter.getInset(1, this)), (short)((int)this.painter.getInset(2, this)), (short)((int)this.painter.getInset(3, this)), (short)((int)this.painter.getInset(4, this)));
         Object var2 = this.attr.getAttribute(CSS.Attribute.TEXT_ALIGN);
         if (var2 != null) {
            String var3 = var2.toString();
            if (var3.equals("left")) {
               this.setJustification(0);
            } else if (var3.equals("center")) {
               this.setJustification(1);
            } else if (var3.equals("right")) {
               this.setJustification(2);
            } else if (var3.equals("justify")) {
               this.setJustification(3);
            }
         }

         this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
         this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
      }

   }

   protected StyleSheet getStyleSheet() {
      HTMLDocument var1 = (HTMLDocument)this.getDocument();
      return var1.getStyleSheet();
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      var2 = super.calculateMinorAxisRequirements(var1, var2);
      if (BlockView.spanSetFromAttributes(var1, var2, this.cssWidth, this.cssHeight)) {
         int var3 = var1 == 0 ? this.getLeftInset() + this.getRightInset() : this.getTopInset() + this.getBottomInset();
         var2.minimum -= var3;
         var2.preferred -= var3;
         var2.maximum -= var3;
      }

      return var2;
   }

   public boolean isVisible() {
      int var1 = this.getLayoutViewCount() - 1;

      for(int var2 = 0; var2 < var1; ++var2) {
         View var3 = this.getLayoutView(var2);
         if (var3.isVisible()) {
            return true;
         }
      }

      if (var1 > 0) {
         View var4 = this.getLayoutView(var1);
         if (var4.getEndOffset() - var4.getStartOffset() == 1) {
            return false;
         }
      }

      if (this.getStartOffset() == this.getDocument().getLength()) {
         boolean var5 = false;
         Container var6 = this.getContainer();
         if (var6 instanceof JTextComponent) {
            var5 = ((JTextComponent)var6).isEditable();
         }

         if (!var5) {
            return false;
         }
      }

      return true;
   }

   public void paint(Graphics var1, Shape var2) {
      if (var2 != null) {
         Rectangle var3;
         if (var2 instanceof Rectangle) {
            var3 = (Rectangle)var2;
         } else {
            var3 = var2.getBounds();
         }

         this.painter.paint(var1, (float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height, this);
         super.paint(var1, var2);
      }
   }

   public float getPreferredSpan(int var1) {
      return !this.isVisible() ? 0.0F : super.getPreferredSpan(var1);
   }

   public float getMinimumSpan(int var1) {
      return !this.isVisible() ? 0.0F : super.getMinimumSpan(var1);
   }

   public float getMaximumSpan(int var1) {
      return !this.isVisible() ? 0.0F : super.getMaximumSpan(var1);
   }
}
