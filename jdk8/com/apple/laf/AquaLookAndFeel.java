package com.apple.laf;

import apple.laf.JRSUIControl;
import apple.laf.JRSUIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.PopupFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class AquaLookAndFeel extends BasicLookAndFeel {
   static final String sOldPropertyPrefix = "com.apple.macos.";
   static final String sPropertyPrefix = "apple.laf.";
   private static final String PKG_PREFIX = "com.apple.laf.";
   private static final String kAquaImageFactoryName = "com.apple.laf.AquaImageFactory";
   private static final String kAquaFontsName = "com.apple.laf.AquaFonts";

   public String getName() {
      return "Mac OS X";
   }

   public String getID() {
      return "Aqua";
   }

   public String getDescription() {
      return "Aqua Look and Feel for Mac OS X";
   }

   public boolean getSupportsWindowDecorations() {
      return false;
   }

   public boolean isNativeLookAndFeel() {
      return true;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }

   public void initialize() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osxui");
            return null;
         }
      });
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            JRSUIControl.initJRSUI();
            return null;
         }
      });
      super.initialize();
      ScreenPopupFactory var1 = new ScreenPopupFactory();
      var1.setActive(true);
      PopupFactory.setSharedInstance(var1);
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(AquaMnemonicHandler.getInstance());
   }

   public void uninitialize() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(AquaMnemonicHandler.getInstance());
      PopupFactory var1 = PopupFactory.getSharedInstance();
      if (var1 != null && var1 instanceof ScreenPopupFactory) {
         ((ScreenPopupFactory)var1).setActive(false);
      }

      super.uninitialize();
   }

   protected ActionMap getAudioActionMap() {
      Object var1 = (ActionMap)UIManager.get("AuditoryCues.actionMap");
      if (var1 != null) {
         return (ActionMap)var1;
      } else {
         Object[] var2 = (Object[])((Object[])UIManager.get("AuditoryCues.cueList"));
         if (var2 != null) {
            var1 = new ActionMapUIResource();

            for(int var3 = var2.length - 1; var3 >= 0; --var3) {
               ((ActionMap)var1).put(var2[var3], this.createAudioAction(var2[var3]));
            }
         }

         UIManager.getLookAndFeelDefaults().put("AuditoryCues.actionMap", var1);
         return (ActionMap)var1;
      }
   }

   public UIDefaults getDefaults() {
      UIDefaults var1 = new UIDefaults();

      try {
         this.initClassDefaults(var1);
         super.initSystemColorDefaults(var1);
         super.initComponentDefaults(var1);
         this.initSystemColorDefaults(var1);
         this.initComponentDefaults(var1);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return var1;
   }

   private void initResourceBundle(UIDefaults var1) {
      var1.setDefaultLocale(Locale.getDefault());
      var1.addResourceBundle("com.apple.laf.resources.aqua");

      try {
         ResourceBundle var2 = ResourceBundle.getBundle("com.apple.laf.resources.aqua");
         Enumeration var3 = var2.getKeys();

         while(var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            var1.put(var4, var2.getString(var4));
         }
      } catch (Exception var5) {
      }

   }

   protected void initComponentDefaults(UIDefaults var1) {
      this.initResourceBundle(var1);
      InsetsUIResource var2 = new InsetsUIResource(0, 0, 0, 0);
      Boolean var4 = Boolean.TRUE;
      Boolean var5 = AquaUtils.shouldUseOpaqueButtons() ? Boolean.TRUE : Boolean.FALSE;
      UIDefaults.ActiveValue var6 = new UIDefaults.ActiveValue() {
         public Object createValue(UIDefaults var1) {
            return new DefaultListCellRenderer.UIResource();
         }
      };
      BorderUIResource.EmptyBorderUIResource var7 = new BorderUIResource.EmptyBorderUIResource(2, 0, 2, 0);
      ColorUIResource var8 = new ColorUIResource(255, 255, 204);
      ColorUIResource var9 = new ColorUIResource(Color.black);
      ColorUIResource var10 = new ColorUIResource(Color.white);
      ColorUIResource var11 = new ColorUIResource(new Color(0, 0, 0, 152));
      ColorUIResource var12 = new ColorUIResource(new Color(192, 192, 192, 192));
      ColorUIResource var13 = new ColorUIResource(new Color(0, 0, 0, 100));
      new ColorUIResource(new Color(255, 255, 255, 254));
      ColorUIResource var15 = new ColorUIResource(0.5F, 0.5F, 0.5F);
      ColorUIResource var16 = new ColorUIResource(0.25F, 0.25F, 0.25F);
      ColorUIResource var17 = new ColorUIResource(1.0F, 0.4F, 0.4F);
      ColorUIResource var18 = new ColorUIResource(240, 240, 240);
      ColorUIResource var19 = new ColorUIResource(new Color(1.0F, 1.0F, 1.0F, 0.55F));
      ColorUIResource var21 = new ColorUIResource(new Color(0.0F, 0.0F, 0.0F, 0.25F));
      ColorUIResource var24 = new ColorUIResource(140, 140, 140);
      SwingLazyValue var25 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
      Integer var26 = new Integer(0);
      Integer var28 = new Integer(500);
      SwingLazyValue var29 = new SwingLazyValue("com.apple.laf.AquaTextFieldBorder", "getTextFieldBorder");
      SwingLazyValue var31 = new SwingLazyValue("com.apple.laf.AquaScrollRegionBorder", "getScrollRegionBorder");
      SwingLazyValue var32 = new SwingLazyValue("com.apple.laf.AquaGroupBorder", "getBorderForTitledBorder");
      SwingLazyValue var33 = new SwingLazyValue("com.apple.laf.AquaGroupBorder", "getTitlelessBorder");
      AquaTableHeaderBorder var34 = AquaTableHeaderBorder.getListHeaderBorder();
      BorderUIResource.EmptyBorderUIResource var35 = new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0);
      Color var36 = AquaImageFactory.getSelectionBackgroundColorUIResource();
      Color var37 = AquaImageFactory.getSelectionForegroundColorUIResource();
      Color var38 = AquaImageFactory.getSelectionInactiveBackgroundColorUIResource();
      Color var39 = AquaImageFactory.getSelectionInactiveForegroundColorUIResource();
      Color var40 = AquaImageFactory.getTextSelectionForegroundColorUIResource();
      Color var41 = AquaImageFactory.getTextSelectionBackgroundColorUIResource();
      ColorUIResource var42 = new ColorUIResource(212, 212, 212);
      SwingLazyValue var48 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
      ColorUIResource var49 = new ColorUIResource(new Color(65, 105, 170));
      Color var50 = AquaImageFactory.getFocusRingColorUIResource();
      BorderUIResource.LineBorderUIResource var51 = new BorderUIResource.LineBorderUIResource(var50);
      Color var52 = AquaImageFactory.getWindowBackgroundColorUIResource();
      SwingLazyValue var56 = new SwingLazyValue("com.apple.laf.AquaFonts", "getControlTextFont");
      SwingLazyValue var57 = new SwingLazyValue("com.apple.laf.AquaFonts", "getControlTextSmallFont");
      SwingLazyValue var58 = new SwingLazyValue("com.apple.laf.AquaFonts", "getAlertHeaderFont");
      SwingLazyValue var59 = new SwingLazyValue("com.apple.laf.AquaFonts", "getMenuFont");
      SwingLazyValue var60 = new SwingLazyValue("com.apple.laf.AquaFonts", "getViewFont");
      ColorUIResource var61 = new ColorUIResource(Color.white);
      AquaMenuBorder var69 = new AquaMenuBorder();
      UIDefaults.LazyInputMap var70 = new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"});
      SwingLazyValue var71 = new SwingLazyValue("com.apple.laf.AquaImageFactory", "getConfirmImageIcon");
      SwingLazyValue var72 = new SwingLazyValue("com.apple.laf.AquaImageFactory", "getCautionImageIcon");
      SwingLazyValue var73 = new SwingLazyValue("com.apple.laf.AquaImageFactory", "getStopImageIcon");
      SwingLazyValue var74 = new SwingLazyValue("com.apple.laf.AquaImageFactory", "getLockImageIcon");
      AquaKeyBindings var75 = AquaKeyBindings.instance();
      Object[] var76 = new Object[]{"control", var52, "Button.background", var52, "Button.foreground", var9, "Button.disabledText", var15, "Button.select", var17, "Button.border", new SwingLazyValue("com.apple.laf.AquaButtonBorder", "getDynamicButtonBorder"), "Button.font", var56, "Button.textIconGap", new Integer(4), "Button.textShiftOffset", var26, "Button.focusInputMap", var70, "Button.margin", new InsetsUIResource(0, 2, 0, 2), "Button.opaque", var5, "CheckBox.background", var52, "CheckBox.foreground", var9, "CheckBox.disabledText", var15, "CheckBox.select", var17, "CheckBox.icon", new SwingLazyValue("com.apple.laf.AquaButtonCheckBoxUI", "getSizingCheckBoxIcon"), "CheckBox.font", var56, "CheckBox.border", AquaButtonBorder.getBevelButtonBorder(), "CheckBox.margin", new InsetsUIResource(1, 1, 0, 1), "CheckBox.focusInputMap", var70, "CheckBoxMenuItem.font", var59, "CheckBoxMenuItem.acceleratorFont", var59, "CheckBoxMenuItem.background", var61, "CheckBoxMenuItem.foreground", var9, "CheckBoxMenuItem.selectionBackground", var50, "CheckBoxMenuItem.selectionForeground", var10, "CheckBoxMenuItem.disabledBackground", var61, "CheckBoxMenuItem.disabledForeground", var15, "CheckBoxMenuItem.acceleratorForeground", var9, "CheckBoxMenuItem.acceleratorSelectionForeground", var9, "CheckBoxMenuItem.acceleratorDelimiter", "", "CheckBoxMenuItem.border", var69, "CheckBoxMenuItem.margin", var2, "CheckBoxMenuItem.borderPainted", Boolean.TRUE, "CheckBoxMenuItem.checkIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getMenuItemCheckIcon"), "CheckBoxMenuItem.dashIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getMenuItemDashIcon"), "ColorChooser.background", var52, "ComboBox.font", var56, "ComboBox.background", var52, "ComboBox.foreground", var9, "ComboBox.selectionBackground", var50, "ComboBox.selectionForeground", var10, "ComboBox.disabledBackground", var61, "ComboBox.disabledForeground", var15, "ComboBox.ancestorInputMap", var75.getComboBoxInputMap(), "DesktopIcon.border", var48, "DesktopIcon.borderColor", var11, "DesktopIcon.borderRimColor", var12, "DesktopIcon.labelBackground", var13, "Desktop.background", var49, "EditorPane.focusInputMap", var75.getMultiLineTextInputMap(), "EditorPane.font", var56, "EditorPane.background", var10, "EditorPane.foreground", var9, "EditorPane.selectionBackground", var41, "EditorPane.selectionForeground", var40, "EditorPane.caretForeground", var9, "EditorPane.caretBlinkRate", var28, "EditorPane.inactiveForeground", var15, "EditorPane.inactiveBackground", var10, "EditorPane.border", var25, "EditorPane.margin", var2, "FileChooser.newFolderIcon", AquaIcon.SystemIcon.getFolderIconUIResource(), "FileChooser.upFolderIcon", AquaIcon.SystemIcon.getFolderIconUIResource(), "FileChooser.homeFolderIcon", AquaIcon.SystemIcon.getDesktopIconUIResource(), "FileChooser.detailsViewIcon", AquaIcon.SystemIcon.getComputerIconUIResource(), "FileChooser.listViewIcon", AquaIcon.SystemIcon.getComputerIconUIResource(), "FileView.directoryIcon", AquaIcon.SystemIcon.getFolderIconUIResource(), "FileView.fileIcon", AquaIcon.SystemIcon.getDocumentIconUIResource(), "FileView.computerIcon", AquaIcon.SystemIcon.getDesktopIconUIResource(), "FileView.hardDriveIcon", AquaIcon.SystemIcon.getHardDriveIconUIResource(), "FileView.floppyDriveIcon", AquaIcon.SystemIcon.getFloppyIconUIResource(), "FileChooser.cancelButtonMnemonic", var26, "FileChooser.saveButtonMnemonic", var26, "FileChooser.openButtonMnemonic", var26, "FileChooser.updateButtonMnemonic", var26, "FileChooser.helpButtonMnemonic", var26, "FileChooser.directoryOpenButtonMnemonic", var26, "FileChooser.lookInLabelMnemonic", var26, "FileChooser.fileNameLabelMnemonic", var26, "FileChooser.filesOfTypeLabelMnemonic", var26, "Focus.color", var50, "FormattedTextField.focusInputMap", var75.getFormattedTextFieldInputMap(), "FormattedTextField.font", var56, "FormattedTextField.background", var10, "FormattedTextField.foreground", var9, "FormattedTextField.inactiveForeground", var15, "FormattedTextField.inactiveBackground", var10, "FormattedTextField.selectionBackground", var41, "FormattedTextField.selectionForeground", var40, "FormattedTextField.caretForeground", var9, "FormattedTextField.caretBlinkRate", var28, "FormattedTextField.border", var29, "FormattedTextField.margin", var2, "IconButton.font", var57, "InternalFrame.titleFont", var59, "InternalFrame.background", var52, "InternalFrame.borderColor", var52, "InternalFrame.borderShadow", Color.red, "InternalFrame.borderDarkShadow", Color.green, "InternalFrame.borderHighlight", Color.blue, "InternalFrame.borderLight", Color.yellow, "InternalFrame.opaque", Boolean.FALSE, "InternalFrame.border", null, "InternalFrame.icon", null, "InternalFrame.paletteBorder", null, "InternalFrame.paletteTitleFont", var59, "InternalFrame.paletteBackground", var52, "InternalFrame.optionDialogBorder", null, "InternalFrame.optionDialogTitleFont", var59, "InternalFrame.optionDialogBackground", var52, "InternalFrame.closeIcon", new SwingLazyValue("com.apple.laf.AquaInternalFrameUI", "exportCloseIcon"), "InternalFrame.maximizeIcon", new SwingLazyValue("com.apple.laf.AquaInternalFrameUI", "exportZoomIcon"), "InternalFrame.iconifyIcon", new SwingLazyValue("com.apple.laf.AquaInternalFrameUI", "exportMinimizeIcon"), "InternalFrame.minimizeIcon", new SwingLazyValue("com.apple.laf.AquaInternalFrameUI", "exportMinimizeIcon"), "InternalFrame.closeSound", null, "InternalFrame.maximizeSound", null, "InternalFrame.minimizeSound", null, "InternalFrame.restoreDownSound", null, "InternalFrame.restoreUpSound", null, "InternalFrame.activeTitleBackground", var52, "InternalFrame.activeTitleForeground", var9, "InternalFrame.inactiveTitleBackground", var52, "InternalFrame.inactiveTitleForeground", var15, "InternalFrame.windowBindings", new Object[]{"shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu"}, "TitledBorder.font", var56, "TitledBorder.titleColor", var9, "TitledBorder.aquaVariant", var32, "InsetBorder.aquaVariant", var33, "Label.font", var56, "Label.background", var52, "Label.foreground", var9, "Label.disabledForeground", var15, "Label.disabledShadow", var16, "Label.opaque", var4, "Label.border", null, "List.font", var60, "List.background", var10, "List.foreground", var9, "List.selectionBackground", var36, "List.selectionForeground", var37, "List.selectionInactiveBackground", var38, "List.selectionInactiveForeground", var39, "List.focusCellHighlightBorder", var51, "List.border", null, "List.cellRenderer", var6, "List.sourceListBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaListUI", "getSourceListBackgroundPainter"), "List.sourceListSelectionBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaListUI", "getSourceListSelectionBackgroundPainter"), "List.sourceListFocusedSelectionBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaListUI", "getSourceListFocusedSelectionBackgroundPainter"), "List.evenRowBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaListUI", "getListEvenBackgroundPainter"), "List.oddRowBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaListUI", "getListOddBackgroundPainter"), "List.focusInputMap", var75.getListInputMap(), "Menu.font", var59, "Menu.acceleratorFont", var59, "Menu.background", var61, "Menu.foreground", var9, "Menu.selectionBackground", var50, "Menu.selectionForeground", var10, "Menu.disabledBackground", var61, "Menu.disabledForeground", var15, "Menu.acceleratorForeground", var9, "Menu.acceleratorSelectionForeground", var9, "Menu.border", var69, "Menu.borderPainted", Boolean.FALSE, "Menu.margin", var2, "Menu.arrowIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getMenuArrowIcon"), "Menu.consumesTabs", Boolean.TRUE, "Menu.menuPopupOffsetY", new Integer(1), "Menu.submenuPopupOffsetY", new Integer(-4), "MenuBar.font", var59, "MenuBar.background", var61, "MenuBar.foreground", var9, "MenuBar.border", new AquaMenuBarBorder(), "MenuBar.margin", new InsetsUIResource(0, 8, 0, 8), "MenuBar.selectionBackground", var50, "MenuBar.selectionForeground", var10, "MenuBar.disabledBackground", var61, "MenuBar.disabledForeground", var15, "MenuBar.backgroundPainter", new SwingLazyValue("com.apple.laf.AquaMenuPainter", "getMenuBarPainter"), "MenuBar.selectedBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaMenuPainter", "getSelectedMenuBarItemPainter"), "MenuItem.font", var59, "MenuItem.acceleratorFont", var59, "MenuItem.background", var61, "MenuItem.foreground", var9, "MenuItem.selectionBackground", var50, "MenuItem.selectionForeground", var10, "MenuItem.disabledBackground", var61, "MenuItem.disabledForeground", var15, "MenuItem.acceleratorForeground", var9, "MenuItem.acceleratorSelectionForeground", var9, "MenuItem.acceleratorDelimiter", "", "MenuItem.border", var69, "MenuItem.margin", var2, "MenuItem.borderPainted", Boolean.TRUE, "MenuItem.selectedBackgroundPainter", new SwingLazyValue("com.apple.laf.AquaMenuPainter", "getSelectedMenuItemPainter"), "OptionPane.font", var58, "OptionPane.messageFont", var56, "OptionPane.buttonFont", var56, "OptionPane.background", var52, "OptionPane.foreground", var9, "OptionPane.messageForeground", var9, "OptionPane.border", new BorderUIResource.EmptyBorderUIResource(12, 21, 17, 21), "OptionPane.messageAreaBorder", var35, "OptionPane.buttonAreaBorder", new BorderUIResource.EmptyBorderUIResource(13, 0, 0, 0), "OptionPane.minimumSize", new DimensionUIResource(262, 90), "OptionPane.errorIcon", var73, "OptionPane.informationIcon", var71, "OptionPane.warningIcon", var72, "OptionPane.questionIcon", var71, "_SecurityDecisionIcon", var74, "OptionPane.windowBindings", new Object[]{"ESCAPE", "close"}, "OptionPane.errorSound", null, "OptionPane.informationSound", null, "OptionPane.questionSound", null, "OptionPane.warningSound", null, "OptionPane.buttonClickThreshhold", new Integer(500), "OptionPane.yesButtonMnemonic", "", "OptionPane.noButtonMnemonic", "", "OptionPane.okButtonMnemonic", "", "OptionPane.cancelButtonMnemonic", "", "Panel.font", var56, "Panel.background", var52, "Panel.foreground", var9, "Panel.opaque", var4, "PasswordField.focusInputMap", var75.getPasswordFieldInputMap(), "PasswordField.font", var56, "PasswordField.background", var10, "PasswordField.foreground", var9, "PasswordField.inactiveForeground", var15, "PasswordField.inactiveBackground", var10, "PasswordField.selectionBackground", var41, "PasswordField.selectionForeground", var40, "PasswordField.caretForeground", var9, "PasswordField.caretBlinkRate", var28, "PasswordField.border", var29, "PasswordField.margin", var2, "PasswordField.echoChar", new Character('●'), "PasswordField.capsLockIconColor", var13, "PopupMenu.font", var59, "PopupMenu.background", var61, "PopupMenu.translucentBackground", var10, "PopupMenu.foreground", var9, "PopupMenu.selectionBackground", var50, "PopupMenu.selectionForeground", var10, "PopupMenu.border", var69, "ProgressBar.font", var56, "ProgressBar.foreground", var9, "ProgressBar.background", var52, "ProgressBar.selectionForeground", var9, "ProgressBar.selectionBackground", var10, "ProgressBar.border", new BorderUIResource(BorderFactory.createEmptyBorder()), "ProgressBar.repaintInterval", new Integer(20), "RadioButton.background", var52, "RadioButton.foreground", var9, "RadioButton.disabledText", var15, "RadioButton.select", var17, "RadioButton.icon", new SwingLazyValue("com.apple.laf.AquaButtonRadioUI", "getSizingRadioButtonIcon"), "RadioButton.font", var56, "RadioButton.border", AquaButtonBorder.getBevelButtonBorder(), "RadioButton.margin", new InsetsUIResource(1, 1, 0, 1), "RadioButton.focusInputMap", var70, "RadioButtonMenuItem.font", var59, "RadioButtonMenuItem.acceleratorFont", var59, "RadioButtonMenuItem.background", var61, "RadioButtonMenuItem.foreground", var9, "RadioButtonMenuItem.selectionBackground", var50, "RadioButtonMenuItem.selectionForeground", var10, "RadioButtonMenuItem.disabledBackground", var61, "RadioButtonMenuItem.disabledForeground", var15, "RadioButtonMenuItem.acceleratorForeground", var9, "RadioButtonMenuItem.acceleratorSelectionForeground", var9, "RadioButtonMenuItem.acceleratorDelimiter", "", "RadioButtonMenuItem.border", var69, "RadioButtonMenuItem.margin", var2, "RadioButtonMenuItem.borderPainted", Boolean.TRUE, "RadioButtonMenuItem.checkIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getMenuItemCheckIcon"), "RadioButtonMenuItem.dashIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getMenuItemDashIcon"), "Separator.background", null, "Separator.foreground", new ColorUIResource(212, 212, 212), "ScrollBar.border", null, "ScrollBar.focusInputMap", var75.getScrollBarInputMap(), "ScrollBar.focusInputMap.RightToLeft", var75.getScrollBarRightToLeftInputMap(), "ScrollBar.width", new Integer(16), "ScrollBar.background", var10, "ScrollBar.foreground", var9, "ScrollPane.font", var56, "ScrollPane.background", var10, "ScrollPane.foreground", var9, "ScrollPane.border", var31, "ScrollPane.viewportBorder", null, "ScrollPane.ancestorInputMap", var75.getScrollPaneInputMap(), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[0]), "Viewport.font", var56, "Viewport.background", var10, "Viewport.foreground", var9, "Slider.foreground", var9, "Slider.background", var52, "Slider.font", var57, "Slider.tickColor", new ColorUIResource(Color.GRAY), "Slider.border", null, "Slider.focusInsets", new InsetsUIResource(2, 2, 2, 2), "Slider.focusInputMap", var75.getSliderInputMap(), "Slider.focusInputMap.RightToLeft", var75.getSliderRightToLeftInputMap(), "Spinner.font", var56, "Spinner.background", var52, "Spinner.foreground", var9, "Spinner.border", null, "Spinner.arrowButtonSize", new Dimension(16, 5), "Spinner.ancestorInputMap", var75.getSpinnerInputMap(), "Spinner.editorBorderPainted", Boolean.TRUE, "Spinner.editorAlignment", 11, "SplitPane.background", var52, "SplitPane.border", var31, "SplitPane.dividerSize", new Integer(9), "SplitPaneDivider.border", null, "SplitPaneDivider.horizontalGradientVariant", new SwingLazyValue("com.apple.laf.AquaSplitPaneDividerUI", "getHorizontalSplitDividerGradientVariant"), "TabbedPane.font", var56, "TabbedPane.smallFont", var57, "TabbedPane.useSmallLayout", Boolean.FALSE, "TabbedPane.background", var52, "TabbedPane.foreground", var9, "TabbedPane.opaque", var4, "TabbedPane.textIconGap", new Integer(4), "TabbedPane.tabInsets", new InsetsUIResource(0, 10, 3, 10), "TabbedPane.leftTabInsets", new InsetsUIResource(0, 10, 3, 10), "TabbedPane.rightTabInsets", new InsetsUIResource(0, 10, 3, 10), "TabbedPane.tabAreaInsets", new InsetsUIResource(3, 9, -1, 9), "TabbedPane.contentBorderInsets", new InsetsUIResource(8, 0, 0, 0), "TabbedPane.selectedTabPadInsets", new InsetsUIResource(0, 0, 0, 0), "TabbedPane.tabsOverlapBorder", Boolean.TRUE, "TabbedPane.selectedTabTitlePressedColor", var18, "TabbedPane.selectedTabTitleDisabledColor", var19, "TabbedPane.selectedTabTitleNormalColor", var10, "TabbedPane.selectedTabTitleShadowDisabledColor", var21, "TabbedPane.selectedTabTitleShadowNormalColor", var13, "TabbedPane.nonSelectedTabTitleNormalColor", var9, "Table.font", var60, "Table.foreground", var9, "Table.background", var10, "Table.selectionForeground", var37, "Table.selectionBackground", var36, "Table.selectionInactiveBackground", var38, "Table.selectionInactiveForeground", var39, "Table.gridColor", var10, "Table.focusCellBackground", var40, "Table.focusCellForeground", var41, "Table.focusCellHighlightBorder", var51, "Table.scrollPaneBorder", var31, "Table.ancestorInputMap", var75.getTableInputMap(), "Table.ancestorInputMap.RightToLeft", var75.getTableRightToLeftInputMap(), "TableHeader.font", var57, "TableHeader.foreground", var9, "TableHeader.background", var10, "TableHeader.cellBorder", var34, "TextArea.focusInputMap", var75.getMultiLineTextInputMap(), "TextArea.font", var56, "TextArea.background", var10, "TextArea.foreground", var9, "TextArea.inactiveForeground", var15, "TextArea.inactiveBackground", var10, "TextArea.selectionBackground", var41, "TextArea.selectionForeground", var40, "TextArea.caretForeground", var9, "TextArea.caretBlinkRate", var28, "TextArea.border", var25, "TextArea.margin", var2, "TextComponent.selectionBackgroundInactive", var42, "TextField.focusInputMap", var75.getTextFieldInputMap(), "TextField.font", var56, "TextField.background", var10, "TextField.foreground", var9, "TextField.inactiveForeground", var15, "TextField.inactiveBackground", var10, "TextField.selectionBackground", var41, "TextField.selectionForeground", var40, "TextField.caretForeground", var9, "TextField.caretBlinkRate", var28, "TextField.border", var29, "TextField.margin", var2, "TextPane.focusInputMap", var75.getMultiLineTextInputMap(), "TextPane.font", var56, "TextPane.background", var10, "TextPane.foreground", var9, "TextPane.selectionBackground", var41, "TextPane.selectionForeground", var40, "TextPane.caretForeground", var9, "TextPane.caretBlinkRate", var28, "TextPane.inactiveForeground", var15, "TextPane.inactiveBackground", var10, "TextPane.border", var25, "TextPane.margin", var2, "ToggleButton.background", var52, "ToggleButton.foreground", var9, "ToggleButton.disabledText", var15, "ToggleButton.border", new SwingLazyValue("com.apple.laf.AquaButtonBorder", "getDynamicButtonBorder"), "ToggleButton.font", var56, "ToggleButton.focusInputMap", var70, "ToggleButton.margin", new InsetsUIResource(2, 2, 2, 2), "ToolBar.font", var56, "ToolBar.background", var52, "ToolBar.foreground", new ColorUIResource(Color.gray), "ToolBar.dockingBackground", var52, "ToolBar.dockingForeground", var36, "ToolBar.floatingBackground", var52, "ToolBar.floatingForeground", new ColorUIResource(Color.darkGray), "ToolBar.border", new SwingLazyValue("com.apple.laf.AquaToolBarUI", "getToolBarBorder"), "ToolBar.borderHandleColor", var24, "ToolBar.separatorSize", null, "ToolBarButton.margin", new InsetsUIResource(3, 3, 3, 3), "ToolBarButton.insets", new InsetsUIResource(1, 1, 1, 1), "ToolTip.font", var57, "ToolTip.background", var8, "ToolTip.foreground", var9, "ToolTip.border", var7, "Tree.font", var60, "Tree.background", var10, "Tree.foreground", var9, "Tree.hash", var10, "Tree.line", var10, "Tree.textForeground", var9, "Tree.textBackground", var10, "Tree.selectionForeground", var37, "Tree.selectionBackground", var36, "Tree.selectionInactiveBackground", var38, "Tree.selectionInactiveForeground", var39, "Tree.selectionBorderColor", var36, "Tree.editorBorderSelectionColor", null, "Tree.leftChildIndent", new Integer(7), "Tree.rightChildIndent", new Integer(13), "Tree.rowHeight", new Integer(19), "Tree.scrollsOnExpand", Boolean.FALSE, "Tree.openIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeOpenFolderIcon"), "Tree.closedIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeFolderIcon"), "Tree.leafIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeDocumentIcon"), "Tree.expandedIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeExpandedIcon"), "Tree.collapsedIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeCollapsedIcon"), "Tree.rightToLeftCollapsedIcon", new SwingLazyValue("com.apple.laf.AquaImageFactory", "getTreeRightToLeftCollapsedIcon"), "Tree.changeSelectionWithFocus", Boolean.TRUE, "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE, "Tree.focusInputMap", var75.getTreeInputMap(), "Tree.focusInputMap.RightToLeft", var75.getTreeRightToLeftInputMap(), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"})};
      var1.putDefaults(var76);
      SwingUtilities2.AATextInfo var77 = SwingUtilities2.AATextInfo.getAATextInfo(true);
      var1.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var77);
   }

   protected void initSystemColorDefaults(UIDefaults var1) {
   }

   protected void initClassDefaults(UIDefaults var1) {
      Object[] var3 = new Object[]{"ButtonUI", "com.apple.laf.AquaButtonUI", "CheckBoxUI", "com.apple.laf.AquaButtonCheckBoxUI", "CheckBoxMenuItemUI", "com.apple.laf.AquaMenuItemUI", "LabelUI", "com.apple.laf.AquaLabelUI", "ListUI", "com.apple.laf.AquaListUI", "MenuUI", "com.apple.laf.AquaMenuUI", "MenuItemUI", "com.apple.laf.AquaMenuItemUI", "OptionPaneUI", "com.apple.laf.AquaOptionPaneUI", "PanelUI", "com.apple.laf.AquaPanelUI", "RadioButtonMenuItemUI", "com.apple.laf.AquaMenuItemUI", "RadioButtonUI", "com.apple.laf.AquaButtonRadioUI", "ProgressBarUI", "com.apple.laf.AquaProgressBarUI", "RootPaneUI", "com.apple.laf.AquaRootPaneUI", "SliderUI", "com.apple.laf.AquaSliderUI", "ScrollBarUI", "com.apple.laf.AquaScrollBarUI", "TabbedPaneUI", "com.apple.laf." + (JRSUIUtils.TabbedPane.shouldUseTabbedPaneContrastUI() ? "AquaTabbedPaneContrastUI" : "AquaTabbedPaneUI"), "TableUI", "com.apple.laf.AquaTableUI", "ToggleButtonUI", "com.apple.laf.AquaButtonToggleUI", "ToolBarUI", "com.apple.laf.AquaToolBarUI", "ToolTipUI", "com.apple.laf.AquaToolTipUI", "TreeUI", "com.apple.laf.AquaTreeUI", "InternalFrameUI", "com.apple.laf.AquaInternalFrameUI", "DesktopIconUI", "com.apple.laf.AquaInternalFrameDockIconUI", "DesktopPaneUI", "com.apple.laf.AquaInternalFramePaneUI", "EditorPaneUI", "com.apple.laf.AquaEditorPaneUI", "TextFieldUI", "com.apple.laf.AquaTextFieldUI", "TextPaneUI", "com.apple.laf.AquaTextPaneUI", "ComboBoxUI", "com.apple.laf.AquaComboBoxUI", "PopupMenuUI", "com.apple.laf.AquaPopupMenuUI", "TextAreaUI", "com.apple.laf.AquaTextAreaUI", "MenuBarUI", "com.apple.laf.AquaMenuBarUI", "FileChooserUI", "com.apple.laf.AquaFileChooserUI", "PasswordFieldUI", "com.apple.laf.AquaTextPasswordFieldUI", "TableHeaderUI", "com.apple.laf.AquaTableHeaderUI", "FormattedTextFieldUI", "com.apple.laf.AquaTextFieldFormattedUI", "SpinnerUI", "com.apple.laf.AquaSpinnerUI", "SplitPaneUI", "com.apple.laf.AquaSplitPaneUI", "ScrollPaneUI", "com.apple.laf.AquaScrollPaneUI", "PopupMenuSeparatorUI", "com.apple.laf.AquaPopupMenuSeparatorUI", "SeparatorUI", "com.apple.laf.AquaPopupMenuSeparatorUI", "ToolBarSeparatorUI", "com.apple.laf.AquaToolBarSeparatorUI", "ColorChooserUI", "javax.swing.plaf.basic.BasicColorChooserUI", "ViewportUI", "javax.swing.plaf.basic.BasicViewportUI"};
      var1.putDefaults(var3);
   }
}
