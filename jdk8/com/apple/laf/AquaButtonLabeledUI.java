package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;

public abstract class AquaButtonLabeledUI extends AquaButtonToggleUI implements AquaUtilControlSize.Sizeable {
   protected static AquaButtonLabeledUI.RecyclableSizingIcon regularIcon = new AquaButtonLabeledUI.RecyclableSizingIcon(18);
   protected static AquaButtonLabeledUI.RecyclableSizingIcon smallIcon = new AquaButtonLabeledUI.RecyclableSizingIcon(16);
   protected static AquaButtonLabeledUI.RecyclableSizingIcon miniIcon = new AquaButtonLabeledUI.RecyclableSizingIcon(14);
   protected AquaButtonBorder widgetBorder = this.getPainter();

   public void applySizeFor(JComponent var1, JRSUIConstants.Size var2) {
      super.applySizeFor(var1, var2);
      this.widgetBorder = (AquaButtonBorder)this.widgetBorder.deriveBorderForSize(var2);
   }

   public Icon getDefaultIcon(JComponent var1) {
      JRSUIConstants.Size var2 = AquaUtilControlSize.getUserSizeFrom(var1);
      if (var2 == JRSUIConstants.Size.REGULAR) {
         return (Icon)regularIcon.get();
      } else if (var2 == JRSUIConstants.Size.SMALL) {
         return (Icon)smallIcon.get();
      } else {
         return var2 == JRSUIConstants.Size.MINI ? (Icon)miniIcon.get() : (Icon)regularIcon.get();
      }
   }

   protected void setThemeBorder(AbstractButton var1) {
      super.setThemeBorder(var1);
      Border var2 = var1.getBorder();
      if (var2 == null || var2 instanceof UIResource) {
         var1.setBorder(AquaButtonBorder.getBevelButtonBorder());
      }

   }

   protected abstract AquaButtonBorder getPainter();

   public synchronized void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      Font var5 = var2.getFont();
      var1.setFont(var5);
      FontMetrics var6 = var1.getFontMetrics();
      Dimension var7 = var3.getSize();
      Insets var8 = var2.getInsets();
      Rectangle var9 = new Rectangle(var3.getWidth(), var3.getHeight());
      Rectangle var10 = new Rectangle();
      Rectangle var11 = new Rectangle();
      Icon var12 = var3.getIcon();
      boolean var13 = var2.getParent() instanceof CellRendererPane;
      if (var3.isOpaque() || var13) {
         var1.setColor(var3.getBackground());
         var1.fillRect(0, 0, var7.width, var7.height);
      }

      if (((AbstractButton)var2).isBorderPainted() && !var13) {
         Border var14 = var2.getBorder();
         if (var14 instanceof AquaButtonBorder) {
            ((AquaButtonBorder)var14).paintButton(var2, var1, var9.x, var9.y, var9.width, var9.height);
         }
      }

      var9.x = var8.left;
      var9.y = var8.top;
      var9.width = var3.getWidth() - (var8.right + var9.x);
      var9.height = var3.getHeight() - (var8.bottom + var9.y);
      String var16 = SwingUtilities.layoutCompoundLabel(var2, var6, var3.getText(), var12 != null ? var12 : this.getDefaultIcon(var3), var3.getVerticalAlignment(), var3.getHorizontalAlignment(), var3.getVerticalTextPosition(), var3.getHorizontalTextPosition(), var9, var10, var11, var3.getText() == null ? 0 : var3.getIconTextGap());
      if (var12 == null) {
         this.widgetBorder.paintButton(var2, var1, var10.x, var10.y, var10.width, var10.height);
      } else {
         if (!var4.isEnabled()) {
            if (var4.isSelected()) {
               var12 = var3.getDisabledSelectedIcon();
            } else {
               var12 = var3.getDisabledIcon();
            }
         } else if (var4.isPressed() && var4.isArmed()) {
            var12 = var3.getPressedIcon();
            if (var12 == null) {
               var12 = var3.getSelectedIcon();
            }
         } else if (var4.isSelected()) {
            if (var3.isRolloverEnabled() && var4.isRollover()) {
               var12 = var3.getRolloverSelectedIcon();
               if (var12 == null) {
                  var12 = var3.getSelectedIcon();
               }
            } else {
               var12 = var3.getSelectedIcon();
            }
         } else if (var3.isRolloverEnabled() && var4.isRollover()) {
            var12 = var3.getRolloverIcon();
         }

         if (var12 == null) {
            var12 = var3.getIcon();
         }

         var12.paintIcon(var2, var1, var10.x, var10.y);
      }

      if (var16 != null) {
         View var15 = (View)var2.getClientProperty("html");
         if (var15 != null) {
            var15.paint(var1, var11);
         } else {
            this.paintText(var1, var3, var11, var16);
         }
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      if (var1.getComponentCount() > 0) {
         return null;
      } else {
         AbstractButton var2 = (AbstractButton)var1;
         String var3 = var2.getText();
         Icon var4 = var2.getIcon();
         if (var4 == null) {
            var4 = this.getDefaultIcon(var2);
         }

         Font var5 = var2.getFont();
         FontMetrics var6 = var2.getFontMetrics(var5);
         Rectangle var7 = new Rectangle(32767, 32767);
         Rectangle var8 = new Rectangle();
         Rectangle var9 = new Rectangle();
         SwingUtilities.layoutCompoundLabel(var1, var6, var3, var4, var2.getVerticalAlignment(), var2.getHorizontalAlignment(), var2.getVerticalTextPosition(), var2.getHorizontalTextPosition(), var7, var8, var9, var3 == null ? 0 : var2.getIconTextGap());
         int var10 = Math.min(var8.x, var9.x);
         int var11 = Math.max(var8.x + var8.width, var9.x + var9.width);
         int var12 = Math.min(var8.y, var9.y);
         int var13 = Math.max(var8.y + var8.height, var9.y + var9.height);
         int var14 = var11 - var10;
         int var15 = var13 - var12;
         Insets var16 = var2.getInsets();
         var14 += var16.left + var16.right;
         var15 += var16.top + var16.bottom;
         return new Dimension(var14, var15);
      }
   }

   public abstract static class LabeledButtonBorder extends AquaButtonBorder {
      public LabeledButtonBorder(AquaUtilControlSize.SizeDescriptor var1) {
         super(var1);
      }

      public LabeledButtonBorder(AquaButtonLabeledUI.LabeledButtonBorder var1) {
         super((AquaButtonBorder)var1);
      }

      protected AquaPainter<? extends JRSUIState> createPainter() {
         AquaPainter var1 = AquaPainter.create(JRSUIStateFactory.getLabeledButton());
         ((JRSUIState.ValueState)var1.state).set(JRSUIConstants.AlignmentVertical.CENTER);
         ((JRSUIState.ValueState)var1.state).set(JRSUIConstants.AlignmentHorizontal.CENTER);
         return var1;
      }

      protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
         this.painter.state.set(AquaUtilControlSize.getUserSizeFrom(var1));
         ((JRSUIState.ValueState)this.painter.state).setValue(var2.isSelected() ? (isIndeterminate(var1) ? 2.0D : 1.0D) : 0.0D);
         super.doButtonPaint(var1, var2, var3, var4, var5, var6, var7);
      }

      protected JRSUIConstants.State getButtonState(AbstractButton var1, ButtonModel var2) {
         JRSUIConstants.State var3 = super.getButtonState(var1, var2);
         if (var3 == JRSUIConstants.State.INACTIVE) {
            return JRSUIConstants.State.INACTIVE;
         } else if (var3 == JRSUIConstants.State.DISABLED) {
            return JRSUIConstants.State.DISABLED;
         } else if (var2.isArmed() && var2.isPressed()) {
            return JRSUIConstants.State.PRESSED;
         } else {
            return var2.isSelected() ? JRSUIConstants.State.ACTIVE : var3;
         }
      }

      static boolean isIndeterminate(AbstractButton var0) {
         return "indeterminate".equals(var0.getClientProperty("JButton.selectedState"));
      }
   }

   protected static class RecyclableSizingIcon extends AquaUtils.RecyclableSingleton<Icon> {
      final int iconSize;

      public RecyclableSizingIcon(int var1) {
         this.iconSize = var1;
      }

      protected Icon getInstance() {
         return new ImageIcon(new BufferedImage(this.iconSize, this.iconSize, 3));
      }
   }
}
