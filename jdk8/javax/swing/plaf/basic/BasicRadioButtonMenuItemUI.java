package javax.swing.plaf.basic;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;

public class BasicRadioButtonMenuItemUI extends BasicMenuItemUI {
   public static ComponentUI createUI(JComponent var0) {
      return new BasicRadioButtonMenuItemUI();
   }

   protected String getPropertyPrefix() {
      return "RadioButtonMenuItem";
   }

   public void processMouseEvent(JMenuItem var1, MouseEvent var2, MenuElement[] var3, MenuSelectionManager var4) {
      Point var5 = var2.getPoint();
      if (var5.x >= 0 && var5.x < var1.getWidth() && var5.y >= 0 && var5.y < var1.getHeight()) {
         if (var2.getID() == 502) {
            var4.clearSelectedPath();
            var1.doClick(0);
            var1.setArmed(false);
         } else {
            var4.setSelectedPath(var3);
         }
      } else if (var1.getModel().isArmed()) {
         MenuElement[] var6 = new MenuElement[var3.length - 1];
         int var7 = 0;

         for(int var8 = var3.length - 1; var7 < var8; ++var7) {
            var6[var7] = var3[var7];
         }

         var4.setSelectedPath(var6);
      }

   }
}
