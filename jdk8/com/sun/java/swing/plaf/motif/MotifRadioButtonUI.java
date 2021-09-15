package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class MotifRadioButtonUI extends BasicRadioButtonUI {
   private static final Object MOTIF_RADIO_BUTTON_UI_KEY = new Object();
   protected Color focusColor;
   private boolean defaults_initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MotifRadioButtonUI var2 = (MotifRadioButtonUI)var1.get(MOTIF_RADIO_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new MotifRadioButtonUI();
         var1.put(MOTIF_RADIO_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
         this.defaults_initialized = true;
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   protected Color getFocusColor() {
      return this.focusColor;
   }

   protected void paintFocus(Graphics var1, Rectangle var2, Dimension var3) {
      var1.setColor(this.getFocusColor());
      var1.drawRect(0, 0, var3.width - 1, var3.height - 1);
   }
}
