package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthCheckBoxUI extends SynthRadioButtonUI {
   public static ComponentUI createUI(JComponent var0) {
      return new SynthCheckBoxUI();
   }

   protected String getPropertyPrefix() {
      return "CheckBox.";
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      var1.getPainter().paintCheckBoxBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintCheckBoxBorder(var1, var2, var3, var4, var5, var6);
   }
}
