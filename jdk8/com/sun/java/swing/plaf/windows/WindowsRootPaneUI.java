package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.KeyEventPostProcessor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import javax.swing.plaf.basic.ComboPopup;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class WindowsRootPaneUI extends BasicRootPaneUI {
   private static final WindowsRootPaneUI windowsRootPaneUI = new WindowsRootPaneUI();
   static final WindowsRootPaneUI.AltProcessor altProcessor = new WindowsRootPaneUI.AltProcessor();

   public static ComponentUI createUI(JComponent var0) {
      return windowsRootPaneUI;
   }

   static class AltProcessor implements KeyEventPostProcessor {
      static boolean altKeyPressed = false;
      static boolean menuCanceledOnPress = false;
      static JRootPane root = null;
      static Window winAncestor = null;

      void altPressed(KeyEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         MenuElement[] var3 = var2.getSelectedPath();
         if (var3.length > 0 && !(var3[0] instanceof ComboPopup)) {
            var2.clearSelectedPath();
            menuCanceledOnPress = true;
            var1.consume();
         } else if (var3.length > 0) {
            menuCanceledOnPress = false;
            WindowsLookAndFeel.setMnemonicHidden(false);
            WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
            var1.consume();
         } else {
            menuCanceledOnPress = false;
            WindowsLookAndFeel.setMnemonicHidden(false);
            WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
            JMenuBar var4 = root != null ? root.getJMenuBar() : null;
            if (var4 == null && winAncestor instanceof JFrame) {
               var4 = ((JFrame)winAncestor).getJMenuBar();
            }

            JMenu var5 = var4 != null ? var4.getMenu(0) : null;
            if (var5 != null) {
               var1.consume();
            }
         }

      }

      void altReleased(KeyEvent var1) {
         if (menuCanceledOnPress) {
            WindowsLookAndFeel.setMnemonicHidden(true);
            WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
         } else {
            MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
            if (var2.getSelectedPath().length == 0) {
               JMenuBar var3 = root != null ? root.getJMenuBar() : null;
               if (var3 == null && winAncestor instanceof JFrame) {
                  var3 = ((JFrame)winAncestor).getJMenuBar();
               }

               JMenu var4 = var3 != null ? var3.getMenu(0) : null;
               boolean var5 = false;
               Toolkit var6 = Toolkit.getDefaultToolkit();
               if (var6 instanceof SunToolkit) {
                  Component var7 = AWTAccessor.getKeyEventAccessor().getOriginalSource(var1);
                  var5 = SunToolkit.getContainingWindow(var7) != winAncestor || var1.getWhen() <= ((SunToolkit)var6).getWindowDeactivationTime(winAncestor);
               }

               if (var4 != null && !var5) {
                  MenuElement[] var8 = new MenuElement[]{var3, var4};
                  var2.setSelectedPath(var8);
               } else if (!WindowsLookAndFeel.isMnemonicHidden()) {
                  WindowsLookAndFeel.setMnemonicHidden(true);
                  WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
               }
            } else if (var2.getSelectedPath()[0] instanceof ComboPopup) {
               WindowsLookAndFeel.setMnemonicHidden(true);
               WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
            }

         }
      }

      public boolean postProcessKeyEvent(KeyEvent var1) {
         if (var1.isConsumed() && var1.getKeyCode() != 18) {
            altKeyPressed = false;
            return false;
         } else {
            if (var1.getKeyCode() == 18) {
               root = SwingUtilities.getRootPane(var1.getComponent());
               winAncestor = root == null ? null : SwingUtilities.getWindowAncestor(root);
               if (var1.getID() == 401) {
                  if (!altKeyPressed) {
                     this.altPressed(var1);
                  }

                  altKeyPressed = true;
                  return true;
               }

               if (var1.getID() == 402) {
                  if (altKeyPressed) {
                     this.altReleased(var1);
                  } else {
                     MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
                     MenuElement[] var3 = var2.getSelectedPath();
                     if (var3.length <= 0) {
                        WindowsLookAndFeel.setMnemonicHidden(true);
                        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
                     }
                  }

                  altKeyPressed = false;
               }

               root = null;
               winAncestor = null;
            } else {
               altKeyPressed = false;
            }

            return false;
         }
      }
   }
}
