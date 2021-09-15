package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import sun.swing.SwingUtilities2;

public class MotifSliderUI extends BasicSliderUI {
   static final Dimension PREFERRED_HORIZONTAL_SIZE = new Dimension(164, 15);
   static final Dimension PREFERRED_VERTICAL_SIZE = new Dimension(15, 164);
   static final Dimension MINIMUM_HORIZONTAL_SIZE = new Dimension(43, 15);
   static final Dimension MINIMUM_VERTICAL_SIZE = new Dimension(15, 43);

   public MotifSliderUI(JSlider var1) {
      super(var1);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new MotifSliderUI((JSlider)var0);
   }

   public Dimension getPreferredHorizontalSize() {
      return PREFERRED_HORIZONTAL_SIZE;
   }

   public Dimension getPreferredVerticalSize() {
      return PREFERRED_VERTICAL_SIZE;
   }

   public Dimension getMinimumHorizontalSize() {
      return MINIMUM_HORIZONTAL_SIZE;
   }

   public Dimension getMinimumVerticalSize() {
      return MINIMUM_VERTICAL_SIZE;
   }

   protected Dimension getThumbSize() {
      return this.slider.getOrientation() == 0 ? new Dimension(30, 15) : new Dimension(15, 30);
   }

   public void paintFocus(Graphics var1) {
   }

   public void paintTrack(Graphics var1) {
   }

   public void paintThumb(Graphics var1) {
      Rectangle var2 = this.thumbRect;
      int var3 = var2.x;
      int var4 = var2.y;
      int var5 = var2.width;
      int var6 = var2.height;
      if (this.slider.isEnabled()) {
         var1.setColor(this.slider.getForeground());
      } else {
         var1.setColor(this.slider.getForeground().darker());
      }

      if (this.slider.getOrientation() == 0) {
         var1.translate(var3, var2.y - 1);
         var1.fillRect(0, 1, var5, var6 - 1);
         var1.setColor(this.getHighlightColor());
         SwingUtilities2.drawHLine(var1, 0, var5 - 1, 1);
         SwingUtilities2.drawVLine(var1, 0, 1, var6);
         SwingUtilities2.drawVLine(var1, var5 / 2, 2, var6 - 1);
         var1.setColor(this.getShadowColor());
         SwingUtilities2.drawHLine(var1, 0, var5 - 1, var6);
         SwingUtilities2.drawVLine(var1, var5 - 1, 1, var6);
         SwingUtilities2.drawVLine(var1, var5 / 2 - 1, 2, var6);
         var1.translate(-var3, -(var2.y - 1));
      } else {
         var1.translate(var2.x - 1, 0);
         var1.fillRect(1, var4, var5 - 1, var6);
         var1.setColor(this.getHighlightColor());
         SwingUtilities2.drawHLine(var1, 1, var5, var4);
         SwingUtilities2.drawVLine(var1, 1, var4 + 1, var4 + var6 - 1);
         SwingUtilities2.drawHLine(var1, 2, var5 - 1, var4 + var6 / 2);
         var1.setColor(this.getShadowColor());
         SwingUtilities2.drawHLine(var1, 2, var5, var4 + var6 - 1);
         SwingUtilities2.drawVLine(var1, var5, var4 + var6 - 1, var4);
         SwingUtilities2.drawHLine(var1, 2, var5 - 1, var4 + var6 / 2 - 1);
         var1.translate(-(var2.x - 1), 0);
      }

   }
}
