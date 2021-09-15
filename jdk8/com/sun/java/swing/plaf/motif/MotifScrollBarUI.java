package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.swing.SwingUtilities2;

public class MotifScrollBarUI extends BasicScrollBarUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifScrollBarUI();
   }

   public Dimension getPreferredSize(JComponent var1) {
      Insets var2 = var1.getInsets();
      int var3 = var2.left + var2.right;
      int var4 = var2.top + var2.bottom;
      return this.scrollbar.getOrientation() == 1 ? new Dimension(var3 + 11, var4 + 33) : new Dimension(var3 + 33, var4 + 11);
   }

   protected JButton createDecreaseButton(int var1) {
      return new MotifScrollBarButton(var1);
   }

   protected JButton createIncreaseButton(int var1) {
      return new MotifScrollBarButton(var1);
   }

   public void paintTrack(Graphics var1, JComponent var2, Rectangle var3) {
      var1.setColor(this.trackColor);
      var1.fillRect(var3.x, var3.y, var3.width, var3.height);
   }

   public void paintThumb(Graphics var1, JComponent var2, Rectangle var3) {
      if (!var3.isEmpty() && this.scrollbar.isEnabled()) {
         int var4 = var3.width;
         int var5 = var3.height;
         var1.translate(var3.x, var3.y);
         var1.setColor(this.thumbColor);
         var1.fillRect(0, 0, var4 - 1, var5 - 1);
         var1.setColor(this.thumbHighlightColor);
         SwingUtilities2.drawVLine(var1, 0, 0, var5 - 1);
         SwingUtilities2.drawHLine(var1, 1, var4 - 1, 0);
         var1.setColor(this.thumbLightShadowColor);
         SwingUtilities2.drawHLine(var1, 1, var4 - 1, var5 - 1);
         SwingUtilities2.drawVLine(var1, var4 - 1, 1, var5 - 2);
         var1.translate(-var3.x, -var3.y);
      }
   }
}
