package com.apple.laf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class AquaMenuItemUI extends BasicMenuItemUI implements AquaMenuPainter.Client {
   static final int kPlain = 0;
   static final int kCheckBox = 1;
   static final int kRadioButton = 2;
   static final String[] sPropertyPrefixes = new String[]{"MenuItem", "CheckBoxMenuItem", "RadioButtonMenuItem"};
   boolean fIsScreenMenuItem = false;
   boolean fIsIndeterminate = false;
   int fType;
   static final AquaMenuItemUI.IndeterminateListener INDETERMINATE_LISTENER = new AquaMenuItemUI.IndeterminateListener();

   AquaMenuItemUI(int var1) {
      this.fType = var1;
   }

   public static ComponentUI createUI(JComponent var0) {
      byte var1 = 0;
      if (var0 instanceof JCheckBoxMenuItem) {
         var1 = 1;
      }

      if (var0 instanceof JRadioButtonMenuItem) {
         var1 = 2;
      }

      return new AquaMenuItemUI(var1);
   }

   protected String getPropertyPrefix() {
      return sPropertyPrefixes[this.fType];
   }

   protected void installListeners() {
      super.installListeners();
      AquaMenuItemUI.IndeterminateListener.install(this.menuItem);
   }

   protected void uninstallListeners() {
      AquaMenuItemUI.IndeterminateListener.uninstall(this.menuItem);
      super.uninstallListeners();
   }

   public void updateListenersForScreenMenuItem() {
      this.setIsScreenMenu(true);
   }

   protected void setIsScreenMenu(boolean var1) {
      if (this.fIsScreenMenuItem != var1) {
         this.fIsScreenMenuItem = var1;
         if (this.fIsScreenMenuItem) {
            this.removeListeners();
         } else {
            this.addListeners();
         }
      }

   }

   protected void removeListeners() {
      this.menuItem.removeMouseListener(this.mouseInputListener);
      this.menuItem.removeMouseMotionListener(this.mouseInputListener);
      this.menuItem.removeMenuDragMouseListener(this.menuDragMouseListener);
   }

   protected void addListeners() {
      this.menuItem.addMouseListener(this.mouseInputListener);
      this.menuItem.addMouseMotionListener(this.mouseInputListener);
      this.menuItem.addMenuDragMouseListener(this.menuDragMouseListener);
   }

   protected void paintMenuItem(Graphics var1, JComponent var2, Icon var3, Icon var4, Color var5, Color var6, int var7) {
      AquaMenuPainter.instance().paintMenuItem(this, var1, var2, var3, var4, var5, var6, this.disabledForeground, this.selectionForeground, var7, this.acceleratorFont);
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4) {
      return AquaMenuPainter.instance().getPreferredMenuItemSize(var1, var2, var3, var4, this.acceleratorFont);
   }

   public void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         Color var3 = var1.getColor();
         var1.setColor(var2.getBackground());
         var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
         var1.setColor(var3);
      }

      this.paint(var1, var2);
   }

   public void paintBackground(Graphics var1, JComponent var2, int var3, int var4) {
      if (!(var2.getParent() instanceof JMenuBar)) {
         Color var5 = var1.getColor();
         var1.setColor(var2.getBackground());
         var1.fillRect(0, 0, var3, var4);
         if (((JMenuItem)var2).isBorderPainted()) {
            if (((JMenuItem)var2).getModel().isArmed()) {
               AquaMenuPainter.instance().paintSelectedMenuItemBackground(var1, var3, var4);
            }
         } else if (((JMenuItem)var2).getModel().isArmed()) {
            Color var6 = var1.getColor();
            var1.setColor(Color.black);
            var1.fillRect(0, 0, var3, var4);
            var1.setColor(var6);
         } else {
            var1.setColor(Color.green);
            var1.fillRect(0, 0, var3, var4);
         }

         var1.setColor(var5);
      }
   }

   protected void doClick(MenuSelectionManager var1) {
      final Dimension var2 = this.menuItem.getSize();
      AquaUtils.blinkMenu(new AquaUtils.Selectable() {
         public void paintSelected(boolean var1) {
            AquaMenuItemUI.this.menuItem.setArmed(var1);
            AquaMenuItemUI.this.menuItem.paintImmediately(0, 0, var2.width, var2.height);
         }
      });
      super.doClick(var1);
   }

   static class IndeterminateListener implements PropertyChangeListener {
      static final String CLIENT_PROPERTY_KEY = "JMenuItem.selectedState";

      static void install(JMenuItem var0) {
         var0.addPropertyChangeListener("JMenuItem.selectedState", AquaMenuItemUI.INDETERMINATE_LISTENER);
         apply(var0, var0.getClientProperty("JMenuItem.selectedState"));
      }

      static void uninstall(JMenuItem var0) {
         var0.removePropertyChangeListener("JMenuItem.selectedState", AquaMenuItemUI.INDETERMINATE_LISTENER);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("JMenuItem.selectedState".equalsIgnoreCase(var2)) {
            Object var3 = var1.getSource();
            if (var3 instanceof JMenuItem) {
               JMenuItem var4 = (JMenuItem)var3;
               apply(var4, var1.getNewValue());
            }
         }
      }

      static void apply(JMenuItem var0, Object var1) {
         ButtonUI var2 = var0.getUI();
         if (var2 instanceof AquaMenuItemUI) {
            AquaMenuItemUI var3 = (AquaMenuItemUI)var2;
            if (var3.fIsIndeterminate = "indeterminate".equals(var1)) {
               var3.checkIcon = UIManager.getIcon(var3.getPropertyPrefix() + ".dashIcon");
            } else {
               var3.checkIcon = UIManager.getIcon(var3.getPropertyPrefix() + ".checkIcon");
            }

         }
      }

      public static boolean isIndeterminate(JMenuItem var0) {
         return "indeterminate".equals(var0.getClientProperty("JMenuItem.selectedState"));
      }
   }
}
