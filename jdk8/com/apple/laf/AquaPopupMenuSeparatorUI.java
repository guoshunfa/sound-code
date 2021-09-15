package com.apple.laf;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class AquaPopupMenuSeparatorUI extends BasicSeparatorUI {
   protected static AquaUtils.RecyclableSingletonFromDefaultConstructor<AquaPopupMenuSeparatorUI> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaPopupMenuSeparatorUI.class);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)instance.get();
   }

   public void update(Graphics var1, JComponent var2) {
      this.paint(var1, var2);
   }

   public void paint(Graphics var1, JComponent var2) {
      Dimension var3 = var2.getSize();
      if (((JSeparator)var2).getOrientation() == 1) {
         var1.setColor(var2.getForeground());
         var1.drawLine(5, 1, 5, var3.height - 2);
         var1.setColor(var2.getBackground());
         var1.drawLine(6, 1, 6, var3.height - 2);
      } else {
         var1.setColor(var2.getForeground());
         var1.drawLine(1, 5, var3.width - 2, 5);
         var1.setColor(var2.getBackground());
         var1.drawLine(1, 6, var3.width - 2, 6);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return ((JSeparator)var1).getOrientation() == 1 ? new Dimension(12, 0) : new Dimension(0, 12);
   }
}
