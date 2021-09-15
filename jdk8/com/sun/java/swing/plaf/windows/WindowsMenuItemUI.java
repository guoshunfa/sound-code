package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import sun.swing.SwingUtilities2;

public class WindowsMenuItemUI extends BasicMenuItemUI {
   final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() {
         return WindowsMenuItemUI.this.menuItem;
      }

      public TMSchema.State getState(JMenuItem var1) {
         return WindowsMenuItemUI.getState(this, var1);
      }

      public TMSchema.Part getPart(JMenuItem var1) {
         return WindowsMenuItemUI.getPart(this, var1);
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsMenuItemUI();
   }

   protected void paintText(Graphics var1, JMenuItem var2, Rectangle var3, String var4) {
      if (isVistaPainting()) {
         paintText(this.accessor, var1, var2, var3, var4);
      } else {
         ButtonModel var5 = var2.getModel();
         Color var6 = var1.getColor();
         if (var5.isEnabled() && (var5.isArmed() || var2 instanceof JMenu && var5.isSelected())) {
            var1.setColor(this.selectionForeground);
         }

         WindowsGraphicsUtils.paintText(var1, var2, var3, var4, 0);
         var1.setColor(var6);
      }
   }

   protected void paintBackground(Graphics var1, JMenuItem var2, Color var3) {
      if (isVistaPainting()) {
         paintBackground(this.accessor, var1, var2, var3);
      } else {
         super.paintBackground(var1, var2, var3);
      }
   }

   static void paintBackground(WindowsMenuItemUIAccessor var0, Graphics var1, JMenuItem var2, Color var3) {
      XPStyle var4 = XPStyle.getXP();

      assert isVistaPainting(var4);

      if (isVistaPainting(var4)) {
         int var5 = var2.getWidth();
         int var6 = var2.getHeight();
         if (var2.isOpaque()) {
            Color var7 = var1.getColor();
            var1.setColor(var2.getBackground());
            var1.fillRect(0, 0, var5, var6);
            var1.setColor(var7);
         }

         TMSchema.Part var9 = var0.getPart(var2);
         XPStyle.Skin var8 = var4.getSkin(var2, var9);
         var8.paintSkin(var1, 0, 0, var5, var6, var0.getState(var2));
      }

   }

   static void paintText(WindowsMenuItemUIAccessor var0, Graphics var1, JMenuItem var2, Rectangle var3, String var4) {
      assert isVistaPainting();

      if (isVistaPainting()) {
         TMSchema.State var5 = var0.getState(var2);
         FontMetrics var6 = SwingUtilities2.getFontMetrics(var2, (Graphics)var1);
         int var7 = var2.getDisplayedMnemonicIndex();
         if (WindowsLookAndFeel.isMnemonicHidden()) {
            var7 = -1;
         }

         WindowsGraphicsUtils.paintXPText(var2, var0.getPart(var2), var5, var1, var3.x, var3.y + var6.getAscent(), var4, var7);
      }

   }

   static TMSchema.State getState(WindowsMenuItemUIAccessor var0, JMenuItem var1) {
      ButtonModel var3 = var1.getModel();
      TMSchema.State var2;
      if (var3.isArmed()) {
         var2 = var3.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
      } else {
         var2 = var3.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
      }

      return var2;
   }

   static TMSchema.Part getPart(WindowsMenuItemUIAccessor var0, JMenuItem var1) {
      return TMSchema.Part.MP_POPUPITEM;
   }

   static boolean isVistaPainting(XPStyle var0) {
      return var0 != null && var0.isSkinDefined((Component)null, TMSchema.Part.MP_POPUPITEM);
   }

   static boolean isVistaPainting() {
      return isVistaPainting(XPStyle.getXP());
   }
}
