package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.JTextComponent;

public abstract class SynthStyle {
   private static Map<Object, Object> DEFAULT_VALUES;
   private static final SynthGraphicsUtils SYNTH_GRAPHICS = new SynthGraphicsUtils();

   private static void populateDefaultValues() {
      UIDefaults.LazyInputMap var0 = new UIDefaults.LazyInputMap(new Object[]{"SPACE", "pressed", "released SPACE", "released"});
      DEFAULT_VALUES.put("Button.focusInputMap", var0);
      DEFAULT_VALUES.put("CheckBox.focusInputMap", var0);
      DEFAULT_VALUES.put("RadioButton.focusInputMap", var0);
      DEFAULT_VALUES.put("ToggleButton.focusInputMap", var0);
      DEFAULT_VALUES.put("SynthArrowButton.focusInputMap", var0);
      DEFAULT_VALUES.put("List.dropLineColor", Color.BLACK);
      DEFAULT_VALUES.put("Tree.dropLineColor", Color.BLACK);
      DEFAULT_VALUES.put("Table.dropLineColor", Color.BLACK);
      DEFAULT_VALUES.put("Table.dropLineShortColor", Color.RED);
      UIDefaults.LazyInputMap var1 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation"});
      DEFAULT_VALUES.put("EditorPane.focusInputMap", var1);
      DEFAULT_VALUES.put("TextArea.focusInputMap", var1);
      DEFAULT_VALUES.put("TextPane.focusInputMap", var1);
      UIDefaults.LazyInputMap var2 = new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation"});
      DEFAULT_VALUES.put("TextField.focusInputMap", var2);
      DEFAULT_VALUES.put("PasswordField.focusInputMap", var2);
      DEFAULT_VALUES.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious"}));
      DEFAULT_VALUES.put("Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious"}));
      DEFAULT_VALUES.put("FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER", "approveSelection", "ctrl ENTER", "approveSelection"}));
      DEFAULT_VALUES.put("FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}));
      DEFAULT_VALUES.put("InternalFrame.icon", LookAndFeel.makeIcon(BasicLookAndFeel.class, "icons/JavaCup16.png"));
      DEFAULT_VALUES.put("InternalFrame.windowBindings", new Object[]{"shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu"});
      DEFAULT_VALUES.put("List.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}));
      DEFAULT_VALUES.put("List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead"}));
      DEFAULT_VALUES.put("MenuBar.windowBindings", new Object[]{"F10", "takeFocus"});
      DEFAULT_VALUES.put("OptionPane.windowBindings", new Object[]{"ESCAPE", "close"});
      DEFAULT_VALUES.put("RootPane.defaultButtonWindowKeyBindings", new Object[]{"ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release"});
      DEFAULT_VALUES.put("RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"shift F10", "postPopup"}));
      DEFAULT_VALUES.put("ScrollBar.anecstorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}));
      DEFAULT_VALUES.put("ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}));
      DEFAULT_VALUES.put("ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd"}));
      DEFAULT_VALUES.put("ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft"}));
      DEFAULT_VALUES.put("SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward"}));
      DEFAULT_VALUES.put("Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"}));
      DEFAULT_VALUES.put("Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"}));
      DEFAULT_VALUES.put("Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"}));
      DEFAULT_VALUES.put("TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus"}));
      DEFAULT_VALUES.put("TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent"}));
      DEFAULT_VALUES.put("Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader"}));
      DEFAULT_VALUES.put("TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable"}));
      DEFAULT_VALUES.put("Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ESCAPE", "cancel"}));
      DEFAULT_VALUES.put("Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{"ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo"}));
      DEFAULT_VALUES.put("Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[]{"RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild"}));
   }

   private static Object getDefaultValue(Object var0) {
      Class var1 = SynthStyle.class;
      synchronized(SynthStyle.class) {
         if (DEFAULT_VALUES == null) {
            DEFAULT_VALUES = new HashMap();
            populateDefaultValues();
         }

         Object var2 = DEFAULT_VALUES.get(var0);
         if (var2 instanceof UIDefaults.LazyValue) {
            var2 = ((UIDefaults.LazyValue)var2).createValue((UIDefaults)null);
            DEFAULT_VALUES.put(var0, var2);
         }

         return var2;
      }
   }

   public SynthGraphicsUtils getGraphicsUtils(SynthContext var1) {
      return SYNTH_GRAPHICS;
   }

   public Color getColor(SynthContext var1, ColorType var2) {
      JComponent var3 = var1.getComponent();
      Region var4 = var1.getRegion();
      if ((var1.getComponentState() & 8) != 0) {
         if (var3 instanceof JTextComponent) {
            JTextComponent var5 = (JTextComponent)var3;
            Color var6 = var5.getDisabledTextColor();
            if (var6 == null || var6 instanceof UIResource) {
               return this.getColorForState(var1, var2);
            }
         } else if (var3 instanceof JLabel && (var2 == ColorType.FOREGROUND || var2 == ColorType.TEXT_FOREGROUND)) {
            return this.getColorForState(var1, var2);
         }
      }

      Color var7 = null;
      if (!var4.isSubregion()) {
         if (var2 == ColorType.BACKGROUND) {
            var7 = var3.getBackground();
         } else if (var2 == ColorType.FOREGROUND) {
            var7 = var3.getForeground();
         } else if (var2 == ColorType.TEXT_FOREGROUND) {
            var7 = var3.getForeground();
         }
      }

      if (var7 == null || var7 instanceof UIResource) {
         var7 = this.getColorForState(var1, var2);
      }

      if (var7 == null) {
         if (var2 == ColorType.BACKGROUND || var2 == ColorType.TEXT_BACKGROUND) {
            return var3.getBackground();
         }

         if (var2 == ColorType.FOREGROUND || var2 == ColorType.TEXT_FOREGROUND) {
            return var3.getForeground();
         }
      }

      return var7;
   }

   protected abstract Color getColorForState(SynthContext var1, ColorType var2);

   public Font getFont(SynthContext var1) {
      JComponent var2 = var1.getComponent();
      if (var1.getComponentState() == 1) {
         return var2.getFont();
      } else {
         Font var3 = var2.getFont();
         return var3 != null && !(var3 instanceof UIResource) ? var3 : this.getFontForState(var1);
      }
   }

   protected abstract Font getFontForState(SynthContext var1);

   public Insets getInsets(SynthContext var1, Insets var2) {
      if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      }

      var2.top = var2.bottom = var2.left = var2.right = 0;
      return var2;
   }

   public SynthPainter getPainter(SynthContext var1) {
      return null;
   }

   public boolean isOpaque(SynthContext var1) {
      return true;
   }

   public Object get(SynthContext var1, Object var2) {
      return getDefaultValue(var2);
   }

   void installDefaults(SynthContext var1, SynthUI var2) {
      if (!var1.isSubregion()) {
         JComponent var3 = var1.getComponent();
         Border var4 = var3.getBorder();
         if (var4 == null || var4 instanceof UIResource) {
            var3.setBorder(new SynthBorder(var2, this.getInsets(var1, (Insets)null)));
         }
      }

      this.installDefaults(var1);
   }

   public void installDefaults(SynthContext var1) {
      if (!var1.isSubregion()) {
         JComponent var2 = var1.getComponent();
         Region var3 = var1.getRegion();
         Font var4 = var2.getFont();
         if (var4 == null || var4 instanceof UIResource) {
            var2.setFont(this.getFontForState(var1));
         }

         Color var5 = var2.getBackground();
         if (var5 == null || var5 instanceof UIResource) {
            var2.setBackground(this.getColorForState(var1, ColorType.BACKGROUND));
         }

         Color var6 = var2.getForeground();
         if (var6 == null || var6 instanceof UIResource) {
            var2.setForeground(this.getColorForState(var1, ColorType.FOREGROUND));
         }

         LookAndFeel.installProperty(var2, "opaque", this.isOpaque(var1));
      }

   }

   public void uninstallDefaults(SynthContext var1) {
      if (!var1.isSubregion()) {
         JComponent var2 = var1.getComponent();
         Border var3 = var2.getBorder();
         if (var3 instanceof UIResource) {
            var2.setBorder((Border)null);
         }
      }

   }

   public int getInt(SynthContext var1, Object var2, int var3) {
      Object var4 = this.get(var1, var2);
      return var4 instanceof Number ? ((Number)var4).intValue() : var3;
   }

   public boolean getBoolean(SynthContext var1, Object var2, boolean var3) {
      Object var4 = this.get(var1, var2);
      return var4 instanceof Boolean ? (Boolean)var4 : var3;
   }

   public Icon getIcon(SynthContext var1, Object var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Icon ? (Icon)var3 : null;
   }

   public String getString(SynthContext var1, Object var2, String var3) {
      Object var4 = this.get(var1, var2);
      return var4 instanceof String ? (String)var4 : var3;
   }
}
