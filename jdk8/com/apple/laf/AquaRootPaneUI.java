package com.apple.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class AquaRootPaneUI extends BasicRootPaneUI implements AncestorListener, WindowListener, ContainerListener {
   private static final AquaUtils.RecyclableSingleton<AquaRootPaneUI> sRootPaneUI = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaRootPaneUI.class);
   static final int kDefaultButtonPaintDelayBetweenFrames = 50;
   JButton fCurrentDefaultButton = null;
   Timer fTimer = null;
   static final boolean sUseScreenMenuBar = AquaMenuBarUI.getScreenMenuBarProperty();

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)sRootPaneUI.get();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      var1.addAncestorListener(this);
      if (var1.isShowing() && var1.isEnabled()) {
         this.updateDefaultButton((JRootPane)var1);
      }

      Container var2 = var1.getParent();
      if (var2 != null && var2 instanceof JFrame) {
         JFrame var3 = (JFrame)var2;
         Color var4 = var3.getBackground();
         if (var4 == null || var4 instanceof UIResource) {
            var3.setBackground(UIManager.getColor("Panel.background"));
         }
      }

      if (sUseScreenMenuBar) {
         JRootPane var5 = (JRootPane)var1;
         var5.addContainerListener(this);
         var5.getLayeredPane().addContainerListener(this);
      }

   }

   public void uninstallUI(JComponent var1) {
      this.stopTimer();
      var1.removeAncestorListener(this);
      if (sUseScreenMenuBar) {
         JRootPane var2 = (JRootPane)var1;
         var2.removeContainerListener(this);
         var2.getLayeredPane().removeContainerListener(this);
      }

      super.uninstallUI(var1);
   }

   public void componentAdded(ContainerEvent var1) {
      if (var1.getContainer() instanceof JRootPane) {
         JRootPane var2 = (JRootPane)var1.getContainer();
         if (var1.getChild() == var2.getLayeredPane()) {
            JLayeredPane var3 = var2.getLayeredPane();
            var3.addContainerListener(this);
         }
      } else if (var1.getChild() instanceof JMenuBar) {
         JMenuBar var5 = (JMenuBar)var1.getChild();
         MenuBarUI var6 = var5.getUI();
         if (var6 instanceof AquaMenuBarUI) {
            Window var4 = SwingUtilities.getWindowAncestor(var5);
            if (var4 != null && var4 instanceof JFrame) {
               ((AquaMenuBarUI)var6).setScreenMenuBar((JFrame)var4);
            }
         }
      }

   }

   public void componentRemoved(ContainerEvent var1) {
      if (var1.getContainer() instanceof JRootPane) {
         JRootPane var2 = (JRootPane)var1.getContainer();
         if (var1.getChild() == var2.getLayeredPane()) {
            JLayeredPane var3 = var2.getLayeredPane();
            var3.removeContainerListener(this);
         }
      } else if (var1.getChild() instanceof JMenuBar) {
         JMenuBar var5 = (JMenuBar)var1.getChild();
         MenuBarUI var6 = var5.getUI();
         if (var6 instanceof AquaMenuBarUI) {
            Window var4 = SwingUtilities.getWindowAncestor(var5);
            if (var4 != null && var4 instanceof JFrame) {
               ((AquaMenuBarUI)var6).clearScreenMenuBar((JFrame)var4);
            }
         }
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      String var2 = var1.getPropertyName();
      JRootPane var3;
      if (!"defaultButton".equals(var2) && !"temporaryDefaultButton".equals(var2)) {
         if ("enabled".equals(var2) || "Frame.active".equals(var2)) {
            var3 = (JRootPane)var1.getSource();
            if (var3.isShowing()) {
               if ((Boolean)var1.getNewValue()) {
                  this.updateDefaultButton((JRootPane)var1.getSource());
               } else {
                  this.stopTimer();
               }
            }
         }
      } else {
         var3 = (JRootPane)var1.getSource();
         if (var3.isShowing() && var3.isEnabled()) {
            this.updateDefaultButton(var3);
         }
      }

   }

   synchronized void stopTimer() {
      if (this.fTimer != null) {
         this.fTimer.stop();
         this.fTimer = null;
      }

   }

   synchronized void updateDefaultButton(JRootPane var1) {
      JButton var2 = var1.getDefaultButton();
      this.fCurrentDefaultButton = var2;
      this.stopTimer();
      if (var2 != null) {
         this.fTimer = new Timer(50, new AquaRootPaneUI.DefaultButtonPainter(var1));
         this.fTimer.start();
      }

   }

   public void ancestorAdded(AncestorEvent var1) {
      JComponent var2 = var1.getComponent();
      Window var3 = SwingUtilities.getWindowAncestor(var2);
      if (var3 != null) {
         var3.removeWindowListener(this);
         var3.addWindowListener(this);
      }

      JComponent var4 = var1.getComponent();
      if (var4 instanceof JRootPane) {
         JRootPane var5 = (JRootPane)var4;
         if (var5.isEnabled() && var5.getDefaultButton() != null) {
            this.updateDefaultButton((JRootPane)var4);
         }
      }

   }

   public void ancestorRemoved(AncestorEvent var1) {
   }

   public void ancestorMoved(AncestorEvent var1) {
   }

   public void windowActivated(WindowEvent var1) {
      updateComponentTreeUIActivation((Component)var1.getSource(), Boolean.TRUE);
   }

   public void windowDeactivated(WindowEvent var1) {
      updateComponentTreeUIActivation((Component)var1.getSource(), Boolean.FALSE);
   }

   public void windowOpened(WindowEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
   }

   public void windowClosed(WindowEvent var1) {
      Window var2 = var1.getWindow();
      var2.removeWindowListener(this);
   }

   public void windowIconified(WindowEvent var1) {
   }

   public void windowDeiconified(WindowEvent var1) {
   }

   public void windowStateChanged(WindowEvent var1) {
   }

   public void windowGainedFocus(WindowEvent var1) {
   }

   public void windowLostFocus(WindowEvent var1) {
   }

   private static void updateComponentTreeUIActivation(Component var0, Object var1) {
      if (var0 instanceof JInternalFrame) {
         var1 = ((JInternalFrame)var0).isSelected() ? Boolean.TRUE : Boolean.FALSE;
      }

      if (var0 instanceof JComponent) {
         ((JComponent)var0).putClientProperty("Frame.active", var1);
      }

      Component[] var2 = null;
      if (var0 instanceof JMenu) {
         var2 = ((JMenu)var0).getMenuComponents();
      } else if (var0 instanceof Container) {
         var2 = ((Container)var0).getComponents();
      }

      if (var2 != null) {
         Component[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Component var6 = var3[var5];
            updateComponentTreeUIActivation(var6, var1);
         }

      }
   }

   public final void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         AquaUtils.fillRect(var1, var2);
      }

      this.paint(var1, var2);
   }

   class DefaultButtonPainter implements ActionListener {
      JRootPane root;

      public DefaultButtonPainter(JRootPane var2) {
         this.root = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JButton var2 = this.root.getDefaultButton();
         if (var2 != null && var2.isShowing()) {
            if (var2.isEnabled()) {
               var2.repaint();
            }
         } else {
            AquaRootPaneUI.this.stopTimer();
         }

      }
   }
}
