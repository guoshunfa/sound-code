package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxArrowButtonEditableState extends State {
   ComboBoxArrowButtonEditableState() {
      super("Editable");
   }

   protected boolean isInState(JComponent var1) {
      Container var2 = var1.getParent();
      return var2 instanceof JComboBox && ((JComboBox)var2).isEditable();
   }
}
