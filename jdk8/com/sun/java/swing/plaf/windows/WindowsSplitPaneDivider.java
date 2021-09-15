package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneDivider extends BasicSplitPaneDivider {
   public WindowsSplitPaneDivider(BasicSplitPaneUI var1) {
      super(var1);
   }

   public void paint(Graphics var1) {
      Color var2 = this.splitPane.hasFocus() ? UIManager.getColor("SplitPane.shadow") : this.getBackground();
      Dimension var3 = this.getSize();
      if (var2 != null) {
         var1.setColor(var2);
         var1.fillRect(0, 0, var3.width, var3.height);
      }

      super.paint(var1);
   }
}
