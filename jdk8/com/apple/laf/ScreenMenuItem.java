package com.apple.laf;

import java.awt.Image;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.peer.MenuComponentPeer;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.plaf.ButtonUI;
import sun.lwawt.macosx.CMenuItem;

final class ScreenMenuItem extends MenuItem implements ActionListener, ComponentListener, ScreenMenuPropertyHandler {
   ScreenMenuPropertyListener fListener;
   JMenuItem fMenuItem;

   ScreenMenuItem(JMenuItem var1) {
      super(var1.getText());
      this.fMenuItem = var1;
      this.setEnabled(this.fMenuItem.isEnabled());
      ButtonUI var2 = this.fMenuItem.getUI();
      if (var2 instanceof ScreenMenuItemUI) {
         ((ScreenMenuItemUI)var2).updateListenersForScreenMenuItem();
      }

   }

   public void addNotify() {
      super.addNotify();
      this.fMenuItem.addComponentListener(this);
      this.fListener = new ScreenMenuPropertyListener(this);
      this.fMenuItem.addPropertyChangeListener(this.fListener);
      this.addActionListener(this);
      this.setEnabled(this.fMenuItem.isEnabled());
      this.setAccelerator(this.fMenuItem.getAccelerator());
      String var1 = this.fMenuItem.getText();
      if (var1 != null) {
         this.setLabel(var1);
      }

      Icon var2 = this.fMenuItem.getIcon();
      if (var2 != null) {
         this.setIcon(var2);
      }

      String var3 = this.fMenuItem.getToolTipText();
      if (var3 != null) {
         this.setToolTipText(var3);
      }

      if (this.fMenuItem instanceof JRadioButtonMenuItem) {
         ButtonUI var4 = this.fMenuItem.getUI();
         if (var4 instanceof ScreenMenuItemUI) {
            ((ScreenMenuItemUI)var4).updateListenersForScreenMenuItem();
         }
      }

   }

   public void removeNotify() {
      super.removeNotify();
      this.removeActionListener(this);
      this.fMenuItem.removePropertyChangeListener(this.fListener);
      this.fListener = null;
      this.fMenuItem.removeComponentListener(this);
   }

   static void syncLabelAndKS(MenuItem var0, String var1, KeyStroke var2) {
      MenuComponentPeer var3 = var0.getPeer();
      if (var3 instanceof CMenuItem) {
         CMenuItem var4 = (CMenuItem)var3;
         if (var2 == null) {
            var4.setLabel(var1);
         } else {
            var4.setLabel(var1, var2.getKeyChar(), var2.getKeyCode(), var2.getModifiers());
         }

      }
   }

   public synchronized void setLabel(String var1) {
      syncLabelAndKS(this, var1, this.fMenuItem.getAccelerator());
   }

   public void setAccelerator(KeyStroke var1) {
      syncLabelAndKS(this, this.fMenuItem.getText(), var1);
   }

   public void actionPerformed(ActionEvent var1) {
      this.fMenuItem.doClick(0);
   }

   public void componentResized(ComponentEvent var1) {
   }

   public void componentMoved(ComponentEvent var1) {
   }

   public void componentShown(ComponentEvent var1) {
      this.setVisible(true);
   }

   public void componentHidden(ComponentEvent var1) {
      this.setVisible(false);
   }

   public void setVisible(boolean var1) {
      MenuContainer var2 = this.getParent();
      if (var2 != null) {
         ((ScreenMenuPropertyHandler)var2).setChildVisible(this.fMenuItem, var1);
      }

   }

   public void setToolTipText(String var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CMenuItem) {
         CMenuItem var3 = (CMenuItem)var2;
         var3.setToolTipText(var1);
      }
   }

   public void setIcon(Icon var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CMenuItem) {
         CMenuItem var3 = (CMenuItem)var2;
         Image var4 = null;
         if (var1 != null && var1.getIconWidth() > 0 && var1.getIconHeight() > 0) {
            var4 = AquaIcon.getImageForIcon(var1);
         }

         var3.setImage(var4);
      }
   }

   public void setChildVisible(JMenuItem var1, boolean var2) {
   }

   public void setIndeterminate(boolean var1) {
   }
}
