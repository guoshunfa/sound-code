package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

public abstract class AquaBorder implements Border, UIResource {
   protected final AquaPainter<? extends JRSUIState> painter;
   protected final AquaUtilControlSize.SizeDescriptor sizeDescriptor;
   protected AquaUtilControlSize.SizeVariant sizeVariant;

   protected AquaBorder(AquaUtilControlSize.SizeDescriptor var1) {
      this.sizeDescriptor = var1;
      this.sizeVariant = var1.get(JRSUIConstants.Size.REGULAR);
      this.painter = this.createPainter();
   }

   protected AquaPainter<? extends JRSUIState> createPainter() {
      AquaPainter var1 = AquaPainter.create(JRSUIState.getInstance());
      var1.state.set(JRSUIConstants.AlignmentVertical.CENTER);
      var1.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
      return var1;
   }

   protected AquaBorder(AquaBorder var1) {
      this.sizeDescriptor = var1.sizeDescriptor;
      this.sizeVariant = var1.sizeVariant;
      this.painter = AquaPainter.create(var1.painter.state.derive());
      this.painter.state.set(JRSUIConstants.AlignmentVertical.CENTER);
      this.painter.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
   }

   protected void setSize(JRSUIConstants.Size var1) {
      this.sizeVariant = this.sizeDescriptor.get(var1);
      this.painter.state.set(var1);
   }

   public Insets getBorderInsets(Component var1) {
      return (Insets)this.sizeVariant.margins.clone();
   }

   protected AquaBorder deriveBorderForSize(JRSUIConstants.Size var1) {
      try {
         Class var2 = this.getClass();
         AquaBorder var3 = (AquaBorder)var2.getConstructor(var2).newInstance(this);
         var3.setSize(var1);
         return var3;
      } catch (Throwable var4) {
         return null;
      }
   }

   public static void repaintBorder(JComponent var0) {
      JComponent var1 = var0;
      Border var2 = var0.getBorder();
      if (var2 == null) {
         Container var3 = var0.getParent();
         if (var3 instanceof JViewport) {
            var1 = (JComponent)var3.getParent();
            if (var1 != null) {
               var2 = var1.getBorder();
            }
         }
      }

      if (var2 != null && var1 != null) {
         int var6 = var1.getWidth();
         int var4 = var1.getHeight();
         Insets var5 = var1.getInsets();
         var1.repaint(0, 0, var6, var5.top);
         var1.repaint(0, 0, var5.left, var4);
         var1.repaint(0, var4 - var5.bottom, var6, var5.bottom);
         var1.repaint(var6 - var5.right, 0, var5.right, var4);
      }
   }

   protected boolean isFocused(Component var1) {
      Component var2 = var1;
      if (var1 instanceof JScrollPane) {
         JViewport var3 = ((JScrollPane)var1).getViewport();
         if (var3 != null) {
            var2 = var3.getView();
            if (var2 instanceof JTextComponent) {
               return false;
            }
         }
      } else if (var1 instanceof JTextComponent && !((JTextComponent)var1).isEditable()) {
         return false;
      }

      return var2 != null && var2 instanceof JComponent && ((JComponent)var2).hasFocus();
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.painter.paint(var2, var1, var3, var4, var5, var6);
   }

   static class Default extends AquaBorder {
      Default() {
         super(new AquaUtilControlSize.SizeDescriptor(new AquaUtilControlSize.SizeVariant()));
      }
   }
}
