package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;

public class WindowsTextAreaUI extends BasicTextAreaUI {
   protected Caret createCaret() {
      return new WindowsTextUI.WindowsCaret();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTextAreaUI();
   }
}
