package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class WindowsPopupMenuUI extends BasicPopupMenuUI {
   static WindowsPopupMenuUI.MnemonicListener mnemonicListener = null;
   static final Object GUTTER_OFFSET_KEY = new StringUIClientPropertyKey("GUTTER_OFFSET_KEY");

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsPopupMenuUI();
   }

   public void installListeners() {
      super.installListeners();
      if (!UIManager.getBoolean("Button.showMnemonics") && mnemonicListener == null) {
         mnemonicListener = new WindowsPopupMenuUI.MnemonicListener();
         MenuSelectionManager.defaultManager().addChangeListener(mnemonicListener);
      }

   }

   public Popup getPopup(JPopupMenu var1, int var2, int var3) {
      PopupFactory var4 = PopupFactory.getSharedInstance();
      return var4.getPopup(var1.getInvoker(), var1, var2, var3);
   }

   static int getTextOffset(JComponent var0) {
      int var1 = -1;
      Object var2 = var0.getClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET);
      if (var2 instanceof Integer) {
         var1 = (Integer)var2;
         int var3 = 0;
         Component var4 = var0.getComponent(0);
         if (var4 != null) {
            var3 = var4.getX();
         }

         var1 += var3;
      }

      return var1;
   }

   static int getSpanBeforeGutter() {
      return 3;
   }

   static int getSpanAfterGutter() {
      return 3;
   }

   static int getGutterWidth() {
      int var0 = 2;
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null) {
         XPStyle.Skin var2 = var1.getSkin((Component)null, TMSchema.Part.MP_POPUPGUTTER);
         var0 = var2.getWidth();
      }

      return var0;
   }

   private static boolean isLeftToRight(JComponent var0) {
      boolean var1 = true;

      for(int var2 = var0.getComponentCount() - 1; var2 >= 0 && var1; --var2) {
         var1 = var0.getComponent(var2).getComponentOrientation().isLeftToRight();
      }

      return var1;
   }

   public void paint(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(var3)) {
         XPStyle.Skin var4 = var3.getSkin(var2, TMSchema.Part.MP_POPUPBACKGROUND);
         var4.paintSkin(var1, 0, 0, var2.getWidth(), var2.getHeight(), TMSchema.State.NORMAL);
         int var5 = getTextOffset(var2);
         if (var5 >= 0 && isLeftToRight(var2)) {
            var4 = var3.getSkin(var2, TMSchema.Part.MP_POPUPGUTTER);
            int var6 = getGutterWidth();
            int var7 = var5 - getSpanAfterGutter() - var6;
            var2.putClientProperty(GUTTER_OFFSET_KEY, var7);
            Insets var8 = var2.getInsets();
            var4.paintSkin(var1, var7, var8.top, var6, var2.getHeight() - var8.bottom - var8.top, TMSchema.State.NORMAL);
         } else if (var2.getClientProperty(GUTTER_OFFSET_KEY) != null) {
            var2.putClientProperty(GUTTER_OFFSET_KEY, (Object)null);
         }
      } else {
         super.paint(var1, var2);
      }

   }

   static class MnemonicListener implements ChangeListener {
      JRootPane repaintRoot = null;

      public void stateChanged(ChangeEvent var1) {
         MenuSelectionManager var2 = (MenuSelectionManager)var1.getSource();
         MenuElement[] var3 = var2.getSelectedPath();
         if (var3.length == 0) {
            if (!WindowsLookAndFeel.isMnemonicHidden()) {
               WindowsLookAndFeel.setMnemonicHidden(true);
               if (this.repaintRoot != null) {
                  Window var4 = SwingUtilities.getWindowAncestor(this.repaintRoot);
                  WindowsGraphicsUtils.repaintMnemonicsInWindow(var4);
               }
            }
         } else {
            Component var5 = (Component)var3[0];
            if (var5 instanceof JPopupMenu) {
               var5 = ((JPopupMenu)var5).getInvoker();
            }

            this.repaintRoot = SwingUtilities.getRootPane(var5);
         }

      }
   }
}
