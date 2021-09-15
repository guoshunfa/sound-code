package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneUI extends BasicSplitPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsSplitPaneUI();
   }

   public BasicSplitPaneDivider createDefaultDivider() {
      return new WindowsSplitPaneDivider(this);
   }
}
