package com.apple.laf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Window;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.View;

public class AquaHighlighter extends DefaultHighlighter implements UIResource {
   static final AquaUtils.RecyclableSingleton<LayeredHighlighter.LayerPainter> instance = new AquaUtils.RecyclableSingleton<LayeredHighlighter.LayerPainter>() {
      protected LayeredHighlighter.LayerPainter getInstance() {
         return new AquaHighlighter.AquaHighlightPainter((Color)null);
      }
   };

   protected static LayeredHighlighter.LayerPainter getInstance() {
      return (LayeredHighlighter.LayerPainter)instance.get();
   }

   public static class AquaHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
      Color selectionColor;
      Color disabledSelectionColor;

      public AquaHighlightPainter(Color var1) {
         super(var1);
      }

      public Color getColor() {
         return this.selectionColor == null ? super.getColor() : this.selectionColor;
      }

      protected Color getInactiveSelectionColor() {
         return this.disabledSelectionColor != null ? this.disabledSelectionColor : (this.disabledSelectionColor = UIManager.getColor("TextComponent.selectionBackgroundInactive"));
      }

      void setColor(JTextComponent var1) {
         this.selectionColor = super.getColor();
         if (this.selectionColor == null) {
            this.selectionColor = var1.getSelectionColor();
         }

         Window var2 = SwingUtilities.getWindowAncestor(var1);
         if (var2 != null && !var2.isActive()) {
            this.selectionColor = this.getInactiveSelectionColor();
         }

         if (!var1.hasFocus()) {
            this.selectionColor = this.getInactiveSelectionColor();
         }

      }

      public void paint(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5) {
         this.setColor(var5);
         super.paint(var1, var2, var3, var4, var5);
      }

      public Shape paintLayer(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
         this.setColor(var5);
         return super.paintLayer(var1, var2, var3, var4, var5, var6);
      }
   }
}
