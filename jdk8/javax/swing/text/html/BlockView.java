package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BlockView extends BoxView {
   private AttributeSet attr;
   private StyleSheet.BoxPainter painter;
   private CSS.LengthValue cssWidth;
   private CSS.LengthValue cssHeight;

   public BlockView(Element var1, int var2) {
      super(var1, var2);
   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 != null) {
         this.setPropertiesFromAttributes();
      }

   }

   protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      if (!spanSetFromAttributes(var1, var2, this.cssWidth, this.cssHeight)) {
         var2 = super.calculateMajorAxisRequirements(var1, var2);
      } else {
         SizeRequirements var3 = super.calculateMajorAxisRequirements(var1, (SizeRequirements)null);
         int var4 = var1 == 0 ? this.getLeftInset() + this.getRightInset() : this.getTopInset() + this.getBottomInset();
         var2.minimum -= var4;
         var2.preferred -= var4;
         var2.maximum -= var4;
         this.constrainSize(var1, var2, var3);
      }

      return var2;
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      if (!spanSetFromAttributes(var1, var2, this.cssWidth, this.cssHeight)) {
         var2 = super.calculateMinorAxisRequirements(var1, var2);
      } else {
         SizeRequirements var3 = super.calculateMinorAxisRequirements(var1, (SizeRequirements)null);
         int var4 = var1 == 0 ? this.getLeftInset() + this.getRightInset() : this.getTopInset() + this.getBottomInset();
         var2.minimum -= var4;
         var2.preferred -= var4;
         var2.maximum -= var4;
         this.constrainSize(var1, var2, var3);
      }

      if (var1 == 0) {
         Object var5 = this.getAttributes().getAttribute(CSS.Attribute.TEXT_ALIGN);
         if (var5 != null) {
            String var6 = var5.toString();
            if (var6.equals("center")) {
               var2.alignment = 0.5F;
            } else if (var6.equals("right")) {
               var2.alignment = 1.0F;
            } else {
               var2.alignment = 0.0F;
            }
         }
      }

      return var2;
   }

   boolean isPercentage(int var1, AttributeSet var2) {
      if (var1 == 0) {
         if (this.cssWidth != null) {
            return this.cssWidth.isPercentage();
         }
      } else if (this.cssHeight != null) {
         return this.cssHeight.isPercentage();
      }

      return false;
   }

   static boolean spanSetFromAttributes(int var0, SizeRequirements var1, CSS.LengthValue var2, CSS.LengthValue var3) {
      if (var0 == 0) {
         if (var2 != null && !var2.isPercentage()) {
            var1.minimum = var1.preferred = var1.maximum = (int)var2.getValue();
            return true;
         }
      } else if (var3 != null && !var3.isPercentage()) {
         var1.minimum = var1.preferred = var1.maximum = (int)var3.getValue();
         return true;
      }

      return false;
   }

   protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
      int var5 = this.getViewCount();
      CSS.Attribute var6 = var2 == 0 ? CSS.Attribute.WIDTH : CSS.Attribute.HEIGHT;

      for(int var7 = 0; var7 < var5; ++var7) {
         View var8 = this.getView(var7);
         int var9 = (int)var8.getMinimumSpan(var2);
         AttributeSet var11 = var8.getAttributes();
         CSS.LengthValue var12 = (CSS.LengthValue)var11.getAttribute(var6);
         int var10;
         if (var12 != null && var12.isPercentage()) {
            var9 = Math.max((int)var12.getValue((float)var1), var9);
            var10 = var9;
         } else {
            var10 = (int)var8.getMaximumSpan(var2);
         }

         if (var10 < var1) {
            float var13 = var8.getAlignment(var2);
            var3[var7] = (int)((float)(var1 - var10) * var13);
            var4[var7] = var10;
         } else {
            var3[var7] = 0;
            var4[var7] = Math.max(var9, var1);
         }
      }

   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = (Rectangle)var2;
      this.painter.paint(var1, (float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height, this);
      super.paint(var1, var3);
   }

   public AttributeSet getAttributes() {
      if (this.attr == null) {
         StyleSheet var1 = this.getStyleSheet();
         this.attr = var1.getViewAttributes(this);
      }

      return this.attr;
   }

   public int getResizeWeight(int var1) {
      switch(var1) {
      case 0:
         return 1;
      case 1:
         return 0;
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public float getAlignment(int var1) {
      switch(var1) {
      case 0:
         return 0.0F;
      case 1:
         if (this.getViewCount() == 0) {
            return 0.0F;
         }

         float var2 = this.getPreferredSpan(1);
         View var3 = this.getView(0);
         float var4 = var3.getPreferredSpan(1);
         float var5 = (int)var2 != 0 ? var4 * var3.getAlignment(1) / var2 : 0.0F;
         return var5;
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.changedUpdate(var1, var2, var3);
      int var4 = var1.getOffset();
      if (var4 <= this.getStartOffset() && var4 + var1.getLength() >= this.getEndOffset()) {
         this.setPropertiesFromAttributes();
      }

   }

   public float getPreferredSpan(int var1) {
      return super.getPreferredSpan(var1);
   }

   public float getMinimumSpan(int var1) {
      return super.getMinimumSpan(var1);
   }

   public float getMaximumSpan(int var1) {
      return super.getMaximumSpan(var1);
   }

   protected void setPropertiesFromAttributes() {
      StyleSheet var1 = this.getStyleSheet();
      this.attr = var1.getViewAttributes(this);
      this.painter = var1.getBoxPainter(this.attr);
      if (this.attr != null) {
         this.setInsets((short)((int)this.painter.getInset(1, this)), (short)((int)this.painter.getInset(2, this)), (short)((int)this.painter.getInset(3, this)), (short)((int)this.painter.getInset(4, this)));
      }

      this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
      this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
   }

   protected StyleSheet getStyleSheet() {
      HTMLDocument var1 = (HTMLDocument)this.getDocument();
      return var1.getStyleSheet();
   }

   private void constrainSize(int var1, SizeRequirements var2, SizeRequirements var3) {
      if (var3.minimum > var2.minimum) {
         var2.minimum = var2.preferred = var3.minimum;
         var2.maximum = Math.max(var2.maximum, var3.maximum);
      }

   }
}
