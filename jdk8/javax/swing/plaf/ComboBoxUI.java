package javax.swing.plaf;

import javax.swing.JComboBox;

public abstract class ComboBoxUI extends ComponentUI {
   public abstract void setPopupVisible(JComboBox var1, boolean var2);

   public abstract boolean isPopupVisible(JComboBox var1);

   public abstract boolean isFocusTraversable(JComboBox var1);
}
