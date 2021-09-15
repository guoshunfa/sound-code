package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MotifSplitPaneUI extends BasicSplitPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifSplitPaneUI();
   }

   public BasicSplitPaneDivider createDefaultDivider() {
      return new MotifSplitPaneDivider(this);
   }
}
