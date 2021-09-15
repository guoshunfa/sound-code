package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.ViewFactory;

class HiddenTagView extends EditableView implements DocumentListener {
   float yAlign = 1.0F;
   boolean isSettingAttributes;
   static final int circleR = 3;
   static final int circleD = 6;
   static final int tagSize = 6;
   static final int padding = 3;
   static final Color UnknownTagBorderColor;
   static final Border StartBorder;
   static final Border EndBorder;

   HiddenTagView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      JTextField var1 = new JTextField(this.getElement().getName());
      Document var2 = this.getDocument();
      Font var3;
      if (var2 instanceof StyledDocument) {
         var3 = ((StyledDocument)var2).getFont(this.getAttributes());
         var1.setFont(var3);
      } else {
         var3 = var1.getFont();
      }

      var1.getDocument().addDocumentListener(this);
      this.updateYAlign(var3);
      JPanel var4 = new JPanel(new BorderLayout());
      var4.setBackground((Color)null);
      if (this.isEndTag()) {
         var4.setBorder(EndBorder);
      } else {
         var4.setBorder(StartBorder);
      }

      var4.add(var1);
      return var4;
   }

   public float getAlignment(int var1) {
      return var1 == 1 ? this.yAlign : 0.5F;
   }

   public float getMinimumSpan(int var1) {
      return var1 == 0 && this.isVisible() ? Math.max(30.0F, super.getPreferredSpan(var1)) : super.getMinimumSpan(var1);
   }

   public float getPreferredSpan(int var1) {
      return var1 == 0 && this.isVisible() ? Math.max(30.0F, super.getPreferredSpan(var1)) : super.getPreferredSpan(var1);
   }

   public float getMaximumSpan(int var1) {
      return var1 == 0 && this.isVisible() ? Math.max(30.0F, super.getMaximumSpan(var1)) : super.getMaximumSpan(var1);
   }

   public void insertUpdate(DocumentEvent var1) {
      this.updateModelFromText();
   }

   public void removeUpdate(DocumentEvent var1) {
      this.updateModelFromText();
   }

   public void changedUpdate(DocumentEvent var1) {
      this.updateModelFromText();
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      if (!this.isSettingAttributes) {
         this.setTextFromModel();
      }

   }

   void updateYAlign(Font var1) {
      Container var2 = this.getContainer();
      FontMetrics var3 = var2 != null ? var2.getFontMetrics(var1) : Toolkit.getDefaultToolkit().getFontMetrics(var1);
      float var4 = (float)var3.getHeight();
      float var5 = (float)var3.getDescent();
      this.yAlign = var4 > 0.0F ? (var4 - var5) / var4 : 0.0F;
   }

   void resetBorder() {
      Component var1 = this.getComponent();
      if (var1 != null) {
         if (this.isEndTag()) {
            ((JPanel)var1).setBorder(EndBorder);
         } else {
            ((JPanel)var1).setBorder(StartBorder);
         }
      }

   }

   void setTextFromModel() {
      if (SwingUtilities.isEventDispatchThread()) {
         this._setTextFromModel();
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               HiddenTagView.this._setTextFromModel();
            }
         });
      }

   }

   void _setTextFromModel() {
      Document var1 = this.getDocument();

      try {
         this.isSettingAttributes = true;
         if (var1 instanceof AbstractDocument) {
            ((AbstractDocument)var1).readLock();
         }

         JTextComponent var2 = this.getTextComponent();
         if (var2 != null) {
            var2.setText(this.getRepresentedText());
            this.resetBorder();
            Container var3 = this.getContainer();
            if (var3 != null) {
               this.preferenceChanged(this, true, true);
               var3.repaint();
            }
         }
      } finally {
         this.isSettingAttributes = false;
         if (var1 instanceof AbstractDocument) {
            ((AbstractDocument)var1).readUnlock();
         }

      }

   }

   void updateModelFromText() {
      if (!this.isSettingAttributes) {
         if (SwingUtilities.isEventDispatchThread()) {
            this._updateModelFromText();
         } else {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  HiddenTagView.this._updateModelFromText();
               }
            });
         }
      }

   }

   void _updateModelFromText() {
      Document var1 = this.getDocument();
      Object var2 = this.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (var2 instanceof HTML.UnknownTag && var1 instanceof StyledDocument) {
         SimpleAttributeSet var3 = new SimpleAttributeSet();
         JTextComponent var4 = this.getTextComponent();
         if (var4 != null) {
            String var5 = var4.getText();
            this.isSettingAttributes = true;

            try {
               var3.addAttribute(StyleConstants.NameAttribute, new HTML.UnknownTag(var5));
               ((StyledDocument)var1).setCharacterAttributes(this.getStartOffset(), this.getEndOffset() - this.getStartOffset(), var3, false);
            } finally {
               this.isSettingAttributes = false;
            }
         }
      }

   }

   JTextComponent getTextComponent() {
      Component var1 = this.getComponent();
      return var1 == null ? null : (JTextComponent)((Container)var1).getComponent(0);
   }

   String getRepresentedText() {
      String var1 = this.getElement().getName();
      return var1 == null ? "" : var1;
   }

   boolean isEndTag() {
      AttributeSet var1 = this.getElement().getAttributes();
      if (var1 != null) {
         Object var2 = var1.getAttribute(HTML.Attribute.ENDTAG);
         if (var2 != null && var2 instanceof String && ((String)var2).equals("true")) {
            return true;
         }
      }

      return false;
   }

   static {
      UnknownTagBorderColor = Color.black;
      StartBorder = new HiddenTagView.StartTagBorder();
      EndBorder = new HiddenTagView.EndTagBorder();
   }

   static class EndTagBorder implements Border, Serializable {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.setColor(HiddenTagView.UnknownTagBorderColor);
         var3 += 3;
         var5 -= 6;
         var2.drawLine(var3 + var5 - 1, var4 + 3, var3 + var5 - 1, var4 + var6 - 3);
         var2.drawArc(var3 + var5 - 6 - 1, var4 + var6 - 6 - 1, 6, 6, 270, 90);
         var2.drawArc(var3 + var5 - 6 - 1, var4, 6, 6, 0, 90);
         var2.drawLine(var3 + 6, var4, var3 + var5 - 3, var4);
         var2.drawLine(var3 + 6, var4 + var6 - 1, var3 + var5 - 3, var4 + var6 - 1);
         var2.drawLine(var3 + 6, var4, var3, var4 + var6 / 2);
         var2.drawLine(var3 + 6, var4 + var6, var3, var4 + var6 / 2);
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(2, 11, 2, 5);
      }

      public boolean isBorderOpaque() {
         return false;
      }
   }

   static class StartTagBorder implements Border, Serializable {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.setColor(HiddenTagView.UnknownTagBorderColor);
         var3 += 3;
         var5 -= 6;
         var2.drawLine(var3, var4 + 3, var3, var4 + var6 - 3);
         var2.drawArc(var3, var4 + var6 - 6 - 1, 6, 6, 180, 90);
         var2.drawArc(var3, var4, 6, 6, 90, 90);
         var2.drawLine(var3 + 3, var4, var3 + var5 - 6, var4);
         var2.drawLine(var3 + 3, var4 + var6 - 1, var3 + var5 - 6, var4 + var6 - 1);
         var2.drawLine(var3 + var5 - 6, var4, var3 + var5 - 1, var4 + var6 / 2);
         var2.drawLine(var3 + var5 - 6, var4 + var6, var3 + var5 - 1, var4 + var6 / 2);
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(2, 5, 2, 11);
      }

      public boolean isBorderOpaque() {
         return false;
      }
   }
}
