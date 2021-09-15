package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class SynthPasswordFieldUI extends SynthTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new SynthPasswordFieldUI();
   }

   protected String getPropertyPrefix() {
      return "PasswordField";
   }

   public View create(Element var1) {
      return new PasswordView(var1);
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      var1.getPainter().paintPasswordFieldBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintPasswordFieldBorder(var1, var2, var3, var4, var5, var6);
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      ActionMap var1 = SwingUtilities.getUIActionMap(this.getComponent());
      if (var1 != null && var1.get("select-word") != null) {
         Action var2 = var1.get("select-line");
         if (var2 != null) {
            var1.put("select-word", var2);
         }
      }

   }
}
