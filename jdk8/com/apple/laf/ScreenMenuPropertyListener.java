package com.apple.laf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.AccessibleState;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

class ScreenMenuPropertyListener implements PropertyChangeListener {
   ScreenMenuPropertyHandler fMenu;

   ScreenMenuPropertyListener(ScreenMenuPropertyHandler var1) {
      this.fMenu = var1;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if ("enabled".equals(var2)) {
         this.fMenu.setEnabled((Boolean)var1.getNewValue());
      } else if (!"AccessibleState".equals(var2)) {
         if ("accelerator".equals(var2)) {
            this.fMenu.setAccelerator((KeyStroke)var1.getNewValue());
         } else if ("text".equals(var2)) {
            this.fMenu.setLabel((String)var1.getNewValue());
         } else if ("icon".equals(var2)) {
            this.fMenu.setIcon((Icon)var1.getNewValue());
         } else if ("ToolTipText".equals(var2)) {
            this.fMenu.setToolTipText((String)var1.getNewValue());
         } else if ("JMenuItem.selectedState".equals(var2)) {
            this.fMenu.setIndeterminate(AquaMenuItemUI.IndeterminateListener.isIndeterminate((JMenuItem)var1.getSource()));
         }
      } else {
         if (var1.getNewValue() == AccessibleState.ENABLED || var1.getOldValue() == AccessibleState.ENABLED) {
            Object var3 = var1.getNewValue();
            this.fMenu.setEnabled(var3 == AccessibleState.ENABLED);
         }

      }
   }
}
