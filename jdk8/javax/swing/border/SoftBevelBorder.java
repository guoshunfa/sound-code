package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class SoftBevelBorder extends BevelBorder {
   public SoftBevelBorder(int var1) {
      super(var1);
   }

   public SoftBevelBorder(int var1, Color var2, Color var3) {
      super(var1, var2, var3);
   }

   @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
   public SoftBevelBorder(int var1, Color var2, Color var3, Color var4, Color var5) {
      super(var1, var2, var3, var4, var5);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Color var7 = var2.getColor();
      var2.translate(var3, var4);
      if (this.bevelType == 0) {
         var2.setColor(this.getHighlightOuterColor(var1));
         var2.drawLine(0, 0, var5 - 2, 0);
         var2.drawLine(0, 0, 0, var6 - 2);
         var2.drawLine(1, 1, 1, 1);
         var2.setColor(this.getHighlightInnerColor(var1));
         var2.drawLine(2, 1, var5 - 2, 1);
         var2.drawLine(1, 2, 1, var6 - 2);
         var2.drawLine(2, 2, 2, 2);
         var2.drawLine(0, var6 - 1, 0, var6 - 2);
         var2.drawLine(var5 - 1, 0, var5 - 1, 0);
         var2.setColor(this.getShadowOuterColor(var1));
         var2.drawLine(2, var6 - 1, var5 - 1, var6 - 1);
         var2.drawLine(var5 - 1, 2, var5 - 1, var6 - 1);
         var2.setColor(this.getShadowInnerColor(var1));
         var2.drawLine(var5 - 2, var6 - 2, var5 - 2, var6 - 2);
      } else if (this.bevelType == 1) {
         var2.setColor(this.getShadowOuterColor(var1));
         var2.drawLine(0, 0, var5 - 2, 0);
         var2.drawLine(0, 0, 0, var6 - 2);
         var2.drawLine(1, 1, 1, 1);
         var2.setColor(this.getShadowInnerColor(var1));
         var2.drawLine(2, 1, var5 - 2, 1);
         var2.drawLine(1, 2, 1, var6 - 2);
         var2.drawLine(2, 2, 2, 2);
         var2.drawLine(0, var6 - 1, 0, var6 - 2);
         var2.drawLine(var5 - 1, 0, var5 - 1, 0);
         var2.setColor(this.getHighlightOuterColor(var1));
         var2.drawLine(2, var6 - 1, var5 - 1, var6 - 1);
         var2.drawLine(var5 - 1, 2, var5 - 1, var6 - 1);
         var2.setColor(this.getHighlightInnerColor(var1));
         var2.drawLine(var5 - 2, var6 - 2, var5 - 2, var6 - 2);
      }

      var2.translate(-var3, -var4);
      var2.setColor(var7);
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.set(3, 3, 3, 3);
      return var2;
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
