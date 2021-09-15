package com.sun.java.swing.plaf.motif;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class MotifMenuUI extends BasicMenuUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifMenuUI();
   }

   protected ChangeListener createChangeListener(JComponent var1) {
      return new MotifMenuUI.MotifChangeHandler((JMenu)var1, this);
   }

   private boolean popupIsOpen(JMenu var1, MenuElement[] var2) {
      JPopupMenu var4 = var1.getPopupMenu();

      for(int var3 = var2.length - 1; var3 >= 0; --var3) {
         if (var2[var3].getComponent() == var4) {
            return true;
         }
      }

      return false;
   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return new MotifMenuUI.MouseInputHandler();
   }

   protected class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         JMenu var3 = (JMenu)var1.getComponent();
         if (var3.isEnabled()) {
            MenuElement[] var5;
            if (var3.isTopLevelMenu()) {
               if (var3.isSelected()) {
                  var2.clearSelectedPath();
               } else {
                  Container var4 = var3.getParent();
                  if (var4 != null && var4 instanceof JMenuBar) {
                     var5 = new MenuElement[]{(MenuElement)var4, var3};
                     var2.setSelectedPath(var5);
                  }
               }
            }

            MenuElement[] var6 = MotifMenuUI.this.getPath();
            if (var6.length > 0) {
               var5 = new MenuElement[var6.length + 1];
               System.arraycopy(var6, 0, var5, 0, var6.length);
               var5[var6.length] = var3.getPopupMenu();
               var2.setSelectedPath(var5);
            }
         }

      }

      public void mouseReleased(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         JMenuItem var3 = (JMenuItem)var1.getComponent();
         Point var4 = var1.getPoint();
         if (var4.x < 0 || var4.x >= var3.getWidth() || var4.y < 0 || var4.y >= var3.getHeight()) {
            var2.processMouseEvent(var1);
         }

      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseDragged(MouseEvent var1) {
         MenuSelectionManager.defaultManager().processMouseEvent(var1);
      }

      public void mouseMoved(MouseEvent var1) {
      }
   }

   public class MotifChangeHandler extends BasicMenuUI.ChangeHandler {
      public MotifChangeHandler(JMenu var2, MotifMenuUI var3) {
         super(var2, var3);
      }

      public void stateChanged(ChangeEvent var1) {
         JMenuItem var2 = (JMenuItem)var1.getSource();
         if (!var2.isArmed() && !var2.isSelected()) {
            var2.setBorderPainted(false);
         } else {
            var2.setBorderPainted(true);
         }

         super.stateChanged(var1);
      }
   }
}
