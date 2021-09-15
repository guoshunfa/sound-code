package com.apple.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class AquaMenuUI extends BasicMenuUI implements AquaMenuPainter.Client {
   public static ComponentUI createUI(JComponent var0) {
      return new AquaMenuUI();
   }

   protected ChangeListener createChangeListener(JComponent var1) {
      return new BasicMenuUI.ChangeHandler((JMenu)var1, this);
   }

   protected void installDefaults() {
      super.installDefaults();
      ((JMenu)this.menuItem).setDelay(133);
   }

   protected void paintMenuItem(Graphics var1, JComponent var2, Icon var3, Icon var4, Color var5, Color var6, int var7) {
      AquaMenuPainter.instance().paintMenuItem(this, var1, var2, var3, var4, var5, var6, this.disabledForeground, this.selectionForeground, var7, this.acceleratorFont);
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4) {
      Dimension var5 = AquaMenuPainter.instance().getPreferredMenuItemSize(var1, var2, var3, var4, this.acceleratorFont);
      if (var1.getParent() instanceof JMenuBar) {
         var5.height = Math.max(var5.height, 21);
      }

      return var5;
   }

   public void paintBackground(Graphics var1, JComponent var2, int var3, int var4) {
      Container var5 = var2.getParent();
      boolean var6 = var5 instanceof JMenuBar;
      ButtonModel var7 = ((JMenuItem)var2).getModel();
      if (!var7.isArmed() && !var7.isSelected()) {
         if (var6) {
            AquaMenuPainter.instance().paintMenuBarBackground(var1, var3, var4, var2);
         } else {
            var1.setColor(var2.getBackground());
            var1.fillRect(0, 0, var3, var4);
         }
      } else if (var6) {
         AquaMenuPainter.instance().paintSelectedMenuTitleBackground(var1, var3, var4);
      } else {
         AquaMenuPainter.instance().paintSelectedMenuItemBackground(var1, var3, var4);
      }

   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return new AquaMenuUI.AquaMouseInputHandler();
   }

   protected MenuDragMouseListener createMenuDragMouseListener(JComponent var1) {
      return new AquaMenuUI.MenuDragMouseHandler();
   }

   static void appendPath(MenuElement[] var0, MenuElement var1) {
      MenuElement[] var2 = new MenuElement[var0.length + 1];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      var2[var0.length] = var1;
      MenuSelectionManager.defaultManager().setSelectedPath(var2);
   }

   protected class AquaMouseInputHandler extends BasicMenuUI.MouseInputHandler {
      protected AquaMouseInputHandler() {
         super();
      }

      public void mouseEntered(MouseEvent var1) {
         JMenu var2 = (JMenu)AquaMenuUI.this.menuItem;
         if (var2.isEnabled()) {
            MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
            MenuElement[] var4 = var3.getSelectedPath();
            if ((var4.length <= 0 || var4[var4.length - 1] != var2.getPopupMenu()) && (!var2.isTopLevelMenu() || var4.length > 0 && var4[0] == var2.getParent())) {
               if (var2.getDelay() == 0) {
                  AquaMenuUI.appendPath(AquaMenuUI.this.getPath(), var2.getPopupMenu());
               } else {
                  var3.setSelectedPath(AquaMenuUI.this.getPath());
                  AquaMenuUI.this.setupPostTimer(var2);
               }
            }

         }
      }
   }

   class MenuDragMouseHandler implements MenuDragMouseListener {
      public void menuDragMouseDragged(MenuDragMouseEvent var1) {
         if (AquaMenuUI.this.menuItem.isEnabled()) {
            MenuSelectionManager var2 = var1.getMenuSelectionManager();
            MenuElement[] var3 = var1.getPath();
            Point var4 = var1.getPoint();
            if (var4.x >= 0 && var4.x < AquaMenuUI.this.menuItem.getWidth() && var4.y >= 0 && var4.y < AquaMenuUI.this.menuItem.getHeight()) {
               JMenu var7 = (JMenu)AquaMenuUI.this.menuItem;
               MenuElement[] var6 = var2.getSelectedPath();
               if (var6.length <= 0 || var6[var6.length - 1] != var7.getPopupMenu()) {
                  if (var7.getDelay() == 0) {
                     AquaMenuUI.appendPath(var3, var7.getPopupMenu());
                  } else {
                     var2.setSelectedPath(var3);
                     AquaMenuUI.this.setupPostTimer(var7);
                  }
               }
            } else if (var1.getID() == 502) {
               Component var5 = var2.componentForPoint(var1.getComponent(), var1.getPoint());
               if (var5 == null) {
                  var2.clearSelectedPath();
               }
            }

         }
      }

      public void menuDragMouseEntered(MenuDragMouseEvent var1) {
      }

      public void menuDragMouseExited(MenuDragMouseEvent var1) {
      }

      public void menuDragMouseReleased(MenuDragMouseEvent var1) {
      }
   }
}
