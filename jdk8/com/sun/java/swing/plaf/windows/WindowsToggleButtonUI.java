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
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class WindowsToggleButtonUI extends BasicToggleButtonUI {
   protected int dashedRectGapX;
   protected int dashedRectGapY;
   protected int dashedRectGapWidth;
   protected int dashedRectGapHeight;
   protected Color focusColor;
   private static final Object WINDOWS_TOGGLE_BUTTON_UI_KEY = new Object();
   private boolean defaults_initialized = false;
   private transient Color cachedSelectedColor = null;
   private transient Color cachedBackgroundColor = null;
   private transient Color cachedHighlightColor = null;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      WindowsToggleButtonUI var2 = (WindowsToggleButtonUI)var1.get(WINDOWS_TOGGLE_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new WindowsToggleButtonUI();
         var1.put(WINDOWS_TOGGLE_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         String var2 = this.getPropertyPrefix();
         this.dashedRectGapX = (Integer)UIManager.get("Button.dashedRectGapX");
         this.dashedRectGapY = (Integer)UIManager.get("Button.dashedRectGapY");
         this.dashedRectGapWidth = (Integer)UIManager.get("Button.dashedRectGapWidth");
         this.dashedRectGapHeight = (Integer)UIManager.get("Button.dashedRectGapHeight");
         this.focusColor = UIManager.getColor(var2 + "focus");
         this.defaults_initialized = true;
      }

      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         var1.setBorder(var3.getBorder(var1, WindowsButtonUI.getXPButtonType(var1)));
         LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
         LookAndFeel.installProperty(var1, "rolloverEnabled", Boolean.TRUE);
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   protected Color getFocusColor() {
      return this.focusColor;
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      if (XPStyle.getXP() == null && var2.isContentAreaFilled()) {
         Color var3 = var1.getColor();
         Color var4 = var2.getBackground();
         Color var5 = UIManager.getColor("ToggleButton.highlight");
         if (var4 != this.cachedBackgroundColor || var5 != this.cachedHighlightColor) {
            int var6 = var4.getRed();
            int var7 = var5.getRed();
            int var8 = var4.getGreen();
            int var9 = var5.getGreen();
            int var10 = var4.getBlue();
            int var11 = var5.getBlue();
            this.cachedSelectedColor = new Color(Math.min(var6, var7) + Math.abs(var6 - var7) / 2, Math.min(var8, var9) + Math.abs(var8 - var9) / 2, Math.min(var10, var11) + Math.abs(var10 - var11) / 2);
            this.cachedBackgroundColor = var4;
            this.cachedHighlightColor = var5;
         }

         var1.setColor(this.cachedSelectedColor);
         var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
         var1.setColor(var3);
      }

   }

   public void paint(Graphics var1, JComponent var2) {
      if (XPStyle.getXP() != null) {
         WindowsButtonUI.paintXPButtonBackground(var1, var2);
      }

      super.paint(var1, var2);
   }

   protected void paintText(Graphics var1, AbstractButton var2, Rectangle var3, String var4) {
      WindowsGraphicsUtils.paintText(var1, var2, var3, var4, this.getTextShiftOffset());
   }

   protected void paintFocus(Graphics var1, AbstractButton var2, Rectangle var3, Rectangle var4, Rectangle var5) {
      var1.setColor(this.getFocusColor());
      BasicGraphicsUtils.drawDashedRect(var1, this.dashedRectGapX, this.dashedRectGapY, var2.getWidth() - this.dashedRectGapWidth, var2.getHeight() - this.dashedRectGapHeight);
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
