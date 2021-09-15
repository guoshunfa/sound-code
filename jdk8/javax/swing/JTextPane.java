package javax.swing;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class JTextPane extends JEditorPane {
   private static final String uiClassID = "TextPaneUI";

   public JTextPane() {
      EditorKit var1 = this.createDefaultEditorKit();
      String var2 = var1.getContentType();
      if (var2 != null && getEditorKitClassNameForContentType(var2) == defaultEditorKitMap.get(var2)) {
         this.setEditorKitForContentType(var2, var1);
      }

      this.setEditorKit(var1);
   }

   public JTextPane(StyledDocument var1) {
      this();
      this.setStyledDocument(var1);
   }

   public String getUIClassID() {
      return "TextPaneUI";
   }

   public void setDocument(Document var1) {
      if (var1 instanceof StyledDocument) {
         super.setDocument(var1);
      } else {
         throw new IllegalArgumentException("Model must be StyledDocument");
      }
   }

   public void setStyledDocument(StyledDocument var1) {
      super.setDocument(var1);
   }

   public StyledDocument getStyledDocument() {
      return (StyledDocument)this.getDocument();
   }

   public void replaceSelection(String var1) {
      this.replaceSelection(var1, true);
   }

   private void replaceSelection(String var1, boolean var2) {
      if (var2 && !this.isEditable()) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      } else {
         StyledDocument var3 = this.getStyledDocument();
         if (var3 != null) {
            try {
               Caret var4 = this.getCaret();
               boolean var5 = this.saveComposedText(var4.getDot());
               int var6 = Math.min(var4.getDot(), var4.getMark());
               int var7 = Math.max(var4.getDot(), var4.getMark());
               AttributeSet var8 = this.getInputAttributes().copyAttributes();
               if (var3 instanceof AbstractDocument) {
                  ((AbstractDocument)var3).replace(var6, var7 - var6, var1, var8);
               } else {
                  if (var6 != var7) {
                     var3.remove(var6, var7 - var6);
                  }

                  if (var1 != null && var1.length() > 0) {
                     var3.insertString(var6, var1, var8);
                  }
               }

               if (var5) {
                  this.restoreComposedText();
               }
            } catch (BadLocationException var9) {
               UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
         }

      }
   }

   public void insertComponent(Component var1) {
      MutableAttributeSet var2 = this.getInputAttributes();
      var2.removeAttributes((AttributeSet)var2);
      StyleConstants.setComponent(var2, var1);
      this.replaceSelection(" ", false);
      var2.removeAttributes((AttributeSet)var2);
   }

   public void insertIcon(Icon var1) {
      MutableAttributeSet var2 = this.getInputAttributes();
      var2.removeAttributes((AttributeSet)var2);
      StyleConstants.setIcon(var2, var1);
      this.replaceSelection(" ", false);
      var2.removeAttributes((AttributeSet)var2);
   }

   public Style addStyle(String var1, Style var2) {
      StyledDocument var3 = this.getStyledDocument();
      return var3.addStyle(var1, var2);
   }

   public void removeStyle(String var1) {
      StyledDocument var2 = this.getStyledDocument();
      var2.removeStyle(var1);
   }

   public Style getStyle(String var1) {
      StyledDocument var2 = this.getStyledDocument();
      return var2.getStyle(var1);
   }

   public void setLogicalStyle(Style var1) {
      StyledDocument var2 = this.getStyledDocument();
      var2.setLogicalStyle(this.getCaretPosition(), var1);
   }

   public Style getLogicalStyle() {
      StyledDocument var1 = this.getStyledDocument();
      return var1.getLogicalStyle(this.getCaretPosition());
   }

   public AttributeSet getCharacterAttributes() {
      StyledDocument var1 = this.getStyledDocument();
      Element var2 = var1.getCharacterElement(this.getCaretPosition());
      return var2 != null ? var2.getAttributes() : null;
   }

   public void setCharacterAttributes(AttributeSet var1, boolean var2) {
      int var3 = this.getSelectionStart();
      int var4 = this.getSelectionEnd();
      if (var3 != var4) {
         StyledDocument var5 = this.getStyledDocument();
         var5.setCharacterAttributes(var3, var4 - var3, var1, var2);
      } else {
         MutableAttributeSet var6 = this.getInputAttributes();
         if (var2) {
            var6.removeAttributes((AttributeSet)var6);
         }

         var6.addAttributes(var1);
      }

   }

   public AttributeSet getParagraphAttributes() {
      StyledDocument var1 = this.getStyledDocument();
      Element var2 = var1.getParagraphElement(this.getCaretPosition());
      return var2 != null ? var2.getAttributes() : null;
   }

   public void setParagraphAttributes(AttributeSet var1, boolean var2) {
      int var3 = this.getSelectionStart();
      int var4 = this.getSelectionEnd();
      StyledDocument var5 = this.getStyledDocument();
      var5.setParagraphAttributes(var3, var4 - var3, var1, var2);
   }

   public MutableAttributeSet getInputAttributes() {
      return this.getStyledEditorKit().getInputAttributes();
   }

   protected final StyledEditorKit getStyledEditorKit() {
      return (StyledEditorKit)this.getEditorKit();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("TextPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected EditorKit createDefaultEditorKit() {
      return new StyledEditorKit();
   }

   public final void setEditorKit(EditorKit var1) {
      if (var1 instanceof StyledEditorKit) {
         super.setEditorKit(var1);
      } else {
         throw new IllegalArgumentException("Must be StyledEditorKit");
      }
   }

   protected String paramString() {
      return super.paramString();
   }
}
