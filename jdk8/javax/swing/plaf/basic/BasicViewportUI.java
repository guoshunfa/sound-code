package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class BasicViewportUI extends ViewportUI {
   private static ViewportUI viewportUI;

   public static ComponentUI createUI(JComponent var0) {
      if (viewportUI == null) {
         viewportUI = new BasicViewportUI();
      }

      return viewportUI;
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.installDefaults(var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults(var1);
      super.uninstallUI(var1);
   }

   protected void installDefaults(JComponent var1) {
      LookAndFeel.installColorsAndFont(var1, "Viewport.background", "Viewport.foreground", "Viewport.font");
      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults(JComponent var1) {
   }
}
