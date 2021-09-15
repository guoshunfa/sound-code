package javax.swing.plaf.nimbus;

import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxEditableState extends State {
   ComboBoxEditableState() {
      super("Editable");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JComboBox && ((JComboBox)var1).isEditable();
   }
}
