package com.apple.laf;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class AquaScrollPaneUI extends BasicScrollPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new AquaScrollPaneUI();
   }

   protected MouseWheelListener createMouseWheelListener() {
      return new AquaScrollPaneUI.XYMouseWheelHandler();
   }

   protected class XYMouseWheelHandler extends BasicScrollPaneUI.MouseWheelHandler {
      protected XYMouseWheelHandler() {
         super();
      }

      public void mouseWheelMoved(MouseWheelEvent var1) {
         super.mouseWheelMoved(var1);
         var1.consume();
      }
   }
}
