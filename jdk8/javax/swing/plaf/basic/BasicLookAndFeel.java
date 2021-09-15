package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public abstract class BasicLookAndFeel extends LookAndFeel implements Serializable {
   static boolean needsEventHelper;
   private transient Object audioLock = new Object();
   private Clip clipPlaying;
   BasicLookAndFeel.AWTEventHelper invocator = null;
   private PropertyChangeListener disposer = null;

   public UIDefaults getDefaults() {
      UIDefaults var1 = new UIDefaults(610, 0.75F);
      this.initClassDefaults(var1);
      this.initSystemColorDefaults(var1);
      this.initComponentDefaults(var1);
      return var1;
   }

   public void initialize() {
      if (needsEventHelper) {
         this.installAWTEventListener();
      }

   }

   void installAWTEventListener() {
      if (this.invocator == null) {
         this.invocator = new BasicLookAndFeel.AWTEventHelper();
         needsEventHelper = true;
         this.disposer = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent var1) {
               BasicLookAndFeel.this.uninitialize();
            }
         };
         AppContext.getAppContext().addPropertyChangeListener("guidisposed", this.disposer);
      }

   }

   public void uninitialize() {
      AppContext var1 = AppContext.getAppContext();
      Object var3;
      synchronized(BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
         var3 = var1.get(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
         if (var3 != null) {
            ((BasicPopupMenuUI.MouseGrabber)var3).uninstall();
         }
      }

      synchronized(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
         var3 = var1.get(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
         if (var3 != null) {
            ((BasicPopupMenuUI.MenuKeyboardHelper)var3).uninstall();
         }
      }

      if (this.invocator != null) {
         AccessController.doPrivileged((PrivilegedAction)this.invocator);
         this.invocator = null;
      }

      if (this.disposer != null) {
         var1.removePropertyChangeListener("guidisposed", this.disposer);
         this.disposer = null;
      }

   }

   protected void initClassDefaults(UIDefaults var1) {
      Object[] var3 = new Object[]{"ButtonUI", "javax.swing.plaf.basic.BasicButtonUI", "CheckBoxUI", "javax.swing.plaf.basic.BasicCheckBoxUI", "ColorChooserUI", "javax.swing.plaf.basic.BasicColorChooserUI", "FormattedTextFieldUI", "javax.swing.plaf.basic.BasicFormattedTextFieldUI", "MenuBarUI", "javax.swing.plaf.basic.BasicMenuBarUI", "MenuUI", "javax.swing.plaf.basic.BasicMenuUI", "MenuItemUI", "javax.swing.plaf.basic.BasicMenuItemUI", "CheckBoxMenuItemUI", "javax.swing.plaf.basic.BasicCheckBoxMenuItemUI", "RadioButtonMenuItemUI", "javax.swing.plaf.basic.BasicRadioButtonMenuItemUI", "RadioButtonUI", "javax.swing.plaf.basic.BasicRadioButtonUI", "ToggleButtonUI", "javax.swing.plaf.basic.BasicToggleButtonUI", "PopupMenuUI", "javax.swing.plaf.basic.BasicPopupMenuUI", "ProgressBarUI", "javax.swing.plaf.basic.BasicProgressBarUI", "ScrollBarUI", "javax.swing.plaf.basic.BasicScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.basic.BasicScrollPaneUI", "SplitPaneUI", "javax.swing.plaf.basic.BasicSplitPaneUI", "SliderUI", "javax.swing.plaf.basic.BasicSliderUI", "SeparatorUI", "javax.swing.plaf.basic.BasicSeparatorUI", "SpinnerUI", "javax.swing.plaf.basic.BasicSpinnerUI", "ToolBarSeparatorUI", "javax.swing.plaf.basic.BasicToolBarSeparatorUI", "PopupMenuSeparatorUI", "javax.swing.plaf.basic.BasicPopupMenuSeparatorUI", "TabbedPaneUI", "javax.swing.plaf.basic.BasicTabbedPaneUI", "TextAreaUI", "javax.swing.plaf.basic.BasicTextAreaUI", "TextFieldUI", "javax.swing.plaf.basic.BasicTextFieldUI", "PasswordFieldUI", "javax.swing.plaf.basic.BasicPasswordFieldUI", "TextPaneUI", "javax.swing.plaf.basic.BasicTextPaneUI", "EditorPaneUI", "javax.swing.plaf.basic.BasicEditorPaneUI", "TreeUI", "javax.swing.plaf.basic.BasicTreeUI", "LabelUI", "javax.swing.plaf.basic.BasicLabelUI", "ListUI", "javax.swing.plaf.basic.BasicListUI", "ToolBarUI", "javax.swing.plaf.basic.BasicToolBarUI", "ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI", "ComboBoxUI", "javax.swing.plaf.basic.BasicComboBoxUI", "TableUI", "javax.swing.plaf.basic.BasicTableUI", "TableHeaderUI", "javax.swing.plaf.basic.BasicTableHeaderUI", "InternalFrameUI", "javax.swing.plaf.basic.BasicInternalFrameUI", "DesktopPaneUI", "javax.swing.plaf.basic.BasicDesktopPaneUI", "DesktopIconUI", "javax.swing.plaf.basic.BasicDesktopIconUI", "FileChooserUI", "javax.swing.plaf.basic.BasicFileChooserUI", "OptionPaneUI", "javax.swing.plaf.basic.BasicOptionPaneUI", "PanelUI", "javax.swing.plaf.basic.BasicPanelUI", "ViewportUI", "javax.swing.plaf.basic.BasicViewportUI", "RootPaneUI", "javax.swing.plaf.basic.BasicRootPaneUI"};
      var1.putDefaults(var3);
   }

   protected void initSystemColorDefaults(UIDefaults var1) {
      String[] var2 = new String[]{"desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", "menu", "#C0C0C0", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", "infoText", "#000000"};
      this.loadSystemColors(var1, var2, this.isNativeLookAndFeel());
   }

   protected void loadSystemColors(UIDefaults var1, String[] var2, boolean var3) {
      int var4;
      Color var5;
      if (var3) {
         for(var4 = 0; var4 < var2.length; var4 += 2) {
            var5 = Color.black;

            try {
               String var6 = var2[var4];
               var5 = (Color)((Color)SystemColor.class.getField(var6).get((Object)null));
            } catch (Exception var8) {
            }

            var1.put(var2[var4], new ColorUIResource(var5));
         }
      } else {
         for(var4 = 0; var4 < var2.length; var4 += 2) {
            var5 = Color.black;

            try {
               var5 = Color.decode(var2[var4 + 1]);
            } catch (NumberFormatException var7) {
               var7.printStackTrace();
            }

            var1.put(var2[var4], new ColorUIResource(var5));
         }
      }

   }

   private void initResourceBundle(UIDefaults var1) {
      var1.setDefaultLocale(Locale.getDefault());
      var1.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
   }

   protected void initComponentDefaults(UIDefaults var1) {
      this.initResourceBundle(var1);
      Integer var2 = new Integer(500);
      Long var3 = new Long(1000L);
      Integer var4 = new Integer(12);
      Integer var5 = new Integer(0);
      Integer var6 = new Integer(1);
      SwingLazyValue var7 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Dialog", var5, var4});
      SwingLazyValue var8 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Serif", var5, var4});
      SwingLazyValue var9 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"SansSerif", var5, var4});
      SwingLazyValue var10 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Monospaced", var5, var4});
      SwingLazyValue var11 = new SwingLazyValue("javax.swing.plaf.FontUIResource", (String)null, new Object[]{"Dialog", var6, var4});
      ColorUIResource var12 = new ColorUIResource(Color.red);
      ColorUIResource var13 = new ColorUIResource(Color.black);
      ColorUIResource var14 = new ColorUIResource(Color.white);
      ColorUIResource var15 = new ColorUIResource(Color.yellow);
      ColorUIResource var16 = new ColorUIResource(Color.gray);
      new ColorUIResource(Color.lightGray);
      ColorUIResource var18 = new ColorUIResource(Color.darkGray);
      ColorUIResource var19 = new ColorUIResource(224, 224, 224);
      Color var20 = var1.getColor("control");
      Color var21 = var1.getColor("controlDkShadow");
      Color var22 = var1.getColor("controlHighlight");
      Color var23 = var1.getColor("controlLtHighlight");
      Color var24 = var1.getColor("controlShadow");
      Color var25 = var1.getColor("controlText");
      Color var26 = var1.getColor("menu");
      Color var27 = var1.getColor("menuText");
      Color var28 = var1.getColor("textHighlight");
      Color var29 = var1.getColor("textHighlightText");
      Color var30 = var1.getColor("textInactiveText");
      Color var31 = var1.getColor("textText");
      Color var32 = var1.getColor("window");
      InsetsUIResource var33 = new InsetsUIResource(0, 0, 0, 0);
      InsetsUIResource var34 = new InsetsUIResource(2, 2, 2, 2);
      InsetsUIResource var35 = new InsetsUIResource(3, 3, 3, 3);
      SwingLazyValue var36 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
      SwingLazyValue var37 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
      SwingLazyValue var38 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
      SwingLazyValue var39 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
      SwingLazyValue var40 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource");
      SwingLazyValue var41 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", (String)null, new Object[]{var15});
      BorderUIResource.EmptyBorderUIResource var42 = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
      SwingLazyValue var43 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$BevelBorderUIResource", (String)null, new Object[]{new Integer(0), var23, var20, var21, var24});
      SwingLazyValue var44 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder");
      SwingLazyValue var45 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getToggleButtonBorder");
      SwingLazyValue var46 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
      Object var47 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/NewFolder.gif");
      Object var48 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/UpFolder.gif");
      Object var49 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/HomeFolder.gif");
      Object var50 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/DetailsView.gif");
      Object var51 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/ListView.gif");
      Object var52 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Directory.gif");
      Object var53 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/File.gif");
      Object var54 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Computer.gif");
      Object var55 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/HardDrive.gif");
      Object var56 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/FloppyDrive.gif");
      SwingLazyValue var57 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
      UIDefaults.ActiveValue var58 = new UIDefaults.ActiveValue() {
         public Object createValue(UIDefaults var1) {
            return new DefaultListCellRenderer.UIResource();
         }
      };
      SwingLazyValue var59 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
      SwingLazyValue var60 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemCheckIcon");
      SwingLazyValue var61 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemArrowIcon");
      SwingLazyValue var62 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuArrowIcon");
      SwingLazyValue var63 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxIcon");
      SwingLazyValue var64 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonIcon");
      SwingLazyValue var65 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxMenuItemIcon");
      SwingLazyValue var66 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonMenuItemIcon");
      String var67 = "+";
      DimensionUIResource var68 = new DimensionUIResource(262, 90);
      Integer var69 = new Integer(0);
      SwingLazyValue var70 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[]{var69, var69, var69, var69});
      Integer var71 = new Integer(10);
      SwingLazyValue var72 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[]{var71, var71, var4, var71});
      SwingLazyValue var73 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[]{new Integer(6), var69, var69, var69});
      SwingLazyValue var74 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getProgressBarBorder");
      DimensionUIResource var75 = new DimensionUIResource(8, 8);
      DimensionUIResource var76 = new DimensionUIResource(4096, 4096);
      DimensionUIResource var78 = new DimensionUIResource(10, 10);
      SwingLazyValue var79 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneBorder");
      SwingLazyValue var80 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneDividerBorder");
      InsetsUIResource var81 = new InsetsUIResource(0, 4, 1, 4);
      InsetsUIResource var82 = new InsetsUIResource(2, 2, 2, 1);
      InsetsUIResource var83 = new InsetsUIResource(3, 2, 0, 2);
      InsetsUIResource var84 = new InsetsUIResource(2, 2, 3, 3);
      SwingLazyValue var85 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder");
      Integer var88 = new Integer(4);
      Object[] var89 = new Object[]{"CheckBoxMenuItem.commandSound", "InternalFrame.closeSound", "InternalFrame.maximizeSound", "InternalFrame.minimizeSound", "InternalFrame.restoreDownSound", "InternalFrame.restoreUpSound", "MenuItem.commandSound", "OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound", "PopupMenu.popupSound", "RadioButtonMenuItem.commandSound"};
      Object[] var90 = new Object[]{"mute"};
      Object[] var91 = new Object[]{"AuditoryCues.cueList", var89, "AuditoryCues.allAuditoryCues", var89, "AuditoryCues.noAuditoryCues", var90, "AuditoryCues.playList", null, "Button.defaultButtonFollowsFocus", Boolean.TRUE, "Button.font", var7, "Button.background", var20, "Button.foreground", var25, "Button.shadow", var24, "Button.darkShadow", var21, "Button.light", var22, "Button.highlight", var23, "Button.border", var44, "Button.margin", new InsetsUIResource(2, 14, 2, 14), "Button.textIconGap", var88, "Button.textShiftOffset", var69, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released"}), "ToggleButton.font", var7, "ToggleButton.background", var20, "ToggleButton.foreground", var25, "ToggleButton.shadow", var24, "ToggleButton.darkShadow", var21, "ToggleButton.light", var22, "ToggleButton.highlight", var23, "ToggleButton.border", var45, "ToggleButton.margin", new InsetsUIResource(2, 14, 2, 14), "ToggleButton.textIconGap", var88, "ToggleButton.textShiftOffset", var69, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "RadioButton.font", var7, "RadioButton.background", var20, "RadioButton.foreground", var25, "RadioButton.shadow", var24, "RadioButton.darkShadow", var21, "RadioButton.light", var22, "RadioButton.highlight", var23, "RadioButton.border", var46, "RadioButton.margin", var34, "RadioButton.textIconGap", var88, "RadioButton.textShiftOffset", var69, "RadioButton.icon", var64, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed"}), "CheckBox.font", var7, "CheckBox.background", var20, "CheckBox.foreground", var25, "CheckBox.border", var46, "CheckBox.margin", var34, "CheckBox.textIconGap", var88, "CheckBox.textShiftOffset", var69, "CheckBox.icon", var63, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "FileChooser.useSystemExtensionHiding", Boolean.FALSE, "ColorChooser.font", var7, "ColorChooser.background", var20, "ColorChooser.foreground", var25, "ColorChooser.swatchesSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesDefaultRecentColor", var20, "ComboBox.font", var9, "ComboBox.background", var32, "ComboBox.foreground", var31, "ComboBox.buttonBackground", var20, "ComboBox.buttonShadow", var24, "ComboBox.buttonDarkShadow", var21, "ComboBox.buttonHighlight", var23, "ComboBox.selectionBackground", var28, "ComboBox.selectionForeground", var29, "ComboBox.disabledBackground", var20, "ComboBox.disabledForeground", var30, "ComboBox.timeFactor", var3, "ComboBox.isEnterSelectablePopup", Boolean.FALSE, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "ENTER", "enterPressed"}), "ComboBox.noActionOnKeyNavigation", Boolean.FALSE, "FileChooser.newFolderIcon", var47, "FileChooser.upFolderIcon", var48, "FileChooser.homeFolderIcon", var49, "FileChooser.detailsViewIcon", var50, "FileChooser.listViewIcon", var51, "FileChooser.readOnly", Boolean.FALSE, "FileChooser.usesSingleFilePane", Boolean.FALSE, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancelSelection", "F5", "refresh"}), "FileView.directoryIcon", var52, "FileView.fileIcon", var53, "FileView.computerIcon", var54, "FileView.hardDriveIcon", var55, "FileView.floppyDriveIcon", var56, "InternalFrame.titleFont", var11, "InternalFrame.borderColor", var20, "InternalFrame.borderShadow", var24, "InternalFrame.borderDarkShadow", var21, "InternalFrame.borderHighlight", var23, "InternalFrame.borderLight", var22, "InternalFrame.border", var57, "InternalFrame.icon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeSound", null, "InternalFrame.maximizeSound", null, "InternalFrame.minimizeSound", null, "InternalFrame.restoreDownSound", null, "InternalFrame.restoreUpSound", null, "InternalFrame.activeTitleBackground", var1.get("activeCaption"), "InternalFrame.activeTitleForeground", var1.get("activeCaptionText"), "InternalFrame.inactiveTitleBackground", var1.get("inactiveCaption"), "InternalFrame.inactiveTitleForeground", var1.get("inactiveCaptionText"), "InternalFrame.windowBindings", new Object[]{"shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu"}, "InternalFrameTitlePane.iconifyButtonOpacity", Boolean.TRUE, "InternalFrameTitlePane.maximizeButtonOpacity", Boolean.TRUE, "InternalFrameTitlePane.closeButtonOpacity", Boolean.TRUE, "DesktopIcon.border", var57, "Desktop.minOnScreenInsets", var35, "Desktop.background", var1.get("desktop"), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious"}), "Label.font", var7, "Label.background", var20, "Label.foreground", var25, "Label.disabledForeground", var14, "Label.disabledShadow", var24, "Label.border", null, "List.font", var7, "List.background", var32, "List.foreground", var31, "List.selectionBackground", var28, "List.selectionForeground", var29, "List.noFocusBorder", var42, "List.focusCellHighlightBorder", var41, "List.dropLineColor", var24, "List.border", null, "List.cellRenderer", var58, "List.timeFactor", var3, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead"}), "MenuBar.font", var7, "MenuBar.background", var26, "MenuBar.foreground", var27, "MenuBar.shadow", var24, "MenuBar.highlight", var23, "MenuBar.border", var59, "MenuBar.windowBindings", new Object[]{"F10", "takeFocus"}, "MenuItem.font", var7, "MenuItem.acceleratorFont", var7, "MenuItem.background", var26, "MenuItem.foreground", var27, "MenuItem.selectionForeground", var29, "MenuItem.selectionBackground", var28, "MenuItem.disabledForeground", null, "MenuItem.acceleratorForeground", var27, "MenuItem.acceleratorSelectionForeground", var29, "MenuItem.acceleratorDelimiter", var67, "MenuItem.border", var36, "MenuItem.borderPainted", Boolean.FALSE, "MenuItem.margin", var34, "MenuItem.checkIcon", var60, "MenuItem.arrowIcon", var61, "MenuItem.commandSound", null, "RadioButtonMenuItem.font", var7, "RadioButtonMenuItem.acceleratorFont", var7, "RadioButtonMenuItem.background", var26, "RadioButtonMenuItem.foreground", var27, "RadioButtonMenuItem.selectionForeground", var29, "RadioButtonMenuItem.selectionBackground", var28, "RadioButtonMenuItem.disabledForeground", null, "RadioButtonMenuItem.acceleratorForeground", var27, "RadioButtonMenuItem.acceleratorSelectionForeground", var29, "RadioButtonMenuItem.border", var36, "RadioButtonMenuItem.borderPainted", Boolean.FALSE, "RadioButtonMenuItem.margin", var34, "RadioButtonMenuItem.checkIcon", var66, "RadioButtonMenuItem.arrowIcon", var61, "RadioButtonMenuItem.commandSound", null, "CheckBoxMenuItem.font", var7, "CheckBoxMenuItem.acceleratorFont", var7, "CheckBoxMenuItem.background", var26, "CheckBoxMenuItem.foreground", var27, "CheckBoxMenuItem.selectionForeground", var29, "CheckBoxMenuItem.selectionBackground", var28, "CheckBoxMenuItem.disabledForeground", null, "CheckBoxMenuItem.acceleratorForeground", var27, "CheckBoxMenuItem.acceleratorSelectionForeground", var29, "CheckBoxMenuItem.border", var36, "CheckBoxMenuItem.borderPainted", Boolean.FALSE, "CheckBoxMenuItem.margin", var34, "CheckBoxMenuItem.checkIcon", var65, "CheckBoxMenuItem.arrowIcon", var61, "CheckBoxMenuItem.commandSound", null, "Menu.font", var7, "Menu.acceleratorFont", var7, "Menu.background", var26, "Menu.foreground", var27, "Menu.selectionForeground", var29, "Menu.selectionBackground", var28, "Menu.disabledForeground", null, "Menu.acceleratorForeground", var27, "Menu.acceleratorSelectionForeground", var29, "Menu.border", var36, "Menu.borderPainted", Boolean.FALSE, "Menu.margin", var34, "Menu.checkIcon", var60, "Menu.arrowIcon", var62, "Menu.menuPopupOffsetX", new Integer(0), "Menu.menuPopupOffsetY", new Integer(0), "Menu.submenuPopupOffsetX", new Integer(0), "Menu.submenuPopupOffsetY", new Integer(0), "Menu.shortcutKeys", new int[]{SwingUtilities2.getSystemMnemonicKeyMask()}, "Menu.crossMenuMnemonic", Boolean.TRUE, "Menu.cancelMode", "hideLastSubmenu", "Menu.preserveTopLevelSelection", Boolean.FALSE, "PopupMenu.font", var7, "PopupMenu.background", var26, "PopupMenu.foreground", var27, "PopupMenu.border", var39, "PopupMenu.popupSound", null, "PopupMenu.selectedWindowInputMapBindings", new Object[]{"ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "ctrl ENTER", "return", "SPACE", "return"}, "PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[]{"LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent"}, "PopupMenu.consumeEventOnClose", Boolean.FALSE, "OptionPane.font", var7, "OptionPane.background", var20, "OptionPane.foreground", var25, "OptionPane.messageForeground", var25, "OptionPane.border", var72, "OptionPane.messageAreaBorder", var70, "OptionPane.buttonAreaBorder", var73, "OptionPane.minimumSize", var68, "OptionPane.errorIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Error.gif"), "OptionPane.informationIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Inform.gif"), "OptionPane.warningIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Warn.gif"), "OptionPane.questionIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Question.gif"), "OptionPane.windowBindings", new Object[]{"ESCAPE", "close"}, "OptionPane.errorSound", null, "OptionPane.informationSound", null, "OptionPane.questionSound", null, "OptionPane.warningSound", null, "OptionPane.buttonClickThreshhold", var2, "Panel.font", var7, "Panel.background", var20, "Panel.foreground", var31, "ProgressBar.font", var7, "ProgressBar.foreground", var28, "ProgressBar.background", var20, "ProgressBar.selectionForeground", var20, "ProgressBar.selectionBackground", var28, "ProgressBar.border", var74, "ProgressBar.cellLength", new Integer(1), "ProgressBar.cellSpacing", var69, "ProgressBar.repaintInterval", new Integer(50), "ProgressBar.cycleTime", new Integer(3000), "ProgressBar.horizontalSize", new DimensionUIResource(146, 12), "ProgressBar.verticalSize", new DimensionUIResource(12, 146), "Separator.shadow", var24, "Separator.highlight", var23, "Separator.background", var23, "Separator.foreground", var24, "ScrollBar.background", var19, "ScrollBar.foreground", var20, "ScrollBar.track", var1.get("scrollbar"), "ScrollBar.trackHighlight", var21, "ScrollBar.thumb", var20, "ScrollBar.thumbHighlight", var23, "ScrollBar.thumbDarkShadow", var21, "ScrollBar.thumbShadow", var24, "ScrollBar.border", null, "ScrollBar.minimumThumbSize", var75, "ScrollBar.maximumThumbSize", var76, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}), "ScrollBar.width", new Integer(16), "ScrollPane.font", var7, "ScrollPane.background", var20, "ScrollPane.foreground", var25, "ScrollPane.border", var85, "ScrollPane.viewportBorder", null, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd"}), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft"}), "Viewport.font", var7, "Viewport.background", var20, "Viewport.foreground", var31, "Slider.font", var7, "Slider.foreground", var20, "Slider.background", var20, "Slider.highlight", var23, "Slider.tickColor", Color.black, "Slider.shadow", var24, "Slider.focus", var21, "Slider.border", null, "Slider.horizontalSize", new Dimension(200, 21), "Slider.verticalSize", new Dimension(21, 200), "Slider.minimumHorizontalSize", new Dimension(36, 21), "Slider.minimumVerticalSize", new Dimension(21, 36), "Slider.focusInsets", var34, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}), "Slider.onlyLeftMouseButtonDrag", Boolean.TRUE, "Spinner.font", var10, "Spinner.background", var20, "Spinner.foreground", var20, "Spinner.border", var85, "Spinner.arrowButtonBorder", null, "Spinner.arrowButtonInsets", null, "Spinner.arrowButtonSize", new Dimension(16, 5), "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "Spinner.editorBorderPainted", Boolean.FALSE, "Spinner.editorAlignment", 11, "SplitPane.background", var20, "SplitPane.highlight", var23, "SplitPane.shadow", var24, "SplitPane.darkShadow", var21, "SplitPane.border", var79, "SplitPane.dividerSize", new Integer(7), "SplitPaneDivider.border", var80, "SplitPaneDivider.draggingColor", var18, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward"}), "TabbedPane.font", var7, "TabbedPane.background", var20, "TabbedPane.foreground", var25, "TabbedPane.highlight", var23, "TabbedPane.light", var22, "TabbedPane.shadow", var24, "TabbedPane.darkShadow", var21, "TabbedPane.selected", null, "TabbedPane.focus", var25, "TabbedPane.textIconGap", var88, "TabbedPane.tabsOverlapBorder", Boolean.FALSE, "TabbedPane.selectionFollowsFocus", Boolean.TRUE, "TabbedPane.labelShift", 1, "TabbedPane.selectedLabelShift", -1, "TabbedPane.tabInsets", var81, "TabbedPane.selectedTabPadInsets", var82, "TabbedPane.tabAreaInsets", var83, "TabbedPane.contentBorderInsets", var84, "TabbedPane.tabRunOverlay", new Integer(2), "TabbedPane.tabsOpaque", Boolean.TRUE, "TabbedPane.contentOpaque", Boolean.TRUE, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent"}), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus"}), "Table.font", var7, "Table.foreground", var25, "Table.background", var32, "Table.selectionForeground", var29, "Table.selectionBackground", var28, "Table.dropLineColor", var24, "Table.dropLineShortColor", var13, "Table.gridColor", var16, "Table.focusCellBackground", var32, "Table.focusCellForeground", var25, "Table.focusCellHighlightBorder", var41, "Table.scrollPaneBorder", var38, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader"}), "Table.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection"}), "Table.ascendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", (String)null, new Object[]{Boolean.TRUE, "Table.sortIconColor"}), "Table.descendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", (String)null, new Object[]{Boolean.FALSE, "Table.sortIconColor"}), "Table.sortIconColor", var24, "TableHeader.font", var7, "TableHeader.foreground", var25, "TableHeader.background", var20, "TableHeader.cellBorder", var43, "TableHeader.focusCellBackground", var1.getColor("text"), "TableHeader.focusCellForeground", null, "TableHeader.focusCellBorder", null, "TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable"}), "TextField.font", var9, "TextField.background", var32, "TextField.foreground", var31, "TextField.shadow", var24, "TextField.darkShadow", var21, "TextField.light", var22, "TextField.highlight", var23, "TextField.inactiveForeground", var30, "TextField.inactiveBackground", var20, "TextField.selectionBackground", var28, "TextField.selectionForeground", var29, "TextField.caretForeground", var31, "TextField.caretBlinkRate", var2, "TextField.border", var85, "TextField.margin", var33, "FormattedTextField.font", var9, "FormattedTextField.background", var32, "FormattedTextField.foreground", var31, "FormattedTextField.inactiveForeground", var30, "FormattedTextField.inactiveBackground", var20, "FormattedTextField.selectionBackground", var28, "FormattedTextField.selectionForeground", var29, "FormattedTextField.caretForeground", var31, "FormattedTextField.caretBlinkRate", var2, "FormattedTextField.border", var85, "FormattedTextField.margin", var33, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "PasswordField.font", var10, "PasswordField.background", var32, "PasswordField.foreground", var31, "PasswordField.inactiveForeground", var30, "PasswordField.inactiveBackground", var20, "PasswordField.selectionBackground", var28, "PasswordField.selectionForeground", var29, "PasswordField.caretForeground", var31, "PasswordField.caretBlinkRate", var2, "PasswordField.border", var85, "PasswordField.margin", var33, "PasswordField.echoChar", '*', "TextArea.font", var10, "TextArea.background", var32, "TextArea.foreground", var31, "TextArea.inactiveForeground", var30, "TextArea.selectionBackground", var28, "TextArea.selectionForeground", var29, "TextArea.caretForeground", var31, "TextArea.caretBlinkRate", var2, "TextArea.border", var36, "TextArea.margin", var33, "TextPane.font", var8, "TextPane.background", var14, "TextPane.foreground", var31, "TextPane.selectionBackground", var28, "TextPane.selectionForeground", var29, "TextPane.caretForeground", var31, "TextPane.caretBlinkRate", var2, "TextPane.inactiveForeground", var30, "TextPane.border", var36, "TextPane.margin", var35, "EditorPane.font", var8, "EditorPane.background", var14, "EditorPane.foreground", var31, "EditorPane.selectionBackground", var28, "EditorPane.selectionForeground", var29, "EditorPane.caretForeground", var31, "EditorPane.caretBlinkRate", var2, "EditorPane.inactiveForeground", var30, "EditorPane.border", var36, "EditorPane.margin", var35, "html.pendingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"), "html.missingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-failed.png"), "TitledBorder.font", var7, "TitledBorder.titleColor", var25, "TitledBorder.border", var37, "ToolBar.font", var7, "ToolBar.background", var20, "ToolBar.foreground", var25, "ToolBar.shadow", var24, "ToolBar.darkShadow", var21, "ToolBar.light", var22, "ToolBar.highlight", var23, "ToolBar.dockingBackground", var20, "ToolBar.dockingForeground", var12, "ToolBar.floatingBackground", var20, "ToolBar.floatingForeground", var18, "ToolBar.border", var37, "ToolBar.separatorSize", var78, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight"}), "ToolTip.font", var9, "ToolTip.background", var1.get("info"), "ToolTip.foreground", var1.get("infoText"), "ToolTip.border", var40, "ToolTipManager.enableToolTipMode", "allWindows", "Tree.paintLines", Boolean.TRUE, "Tree.lineTypeDashed", Boolean.FALSE, "Tree.font", var7, "Tree.background", var32, "Tree.foreground", var31, "Tree.hash", var16, "Tree.textForeground", var31, "Tree.textBackground", var1.get("text"), "Tree.selectionForeground", var29, "Tree.selectionBackground", var28, "Tree.selectionBorderColor", var13, "Tree.dropLineColor", var24, "Tree.editorBorder", var40, "Tree.leftChildIndent", new Integer(7), "Tree.rightChildIndent", new Integer(13), "Tree.rowHeight", new Integer(16), "Tree.scrollsOnExpand", Boolean.TRUE, "Tree.openIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeOpen.gif"), "Tree.closedIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeClosed.gif"), "Tree.leafIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeLeaf.gif"), "Tree.expandedIcon", null, "Tree.collapsedIcon", null, "Tree.changeSelectionWithFocus", Boolean.TRUE, "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE, "Tree.timeFactor", var3, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild"}), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"}), "RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"shift F10", "postPopup", "CONTEXT_MENU", "postPopup"}), "RootPane.defaultButtonWindowKeyBindings", new Object[]{"ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release"}};
      var1.putDefaults(var91);
   }

   static int getFocusAcceleratorKeyMask() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return var0 instanceof SunToolkit ? ((SunToolkit)var0).getFocusAcceleratorKeyMask() : 8;
   }

   static Object getUIOfType(ComponentUI var0, Class var1) {
      return var1.isInstance(var0) ? var0 : null;
   }

   protected ActionMap getAudioActionMap() {
      Object var1 = (ActionMap)UIManager.get("AuditoryCues.actionMap");
      if (var1 == null) {
         Object[] var2 = (Object[])((Object[])UIManager.get("AuditoryCues.cueList"));
         if (var2 != null) {
            var1 = new ActionMapUIResource();

            for(int var3 = var2.length - 1; var3 >= 0; --var3) {
               ((ActionMap)var1).put(var2[var3], this.createAudioAction(var2[var3]));
            }
         }

         UIManager.getLookAndFeelDefaults().put("AuditoryCues.actionMap", var1);
      }

      return (ActionMap)var1;
   }

   protected Action createAudioAction(Object var1) {
      if (var1 != null) {
         String var2 = (String)var1;
         String var3 = (String)UIManager.get(var1);
         return new BasicLookAndFeel.AudioAction(var2, var3);
      } else {
         return null;
      }
   }

   private byte[] loadAudioData(final String var1) {
      if (var1 == null) {
         return null;
      } else {
         byte[] var2 = (byte[])AccessController.doPrivileged(new PrivilegedAction<byte[]>() {
            public byte[] run() {
               try {
                  InputStream var1x = BasicLookAndFeel.this.getClass().getResourceAsStream(var1);
                  if (var1x == null) {
                     return null;
                  } else {
                     BufferedInputStream var2 = new BufferedInputStream(var1x);
                     ByteArrayOutputStream var3 = new ByteArrayOutputStream(1024);
                     byte[] var4 = new byte[1024];

                     int var5;
                     while((var5 = var2.read(var4)) > 0) {
                        var3.write(var4, 0, var5);
                     }

                     var2.close();
                     var3.flush();
                     var4 = var3.toByteArray();
                     return var4;
                  }
               } catch (IOException var6) {
                  System.err.println(var6.toString());
                  return null;
               }
            }
         });
         if (var2 == null) {
            System.err.println(this.getClass().getName() + "/" + var1 + " not found.");
            return null;
         } else if (var2.length == 0) {
            System.err.println("warning: " + var1 + " is zero-length");
            return null;
         } else {
            return var2;
         }
      }
   }

   protected void playSound(Action var1) {
      if (var1 != null) {
         Object[] var2 = (Object[])((Object[])UIManager.get("AuditoryCues.playList"));
         if (var2 != null) {
            HashSet var3 = new HashSet();
            Object[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Object var7 = var4[var6];
               var3.add(var7);
            }

            String var8 = (String)var1.getValue("Name");
            if (var3.contains(var8)) {
               var1.actionPerformed(new ActionEvent(this, 1001, var8));
            }
         }
      }

   }

   static void installAudioActionMap(ActionMap var0) {
      LookAndFeel var1 = UIManager.getLookAndFeel();
      if (var1 instanceof BasicLookAndFeel) {
         var0.setParent(((BasicLookAndFeel)var1).getAudioActionMap());
      }

   }

   static void playSound(JComponent var0, Object var1) {
      LookAndFeel var2 = UIManager.getLookAndFeel();
      if (var2 instanceof BasicLookAndFeel) {
         ActionMap var3 = var0.getActionMap();
         if (var3 != null) {
            Action var4 = var3.get(var1);
            if (var4 != null) {
               ((BasicLookAndFeel)var2).playSound(var4);
            }
         }
      }

   }

   class AWTEventHelper implements AWTEventListener, PrivilegedAction<Object> {
      AWTEventHelper() {
         AccessController.doPrivileged((PrivilegedAction)this);
      }

      public Object run() {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         if (BasicLookAndFeel.this.invocator == null) {
            var1.addAWTEventListener(this, 16L);
         } else {
            var1.removeAWTEventListener(BasicLookAndFeel.this.invocator);
         }

         return null;
      }

      public void eventDispatched(AWTEvent var1) {
         int var2 = var1.getID();
         Object var5;
         if (((long)var2 & 16L) != 0L) {
            MouseEvent var3 = (MouseEvent)var1;
            if (var3.isPopupTrigger()) {
               MenuElement[] var4 = MenuSelectionManager.defaultManager().getSelectedPath();
               if (var4 != null && var4.length != 0) {
                  return;
               }

               var5 = var3.getSource();
               JComponent var6 = null;
               if (var5 instanceof JComponent) {
                  var6 = (JComponent)var5;
               } else if (var5 instanceof BasicSplitPaneDivider) {
                  var6 = (JComponent)((BasicSplitPaneDivider)var5).getParent();
               }

               if (var6 != null && var6.getComponentPopupMenu() != null) {
                  Point var7 = var6.getPopupLocation(var3);
                  if (var7 == null) {
                     var7 = var3.getPoint();
                     var7 = SwingUtilities.convertPoint((Component)var5, var7, var6);
                  }

                  var6.getComponentPopupMenu().show(var6, var7.x, var7.y);
                  var3.consume();
               }
            }
         }

         if (var2 == 501) {
            Object var9 = var1.getSource();
            if (!(var9 instanceof Component)) {
               return;
            }

            Component var10 = (Component)var9;
            if (var10 != null) {
               for(var5 = var10; var5 != null && !(var5 instanceof Window); var5 = ((Component)var5).getParent()) {
                  if (var5 instanceof JInternalFrame) {
                     try {
                        ((JInternalFrame)var5).setSelected(true);
                     } catch (PropertyVetoException var8) {
                     }
                  }
               }
            }
         }

      }
   }

   private class AudioAction extends AbstractAction implements LineListener {
      private String audioResource;
      private byte[] audioBuffer;

      public AudioAction(String var2, String var3) {
         super(var2);
         this.audioResource = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.audioBuffer == null) {
            this.audioBuffer = BasicLookAndFeel.this.loadAudioData(this.audioResource);
         }

         if (this.audioBuffer != null) {
            this.cancelCurrentSound((Clip)null);

            try {
               AudioInputStream var2 = AudioSystem.getAudioInputStream((InputStream)(new ByteArrayInputStream(this.audioBuffer)));
               DataLine.Info var3 = new DataLine.Info(Clip.class, var2.getFormat());
               Clip var4 = (Clip)AudioSystem.getLine(var3);
               var4.open(var2);
               var4.addLineListener(this);
               synchronized(BasicLookAndFeel.this.audioLock) {
                  BasicLookAndFeel.this.clipPlaying = var4;
               }

               var4.start();
            } catch (Exception var8) {
            }
         }

      }

      public void update(LineEvent var1) {
         if (var1.getType() == LineEvent.Type.STOP) {
            this.cancelCurrentSound((Clip)var1.getLine());
         }

      }

      private void cancelCurrentSound(Clip var1) {
         Clip var2 = null;
         synchronized(BasicLookAndFeel.this.audioLock) {
            if (var1 == null || var1 == BasicLookAndFeel.this.clipPlaying) {
               var2 = BasicLookAndFeel.this.clipPlaying;
               BasicLookAndFeel.this.clipPlaying = null;
            }
         }

         if (var2 != null) {
            var2.removeLineListener(this);
            var2.close();
         }

      }
   }
}
