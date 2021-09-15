package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class WindowsMenuBarUI extends BasicMenuBarUI {
   private WindowListener windowListener = null;
   private HierarchyListener hierarchyListener = null;
   private Window window = null;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsMenuBarUI();
   }

   protected void uninstallListeners() {
      this.uninstallWindowListener();
      if (this.hierarchyListener != null) {
         this.menuBar.removeHierarchyListener(this.hierarchyListener);
         this.hierarchyListener = null;
      }

      super.uninstallListeners();
   }

   private void installWindowListener() {
      if (this.windowListener == null) {
         Container var1 = this.menuBar.getTopLevelAncestor();
         if (var1 instanceof Window) {
            this.window = (Window)var1;
            this.windowListener = new WindowAdapter() {
               public void windowActivated(WindowEvent var1) {
                  WindowsMenuBarUI.this.menuBar.repaint();
               }

               public void windowDeactivated(WindowEvent var1) {
                  WindowsMenuBarUI.this.menuBar.repaint();
               }
            };
            ((Window)var1).addWindowListener(this.windowListener);
         }
      }

   }

   private void uninstallWindowListener() {
      if (this.windowListener != null && this.window != null) {
         this.window.removeWindowListener(this.windowListener);
      }

      this.window = null;
      this.windowListener = null;
   }

   protected void installListeners() {
      if (WindowsLookAndFeel.isOnVista()) {
         this.installWindowListener();
         this.hierarchyListener = new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent var1) {
               if ((var1.getChangeFlags() & 2L) != 0L) {
                  if (WindowsMenuBarUI.this.menuBar.isDisplayable()) {
                     WindowsMenuBarUI.this.installWindowListener();
                  } else {
                     WindowsMenuBarUI.this.uninstallWindowListener();
                  }
               }

            }
         };
         this.menuBar.addHierarchyListener(this.hierarchyListener);
      }

      super.installListeners();
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      Object var1 = SwingUtilities.getUIActionMap(this.menuBar);
      if (var1 == null) {
         var1 = new ActionMapUIResource();
         SwingUtilities.replaceUIActionMap(this.menuBar, (ActionMap)var1);
      }

      ((ActionMap)var1).put("takeFocus", new WindowsMenuBarUI.TakeFocus());
   }

   public void paint(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(var3)) {
         XPStyle.Skin var4 = var3.getSkin(var2, TMSchema.Part.MP_BARBACKGROUND);
         int var5 = var2.getWidth();
         int var6 = var2.getHeight();
         TMSchema.State var7 = isActive(var2) ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
         var4.paintSkin(var1, 0, 0, var5, var6, var7);
      } else {
         super.paint(var1, var2);
      }

   }

   static boolean isActive(JComponent var0) {
      JRootPane var1 = var0.getRootPane();
      if (var1 != null) {
         Container var2 = var1.getParent();
         if (var2 instanceof Window) {
            return ((Window)var2).isActive();
         }
      }

      return true;
   }

   private static class TakeFocus extends AbstractAction {
      private TakeFocus() {
      }

      public void actionPerformed(ActionEvent var1) {
         JMenuBar var2 = (JMenuBar)var1.getSource();
         JMenu var3 = var2.getMenu(0);
         if (var3 != null) {
            MenuSelectionManager var4 = MenuSelectionManager.defaultManager();
            MenuElement[] var5 = new MenuElement[]{var2, var3};
            var4.setSelectedPath(var5);
            WindowsLookAndFeel.setMnemonicHidden(false);
            WindowsLookAndFeel.repaintRootPane(var2);
         }

      }

      // $FF: synthetic method
      TakeFocus(Object var1) {
         this();
      }
   }
}
