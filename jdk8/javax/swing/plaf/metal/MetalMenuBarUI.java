package javax.swing.plaf.metal;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class MetalMenuBarUI extends BasicMenuBarUI {
   public static ComponentUI createUI(JComponent var0) {
      if (var0 == null) {
         throw new NullPointerException("Must pass in a non-null component");
      } else {
         return new MetalMenuBarUI();
      }
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      MetalToolBarUI.register(var1);
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      MetalToolBarUI.unregister(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      boolean var3 = var2.isOpaque();
      if (var1 == null) {
         throw new NullPointerException("Graphics must be non-null");
      } else {
         if (var3 && var2.getBackground() instanceof UIResource && UIManager.get("MenuBar.gradient") != null) {
            if (MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)var2)) {
               JToolBar var4 = (JToolBar)MetalToolBarUI.findRegisteredComponentOfType(var2, JToolBar.class);
               if (var4.isOpaque() && var4.getBackground() instanceof UIResource) {
                  MetalUtils.drawGradient(var2, var1, "MenuBar.gradient", 0, 0, var2.getWidth(), var2.getHeight() + var4.getHeight(), true);
                  this.paint(var1, var2);
                  return;
               }
            }

            MetalUtils.drawGradient(var2, var1, "MenuBar.gradient", 0, 0, var2.getWidth(), var2.getHeight(), true);
            this.paint(var1, var2);
         } else {
            super.update(var1, var2);
         }

      }
   }
}
