package com.sun.java.swing.plaf.motif;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MotifScrollPaneUI extends BasicScrollPaneUI {
   private static final Border vsbMarginBorderR = new EmptyBorder(0, 4, 0, 0);
   private static final Border vsbMarginBorderL = new EmptyBorder(0, 0, 0, 4);
   private static final Border hsbMarginBorder = new EmptyBorder(4, 0, 0, 0);
   private CompoundBorder vsbBorder;
   private CompoundBorder hsbBorder;
   private PropertyChangeListener propertyChangeHandler;

   protected void installListeners(JScrollPane var1) {
      super.installListeners(var1);
      this.propertyChangeHandler = this.createPropertyChangeHandler();
      var1.addPropertyChangeListener(this.propertyChangeHandler);
   }

   protected void uninstallListeners(JComponent var1) {
      super.uninstallListeners(var1);
      var1.removePropertyChangeListener(this.propertyChangeHandler);
   }

   private PropertyChangeListener createPropertyChangeHandler() {
      return new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            if (var2.equals("componentOrientation")) {
               JScrollPane var3 = (JScrollPane)var1.getSource();
               JScrollBar var4 = var3.getVerticalScrollBar();
               if (var4 != null && MotifScrollPaneUI.this.vsbBorder != null && var4.getBorder() == MotifScrollPaneUI.this.vsbBorder) {
                  if (MotifGraphicsUtils.isLeftToRight(var3)) {
                     MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderR, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
                  } else {
                     MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderL, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
                  }

                  var4.setBorder(MotifScrollPaneUI.this.vsbBorder);
               }
            }

         }
      };
   }

   protected void installDefaults(JScrollPane var1) {
      super.installDefaults(var1);
      JScrollBar var2 = var1.getVerticalScrollBar();
      if (var2 != null) {
         if (MotifGraphicsUtils.isLeftToRight(var1)) {
            this.vsbBorder = new CompoundBorder(vsbMarginBorderR, var2.getBorder());
         } else {
            this.vsbBorder = new CompoundBorder(vsbMarginBorderL, var2.getBorder());
         }

         var2.setBorder(this.vsbBorder);
      }

      JScrollBar var3 = var1.getHorizontalScrollBar();
      if (var3 != null) {
         this.hsbBorder = new CompoundBorder(hsbMarginBorder, var3.getBorder());
         var3.setBorder(this.hsbBorder);
      }

   }

   protected void uninstallDefaults(JScrollPane var1) {
      super.uninstallDefaults(var1);
      JScrollBar var2 = this.scrollpane.getVerticalScrollBar();
      if (var2 != null) {
         if (var2.getBorder() == this.vsbBorder) {
            var2.setBorder((Border)null);
         }

         this.vsbBorder = null;
      }

      JScrollBar var3 = this.scrollpane.getHorizontalScrollBar();
      if (var3 != null) {
         if (var3.getBorder() == this.hsbBorder) {
            var3.setBorder((Border)null);
         }

         this.hsbBorder = null;
      }

   }

   public static ComponentUI createUI(JComponent var0) {
      return new MotifScrollPaneUI();
   }
}
