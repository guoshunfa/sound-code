package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

class OptionComboBoxModel<E> extends DefaultComboBoxModel<E> implements Serializable {
   private Option selectedOption = null;

   public void setInitialSelection(Option var1) {
      this.selectedOption = var1;
   }

   public Option getInitialSelection() {
      return this.selectedOption;
   }
}
