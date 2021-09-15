package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class NoFramesView extends BlockView {
   boolean visible = false;

   public NoFramesView(Element var1, int var2) {
      super(var1, var2);
   }

   public void paint(Graphics var1, Shape var2) {
      Container var3 = this.getContainer();
      if (var3 != null && this.visible != ((JTextComponent)var3).isEditable()) {
         this.visible = ((JTextComponent)var3).isEditable();
      }

      if (this.isVisible()) {
         super.paint(var1, var2);
      }
   }

   public void setParent(View var1) {
      if (var1 != null) {
         Container var2 = var1.getContainer();
         if (var2 != null) {
            this.visible = ((JTextComponent)var2).isEditable();
         }
      }

      super.setParent(var1);
   }

   public boolean isVisible() {
      return this.visible;
   }

   protected void layout(int var1, int var2) {
      if (this.isVisible()) {
         super.layout(var1, var2);
      }
   }

   public float getPreferredSpan(int var1) {
      return !this.visible ? 0.0F : super.getPreferredSpan(var1);
   }

   public float getMinimumSpan(int var1) {
      return !this.visible ? 0.0F : super.getMinimumSpan(var1);
   }

   public float getMaximumSpan(int var1) {
      return !this.visible ? 0.0F : super.getMaximumSpan(var1);
   }
}
