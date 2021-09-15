package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.awt.AppContext;

public class WindowsButtonUI extends BasicButtonUI {
   protected int dashedRectGapX;
   protected int dashedRectGapY;
   protected int dashedRectGapWidth;
   protected int dashedRectGapHeight;
   protected Color focusColor;
   private boolean defaults_initialized = false;
   private static final Object WINDOWS_BUTTON_UI_KEY = new Object();
   private Rectangle viewRect = new Rectangle();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      WindowsButtonUI var2 = (WindowsButtonUI)var1.get(WINDOWS_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new WindowsButtonUI();
         var1.put(WINDOWS_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         String var2 = this.getPropertyPrefix();
         this.dashedRectGapX = UIManager.getInt(var2 + "dashedRectGapX");
         this.dashedRectGapY = UIManager.getInt(var2 + "dashedRectGapY");
         this.dashedRectGapWidth = UIManager.getInt(var2 + "dashedRectGapWidth");
         this.dashedRectGapHeight = UIManager.getInt(var2 + "dashedRectGapHeight");
         this.focusColor = UIManager.getColor(var2 + "focus");
         this.defaults_initialized = true;
      }

      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         var1.setBorder(var3.getBorder(var1, getXPButtonType(var1)));
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

   protected void paintText(Graphics var1, AbstractButton var2, Rectangle var3, String var4) {
      WindowsGraphicsUtils.paintText(var1, var2, var3, var4, this.getTextShiftOffset());
   }

   protected void paintFocus(Graphics var1, AbstractButton var2, Rectangle var3, Rectangle var4, Rectangle var5) {
      int var6 = var2.getWidth();
      int var7 = var2.getHeight();
      var1.setColor(this.getFocusColor());
      BasicGraphicsUtils.drawDashedRect(var1, this.dashedRectGapX, this.dashedRectGapY, var6 - this.dashedRectGapWidth, var7 - this.dashedRectGapHeight);
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      this.setTextShiftOffset();
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

   public void paint(Graphics var1, JComponent var2) {
      if (XPStyle.getXP() != null) {
         paintXPButtonBackground(var1, var2);
      }

      super.paint(var1, var2);
   }

   static TMSchema.Part getXPButtonType(AbstractButton var0) {
      if (var0 instanceof JCheckBox) {
         return TMSchema.Part.BP_CHECKBOX;
      } else if (var0 instanceof JRadioButton) {
         return TMSchema.Part.BP_RADIOBUTTON;
      } else {
         boolean var1 = var0.getParent() instanceof JToolBar;
         return var1 ? TMSchema.Part.TP_BUTTON : TMSchema.Part.BP_PUSHBUTTON;
      }
   }

   static TMSchema.State getXPButtonState(AbstractButton var0) {
      TMSchema.Part var1 = getXPButtonType(var0);
      ButtonModel var2 = var0.getModel();
      TMSchema.State var3 = TMSchema.State.NORMAL;
      switch(var1) {
      case BP_RADIOBUTTON:
      case BP_CHECKBOX:
         if (!var2.isEnabled()) {
            var3 = var2.isSelected() ? TMSchema.State.CHECKEDDISABLED : TMSchema.State.UNCHECKEDDISABLED;
         } else if (var2.isPressed() && var2.isArmed()) {
            var3 = var2.isSelected() ? TMSchema.State.CHECKEDPRESSED : TMSchema.State.UNCHECKEDPRESSED;
         } else if (var2.isRollover()) {
            var3 = var2.isSelected() ? TMSchema.State.CHECKEDHOT : TMSchema.State.UNCHECKEDHOT;
         } else {
            var3 = var2.isSelected() ? TMSchema.State.CHECKEDNORMAL : TMSchema.State.UNCHECKEDNORMAL;
         }
         break;
      case BP_PUSHBUTTON:
      case TP_BUTTON:
         boolean var4 = var0.getParent() instanceof JToolBar;
         if (var4) {
            if (var2.isArmed() && var2.isPressed()) {
               var3 = TMSchema.State.PRESSED;
            } else if (!var2.isEnabled()) {
               var3 = TMSchema.State.DISABLED;
            } else if (var2.isSelected() && var2.isRollover()) {
               var3 = TMSchema.State.HOTCHECKED;
            } else if (var2.isSelected()) {
               var3 = TMSchema.State.CHECKED;
            } else if (var2.isRollover()) {
               var3 = TMSchema.State.HOT;
            } else if (var0.hasFocus()) {
               var3 = TMSchema.State.HOT;
            }
         } else if ((!var2.isArmed() || !var2.isPressed()) && !var2.isSelected()) {
            if (!var2.isEnabled()) {
               var3 = TMSchema.State.DISABLED;
            } else if (!var2.isRollover() && !var2.isPressed()) {
               if (var0 instanceof JButton && ((JButton)var0).isDefaultButton()) {
                  var3 = TMSchema.State.DEFAULTED;
               } else if (var0.hasFocus()) {
                  var3 = TMSchema.State.HOT;
               }
            } else {
               var3 = TMSchema.State.HOT;
            }
         } else {
            var3 = TMSchema.State.PRESSED;
         }
         break;
      default:
         var3 = TMSchema.State.NORMAL;
      }

      return var3;
   }

   static void paintXPButtonBackground(Graphics var0, JComponent var1) {
      AbstractButton var2 = (AbstractButton)var1;
      XPStyle var3 = XPStyle.getXP();
      TMSchema.Part var4 = getXPButtonType(var2);
      if (var2.isContentAreaFilled() && var3 != null) {
         XPStyle.Skin var5 = var3.getSkin(var2, var4);
         TMSchema.State var6 = getXPButtonState(var2);
         Dimension var7 = var1.getSize();
         int var8 = 0;
         int var9 = 0;
         int var10 = var7.width;
         int var11 = var7.height;
         Border var12 = var1.getBorder();
         Insets var13;
         if (var12 != null) {
            var13 = getOpaqueInsets(var12, var1);
         } else {
            var13 = var1.getInsets();
         }

         if (var13 != null) {
            var8 += var13.left;
            var9 += var13.top;
            var10 -= var13.left + var13.right;
            var11 -= var13.top + var13.bottom;
         }

         var5.paintSkin(var0, var8, var9, var10, var11, var6);
      }

   }

   private static Insets getOpaqueInsets(Border var0, Component var1) {
      if (var0 == null) {
         return null;
      } else if (var0.isBorderOpaque()) {
         return var0.getBorderInsets(var1);
      } else if (var0 instanceof CompoundBorder) {
         CompoundBorder var2 = (CompoundBorder)var0;
         Insets var3 = getOpaqueInsets(var2.getOutsideBorder(), var1);
         if (var3 != null && var3.equals(var2.getOutsideBorder().getBorderInsets(var1))) {
            Insets var4 = getOpaqueInsets(var2.getInsideBorder(), var1);
            return var4 == null ? var3 : new Insets(var3.top + var4.top, var3.left + var4.left, var3.bottom + var4.bottom, var3.right + var4.right);
         } else {
            return var3;
         }
      } else {
         return null;
      }
   }
}
