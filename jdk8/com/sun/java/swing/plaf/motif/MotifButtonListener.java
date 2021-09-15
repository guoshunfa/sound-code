package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

public class MotifButtonListener extends BasicButtonListener {
   public MotifButtonListener(AbstractButton var1) {
      super(var1);
   }

   protected void checkOpacity(AbstractButton var1) {
      var1.setOpaque(false);
   }
}
