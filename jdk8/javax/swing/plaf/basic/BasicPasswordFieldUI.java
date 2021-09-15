package javax.swing.plaf.basic;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class BasicPasswordFieldUI extends BasicTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicPasswordFieldUI();
   }

   protected String getPropertyPrefix() {
      return "PasswordField";
   }

   protected void installDefaults() {
      super.installDefaults();
      String var1 = this.getPropertyPrefix();
      Character var2 = (Character)UIManager.getDefaults().get(var1 + ".echoChar");
      if (var2 != null) {
         LookAndFeel.installProperty(this.getComponent(), "echoChar", var2);
      }

   }

   public View create(Element var1) {
      return new PasswordView(var1);
   }

   ActionMap createActionMap() {
      ActionMap var1 = super.createActionMap();
      if (var1.get("select-word") != null) {
         Action var2 = var1.get("select-line");
         if (var2 != null) {
            var1.remove("select-word");
            var1.put("select-word", var2);
         }
      }

      return var1;
   }
}
