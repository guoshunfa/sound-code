package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class WindowsToolBarUI extends BasicToolBarUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsToolBarUI();
   }

   protected void installDefaults() {
      if (XPStyle.getXP() != null) {
         this.setRolloverBorders(true);
      }

      super.installDefaults();
   }

   protected Border createRolloverBorder() {
      return (Border)(XPStyle.getXP() != null ? new EmptyBorder(3, 3, 3, 3) : super.createRolloverBorder());
   }

   protected Border createNonRolloverBorder() {
      return (Border)(XPStyle.getXP() != null ? new EmptyBorder(3, 3, 3, 3) : super.createNonRolloverBorder());
   }

   public void paint(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         var3.getSkin(var2, TMSchema.Part.TP_TOOLBAR).paintSkin(var1, 0, 0, var2.getWidth(), var2.getHeight(), (TMSchema.State)null, true);
      } else {
         super.paint(var1, var2);
      }

   }

   protected Border getRolloverBorder(AbstractButton var1) {
      XPStyle var2 = XPStyle.getXP();
      return var2 != null ? var2.getBorder(var1, WindowsButtonUI.getXPButtonType(var1)) : super.getRolloverBorder(var1);
   }
}
