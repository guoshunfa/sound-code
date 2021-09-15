package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;

public class MotifTextFieldUI extends BasicTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifTextFieldUI();
   }

   protected Caret createCaret() {
      return MotifTextUI.createCaret();
   }
}
