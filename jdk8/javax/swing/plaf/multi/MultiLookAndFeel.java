package javax.swing.plaf.multi;

import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class MultiLookAndFeel extends LookAndFeel {
   public String getName() {
      return "Multiplexing Look and Feel";
   }

   public String getID() {
      return "Multiplex";
   }

   public String getDescription() {
      return "Allows multiple UI instances per component instance";
   }

   public boolean isNativeLookAndFeel() {
      return false;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }

   public UIDefaults getDefaults() {
      String var1 = "javax.swing.plaf.multi.Multi";
      Object[] var2 = new Object[]{"ButtonUI", var1 + "ButtonUI", "CheckBoxMenuItemUI", var1 + "MenuItemUI", "CheckBoxUI", var1 + "ButtonUI", "ColorChooserUI", var1 + "ColorChooserUI", "ComboBoxUI", var1 + "ComboBoxUI", "DesktopIconUI", var1 + "DesktopIconUI", "DesktopPaneUI", var1 + "DesktopPaneUI", "EditorPaneUI", var1 + "TextUI", "FileChooserUI", var1 + "FileChooserUI", "FormattedTextFieldUI", var1 + "TextUI", "InternalFrameUI", var1 + "InternalFrameUI", "LabelUI", var1 + "LabelUI", "ListUI", var1 + "ListUI", "MenuBarUI", var1 + "MenuBarUI", "MenuItemUI", var1 + "MenuItemUI", "MenuUI", var1 + "MenuItemUI", "OptionPaneUI", var1 + "OptionPaneUI", "PanelUI", var1 + "PanelUI", "PasswordFieldUI", var1 + "TextUI", "PopupMenuSeparatorUI", var1 + "SeparatorUI", "PopupMenuUI", var1 + "PopupMenuUI", "ProgressBarUI", var1 + "ProgressBarUI", "RadioButtonMenuItemUI", var1 + "MenuItemUI", "RadioButtonUI", var1 + "ButtonUI", "RootPaneUI", var1 + "RootPaneUI", "ScrollBarUI", var1 + "ScrollBarUI", "ScrollPaneUI", var1 + "ScrollPaneUI", "SeparatorUI", var1 + "SeparatorUI", "SliderUI", var1 + "SliderUI", "SpinnerUI", var1 + "SpinnerUI", "SplitPaneUI", var1 + "SplitPaneUI", "TabbedPaneUI", var1 + "TabbedPaneUI", "TableHeaderUI", var1 + "TableHeaderUI", "TableUI", var1 + "TableUI", "TextAreaUI", var1 + "TextUI", "TextFieldUI", var1 + "TextUI", "TextPaneUI", var1 + "TextUI", "ToggleButtonUI", var1 + "ButtonUI", "ToolBarSeparatorUI", var1 + "SeparatorUI", "ToolBarUI", var1 + "ToolBarUI", "ToolTipUI", var1 + "ToolTipUI", "TreeUI", var1 + "TreeUI", "ViewportUI", var1 + "ViewportUI"};
      MultiUIDefaults var3 = new MultiUIDefaults(var2.length / 2, 0.75F);
      var3.putDefaults(var2);
      return var3;
   }

   public static ComponentUI createUIs(ComponentUI var0, Vector var1, JComponent var2) {
      ComponentUI var3 = UIManager.getDefaults().getUI(var2);
      if (var3 == null) {
         return null;
      } else {
         var1.addElement(var3);
         LookAndFeel[] var4 = UIManager.getAuxiliaryLookAndFeels();
         if (var4 != null) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               var3 = var4[var5].getDefaults().getUI(var2);
               if (var3 != null) {
                  var1.addElement(var3);
               }
            }
         }

         return var1.size() == 1 ? (ComponentUI)var1.elementAt(0) : var0;
      }
   }

   protected static ComponentUI[] uisToArray(Vector var0) {
      if (var0 == null) {
         return new ComponentUI[0];
      } else {
         int var1 = var0.size();
         if (var1 <= 0) {
            return null;
         } else {
            ComponentUI[] var2 = new ComponentUI[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
               var2[var3] = (ComponentUI)var0.elementAt(var3);
            }

            return var2;
         }
      }
   }
}
