package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

public class WindowsCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
   final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() {
         return WindowsCheckBoxMenuItemUI.this.menuItem;
      }

      public TMSchema.State getState(JMenuItem var1) {
         return WindowsMenuItemUI.getState(this, var1);
      }

      public TMSchema.Part getPart(JMenuItem var1) {
         return WindowsMenuItemUI.getPart(this, var1);
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsCheckBoxMenuItemUI();
   }

   protected void paintBackground(Graphics var1, JMenuItem var2, Color var3) {
      if (WindowsMenuItemUI.isVistaPainting()) {
         WindowsMenuItemUI.paintBackground(this.accessor, var1, var2, var3);
      } else {
         super.paintBackground(var1, var2, var3);
      }
   }

   protected void paintText(Graphics var1, JMenuItem var2, Rectangle var3, String var4) {
      if (WindowsMenuItemUI.isVistaPainting()) {
         WindowsMenuItemUI.paintText(this.accessor, var1, var2, var3, var4);
      } else {
         ButtonModel var5 = var2.getModel();
         Color var6 = var1.getColor();
         if (var5.isEnabled() && var5.isArmed()) {
            var1.setColor(this.selectionForeground);
         }

         WindowsGraphicsUtils.paintText(var1, var2, var3, var4, 0);
         var1.setColor(var6);
      }
   }
}
