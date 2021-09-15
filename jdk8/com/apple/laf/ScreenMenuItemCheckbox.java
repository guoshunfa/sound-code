package com.apple.laf;

import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.MenuComponentPeer;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.plaf.ButtonUI;
import sun.lwawt.macosx.CCheckboxMenuItem;
import sun.lwawt.macosx.CMenuItem;

final class ScreenMenuItemCheckbox extends CheckboxMenuItem implements ActionListener, ComponentListener, ScreenMenuPropertyHandler, ItemListener {
   JMenuItem fMenuItem;
   MenuContainer fParent;
   ScreenMenuPropertyListener fPropertyListener;

   ScreenMenuItemCheckbox(JCheckBoxMenuItem var1) {
      super(var1.getText(), var1.getState());
      this.init(var1);
   }

   ScreenMenuItemCheckbox(JRadioButtonMenuItem var1) {
      super(var1.getText(), var1.getModel().isSelected());
      this.init(var1);
   }

   public void init(JMenuItem var1) {
      this.fMenuItem = var1;
      this.setEnabled(this.fMenuItem.isEnabled());
   }

   public void addNotify() {
      super.addNotify();
      CCheckboxMenuItem var1 = (CCheckboxMenuItem)this.getPeer();
      var1.setAutoToggle(false);
      this.fMenuItem.addComponentListener(this);
      this.fPropertyListener = new ScreenMenuPropertyListener(this);
      this.fMenuItem.addPropertyChangeListener(this.fPropertyListener);
      this.addActionListener(this);
      this.addItemListener(this);
      this.fMenuItem.addItemListener(this);
      this.setIndeterminate(AquaMenuItemUI.IndeterminateListener.isIndeterminate(this.fMenuItem));
      this.setAccelerator(this.fMenuItem.getAccelerator());
      Icon var2 = this.fMenuItem.getIcon();
      if (var2 != null) {
         this.setIcon(var2);
      }

      String var3 = this.fMenuItem.getToolTipText();
      if (var3 != null) {
         this.setToolTipText(var3);
      }

      this.fMenuItem.addItemListener(this);
      ButtonUI var4 = this.fMenuItem.getUI();
      if (var4 instanceof ScreenMenuItemUI) {
         ((ScreenMenuItemUI)var4).updateListenersForScreenMenuItem();
      }

      if (this.fMenuItem instanceof JCheckBoxMenuItem) {
         this.forceSetState(this.fMenuItem.isSelected());
      } else {
         this.forceSetState(this.fMenuItem.getModel().isSelected());
      }

   }

   public void removeNotify() {
      this.fMenuItem.removeComponentListener(this);
      this.fMenuItem.removePropertyChangeListener(this.fPropertyListener);
      this.fPropertyListener = null;
      this.removeActionListener(this);
      this.removeItemListener(this);
      this.fMenuItem.removeItemListener(this);
      super.removeNotify();
   }

   public synchronized void setLabel(String var1) {
      ScreenMenuItem.syncLabelAndKS(this, var1, this.fMenuItem.getAccelerator());
   }

   public void setAccelerator(KeyStroke var1) {
      ScreenMenuItem.syncLabelAndKS(this, this.fMenuItem.getText(), var1);
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

   public void setToolTipText(String var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CMenuItem) {
         ((CMenuItem)var2).setToolTipText(var1);
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

   public void setVisible(boolean var1) {
      if (this.fParent == null) {
         this.fParent = this.getParent();
      }

      ((ScreenMenuPropertyHandler)this.fParent).setChildVisible(this.fMenuItem, var1);
   }

   public void setChildVisible(JMenuItem var1, boolean var2) {
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getSource() == this) {
         this.fMenuItem.doClick(0);
      } else {
         switch(var1.getStateChange()) {
         case 1:
            this.forceSetState(true);
            break;
         case 2:
            this.forceSetState(false);
         }

      }
   }

   public void setIndeterminate(boolean var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CCheckboxMenuItem) {
         ((CCheckboxMenuItem)var2).setIsIndeterminate(var1);
      }

   }

   public synchronized void setState(boolean var1) {
   }

   private void forceSetState(boolean var1) {
      super.setState(var1);
   }
}
