package com.apple.laf;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class AquaInternalFramePaneUI extends BasicDesktopPaneUI implements MouseListener {
   JComponent fDock;
   AquaInternalFramePaneUI.DockLayoutManager fLayoutMgr;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaInternalFramePaneUI();
   }

   public void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         super.update(var1, var2);
      } else {
         this.paint(var1, var2);
      }
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.fLayoutMgr = new AquaInternalFramePaneUI.DockLayoutManager();
      var1.setLayout(this.fLayoutMgr);
      var1.addMouseListener(this);
   }

   public void uninstallUI(JComponent var1) {
      var1.removeMouseListener(this);
      if (this.fDock != null) {
         var1.remove(this.fDock);
         this.fDock = null;
      }

      if (this.fLayoutMgr != null) {
         var1.setLayout((LayoutManager)null);
         this.fLayoutMgr = null;
      }

      super.uninstallUI(var1);
   }

   protected void installDesktopManager() {
      if (this.desktop.getDesktopManager() == null) {
         this.desktopManager = new AquaInternalFramePaneUI.AquaDockingDesktopManager();
         this.desktop.setDesktopManager(this.desktopManager);
      }

   }

   protected void uninstallDesktopManager() {
      DesktopManager var1 = this.desktop.getDesktopManager();
      if (var1 instanceof AquaInternalFramePaneUI.AquaDockingDesktopManager) {
         this.desktop.setDesktopManager((DesktopManager)null);
      }

   }

   JComponent getDock() {
      if (this.fDock == null) {
         this.fDock = new AquaInternalFramePaneUI.Dock(this.desktop);
         this.desktop.add(this.fDock, new Integer(399));
      }

      return this.fDock;
   }

   public void mousePressed(MouseEvent var1) {
      JInternalFrame var2 = this.desktop.getSelectedFrame();
      if (var2 != null) {
         try {
            var2.setSelected(false);
         } catch (PropertyVetoException var4) {
         }

         this.desktop.getDesktopManager().deactivateFrame(var2);
      }

   }

   public void mouseReleased(MouseEvent var1) {
   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   class AquaDockingDesktopManager extends AquaInternalFrameManager {
      public void openFrame(JInternalFrame var1) {
         JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
         Container var3 = var2.getParent();
         if (var3 != null) {
            if (var3.getParent() != null) {
               var3.getParent().add(var1);
            }

            this.removeIconFor(var1);
         }
      }

      public void deiconifyFrame(JInternalFrame var1) {
         JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
         Container var3 = var2.getParent();
         if (var3 != null) {
            if (var3.getParent() != null) {
               var3.getParent().add(var1);
            }

            this.removeIconFor(var1);
            var1.moveToFront();

            try {
               var1.setSelected(true);
            } catch (PropertyVetoException var5) {
            }

         }
      }

      public void iconifyFrame(JInternalFrame var1) {
         JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
         ((AquaInternalFrameDockIconUI)var2.getUI()).updateIcon();
         super.iconifyFrame(var1);
      }

      void addIcon(Container var1, JInternalFrame.JDesktopIcon var2) {
         DesktopPaneUI var3 = ((JDesktopPane)var1).getUI();
         ((AquaInternalFramePaneUI)var3).getDock().add(var2);
      }
   }

   class Dock extends JComponent implements Border {
      static final int DOCK_EDGE_SLACK = 8;

      Dock(JComponent var2) {
         this.setBorder(this);
         this.setLayout(new FlowLayout(1, 0, 0));
         this.setVisible(false);
      }

      public void removeNotify() {
         AquaInternalFramePaneUI.this.fDock = null;
         super.removeNotify();
      }

      void updateSize() {
         Dimension var1 = this.getPreferredSize();
         this.setBounds((this.getParent().getWidth() - var1.width) / 2, this.getParent().getHeight() - var1.height, var1.width, var1.height);
      }

      public Component add(Component var1) {
         super.add(var1);
         if (!this.isVisible()) {
            this.setVisible(true);
         }

         this.updateSize();
         this.validate();
         return var1;
      }

      public void remove(Component var1) {
         super.remove(var1);
         if (this.getComponentCount() == 0) {
            this.setVisible(false);
         } else {
            this.updateSize();
            this.validate();
         }

      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(2, 8, 0, 8);
      }

      public boolean isBorderOpaque() {
         return false;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var2 instanceof Graphics2D) {
            Graphics2D var7 = (Graphics2D)var2;
            int var8 = this.getHeight();
            int var9 = this.getWidth();
            Object var10 = var7.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            var7.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            var7.setColor(UIManager.getColor("DesktopIcon.borderColor"));
            var7.fillRoundRect(4, 4, var9 - 9, var8 + 8, 8, 8);
            var7.setColor(UIManager.getColor("DesktopIcon.borderRimColor"));
            var7.setStroke(new BasicStroke(2.0F));
            var7.drawRoundRect(4, 4, var9 - 9, var8 + 8, 8, 8);
            if (var10 != null) {
               var7.setRenderingHint(RenderingHints.KEY_ANTIALIASING, var10);
            }

         }
      }
   }

   class DockLayoutManager implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return var1.getSize();
      }

      public Dimension minimumLayoutSize(Container var1) {
         return var1.getSize();
      }

      public void layoutContainer(Container var1) {
         if (AquaInternalFramePaneUI.this.fDock != null) {
            ((AquaInternalFramePaneUI.Dock)AquaInternalFramePaneUI.this.fDock).updateSize();
         }

      }
   }
}
