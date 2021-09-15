package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseMotionListener implements MouseMotionListener {
   public void mouseDragged(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }

   public void mouseMoved(MouseEvent var1) {
      MenuSelectionManager.defaultManager().processMouseEvent(var1);
   }
}
