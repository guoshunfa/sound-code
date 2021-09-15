package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;

public class BasicSeparatorUI extends SeparatorUI {
   protected Color shadow;
   protected Color highlight;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicSeparatorUI();
   }

   public void installUI(JComponent var1) {
      this.installDefaults((JSeparator)var1);
      this.installListeners((JSeparator)var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults((JSeparator)var1);
      this.uninstallListeners((JSeparator)var1);
   }

   protected void installDefaults(JSeparator var1) {
      LookAndFeel.installColors(var1, "Separator.background", "Separator.foreground");
      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void uninstallDefaults(JSeparator var1) {
   }

   protected void installListeners(JSeparator var1) {
   }

   protected void uninstallListeners(JSeparator var1) {
   }

   public void paint(Graphics var1, JComponent var2) {
      Dimension var3 = var2.getSize();
      if (((JSeparator)var2).getOrientation() == 1) {
         var1.setColor(var2.getForeground());
         var1.drawLine(0, 0, 0, var3.height);
         var1.setColor(var2.getBackground());
         var1.drawLine(1, 0, 1, var3.height);
      } else {
         var1.setColor(var2.getForeground());
         var1.drawLine(0, 0, var3.width, 0);
         var1.setColor(var2.getBackground());
         var1.drawLine(0, 1, var3.width, 1);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return ((JSeparator)var1).getOrientation() == 1 ? new Dimension(2, 0) : new Dimension(0, 2);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return null;
   }

   public Dimension getMaximumSize(JComponent var1) {
      return null;
   }
}
