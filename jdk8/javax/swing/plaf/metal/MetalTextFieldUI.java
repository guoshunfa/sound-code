package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class MetalTextFieldUI extends BasicTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MetalTextFieldUI();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
   }
}
