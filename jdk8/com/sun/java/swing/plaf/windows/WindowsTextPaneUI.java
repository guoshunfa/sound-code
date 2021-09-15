package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Caret;

public class WindowsTextPaneUI extends BasicTextPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTextPaneUI();
   }

   protected Caret createCaret() {
      return new WindowsTextUI.WindowsCaret();
   }
}
