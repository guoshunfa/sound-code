package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalRadioButtonUI extends BasicRadioButtonUI {
   private static final Object METAL_RADIO_BUTTON_UI_KEY = new Object();
   protected Color focusColor;
   protected Color selectColor;
   protected Color disabledTextColor;
   private boolean defaults_initialized = false;

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MetalRadioButtonUI var2 = (MetalRadioButtonUI)var1.get(METAL_RADIO_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new MetalRadioButtonUI();
         var1.put(METAL_RADIO_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
         this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
         this.disabledTextColor = UIManager.getColor(this.getPropertyPrefix() + "disabledText");
         this.defaults_initialized = true;
      }

      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   protected Color getSelectColor() {
      return this.selectColor;
   }

   protected Color getDisabledTextColor() {
      return this.disabledTextColor;
   }

   protected Color getFocusColor() {
      return this.focusColor;
   }

   public synchronized void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      Dimension var5 = var2.getSize();
      int var6 = var5.width;
      int var7 = var5.height;
      Font var8 = var2.getFont();
      var1.setFont(var8);
      FontMetrics var9 = SwingUtilities2.getFontMetrics(var2, var1, var8);
      Rectangle var10 = new Rectangle(var5);
      Rectangle var11 = new Rectangle();
      Rectangle var12 = new Rectangle();
      Insets var13 = var2.getInsets();
      var10.x += var13.left;
      var10.y += var13.top;
      var10.width -= var13.right + var10.x;
      var10.height -= var13.bottom + var10.y;
      Icon var14 = var3.getIcon();
      Object var15 = null;
      Object var16 = null;
      String var17 = SwingUtilities.layoutCompoundLabel(var2, var9, var3.getText(), var14 != null ? var14 : this.getDefaultIcon(), var3.getVerticalAlignment(), var3.getHorizontalAlignment(), var3.getVerticalTextPosition(), var3.getHorizontalTextPosition(), var10, var11, var12, var3.getIconTextGap());
      if (var2.isOpaque()) {
         var1.setColor(var3.getBackground());
         var1.fillRect(0, 0, var5.width, var5.height);
      }

      if (var14 != null) {
         if (!var4.isEnabled()) {
            if (var4.isSelected()) {
               var14 = var3.getDisabledSelectedIcon();
            } else {
               var14 = var3.getDisabledIcon();
            }
         } else if (var4.isPressed() && var4.isArmed()) {
            var14 = var3.getPressedIcon();
            if (var14 == null) {
               var14 = var3.getSelectedIcon();
            }
         } else if (var4.isSelected()) {
            if (var3.isRolloverEnabled() && var4.isRollover()) {
               var14 = var3.getRolloverSelectedIcon();
               if (var14 == null) {
                  var14 = var3.getSelectedIcon();
               }
            } else {
               var14 = var3.getSelectedIcon();
            }
         } else if (var3.isRolloverEnabled() && var4.isRollover()) {
            var14 = var3.getRolloverIcon();
         }

         if (var14 == null) {
            var14 = var3.getIcon();
         }

         var14.paintIcon(var2, var1, var11.x, var11.y);
      } else {
         this.getDefaultIcon().paintIcon(var2, var1, var11.x, var11.y);
      }

      if (var17 != null) {
         View var18 = (View)var2.getClientProperty("html");
         if (var18 != null) {
            var18.paint(var1, var12);
         } else {
            int var19 = var3.getDisplayedMnemonicIndex();
            if (var4.isEnabled()) {
               var1.setColor(var3.getForeground());
            } else {
               var1.setColor(this.getDisabledTextColor());
            }

            SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var17, var19, var12.x, var12.y + var9.getAscent());
         }

         if (var3.hasFocus() && var3.isFocusPainted() && var12.width > 0 && var12.height > 0) {
            this.paintFocus(var1, var12, var5);
         }
      }

   }

   protected void paintFocus(Graphics var1, Rectangle var2, Dimension var3) {
      var1.setColor(this.getFocusColor());
      var1.drawRect(var2.x - 1, var2.y - 1, var2.width + 1, var2.height + 1);
   }
}
