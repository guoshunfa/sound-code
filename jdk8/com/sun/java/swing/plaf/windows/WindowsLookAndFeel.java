package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.OSInfo;
import sun.awt.shell.ShellFolder;
import sun.font.FontUtilities;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLayoutStyle;
import sun.swing.ImageIconUIResource;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class WindowsLookAndFeel extends BasicLookAndFeel {
   static final Object HI_RES_DISABLED_ICON_CLIENT_KEY = new StringUIClientPropertyKey("WindowsLookAndFeel.generateHiResDisabledIcon");
   private boolean updatePending = false;
   private boolean useSystemFontSettings = true;
   private boolean useSystemFontSizeSettings;
   private DesktopProperty themeActive;
   private DesktopProperty dllName;
   private DesktopProperty colorName;
   private DesktopProperty sizeName;
   private DesktopProperty aaSettings;
   private transient LayoutStyle style;
   private int baseUnitX;
   private int baseUnitY;
   private static boolean isMnemonicHidden = true;
   private static boolean isClassicWindows = false;

   public String getName() {
      return "Windows";
   }

   public String getDescription() {
      return "The Microsoft Windows Look and Feel";
   }

   public String getID() {
      return "Windows";
   }

   public boolean isNativeLookAndFeel() {
      return OSInfo.getOSType() == OSInfo.OSType.WINDOWS;
   }

   public boolean isSupportedLookAndFeel() {
      return this.isNativeLookAndFeel();
   }

   public void initialize() {
      super.initialize();
      if (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0) {
         isClassicWindows = true;
      } else {
         isClassicWindows = false;
         XPStyle.invalidateStyle();
      }

      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.useSystemFontSettings")));
      this.useSystemFontSettings = var1 == null || Boolean.valueOf(var1);
      if (this.useSystemFontSettings) {
         Object var2 = UIManager.get("Application.useSystemFontSettings");
         this.useSystemFontSettings = var2 == null || Boolean.TRUE.equals(var2);
      }

      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
   }

   protected void initClassDefaults(UIDefaults var1) {
      super.initClassDefaults(var1);
      Object[] var3 = new Object[]{"ButtonUI", "com.sun.java.swing.plaf.windows.WindowsButtonUI", "CheckBoxUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxUI", "CheckBoxMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxMenuItemUI", "LabelUI", "com.sun.java.swing.plaf.windows.WindowsLabelUI", "RadioButtonUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonUI", "RadioButtonMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonMenuItemUI", "ToggleButtonUI", "com.sun.java.swing.plaf.windows.WindowsToggleButtonUI", "ProgressBarUI", "com.sun.java.swing.plaf.windows.WindowsProgressBarUI", "SliderUI", "com.sun.java.swing.plaf.windows.WindowsSliderUI", "SeparatorUI", "com.sun.java.swing.plaf.windows.WindowsSeparatorUI", "SplitPaneUI", "com.sun.java.swing.plaf.windows.WindowsSplitPaneUI", "SpinnerUI", "com.sun.java.swing.plaf.windows.WindowsSpinnerUI", "TabbedPaneUI", "com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI", "TextAreaUI", "com.sun.java.swing.plaf.windows.WindowsTextAreaUI", "TextFieldUI", "com.sun.java.swing.plaf.windows.WindowsTextFieldUI", "PasswordFieldUI", "com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI", "TextPaneUI", "com.sun.java.swing.plaf.windows.WindowsTextPaneUI", "EditorPaneUI", "com.sun.java.swing.plaf.windows.WindowsEditorPaneUI", "TreeUI", "com.sun.java.swing.plaf.windows.WindowsTreeUI", "ToolBarUI", "com.sun.java.swing.plaf.windows.WindowsToolBarUI", "ToolBarSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsToolBarSeparatorUI", "ComboBoxUI", "com.sun.java.swing.plaf.windows.WindowsComboBoxUI", "TableHeaderUI", "com.sun.java.swing.plaf.windows.WindowsTableHeaderUI", "InternalFrameUI", "com.sun.java.swing.plaf.windows.WindowsInternalFrameUI", "DesktopPaneUI", "com.sun.java.swing.plaf.windows.WindowsDesktopPaneUI", "DesktopIconUI", "com.sun.java.swing.plaf.windows.WindowsDesktopIconUI", "FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI", "MenuUI", "com.sun.java.swing.plaf.windows.WindowsMenuUI", "MenuItemUI", "com.sun.java.swing.plaf.windows.WindowsMenuItemUI", "MenuBarUI", "com.sun.java.swing.plaf.windows.WindowsMenuBarUI", "PopupMenuUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuUI", "PopupMenuSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI", "ScrollBarUI", "com.sun.java.swing.plaf.windows.WindowsScrollBarUI", "RootPaneUI", "com.sun.java.swing.plaf.windows.WindowsRootPaneUI"};
      var1.putDefaults(var3);
   }

   protected void initSystemColorDefaults(UIDefaults var1) {
      String[] var2 = new String[]{"desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", "menu", "#C0C0C0", "menuPressedItemB", "#000080", "menuPressedItemF", "#FFFFFF", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", "infoText", "#000000"};
      this.loadSystemColors(var1, var2, this.isNativeLookAndFeel());
   }

   private void initResourceBundle(UIDefaults var1) {
      var1.addResourceBundle("com.sun.java.swing.plaf.windows.resources.windows");
   }

   protected void initComponentDefaults(UIDefaults var1) {
      super.initComponentDefaults(var1);
      this.initResourceBundle(var1);
      Integer var2 = 12;
      Integer var3 = 0;
      Integer var4 = 1;
      SwingLazyValue var5 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Dialog", var3, var2});
      SwingLazyValue var6 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"SansSerif", var3, var2});
      SwingLazyValue var7 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Monospaced", var3, var2});
      SwingLazyValue var8 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Dialog", var4, var2});
      ColorUIResource var9 = new ColorUIResource(Color.red);
      ColorUIResource var10 = new ColorUIResource(Color.black);
      ColorUIResource var11 = new ColorUIResource(Color.white);
      ColorUIResource var12 = new ColorUIResource(Color.gray);
      ColorUIResource var13 = new ColorUIResource(Color.darkGray);
      isClassicWindows = OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0;
      Icon var15 = WindowsTreeUI.ExpandedIcon.createExpandedIcon();
      Icon var16 = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();
      UIDefaults.LazyInputMap var17 = new UIDefaults.LazyInputMap(new Object[]{"control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation"});
      UIDefaults.LazyInputMap var18 = new UIDefaults.LazyInputMap(new Object[]{"control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-begin-line", "control RIGHT", "caret-end-line", "control shift LEFT", "selection-begin-line", "control shift RIGHT", "selection-end-line", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation"});
      UIDefaults.LazyInputMap var19 = new UIDefaults.LazyInputMap(new Object[]{"control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "control A", "select-all", "control BACK_SLASH", "unselect", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "control HOME", "caret-begin", "control END", "caret-end", "control shift HOME", "selection-begin", "control shift END", "selection-end", "UP", "caret-up", "DOWN", "caret-down", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift DOWN", "selection-down", "ENTER", "insert-break", "TAB", "insert-tab", "control T", "next-link-action", "control shift T", "previous-link-action", "control SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation"});
      String var20 = "+";
      DesktopProperty var21 = new DesktopProperty("win.3d.backgroundColor", var1.get("control"));
      DesktopProperty var22 = new DesktopProperty("win.3d.lightColor", var1.get("controlHighlight"));
      DesktopProperty var23 = new DesktopProperty("win.3d.highlightColor", var1.get("controlLtHighlight"));
      DesktopProperty var24 = new DesktopProperty("win.3d.shadowColor", var1.get("controlShadow"));
      DesktopProperty var25 = new DesktopProperty("win.3d.darkShadowColor", var1.get("controlDkShadow"));
      DesktopProperty var26 = new DesktopProperty("win.button.textColor", var1.get("controlText"));
      DesktopProperty var27 = new DesktopProperty("win.menu.backgroundColor", var1.get("menu"));
      DesktopProperty var28 = new DesktopProperty("win.menubar.backgroundColor", var1.get("menu"));
      DesktopProperty var29 = new DesktopProperty("win.menu.textColor", var1.get("menuText"));
      DesktopProperty var30 = new DesktopProperty("win.item.highlightColor", var1.get("textHighlight"));
      DesktopProperty var31 = new DesktopProperty("win.item.highlightTextColor", var1.get("textHighlightText"));
      DesktopProperty var32 = new DesktopProperty("win.frame.backgroundColor", var1.get("window"));
      DesktopProperty var33 = new DesktopProperty("win.frame.textColor", var1.get("windowText"));
      DesktopProperty var34 = new DesktopProperty("win.frame.sizingBorderWidth", 1);
      DesktopProperty var35 = new DesktopProperty("win.frame.captionHeight", 18);
      DesktopProperty var36 = new DesktopProperty("win.frame.captionButtonWidth", 16);
      DesktopProperty var37 = new DesktopProperty("win.frame.captionButtonHeight", 16);
      DesktopProperty var38 = new DesktopProperty("win.text.grayedTextColor", var1.get("textInactiveText"));
      DesktopProperty var39 = new DesktopProperty("win.scrollbar.backgroundColor", var1.get("scrollbar"));
      WindowsLookAndFeel.FocusColorProperty var40 = new WindowsLookAndFeel.FocusColorProperty();
      WindowsLookAndFeel.XPColorValue var41 = new WindowsLookAndFeel.XPColorValue(TMSchema.Part.EP_EDIT, (TMSchema.State)null, TMSchema.Prop.FILLCOLOR, var32);
      Object var44 = var5;
      Object var45 = var7;
      Object var46 = var5;
      Object var47 = var5;
      Object var48 = var8;
      Object var49 = var6;
      Object var50 = var5;
      DesktopProperty var51 = new DesktopProperty("win.scrollbar.width", 16);
      DesktopProperty var52 = new DesktopProperty("win.menu.height", (Object)null);
      DesktopProperty var53 = new DesktopProperty("win.item.hotTrackingOn", true);
      DesktopProperty var54 = new DesktopProperty("win.menu.keyboardCuesOn", Boolean.TRUE);
      if (this.useSystemFontSettings) {
         var44 = this.getDesktopFontValue("win.menu.font", var5);
         var45 = this.getDesktopFontValue("win.ansiFixed.font", var7);
         var46 = this.getDesktopFontValue("win.defaultGUI.font", var5);
         var47 = this.getDesktopFontValue("win.messagebox.font", var5);
         var48 = this.getDesktopFontValue("win.frame.captionFont", var8);
         var50 = this.getDesktopFontValue("win.icon.font", var5);
         var49 = this.getDesktopFontValue("win.tooltip.font", var6);
         SwingUtilities2.AATextInfo var55 = SwingUtilities2.AATextInfo.getAATextInfo(true);
         var1.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var55);
         this.aaSettings = new WindowsLookAndFeel.FontDesktopProperty("awt.font.desktophints");
      }

      if (this.useSystemFontSizeSettings) {
         var44 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.menu.font.height", "Dialog", 0, 12);
         var45 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.ansiFixed.font.height", "Monospaced", 0, 12);
         var46 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.defaultGUI.font.height", "Dialog", 0, 12);
         var47 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.messagebox.font.height", "Dialog", 0, 12);
         var48 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.frame.captionFont.height", "Dialog", 1, 12);
         var49 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.tooltip.font.height", "SansSerif", 0, 12);
         var50 = new WindowsLookAndFeel.WindowsFontSizeProperty("win.icon.font.height", "Dialog", 0, 12);
      }

      if (!(this instanceof WindowsClassicLookAndFeel) && OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0 && AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.noxp"))) == null) {
         this.themeActive = new WindowsLookAndFeel.TriggerDesktopProperty("win.xpstyle.themeActive");
         this.dllName = new WindowsLookAndFeel.TriggerDesktopProperty("win.xpstyle.dllName");
         this.colorName = new WindowsLookAndFeel.TriggerDesktopProperty("win.xpstyle.colorName");
         this.sizeName = new WindowsLookAndFeel.TriggerDesktopProperty("win.xpstyle.sizeName");
      }

      Object[] var56 = new Object[]{"AuditoryCues.playList", null, "Application.useSystemFontSettings", this.useSystemFontSettings, "TextField.focusInputMap", var17, "PasswordField.focusInputMap", var18, "TextArea.focusInputMap", var19, "TextPane.focusInputMap", var19, "EditorPane.focusInputMap", var19, "Button.font", var46, "Button.background", var21, "Button.foreground", var26, "Button.shadow", var24, "Button.darkShadow", var25, "Button.light", var22, "Button.highlight", var23, "Button.disabledForeground", var38, "Button.disabledShadow", var23, "Button.focus", var40, "Button.dashedRectGapX", new WindowsLookAndFeel.XPValue(3, 5), "Button.dashedRectGapY", new WindowsLookAndFeel.XPValue(3, 4), "Button.dashedRectGapWidth", new WindowsLookAndFeel.XPValue(6, 10), "Button.dashedRectGapHeight", new WindowsLookAndFeel.XPValue(6, 8), "Button.textShiftOffset", new WindowsLookAndFeel.XPValue(0, 1), "Button.showMnemonics", var54, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "Caret.width", new DesktopProperty("win.caret.width", (Object)null), "CheckBox.font", var46, "CheckBox.interiorBackground", var32, "CheckBox.background", var21, "CheckBox.foreground", var33, "CheckBox.shadow", var24, "CheckBox.darkShadow", var25, "CheckBox.light", var22, "CheckBox.highlight", var23, "CheckBox.focus", var40, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "CheckBoxMenuItem.font", var44, "CheckBoxMenuItem.background", var27, "CheckBoxMenuItem.foreground", var29, "CheckBoxMenuItem.selectionForeground", var31, "CheckBoxMenuItem.selectionBackground", var30, "CheckBoxMenuItem.acceleratorForeground", var29, "CheckBoxMenuItem.acceleratorSelectionForeground", var31, "CheckBoxMenuItem.commandSound", "win.sound.menuCommand", "ComboBox.font", var46, "ComboBox.background", var32, "ComboBox.foreground", var33, "ComboBox.buttonBackground", var21, "ComboBox.buttonShadow", var24, "ComboBox.buttonDarkShadow", var25, "ComboBox.buttonHighlight", var23, "ComboBox.selectionBackground", var30, "ComboBox.selectionForeground", var31, "ComboBox.editorBorder", new WindowsLookAndFeel.XPValue(new EmptyBorder(1, 2, 1, 1), new EmptyBorder(1, 4, 1, 4)), "ComboBox.disabledBackground", new WindowsLookAndFeel.XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.FILLCOLOR, var21), "ComboBox.disabledForeground", new WindowsLookAndFeel.XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.TEXTCOLOR, var38), "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext2", "KP_DOWN", "selectNext2", "UP", "selectPrevious2", "KP_UP", "selectPrevious2", "ENTER", "enterPressed", "F4", "togglePopup", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup"}), "Desktop.background", new DesktopProperty("win.desktop.backgroundColor", var1.get("desktop")), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "LEFT", "left", "KP_LEFT", "left", "UP", "up", "KP_UP", "up", "DOWN", "down", "KP_DOWN", "down", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious"}), "DesktopIcon.width", 160, "EditorPane.font", var46, "EditorPane.background", var32, "EditorPane.foreground", var33, "EditorPane.selectionBackground", var30, "EditorPane.selectionForeground", var31, "EditorPane.caretForeground", var33, "EditorPane.inactiveForeground", var38, "EditorPane.inactiveBackground", var32, "EditorPane.disabledBackground", var21, "FileChooser.homeFolderIcon", new WindowsLookAndFeel.LazyWindowsIcon((String)null, "icons/HomeFolder.gif"), "FileChooser.listFont", var50, "FileChooser.listViewBackground", new WindowsLookAndFeel.XPColorValue(TMSchema.Part.LVP_LISTVIEW, (TMSchema.State)null, TMSchema.Prop.FILLCOLOR, var32), "FileChooser.listViewBorder", new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.LVP_LISTVIEW, new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource")), "FileChooser.listViewIcon", new WindowsLookAndFeel.LazyWindowsIcon("fileChooserIcon ListView", "icons/ListView.gif"), "FileChooser.listViewWindowsStyle", Boolean.TRUE, "FileChooser.detailsViewIcon", new WindowsLookAndFeel.LazyWindowsIcon("fileChooserIcon DetailsView", "icons/DetailsView.gif"), "FileChooser.viewMenuIcon", new WindowsLookAndFeel.LazyWindowsIcon("fileChooserIcon ViewMenu", "icons/ListView.gif"), "FileChooser.upFolderIcon", new WindowsLookAndFeel.LazyWindowsIcon("fileChooserIcon UpFolder", "icons/UpFolder.gif"), "FileChooser.newFolderIcon", new WindowsLookAndFeel.LazyWindowsIcon("fileChooserIcon NewFolder", "icons/NewFolder.gif"), "FileChooser.useSystemExtensionHiding", Boolean.TRUE, "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.noPlacesBar", new DesktopProperty("win.comdlg.noPlacesBar", Boolean.FALSE), "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up"}), "FileView.directoryIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/Directory.gif"), "FileView.fileIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/File.gif"), "FileView.computerIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/Computer.gif"), "FileView.hardDriveIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/HardDrive.gif"), "FileView.floppyDriveIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/FloppyDrive.gif"), "FormattedTextField.font", var46, "InternalFrame.titleFont", var48, "InternalFrame.titlePaneHeight", var35, "InternalFrame.titleButtonWidth", var36, "InternalFrame.titleButtonHeight", var37, "InternalFrame.titleButtonToolTipsOn", var53, "InternalFrame.borderColor", var21, "InternalFrame.borderShadow", var24, "InternalFrame.borderDarkShadow", var25, "InternalFrame.borderHighlight", var23, "InternalFrame.borderLight", var22, "InternalFrame.borderWidth", var34, "InternalFrame.minimizeIconBackground", var21, "InternalFrame.resizeIconHighlight", var22, "InternalFrame.resizeIconShadow", var24, "InternalFrame.activeBorderColor", new DesktopProperty("win.frame.activeBorderColor", var1.get("windowBorder")), "InternalFrame.inactiveBorderColor", new DesktopProperty("win.frame.inactiveBorderColor", var1.get("windowBorder")), "InternalFrame.activeTitleBackground", new DesktopProperty("win.frame.activeCaptionColor", var1.get("activeCaption")), "InternalFrame.activeTitleGradient", new DesktopProperty("win.frame.activeCaptionGradientColor", var1.get("activeCaption")), "InternalFrame.activeTitleForeground", new DesktopProperty("win.frame.captionTextColor", var1.get("activeCaptionText")), "InternalFrame.inactiveTitleBackground", new DesktopProperty("win.frame.inactiveCaptionColor", var1.get("inactiveCaption")), "InternalFrame.inactiveTitleGradient", new DesktopProperty("win.frame.inactiveCaptionGradientColor", var1.get("inactiveCaption")), "InternalFrame.inactiveTitleForeground", new DesktopProperty("win.frame.inactiveCaptionTextColor", var1.get("inactiveCaptionText")), "InternalFrame.maximizeIcon", WindowsIconFactory.createFrameMaximizeIcon(), "InternalFrame.minimizeIcon", WindowsIconFactory.createFrameMinimizeIcon(), "InternalFrame.iconifyIcon", WindowsIconFactory.createFrameIconifyIcon(), "InternalFrame.closeIcon", WindowsIconFactory.createFrameCloseIcon(), "InternalFrame.icon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane$ScalableIconUIResource", new Object[][]{{SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/JavaCup32.png")}}), "InternalFrame.closeSound", "win.sound.close", "InternalFrame.maximizeSound", "win.sound.maximize", "InternalFrame.minimizeSound", "win.sound.minimize", "InternalFrame.restoreDownSound", "win.sound.restoreDown", "InternalFrame.restoreUpSound", "win.sound.restoreUp", "InternalFrame.windowBindings", new Object[]{"shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu"}, "Label.font", var46, "Label.background", var21, "Label.foreground", var33, "Label.disabledForeground", var38, "Label.disabledShadow", var23, "List.font", var46, "List.background", var32, "List.foreground", var33, "List.selectionBackground", var30, "List.selectionForeground", var31, "List.lockToPositionOnScroll", Boolean.TRUE, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "PopupMenu.font", var44, "PopupMenu.background", var27, "PopupMenu.foreground", var29, "PopupMenu.popupSound", "win.sound.menuPopup", "PopupMenu.consumeEventOnClose", Boolean.TRUE, "Menu.font", var44, "Menu.foreground", var29, "Menu.background", var27, "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE, "Menu.selectionForeground", var31, "Menu.selectionBackground", var30, "Menu.acceleratorForeground", var29, "Menu.acceleratorSelectionForeground", var31, "Menu.menuPopupOffsetX", 0, "Menu.menuPopupOffsetY", 0, "Menu.submenuPopupOffsetX", -4, "Menu.submenuPopupOffsetY", -3, "Menu.crossMenuMnemonic", Boolean.FALSE, "Menu.preserveTopLevelSelection", Boolean.TRUE, "MenuBar.font", var44, "MenuBar.background", new WindowsLookAndFeel.XPValue(var28, var27), "MenuBar.foreground", var29, "MenuBar.shadow", var24, "MenuBar.highlight", var23, "MenuBar.height", var52, "MenuBar.rolloverEnabled", var53, "MenuBar.windowBindings", new Object[]{"F10", "takeFocus"}, "MenuItem.font", var44, "MenuItem.acceleratorFont", var44, "MenuItem.foreground", var29, "MenuItem.background", var27, "MenuItem.selectionForeground", var31, "MenuItem.selectionBackground", var30, "MenuItem.disabledForeground", var38, "MenuItem.acceleratorForeground", var29, "MenuItem.acceleratorSelectionForeground", var31, "MenuItem.acceleratorDelimiter", var20, "MenuItem.commandSound", "win.sound.menuCommand", "MenuItem.disabledAreNavigable", Boolean.TRUE, "RadioButton.font", var46, "RadioButton.interiorBackground", var32, "RadioButton.background", var21, "RadioButton.foreground", var33, "RadioButton.shadow", var24, "RadioButton.darkShadow", var25, "RadioButton.light", var22, "RadioButton.highlight", var23, "RadioButton.focus", var40, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "RadioButtonMenuItem.font", var44, "RadioButtonMenuItem.foreground", var29, "RadioButtonMenuItem.background", var27, "RadioButtonMenuItem.selectionForeground", var31, "RadioButtonMenuItem.selectionBackground", var30, "RadioButtonMenuItem.disabledForeground", var38, "RadioButtonMenuItem.acceleratorForeground", var29, "RadioButtonMenuItem.acceleratorSelectionForeground", var31, "RadioButtonMenuItem.commandSound", "win.sound.menuCommand", "OptionPane.font", var47, "OptionPane.messageFont", var47, "OptionPane.buttonFont", var47, "OptionPane.background", var21, "OptionPane.foreground", var33, "OptionPane.buttonMinimumWidth", new WindowsLookAndFeel.XPDLUValue(50, 50, 3), "OptionPane.messageForeground", var26, "OptionPane.errorIcon", new WindowsLookAndFeel.LazyWindowsIcon("optionPaneIcon Error", "icons/Error.gif"), "OptionPane.informationIcon", new WindowsLookAndFeel.LazyWindowsIcon("optionPaneIcon Information", "icons/Inform.gif"), "OptionPane.questionIcon", new WindowsLookAndFeel.LazyWindowsIcon("optionPaneIcon Question", "icons/Question.gif"), "OptionPane.warningIcon", new WindowsLookAndFeel.LazyWindowsIcon("optionPaneIcon Warning", "icons/Warn.gif"), "OptionPane.windowBindings", new Object[]{"ESCAPE", "close"}, "OptionPane.errorSound", "win.sound.hand", "OptionPane.informationSound", "win.sound.asterisk", "OptionPane.questionSound", "win.sound.question", "OptionPane.warningSound", "win.sound.exclamation", "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "FormattedTextField.inactiveBackground", var21, "FormattedTextField.disabledBackground", var21, "Panel.font", var46, "Panel.background", var21, "Panel.foreground", var33, "PasswordField.font", var46, "PasswordField.background", var41, "PasswordField.foreground", var33, "PasswordField.inactiveForeground", var38, "PasswordField.inactiveBackground", var21, "PasswordField.disabledBackground", var21, "PasswordField.selectionBackground", var30, "PasswordField.selectionForeground", var31, "PasswordField.caretForeground", var33, "PasswordField.echoChar", new WindowsLookAndFeel.XPValue(new Character('●'), new Character('*')), "ProgressBar.font", var46, "ProgressBar.foreground", var30, "ProgressBar.background", var21, "ProgressBar.shadow", var24, "ProgressBar.highlight", var23, "ProgressBar.selectionForeground", var21, "ProgressBar.selectionBackground", var30, "ProgressBar.cellLength", 7, "ProgressBar.cellSpacing", 2, "ProgressBar.indeterminateInsets", new Insets(3, 3, 3, 3), "RootPane.defaultButtonWindowKeyBindings", new Object[]{"ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release"}, "ScrollBar.background", var39, "ScrollBar.foreground", var21, "ScrollBar.track", var11, "ScrollBar.trackForeground", var39, "ScrollBar.trackHighlight", var10, "ScrollBar.trackHighlightForeground", var13, "ScrollBar.thumb", var21, "ScrollBar.thumbHighlight", var23, "ScrollBar.thumbDarkShadow", var25, "ScrollBar.thumbShadow", var24, "ScrollBar.width", var51, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "ctrl PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "ctrl PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "ScrollPane.font", var46, "ScrollPane.background", var21, "ScrollPane.foreground", var26, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd"}), "Separator.background", var23, "Separator.foreground", var24, "Slider.font", var46, "Slider.foreground", var21, "Slider.background", var21, "Slider.highlight", var23, "Slider.shadow", var24, "Slider.focus", var25, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "Spinner.font", var46, "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "SplitPane.background", var21, "SplitPane.highlight", var23, "SplitPane.shadow", var24, "SplitPane.darkShadow", var25, "SplitPane.dividerSize", 5, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward"}), "TabbedPane.tabsOverlapBorder", new WindowsLookAndFeel.XPValue(Boolean.TRUE, Boolean.FALSE), "TabbedPane.tabInsets", new WindowsLookAndFeel.XPValue(new InsetsUIResource(1, 4, 1, 4), new InsetsUIResource(0, 4, 1, 4)), "TabbedPane.tabAreaInsets", new WindowsLookAndFeel.XPValue(new InsetsUIResource(3, 2, 2, 2), new InsetsUIResource(3, 2, 0, 2)), "TabbedPane.font", var46, "TabbedPane.background", var21, "TabbedPane.foreground", var26, "TabbedPane.highlight", var23, "TabbedPane.light", var22, "TabbedPane.shadow", var24, "TabbedPane.darkShadow", var25, "TabbedPane.focus", var26, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent"}), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus"}), "Table.font", var46, "Table.foreground", var26, "Table.background", var32, "Table.highlight", var23, "Table.light", var22, "Table.shadow", var24, "Table.darkShadow", var25, "Table.selectionForeground", var31, "Table.selectionBackground", var30, "Table.gridColor", var12, "Table.focusCellBackground", var32, "Table.focusCellForeground", var26, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader"}), "Table.sortIconHighlight", var24, "Table.sortIconLight", var11, "TableHeader.font", var46, "TableHeader.foreground", var26, "TableHeader.background", var21, "TableHeader.focusCellBackground", new WindowsLookAndFeel.XPValue(WindowsLookAndFeel.XPValue.NULL_VALUE, var32), "TextArea.font", var45, "TextArea.background", var32, "TextArea.foreground", var33, "TextArea.inactiveForeground", var38, "TextArea.inactiveBackground", var32, "TextArea.disabledBackground", var21, "TextArea.selectionBackground", var30, "TextArea.selectionForeground", var31, "TextArea.caretForeground", var33, "TextField.font", var46, "TextField.background", var41, "TextField.foreground", var33, "TextField.shadow", var24, "TextField.darkShadow", var25, "TextField.light", var22, "TextField.highlight", var23, "TextField.inactiveForeground", var38, "TextField.inactiveBackground", var21, "TextField.disabledBackground", var21, "TextField.selectionBackground", var30, "TextField.selectionForeground", var31, "TextField.caretForeground", var33, "TextPane.font", var46, "TextPane.background", var32, "TextPane.foreground", var33, "TextPane.selectionBackground", var30, "TextPane.selectionForeground", var31, "TextPane.inactiveBackground", var32, "TextPane.disabledBackground", var21, "TextPane.caretForeground", var33, "TitledBorder.font", var46, "TitledBorder.titleColor", new WindowsLookAndFeel.XPColorValue(TMSchema.Part.BP_GROUPBOX, (TMSchema.State)null, TMSchema.Prop.TEXTCOLOR, var33), "ToggleButton.font", var46, "ToggleButton.background", var21, "ToggleButton.foreground", var26, "ToggleButton.shadow", var24, "ToggleButton.darkShadow", var25, "ToggleButton.light", var22, "ToggleButton.highlight", var23, "ToggleButton.focus", var26, "ToggleButton.textShiftOffset", 1, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "ToolBar.font", var44, "ToolBar.background", var21, "ToolBar.foreground", var26, "ToolBar.shadow", var24, "ToolBar.darkShadow", var25, "ToolBar.light", var22, "ToolBar.highlight", var23, "ToolBar.dockingBackground", var21, "ToolBar.dockingForeground", var9, "ToolBar.floatingBackground", var21, "ToolBar.floatingForeground", var13, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight"}), "ToolBar.separatorSize", null, "ToolTip.font", var49, "ToolTip.background", new DesktopProperty("win.tooltip.backgroundColor", var1.get("info")), "ToolTip.foreground", new DesktopProperty("win.tooltip.textColor", var1.get("infoText")), "ToolTipManager.enableToolTipMode", "activeApplication", "Tree.selectionBorderColor", var10, "Tree.drawDashedFocusIndicator", Boolean.TRUE, "Tree.lineTypeDashed", Boolean.TRUE, "Tree.font", var46, "Tree.background", var32, "Tree.foreground", var33, "Tree.hash", var12, "Tree.leftChildIndent", 8, "Tree.rightChildIndent", 11, "Tree.textForeground", var33, "Tree.textBackground", var32, "Tree.selectionForeground", var31, "Tree.selectionBackground", var30, "Tree.expandedIcon", var15, "Tree.collapsedIcon", var16, "Tree.openIcon", new WindowsLookAndFeel.ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 5", "icons/TreeOpen.gif"), "Tree.closedIcon", new WindowsLookAndFeel.ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 4", "icons/TreeClosed.gif"), "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"}), "Viewport.font", var46, "Viewport.background", var21, "Viewport.foreground", var33};
      var1.putDefaults(var56);
      var1.putDefaults(this.getLazyValueDefaults());
      this.initVistaComponentDefaults(var1);
   }

   static boolean isOnVista() {
      return OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_VISTA) >= 0;
   }

   private void initVistaComponentDefaults(UIDefaults var1) {
      if (isOnVista()) {
         String[] var2 = new String[]{"MenuItem", "Menu", "CheckBoxMenuItem", "RadioButtonMenuItem"};
         Object[] var3 = new Object[var2.length * 2];
         int var4 = 0;

         int var5;
         String var6;
         Object var7;
         for(var5 = 0; var4 < var2.length; ++var4) {
            var6 = var2[var4] + ".opaque";
            var7 = var1.get(var6);
            var3[var5++] = var6;
            var3[var5++] = new WindowsLookAndFeel.XPValue(Boolean.FALSE, var7);
         }

         var1.putDefaults(var3);
         var4 = 0;

         for(var5 = 0; var4 < var2.length; ++var4) {
            var6 = var2[var4] + ".acceleratorSelectionForeground";
            var7 = var1.get(var6);
            var3[var5++] = var6;
            var3[var5++] = new WindowsLookAndFeel.XPValue(var1.getColor(var2[var4] + ".acceleratorForeground"), var7);
         }

         var1.putDefaults(var3);
         WindowsIconFactory.VistaMenuItemCheckIconFactory var13 = WindowsIconFactory.getMenuItemCheckIconFactory();
         var5 = 0;

         Object var8;
         int var14;
         String var15;
         for(var14 = 0; var5 < var2.length; ++var5) {
            var15 = var2[var5] + ".checkIconFactory";
            var8 = var1.get(var15);
            var3[var14++] = var15;
            var3[var14++] = new WindowsLookAndFeel.XPValue(var13, var8);
         }

         var1.putDefaults(var3);
         var5 = 0;

         for(var14 = 0; var5 < var2.length; ++var5) {
            var15 = var2[var5] + ".checkIcon";
            var8 = var1.get(var15);
            var3[var14++] = var15;
            var3[var14++] = new WindowsLookAndFeel.XPValue(var13.getIcon(var2[var5]), var8);
         }

         var1.putDefaults(var3);
         var5 = 0;

         for(var14 = 0; var5 < var2.length; ++var5) {
            var15 = var2[var5] + ".evenHeight";
            var8 = var1.get(var15);
            var3[var14++] = var15;
            var3[var14++] = new WindowsLookAndFeel.XPValue(Boolean.TRUE, var8);
         }

         var1.putDefaults(var3);
         InsetsUIResource var17 = new InsetsUIResource(0, 0, 0, 0);
         var14 = 0;

         int var16;
         for(var16 = 0; var14 < var2.length; ++var14) {
            String var18 = var2[var14] + ".margin";
            Object var9 = var1.get(var18);
            var3[var16++] = var18;
            var3[var16++] = new WindowsLookAndFeel.XPValue(var17, var9);
         }

         var1.putDefaults(var3);
         Integer var22 = 0;
         var16 = 0;

         int var19;
         String var20;
         for(var19 = 0; var16 < var2.length; ++var16) {
            var20 = var2[var16] + ".checkIconOffset";
            Object var10 = var1.get(var20);
            var3[var19++] = var20;
            var3[var19++] = new WindowsLookAndFeel.XPValue(var22, var10);
         }

         var1.putDefaults(var3);
         Integer var23 = WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter();
         var19 = 0;

         int var21;
         for(var21 = 0; var19 < var2.length; ++var19) {
            String var24 = var2[var19] + ".afterCheckIconGap";
            Object var11 = var1.get(var24);
            var3[var21++] = var24;
            var3[var21++] = new WindowsLookAndFeel.XPValue(var23, var11);
         }

         var1.putDefaults(var3);
         UIDefaults.ActiveValue var26 = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults var1) {
               return WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth() + WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter();
            }
         };
         var21 = 0;

         for(int var25 = 0; var21 < var2.length; ++var21) {
            String var27 = var2[var21] + ".minimumTextOffset";
            Object var12 = var1.get(var27);
            var3[var25++] = var27;
            var3[var25++] = new WindowsLookAndFeel.XPValue(var26, var12);
         }

         var1.putDefaults(var3);
         var20 = "PopupMenu.border";
         WindowsLookAndFeel.XPBorderValue var28 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"), BorderFactory.createEmptyBorder(2, 2, 2, 2));
         var1.put(var20, var28);
         var1.put("Table.ascendingSortIcon", new WindowsLookAndFeel.XPValue(new WindowsLookAndFeel.SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDDOWN), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", (String)null, new Object[]{Boolean.TRUE})));
         var1.put("Table.descendingSortIcon", new WindowsLookAndFeel.XPValue(new WindowsLookAndFeel.SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDUP), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", (String)null, new Object[]{Boolean.FALSE})));
      }
   }

   private Object getDesktopFontValue(String var1, Object var2) {
      return this.useSystemFontSettings ? new WindowsLookAndFeel.WindowsFontProperty(var1, var2) : null;
   }

   private Object[] getLazyValueDefaults() {
      WindowsLookAndFeel.XPBorderValue var1 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.BP_PUSHBUTTON, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder"));
      WindowsLookAndFeel.XPBorderValue var2 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.EP_EDIT, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder"));
      WindowsLookAndFeel.XPValue var3 = new WindowsLookAndFeel.XPValue(new InsetsUIResource(2, 2, 2, 2), new InsetsUIResource(1, 1, 1, 1));
      WindowsLookAndFeel.XPBorderValue var4 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.EP_EDIT, var2, new EmptyBorder(2, 2, 2, 2));
      WindowsLookAndFeel.XPValue var5 = new WindowsLookAndFeel.XPValue(new InsetsUIResource(1, 1, 1, 1), (Object)null);
      WindowsLookAndFeel.XPBorderValue var6 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.CP_COMBOBOX, var2);
      SwingLazyValue var7 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getFocusCellHighlightBorder");
      SwingLazyValue var8 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
      SwingLazyValue var9 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getInternalFrameBorder");
      SwingLazyValue var10 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
      SwingLazyValue var11 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
      SwingLazyValue var12 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
      WindowsLookAndFeel.XPBorderValue var13 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"));
      SwingLazyValue var14 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getProgressBarBorder");
      SwingLazyValue var15 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
      WindowsLookAndFeel.XPBorderValue var16 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.LBP_LISTBOX, var2);
      WindowsLookAndFeel.XPBorderValue var17 = new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.LBP_LISTBOX, var10);
      SwingLazyValue var18 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getTableHeaderBorder");
      SwingLazyValue var19 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getToolBarBorder");
      SwingLazyValue var20 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource");
      SwingLazyValue var21 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getCheckBoxIcon");
      SwingLazyValue var22 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonIcon");
      SwingLazyValue var23 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonMenuItemIcon");
      SwingLazyValue var24 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemCheckIcon");
      SwingLazyValue var25 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemArrowIcon");
      SwingLazyValue var26 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuArrowIcon");
      Object[] var27 = new Object[]{"Button.border", var1, "CheckBox.border", var15, "ComboBox.border", var6, "DesktopIcon.border", var9, "FormattedTextField.border", var2, "FormattedTextField.margin", var3, "InternalFrame.border", var9, "List.focusCellHighlightBorder", var7, "Table.focusCellHighlightBorder", var7, "Menu.border", var11, "MenuBar.border", var12, "MenuItem.border", var11, "PasswordField.border", var2, "PasswordField.margin", var3, "PopupMenu.border", var13, "ProgressBar.border", var14, "RadioButton.border", var15, "ScrollPane.border", var16, "Spinner.border", var4, "Spinner.arrowButtonInsets", var5, "Spinner.arrowButtonSize", new Dimension(17, 9), "Table.scrollPaneBorder", var17, "TableHeader.cellBorder", var18, "TextArea.margin", var3, "TextField.border", var2, "TextField.margin", var3, "TitledBorder.border", new WindowsLookAndFeel.XPBorderValue(TMSchema.Part.BP_GROUPBOX, var8), "ToggleButton.border", var15, "ToolBar.border", var19, "ToolTip.border", var20, "CheckBox.icon", var21, "Menu.arrowIcon", var26, "MenuItem.checkIcon", var24, "MenuItem.arrowIcon", var25, "RadioButton.icon", var22, "RadioButtonMenuItem.checkIcon", var23, "InternalFrame.layoutTitlePaneAtOrigin", new WindowsLookAndFeel.XPValue(Boolean.TRUE, Boolean.FALSE), "Table.ascendingSortIcon", new WindowsLookAndFeel.XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", (String)null, new Object[]{Boolean.TRUE, "Table.sortIconColor"}), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", (String)null, new Object[]{Boolean.TRUE})), "Table.descendingSortIcon", new WindowsLookAndFeel.XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", (String)null, new Object[]{Boolean.FALSE, "Table.sortIconColor"}), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", (String)null, new Object[]{Boolean.FALSE}))};
      return var27;
   }

   public void uninitialize() {
      super.uninitialize();
      if (WindowsPopupMenuUI.mnemonicListener != null) {
         MenuSelectionManager.defaultManager().removeChangeListener(WindowsPopupMenuUI.mnemonicListener);
      }

      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
      DesktopProperty.flushUnreferencedProperties();
   }

   public static void setMnemonicHidden(boolean var0) {
      if (UIManager.getBoolean("Button.showMnemonics")) {
         isMnemonicHidden = false;
      } else {
         isMnemonicHidden = var0;
      }

   }

   public static boolean isMnemonicHidden() {
      if (UIManager.getBoolean("Button.showMnemonics")) {
         isMnemonicHidden = false;
      }

      return isMnemonicHidden;
   }

   public static boolean isClassicWindows() {
      return isClassicWindows;
   }

   public void provideErrorFeedback(Component var1) {
      super.provideErrorFeedback(var1);
   }

   public LayoutStyle getLayoutStyle() {
      Object var1 = this.style;
      if (var1 == null) {
         var1 = new WindowsLookAndFeel.WindowsLayoutStyle();
         this.style = (LayoutStyle)var1;
      }

      return (LayoutStyle)var1;
   }

   protected Action createAudioAction(Object var1) {
      if (var1 != null) {
         String var2 = (String)var1;
         String var3 = (String)UIManager.get(var1);
         return new WindowsLookAndFeel.AudioAction(var2, var3);
      } else {
         return null;
      }
   }

   static void repaintRootPane(Component var0) {
      JRootPane var1;
      for(var1 = null; var0 != null; var0 = ((Component)var0).getParent()) {
         if (var0 instanceof JRootPane) {
            var1 = (JRootPane)var0;
         }
      }

      if (var1 != null) {
         var1.repaint();
      } else {
         ((Component)var0).repaint();
      }

   }

   private int dluToPixels(int var1, int var2) {
      if (this.baseUnitX == 0) {
         this.calculateBaseUnits();
      }

      if (var2 != 3 && var2 != 7) {
         assert var2 == 1 || var2 == 5;

         return var1 * this.baseUnitY / 8;
      } else {
         return var1 * this.baseUnitX / 4;
      }
   }

   private void calculateBaseUnits() {
      FontMetrics var1 = Toolkit.getDefaultToolkit().getFontMetrics(UIManager.getFont("Button.font"));
      this.baseUnitX = var1.stringWidth("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
      this.baseUnitX = (this.baseUnitX / 26 + 1) / 2;
      this.baseUnitY = var1.getAscent() + var1.getDescent() - 1;
   }

   public Icon getDisabledIcon(JComponent var1, Icon var2) {
      if (var2 != null && var1 != null && Boolean.TRUE.equals(var1.getClientProperty(HI_RES_DISABLED_ICON_CLIENT_KEY)) && var2.getIconWidth() > 0 && var2.getIconHeight() > 0) {
         BufferedImage var3 = new BufferedImage(var2.getIconWidth(), var2.getIconWidth(), 2);
         var2.paintIcon(var1, var3.getGraphics(), 0, 0);
         WindowsLookAndFeel.RGBGrayFilter var4 = new WindowsLookAndFeel.RGBGrayFilter();
         FilteredImageSource var5 = new FilteredImageSource(var3.getSource(), var4);
         Image var6 = var1.createImage(var5);
         return new ImageIconUIResource(var6);
      } else {
         return super.getDisabledIcon(var1, var2);
      }
   }

   private static class FocusColorProperty extends DesktopProperty {
      public FocusColorProperty() {
         super("win.3d.backgroundColor", Color.BLACK);
      }

      protected Object configureValue(Object var1) {
         Object var2 = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
         if (var2 != null && (Boolean)var2) {
            return Color.BLACK.equals(var1) ? Color.WHITE : Color.BLACK;
         } else {
            return Color.BLACK;
         }
      }
   }

   private static class RGBGrayFilter extends RGBImageFilter {
      public RGBGrayFilter() {
         this.canFilterIndexColorModel = true;
      }

      public int filterRGB(int var1, int var2, int var3) {
         float var4 = ((float)(var3 >> 16 & 255) / 255.0F + (float)(var3 >> 8 & 255) / 255.0F + (float)(var3 & 255) / 255.0F) / 3.0F;
         float var5 = (float)(var3 >> 24 & 255) / 255.0F;
         var4 = Math.min(1.0F, (1.0F - var4) / 2.857143F + var4);
         int var6 = (int)(var5 * 255.0F) << 24 | (int)(var4 * 255.0F) << 16 | (int)(var4 * 255.0F) << 8 | (int)(var4 * 255.0F);
         return var6;
      }
   }

   private class WindowsLayoutStyle extends DefaultLayoutStyle {
      private WindowsLayoutStyle() {
      }

      public int getPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, Container var5) {
         super.getPreferredGap(var1, var2, var3, var4, var5);
         switch(var3) {
         case INDENT:
            if (var4 == 3 || var4 == 7) {
               int var6 = this.getIndent(var1, var4);
               if (var6 > 0) {
                  return var6;
               }

               return 10;
            }
         case RELATED:
            break;
         case UNRELATED:
            return this.getButtonGap(var1, var2, var4, WindowsLookAndFeel.this.dluToPixels(7, var4));
         default:
            return 0;
         }

         return this.isLabelAndNonlabel(var1, var2, var4) ? this.getButtonGap(var1, var2, var4, WindowsLookAndFeel.this.dluToPixels(3, var4)) : this.getButtonGap(var1, var2, var4, WindowsLookAndFeel.this.dluToPixels(4, var4));
      }

      public int getContainerGap(JComponent var1, int var2, Container var3) {
         super.getContainerGap(var1, var2, var3);
         return this.getButtonGap(var1, var2, WindowsLookAndFeel.this.dluToPixels(7, var2));
      }

      // $FF: synthetic method
      WindowsLayoutStyle(Object var2) {
         this();
      }
   }

   private class FontDesktopProperty extends WindowsLookAndFeel.TriggerDesktopProperty {
      FontDesktopProperty(String var2) {
         super(var2);
      }

      protected void updateUI() {
         SwingUtilities2.AATextInfo var1 = SwingUtilities2.AATextInfo.getAATextInfo(true);
         UIDefaults var2 = UIManager.getLookAndFeelDefaults();
         var2.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var1);
         super.updateUI();
      }
   }

   private class TriggerDesktopProperty extends DesktopProperty {
      TriggerDesktopProperty(String var2) {
         super(var2, (Object)null);
         this.getValueFromDesktop();
      }

      protected void updateUI() {
         super.updateUI();
         this.getValueFromDesktop();
      }
   }

   private class XPDLUValue extends WindowsLookAndFeel.XPValue {
      private int direction;

      XPDLUValue(int var2, int var3, int var4) {
         super(var2, var3);
         this.direction = var4;
      }

      public Object getXPValue(UIDefaults var1) {
         int var2 = WindowsLookAndFeel.this.dluToPixels((Integer)this.xpValue, this.direction);
         return var2;
      }

      public Object getClassicValue(UIDefaults var1) {
         int var2 = WindowsLookAndFeel.this.dluToPixels((Integer)this.classicValue, this.direction);
         return var2;
      }
   }

   private static class XPColorValue extends WindowsLookAndFeel.XPValue {
      XPColorValue(TMSchema.Part var1, TMSchema.State var2, TMSchema.Prop var3, Object var4) {
         super(new WindowsLookAndFeel.XPColorValue.XPColorValueKey(var1, var2, var3), var4);
      }

      public Object getXPValue(UIDefaults var1) {
         WindowsLookAndFeel.XPColorValue.XPColorValueKey var2 = (WindowsLookAndFeel.XPColorValue.XPColorValueKey)this.xpValue;
         XPStyle var3 = XPStyle.getXP();
         return var3 != null ? var3.getColor(var2.skin, var2.prop, (Color)null) : null;
      }

      private static class XPColorValueKey {
         XPStyle.Skin skin;
         TMSchema.Prop prop;

         XPColorValueKey(TMSchema.Part var1, TMSchema.State var2, TMSchema.Prop var3) {
            this.skin = new XPStyle.Skin(var1, var2);
            this.prop = var3;
         }
      }
   }

   private static class XPBorderValue extends WindowsLookAndFeel.XPValue {
      private final Border extraMargin;

      XPBorderValue(TMSchema.Part var1, Object var2) {
         this(var1, var2, (Border)null);
      }

      XPBorderValue(TMSchema.Part var1, Object var2, Border var3) {
         super(var1, var2);
         this.extraMargin = var3;
      }

      public Object getXPValue(UIDefaults var1) {
         XPStyle var2 = XPStyle.getXP();
         Border var3 = var2 != null ? var2.getBorder((Component)null, (TMSchema.Part)this.xpValue) : null;
         return var3 != null && this.extraMargin != null ? new BorderUIResource.CompoundBorderUIResource(var3, this.extraMargin) : var3;
      }
   }

   private static class XPValue implements UIDefaults.ActiveValue {
      protected Object classicValue;
      protected Object xpValue;
      private static final Object NULL_VALUE = new Object();

      XPValue(Object var1, Object var2) {
         this.xpValue = var1;
         this.classicValue = var2;
      }

      public Object createValue(UIDefaults var1) {
         Object var2 = null;
         if (XPStyle.getXP() != null) {
            var2 = this.getXPValue(var1);
         }

         if (var2 == null) {
            var2 = this.getClassicValue(var1);
         } else if (var2 == NULL_VALUE) {
            var2 = null;
         }

         return var2;
      }

      protected Object getXPValue(UIDefaults var1) {
         return this.recursiveCreateValue(this.xpValue, var1);
      }

      protected Object getClassicValue(UIDefaults var1) {
         return this.recursiveCreateValue(this.classicValue, var1);
      }

      private Object recursiveCreateValue(Object var1, UIDefaults var2) {
         if (var1 instanceof UIDefaults.LazyValue) {
            var1 = ((UIDefaults.LazyValue)var1).createValue(var2);
         }

         return var1 instanceof UIDefaults.ActiveValue ? ((UIDefaults.ActiveValue)var1).createValue(var2) : var1;
      }
   }

   private static class WindowsFontSizeProperty extends DesktopProperty {
      private String fontName;
      private int fontSize;
      private int fontStyle;

      WindowsFontSizeProperty(String var1, String var2, int var3, int var4) {
         super(var1, (Object)null);
         this.fontName = var2;
         this.fontSize = var4;
         this.fontStyle = var3;
      }

      protected Object configureValue(Object var1) {
         if (var1 == null) {
            var1 = new FontUIResource(this.fontName, this.fontStyle, this.fontSize);
         } else if (var1 instanceof Integer) {
            var1 = new FontUIResource(this.fontName, this.fontStyle, (Integer)var1);
         }

         return var1;
      }
   }

   private static class WindowsFontProperty extends DesktopProperty {
      WindowsFontProperty(String var1, Object var2) {
         super(var1, var2);
      }

      public void invalidate(LookAndFeel var1) {
         if ("win.defaultGUI.font.height".equals(this.getKey())) {
            ((WindowsLookAndFeel)var1).style = null;
         }

         super.invalidate(var1);
      }

      protected Object configureValue(Object var1) {
         if (!(var1 instanceof Font)) {
            return super.configureValue(var1);
         } else {
            Object var2 = (Font)var1;
            if ("MS Sans Serif".equals(((Font)var2).getName())) {
               int var3 = ((Font)var2).getSize();

               int var4;
               try {
                  var4 = Toolkit.getDefaultToolkit().getScreenResolution();
               } catch (HeadlessException var6) {
                  var4 = 96;
               }

               if (Math.round((float)var3 * 72.0F / (float)var4) < 8) {
                  var3 = Math.round((float)(8 * var4) / 72.0F);
               }

               FontUIResource var5 = new FontUIResource("Microsoft Sans Serif", ((Font)var2).getStyle(), var3);
               if (var5.getName() != null && var5.getName().equals(var5.getFamily())) {
                  var2 = var5;
               } else if (var3 != ((Font)var2).getSize()) {
                  var2 = new FontUIResource("MS Sans Serif", ((Font)var2).getStyle(), var3);
               }
            }

            if (FontUtilities.fontSupportsDefaultEncoding((Font)var2)) {
               if (!(var2 instanceof UIResource)) {
                  var2 = new FontUIResource((Font)var2);
               }
            } else {
               var2 = FontUtilities.getCompositeFontUIResource((Font)var2);
            }

            return var2;
         }
      }
   }

   private static class SkinIcon implements Icon, UIResource {
      private final TMSchema.Part part;
      private final TMSchema.State state;

      SkinIcon(TMSchema.Part var1, TMSchema.State var2) {
         this.part = var1;
         this.state = var2;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         XPStyle var5 = XPStyle.getXP();

         assert var5 != null;

         if (var5 != null) {
            XPStyle.Skin var6 = var5.getSkin((Component)null, this.part);
            var6.paintSkin(var2, var3, var4, this.state);
         }

      }

      public int getIconWidth() {
         int var1 = 0;
         XPStyle var2 = XPStyle.getXP();

         assert var2 != null;

         if (var2 != null) {
            XPStyle.Skin var3 = var2.getSkin((Component)null, this.part);
            var1 = var3.getWidth();
         }

         return var1;
      }

      public int getIconHeight() {
         int var1 = 0;
         XPStyle var2 = XPStyle.getXP();
         if (var2 != null) {
            XPStyle.Skin var3 = var2.getSkin((Component)null, this.part);
            var1 = var3.getHeight();
         }

         return var1;
      }
   }

   private class ActiveWindowsIcon implements UIDefaults.ActiveValue {
      private Icon icon;
      private String nativeImageName;
      private String fallbackName;
      private DesktopProperty desktopProperty;

      ActiveWindowsIcon(String var2, String var3, String var4) {
         this.nativeImageName = var3;
         this.fallbackName = var4;
         if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) < 0) {
            this.desktopProperty = new WindowsLookAndFeel.TriggerDesktopProperty(var2) {
               protected void updateUI() {
                  ActiveWindowsIcon.this.icon = null;
                  super.updateUI();
               }
            };
         }

      }

      public Object createValue(UIDefaults var1) {
         if (this.icon == null) {
            Image var2 = (Image)ShellFolder.get(this.nativeImageName);
            if (var2 != null) {
               this.icon = new ImageIconUIResource(var2);
            }
         }

         if (this.icon == null && this.fallbackName != null) {
            UIDefaults.LazyValue var3 = (UIDefaults.LazyValue)SwingUtilities2.makeIcon(WindowsLookAndFeel.class, BasicLookAndFeel.class, this.fallbackName);
            this.icon = (Icon)var3.createValue(var1);
         }

         return this.icon;
      }
   }

   private static class LazyWindowsIcon implements UIDefaults.LazyValue {
      private String nativeImage;
      private String resource;

      LazyWindowsIcon(String var1, String var2) {
         this.nativeImage = var1;
         this.resource = var2;
      }

      public Object createValue(UIDefaults var1) {
         if (this.nativeImage != null) {
            Image var2 = (Image)ShellFolder.get(this.nativeImage);
            if (var2 != null) {
               return new ImageIcon(var2);
            }
         }

         return SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, this.resource);
      }
   }

   private static class AudioAction extends AbstractAction {
      private Runnable audioRunnable;
      private String audioResource;

      public AudioAction(String var1, String var2) {
         super(var1);
         this.audioResource = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.audioRunnable == null) {
            this.audioRunnable = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty(this.audioResource);
         }

         if (this.audioRunnable != null) {
            (new Thread(this.audioRunnable)).start();
         }

      }
   }
}
