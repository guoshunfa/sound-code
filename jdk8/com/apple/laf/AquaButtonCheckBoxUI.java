package com.apple.laf;

import apple.laf.JRSUIConstants;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class AquaButtonCheckBoxUI extends AquaButtonLabeledUI {
   protected static final AquaUtils.RecyclableSingleton<AquaButtonCheckBoxUI> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonCheckBoxUI.class);
   protected static final AquaUtils.RecyclableSingleton<ImageIcon> sizingIcon = new AquaUtils.RecyclableSingleton<ImageIcon>() {
      protected ImageIcon getInstance() {
         return new ImageIcon(AquaNativeResources.getRadioButtonSizerImage());
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)instance.get();
   }

   public static Icon getSizingCheckBoxIcon() {
      return (Icon)sizingIcon.get();
   }

   public String getPropertyPrefix() {
      return "CheckBox.";
   }

   protected AquaButtonBorder getPainter() {
      return new AquaButtonCheckBoxUI.CheckBoxButtonBorder();
   }

   public static class CheckBoxButtonBorder extends AquaButtonLabeledUI.LabeledButtonBorder {
      public CheckBoxButtonBorder() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).replaceMargins("CheckBox.margin")));
         this.painter.state.set(JRSUIConstants.Widget.BUTTON_CHECK_BOX);
      }

      public CheckBoxButtonBorder(AquaButtonCheckBoxUI.CheckBoxButtonBorder var1) {
         super((AquaButtonLabeledUI.LabeledButtonBorder)var1);
      }
   }
}
