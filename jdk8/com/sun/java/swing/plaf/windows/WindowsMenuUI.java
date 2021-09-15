package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class WindowsMenuUI extends BasicMenuUI {
   protected Integer menuBarHeight;
   protected boolean hotTrackingOn;
   final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() {
         return WindowsMenuUI.this.menuItem;
      }

      public TMSchema.State getState(JMenuItem var1) {
         TMSchema.State var2 = var1.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
         ButtonModel var3 = var1.getModel();
         if (!var3.isArmed() && !var3.isSelected()) {
            if (var3.isRollover() && ((JMenu)var1).isTopLevelMenu()) {
               TMSchema.State var4 = var2;
               var2 = var1.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
               MenuElement[] var5 = ((JMenuBar)var1.getParent()).getSubElements();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  MenuElement var8 = var5[var7];
                  if (((JMenuItem)var8).isSelected()) {
                     var2 = var4;
                     break;
                  }
               }
            }
         } else {
            var2 = var1.isEnabled() ? TMSchema.State.PUSHED : TMSchema.State.DISABLEDPUSHED;
         }

         if (!((JMenu)var1).isTopLevelMenu()) {
            if (var2 == TMSchema.State.PUSHED) {
               var2 = TMSchema.State.HOT;
            } else if (var2 == TMSchema.State.DISABLEDPUSHED) {
               var2 = TMSchema.State.DISABLEDHOT;
            }
         }

         if (((JMenu)var1).isTopLevelMenu() && WindowsMenuItemUI.isVistaPainting() && !WindowsMenuBarUI.isActive(var1)) {
            var2 = TMSchema.State.DISABLED;
         }

         return var2;
      }

      public TMSchema.Part getPart(JMenuItem var1) {
         return ((JMenu)var1).isTopLevelMenu() ? TMSchema.Part.MP_BARITEM : TMSchema.Part.MP_POPUPITEM;
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsMenuUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      if (!WindowsLookAndFeel.isClassicWindows()) {
         this.menuItem.setRolloverEnabled(true);
      }

      this.menuBarHeight = UIManager.getInt("MenuBar.height");
      Object var1 = UIManager.get("MenuBar.rolloverEnabled");
      this.hotTrackingOn = var1 instanceof Boolean ? (Boolean)var1 : true;
   }

   protected void paintBackground(Graphics var1, JMenuItem var2, Color var3) {
      if (WindowsMenuItemUI.isVistaPainting()) {
         WindowsMenuItemUI.paintBackground(this.accessor, var1, var2, var3);
      } else {
         JMenu var4 = (JMenu)var2;
         ButtonModel var5 = var4.getModel();
         if (!WindowsLookAndFeel.isClassicWindows() && var4.isTopLevelMenu() && (XPStyle.getXP() == null || !var5.isArmed() && !var5.isSelected())) {
            Color var6 = var1.getColor();
            int var7 = var4.getWidth();
            int var8 = var4.getHeight();
            UIDefaults var9 = UIManager.getLookAndFeelDefaults();
            Color var10 = var9.getColor("controlLtHighlight");
            Color var11 = var9.getColor("controlShadow");
            var1.setColor(var4.getBackground());
            var1.fillRect(0, 0, var7, var8);
            if (var4.isOpaque()) {
               if (!var5.isArmed() && !var5.isSelected()) {
                  if (var5.isRollover() && var5.isEnabled()) {
                     boolean var12 = false;
                     MenuElement[] var13 = ((JMenuBar)var4.getParent()).getSubElements();

                     for(int var14 = 0; var14 < var13.length; ++var14) {
                        if (((JMenuItem)var13[var14]).isSelected()) {
                           var12 = true;
                           break;
                        }
                     }

                     if (!var12) {
                        if (XPStyle.getXP() != null) {
                           var1.setColor(this.selectionBackground);
                           var1.fillRect(0, 0, var7, var8);
                        } else {
                           var1.setColor(var10);
                           var1.drawLine(0, 0, var7 - 1, 0);
                           var1.drawLine(0, 0, 0, var8 - 2);
                           var1.setColor(var11);
                           var1.drawLine(var7 - 1, 0, var7 - 1, var8 - 2);
                           var1.drawLine(0, var8 - 2, var7 - 1, var8 - 2);
                        }
                     }
                  }
               } else {
                  var1.setColor(var11);
                  var1.drawLine(0, 0, var7 - 1, 0);
                  var1.drawLine(0, 0, 0, var8 - 2);
                  var1.setColor(var10);
                  var1.drawLine(var7 - 1, 0, var7 - 1, var8 - 2);
                  var1.drawLine(0, var8 - 2, var7 - 1, var8 - 2);
               }
            }

            var1.setColor(var6);
         } else {
            super.paintBackground(var1, var4, var3);
         }
      }
   }

   protected void paintText(Graphics var1, JMenuItem var2, Rectangle var3, String var4) {
      if (WindowsMenuItemUI.isVistaPainting()) {
         WindowsMenuItemUI.paintText(this.accessor, var1, var2, var3, var4);
      } else {
         JMenu var5 = (JMenu)var2;
         ButtonModel var6 = var2.getModel();
         Color var7 = var1.getColor();
         boolean var8 = var6.isRollover();
         if (var8 && var5.isTopLevelMenu()) {
            MenuElement[] var9 = ((JMenuBar)var5.getParent()).getSubElements();

            for(int var10 = 0; var10 < var9.length; ++var10) {
               if (((JMenuItem)var9[var10]).isSelected()) {
                  var8 = false;
                  break;
               }
            }
         }

         if (var6.isSelected() && (WindowsLookAndFeel.isClassicWindows() || !var5.isTopLevelMenu()) || XPStyle.getXP() != null && (var8 || var6.isArmed() || var6.isSelected())) {
            var1.setColor(this.selectionForeground);
         }

         WindowsGraphicsUtils.paintText(var1, var2, var3, var4, 0);
         var1.setColor(var7);
      }
   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return new WindowsMenuUI.WindowsMouseInputHandler();
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4) {
      Dimension var5 = super.getPreferredMenuItemSize(var1, var2, var3, var4);
      if (var1 instanceof JMenu && ((JMenu)var1).isTopLevelMenu() && this.menuBarHeight != null && var5.height < this.menuBarHeight) {
         var5.height = this.menuBarHeight;
      }

      return var5;
   }

   protected class WindowsMouseInputHandler extends BasicMenuUI.MouseInputHandler {
      protected WindowsMouseInputHandler() {
         super();
      }

      public void mouseEntered(MouseEvent var1) {
         super.mouseEntered(var1);
         JMenu var2 = (JMenu)var1.getSource();
         if (WindowsMenuUI.this.hotTrackingOn && var2.isTopLevelMenu() && var2.isRolloverEnabled()) {
            var2.getModel().setRollover(true);
            WindowsMenuUI.this.menuItem.repaint();
         }

      }

      public void mouseExited(MouseEvent var1) {
         super.mouseExited(var1);
         JMenu var2 = (JMenu)var1.getSource();
         ButtonModel var3 = var2.getModel();
         if (var2.isRolloverEnabled()) {
            var3.setRollover(false);
            WindowsMenuUI.this.menuItem.repaint();
         }

      }
   }
}
