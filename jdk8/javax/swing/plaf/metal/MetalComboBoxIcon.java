package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;

public class MetalComboBoxIcon implements Icon, Serializable {
   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      JComponent var5 = (JComponent)var1;
      int var6 = this.getIconWidth();
      var2.translate(var3, var4);
      var2.setColor(var5.isEnabled() ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlShadow());
      var2.drawLine(0, 0, var6 - 1, 0);
      var2.drawLine(1, 1, 1 + (var6 - 3), 1);
      var2.drawLine(2, 2, 2 + (var6 - 5), 2);
      var2.drawLine(3, 3, 3 + (var6 - 7), 3);
      var2.drawLine(4, 4, 4 + (var6 - 9), 4);
      var2.translate(-var3, -var4);
   }

   public int getIconWidth() {
      return 10;
   }

   public int getIconHeight() {
      return 5;
   }
}
