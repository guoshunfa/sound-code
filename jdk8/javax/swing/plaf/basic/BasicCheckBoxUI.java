package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class BasicCheckBoxUI extends BasicRadioButtonUI {
   private static final Object BASIC_CHECK_BOX_UI_KEY = new Object();
   private static final String propertyPrefix = "CheckBox.";

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      BasicCheckBoxUI var2 = (BasicCheckBoxUI)var1.get(BASIC_CHECK_BOX_UI_KEY);
      if (var2 == null) {
         var2 = new BasicCheckBoxUI();
         var1.put(BASIC_CHECK_BOX_UI_KEY, var2);
      }

      return var2;
   }

   public String getPropertyPrefix() {
      return "CheckBox.";
   }
}
