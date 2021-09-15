package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;

public class MotifLabelUI extends BasicLabelUI {
   private static final Object MOTIF_LABEL_UI_KEY = new Object();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MotifLabelUI var2 = (MotifLabelUI)var1.get(MOTIF_LABEL_UI_KEY);
      if (var2 == null) {
         var2 = new MotifLabelUI();
         var1.put(MOTIF_LABEL_UI_KEY, var2);
      }

      return var2;
   }
}
