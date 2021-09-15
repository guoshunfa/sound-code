package javax.swing.plaf.metal;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalLabelUI extends BasicLabelUI {
   protected static MetalLabelUI metalLabelUI = new MetalLabelUI();
   private static final Object METAL_LABEL_UI_KEY = new Object();

   public static ComponentUI createUI(JComponent var0) {
      if (System.getSecurityManager() != null) {
         AppContext var1 = AppContext.getAppContext();
         MetalLabelUI var2 = (MetalLabelUI)var1.get(METAL_LABEL_UI_KEY);
         if (var2 == null) {
            var2 = new MetalLabelUI();
            var1.put(METAL_LABEL_UI_KEY, var2);
         }

         return var2;
      } else {
         return metalLabelUI;
      }
   }

   protected void paintDisabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      var2.setColor(UIManager.getColor("Label.disabledForeground"));
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
   }
}
