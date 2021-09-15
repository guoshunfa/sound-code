package com.apple.laf;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

public class AquaPanelUI extends BasicPanelUI {
   static AquaUtils.RecyclableSingleton<AquaPanelUI> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaPanelUI.class);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)instance.get();
   }

   public final void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         AquaUtils.fillRect(var1, var2);
      }

      this.paint(var1, var2);
   }
}
