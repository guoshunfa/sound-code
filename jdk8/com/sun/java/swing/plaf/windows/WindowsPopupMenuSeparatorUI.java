package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsPopupMenuSeparatorUI();
   }

   public void paint(Graphics var1, JComponent var2) {
      Dimension var3 = var2.getSize();
      XPStyle var4 = XPStyle.getXP();
      int var5;
      if (WindowsMenuItemUI.isVistaPainting(var4)) {
         var5 = 1;
         Container var6 = var2.getParent();
         if (var6 instanceof JComponent) {
            Object var7 = ((JComponent)var6).getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
            if (var7 instanceof Integer) {
               var5 = (Integer)var7 - var2.getX();
               var5 += WindowsPopupMenuUI.getGutterWidth();
            }
         }

         XPStyle.Skin var10 = var4.getSkin(var2, TMSchema.Part.MP_POPUPSEPARATOR);
         int var8 = var10.getHeight();
         int var9 = (var3.height - var8) / 2;
         var10.paintSkin(var1, var5, var9, var3.width - var5 - 1, var8, TMSchema.State.NORMAL);
      } else {
         var5 = var3.height / 2;
         var1.setColor(var2.getForeground());
         var1.drawLine(1, var5 - 1, var3.width - 2, var5 - 1);
         var1.setColor(var2.getBackground());
         var1.drawLine(1, var5, var3.width - 2, var5);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      int var2 = 0;
      Font var3 = var1.getFont();
      if (var3 != null) {
         var2 = var1.getFontMetrics(var3).getHeight();
      }

      return new Dimension(0, var2 / 2 + 2);
   }
}
