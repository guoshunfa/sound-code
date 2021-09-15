package com.apple.laf;

import apple.laf.JRSUIConstants;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class AquaButtonRadioUI extends AquaButtonLabeledUI {
   protected static final AquaUtils.RecyclableSingleton<AquaButtonRadioUI> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonRadioUI.class);
   protected static final AquaUtils.RecyclableSingleton<ImageIcon> sizingIcon = new AquaUtils.RecyclableSingleton<ImageIcon>() {
      protected ImageIcon getInstance() {
         return new ImageIcon(AquaNativeResources.getRadioButtonSizerImage());
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)instance.get();
   }

   public static Icon getSizingRadioButtonIcon() {
      return (Icon)sizingIcon.get();
   }

   protected String getPropertyPrefix() {
      return "RadioButton.";
   }

   protected AquaButtonBorder getPainter() {
      return new AquaButtonRadioUI.RadioButtonBorder();
   }

   public static class RadioButtonBorder extends AquaButtonLabeledUI.LabeledButtonBorder {
      public RadioButtonBorder() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).replaceMargins("RadioButton.margin")));
         this.painter.state.set(JRSUIConstants.Widget.BUTTON_RADIO);
      }

      public RadioButtonBorder(AquaButtonRadioUI.RadioButtonBorder var1) {
         super((AquaButtonLabeledUI.LabeledButtonBorder)var1);
      }
   }
}
