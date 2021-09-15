package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;

public abstract class AquaButtonBorder extends AquaBorder implements Border, UIResource {
   public static final AquaUtils.RecyclableSingleton<AquaButtonBorder.Dynamic> fDynamic = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonBorder.Dynamic.class);
   private static final AquaUtils.RecyclableSingleton<AquaButtonBorder.Toggle> fToggle = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonBorder.Toggle.class);
   public static final AquaUtils.RecyclableSingleton<AquaButtonBorder.Toolbar> fToolBar = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonBorder.Toolbar.class);
   public static final AquaUtils.RecyclableSingleton<AquaButtonBorder.Named> fBevel = new AquaUtils.RecyclableSingleton<AquaButtonBorder.Named>() {
      protected AquaButtonBorder.Named getInstance() {
         return new AquaButtonBorder.Named(JRSUIConstants.Widget.BUTTON_BEVEL, new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(2, 4, 2, 4)));
      }
   };

   public static AquaButtonBorder getDynamicButtonBorder() {
      return (AquaButtonBorder)fDynamic.get();
   }

   public static AquaButtonBorder getToggleButtonBorder() {
      return (AquaButtonBorder)fToggle.get();
   }

   public static Border getToolBarButtonBorder() {
      return (Border)fToolBar.get();
   }

   public static AquaButtonBorder getBevelButtonBorder() {
      return (AquaButtonBorder)fBevel.get();
   }

   public AquaButtonBorder(AquaUtilControlSize.SizeDescriptor var1) {
      super(var1);
   }

   public AquaButtonBorder(AquaButtonBorder var1) {
      super((AquaBorder)var1);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public void paintButton(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      AbstractButton var7 = (AbstractButton)var1;
      ButtonModel var8 = var7.getModel();
      JRSUIConstants.State var9 = this.getButtonState(var7, var8);
      this.painter.state.set(var9);
      this.painter.state.set(var9 != JRSUIConstants.State.DISABLED && var9 != JRSUIConstants.State.INACTIVE && var7.isFocusPainted() && this.isFocused(var7) ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
      Insets var10 = this.sizeVariant.insets;
      var3 += var10.left;
      var4 += var10.top;
      var5 -= var10.left + var10.right;
      var6 -= var10.top + var10.bottom;
      this.doButtonPaint(var7, var8, var2, var3, var4, var5, var6);
   }

   protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
      this.painter.paint(var3, var1, var4, var5, var6, var7);
   }

   protected JRSUIConstants.State getButtonState(AbstractButton var1, ButtonModel var2) {
      if (!var1.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else if (!AquaFocusHandler.isActive(var1)) {
         return JRSUIConstants.State.INACTIVE;
      } else if (var2.isArmed() && var2.isPressed()) {
         return JRSUIConstants.State.PRESSED;
      } else if (var2.isSelected() && this.isSelectionPressing()) {
         return JRSUIConstants.State.PRESSED;
      } else {
         return var1 instanceof JButton && ((JButton)var1).isDefaultButton() ? JRSUIConstants.State.PULSED : JRSUIConstants.State.ACTIVE;
      }
   }

   protected boolean isSelectionPressing() {
      return true;
   }

   public boolean hasSmallerInsets(JComponent var1) {
      Insets var2 = var1.getInsets();
      Insets var3 = this.sizeVariant.margins;
      if (var3.equals(var2)) {
         return false;
      } else {
         return var2.top < var3.top || var2.left < var3.left || var2.right < var3.right || var2.bottom < var3.bottom;
      }
   }

   public Insets getBorderInsets(Component var1) {
      if (var1 != null && var1 instanceof AbstractButton) {
         Insets var2 = ((AbstractButton)var1).getMargin();
         Object var3 = var2 == null ? new InsetsUIResource(0, 0, 0, 0) : (Insets)var2.clone();
         ((Insets)var3).top += this.sizeVariant.margins.top;
         ((Insets)var3).bottom += this.sizeVariant.margins.bottom;
         ((Insets)var3).left += this.sizeVariant.margins.left;
         ((Insets)var3).right += this.sizeVariant.margins.right;
         return (Insets)var3;
      } else {
         return new Insets(0, 0, 0, 0);
      }
   }

   public Insets getContentInsets(AbstractButton var1, int var2, int var3) {
      return null;
   }

   public void alterPreferredSize(Dimension var1) {
      if (this.sizeVariant.h > 0 && this.sizeVariant.h > var1.height) {
         var1.height = this.sizeVariant.h;
      }

      if (this.sizeVariant.w > 0 && this.sizeVariant.w > var1.width) {
         var1.width = this.sizeVariant.w;
      }

   }

   public boolean isBorderOpaque() {
      return false;
   }

   public static class Toolbar extends AquaButtonBorder {
      public Toolbar() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(5, 5, 5, 5)));
         this.painter.state.set(JRSUIConstants.Widget.TOOLBAR_ITEM_WELL);
      }

      public Toolbar(AquaButtonBorder.Toolbar var1) {
         super((AquaButtonBorder)var1);
      }

      protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
         if (var2.isSelected()) {
            super.doButtonPaint(var1, var2, var3, var4, var5, var6, var7);
         }
      }
   }

   public static class Named extends AquaButtonBorder {
      public Named(JRSUIConstants.Widget var1, AquaUtilControlSize.SizeDescriptor var2) {
         super(var2);
         this.painter.state.set(var1);
      }

      public Named(AquaButtonBorder.Named var1) {
         super((AquaButtonBorder)var1);
      }

      protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
         this.painter.state.set(var2.isSelected() ? JRSUIConstants.BooleanValue.YES : JRSUIConstants.BooleanValue.NO);
         super.doButtonPaint(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public static class Toggle extends AquaButtonBorder {
      public Toggle() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(6, 6, 6, 6)));
      }

      public Toggle(AquaButtonBorder.Toggle var1) {
         super((AquaButtonBorder)var1);
      }

      protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
         if (var7 >= 23 && var6 >= 16) {
            this.painter.state.set(JRSUIConstants.Widget.BUTTON_BEVEL_ROUND);
            super.doButtonPaint(var1, var2, var3, var4, var5 + 1, var6, var7 - 1);
         } else {
            this.painter.state.set(JRSUIConstants.Widget.BUTTON_BEVEL);
            super.doButtonPaint(var1, var2, var3, var4, var5, var6, var7);
         }
      }
   }

   public static class Dynamic extends AquaButtonBorder {
      final Insets ALTERNATE_PUSH_INSETS = new Insets(3, 12, 5, 12);
      final Insets ALTERNATE_BEVEL_INSETS = new Insets(0, 5, 0, 5);
      final Insets ALTERNATE_SQUARE_INSETS = new Insets(0, 2, 0, 2);

      public Dynamic() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant(75, 29)).alterMargins(3, 20, 5, 20)) {
            public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveSmall(var1.alterMinSize(0, -2).alterMargins(0, -3, 0, -3).alterInsets(-3, -3, -4, -3));
            }

            public AquaUtilControlSize.SizeVariant deriveMini(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveMini(var1.alterMinSize(0, -2).alterMargins(0, -3, 0, -3).alterInsets(-3, -3, -1, -3));
            }
         });
      }

      public Dynamic(AquaButtonBorder.Dynamic var1) {
         super((AquaButtonBorder)var1);
      }

      protected JRSUIConstants.State getButtonState(AbstractButton var1, ButtonModel var2) {
         JRSUIConstants.State var3 = super.getButtonState(var1, var2);
         this.painter.state.set(var3 == JRSUIConstants.State.PULSED ? JRSUIConstants.Animating.YES : JRSUIConstants.Animating.NO);
         return var3;
      }

      public Insets getContentInsets(AbstractButton var1, int var2, int var3) {
         JRSUIConstants.Size var4 = AquaUtilControlSize.getUserSizeFrom(var1);
         JRSUIConstants.Widget var5 = this.getStyleForSize(var1, var4, var2, var3);
         if (var5 == JRSUIConstants.Widget.BUTTON_PUSH) {
            return this.ALTERNATE_PUSH_INSETS;
         } else if (var5 == JRSUIConstants.Widget.BUTTON_BEVEL_ROUND) {
            return this.ALTERNATE_BEVEL_INSETS;
         } else {
            return var5 == JRSUIConstants.Widget.BUTTON_BEVEL ? this.ALTERNATE_SQUARE_INSETS : null;
         }
      }

      protected void doButtonPaint(AbstractButton var1, ButtonModel var2, Graphics var3, int var4, int var5, int var6, int var7) {
         JRSUIConstants.Size var8 = AquaUtilControlSize.getUserSizeFrom(var1);
         this.painter.state.set(var8);
         JRSUIConstants.Widget var9 = this.getStyleForSize(var1, var8, var6, var7);
         this.painter.state.set(var9);
         if (var9 == JRSUIConstants.Widget.BUTTON_PUSH && var5 % 2 == 0) {
            if (var8 == JRSUIConstants.Size.REGULAR) {
               ++var5;
               --var7;
            }

            if (var8 == JRSUIConstants.Size.MINI) {
               --var7;
               var4 += 4;
               var6 -= 8;
            }
         }

         super.doButtonPaint(var1, var2, var3, var4, var5, var6, var7);
      }

      protected JRSUIConstants.Widget getStyleForSize(AbstractButton var1, JRSUIConstants.Size var2, int var3, int var4) {
         if (var2 != null && var2 != JRSUIConstants.Size.REGULAR) {
            return JRSUIConstants.Widget.BUTTON_PUSH;
         } else if (var4 >= 23 && var3 >= 16) {
            if (var4 <= 32 && var3 < 40) {
               return JRSUIConstants.Widget.BUTTON_BEVEL;
            } else {
               return var4 <= 32 && var1.getIcon() == null && !this.hasSmallerInsets(var1) ? JRSUIConstants.Widget.BUTTON_PUSH : JRSUIConstants.Widget.BUTTON_BEVEL_ROUND;
            }
         } else {
            return JRSUIConstants.Widget.BUTTON_BEVEL;
         }
      }
   }

   static class SizeConstants {
      protected static final int fNormalButtonHeight = 29;
      protected static final int fNormalMinButtonWidth = 40;
      protected static final int fSquareButtonHeightThreshold = 23;
      protected static final int fSquareButtonWidthThreshold = 16;
   }
}
