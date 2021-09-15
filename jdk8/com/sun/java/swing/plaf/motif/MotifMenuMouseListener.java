package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseListener extends MouseAdapter {
   public void mousePressed(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }

   public void mouseEntered(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }

   public void mouseExited(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }
}
