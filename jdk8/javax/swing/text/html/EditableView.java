package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class EditableView extends ComponentView {
   private boolean isVisible;

   EditableView(Element var1) {
      super(var1);
   }

   public float getMinimumSpan(int var1) {
      return this.isVisible ? super.getMinimumSpan(var1) : 0.0F;
   }

   public float getPreferredSpan(int var1) {
      return this.isVisible ? super.getPreferredSpan(var1) : 0.0F;
   }

   public float getMaximumSpan(int var1) {
      return this.isVisible ? super.getMaximumSpan(var1) : 0.0F;
   }

   public void paint(Graphics var1, Shape var2) {
      Component var3 = this.getComponent();
      Container var4 = this.getContainer();
      if (var4 instanceof JTextComponent && this.isVisible != ((JTextComponent)var4).isEditable()) {
         this.isVisible = ((JTextComponent)var4).isEditable();
         this.preferenceChanged((View)null, true, true);
         var4.repaint();
      }

      if (this.isVisible) {
         super.paint(var1, var2);
      } else {
         this.setSize(0.0F, 0.0F);
      }

      if (var3 != null) {
         var3.setFocusable(this.isVisible);
      }

   }

   public void setParent(View var1) {
      if (var1 != null) {
         Container var2 = var1.getContainer();
         if (var2 != null) {
            if (var2 instanceof JTextComponent) {
               this.isVisible = ((JTextComponent)var2).isEditable();
            } else {
               this.isVisible = false;
            }
         }
      }

      super.setParent(var1);
   }

   public boolean isVisible() {
      return this.isVisible;
   }
}
