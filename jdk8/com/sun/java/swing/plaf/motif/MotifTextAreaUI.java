package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;

public class MotifTextAreaUI extends BasicTextAreaUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifTextAreaUI();
   }

   protected Caret createCaret() {
      return MotifTextUI.createCaret();
   }
}
