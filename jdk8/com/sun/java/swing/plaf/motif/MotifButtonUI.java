package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;

public class MotifButtonUI extends BasicButtonUI {
   protected Color selectColor;
   private boolean defaults_initialized = false;
   private static final Object MOTIF_BUTTON_UI_KEY = new Object();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      MotifButtonUI var2 = (MotifButtonUI)var1.get(MOTIF_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new MotifButtonUI();
         var1.put(MOTIF_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected BasicButtonListener createButtonListener(AbstractButton var1) {
      return new MotifButtonListener(var1);
   }

   public void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
         this.defaults_initialized = true;
      }

      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   protected Color getSelectColor() {
      return this.selectColor;
   }

   public void paint(Graphics var1, JComponent var2) {
      this.fillContentArea(var1, (AbstractButton)var2, var2.getBackground());
      super.paint(var1, var2);
   }

   protected void paintIcon(Graphics var1, JComponent var2, Rectangle var3) {
      Shape var4 = var1.getClip();
      Rectangle var5 = AbstractBorder.getInteriorRectangle(var2, var2.getBorder(), 0, 0, var2.getWidth(), var2.getHeight());
      Rectangle var6 = var4.getBounds();
      var5 = SwingUtilities.computeIntersection(var6.x, var6.y, var6.width, var6.height, var5);
      var1.setClip(var5);
      super.paintIcon(var1, var2, var3);
      var1.setClip(var4);
   }

   protected void paintFocus(Graphics var1, AbstractButton var2, Rectangle var3, Rectangle var4, Rectangle var5) {
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      this.fillContentArea(var1, var2, this.selectColor);
   }

   protected void fillContentArea(Graphics var1, AbstractButton var2, Color var3) {
      if (var2.isContentAreaFilled()) {
         Insets var4 = var2.getMargin();
         Insets var5 = var2.getInsets();
         Dimension var6 = var2.getSize();
         var1.setColor(var3);
         var1.fillRect(var5.left - var4.left, var5.top - var4.top, var6.width - (var5.left - var4.left) - (var5.right - var4.right), var6.height - (var5.top - var4.top) - (var5.bottom - var4.bottom));
      }

   }
}
