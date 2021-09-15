package javax.swing.plaf;

import javax.swing.JOptionPane;

public abstract class OptionPaneUI extends ComponentUI {
   public abstract void selectInitialValue(JOptionPane var1);

   public abstract boolean containsCustomComponents(JOptionPane var1);
}
