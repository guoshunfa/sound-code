package com.sun.java.swing.plaf.windows;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class WindowsCheckBoxUI extends WindowsRadioButtonUI {
   private static final Object WINDOWS_CHECK_BOX_UI_KEY = new Object();
   private static final String propertyPrefix = "CheckBox.";
   private boolean defaults_initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      WindowsCheckBoxUI var2 = (WindowsCheckBoxUI)var1.get(WINDOWS_CHECK_BOX_UI_KEY);
      if (var2 == null) {
         var2 = new WindowsCheckBoxUI();
         var1.put(WINDOWS_CHECK_BOX_UI_KEY, var2);
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

   public void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }
}
