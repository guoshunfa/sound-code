package javax.swing.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.Icon;

public class IconView extends View {
   private Icon c;

   public IconView(Element var1) {
      super(var1);
      AttributeSet var2 = var1.getAttributes();
      this.c = StyleConstants.getIcon(var2);
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = var2.getBounds();
      this.c.paintIcon(this.getContainer(), var1, var3.x, var3.y);
   }

   public float getPreferredSpan(int var1) {
      switch(var1) {
      case 0:
         return (float)this.c.getIconWidth();
      case 1:
         return (float)this.c.getIconHeight();
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public float getAlignment(int var1) {
      switch(var1) {
      case 1:
         return 1.0F;
      default:
         return super.getAlignment(var1);
      }
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
         throw new BadLocationException(var1 + " not in range " + var4 + "," + var5, var1);
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
}
