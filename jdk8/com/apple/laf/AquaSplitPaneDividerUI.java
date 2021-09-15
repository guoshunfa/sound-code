package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

public class AquaSplitPaneDividerUI extends BasicSplitPaneDivider {
   final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIStateFactory.getSplitPaneDivider());
   static final AquaUtils.LazyKeyedSingleton<Integer, Image> directionArrows = new AquaUtils.LazyKeyedSingleton<Integer, Image>() {
      protected Image getInstance(Integer var1) {
         Image var2 = AquaImageFactory.getArrowImageForDirection(var1);
         int var3 = var2.getHeight((ImageObserver)null) * 5 / 7;
         int var4 = var2.getWidth((ImageObserver)null) * 5 / 7;
         return AquaUtils.generateLightenedImage(var2.getScaledInstance(var4, var3, 4), 50);
      }
   };
   static final int kMaxPopupArrowSize = 9;

   public AquaSplitPaneDividerUI(AquaSplitPaneUI var1) {
      super(var1);
      this.setLayout(new AquaSplitPaneDividerUI.DividerLayout());
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getSource() == this.splitPane) {
         String var2 = var1.getPropertyName();
         if ("enabled".equals(var2)) {
            boolean var3 = this.splitPane.isEnabled();
            if (this.leftButton != null) {
               this.leftButton.setEnabled(var3);
            }

            if (this.rightButton != null) {
               this.rightButton.setEnabled(var3);
            }
         } else if ("orientation".equals(var2)) {
            if (this.rightButton != null) {
               this.remove(this.rightButton);
               this.rightButton = null;
            }

            if (this.leftButton != null) {
               this.remove(this.leftButton);
               this.leftButton = null;
            }

            this.oneTouchExpandableChanged();
         }
      }

      super.propertyChange(var1);
   }

   public int getMaxDividerSize() {
      return 10;
   }

   public void paint(Graphics var1) {
      Dimension var2 = this.getSize();
      int var3 = 0;
      int var4 = 0;
      boolean var5 = this.splitPane.getOrientation() == 0;
      int var6 = this.getMaxDividerSize();
      boolean var7 = true;
      int var8;
      if (var5) {
         if (var2.height > var6) {
            var8 = var2.height - var6;
            var4 = var8 / 2;
            var2.height = var6;
         }

         if (var2.height < 4) {
            var7 = false;
         }
      } else {
         if (var2.width > var6) {
            var8 = var2.width - var6;
            var3 = var8 / 2;
            var2.width = var6;
         }

         if (var2.width < 4) {
            var7 = false;
         }
      }

      if (var7) {
         this.painter.state.set(this.getState());
         this.painter.paint(var1, this.splitPane, var3, var4, var2.width, var2.height);
      }

      super.paint(var1);
   }

   protected JRSUIConstants.State getState() {
      return this.splitPane.isEnabled() ? JRSUIConstants.State.ACTIVE : JRSUIConstants.State.DISABLED;
   }

   protected JButton createLeftOneTouchButton() {
      return createButtonForDirection(this.getDirection(true));
   }

   protected JButton createRightOneTouchButton() {
      return createButtonForDirection(this.getDirection(false));
   }

   static JButton createButtonForDirection(int var0) {
      JButton var1 = new JButton(new ImageIcon((Image)directionArrows.get(var0)));
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setRequestFocusEnabled(false);
      var1.setFocusable(false);
      var1.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      return var1;
   }

   int getDirection(boolean var1) {
      if (this.splitPane.getOrientation() == 1) {
         return var1 ? 7 : 3;
      } else {
         return var1 ? 1 : 5;
      }
   }

   public static Border getHorizontalSplitDividerGradientVariant() {
      return AquaSplitPaneDividerUI.HorizontalSplitDividerGradientPainter.instance();
   }

   static class HorizontalSplitDividerGradientPainter implements Border {
      private static final AquaUtils.RecyclableSingleton<AquaSplitPaneDividerUI.HorizontalSplitDividerGradientPainter> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaSplitPaneDividerUI.HorizontalSplitDividerGradientPainter.class);
      final Color startColor;
      final Color endColor;
      final Color borderLines;

      HorizontalSplitDividerGradientPainter() {
         this.startColor = Color.white;
         this.endColor = new Color(217, 217, 217);
         this.borderLines = Color.lightGray;
      }

      static AquaSplitPaneDividerUI.HorizontalSplitDividerGradientPainter instance() {
         return (AquaSplitPaneDividerUI.HorizontalSplitDividerGradientPainter)instance.get();
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(0, 0, 0, 0);
      }

      public boolean isBorderOpaque() {
         return true;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var2 instanceof Graphics2D) {
            Graphics2D var7 = (Graphics2D)var2;
            Color var8 = var7.getColor();
            var7.setPaint(new GradientPaint(0.0F, 0.0F, this.startColor, 0.0F, (float)var6, this.endColor));
            var7.fillRect(var3, var4, var5, var6);
            var7.setColor(this.borderLines);
            var7.drawLine(var3, var4, var3 + var5, var4);
            var7.drawLine(var3, var4 + var6 - 1, var3 + var5, var4 + var6 - 1);
            var7.setColor(var8);
         }
      }
   }

   protected class DividerLayout extends BasicSplitPaneDivider.DividerLayout {
      protected DividerLayout() {
         super();
      }

      public void layoutContainer(Container var1) {
         int var2 = AquaSplitPaneDividerUI.this.getMaxDividerSize();
         Dimension var3 = AquaSplitPaneDividerUI.this.getSize();
         if (AquaSplitPaneDividerUI.this.leftButton != null && AquaSplitPaneDividerUI.this.rightButton != null && var1 == AquaSplitPaneDividerUI.this) {
            if (!AquaSplitPaneDividerUI.this.splitPane.isOneTouchExpandable()) {
               AquaSplitPaneDividerUI.this.leftButton.setBounds(-5, -5, 1, 1);
               AquaSplitPaneDividerUI.this.rightButton.setBounds(-5, -5, 1, 1);
            } else {
               int var4 = Math.min(AquaSplitPaneDividerUI.this.getDividerSize(), 9);
               int var5;
               int var6;
               byte var7;
               if (AquaSplitPaneDividerUI.this.orientation == 0) {
                  var5 = 0;
                  if (var3.height > var2) {
                     var6 = var3.height - var2;
                     var5 = var6 / 2;
                  }

                  var7 = 11;
                  AquaSplitPaneDividerUI.this.rightButton.setBounds(var7, var5, 9, var4);
                  var6 = var7 - 11;
                  AquaSplitPaneDividerUI.this.leftButton.setBounds(var6, var5, 9, var4);
               } else {
                  var5 = 0;
                  if (var3.width > var2) {
                     var6 = var3.width - var2;
                     var5 = var6 / 2;
                  }

                  var7 = 11;
                  AquaSplitPaneDividerUI.this.rightButton.setBounds(var5, var7, var4, 9);
                  var6 = var7 - 11;
                  AquaSplitPaneDividerUI.this.leftButton.setBounds(var5, var6, var4, 9);
               }

            }
         }
      }
   }
}
