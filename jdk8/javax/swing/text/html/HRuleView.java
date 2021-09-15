package javax.swing.text.html;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class HRuleView extends View {
   private float topMargin;
   private float bottomMargin;
   private float leftMargin;
   private float rightMargin;
   private int alignment = 1;
   private String noshade = null;
   private int size = 0;
   private CSS.LengthValue widthValue;
   private static final int SPACE_ABOVE = 3;
   private static final int SPACE_BELOW = 3;
   private AttributeSet attr;

   public HRuleView(Element var1) {
      super(var1);
      this.setPropertiesFromAttributes();
   }

   protected void setPropertiesFromAttributes() {
      StyleSheet var1 = ((HTMLDocument)this.getDocument()).getStyleSheet();
      AttributeSet var2 = this.getElement().getAttributes();
      this.attr = var1.getViewAttributes(this);
      this.alignment = 1;
      this.size = 0;
      this.noshade = null;
      this.widthValue = null;
      if (this.attr != null) {
         if (this.attr.getAttribute(StyleConstants.Alignment) != null) {
            this.alignment = StyleConstants.getAlignment(this.attr);
         }

         this.noshade = (String)var2.getAttribute(HTML.Attribute.NOSHADE);
         Object var3 = var2.getAttribute(HTML.Attribute.SIZE);
         if (var3 != null && var3 instanceof String) {
            try {
               this.size = Integer.parseInt((String)var3);
            } catch (NumberFormatException var5) {
               this.size = 1;
            }
         }

         var3 = this.attr.getAttribute(CSS.Attribute.WIDTH);
         if (var3 != null && var3 instanceof CSS.LengthValue) {
            this.widthValue = (CSS.LengthValue)var3;
         }

         this.topMargin = this.getLength(CSS.Attribute.MARGIN_TOP, this.attr);
         this.bottomMargin = this.getLength(CSS.Attribute.MARGIN_BOTTOM, this.attr);
         this.leftMargin = this.getLength(CSS.Attribute.MARGIN_LEFT, this.attr);
         this.rightMargin = this.getLength(CSS.Attribute.MARGIN_RIGHT, this.attr);
      } else {
         this.topMargin = this.bottomMargin = this.leftMargin = this.rightMargin = 0.0F;
      }

      this.size = Math.max(2, this.size);
   }

   private float getLength(CSS.Attribute var1, AttributeSet var2) {
      CSS.LengthValue var3 = (CSS.LengthValue)var2.getAttribute(var1);
      float var4 = var3 != null ? var3.getValue() : 0.0F;
      return var4;
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
      boolean var4 = false;
      int var5 = var3.y + 3 + (int)this.topMargin;
      int var6 = var3.width - (int)(this.leftMargin + this.rightMargin);
      if (this.widthValue != null) {
         var6 = (int)this.widthValue.getValue((float)var6);
      }

      int var7 = var3.height - (6 + (int)this.topMargin + (int)this.bottomMargin);
      if (this.size > 0) {
         var7 = this.size;
      }

      int var11;
      switch(this.alignment) {
      case 0:
      default:
         var11 = var3.x + (int)this.leftMargin;
         break;
      case 1:
         var11 = var3.x + var3.width / 2 - var6 / 2;
         break;
      case 2:
         var11 = var3.x + var3.width - var6 - (int)this.rightMargin;
      }

      if (this.noshade != null) {
         var1.setColor(Color.black);
         var1.fillRect(var11, var5, var6, var7);
      } else {
         Color var8 = this.getContainer().getBackground();
         Color var9;
         Color var10;
         if (var8 != null && !var8.equals(Color.white)) {
            var10 = Color.darkGray;
            var9 = Color.white;
         } else {
            var10 = Color.darkGray;
            var9 = Color.lightGray;
         }

         var1.setColor(var9);
         var1.drawLine(var11 + var6 - 1, var5, var11 + var6 - 1, var5 + var7 - 1);
         var1.drawLine(var11, var5 + var7 - 1, var11 + var6 - 1, var5 + var7 - 1);
         var1.setColor(var10);
         var1.drawLine(var11, var5, var11 + var6 - 1, var5);
         var1.drawLine(var11, var5, var11, var5 + var7 - 1);
      }

   }

   public float getPreferredSpan(int var1) {
      switch(var1) {
      case 0:
         return 1.0F;
      case 1:
         if (this.size > 0) {
            return (float)(this.size + 3 + 3) + this.topMargin + this.bottomMargin;
         } else {
            if (this.noshade != null) {
               return 8.0F + this.topMargin + this.bottomMargin;
            }

            return 6.0F + this.topMargin + this.bottomMargin;
         }
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public int getResizeWeight(int var1) {
      if (var1 == 0) {
         return 1;
      } else {
         return var1 == 1 ? 0 : 0;
      }
   }

   public int getBreakWeight(int var1, float var2, float var3) {
      return var1 == 0 ? 3000 : 0;
   }

   public View breakView(int var1, int var2, float var3, float var4) {
      return null;
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      int var4 = this.getStartOffset();
      int var5 = this.getEndOffset();
      if (var1 >= var4 && var1 <= var5) {
         Rectangle var6 = var2.getBounds();
         if (var1 == var5) {
            var6.x += var6.width;
         }

         var6.width = 0;
         return var6;
      } else {
         return null;
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      Rectangle var5 = (Rectangle)var3;
      if (var1 < (float)(var5.x + var5.width / 2)) {
         var4[0] = Position.Bias.Forward;
         return this.getStartOffset();
      } else {
         var4[0] = Position.Bias.Backward;
         return this.getEndOffset();
      }
   }

   public AttributeSet getAttributes() {
      return this.attr;
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.changedUpdate(var1, var2, var3);
      int var4 = var1.getOffset();
      if (var4 <= this.getStartOffset() && var4 + var1.getLength() >= this.getEndOffset()) {
         this.setPropertiesFromAttributes();
      }

   }
}
