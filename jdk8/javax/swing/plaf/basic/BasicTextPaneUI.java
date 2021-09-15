package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicTextPaneUI extends BasicEditorPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicTextPaneUI();
   }

   protected String getPropertyPrefix() {
      return "TextPane";
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
   }
}
