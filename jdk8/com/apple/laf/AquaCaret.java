package com.apple.laf;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class AquaCaret extends DefaultCaret implements UIResource, PropertyChangeListener {
   final boolean isMultiLineEditor;
   final JTextComponent c;
   boolean mFocused = false;
   private boolean shouldSelectAllOnFocus = true;
   boolean fPainting = false;

   public AquaCaret(Window var1, JTextComponent var2) {
      this.c = var2;
      this.isMultiLineEditor = this.c instanceof JTextArea || this.c instanceof JEditorPane;
      var2.addPropertyChangeListener(this);
   }

   protected Highlighter.HighlightPainter getSelectionPainter() {
      return AquaHighlighter.getInstance();
   }

   public void setVisible(boolean var1) {
      if (var1) {
         var1 = this.getDot() == this.getMark();
      }

      super.setVisible(var1);
   }

   protected void fireStateChanged() {
      if (this.mFocused) {
         this.setVisible(this.getComponent().isEditable());
      }

      super.fireStateChanged();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if ("Frame.active".equals(var2)) {
         JTextComponent var3 = (JTextComponent)var1.getSource();
         if (var1.getNewValue() == Boolean.TRUE) {
            this.setVisible(var3.hasFocus());
         } else {
            this.setVisible(false);
         }

         if (this.getDot() != this.getMark()) {
            var3.getUI().damageRange(var3, this.getDot(), this.getMark());
         }
      }

   }

   public void focusGained(FocusEvent var1) {
      JTextComponent var2 = this.getComponent();
      if (var2.isEnabled() && var2.isEditable()) {
         this.mFocused = true;
         if (!this.shouldSelectAllOnFocus) {
            this.shouldSelectAllOnFocus = true;
            super.focusGained(var1);
         } else if (this.isMultiLineEditor) {
            super.focusGained(var1);
         } else {
            int var3 = var2.getDocument().getLength();
            int var4 = this.getDot();
            int var5 = this.getMark();
            if (var4 == var5) {
               if (var4 == 0) {
                  var2.setCaretPosition(var3);
                  var2.moveCaretPosition(0);
               } else if (var4 == var3) {
                  var2.setCaretPosition(0);
                  var2.moveCaretPosition(var3);
               }
            }

            super.focusGained(var1);
         }
      } else {
         super.focusGained(var1);
      }
   }

   public void focusLost(FocusEvent var1) {
      this.mFocused = false;
      this.shouldSelectAllOnFocus = true;
      if (this.isMultiLineEditor) {
         this.setVisible(false);
         this.c.repaint();
      } else {
         super.focusLost(var1);
      }

   }

   public void mousePressed(MouseEvent var1) {
      if (!var1.isPopupTrigger()) {
         super.mousePressed(var1);
         this.shouldSelectAllOnFocus = false;
      }

   }

   protected synchronized void damage(Rectangle var1) {
      if (var1 != null && !this.fPainting) {
         this.x = var1.x - 4;
         this.y = var1.y;
         this.width = 10;
         this.height = var1.height;
         Rectangle var2 = new Rectangle(this.x, this.y, this.width, this.height);
         Border var3 = this.getComponent().getBorder();
         if (var3 != null) {
            Rectangle var4 = this.getComponent().getBounds();
            var4.x = var4.y = 0;
            Insets var5 = var3.getBorderInsets(this.getComponent());
            var4.x += var5.left;
            var4.y += var5.top;
            var4.width -= var5.left + var5.right;
            var4.height -= var5.top + var5.bottom;
            Rectangle2D.intersect(var2, var4, var2);
         }

         this.x = var2.x;
         this.y = var2.y;
         this.width = Math.max(var2.width, 1);
         this.height = Math.max(var2.height, 1);
         this.repaint();
      }
   }

   public void paint(Graphics var1) {
      if (this.isVisible()) {
         this.fPainting = true;
         super.paint(var1);
         this.fPainting = false;
      }

   }
}
