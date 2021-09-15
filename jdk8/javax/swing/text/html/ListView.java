package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.Element;

public class ListView extends BlockView {
   private StyleSheet.ListPainter listPainter;

   public ListView(Element var1) {
      super(var1, 1);
   }

   public float getAlignment(int var1) {
      switch(var1) {
      case 0:
         return 0.5F;
      case 1:
         return 0.5F;
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public void paint(Graphics var1, Shape var2) {
      super.paint(var1, var2);
      Rectangle var3 = var2.getBounds();
      Rectangle var4 = var1.getClipBounds();
      if (var4.x + var4.width < var3.x + this.getLeftInset()) {
         Rectangle var5 = var3;
         var3 = this.getInsideAllocation(var2);
         int var6 = this.getViewCount();
         int var7 = var4.y + var4.height;

         for(int var8 = 0; var8 < var6; ++var8) {
            var5.setBounds(var3);
            this.childAllocation(var8, var5);
            if (var5.y >= var7) {
               break;
            }

            if (var5.y + var5.height >= var4.y) {
               this.listPainter.paint(var1, (float)var5.x, (float)var5.y, (float)var5.width, (float)var5.height, this, var8);
            }
         }
      }

   }

   protected void paintChild(Graphics var1, Rectangle var2, int var3) {
      this.listPainter.paint(var1, (float)var2.x, (float)var2.y, (float)var2.width, (float)var2.height, this, var3);
      super.paintChild(var1, var2, var3);
   }

   protected void setPropertiesFromAttributes() {
      super.setPropertiesFromAttributes();
      this.listPainter = this.getStyleSheet().getListPainter(this.getAttributes());
   }
}
