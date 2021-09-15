package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class MotifCheckBoxUI extends MotifRadioButtonUI {
   private static final Object MOTIF_CHECK_BOX_UI_KEY = new Object();
   private static final String propertyPrefix = "CheckBox.";
   private boolean defaults_initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MotifCheckBoxUI var2 = (MotifCheckBoxUI)var1.get(MOTIF_CHECK_BOX_UI_KEY);
      if (var2 == null) {
         var2 = new MotifCheckBoxUI();
         var1.put(MOTIF_CHECK_BOX_UI_KEY, var2);
      }

      return var2;
   }

   public String getPropertyPrefix() {
      return "CheckBox.";
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.icon = UIManager.getIcon(this.getPropertyPrefix() + "icon");
         this.defaults_initialized = true;
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }
}
