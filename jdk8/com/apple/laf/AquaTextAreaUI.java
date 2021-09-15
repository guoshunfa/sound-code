package com.apple.laf;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class AquaTextAreaUI extends BasicTextAreaUI {
   AquaFocusHandler handler;
   boolean oldDragState = false;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTextAreaUI();
   }

   protected void installListeners() {
      super.installListeners();
      JTextComponent var1 = this.getComponent();
      this.handler = new AquaFocusHandler();
      var1.addFocusListener(this.handler);
      var1.addPropertyChangeListener(this.handler);
      AquaUtilControlSize.addSizePropertyListener(var1);
   }

   protected void uninstallListeners() {
      JTextComponent var1 = this.getComponent();
      AquaUtilControlSize.removeSizePropertyListener(var1);
      var1.removeFocusListener(this.handler);
      var1.removePropertyChangeListener(this.handler);
      this.handler = null;
      super.uninstallListeners();
   }

   protected void installDefaults() {
      if (!GraphicsEnvironment.isHeadless()) {
         this.oldDragState = this.getComponent().getDragEnabled();
         this.getComponent().setDragEnabled(true);
      }

      super.installDefaults();
   }

   protected void uninstallDefaults() {
      if (!GraphicsEnvironment.isHeadless()) {
         this.getComponent().setDragEnabled(this.oldDragState);
      }

      super.uninstallDefaults();
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      AquaKeyBindings var1 = AquaKeyBindings.instance();
      var1.setDefaultAction(this.getKeymapName());
      JTextComponent var2 = this.getComponent();
      var1.installAquaUpDownActions(var2);
   }

   protected Caret createCaret() {
      JTextComponent var1 = this.getComponent();
      Window var2 = SwingUtilities.getWindowAncestor(var1);
      AquaCaret var3 = new AquaCaret(var2, var1);
      return var3;
   }

   protected Highlighter createHighlighter() {
      return new AquaHighlighter();
   }
}
