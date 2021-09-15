package java.awt;

import java.io.Serializable;

public class CheckboxGroup implements Serializable {
   Checkbox selectedCheckbox = null;
   private static final long serialVersionUID = 3729780091441768983L;

   public Checkbox getSelectedCheckbox() {
      return this.getCurrent();
   }

   /** @deprecated */
   @Deprecated
   public Checkbox getCurrent() {
      return this.selectedCheckbox;
   }

   public void setSelectedCheckbox(Checkbox var1) {
      this.setCurrent(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setCurrent(Checkbox var1) {
      if (var1 == null || var1.group == this) {
         Checkbox var2 = this.selectedCheckbox;
         this.selectedCheckbox = var1;
         if (var2 != null && var2 != var1 && var2.group == this) {
            var2.setState(false);
         }

         if (var1 != null && var2 != var1 && !var1.getState()) {
            var1.setStateInternal(true);
         }

      }
   }

   public String toString() {
      return this.getClass().getName() + "[selectedCheckbox=" + this.selectedCheckbox + "]";
   }
}
