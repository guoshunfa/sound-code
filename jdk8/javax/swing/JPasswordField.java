package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

public class JPasswordField extends JTextField {
   private static final String uiClassID = "PasswordFieldUI";
   private char echoChar;
   private boolean echoCharSet;

   public JPasswordField() {
      this((Document)null, (String)null, 0);
   }

   public JPasswordField(String var1) {
      this((Document)null, var1, 0);
   }

   public JPasswordField(int var1) {
      this((Document)null, (String)null, var1);
   }

   public JPasswordField(String var1, int var2) {
      this((Document)null, var1, var2);
   }

   public JPasswordField(Document var1, String var2, int var3) {
      super(var1, var2, var3);
      this.echoCharSet = false;
      this.enableInputMethods(false);
   }

   public String getUIClassID() {
      return "PasswordFieldUI";
   }

   public void updateUI() {
      if (!this.echoCharSet) {
         this.echoChar = '*';
      }

      super.updateUI();
   }

   public char getEchoChar() {
      return this.echoChar;
   }

   public void setEchoChar(char var1) {
      this.echoChar = var1;
      this.echoCharSet = true;
      this.repaint();
      this.revalidate();
   }

   public boolean echoCharIsSet() {
      return this.echoChar != 0;
   }

   public void cut() {
      if (this.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      } else {
         super.cut();
      }

   }

   public void copy() {
      if (this.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      } else {
         super.copy();
      }

   }

   /** @deprecated */
   @Deprecated
   public String getText() {
      return super.getText();
   }

   /** @deprecated */
   @Deprecated
   public String getText(int var1, int var2) throws BadLocationException {
      return super.getText(var1, var2);
   }

   public char[] getPassword() {
      Document var1 = this.getDocument();
      Segment var2 = new Segment();

      try {
         var1.getText(0, var1.getLength(), var2);
      } catch (BadLocationException var4) {
         return null;
      }

      char[] var3 = new char[var2.count];
      System.arraycopy(var2.array, var2.offset, var3, 0, var2.count);
      return var3;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("PasswordFieldUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      return super.paramString() + ",echoChar=" + this.echoChar;
   }

   boolean customSetUIProperty(String var1, Object var2) {
      if (var1 == "echoChar") {
         if (!this.echoCharSet) {
            this.setEchoChar((Character)var2);
            this.echoCharSet = false;
         }

         return true;
      } else {
         return false;
      }
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JPasswordField.AccessibleJPasswordField();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJPasswordField extends JTextField.AccessibleJTextField {
      protected AccessibleJPasswordField() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PASSWORD_TEXT;
      }

      public AccessibleText getAccessibleText() {
         return this;
      }

      private String getEchoString(String var1) {
         if (var1 == null) {
            return null;
         } else {
            char[] var2 = new char[var1.length()];
            Arrays.fill(var2, JPasswordField.this.getEchoChar());
            return new String(var2);
         }
      }

      public String getAtIndex(int var1, int var2) {
         String var3 = null;
         if (var1 == 1) {
            var3 = super.getAtIndex(var1, var2);
         } else {
            char[] var4 = JPasswordField.this.getPassword();
            if (var4 == null || var2 < 0 || var2 >= var4.length) {
               return null;
            }

            var3 = new String(var4);
         }

         return this.getEchoString(var3);
      }

      public String getAfterIndex(int var1, int var2) {
         if (var1 == 1) {
            String var3 = super.getAfterIndex(var1, var2);
            return this.getEchoString(var3);
         } else {
            return null;
         }
      }

      public String getBeforeIndex(int var1, int var2) {
         if (var1 == 1) {
            String var3 = super.getBeforeIndex(var1, var2);
            return this.getEchoString(var3);
         } else {
            return null;
         }
      }

      public String getTextRange(int var1, int var2) {
         String var3 = super.getTextRange(var1, var2);
         return this.getEchoString(var3);
      }

      public AccessibleTextSequence getTextSequenceAt(int var1, int var2) {
         if (var1 == 1) {
            AccessibleTextSequence var5 = super.getTextSequenceAt(var1, var2);
            return var5 == null ? null : new AccessibleTextSequence(var5.startIndex, var5.endIndex, this.getEchoString(var5.text));
         } else {
            char[] var3 = JPasswordField.this.getPassword();
            if (var3 != null && var2 >= 0 && var2 < var3.length) {
               String var4 = new String(var3);
               return new AccessibleTextSequence(0, var3.length - 1, this.getEchoString(var4));
            } else {
               return null;
            }
         }
      }

      public AccessibleTextSequence getTextSequenceAfter(int var1, int var2) {
         if (var1 == 1) {
            AccessibleTextSequence var3 = super.getTextSequenceAfter(var1, var2);
            return var3 == null ? null : new AccessibleTextSequence(var3.startIndex, var3.endIndex, this.getEchoString(var3.text));
         } else {
            return null;
         }
      }

      public AccessibleTextSequence getTextSequenceBefore(int var1, int var2) {
         if (var1 == 1) {
            AccessibleTextSequence var3 = super.getTextSequenceBefore(var1, var2);
            return var3 == null ? null : new AccessibleTextSequence(var3.startIndex, var3.endIndex, this.getEchoString(var3.text));
         } else {
            return null;
         }
      }
   }
}
