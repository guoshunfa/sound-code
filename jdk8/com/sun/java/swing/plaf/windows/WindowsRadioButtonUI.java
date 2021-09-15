package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class WindowsRadioButtonUI extends BasicRadioButtonUI {
   private static final Object WINDOWS_RADIO_BUTTON_UI_KEY = new Object();
   protected int dashedRectGapX;
   protected int dashedRectGapY;
   protected int dashedRectGapWidth;
   protected int dashedRectGapHeight;
   protected Color focusColor;
   private boolean initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      WindowsRadioButtonUI var2 = (WindowsRadioButtonUI)var1.get(WINDOWS_RADIO_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new WindowsRadioButtonUI();
         var1.put(WINDOWS_RADIO_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.initialized) {
         this.dashedRectGapX = (Integer)UIManager.get("Button.dashedRectGapX");
         this.dashedRectGapY = (Integer)UIManager.get("Button.dashedRectGapY");
         this.dashedRectGapWidth = (Integer)UIManager.get("Button.dashedRectGapWidth");
         this.dashedRectGapHeight = (Integer)UIManager.get("Button.dashedRectGapHeight");
         this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
         this.initialized = true;
      }

      if (XPStyle.getXP() != null) {
         LookAndFeel.installProperty(var1, "rolloverEnabled", Boolean.TRUE);
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.initialized = false;
   }

   protected Color getFocusColor() {
      return this.focusColor;
   }

   protected void paintText(Graphics var1, AbstractButton var2, Rectangle var3, String var4) {
      WindowsGraphicsUtils.paintText(var1, var2, var3, var4, this.getTextShiftOffset());
   }

   protected void paintFocus(Graphics var1, Rectangle var2, Dimension var3) {
      var1.setColor(this.getFocusColor());
      BasicGraphicsUtils.drawDashedRect(var1, var2.x, var2.y, var2.width, var2.height);
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = super.getPreferredSize(var1);
      AbstractButton var3 = (AbstractButton)var1;
      if (var2 != null && var3.isFocusPainted()) {
         if (var2.width % 2 == 0) {
            ++var2.width;
         }

         if (var2.height % 2 == 0) {
            ++var2.height;
         }
      }

      return var2;
   }
}
