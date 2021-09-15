package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Caret;

public class MotifTextPaneUI extends BasicTextPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifTextPaneUI();
   }

   protected Caret createCaret() {
      return MotifTextUI.createCaret();
   }
}
