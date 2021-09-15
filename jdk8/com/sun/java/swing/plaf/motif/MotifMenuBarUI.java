package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class MotifMenuBarUI extends BasicMenuBarUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifMenuBarUI();
   }
}
