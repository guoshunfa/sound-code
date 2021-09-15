package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

class CommentView extends HiddenTagView {
   static final Border CBorder = new CommentView.CommentBorder();
   static final int commentPadding = 3;
   static final int commentPaddingD = 9;

   CommentView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      Container var1 = this.getContainer();
      if (var1 != null && !((JTextComponent)var1).isEditable()) {
         return null;
      } else {
         JTextArea var2 = new JTextArea(this.getRepresentedText());
         Document var3 = this.getDocument();
         Font var4;
         if (var3 instanceof StyledDocument) {
            var4 = ((StyledDocument)var3).getFont(this.getAttributes());
            var2.setFont(var4);
         } else {
            var4 = var2.getFont();
         }

         this.updateYAlign(var4);
         var2.setBorder(CBorder);
         var2.getDocument().addDocumentListener(this);
         var2.setFocusable(this.isVisible());
         return var2;
      }
   }

   void resetBorder() {
   }

   void _updateModelFromText() {
      JTextComponent var1 = this.getTextComponent();
      Document var2 = this.getDocument();
      if (var1 != null && var2 != null) {
         String var3 = var1.getText();
         SimpleAttributeSet var4 = new SimpleAttributeSet();
         this.isSettingAttributes = true;

         try {
            var4.addAttribute(HTML.Attribute.COMMENT, var3);
            ((StyledDocument)var2).setCharacterAttributes(this.getStartOffset(), this.getEndOffset() - this.getStartOffset(), var4, false);
         } finally {
            this.isSettingAttributes = false;
         }
      }

   }

   JTextComponent getTextComponent() {
      return (JTextComponent)this.getComponent();
   }

   String getRepresentedText() {
      AttributeSet var1 = this.getElement().getAttributes();
      if (var1 != null) {
         Object var2 = var1.getAttribute(HTML.Attribute.COMMENT);
         if (var2 instanceof String) {
            return (String)var2;
         }
      }

      return "";
   }

   static class CommentBorder extends LineBorder {
      CommentBorder() {
         super(Color.black, 1);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         super.paintBorder(var1, var2, var3 + 3, var4, var5 - 9, var6);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = super.getBorderInsets(var1, var2);
         var3.left += 3;
         var3.right += 3;
         return var3;
      }

      public boolean isBorderOpaque() {
         return false;
      }
   }
}
