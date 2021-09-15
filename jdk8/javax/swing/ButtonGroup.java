package javax.swing;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class ButtonGroup implements Serializable {
   protected Vector<AbstractButton> buttons = new Vector();
   ButtonModel selection = null;

   public void add(AbstractButton var1) {
      if (var1 != null) {
         this.buttons.addElement(var1);
         if (var1.isSelected()) {
            if (this.selection == null) {
               this.selection = var1.getModel();
            } else {
               var1.setSelected(false);
            }
         }

         var1.getModel().setGroup(this);
      }
   }

   public void remove(AbstractButton var1) {
      if (var1 != null) {
         this.buttons.removeElement(var1);
         if (var1.getModel() == this.selection) {
            this.selection = null;
         }

         var1.getModel().setGroup((ButtonGroup)null);
      }
   }

   public void clearSelection() {
      if (this.selection != null) {
         ButtonModel var1 = this.selection;
         this.selection = null;
         var1.setSelected(false);
      }

   }

   public Enumeration<AbstractButton> getElements() {
      return this.buttons.elements();
   }

   public ButtonModel getSelection() {
      return this.selection;
   }

   public void setSelected(ButtonModel var1, boolean var2) {
      if (var2 && var1 != null && var1 != this.selection) {
         ButtonModel var3 = this.selection;
         this.selection = var1;
         if (var3 != null) {
            var3.setSelected(false);
         }

         var1.setSelected(true);
      }

   }

   public boolean isSelected(ButtonModel var1) {
      return var1 == this.selection;
   }

   public int getButtonCount() {
      return this.buttons == null ? 0 : this.buttons.size();
   }
}
