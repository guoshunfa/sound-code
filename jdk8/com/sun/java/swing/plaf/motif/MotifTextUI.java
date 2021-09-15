package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import javax.swing.KeyStroke;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class MotifTextUI {
   static final JTextComponent.KeyBinding[] defaultBindings = new JTextComponent.KeyBinding[]{new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 2), "copy-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 1), "paste-from-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(127, 1), "cut-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(37, 1), "selection-backward"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(39, 1), "selection-forward")};

   public static Caret createCaret() {
      return new MotifTextUI.MotifCaret();
   }

   public static class MotifCaret extends DefaultCaret implements UIResource {
      static final int IBeamOverhang = 2;

      public void focusGained(FocusEvent var1) {
         super.focusGained(var1);
         this.getComponent().repaint();
      }

      public void focusLost(FocusEvent var1) {
         super.focusLost(var1);
         this.getComponent().repaint();
      }

      protected void damage(Rectangle var1) {
         if (var1 != null) {
            this.x = var1.x - 2 - 1;
            this.y = var1.y;
            this.width = var1.width + 4 + 3;
            this.height = var1.height;
            this.repaint();
         }

      }

      public void paint(Graphics var1) {
         if (this.isVisible()) {
            try {
               JTextComponent var2 = this.getComponent();
               Color var3 = var2.hasFocus() ? var2.getCaretColor() : var2.getDisabledTextColor();
               TextUI var4 = var2.getUI();
               int var5 = this.getDot();
               Rectangle var6 = var4.modelToView(var2, var5);
               int var7 = var6.x - 2;
               int var8 = var6.x + 2;
               int var9 = var6.y + 1;
               int var10 = var6.y + var6.height - 2;
               var1.setColor(var3);
               var1.drawLine(var6.x, var9, var6.x, var10);
               var1.drawLine(var7, var9, var8, var9);
               var1.drawLine(var7, var10, var8, var10);
            } catch (BadLocationException var11) {
            }
         }

      }
   }
}
