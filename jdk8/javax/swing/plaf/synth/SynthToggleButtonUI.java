package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthToggleButtonUI extends SynthButtonUI {
   public static ComponentUI createUI(JComponent var0) {
      return new SynthToggleButtonUI();
   }

   protected String getPropertyPrefix() {
      return "ToggleButton.";
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      if (((AbstractButton)var3).isContentAreaFilled()) {
         byte var4 = 0;
         byte var5 = 0;
         int var6 = var3.getWidth();
         int var7 = var3.getHeight();
         SynthPainter var8 = var1.getPainter();
         var8.paintToggleButtonBackground(var1, var2, var4, var5, var6, var7);
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintToggleButtonBorder(var1, var2, var3, var4, var5, var6);
   }
}
