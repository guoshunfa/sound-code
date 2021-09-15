package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

public class WindowsPasswordFieldUI extends BasicPasswordFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsPasswordFieldUI();
   }

   protected Caret createCaret() {
      return new WindowsTextUI.WindowsCaret();
   }
}
