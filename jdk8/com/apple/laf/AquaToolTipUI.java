package com.apple.laf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

public class AquaToolTipUI extends BasicToolTipUI {
   static final AquaUtils.RecyclableSingletonFromDefaultConstructor<AquaToolTipUI> sharedAquaInstance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaToolTipUI.class);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)sharedAquaInstance.get();
   }
}
