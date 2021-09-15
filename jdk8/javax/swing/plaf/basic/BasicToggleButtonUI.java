package javax.swing.plaf.basic;

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
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;

public class BasicToggleButtonUI extends BasicButtonUI {
   private static final Object BASIC_TOGGLE_BUTTON_UI_KEY = new Object();
   private static final String propertyPrefix = "ToggleButton.";

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      BasicToggleButtonUI var2 = (BasicToggleButtonUI)var1.get(BASIC_TOGGLE_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new BasicToggleButtonUI();
         var1.put(BASIC_TOGGLE_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected String getPropertyPrefix() {
      return "ToggleButton.";
   }

   public void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      Dimension var5 = var3.getSize();
      FontMetrics var6 = var1.getFontMetrics();
      Insets var7 = var2.getInsets();
      Rectangle var8 = new Rectangle(var5);
      var8.x += var7.left;
      var8.y += var7.top;
      var8.width -= var7.right + var8.x;
      var8.height -= var7.bottom + var8.y;
      Rectangle var9 = new Rectangle();
      Rectangle var10 = new Rectangle();
      Font var11 = var2.getFont();
      var1.setFont(var11);
      String var12 = SwingUtilities.layoutCompoundLabel(var2, var6, var3.getText(), var3.getIcon(), var3.getVerticalAlignment(), var3.getHorizontalAlignment(), var3.getVerticalTextPosition(), var3.getHorizontalTextPosition(), var8, var9, var10, var3.getText() == null ? 0 : var3.getIconTextGap());
      var1.setColor(var3.getBackground());
      if (var4.isArmed() && var4.isPressed() || var4.isSelected()) {
         this.paintButtonPressed(var1, var3);
      }

      if (var3.getIcon() != null) {
         this.paintIcon(var1, var3, var9);
      }

      if (var12 != null && !var12.equals("")) {
         View var13 = (View)var2.getClientProperty("html");
         if (var13 != null) {
            var13.paint(var1, var10);
         } else {
            this.paintText(var1, var3, var10, var12);
         }
      }

      if (var3.isFocusPainted() && var3.hasFocus()) {
         this.paintFocus(var1, var3, var8, var10, var9);
      }

   }

   protected void paintIcon(Graphics var1, AbstractButton var2, Rectangle var3) {
      ButtonModel var4 = var2.getModel();
      Icon var5 = null;
      if (!var4.isEnabled()) {
         if (var4.isSelected()) {
            var5 = var2.getDisabledSelectedIcon();
         } else {
            var5 = var2.getDisabledIcon();
         }
      } else if (var4.isPressed() && var4.isArmed()) {
         var5 = var2.getPressedIcon();
         if (var5 == null) {
            var5 = var2.getSelectedIcon();
         }
      } else if (var4.isSelected()) {
         if (var2.isRolloverEnabled() && var4.isRollover()) {
            var5 = var2.getRolloverSelectedIcon();
            if (var5 == null) {
               var5 = var2.getSelectedIcon();
            }
         } else {
            var5 = var2.getSelectedIcon();
         }
      } else if (var2.isRolloverEnabled() && var4.isRollover()) {
         var5 = var2.getRolloverIcon();
      }

      if (var5 == null) {
         var5 = var2.getIcon();
      }

      var5.paintIcon(var2, var1, var3.x, var3.y);
   }

   protected int getTextShiftOffset() {
      return 0;
   }
}
