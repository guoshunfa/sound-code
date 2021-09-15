package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

public class MotifPasswordFieldUI extends BasicPasswordFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifPasswordFieldUI();
   }

   protected Caret createCaret() {
      return MotifTextUI.createCaret();
   }
}
