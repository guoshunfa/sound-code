package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.AppContext;
import sun.awt.OSInfo;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLayoutStyle;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class MetalLookAndFeel extends BasicLookAndFeel {
   private static boolean METAL_LOOK_AND_FEEL_INITED = false;
   private static boolean checkedWindows;
   private static boolean isWindows;
   private static boolean checkedSystemFontSettings;
   private static boolean useSystemFonts;
   static ReferenceQueue<LookAndFeel> queue = new ReferenceQueue();

   static boolean isWindows() {
      if (!checkedWindows) {
         OSInfo.OSType var0 = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
         if (var0 == OSInfo.OSType.WINDOWS) {
            isWindows = true;
            String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.useSystemFontSettings")));
            useSystemFonts = var1 != null && Boolean.valueOf(var1);
         }

         checkedWindows = true;
      }

      return isWindows;
   }

   static boolean useSystemFonts() {
      if (isWindows() && useSystemFonts) {
         if (!METAL_LOOK_AND_FEEL_INITED) {
            return true;
         } else {
            Object var0 = UIManager.get("Application.useSystemFontSettings");
            return var0 == null || Boolean.TRUE.equals(var0);
         }
      } else {
         return false;
      }
   }

   private static boolean useHighContrastTheme() {
      if (isWindows() && useSystemFonts()) {
         Boolean var0 = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
         return var0 == null ? false : var0;
      } else {
         return false;
      }
   }

   static boolean usingOcean() {
      return getCurrentTheme() instanceof OceanTheme;
   }

   public String getName() {
      return "Metal";
   }

   public String getID() {
      return "Metal";
   }

   public String getDescription() {
      return "The Java(tm) Look and Feel";
   }

   public boolean isNativeLookAndFeel() {
      return false;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }

   public boolean getSupportsWindowDecorations() {
      return true;
   }

   protected void initClassDefaults(UIDefaults var1) {
      super.initClassDefaults(var1);
      Object[] var3 = new Object[]{"ButtonUI", "javax.swing.plaf.metal.MetalButtonUI", "CheckBoxUI", "javax.swing.plaf.metal.MetalCheckBoxUI", "ComboBoxUI", "javax.swing.plaf.metal.MetalComboBoxUI", "DesktopIconUI", "javax.swing.plaf.metal.MetalDesktopIconUI", "FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI", "InternalFrameUI", "javax.swing.plaf.metal.MetalInternalFrameUI", "LabelUI", "javax.swing.plaf.metal.MetalLabelUI", "PopupMenuSeparatorUI", "javax.swing.plaf.metal.MetalPopupMenuSeparatorUI", "ProgressBarUI", "javax.swing.plaf.metal.MetalProgressBarUI", "RadioButtonUI", "javax.swing.plaf.metal.MetalRadioButtonUI", "ScrollBarUI", "javax.swing.plaf.metal.MetalScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.metal.MetalScrollPaneUI", "SeparatorUI", "javax.swing.plaf.metal.MetalSeparatorUI", "SliderUI", "javax.swing.plaf.metal.MetalSliderUI", "SplitPaneUI", "javax.swing.plaf.metal.MetalSplitPaneUI", "TabbedPaneUI", "javax.swing.plaf.metal.MetalTabbedPaneUI", "TextFieldUI", "javax.swing.plaf.metal.MetalTextFieldUI", "ToggleButtonUI", "javax.swing.plaf.metal.MetalToggleButtonUI", "ToolBarUI", "javax.swing.plaf.metal.MetalToolBarUI", "ToolTipUI", "javax.swing.plaf.metal.MetalToolTipUI", "TreeUI", "javax.swing.plaf.metal.MetalTreeUI", "RootPaneUI", "javax.swing.plaf.metal.MetalRootPaneUI"};
      var1.putDefaults(var3);
   }

   protected void initSystemColorDefaults(UIDefaults var1) {
      MetalTheme var2 = getCurrentTheme();
      ColorUIResource var3 = var2.getControl();
      Object[] var4 = new Object[]{"desktop", var2.getDesktopColor(), "activeCaption", var2.getWindowTitleBackground(), "activeCaptionText", var2.getWindowTitleForeground(), "activeCaptionBorder", var2.getPrimaryControlShadow(), "inactiveCaption", var2.getWindowTitleInactiveBackground(), "inactiveCaptionText", var2.getWindowTitleInactiveForeground(), "inactiveCaptionBorder", var2.getControlShadow(), "window", var2.getWindowBackground(), "windowBorder", var3, "windowText", var2.getUserTextColor(), "menu", var2.getMenuBackground(), "menuText", var2.getMenuForeground(), "text", var2.getWindowBackground(), "textText", var2.getUserTextColor(), "textHighlight", var2.getTextHighlightColor(), "textHighlightText", var2.getHighlightedTextColor(), "textInactiveText", var2.getInactiveSystemTextColor(), "control", var3, "controlText", var2.getControlTextColor(), "controlHighlight", var2.getControlHighlight(), "controlLtHighlight", var2.getControlHighlight(), "controlShadow", var2.getControlShadow(), "controlDkShadow", var2.getControlDarkShadow(), "scrollbar", var3, "info", var2.getPrimaryControl(), "infoText", var2.getPrimaryControlInfo()};
      var1.putDefaults(var4);
   }

   private void initResourceBundle(UIDefaults var1) {
      var1.addResourceBundle("com.sun.swing.internal.plaf.metal.resources.metal");
   }

   protected void initComponentDefaults(UIDefaults var1) {
      super.initComponentDefaults(var1);
      this.initResourceBundle(var1);
      ColorUIResource var2 = getAcceleratorForeground();
      ColorUIResource var3 = getAcceleratorSelectedForeground();
      ColorUIResource var4 = getControl();
      ColorUIResource var5 = getControlHighlight();
      ColorUIResource var6 = getControlShadow();
      ColorUIResource var7 = getControlDarkShadow();
      ColorUIResource var8 = getControlTextColor();
      ColorUIResource var9 = getFocusColor();
      ColorUIResource var10 = getInactiveControlTextColor();
      ColorUIResource var11 = getMenuBackground();
      ColorUIResource var12 = getMenuSelectedBackground();
      ColorUIResource var13 = getMenuDisabledForeground();
      ColorUIResource var14 = getMenuSelectedForeground();
      ColorUIResource var15 = getPrimaryControl();
      ColorUIResource var16 = getPrimaryControlDarkShadow();
      ColorUIResource var17 = getPrimaryControlShadow();
      ColorUIResource var18 = getSystemTextColor();
      InsetsUIResource var19 = new InsetsUIResource(0, 0, 0, 0);
      Integer var20 = 0;
      SwingLazyValue var21 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getTextFieldBorder");
      UIDefaults.LazyValue var22 = (var0) -> {
         return new MetalBorders.DialogBorder();
      };
      UIDefaults.LazyValue var23 = (var0) -> {
         return new MetalBorders.QuestionDialogBorder();
      };
      UIDefaults.LazyInputMap var24 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation"});
      UIDefaults.LazyInputMap var25 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-begin-line", "ctrl KP_LEFT", "caret-begin-line", "ctrl RIGHT", "caret-end-line", "ctrl KP_RIGHT", "caret-end-line", "ctrl shift LEFT", "selection-begin-line", "ctrl shift KP_LEFT", "selection-begin-line", "ctrl shift RIGHT", "selection-end-line", "ctrl shift KP_RIGHT", "selection-end-line", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation"});
      UIDefaults.LazyInputMap var26 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation"});
      SwingLazyValue var27 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder");
      SwingLazyValue var28 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getButtonBorder");
      SwingLazyValue var29 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getToggleButtonBorder");
      SwingLazyValue var30 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{var6});
      SwingLazyValue var31 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getDesktopIconBorder");
      SwingLazyValue var32 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuBarBorder");
      SwingLazyValue var33 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PopupMenuBorder");
      SwingLazyValue var34 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuItemBorder");
      String var35 = "-";
      SwingLazyValue var36 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ToolBarBorder");
      SwingLazyValue var37 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{var7, new Integer(1)});
      SwingLazyValue var38 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{var16});
      SwingLazyValue var39 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{var7});
      SwingLazyValue var40 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[]{var9});
      InsetsUIResource var41 = new InsetsUIResource(4, 2, 0, 6);
      InsetsUIResource var42 = new InsetsUIResource(0, 9, 1, 9);
      Object[] var43 = new Object[]{new Integer(16)};
      Object[] var44 = new Object[]{"OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound"};
      MetalTheme var45 = getCurrentTheme();
      MetalLookAndFeel.FontActiveValue var46 = new MetalLookAndFeel.FontActiveValue(var45, 3);
      MetalLookAndFeel.FontActiveValue var47 = new MetalLookAndFeel.FontActiveValue(var45, 0);
      MetalLookAndFeel.FontActiveValue var48 = new MetalLookAndFeel.FontActiveValue(var45, 2);
      MetalLookAndFeel.FontActiveValue var49 = new MetalLookAndFeel.FontActiveValue(var45, 4);
      MetalLookAndFeel.FontActiveValue var50 = new MetalLookAndFeel.FontActiveValue(var45, 5);
      MetalLookAndFeel.FontActiveValue var51 = new MetalLookAndFeel.FontActiveValue(var45, 1);
      Object[] var52 = new Object[]{"AuditoryCues.defaultCueList", var44, "AuditoryCues.playList", null, "TextField.border", var21, "TextField.font", var48, "PasswordField.border", var21, "PasswordField.font", var48, "PasswordField.echoChar", '•', "TextArea.font", var48, "TextPane.background", var1.get("window"), "TextPane.font", var48, "EditorPane.background", var1.get("window"), "EditorPane.font", var48, "TextField.focusInputMap", var24, "PasswordField.focusInputMap", var25, "TextArea.focusInputMap", var26, "TextPane.focusInputMap", var26, "EditorPane.focusInputMap", var26, "FormattedTextField.border", var21, "FormattedTextField.font", var48, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "Button.defaultButtonFollowsFocus", Boolean.FALSE, "Button.disabledText", var10, "Button.select", var6, "Button.border", var28, "Button.font", var47, "Button.focus", var9, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "CheckBox.disabledText", var10, "Checkbox.select", var6, "CheckBox.font", var47, "CheckBox.focus", var9, "CheckBox.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxIcon"), "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "RadioButton.disabledText", var10, "RadioButton.select", var6, "RadioButton.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonIcon"), "RadioButton.font", var47, "RadioButton.focus", var9, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "ToggleButton.select", var6, "ToggleButton.disabledText", var10, "ToggleButton.focus", var9, "ToggleButton.border", var29, "ToggleButton.font", var47, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "FileView.directoryIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "FileView.fileIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "FileView.computerIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeComputerIcon"), "FileView.hardDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeHardDriveIcon"), "FileView.floppyDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFloppyDriveIcon"), "FileChooser.detailsViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"), "FileChooser.homeFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"), "FileChooser.listViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"), "FileChooser.newFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"), "FileChooser.upFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"), "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up"}), "ToolTip.font", var51, "ToolTip.border", var38, "ToolTip.borderInactive", var39, "ToolTip.backgroundInactive", var4, "ToolTip.foregroundInactive", var7, "ToolTip.hideAccelerator", Boolean.FALSE, "ToolTipManager.enableToolTipMode", "activeApplication", "Slider.font", var47, "Slider.border", null, "Slider.foreground", var17, "Slider.focus", var9, "Slider.focusInsets", var19, "Slider.trackWidth", new Integer(7), "Slider.majorTickLength", new Integer(6), "Slider.horizontalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getHorizontalSliderThumbIcon"), "Slider.verticalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getVerticalSliderThumbIcon"), "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "ProgressBar.font", var47, "ProgressBar.foreground", var17, "ProgressBar.selectionBackground", var16, "ProgressBar.border", var37, "ProgressBar.cellSpacing", var20, "ProgressBar.cellLength", 1, "ComboBox.background", var4, "ComboBox.foreground", var8, "ComboBox.selectionBackground", var17, "ComboBox.selectionForeground", var8, "ComboBox.font", var47, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious"}), "InternalFrame.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameDefaultMenuIcon"), "InternalFrame.border", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$InternalFrameBorder"), "InternalFrame.optionDialogBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$OptionDialogBorder"), "InternalFrame.paletteBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PaletteBorder"), "InternalFrame.paletteTitleHeight", new Integer(11), "InternalFrame.paletteCloseIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory$PaletteCloseIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameCloseIcon", var43), "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMaximizeIcon", var43), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMinimizeIcon", var43), "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameAltMaximizeIcon", var43), "InternalFrame.titleFont", var49, "InternalFrame.windowBindings", null, "InternalFrame.closeSound", "sounds/FrameClose.wav", "InternalFrame.maximizeSound", "sounds/FrameMaximize.wav", "InternalFrame.minimizeSound", "sounds/FrameMinimize.wav", "InternalFrame.restoreDownSound", "sounds/FrameRestoreDown.wav", "InternalFrame.restoreUpSound", "sounds/FrameRestoreUp.wav", "DesktopIcon.border", var31, "DesktopIcon.font", var47, "DesktopIcon.foreground", var8, "DesktopIcon.background", var4, "DesktopIcon.width", 160, "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious"}), "TitledBorder.font", var47, "TitledBorder.titleColor", var18, "TitledBorder.border", var30, "Label.font", var47, "Label.foreground", var18, "Label.disabledForeground", getInactiveSystemTextColor(), "List.font", var47, "List.focusCellHighlightBorder", var40, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "ScrollBar.background", var4, "ScrollBar.highlight", var5, "ScrollBar.shadow", var6, "ScrollBar.darkShadow", var7, "ScrollBar.thumb", var17, "ScrollBar.thumbShadow", var16, "ScrollBar.thumbHighlight", var15, "ScrollBar.width", new Integer(17), "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "ScrollPane.border", var27, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd"}), "TabbedPane.font", var47, "TabbedPane.tabAreaBackground", var4, "TabbedPane.background", var6, "TabbedPane.light", var4, "TabbedPane.focus", var16, "TabbedPane.selected", var4, "TabbedPane.selectHighlight", var5, "TabbedPane.tabAreaInsets", var41, "TabbedPane.tabInsets", var42, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent"}), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus"}), "Table.font", var48, "Table.focusCellHighlightBorder", var40, "Table.scrollPaneBorder", var27, "Table.dropLineColor", var9, "Table.dropLineShortColor", var16, "Table.gridColor", var6, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader"}), "Table.ascendingSortIcon", SwingUtilities2.makeIcon(this.getClass(), MetalLookAndFeel.class, "icons/sortUp.png"), "Table.descendingSortIcon", SwingUtilities2.makeIcon(this.getClass(), MetalLookAndFeel.class, "icons/sortDown.png"), "TableHeader.font", var48, "TableHeader.cellBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$TableHeaderBorder"), "MenuBar.border", var32, "MenuBar.font", var46, "MenuBar.windowBindings", new Object[]{"F10", "takeFocus"}, "Menu.border", var34, "Menu.borderPainted", Boolean.TRUE, "Menu.menuPopupOffsetX", var20, "Menu.menuPopupOffsetY", var20, "Menu.submenuPopupOffsetX", new Integer(-4), "Menu.submenuPopupOffsetY", new Integer(-3), "Menu.font", var46, "Menu.selectionForeground", var14, "Menu.selectionBackground", var12, "Menu.disabledForeground", var13, "Menu.acceleratorFont", var50, "Menu.acceleratorForeground", var2, "Menu.acceleratorSelectionForeground", var3, "Menu.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "Menu.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuArrowIcon"), "MenuItem.border", var34, "MenuItem.borderPainted", Boolean.TRUE, "MenuItem.font", var46, "MenuItem.selectionForeground", var14, "MenuItem.selectionBackground", var12, "MenuItem.disabledForeground", var13, "MenuItem.acceleratorFont", var50, "MenuItem.acceleratorForeground", var2, "MenuItem.acceleratorSelectionForeground", var3, "MenuItem.acceleratorDelimiter", var35, "MenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "MenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "MenuItem.commandSound", "sounds/MenuItemCommand.wav", "OptionPane.windowBindings", new Object[]{"ESCAPE", "close"}, "OptionPane.informationSound", "sounds/OptionPaneInformation.wav", "OptionPane.warningSound", "sounds/OptionPaneWarning.wav", "OptionPane.errorSound", "sounds/OptionPaneError.wav", "OptionPane.questionSound", "sounds/OptionPaneQuestion.wav", "OptionPane.errorDialog.border.background", new ColorUIResource(153, 51, 51), "OptionPane.errorDialog.titlePane.foreground", new ColorUIResource(51, 0, 0), "OptionPane.errorDialog.titlePane.background", new ColorUIResource(255, 153, 153), "OptionPane.errorDialog.titlePane.shadow", new ColorUIResource(204, 102, 102), "OptionPane.questionDialog.border.background", new ColorUIResource(51, 102, 51), "OptionPane.questionDialog.titlePane.foreground", new ColorUIResource(0, 51, 0), "OptionPane.questionDialog.titlePane.background", new ColorUIResource(153, 204, 153), "OptionPane.questionDialog.titlePane.shadow", new ColorUIResource(102, 153, 102), "OptionPane.warningDialog.border.background", new ColorUIResource(153, 102, 51), "OptionPane.warningDialog.titlePane.foreground", new ColorUIResource(102, 51, 0), "OptionPane.warningDialog.titlePane.background", new ColorUIResource(255, 204, 153), "OptionPane.warningDialog.titlePane.shadow", new ColorUIResource(204, 153, 102), "Separator.background", getSeparatorBackground(), "Separator.foreground", getSeparatorForeground(), "PopupMenu.border", var33, "PopupMenu.popupSound", "sounds/PopupMenuPopup.wav", "PopupMenu.font", var46, "CheckBoxMenuItem.border", var34, "CheckBoxMenuItem.borderPainted", Boolean.TRUE, "CheckBoxMenuItem.font", var46, "CheckBoxMenuItem.selectionForeground", var14, "CheckBoxMenuItem.selectionBackground", var12, "CheckBoxMenuItem.disabledForeground", var13, "CheckBoxMenuItem.acceleratorFont", var50, "CheckBoxMenuItem.acceleratorForeground", var2, "CheckBoxMenuItem.acceleratorSelectionForeground", var3, "CheckBoxMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxMenuItemIcon"), "CheckBoxMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "CheckBoxMenuItem.commandSound", "sounds/MenuItemCommand.wav", "RadioButtonMenuItem.border", var34, "RadioButtonMenuItem.borderPainted", Boolean.TRUE, "RadioButtonMenuItem.font", var46, "RadioButtonMenuItem.selectionForeground", var14, "RadioButtonMenuItem.selectionBackground", var12, "RadioButtonMenuItem.disabledForeground", var13, "RadioButtonMenuItem.acceleratorFont", var50, "RadioButtonMenuItem.acceleratorForeground", var2, "RadioButtonMenuItem.acceleratorSelectionForeground", var3, "RadioButtonMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonMenuItemIcon"), "RadioButtonMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "RadioButtonMenuItem.commandSound", "sounds/MenuItemCommand.wav", "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "Spinner.arrowButtonInsets", var19, "Spinner.border", var21, "Spinner.arrowButtonBorder", var28, "Spinner.font", var47, "SplitPane.dividerSize", new Integer(10), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward"}), "SplitPane.centerOneTouchButtons", Boolean.FALSE, "SplitPane.dividerFocusColor", var15, "Tree.font", var48, "Tree.textBackground", getWindowBackground(), "Tree.selectionBorderColor", var9, "Tree.openIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "Tree.closedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "Tree.leafIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "Tree.expandedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[]{false}), "Tree.collapsedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[]{true}), "Tree.line", var15, "Tree.hash", var15, "Tree.rowHeight", var20, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"}), "ToolBar.border", var36, "ToolBar.background", var11, "ToolBar.foreground", getMenuForeground(), "ToolBar.font", var46, "ToolBar.dockingBackground", var11, "ToolBar.floatingBackground", var11, "ToolBar.dockingForeground", var16, "ToolBar.floatingForeground", var15, "ToolBar.rolloverBorder", (var0) -> {
         return MetalBorders.getToolBarRolloverBorder();
      }, "ToolBar.nonrolloverBorder", (var0) -> {
         return MetalBorders.getToolBarNonrolloverBorder();
      }, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight"}), "RootPane.frameBorder", (var0) -> {
         return new MetalBorders.FrameBorder();
      }, "RootPane.plainDialogBorder", var22, "RootPane.informationDialogBorder", var22, "RootPane.errorDialogBorder", (var0) -> {
         return new MetalBorders.ErrorDialogBorder();
      }, "RootPane.colorChooserDialogBorder", var23, "RootPane.fileChooserDialogBorder", var23, "RootPane.questionDialogBorder", var23, "RootPane.warningDialogBorder", (var0) -> {
         return new MetalBorders.WarningDialogBorder();
      }, "RootPane.defaultButtonWindowKeyBindings", new Object[]{"ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release"}};
      var1.putDefaults(var52);
      if (isWindows() && useSystemFonts() && var45.isSystemTheme()) {
         MetalFontDesktopProperty var53 = new MetalFontDesktopProperty("win.messagebox.font.height", 0);
         var52 = new Object[]{"OptionPane.messageFont", var53, "OptionPane.buttonFont", var53};
         var1.putDefaults(var52);
      }

      flushUnreferenced();
      boolean var55 = SwingUtilities2.isLocalDisplay();
      SwingUtilities2.AATextInfo var54 = SwingUtilities2.AATextInfo.getAATextInfo(var55);
      var1.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var54);
      new MetalLookAndFeel.AATextListener(this);
   }

   protected void createDefaultTheme() {
      getCurrentTheme();
   }

   public UIDefaults getDefaults() {
      METAL_LOOK_AND_FEEL_INITED = true;
      this.createDefaultTheme();
      UIDefaults var1 = super.getDefaults();
      MetalTheme var2 = getCurrentTheme();
      var2.addCustomEntriesToTable(var1);
      var2.install();
      return var1;
   }

   public void provideErrorFeedback(Component var1) {
      super.provideErrorFeedback(var1);
   }

   public static void setCurrentTheme(MetalTheme var0) {
      if (var0 == null) {
         throw new NullPointerException("Can't have null theme");
      } else {
         AppContext.getAppContext().put("currentMetalTheme", var0);
      }
   }

   public static MetalTheme getCurrentTheme() {
      AppContext var1 = AppContext.getAppContext();
      Object var0 = (MetalTheme)var1.get("currentMetalTheme");
      if (var0 == null) {
         if (useHighContrastTheme()) {
            var0 = new MetalHighContrastTheme();
         } else {
            String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.metalTheme")));
            if ("steel".equals(var2)) {
               var0 = new DefaultMetalTheme();
            } else {
               var0 = new OceanTheme();
            }
         }

         setCurrentTheme((MetalTheme)var0);
      }

      return (MetalTheme)var0;
   }

   public Icon getDisabledIcon(JComponent var1, Icon var2) {
      return var2 instanceof ImageIcon && usingOcean() ? MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)var2).getImage()) : super.getDisabledIcon(var1, var2);
   }

   public Icon getDisabledSelectedIcon(JComponent var1, Icon var2) {
      return var2 instanceof ImageIcon && usingOcean() ? MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)var2).getImage()) : super.getDisabledSelectedIcon(var1, var2);
   }

   public static FontUIResource getControlTextFont() {
      return getCurrentTheme().getControlTextFont();
   }

   public static FontUIResource getSystemTextFont() {
      return getCurrentTheme().getSystemTextFont();
   }

   public static FontUIResource getUserTextFont() {
      return getCurrentTheme().getUserTextFont();
   }

   public static FontUIResource getMenuTextFont() {
      return getCurrentTheme().getMenuTextFont();
   }

   public static FontUIResource getWindowTitleFont() {
      return getCurrentTheme().getWindowTitleFont();
   }

   public static FontUIResource getSubTextFont() {
      return getCurrentTheme().getSubTextFont();
   }

   public static ColorUIResource getDesktopColor() {
      return getCurrentTheme().getDesktopColor();
   }

   public static ColorUIResource getFocusColor() {
      return getCurrentTheme().getFocusColor();
   }

   public static ColorUIResource getWhite() {
      return getCurrentTheme().getWhite();
   }

   public static ColorUIResource getBlack() {
      return getCurrentTheme().getBlack();
   }

   public static ColorUIResource getControl() {
      return getCurrentTheme().getControl();
   }

   public static ColorUIResource getControlShadow() {
      return getCurrentTheme().getControlShadow();
   }

   public static ColorUIResource getControlDarkShadow() {
      return getCurrentTheme().getControlDarkShadow();
   }

   public static ColorUIResource getControlInfo() {
      return getCurrentTheme().getControlInfo();
   }

   public static ColorUIResource getControlHighlight() {
      return getCurrentTheme().getControlHighlight();
   }

   public static ColorUIResource getControlDisabled() {
      return getCurrentTheme().getControlDisabled();
   }

   public static ColorUIResource getPrimaryControl() {
      return getCurrentTheme().getPrimaryControl();
   }

   public static ColorUIResource getPrimaryControlShadow() {
      return getCurrentTheme().getPrimaryControlShadow();
   }

   public static ColorUIResource getPrimaryControlDarkShadow() {
      return getCurrentTheme().getPrimaryControlDarkShadow();
   }

   public static ColorUIResource getPrimaryControlInfo() {
      return getCurrentTheme().getPrimaryControlInfo();
   }

   public static ColorUIResource getPrimaryControlHighlight() {
      return getCurrentTheme().getPrimaryControlHighlight();
   }

   public static ColorUIResource getSystemTextColor() {
      return getCurrentTheme().getSystemTextColor();
   }

   public static ColorUIResource getControlTextColor() {
      return getCurrentTheme().getControlTextColor();
   }

   public static ColorUIResource getInactiveControlTextColor() {
      return getCurrentTheme().getInactiveControlTextColor();
   }

   public static ColorUIResource getInactiveSystemTextColor() {
      return getCurrentTheme().getInactiveSystemTextColor();
   }

   public static ColorUIResource getUserTextColor() {
      return getCurrentTheme().getUserTextColor();
   }

   public static ColorUIResource getTextHighlightColor() {
      return getCurrentTheme().getTextHighlightColor();
   }

   public static ColorUIResource getHighlightedTextColor() {
      return getCurrentTheme().getHighlightedTextColor();
   }

   public static ColorUIResource getWindowBackground() {
      return getCurrentTheme().getWindowBackground();
   }

   public static ColorUIResource getWindowTitleBackground() {
      return getCurrentTheme().getWindowTitleBackground();
   }

   public static ColorUIResource getWindowTitleForeground() {
      return getCurrentTheme().getWindowTitleForeground();
   }

   public static ColorUIResource getWindowTitleInactiveBackground() {
      return getCurrentTheme().getWindowTitleInactiveBackground();
   }

   public static ColorUIResource getWindowTitleInactiveForeground() {
      return getCurrentTheme().getWindowTitleInactiveForeground();
   }

   public static ColorUIResource getMenuBackground() {
      return getCurrentTheme().getMenuBackground();
   }

   public static ColorUIResource getMenuForeground() {
      return getCurrentTheme().getMenuForeground();
   }

   public static ColorUIResource getMenuSelectedBackground() {
      return getCurrentTheme().getMenuSelectedBackground();
   }

   public static ColorUIResource getMenuSelectedForeground() {
      return getCurrentTheme().getMenuSelectedForeground();
   }

   public static ColorUIResource getMenuDisabledForeground() {
      return getCurrentTheme().getMenuDisabledForeground();
   }

   public static ColorUIResource getSeparatorBackground() {
      return getCurrentTheme().getSeparatorBackground();
   }

   public static ColorUIResource getSeparatorForeground() {
      return getCurrentTheme().getSeparatorForeground();
   }

   public static ColorUIResource getAcceleratorForeground() {
      return getCurrentTheme().getAcceleratorForeground();
   }

   public static ColorUIResource getAcceleratorSelectedForeground() {
      return getCurrentTheme().getAcceleratorSelectedForeground();
   }

   public LayoutStyle getLayoutStyle() {
      return MetalLookAndFeel.MetalLayoutStyle.INSTANCE;
   }

   static void flushUnreferenced() {
      MetalLookAndFeel.AATextListener var0;
      while((var0 = (MetalLookAndFeel.AATextListener)queue.poll()) != null) {
         var0.dispose();
      }

   }

   private static class MetalLayoutStyle extends DefaultLayoutStyle {
      private static MetalLookAndFeel.MetalLayoutStyle INSTANCE = new MetalLookAndFeel.MetalLayoutStyle();

      public int getPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, Container var5) {
         super.getPreferredGap(var1, var2, var3, var4, var5);
         byte var6 = 0;
         switch(var3) {
         case INDENT:
            if (var4 == 3 || var4 == 7) {
               int var9 = this.getIndent(var1, var4);
               return var9 > 0 ? var9 : 12;
            }
         case RELATED:
            if (var1.getUIClassID() == "ToggleButtonUI" && var2.getUIClassID() == "ToggleButtonUI") {
               ButtonModel var7 = ((JToggleButton)var1).getModel();
               ButtonModel var8 = ((JToggleButton)var2).getModel();
               if (var7 instanceof DefaultButtonModel && var8 instanceof DefaultButtonModel && ((DefaultButtonModel)var7).getGroup() == ((DefaultButtonModel)var8).getGroup() && ((DefaultButtonModel)var7).getGroup() != null) {
                  return 2;
               }

               if (MetalLookAndFeel.usingOcean()) {
                  return 6;
               }

               return 5;
            }

            var6 = 6;
            break;
         case UNRELATED:
            var6 = 12;
         }

         if (this.isLabelAndNonlabel(var1, var2, var4)) {
            return this.getButtonGap(var1, var2, var4, var6 + 6);
         } else {
            return this.getButtonGap(var1, var2, var4, var6);
         }
      }

      public int getContainerGap(JComponent var1, int var2, Container var3) {
         super.getContainerGap(var1, var2, var3);
         return this.getButtonGap(var1, var2, 12 - this.getButtonAdjustment(var1, var2));
      }

      protected int getButtonGap(JComponent var1, JComponent var2, int var3, int var4) {
         var4 = super.getButtonGap(var1, var2, var3, var4);
         if (var4 > 0) {
            int var5 = this.getButtonAdjustment(var1, var3);
            if (var5 == 0) {
               var5 = this.getButtonAdjustment(var2, this.flipDirection(var3));
            }

            var4 -= var5;
         }

         return var4 < 0 ? 0 : var4;
      }

      private int getButtonAdjustment(JComponent var1, int var2) {
         String var3 = var1.getUIClassID();
         if (var3 != "ButtonUI" && var3 != "ToggleButtonUI") {
            if (var2 == 5 && (var3 == "RadioButtonUI" || var3 == "CheckBoxUI") && !MetalLookAndFeel.usingOcean()) {
               return 1;
            }
         } else if (!MetalLookAndFeel.usingOcean() && (var2 == 3 || var2 == 5) && var1.getBorder() instanceof UIResource) {
            return 1;
         }

         return 0;
      }
   }

   static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener {
      private String key = "awt.font.desktophints";
      private static boolean updatePending;

      AATextListener(LookAndFeel var1) {
         super(var1, MetalLookAndFeel.queue);
         Toolkit var2 = Toolkit.getDefaultToolkit();
         var2.addPropertyChangeListener(this.key, this);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         LookAndFeel var2 = (LookAndFeel)this.get();
         if (var2 != null && var2 == UIManager.getLookAndFeel()) {
            UIDefaults var3 = UIManager.getLookAndFeelDefaults();
            boolean var4 = SwingUtilities2.isLocalDisplay();
            SwingUtilities2.AATextInfo var5 = SwingUtilities2.AATextInfo.getAATextInfo(var4);
            var3.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var5);
            this.updateUI();
         } else {
            this.dispose();
         }
      }

      void dispose() {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         var1.removePropertyChangeListener(this.key, this);
      }

      private static void updateWindowUI(Window var0) {
         SwingUtilities.updateComponentTreeUI(var0);
         Window[] var1 = var0.getOwnedWindows();
         Window[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Window var5 = var2[var4];
            updateWindowUI(var5);
         }

      }

      private static void updateAllUIs() {
         Frame[] var0 = Frame.getFrames();
         Frame[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Frame var4 = var1[var3];
            updateWindowUI(var4);
         }

      }

      private static synchronized void setUpdatePending(boolean var0) {
         updatePending = var0;
      }

      private static synchronized boolean isUpdatePending() {
         return updatePending;
      }

      protected void updateUI() {
         if (!isUpdatePending()) {
            setUpdatePending(true);
            Runnable var1 = new Runnable() {
               public void run() {
                  MetalLookAndFeel.AATextListener.updateAllUIs();
                  MetalLookAndFeel.AATextListener.setUpdatePending(false);
               }
            };
            SwingUtilities.invokeLater(var1);
         }

      }
   }

   private static class FontActiveValue implements UIDefaults.ActiveValue {
      private int type;
      private MetalTheme theme;

      FontActiveValue(MetalTheme var1, int var2) {
         this.theme = var1;
         this.type = var2;
      }

      public Object createValue(UIDefaults var1) {
         FontUIResource var2 = null;
         switch(this.type) {
         case 0:
            var2 = this.theme.getControlTextFont();
            break;
         case 1:
            var2 = this.theme.getSystemTextFont();
            break;
         case 2:
            var2 = this.theme.getUserTextFont();
            break;
         case 3:
            var2 = this.theme.getMenuTextFont();
            break;
         case 4:
            var2 = this.theme.getWindowTitleFont();
            break;
         case 5:
            var2 = this.theme.getSubTextFont();
         }

         return var2;
      }
   }
}
