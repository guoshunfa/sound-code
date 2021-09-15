package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class MetalPopupMenuSeparatorUI extends MetalSeparatorUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MetalPopupMenuSeparatorUI();
   }

   public void paint(Graphics var1, JComponent var2) {
      Dimension var3 = var2.getSize();
      var1.setColor(var2.getForeground());
      var1.drawLine(0, 1, var3.width, 1);
      var1.setColor(var2.getBackground());
      var1.drawLine(0, 2, var3.width, 2);
      var1.drawLine(0, 0, 0, 0);
      var1.drawLine(0, 3, 0, 3);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return new Dimension(0, 4);
   }
}
