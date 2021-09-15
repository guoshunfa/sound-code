package javax.swing.plaf.metal;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MetalProgressBarUI extends BasicProgressBarUI {
   private Rectangle innards;
   private Rectangle box;

   public static ComponentUI createUI(JComponent var0) {
      return new MetalProgressBarUI();
   }

   public void paintDeterminate(Graphics var1, JComponent var2) {
      super.paintDeterminate(var1, var2);
      if (var1 instanceof Graphics2D) {
         if (this.progressBar.isBorderPainted()) {
            Insets var3 = this.progressBar.getInsets();
            int var4 = this.progressBar.getWidth() - (var3.left + var3.right);
            int var5 = this.progressBar.getHeight() - (var3.top + var3.bottom);
            int var6 = this.getAmountFull(var3, var4, var5);
            boolean var7 = MetalUtils.isLeftToRight(var2);
            int var8 = var3.left;
            int var9 = var3.top;
            int var10 = var3.left + var4 - 1;
            int var11 = var3.top + var5 - 1;
            Graphics2D var12 = (Graphics2D)var1;
            var12.setStroke(new BasicStroke(1.0F));
            if (this.progressBar.getOrientation() == 0) {
               var12.setColor(MetalLookAndFeel.getControlShadow());
               var12.drawLine(var8, var9, var10, var9);
               if (var6 > 0) {
                  var12.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                  if (var7) {
                     var12.drawLine(var8, var9, var8 + var6 - 1, var9);
                  } else {
                     var12.drawLine(var10, var9, var10 - var6 + 1, var9);
                     if (this.progressBar.getPercentComplete() != 1.0D) {
                        var12.setColor(MetalLookAndFeel.getControlShadow());
                     }
                  }
               }

               var12.drawLine(var8, var9, var8, var11);
            } else {
               var12.setColor(MetalLookAndFeel.getControlShadow());
               var12.drawLine(var8, var9, var8, var11);
               if (var6 > 0) {
                  var12.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                  var12.drawLine(var8, var11, var8, var11 - var6 + 1);
               }

               var12.setColor(MetalLookAndFeel.getControlShadow());
               if (this.progressBar.getPercentComplete() == 1.0D) {
                  var12.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
               }

               var12.drawLine(var8, var9, var10, var9);
            }
         }

      }
   }

   public void paintIndeterminate(Graphics var1, JComponent var2) {
      super.paintIndeterminate(var1, var2);
      if (this.progressBar.isBorderPainted() && var1 instanceof Graphics2D) {
         Insets var3 = this.progressBar.getInsets();
         int var4 = this.progressBar.getWidth() - (var3.left + var3.right);
         int var5 = this.progressBar.getHeight() - (var3.top + var3.bottom);
         this.getAmountFull(var3, var4, var5);
         boolean var7 = MetalUtils.isLeftToRight(var2);
         Rectangle var12 = null;
         var12 = this.getBox(var12);
         int var8 = var3.left;
         int var9 = var3.top;
         int var10 = var3.left + var4 - 1;
         int var11 = var3.top + var5 - 1;
         Graphics2D var13 = (Graphics2D)var1;
         var13.setStroke(new BasicStroke(1.0F));
         if (this.progressBar.getOrientation() == 0) {
            var13.setColor(MetalLookAndFeel.getControlShadow());
            var13.drawLine(var8, var9, var10, var9);
            var13.drawLine(var8, var9, var8, var11);
            var13.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            var13.drawLine(var12.x, var9, var12.x + var12.width - 1, var9);
         } else {
            var13.setColor(MetalLookAndFeel.getControlShadow());
            var13.drawLine(var8, var9, var8, var11);
            var13.drawLine(var8, var9, var10, var9);
            var13.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            var13.drawLine(var8, var12.y, var8, var12.y + var12.height - 1);
         }

      }
   }
}
