package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class WindowsInternalFrameUI extends BasicInternalFrameUI {
   XPStyle xp = XPStyle.getXP();

   public void installDefaults() {
      super.installDefaults();
      if (this.xp != null) {
         this.frame.setBorder(new WindowsInternalFrameUI.XPBorder());
      } else {
         this.frame.setBorder(UIManager.getBorder("InternalFrame.border"));
      }

   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      LookAndFeel.installProperty(var1, "opaque", this.xp == null ? Boolean.TRUE : Boolean.FALSE);
   }

   public void uninstallDefaults() {
      this.frame.setBorder((Border)null);
      super.uninstallDefaults();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsInternalFrameUI((JInternalFrame)var0);
   }

   public WindowsInternalFrameUI(JInternalFrame var1) {
      super(var1);
   }

   protected DesktopManager createDesktopManager() {
      return new WindowsDesktopManager();
   }

   protected JComponent createNorthPane(JInternalFrame var1) {
      this.titlePane = new WindowsInternalFrameTitlePane(var1);
      return this.titlePane;
   }

   private class XPBorder extends AbstractBorder {
      private XPStyle.Skin leftSkin;
      private XPStyle.Skin rightSkin;
      private XPStyle.Skin bottomSkin;

      private XPBorder() {
         this.leftSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMELEFT);
         this.rightSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMERIGHT);
         this.bottomSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMEBOTTOM);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         TMSchema.State var7 = ((JInternalFrame)var1).isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
         int var8 = WindowsInternalFrameUI.this.titlePane != null ? WindowsInternalFrameUI.this.titlePane.getSize().height : 0;
         this.bottomSkin.paintSkin(var2, 0, var6 - this.bottomSkin.getHeight(), var5, this.bottomSkin.getHeight(), var7);
         this.leftSkin.paintSkin(var2, 0, var8 - 1, this.leftSkin.getWidth(), var6 - var8 - this.bottomSkin.getHeight() + 2, var7);
         this.rightSkin.paintSkin(var2, var5 - this.rightSkin.getWidth(), var8 - 1, this.rightSkin.getWidth(), var6 - var8 - this.bottomSkin.getHeight() + 2, var7);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.top = 4;
         var2.left = this.leftSkin.getWidth();
         var2.right = this.rightSkin.getWidth();
         var2.bottom = this.bottomSkin.getHeight();
         return var2;
      }

      public boolean isBorderOpaque() {
         return true;
      }

      // $FF: synthetic method
      XPBorder(Object var2) {
         this();
      }
   }
}
