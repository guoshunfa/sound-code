package javax.swing.plaf.basic;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

public class BasicPanelUI extends PanelUI {
   private static PanelUI panelUI;

   public static ComponentUI createUI(JComponent var0) {
      if (panelUI == null) {
         panelUI = new BasicPanelUI();
      }

      return panelUI;
   }

   public void installUI(JComponent var1) {
      JPanel var2 = (JPanel)var1;
      super.installUI(var2);
      this.installDefaults(var2);
   }

   public void uninstallUI(JComponent var1) {
      JPanel var2 = (JPanel)var1;
      this.uninstallDefaults(var2);
      super.uninstallUI(var1);
   }

   protected void installDefaults(JPanel var1) {
      LookAndFeel.installColorsAndFont(var1, "Panel.background", "Panel.foreground", "Panel.font");
      LookAndFeel.installBorder(var1, "Panel.border");
      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults(JPanel var1) {
      LookAndFeel.uninstallBorder(var1);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      Border var4 = var1.getBorder();
      return var4 instanceof AbstractBorder ? ((AbstractBorder)var4).getBaseline(var1, var2, var3) : -1;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      Border var2 = var1.getBorder();
      return var2 instanceof AbstractBorder ? ((AbstractBorder)var2).getBaselineResizeBehavior(var1) : Component.BaselineResizeBehavior.OTHER;
   }
}
