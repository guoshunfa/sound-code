package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;

public class AquaScrollRegionBorder extends AquaBorder {
   static final AquaUtils.RecyclableSingletonFromDefaultConstructor<AquaScrollRegionBorder> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaScrollRegionBorder.class);

   public static AquaScrollRegionBorder getScrollRegionBorder() {
      return (AquaScrollRegionBorder)instance.get();
   }

   public AquaScrollRegionBorder() {
      super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(2, 2, 2, 2)));
   }

   protected AquaPainter<? extends JRSUIState> createPainter() {
      JRSUIState var1 = JRSUIState.getInstance();
      var1.set(JRSUIConstants.Widget.FRAME_LIST_BOX);
      return AquaPainter.create(var1, 7, 7, 3, 3, 3, 3);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JRSUIConstants.State var7 = this.getState((JComponent)var1);
      this.painter.state.set(var7);
      this.painter.state.set(this.isFocused(var1) && var7 == JRSUIConstants.State.ACTIVE ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
      this.painter.paint(var2, var1, var3, var4, var5, var6);
   }

   protected JRSUIConstants.State getState(JComponent var1) {
      if (!AquaFocusHandler.isActive(var1)) {
         return JRSUIConstants.State.INACTIVE;
      } else {
         return !var1.isEnabled() ? JRSUIConstants.State.DISABLED : JRSUIConstants.State.ACTIVE;
      }
   }
}
