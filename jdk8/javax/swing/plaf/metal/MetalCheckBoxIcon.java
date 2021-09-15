package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.plaf.UIResource;

public class MetalCheckBoxIcon implements Icon, UIResource, Serializable {
   protected int getControlSize() {
      return 13;
   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      JCheckBox var5 = (JCheckBox)var1;
      ButtonModel var6 = var5.getModel();
      int var7 = this.getControlSize();
      boolean var8 = var6.isSelected();
      if (var6.isEnabled()) {
         if (var5.isBorderPaintedFlat()) {
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            var2.drawRect(var3 + 1, var4, var7 - 1, var7 - 1);
         }

         if (var6.isPressed() && var6.isArmed()) {
            if (var5.isBorderPaintedFlat()) {
               var2.setColor(MetalLookAndFeel.getControlShadow());
               var2.fillRect(var3 + 2, var4 + 1, var7 - 2, var7 - 2);
            } else {
               var2.setColor(MetalLookAndFeel.getControlShadow());
               var2.fillRect(var3, var4, var7 - 1, var7 - 1);
               MetalUtils.drawPressed3DBorder(var2, var3, var4, var7, var7);
            }
         } else if (!var5.isBorderPaintedFlat()) {
            MetalUtils.drawFlush3DBorder(var2, var3, var4, var7, var7);
         }

         var2.setColor(MetalLookAndFeel.getControlInfo());
      } else {
         var2.setColor(MetalLookAndFeel.getControlShadow());
         var2.drawRect(var3, var4, var7 - 1, var7 - 1);
      }

      if (var8) {
         if (var5.isBorderPaintedFlat()) {
            ++var3;
         }

         this.drawCheck(var1, var2, var3, var4);
      }

   }

   protected void drawCheck(Component var1, Graphics var2, int var3, int var4) {
      int var5 = this.getControlSize();
      var2.fillRect(var3 + 3, var4 + 5, 2, var5 - 8);
      var2.drawLine(var3 + (var5 - 4), var4 + 3, var3 + 5, var4 + (var5 - 6));
      var2.drawLine(var3 + (var5 - 4), var4 + 4, var3 + 5, var4 + (var5 - 5));
   }

   public int getIconWidth() {
      return this.getControlSize();
   }

   public int getIconHeight() {
      return this.getControlSize();
   }
}
