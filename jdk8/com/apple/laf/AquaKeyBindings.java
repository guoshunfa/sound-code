package com.apple.laf;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;
import javax.swing.text.Utilities;

public class AquaKeyBindings {
   static final AquaUtils.RecyclableSingleton<AquaKeyBindings> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaKeyBindings.class);
   final DefaultEditorKit.DefaultKeyTypedAction defaultKeyTypedAction = new DefaultEditorKit.DefaultKeyTypedAction();
   static final String upMultilineAction = "aqua-move-up";
   static final String downMultilineAction = "aqua-move-down";
   static final String pageUpMultiline = "aqua-page-up";
   static final String pageDownMultiline = "aqua-page-down";
   final String[] commonTextEditorBindings = new String[]{"ENTER", "notify-field-accept", "COPY", "copy-to-clipboard", "CUT", "cut-to-clipboard", "PASTE", "paste-from-clipboard", "meta A", "select-all", "meta C", "copy-to-clipboard", "meta V", "paste-from-clipboard", "meta X", "cut-to-clipboard", "meta BACK_SLASH", "unselect", "DELETE", "delete-next", "alt DELETE", "delete-next-word", "BACK_SPACE", "delete-previous", "alt BACK_SPACE", "delete-previous-word", "LEFT", "caret-backward", "KP_LEFT", "caret-backward", "RIGHT", "caret-forward", "KP_RIGHT", "caret-forward", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "meta LEFT", "caret-begin-line", "meta KP_LEFT", "caret-begin-line", "meta RIGHT", "caret-end-line", "meta KP_RIGHT", "caret-end-line", "shift meta LEFT", "selection-begin-line", "shift meta KP_LEFT", "selection-begin-line", "shift meta RIGHT", "selection-end-line", "shift meta KP_RIGHT", "selection-end-line", "alt LEFT", "caret-previous-word", "alt KP_LEFT", "caret-previous-word", "alt RIGHT", "caret-next-word", "alt KP_RIGHT", "caret-next-word", "shift alt LEFT", "selection-previous-word", "shift alt KP_LEFT", "selection-previous-word", "shift alt RIGHT", "selection-next-word", "shift alt KP_RIGHT", "selection-next-word", "control A", "caret-begin-line", "control B", "caret-backward", "control D", "delete-next", "control E", "caret-end-line", "control F", "caret-forward", "control H", "delete-previous", "control W", "delete-previous-word", "control shift O", "toggle-componentOrientation", "END", "caret-end", "HOME", "caret-begin", "shift END", "selection-end", "shift HOME", "selection-begin", "PAGE_DOWN", "aqua-page-down", "PAGE_UP", "aqua-page-up", "shift PAGE_DOWN", "selection-page-down", "shift PAGE_UP", "selection-page-up", "meta shift PAGE_DOWN", "selection-page-right", "meta shift PAGE_UP", "selection-page-left", "meta DOWN", "caret-end", "meta KP_DOWN", "caret-end", "meta UP", "caret-begin", "meta KP_UP", "caret-begin", "shift meta DOWN", "selection-end", "shift meta KP_DOWN", "selection-end", "shift meta UP", "selection-begin", "shift meta KP_UP", "selection-begin"};
   final TextAction moveUpMultilineAction = new AquaKeyBindings.AquaMultilineAction("aqua-move-up", "caret-up", "caret-begin");
   final TextAction moveDownMultilineAction = new AquaKeyBindings.AquaMultilineAction("aqua-move-down", "caret-down", "caret-end");
   final TextAction pageUpMultilineAction = new AquaKeyBindings.AquaMultilineAction("aqua-page-up", "page-up", "caret-begin");
   final TextAction pageDownMultilineAction = new AquaKeyBindings.AquaMultilineAction("aqua-page-down", "page-down", "caret-end");

   static AquaKeyBindings instance() {
      return (AquaKeyBindings)instance.get();
   }

   void setDefaultAction(String var1) {
      Keymap var2 = JTextComponent.getKeymap(var1);
      var2.setDefaultAction(this.defaultKeyTypedAction);
   }

   AquaKeyBindings.LateBoundInputMap getTextFieldInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(this.commonTextEditorBindings), new AquaKeyBindings.SimpleBinding(new String[]{"DOWN", "caret-end-line", "KP_DOWN", "caret-end-line", "UP", "caret-begin-line", "KP_UP", "caret-begin-line", "shift DOWN", "selection-end-line", "shift KP_DOWN", "selection-end-line", "shift UP", "selection-begin-line", "shift KP_UP", "selection-begin-line", "control P", "caret-begin", "control N", "caret-end", "control V", "caret-end"})});
   }

   AquaKeyBindings.LateBoundInputMap getPasswordFieldInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(this.getTextFieldInputMap().getBindings()), new AquaKeyBindings.SimpleBinding(new String[]{"alt LEFT", null, "alt KP_LEFT", null, "alt RIGHT", null, "alt KP_RIGHT", null, "shift alt LEFT", null, "shift alt KP_LEFT", null, "shift alt RIGHT", null, "shift alt KP_RIGHT", null})});
   }

   AquaKeyBindings.LateBoundInputMap getMultiLineTextInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(this.commonTextEditorBindings), new AquaKeyBindings.SimpleBinding(new String[]{"ENTER", "insert-break", "DOWN", "aqua-move-down", "KP_DOWN", "aqua-move-down", "UP", "aqua-move-up", "KP_UP", "aqua-move-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "shift UP", "selection-up", "shift KP_UP", "selection-up", "alt shift DOWN", "selection-end-paragraph", "alt shift KP_DOWN", "selection-end-paragraph", "alt shift UP", "selection-begin-paragraph", "alt shift KP_UP", "selection-begin-paragraph", "control P", "caret-up", "control N", "caret-down", "control V", "aqua-page-down", "TAB", "insert-tab", "meta SPACE", "activate-link-action", "meta T", "next-link-action", "meta shift T", "previous-link-action", "END", "caret-end", "HOME", "caret-begin", "shift END", "selection-end", "shift HOME", "selection-begin", "PAGE_DOWN", "aqua-page-down", "PAGE_UP", "aqua-page-up", "shift PAGE_DOWN", "selection-page-down", "shift PAGE_UP", "selection-page-up", "meta shift PAGE_DOWN", "selection-page-right", "meta shift PAGE_UP", "selection-page-left"})});
   }

   AquaKeyBindings.LateBoundInputMap getFormattedTextFieldInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{this.getTextFieldInputMap(), new AquaKeyBindings.SimpleBinding(new String[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement", "ESCAPE", "reset-field-edit"})});
   }

   AquaKeyBindings.LateBoundInputMap getComboBoxInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"ESCAPE", "aquaHidePopup", "PAGE_UP", "aquaSelectPageUp", "PAGE_DOWN", "aquaSelectPageDown", "HOME", "aquaSelectHome", "END", "aquaSelectEnd", "ENTER", "enterPressed", "UP", "aquaSelectPrevious", "KP_UP", "aquaSelectPrevious", "DOWN", "aquaSelectNext", "KP_DOWN", "aquaSelectNext", "SPACE", "aquaSpacePressed"})});
   }

   AquaKeyBindings.LateBoundInputMap getListInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"meta C", "copy", "meta V", "paste", "meta X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "meta A", "selectAll", "HOME", "aquaHome", "shift HOME", "selectFirstRowExtendSelection", "END", "aquaEnd", "shift END", "selectLastRowExtendSelection", "shift PAGE_UP", "scrollUpExtendSelection", "shift PAGE_DOWN", "scrollDownExtendSelection"})});
   }

   AquaKeyBindings.LateBoundInputMap getScrollBarInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll"})});
   }

   AquaKeyBindings.LateBoundInputMap getScrollBarRightToLeftInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"})});
   }

   AquaKeyBindings.LateBoundInputMap getScrollPaneInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "HOME", "scrollHome", "END", "scrollEnd"})});
   }

   AquaKeyBindings.LateBoundInputMap getSliderInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll"})});
   }

   AquaKeyBindings.LateBoundInputMap getSliderRightToLeftInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement"})});
   }

   AquaKeyBindings.LateBoundInputMap getSpinnerInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement"})});
   }

   AquaKeyBindings.LateBoundInputMap getTableInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"meta C", "copy", "meta V", "paste", "meta X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "PAGE_UP", "scrollUpChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "HOME", "selectFirstColumn", "END", "selectLastColumn", "shift PAGE_UP", "scrollUpExtendSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "shift HOME", "selectFirstColumnExtendSelection", "shift END", "selectLastColumnExtendSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "meta A", "selectAll", "ESCAPE", "cancel", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "alt TAB", "focusHeader", "alt shift TAB", "focusHeader"})});
   }

   AquaKeyBindings.LateBoundInputMap getTableRightToLeftInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection"})});
   }

   AquaKeyBindings.LateBoundInputMap getTreeInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"meta C", "copy", "meta V", "paste", "meta X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "RIGHT", "aquaExpandNode", "KP_RIGHT", "aquaExpandNode", "LEFT", "aquaCollapseNode", "KP_LEFT", "aquaCollapseNode", "shift RIGHT", "aquaExpandNode", "shift KP_RIGHT", "aquaExpandNode", "shift LEFT", "aquaCollapseNode", "shift KP_LEFT", "aquaCollapseNode", "ctrl LEFT", "aquaCollapseNode", "ctrl KP_LEFT", "aquaCollapseNode", "ctrl RIGHT", "aquaExpandNode", "ctrl KP_RIGHT", "aquaExpandNode", "alt RIGHT", "aquaFullyExpandNode", "alt KP_RIGHT", "aquaFullyExpandNode", "alt LEFT", "aquaFullyCollapseNode", "alt KP_LEFT", "aquaFullyCollapseNode", "meta A", "selectAll", "RETURN", "startEditing"})});
   }

   AquaKeyBindings.LateBoundInputMap getTreeRightToLeftInputMap() {
      return new AquaKeyBindings.LateBoundInputMap(new AquaKeyBindings.BindingsProvider[]{new AquaKeyBindings.SimpleBinding(new String[]{"RIGHT", "aquaCollapseNode", "KP_RIGHT", "aquaCollapseNode", "LEFT", "aquaExpandNode", "KP_LEFT", "aquaExpandNode", "shift RIGHT", "aquaCollapseNode", "shift KP_RIGHT", "aquaCollapseNode", "shift LEFT", "aquaExpandNode", "shift KP_LEFT", "aquaExpandNode", "ctrl LEFT", "aquaExpandNode", "ctrl KP_LEFT", "aquaExpandNode", "ctrl RIGHT", "aquaCollapseNode", "ctrl KP_RIGHT", "aquaCollapseNode"})});
   }

   void installAquaUpDownActions(JTextComponent var1) {
      ActionMap var2 = var1.getActionMap();
      var2.put("aqua-move-up", this.moveUpMultilineAction);
      var2.put("aqua-move-down", this.moveDownMultilineAction);
      var2.put("aqua-page-up", this.pageUpMultilineAction);
      var2.put("aqua-page-down", this.pageDownMultilineAction);
   }

   static class AquaMultilineAction extends TextAction {
      final String targetActionName;
      final String proxyActionName;

      public AquaMultilineAction(String var1, String var2, String var3) {
         super(var1);
         this.targetActionName = var2;
         this.proxyActionName = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         ActionMap var3 = var2.getActionMap();
         Action var4 = var3.get(this.targetActionName);
         int var5 = var2.getCaretPosition();
         var4.actionPerformed(var1);
         if (var5 == var2.getCaretPosition()) {
            Action var6 = var3.get(this.proxyActionName);
            var6.actionPerformed(var1);
         }
      }
   }

   abstract static class DeleteWordAction extends TextAction {
      public DeleteWordAction(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         if (var1 != null) {
            JTextComponent var2 = this.getTextComponent(var1);
            if (var2 != null) {
               if (var2.isEditable() && var2.isEnabled()) {
                  try {
                     int var3 = var2.getSelectionStart();
                     Element var4 = Utilities.getParagraphElement(var2, var3);
                     int var5 = this.getEnd(var2, var4, var3);
                     int var6 = Math.min(var3, var5);
                     int var7 = Math.abs(var5 - var3);
                     if (var6 >= 0) {
                        var2.getDocument().remove(var6, var7);
                        return;
                     }
                  } catch (BadLocationException var8) {
                  }

                  UIManager.getLookAndFeel().provideErrorFeedback(var2);
               } else {
                  UIManager.getLookAndFeel().provideErrorFeedback(var2);
               }
            }
         }
      }

      abstract int getEnd(JTextComponent var1, Element var2, int var3) throws BadLocationException;
   }

   static class LateBoundInputMap implements UIDefaults.LazyValue, AquaKeyBindings.BindingsProvider {
      private final AquaKeyBindings.BindingsProvider[] providerList;
      private String[] mergedBindings;

      public LateBoundInputMap(AquaKeyBindings.BindingsProvider... var1) {
         this.providerList = var1;
      }

      public Object createValue(UIDefaults var1) {
         return LookAndFeel.makeInputMap(this.getBindings());
      }

      public String[] getBindings() {
         if (this.mergedBindings != null) {
            return this.mergedBindings;
         } else {
            String[][] var1 = new String[this.providerList.length][];
            int var2 = 0;

            for(int var3 = 0; var3 < this.providerList.length; ++var3) {
               var1[var3] = this.providerList[var3].getBindings();
               var2 += var1[var3].length;
            }

            if (var1.length == 1) {
               return this.mergedBindings = var1[0];
            } else {
               ArrayList var5 = new ArrayList(var2);
               Collections.addAll(var5, var1[0]);

               for(int var4 = 1; var4 < this.providerList.length; ++var4) {
                  mergeBindings(var5, var1[var4]);
               }

               return this.mergedBindings = (String[])var5.toArray(new String[var5.size()]);
            }
         }
      }

      static void mergeBindings(ArrayList<String> var0, String[] var1) {
         for(int var2 = 0; var2 < var1.length; var2 += 2) {
            String var3 = var1[var2];
            String var4 = var1[var2 + 1];
            int var5 = var0.indexOf(var3);
            if (var5 == -1) {
               var0.add(var3);
               var0.add(var4);
            } else {
               var0.set(var5, var3);
               var0.set(var5 + 1, var4);
            }
         }

      }
   }

   static class SimpleBinding implements AquaKeyBindings.BindingsProvider {
      final String[] bindings;

      public SimpleBinding(String[] var1) {
         this.bindings = var1;
      }

      public String[] getBindings() {
         return this.bindings;
      }
   }

   interface BindingsProvider {
      String[] getBindings();
   }
}
