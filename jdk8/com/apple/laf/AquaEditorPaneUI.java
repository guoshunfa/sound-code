package com.apple.laf;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Caret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class AquaEditorPaneUI extends BasicEditorPaneUI {
   boolean oldDragState = false;
   FocusListener focusListener;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaEditorPaneUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      if (!GraphicsEnvironment.isHeadless()) {
         this.oldDragState = this.getComponent().getDragEnabled();
         this.getComponent().setDragEnabled(true);
      }

   }

   protected void uninstallDefaults() {
      if (!GraphicsEnvironment.isHeadless()) {
         this.getComponent().setDragEnabled(this.oldDragState);
      }

      super.uninstallDefaults();
   }

   protected void installListeners() {
      super.installListeners();
      this.focusListener = this.createFocusListener();
      this.getComponent().addFocusListener(this.focusListener);
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      AquaKeyBindings var1 = AquaKeyBindings.instance();
      var1.setDefaultAction(this.getKeymapName());
      JTextComponent var2 = this.getComponent();
      var1.installAquaUpDownActions(var2);
   }

   protected void uninstallListeners() {
      this.getComponent().removeFocusListener(this.focusListener);
      super.uninstallListeners();
   }

   protected FocusListener createFocusListener() {
      return new AquaFocusHandler();
   }

   protected Caret createCaret() {
      Window var1 = SwingUtilities.getWindowAncestor(this.getComponent());
      AquaCaret var2 = new AquaCaret(var1, this.getComponent());
      return var2;
   }

   protected Highlighter createHighlighter() {
      return new AquaHighlighter();
   }
}
