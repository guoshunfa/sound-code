package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import sun.awt.OSInfo;
import sun.awt.SunToolkit;
import sun.awt.UNIXToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLayoutStyle;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class GTKLookAndFeel extends SynthLookAndFeel {
   private static final boolean IS_22;
   static Object aaTextInfo;
   private static boolean isSunCJK;
   private static boolean gtkAAFontSettingsCond;
   private Font fallbackFont;
   private boolean inInitialize;
   private boolean pclInstalled;
   private GTKStyleFactory styleFactory;
   private static String gtkThemeName = "Default";
   static ReferenceQueue<GTKLookAndFeel> queue;

   static boolean is2_2() {
      return IS_22;
   }

   static GTKConstants.PositionType SwingOrientationConstantToGTK(int var0) {
      switch(var0) {
      case 1:
         return GTKConstants.PositionType.TOP;
      case 2:
         return GTKConstants.PositionType.LEFT;
      case 3:
         return GTKConstants.PositionType.BOTTOM;
      case 4:
         return GTKConstants.PositionType.RIGHT;
      default:
         assert false : "Unknown orientation: " + var0;

         return GTKConstants.PositionType.TOP;
      }
   }

   static GTKConstants.StateType synthStateToGTKStateType(int var0) {
      GTKConstants.StateType var1;
      switch(var0) {
      case 1:
      default:
         var1 = GTKConstants.StateType.NORMAL;
         break;
      case 2:
         var1 = GTKConstants.StateType.PRELIGHT;
         break;
      case 4:
         var1 = GTKConstants.StateType.ACTIVE;
         break;
      case 8:
         var1 = GTKConstants.StateType.INSENSITIVE;
         break;
      case 512:
         var1 = GTKConstants.StateType.SELECTED;
      }

      return var1;
   }

   static int synthStateToGTKState(Region var0, int var1) {
      short var2;
      if ((var1 & 4) != 0) {
         if (var0 != Region.RADIO_BUTTON && var0 != Region.CHECK_BOX && var0 != Region.MENU && var0 != Region.MENU_ITEM && var0 != Region.RADIO_BUTTON_MENU_ITEM && var0 != Region.CHECK_BOX_MENU_ITEM && var0 != Region.SPLIT_PANE) {
            var2 = 4;
         } else {
            var2 = 2;
         }
      } else if (var0 == Region.TABBED_PANE_TAB) {
         if ((var1 & 8) != 0) {
            var2 = 8;
         } else if ((var1 & 512) != 0) {
            var2 = 1;
         } else {
            var2 = 4;
         }
      } else if ((var1 & 512) != 0) {
         if (var0 == Region.MENU) {
            var2 = 2;
         } else if (var0 != Region.RADIO_BUTTON && var0 != Region.TOGGLE_BUTTON && var0 != Region.RADIO_BUTTON_MENU_ITEM && var0 != Region.CHECK_BOX_MENU_ITEM && var0 != Region.CHECK_BOX && var0 != Region.BUTTON) {
            var2 = 512;
         } else if ((var1 & 8) != 0) {
            var2 = 8;
         } else if ((var1 & 2) != 0) {
            var2 = 2;
         } else {
            var2 = 4;
         }
      } else if ((var1 & 2) != 0) {
         var2 = 2;
      } else if ((var1 & 8) != 0) {
         var2 = 8;
      } else if (var0 == Region.SLIDER_TRACK) {
         var2 = 4;
      } else {
         var2 = 1;
      }

      return var2;
   }

   static boolean isText(Region var0) {
      return var0 == Region.TEXT_FIELD || var0 == Region.FORMATTED_TEXT_FIELD || var0 == Region.LIST || var0 == Region.PASSWORD_FIELD || var0 == Region.SPINNER || var0 == Region.TABLE || var0 == Region.TEXT_AREA || var0 == Region.TEXT_FIELD || var0 == Region.TEXT_PANE || var0 == Region.TREE;
   }

   public UIDefaults getDefaults() {
      UIDefaults var1 = super.getDefaults();
      var1.put("TabbedPane.isTabRollover", Boolean.FALSE);
      var1.put("Synth.doNotSetTextAA", true);
      this.initResourceBundle(var1);
      this.initSystemColorDefaults(var1);
      this.initComponentDefaults(var1);
      this.installPropertyChangeListeners();
      return var1;
   }

   private void installPropertyChangeListeners() {
      if (!this.pclInstalled) {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         GTKLookAndFeel.WeakPCL var2 = new GTKLookAndFeel.WeakPCL(this, var1, "gnome.Net/ThemeName");
         var1.addPropertyChangeListener(var2.getKey(), var2);
         var2 = new GTKLookAndFeel.WeakPCL(this, var1, "gnome.Gtk/FontName");
         var1.addPropertyChangeListener(var2.getKey(), var2);
         var2 = new GTKLookAndFeel.WeakPCL(this, var1, "gnome.Xft/DPI");
         var1.addPropertyChangeListener(var2.getKey(), var2);
         flushUnreferenced();
         this.pclInstalled = true;
      }

   }

   private void initResourceBundle(UIDefaults var1) {
      var1.addResourceBundle("com.sun.java.swing.plaf.gtk.resources.gtk");
   }

   protected void initComponentDefaults(UIDefaults var1) {
      super.initComponentDefaults(var1);
      Integer var2 = 0;
      SwingLazyValue var3 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[]{var2, var2, var2, var2});
      GTKStyle.GTKLazyValue var4 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getUnselectedCellBorder");
      GTKStyle.GTKLazyValue var5 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getSelectedCellBorder");
      GTKStyle.GTKLazyValue var6 = new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder", "getNoFocusCellBorder");
      GTKStyleFactory var7 = (GTKStyleFactory)getStyleFactory();
      GTKStyle var8 = (GTKStyle)var7.getStyle((JComponent)null, Region.TREE);
      Color var9 = var8.getGTKColor(1, GTKColorType.TEXT_BACKGROUND);
      Color var10 = var8.getGTKColor(1, GTKColorType.BACKGROUND);
      Color var11 = var8.getGTKColor(1, GTKColorType.FOREGROUND);
      GTKStyle var12 = (GTKStyle)var7.getStyle((JComponent)null, Region.PROGRESS_BAR);
      int var13 = var12.getXThickness();
      int var14 = var12.getYThickness();
      int var15 = 150 - var13 * 2;
      int var16 = 20 - var14 * 2;
      int var17 = 22 - var13 * 2;
      int var18 = 80 - var14 * 2;
      Integer var19 = 500;
      InsetsUIResource var20 = new InsetsUIResource(0, 0, 0, 0);
      Double var21 = new Double(0.025D);
      Color var22 = var1.getColor("caretColor");
      Color var23 = var1.getColor("controlText");
      UIDefaults.LazyInputMap var24 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation"});
      UIDefaults.LazyInputMap var25 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-begin-line", "ctrl KP_LEFT", "caret-begin-line", "ctrl RIGHT", "caret-end-line", "ctrl KP_RIGHT", "caret-end-line", "ctrl shift LEFT", "selection-begin-line", "ctrl shift KP_LEFT", "selection-begin-line", "ctrl shift RIGHT", "selection-end-line", "ctrl shift KP_RIGHT", "selection-end-line", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation"});
      InsetsUIResource var26 = new InsetsUIResource(3, 3, 3, 3);
      UIDefaults.LazyInputMap var27 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation"});

      class FontLazyValue implements UIDefaults.LazyValue {
         private Region region;

         FontLazyValue(Region var2) {
            this.region = var2;
         }

         public Object createValue(UIDefaults var1) {
            GTKStyleFactory var2 = (GTKStyleFactory)SynthLookAndFeel.getStyleFactory();
            GTKStyle var3 = (GTKStyle)var2.getStyle((JComponent)null, this.region);
            return var3.getFontForState((SynthContext)null);
         }
      }

      Object[] var28 = new Object[]{"ArrowButton.size", 13, "Button.defaultButtonFollowsFocus", Boolean.FALSE, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released"}), "Button.font", new FontLazyValue(Region.BUTTON), "Button.margin", var20, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "CheckBox.icon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getCheckBoxIcon"), "CheckBox.font", new FontLazyValue(Region.CHECK_BOX), "CheckBox.margin", var20, "CheckBoxMenuItem.arrowIcon", null, "CheckBoxMenuItem.checkIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getCheckBoxMenuItemCheckIcon"), "CheckBoxMenuItem.font", new FontLazyValue(Region.CHECK_BOX_MENU_ITEM), "CheckBoxMenuItem.margin", var20, "CheckBoxMenuItem.alignAcceleratorText", Boolean.FALSE, "ColorChooser.showPreviewPanelText", Boolean.FALSE, "ColorChooser.panels", new UIDefaults.ActiveValue() {
         public Object createValue(UIDefaults var1) {
            return new AbstractColorChooserPanel[]{new GTKColorChooserPanel()};
         }
      }, "ColorChooser.font", new FontLazyValue(Region.COLOR_CHOOSER), "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious"}), "ComboBox.font", new FontLazyValue(Region.COMBO_BOX), "ComboBox.isEnterSelectablePopup", Boolean.TRUE, "EditorPane.caretForeground", var22, "EditorPane.caretAspectRatio", var21, "EditorPane.caretBlinkRate", var19, "EditorPane.margin", var26, "EditorPane.focusInputMap", var27, "EditorPane.font", new FontLazyValue(Region.EDITOR_PANE), "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancelSelection", "ctrl ENTER", "approveSelection"}), "FileChooserUI", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", "FormattedTextField.caretForeground", var22, "FormattedTextField.caretAspectRatio", var21, "FormattedTextField.caretBlinkRate", var19, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "FormattedTextField.font", new FontLazyValue(Region.FORMATTED_TEXT_FIELD), "InternalFrameTitlePane.titlePaneLayout", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.Metacity", "getTitlePaneLayout"), "InternalFrame.windowBindings", new Object[]{"shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu"}, "InternalFrame.layoutTitlePaneAtOrigin", Boolean.TRUE, "InternalFrame.useTaskBar", Boolean.TRUE, "InternalFrameTitlePane.iconifyButtonOpacity", null, "InternalFrameTitlePane.maximizeButtonOpacity", null, "InternalFrameTitlePane.closeButtonOpacity", null, "Label.font", new FontLazyValue(Region.LABEL), "List.background", var9, "List.focusCellHighlightBorder", var4, "List.focusSelectedCellHighlightBorder", var5, "List.noFocusBorder", var6, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead"}), "List.font", new FontLazyValue(Region.LIST), "List.rendererUseUIBorder", Boolean.FALSE, "Menu.arrowIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getMenuArrowIcon"), "Menu.checkIcon", null, "Menu.font", new FontLazyValue(Region.MENU), "Menu.margin", var20, "Menu.cancelMode", "hideMenuTree", "Menu.alignAcceleratorText", Boolean.FALSE, "Menu.useMenuBarForTopLevelMenus", Boolean.TRUE, "MenuBar.windowBindings", new Object[]{"F10", "takeFocus"}, "MenuBar.font", new FontLazyValue(Region.MENU_BAR), "MenuItem.arrowIcon", null, "MenuItem.checkIcon", null, "MenuItem.font", new FontLazyValue(Region.MENU_ITEM), "MenuItem.margin", var20, "MenuItem.alignAcceleratorText", Boolean.FALSE, "OptionPane.setButtonMargin", Boolean.FALSE, "OptionPane.sameSizeButtons", Boolean.TRUE, "OptionPane.buttonOrientation", new Integer(4), "OptionPane.minimumSize", new DimensionUIResource(262, 90), "OptionPane.buttonPadding", new Integer(10), "OptionPane.windowBindings", new Object[]{"ESCAPE", "close"}, "OptionPane.buttonClickThreshhold", new Integer(500), "OptionPane.isYesLast", Boolean.TRUE, "OptionPane.font", new FontLazyValue(Region.OPTION_PANE), "Panel.font", new FontLazyValue(Region.PANEL), "PasswordField.caretForeground", var22, "PasswordField.caretAspectRatio", var21, "PasswordField.caretBlinkRate", var19, "PasswordField.margin", var20, "PasswordField.focusInputMap", var25, "PasswordField.font", new FontLazyValue(Region.PASSWORD_FIELD), "PopupMenu.consumeEventOnClose", Boolean.TRUE, "PopupMenu.selectedWindowInputMapBindings", new Object[]{"ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return"}, "PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[]{"LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent"}, "PopupMenu.font", new FontLazyValue(Region.POPUP_MENU), "ProgressBar.horizontalSize", new DimensionUIResource(var15, var16), "ProgressBar.verticalSize", new DimensionUIResource(var17, var18), "ProgressBar.font", new FontLazyValue(Region.PROGRESS_BAR), "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed"}), "RadioButton.icon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getRadioButtonIcon"), "RadioButton.font", new FontLazyValue(Region.RADIO_BUTTON), "RadioButton.margin", var20, "RadioButtonMenuItem.arrowIcon", null, "RadioButtonMenuItem.checkIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getRadioButtonMenuItemCheckIcon"), "RadioButtonMenuItem.font", new FontLazyValue(Region.RADIO_BUTTON_MENU_ITEM), "RadioButtonMenuItem.margin", var20, "RadioButtonMenuItem.alignAcceleratorText", Boolean.FALSE, "RootPane.defaultButtonWindowKeyBindings", new Object[]{"ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release"}, "ScrollBar.squareButtons", Boolean.FALSE, "ScrollBar.thumbHeight", 14, "ScrollBar.width", 16, "ScrollBar.minimumThumbSize", new Dimension(8, 8), "ScrollBar.maximumThumbSize", new Dimension(4096, 4096), "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, "ScrollBar.alwaysShowThumb", Boolean.TRUE, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}), "Spinner.disableOnBoundaryValues", Boolean.TRUE, "ScrollPane.fillUpperCorner", Boolean.TRUE, "ScrollPane.fillLowerCorner", Boolean.TRUE, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd"}), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft"}), "ScrollPane.font", new FontLazyValue(Region.SCROLL_PANE), "Separator.insets", var20, "Separator.thickness", 2, "Slider.paintValue", Boolean.TRUE, "Slider.thumbWidth", 30, "Slider.thumbHeight", 14, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}), "Slider.onlyLeftMouseButtonDrag", Boolean.FALSE, "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}), "Spinner.font", new FontLazyValue(Region.SPINNER), "Spinner.editorAlignment", 10, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward"}), "SplitPane.size", 7, "SplitPane.oneTouchOffset", 2, "SplitPane.oneTouchButtonSize", 5, "SplitPane.supportsOneTouchButtons", Boolean.FALSE, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent", "SPACE", "selectTabWithFocus"}), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus"}), "TabbedPane.labelShift", 3, "TabbedPane.selectedLabelShift", 3, "TabbedPane.font", new FontLazyValue(Region.TABBED_PANE), "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 0, 1), "Table.scrollPaneBorder", var3, "Table.background", var9, "Table.focusCellBackground", var10, "Table.focusCellForeground", var11, "Table.focusCellHighlightBorder", var4, "Table.focusSelectedCellHighlightBorder", var5, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader"}), "Table.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "shift RIGHT", "selectPreviousColumnChangeLead", "shift KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection"}), "Table.font", new FontLazyValue(Region.TABLE), "Table.ascendingSortIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getAscendingSortIcon"), "Table.descendingSortIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getDescendingSortIcon"), "TableHeader.font", new FontLazyValue(Region.TABLE_HEADER), "TableHeader.alignSorterArrow", Boolean.TRUE, "TextArea.caretForeground", var22, "TextArea.caretAspectRatio", var21, "TextArea.caretBlinkRate", var19, "TextArea.margin", var20, "TextArea.focusInputMap", var27, "TextArea.font", new FontLazyValue(Region.TEXT_AREA), "TextField.caretForeground", var22, "TextField.caretAspectRatio", var21, "TextField.caretBlinkRate", var19, "TextField.margin", var20, "TextField.focusInputMap", var24, "TextField.font", new FontLazyValue(Region.TEXT_FIELD), "TextPane.caretForeground", var22, "TextPane.caretAspectRatio", var21, "TextPane.caretBlinkRate", var19, "TextPane.margin", var26, "TextPane.focusInputMap", var27, "TextPane.font", new FontLazyValue(Region.TEXT_PANE), "TitledBorder.titleColor", var23, "TitledBorder.border", new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1) {
            return new GTKPainter.TitledBorder();
         }
      }, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"}), "ToggleButton.font", new FontLazyValue(Region.TOGGLE_BUTTON), "ToggleButton.margin", var20, "ToolBar.separatorSize", new DimensionUIResource(10, 10), "ToolBar.handleIcon", new UIDefaults.ActiveValue() {
         public Object createValue(UIDefaults var1) {
            return GTKIconFactory.getToolBarHandleIcon();
         }
      }, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight"}), "ToolBar.font", new FontLazyValue(Region.TOOL_BAR), "ToolTip.font", new FontLazyValue(Region.TOOL_TIP), "Tree.padding", 4, "Tree.background", var9, "Tree.drawHorizontalLines", Boolean.FALSE, "Tree.drawVerticalLines", Boolean.FALSE, "Tree.rowHeight", -1, "Tree.scrollsOnExpand", Boolean.FALSE, "Tree.expanderSize", 10, "Tree.repaintWholeRow", Boolean.TRUE, "Tree.closedIcon", null, "Tree.leafIcon", null, "Tree.openIcon", null, "Tree.expandedIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getTreeExpandedIcon"), "Tree.collapsedIcon", new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.GTKIconFactory", "getTreeCollapsedIcon"), "Tree.leftChildIndent", 2, "Tree.rightChildIndent", 12, "Tree.scrollsHorizontallyAndVertically", Boolean.FALSE, "Tree.drawsFocusBorder", Boolean.TRUE, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "typed +", "expand", "typed -", "collapse", "BACK_SPACE", "moveSelectionToParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}), "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild"}), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"}), "Tree.font", new FontLazyValue(Region.TREE), "Viewport.font", new FontLazyValue(Region.VIEWPORT)};
      var1.putDefaults(var28);
      if (this.fallbackFont != null) {
         var1.put("TitledBorder.font", this.fallbackFont);
      }

      var1.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);
   }

   protected void initSystemColorDefaults(UIDefaults var1) {
      GTKStyleFactory var2 = (GTKStyleFactory)getStyleFactory();
      GTKStyle var3 = (GTKStyle)var2.getStyle((JComponent)null, Region.INTERNAL_FRAME);
      var1.put("window", var3.getGTKColor(1, GTKColorType.BACKGROUND));
      var1.put("windowText", var3.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
      GTKStyle var4 = (GTKStyle)var2.getStyle((JComponent)null, Region.TEXT_FIELD);
      var1.put("text", var4.getGTKColor(1, GTKColorType.TEXT_BACKGROUND));
      var1.put("textText", var4.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
      var1.put("textHighlight", var4.getGTKColor(512, GTKColorType.TEXT_BACKGROUND));
      var1.put("textHighlightText", var4.getGTKColor(512, GTKColorType.TEXT_FOREGROUND));
      var1.put("textInactiveText", var4.getGTKColor(8, GTKColorType.TEXT_FOREGROUND));
      Object var5 = var4.getClassSpecificValue("cursor-color");
      if (var5 == null) {
         var5 = GTKStyle.BLACK_COLOR;
      }

      var1.put("caretColor", var5);
      GTKStyle var6 = (GTKStyle)var2.getStyle((JComponent)null, Region.MENU_ITEM);
      var1.put("menu", var6.getGTKColor(1, GTKColorType.BACKGROUND));
      var1.put("menuText", var6.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
      GTKStyle var7 = (GTKStyle)var2.getStyle((JComponent)null, Region.SCROLL_BAR);
      var1.put("scrollbar", var7.getGTKColor(1, GTKColorType.BACKGROUND));
      GTKStyle var8 = (GTKStyle)var2.getStyle((JComponent)null, Region.OPTION_PANE);
      var1.put("info", var8.getGTKColor(1, GTKColorType.BACKGROUND));
      var1.put("infoText", var8.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
      GTKStyle var9 = (GTKStyle)var2.getStyle((JComponent)null, Region.DESKTOP_PANE);
      var1.put("desktop", var9.getGTKColor(1, GTKColorType.BACKGROUND));
      GTKStyle var10 = (GTKStyle)var2.getStyle((JComponent)null, Region.LABEL);
      Color var11 = var10.getGTKColor(1, GTKColorType.BACKGROUND);
      var1.put("control", var11);
      var1.put("controlHighlight", var11);
      var1.put("controlText", var10.getGTKColor(1, GTKColorType.TEXT_FOREGROUND));
      var1.put("controlLtHighlight", var10.getGTKColor(1, GTKColorType.LIGHT));
      var1.put("controlShadow", var10.getGTKColor(1, GTKColorType.DARK));
      var1.put("controlDkShadow", var10.getGTKColor(1, GTKColorType.BLACK));
      var1.put("light", var10.getGTKColor(1, GTKColorType.LIGHT));
      var1.put("mid", var10.getGTKColor(1, GTKColorType.MID));
      var1.put("dark", var10.getGTKColor(1, GTKColorType.DARK));
      var1.put("black", var10.getGTKColor(1, GTKColorType.BLACK));
      var1.put("white", var10.getGTKColor(1, GTKColorType.WHITE));
   }

   public static ComponentUI createUI(JComponent var0) {
      String var1 = var0.getUIClassID().intern();
      return var1 == "FileChooserUI" ? GTKFileChooserUI.createUI(var0) : SynthLookAndFeel.createUI(var0);
   }

   static String getGtkThemeName() {
      return gtkThemeName;
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   public void initialize() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (var1 instanceof UNIXToolkit && !((UNIXToolkit)var1).loadGTK()) {
         throw new InternalError("Unable to load native GTK libraries");
      } else {
         super.initialize();
         this.inInitialize = true;
         this.loadStyles();
         this.inInitialize = false;
         gtkAAFontSettingsCond = !isSunCJK && SwingUtilities2.isLocalDisplay();
         aaTextInfo = SwingUtilities2.AATextInfo.getAATextInfo(gtkAAFontSettingsCond);
      }
   }

   private static void flushUnreferenced() {
      GTKLookAndFeel.WeakPCL var0;
      while((var0 = (GTKLookAndFeel.WeakPCL)queue.poll()) != null) {
         var0.dispose();
      }

   }

   public boolean isSupportedLookAndFeel() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      return var1 instanceof SunToolkit && ((SunToolkit)var1).isNativeGTKAvailable();
   }

   public boolean isNativeLookAndFeel() {
      return true;
   }

   public String getDescription() {
      return "GTK look and feel";
   }

   public String getName() {
      return "GTK look and feel";
   }

   public String getID() {
      return "GTK";
   }

   protected void loadSystemColors(UIDefaults var1, String[] var2, boolean var3) {
      super.loadSystemColors(var1, var2, false);
   }

   private void loadStyles() {
      gtkThemeName = (String)Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Net/ThemeName");
      setStyleFactory(this.getGTKStyleFactory());
      if (!this.inInitialize) {
         UIDefaults var1 = UIManager.getLookAndFeelDefaults();
         this.initSystemColorDefaults(var1);
         this.initComponentDefaults(var1);
      }

   }

   private GTKStyleFactory getGTKStyleFactory() {
      GTKEngine var1 = GTKEngine.INSTANCE;
      Object var2 = var1.getSetting(GTKEngine.Settings.GTK_ICON_SIZES);
      if (var2 instanceof String && !this.configIconSizes((String)var2)) {
         System.err.println("Error parsing gtk-icon-sizes string: '" + var2 + "'");
      }

      Object var3 = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Gtk/FontName");
      if (!(var3 instanceof String)) {
         var3 = var1.getSetting(GTKEngine.Settings.GTK_FONT_NAME);
         if (!(var3 instanceof String)) {
            var3 = "sans 10";
         }
      }

      if (this.styleFactory == null) {
         this.styleFactory = new GTKStyleFactory();
      }

      Font var4 = PangoFonts.lookupFont((String)var3);
      this.fallbackFont = var4;
      this.styleFactory.initStyles(var4);
      return this.styleFactory;
   }

   private boolean configIconSizes(String var1) {
      String[] var2 = var1.split(":");

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String[] var4 = var2[var3].split("=");
         if (var4.length != 2) {
            return false;
         }

         String var5 = var4[0].trim().intern();
         if (var5.length() < 1) {
            return false;
         }

         var4 = var4[1].split(",");
         if (var4.length != 2) {
            return false;
         }

         String var6 = var4[0].trim();
         String var7 = var4[1].trim();
         if (var6.length() < 1 || var7.length() < 1) {
            return false;
         }

         int var8;
         int var9;
         try {
            var8 = Integer.parseInt(var6);
            var9 = Integer.parseInt(var7);
         } catch (NumberFormatException var11) {
            return false;
         }

         if (var8 > 0 && var9 > 0) {
            int var10 = GTKStyle.GTKStockIconInfo.getIconType(var5);
            GTKStyle.GTKStockIconInfo.setIconSize(var10, var8, var9);
         } else {
            System.err.println("Invalid size in gtk-icon-sizes: " + var8 + "," + var9);
         }
      }

      return true;
   }

   public boolean shouldUpdateStyleOnAncestorChanged() {
      return true;
   }

   public LayoutStyle getLayoutStyle() {
      return GTKLookAndFeel.GnomeLayoutStyle.INSTANCE;
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.gtk.version")));
      if (var0 != null) {
         IS_22 = var0.equals("2.2");
      } else {
         IS_22 = true;
      }

      String var1 = Locale.getDefault().getLanguage();
      boolean var2 = Locale.CHINESE.getLanguage().equals(var1) || Locale.JAPANESE.getLanguage().equals(var1) || Locale.KOREAN.getLanguage().equals(var1);
      if (var2) {
         boolean var3 = false;
         switch(OSInfo.getOSType()) {
         case SOLARIS:
            var3 = true;
            break;
         case LINUX:
            Boolean var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
               public Boolean run() {
                  File var1 = new File("/etc/sun-release");
                  return var1.exists();
               }
            });
            var3 = var4;
         }

         if (var3 && !SunGraphicsEnvironment.isOpenSolaris) {
            isSunCJK = true;
         }
      }

      queue = new ReferenceQueue();
   }

   private static class GnomeLayoutStyle extends DefaultLayoutStyle {
      private static GTKLookAndFeel.GnomeLayoutStyle INSTANCE = new GTKLookAndFeel.GnomeLayoutStyle();

      public int getPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, Container var5) {
         super.getPreferredGap(var1, var2, var3, var4, var5);
         switch(var3) {
         case INDENT:
            if (var4 == 3 || var4 == 7) {
               return 12;
            }
         case RELATED:
            if (this.isLabelAndNonlabel(var1, var2, var4)) {
               return 12;
            }

            return 6;
         case UNRELATED:
            return 12;
         default:
            return 0;
         }
      }

      public int getContainerGap(JComponent var1, int var2, Container var3) {
         super.getContainerGap(var1, var2, var3);
         return 12;
      }
   }

   static class WeakPCL extends WeakReference<GTKLookAndFeel> implements PropertyChangeListener {
      private Toolkit kit;
      private String key;

      WeakPCL(GTKLookAndFeel var1, Toolkit var2, String var3) {
         super(var1, GTKLookAndFeel.queue);
         this.kit = var2;
         this.key = var3;
      }

      public String getKey() {
         return this.key;
      }

      public void propertyChange(final PropertyChangeEvent var1) {
         final GTKLookAndFeel var2 = (GTKLookAndFeel)this.get();
         if (var2 != null && UIManager.getLookAndFeel() == var2) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  String var1x = var1.getPropertyName();
                  if ("gnome.Net/ThemeName".equals(var1x)) {
                     GTKEngine.INSTANCE.themeChanged();
                     GTKIconFactory.resetIcons();
                  }

                  var2.loadStyles();
                  Window[] var2x = Window.getWindows();

                  for(int var3 = 0; var3 < var2x.length; ++var3) {
                     SynthLookAndFeel.updateStyles(var2x[var3]);
                  }

               }
            });
         } else {
            this.dispose();
         }

      }

      void dispose() {
         this.kit.removePropertyChangeListener(this.key, this);
      }
   }
}
