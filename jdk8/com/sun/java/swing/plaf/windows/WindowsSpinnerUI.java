package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class WindowsSpinnerUI extends BasicSpinnerUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsSpinnerUI();
   }

   public void paint(Graphics var1, JComponent var2) {
      if (XPStyle.getXP() != null) {
         this.paintXPBackground(var1, var2);
      }

      super.paint(var1, var2);
   }

   private TMSchema.State getXPState(JComponent var1) {
      TMSchema.State var2 = TMSchema.State.NORMAL;
      if (!var1.isEnabled()) {
         var2 = TMSchema.State.DISABLED;
      }

      return var2;
   }

   private void paintXPBackground(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         XPStyle.Skin var4 = var3.getSkin(var2, TMSchema.Part.EP_EDIT);
         TMSchema.State var5 = this.getXPState(var2);
         var4.paintSkin(var1, 0, 0, var2.getWidth(), var2.getHeight(), var5);
      }
   }

   protected Component createPreviousButton() {
      if (XPStyle.getXP() != null) {
         XPStyle.GlyphButton var1 = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_DOWN);
         Dimension var2 = UIManager.getDimension("Spinner.arrowButtonSize");
         var1.setPreferredSize(var2);
         var1.setRequestFocusEnabled(false);
         this.installPreviousButtonListeners(var1);
         return var1;
      } else {
         return super.createPreviousButton();
      }
   }

   protected Component createNextButton() {
      if (XPStyle.getXP() != null) {
         XPStyle.GlyphButton var1 = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_UP);
         Dimension var2 = UIManager.getDimension("Spinner.arrowButtonSize");
         var1.setPreferredSize(var2);
         var1.setRequestFocusEnabled(false);
         this.installNextButtonListeners(var1);
         return var1;
      } else {
         return super.createNextButton();
      }
   }

   private UIResource getUIResource(Object[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] instanceof UIResource) {
            return (UIResource)var1[var2];
         }
      }

      return null;
   }
}
