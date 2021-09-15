package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Caret;

public class MotifEditorPaneUI extends BasicEditorPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifEditorPaneUI();
   }

   protected Caret createCaret() {
      return MotifTextUI.createCaret();
   }
}
