package com.apple.laf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class AquaButtonToggleUI extends AquaButtonUI {
   static final AquaUtils.RecyclableSingleton<AquaButtonToggleUI> aquaToggleButtonUI = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonToggleUI.class);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)aquaToggleButtonUI.get();
   }

   protected String getPropertyPrefix() {
      return "ToggleButton.";
   }
}
