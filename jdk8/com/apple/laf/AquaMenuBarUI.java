package com.apple.laf;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MenuBar;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.basic.BasicMenuBarUI;
import sun.lwawt.macosx.LWCToolkit;
import sun.security.action.GetBooleanAction;

public class AquaMenuBarUI extends BasicMenuBarUI implements ScreenMenuBarProvider {
   ScreenMenuBar fScreenMenuBar;
   boolean useScreenMenuBar = getScreenMenuBarProperty();

   public void uninstallUI(JComponent var1) {
      if (this.fScreenMenuBar != null) {
         JFrame var2 = (JFrame)((JFrame)var1.getTopLevelAncestor());
         if (var2.getMenuBar() == this.fScreenMenuBar) {
            var2.setMenuBar((MenuBar)null);
         }

         this.fScreenMenuBar = null;
      }

      super.uninstallUI(var1);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaMenuBarUI();
   }

   protected void installKeyboardActions() {
      if (!this.useScreenMenuBar) {
         super.installKeyboardActions();
      }

   }

   protected void uninstallKeyboardActions() {
      if (!this.useScreenMenuBar) {
         super.uninstallKeyboardActions();
      }

   }

   public void paint(Graphics var1, JComponent var2) {
      AquaMenuPainter.instance().paintMenuBarBackground(var1, var2.getWidth(), var2.getHeight(), var2);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return isScreenMenuBar((JMenuBar)var1) && this.setScreenMenuBar((JFrame)((JFrame)var1.getTopLevelAncestor())) ? new Dimension(0, 0) : null;
   }

   void clearScreenMenuBar(JFrame var1) {
      if (this.useScreenMenuBar) {
         var1.setMenuBar((MenuBar)null);
      }

   }

   boolean setScreenMenuBar(JFrame var1) {
      if (this.useScreenMenuBar) {
         try {
            this.getScreenMenuBar();
         } catch (Throwable var3) {
            return false;
         }

         var1.setMenuBar(this.fScreenMenuBar);
      }

      return true;
   }

   public ScreenMenuBar getScreenMenuBar() {
      synchronized(this) {
         if (this.fScreenMenuBar == null) {
            this.fScreenMenuBar = new ScreenMenuBar(this.menuBar);
         }
      }

      return this.fScreenMenuBar;
   }

   public static final boolean isScreenMenuBar(JMenuBar var0) {
      MenuBarUI var1 = var0.getUI();
      if (var1 instanceof AquaMenuBarUI) {
         if (!((AquaMenuBarUI)var1).useScreenMenuBar) {
            return false;
         }

         Container var2 = var0.getTopLevelAncestor();
         if (var2 instanceof JFrame) {
            MenuBar var3 = ((JFrame)var2).getMenuBar();
            boolean var4 = ((JFrame)var2).getJMenuBar() == var0;
            if (var3 == null) {
               return var4;
            }

            return var3 instanceof ScreenMenuBar && var4;
         }
      }

      return false;
   }

   static boolean getScreenMenuBarProperty() {
      if (LWCToolkit.isEmbedded()) {
         return false;
      } else if ((Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("apple.laf.useScreenMenuBar")))) {
         return true;
      } else if ((Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("com.apple.macos.useScreenMenuBar")))) {
         System.err.println("com.apple.macos.useScreenMenuBar has been deprecated. Please switch to apple.laf.useScreenMenuBar");
         return true;
      } else {
         return false;
      }
   }
}
