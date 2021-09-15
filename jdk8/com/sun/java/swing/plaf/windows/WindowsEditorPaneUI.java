package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Caret;

public class WindowsEditorPaneUI extends BasicEditorPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsEditorPaneUI();
   }

   protected Caret createCaret() {
      return new WindowsTextUI.WindowsCaret();
   }
}
