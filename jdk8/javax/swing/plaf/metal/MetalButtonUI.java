package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalButtonUI extends BasicButtonUI {
   protected Color focusColor;
   protected Color selectColor;
   protected Color disabledTextColor;
   private static final Object METAL_BUTTON_UI_KEY = new Object();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MetalButtonUI var2 = (MetalButtonUI)var1.get(METAL_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new MetalButtonUI();
         var1.put(METAL_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
   }

   public void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
   }

   protected BasicButtonListener createButtonListener(AbstractButton var1) {
      return super.createButtonListener(var1);
   }

   protected Color getSelectColor() {
      this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
      return this.selectColor;
   }

   protected Color getDisabledTextColor() {
      this.disabledTextColor = UIManager.getColor(this.getPropertyPrefix() + "disabledText");
      return this.disabledTextColor;
   }

   protected Color getFocusColor() {
      this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
      return this.focusColor;
   }

   public void update(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      if (var2.getBackground() instanceof UIResource && var3.isContentAreaFilled() && var2.isEnabled()) {
         ButtonModel var4 = var3.getModel();
         if (!MetalUtils.isToolBarButton(var2)) {
            if (!var4.isArmed() && !var4.isPressed() && MetalUtils.drawGradient(var2, var1, "Button.gradient", 0, 0, var2.getWidth(), var2.getHeight(), true)) {
               this.paint(var1, var2);
               return;
            }
         } else if (var4.isRollover() && MetalUtils.drawGradient(var2, var1, "Button.gradient", 0, 0, var2.getWidth(), var2.getHeight(), true)) {
            this.paint(var1, var2);
            return;
         }
      }

      super.update(var1, var2);
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      if (var2.isContentAreaFilled()) {
         Dimension var3 = var2.getSize();
         var1.setColor(this.getSelectColor());
         var1.fillRect(0, 0, var3.width, var3.height);
      }

   }

   protected void paintFocus(Graphics var1, AbstractButton var2, Rectangle var3, Rectangle var4, Rectangle var5) {
      Rectangle var6 = new Rectangle();
      String var7 = var2.getText();
      boolean var8 = var2.getIcon() != null;
      if (var7 != null && !var7.equals("")) {
         if (!var8) {
            var6.setBounds(var4);
         } else {
            var6.setBounds(var5.union(var4));
         }
      } else if (var8) {
         var6.setBounds(var5);
      }

      var1.setColor(this.getFocusColor());
      var1.drawRect(var6.x - 1, var6.y - 1, var6.width + 1, var6.height + 1);
   }

   protected void paintText(Graphics var1, JComponent var2, Rectangle var3, String var4) {
      AbstractButton var5 = (AbstractButton)var2;
      ButtonModel var6 = var5.getModel();
      FontMetrics var7 = SwingUtilities2.getFontMetrics(var2, var1);
      int var8 = var5.getDisplayedMnemonicIndex();
      if (var6.isEnabled()) {
         var1.setColor(var5.getForeground());
      } else {
         var1.setColor(this.getDisabledTextColor());
      }

      SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var8, var3.x, var3.y + var7.getAscent());
   }
}
