package com.apple.laf;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class AquaTextFieldUI extends BasicTextFieldUI {
   protected AquaUtils.JComponentPainter delegate;
   protected AquaFocusHandler handler;
   boolean oldDragState = false;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTextFieldUI();
   }

   protected void installListeners() {
      super.installListeners();
      this.handler = new AquaFocusHandler();
      JTextComponent var1 = this.getComponent();
      var1.addFocusListener(this.handler);
      var1.addPropertyChangeListener(this.handler);
      LookAndFeel.installProperty(var1, "opaque", UIManager.getBoolean(this.getPropertyPrefix() + "opaque"));
      AquaUtilControlSize.addSizePropertyListener(var1);
      AquaTextFieldSearch.installSearchFieldListener(var1);
   }

   protected void uninstallListeners() {
      JTextComponent var1 = this.getComponent();
      AquaTextFieldSearch.uninstallSearchFieldListener(var1);
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
      super.uninstallDefaults();
      if (!GraphicsEnvironment.isHeadless()) {
         this.getComponent().setDragEnabled(this.oldDragState);
      }

   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      AquaKeyBindings.instance().setDefaultAction(this.getKeymapName());
   }

   protected Rectangle getVisibleEditorRect() {
      Rectangle var1 = super.getVisibleEditorRect();
      if (var1 == null) {
         return null;
      } else {
         if (!this.getComponent().isOpaque()) {
            var1.y -= 3;
            var1.height += 6;
         }

         return var1;
      }
   }

   protected void paintSafely(Graphics var1) {
      this.paintBackgroundSafely(var1);
      super.paintSafely(var1);
   }

   protected void paintBackgroundSafely(Graphics var1) {
      JTextComponent var2 = this.getComponent();
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      if (this.delegate != null) {
         this.delegate.paint(var2, var1, 0, 0, var3, var4);
      } else {
         boolean var5 = var2.isOpaque();
         if (!(var2.getBorder() instanceof AquaTextFieldBorder)) {
            if (var5 || !AquaUtils.hasOpaqueBeenExplicitlySet(var2)) {
               var1.setColor(var2.getBackground());
               var1.fillRect(0, 0, var3, var4);
            }
         } else {
            var1.setColor(var2.getBackground());
            if (var5) {
               var1.fillRect(0, 0, var3, var4);
            } else {
               Insets var6 = var2.getMargin();
               Insets var7 = var2.getInsets();
               if (var7 == null) {
                  var7 = new Insets(0, 0, 0, 0);
               }

               if (var6 != null) {
                  var7.top -= var6.top;
                  var7.left -= var6.left;
                  var7.bottom -= var6.bottom;
                  var7.right -= var6.right;
               }

               int var8 = AquaTextFieldBorder.getShrinkageFor(var2, var4);
               var1.fillRect(var7.left - 2, var7.top - var8 - 1, var3 - var7.right - var7.left + 4, var4 - var7.bottom - var7.top + var8 * 2 + 2);
            }
         }
      }
   }

   protected void paintBackground(Graphics var1) {
   }

   protected Caret createCaret() {
      JTextComponent var1 = this.getComponent();
      Window var2 = SwingUtilities.getWindowAncestor(var1);
      return new AquaCaret(var2, var1);
   }

   protected Highlighter createHighlighter() {
      return new AquaHighlighter();
   }

   protected void setPaintingDelegate(AquaUtils.JComponentPainter var1) {
      this.delegate = var1;
   }
}
