package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicFormattedTextFieldUI extends BasicTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicFormattedTextFieldUI();
   }

   protected String getPropertyPrefix() {
      return "FormattedTextField";
   }
}
