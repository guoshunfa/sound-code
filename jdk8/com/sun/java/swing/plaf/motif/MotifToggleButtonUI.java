package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class MotifToggleButtonUI extends BasicToggleButtonUI {
   private static final Object MOTIF_TOGGLE_BUTTON_UI_KEY = new Object();
   protected Color selectColor;
   private boolean defaults_initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MotifToggleButtonUI var2 = (MotifToggleButtonUI)var1.get(MOTIF_TOGGLE_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new MotifToggleButtonUI();
         var1.put(MOTIF_TOGGLE_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
         this.defaults_initialized = true;
      }

      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   protected Color getSelectColor() {
      return this.selectColor;
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      if (var2.isContentAreaFilled()) {
         Color var3 = var1.getColor();
         Dimension var4 = var2.getSize();
         Insets var5 = var2.getInsets();
         Insets var6 = var2.getMargin();
         if (var2.getBackground() instanceof UIResource) {
            var1.setColor(this.getSelectColor());
         }

         var1.fillRect(var5.left - var6.left, var5.top - var6.top, var4.width - (var5.left - var6.left) - (var5.right - var6.right), var4.height - (var5.top - var6.top) - (var5.bottom - var6.bottom));
         var1.setColor(var3);
      }

   }

   public Insets getInsets(JComponent var1) {
      Border var2 = var1.getBorder();
      Insets var3 = var2 != null ? var2.getBorderInsets(var1) : new Insets(0, 0, 0, 0);
      return var3;
   }
}
