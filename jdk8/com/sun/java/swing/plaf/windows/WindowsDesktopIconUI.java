package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class WindowsDesktopIconUI extends BasicDesktopIconUI {
   private int width;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsDesktopIconUI();
   }

   public void installDefaults() {
      super.installDefaults();
      this.width = UIManager.getInt("DesktopIcon.width");
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      var1.setOpaque(XPStyle.getXP() == null);
   }

   public void uninstallUI(JComponent var1) {
      WindowsInternalFrameTitlePane var2 = (WindowsInternalFrameTitlePane)this.iconPane;
      super.uninstallUI(var1);
      var2.uninstallListeners();
   }

   protected void installComponents() {
      this.iconPane = new WindowsInternalFrameTitlePane(this.frame);
      this.desktopIcon.setLayout(new BorderLayout());
      this.desktopIcon.add(this.iconPane, "Center");
      if (XPStyle.getXP() != null) {
         this.desktopIcon.setBorder((Border)null);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = super.getMinimumSize(var1);
      var2.width = this.width;
      return var2;
   }
}
