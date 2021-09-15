package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;

public abstract class WindowsTextUI extends BasicTextUI {
   static LayeredHighlighter.LayerPainter WindowsPainter = new WindowsTextUI.WindowsHighlightPainter((Color)null);

   protected Caret createCaret() {
      return new WindowsTextUI.WindowsCaret();
   }

   static class WindowsHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
      WindowsHighlightPainter(Color var1) {
         super(var1);
      }

      public void paint(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5) {
         Rectangle var6 = var4.getBounds();

         try {
            TextUI var7 = var5.getUI();
            Rectangle var8 = var7.modelToView(var5, var2);
            Rectangle var9 = var7.modelToView(var5, var3);
            Color var10 = this.getColor();
            if (var10 == null) {
               var1.setColor(var5.getSelectionColor());
            } else {
               var1.setColor(var10);
            }

            boolean var11 = false;
            boolean var12 = false;
            int var13;
            if (var5.isEditable()) {
               var13 = var5.getCaretPosition();
               var11 = var2 == var13;
               var12 = var3 == var13;
            }

            if (var8.y == var9.y) {
               Rectangle var15 = var8.union(var9);
               if (var15.width > 0) {
                  if (var11) {
                     ++var15.x;
                     --var15.width;
                  } else if (var12) {
                     --var15.width;
                  }
               }

               var1.fillRect(var15.x, var15.y, var15.width, var15.height);
            } else {
               var13 = var6.x + var6.width - var8.x;
               if (var11 && var13 > 0) {
                  ++var8.x;
                  --var13;
               }

               var1.fillRect(var8.x, var8.y, var13, var8.height);
               if (var8.y + var8.height != var9.y) {
                  var1.fillRect(var6.x, var8.y + var8.height, var6.width, var9.y - (var8.y + var8.height));
               }

               if (var12 && var9.x > var6.x) {
                  --var9.x;
               }

               var1.fillRect(var6.x, var9.y, var9.x - var6.x, var9.height);
            }
         } catch (BadLocationException var14) {
         }

      }

      public Shape paintLayer(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
         Color var7 = this.getColor();
         if (var7 == null) {
            var1.setColor(var5.getSelectionColor());
         } else {
            var1.setColor(var7);
         }

         boolean var8 = false;
         boolean var9 = false;
         if (var5.isEditable()) {
            int var10 = var5.getCaretPosition();
            var8 = var2 == var10;
            var9 = var3 == var10;
         }

         if (var2 == var6.getStartOffset() && var3 == var6.getEndOffset()) {
            Rectangle var14;
            if (var4 instanceof Rectangle) {
               var14 = (Rectangle)var4;
            } else {
               var14 = var4.getBounds();
            }

            if (var8 && var14.width > 0) {
               var1.fillRect(var14.x + 1, var14.y, var14.width - 1, var14.height);
            } else if (var9 && var14.width > 0) {
               var1.fillRect(var14.x, var14.y, var14.width - 1, var14.height);
            } else {
               var1.fillRect(var14.x, var14.y, var14.width, var14.height);
            }

            return var14;
         } else {
            try {
               Shape var13 = var6.modelToView(var2, Position.Bias.Forward, var3, Position.Bias.Backward, var4);
               Rectangle var11 = var13 instanceof Rectangle ? (Rectangle)var13 : var13.getBounds();
               if (var8 && var11.width > 0) {
                  var1.fillRect(var11.x + 1, var11.y, var11.width - 1, var11.height);
               } else if (var9 && var11.width > 0) {
                  var1.fillRect(var11.x, var11.y, var11.width - 1, var11.height);
               } else {
                  var1.fillRect(var11.x, var11.y, var11.width, var11.height);
               }

               return var11;
            } catch (BadLocationException var12) {
               return null;
            }
         }
      }
   }

   static class WindowsCaret extends DefaultCaret implements UIResource {
      protected Highlighter.HighlightPainter getSelectionPainter() {
         return WindowsTextUI.WindowsPainter;
      }
   }
}
