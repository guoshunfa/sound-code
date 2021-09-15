package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.Position;

public class WindowsTextFieldUI extends BasicTextFieldUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTextFieldUI();
   }

   protected void paintBackground(Graphics var1) {
      super.paintBackground(var1);
   }

   protected Caret createCaret() {
      return new WindowsTextFieldUI.WindowsFieldCaret();
   }

   static class WindowsFieldCaret extends DefaultCaret implements UIResource {
      public WindowsFieldCaret() {
      }

      protected void adjustVisibility(Rectangle var1) {
         SwingUtilities.invokeLater(new WindowsTextFieldUI.WindowsFieldCaret.SafeScroller(var1));
      }

      protected Highlighter.HighlightPainter getSelectionPainter() {
         return WindowsTextUI.WindowsPainter;
      }

      private class SafeScroller implements Runnable {
         private Rectangle r;

         SafeScroller(Rectangle var2) {
            this.r = var2;
         }

         public void run() {
            JTextField var1 = (JTextField)WindowsFieldCaret.this.getComponent();
            if (var1 != null) {
               TextUI var2 = var1.getUI();
               int var3 = WindowsFieldCaret.this.getDot();
               Position.Bias var4 = Position.Bias.Forward;
               Rectangle var5 = null;

               try {
                  var5 = var2.modelToView(var1, var3, var4);
               } catch (BadLocationException var12) {
               }

               Insets var6 = var1.getInsets();
               BoundedRangeModel var7 = var1.getHorizontalVisibility();
               int var8 = this.r.x + var7.getValue() - var6.left;
               int var9 = var7.getExtent() / 4;
               if (this.r.x < var6.left) {
                  var7.setValue(var8 - var9);
               } else if (this.r.x + this.r.width > var6.left + var7.getExtent()) {
                  var7.setValue(var8 - 3 * var9);
               }

               if (var5 != null) {
                  try {
                     Rectangle var10 = var2.modelToView(var1, var3, var4);
                     if (var10 != null && !var10.equals(var5)) {
                        WindowsFieldCaret.this.damage(var10);
                     }
                  } catch (BadLocationException var11) {
                  }
               }
            }

         }
      }
   }
}
