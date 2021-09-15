package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

public abstract class AquaGroupBorder extends AquaBorder {
   static final AquaUtils.RecyclableSingletonFromDefaultConstructor<? extends Border> tabbedPaneGroupBorder = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaGroupBorder.TabbedPane.class);
   static final AquaUtils.RecyclableSingletonFromDefaultConstructor<? extends Border> titleBorderGroupBorder = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaGroupBorder.Titled.class);
   static final AquaUtils.RecyclableSingletonFromDefaultConstructor<? extends Border> titlelessGroupBorder = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaGroupBorder.Titleless.class);

   public static Border getTabbedPaneGroupBorder() {
      return (Border)tabbedPaneGroupBorder.get();
   }

   public static Border getBorderForTitledBorder() {
      return (Border)titleBorderGroupBorder.get();
   }

   public static Border getTitlelessBorder() {
      return (Border)titlelessGroupBorder.get();
   }

   protected AquaGroupBorder(AquaUtilControlSize.SizeVariant var1) {
      super(new AquaUtilControlSize.SizeDescriptor(var1));
      this.painter.state.set(JRSUIConstants.Widget.FRAME_GROUP_BOX);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Insets var7 = this.sizeVariant.insets;
      var3 += var7.left;
      var4 += var7.top;
      var5 -= var7.left + var7.right;
      var6 -= var7.top + var7.bottom;
      this.painter.paint(var2, var1, var3, var4, var5, var6);
   }

   protected static class Titleless extends AquaGroupBorder {
      public Titleless() {
         super((new AquaUtilControlSize.SizeVariant()).alterMargins(8, 12, 8, 12).alterInsets(3, 5, 1, 5));
      }
   }

   protected static class Titled extends AquaGroupBorder {
      public Titled() {
         super((new AquaUtilControlSize.SizeVariant()).alterMargins(16, 20, 16, 20).alterInsets(16, 5, 4, 5));
      }
   }

   protected static class TabbedPane extends AquaGroupBorder {
      public TabbedPane() {
         super((new AquaUtilControlSize.SizeVariant()).alterMargins(8, 12, 8, 12).alterInsets(5, 5, 7, 5));
      }
   }
}
