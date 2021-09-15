package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class MotifMenuItemUI extends BasicMenuItemUI {
   protected ChangeListener changeListener;

   public static ComponentUI createUI(JComponent var0) {
      return new MotifMenuItemUI();
   }

   protected void installListeners() {
      super.installListeners();
      this.changeListener = this.createChangeListener(this.menuItem);
      this.menuItem.addChangeListener(this.changeListener);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.menuItem.removeChangeListener(this.changeListener);
   }

   protected ChangeListener createChangeListener(JComponent var1) {
      return new MotifMenuItemUI.ChangeHandler();
   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return new MotifMenuItemUI.MouseInputHandler();
   }

   protected class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         var2.setSelectedPath(MotifMenuItemUI.this.getPath());
      }

      public void mouseReleased(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         JMenuItem var3 = (JMenuItem)var1.getComponent();
         Point var4 = var1.getPoint();
         if (var4.x >= 0 && var4.x < var3.getWidth() && var4.y >= 0 && var4.y < var3.getHeight()) {
            var2.clearSelectedPath();
            var3.doClick(0);
         } else {
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

   protected class ChangeHandler implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         JMenuItem var2 = (JMenuItem)var1.getSource();
         LookAndFeel.installProperty(var2, "borderPainted", var2.isArmed() || var2.isSelected());
      }
   }
}
