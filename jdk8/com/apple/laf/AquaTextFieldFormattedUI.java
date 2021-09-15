package com.apple.laf;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;

public class AquaTextFieldFormattedUI extends AquaTextFieldUI implements MouseListener {
   public static ComponentUI createUI(JComponent var0) {
      return new AquaTextFieldFormattedUI();
   }

   protected String getPropertyPrefix() {
      return "FormattedTextField";
   }

   protected void installListeners() {
      super.installListeners();
      this.getComponent().addMouseListener(this);
   }

   protected void uninstallListeners() {
      this.getComponent().removeMouseListener(this);
      super.uninstallListeners();
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 1) {
         JTextComponent var2 = this.getComponent();
         var2.setCaretPosition(this.viewToModel(var2, var1.getPoint()));
      }
   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
   }

   public void mouseReleased(MouseEvent var1) {
   }
}
