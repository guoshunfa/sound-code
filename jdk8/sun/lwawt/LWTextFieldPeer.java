package sun.lwawt;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.peer.TextFieldPeer;
import javax.swing.InputMap;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

final class LWTextFieldPeer extends LWTextComponentPeer<TextField, JPasswordField> implements TextFieldPeer, ActionListener {
   LWTextFieldPeer(TextField var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JPasswordField createDelegate() {
      return new LWTextFieldPeer.JPasswordFieldDelegate();
   }

   void initializeImpl() {
      super.initializeImpl();
      this.setEchoChar(((TextField)this.getTarget()).getEchoChar());
      synchronized(this.getDelegateLock()) {
         ((JPasswordField)this.getDelegate()).addActionListener(this);
      }
   }

   public JTextComponent getTextComponent() {
      return (JTextComponent)this.getDelegate();
   }

   public void setEchoChar(char var1) {
      synchronized(this.getDelegateLock()) {
         ((JPasswordField)this.getDelegate()).setEchoChar(var1);
         boolean var3;
         String var4;
         if (var1 != 0) {
            var3 = false;
            var4 = "PasswordField.focusInputMap";
         } else {
            var3 = true;
            var4 = "TextField.focusInputMap";
         }

         ((JPasswordField)this.getDelegate()).putClientProperty("JPasswordField.cutCopyAllowed", var3);
         InputMap var5 = (InputMap)UIManager.get(var4);
         SwingUtilities.replaceUIInputMap(this.getDelegate(), 0, var5);
      }
   }

   public Dimension getPreferredSize(int var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(int var1) {
      return this.getMinimumSize(1, var1);
   }

   public void actionPerformed(ActionEvent var1) {
      this.postEvent(new ActionEvent(this.getTarget(), 1001, this.getText(), var1.getWhen(), var1.getModifiers()));
   }

   void handleJavaFocusEvent(FocusEvent var1) {
      if (var1.getID() == 1005) {
         this.setCaretPosition(0);
      }

      super.handleJavaFocusEvent(var1);
   }

   private final class JPasswordFieldDelegate extends JPasswordField {
      JPasswordFieldDelegate() {
      }

      public void replaceSelection(String var1) {
         this.getDocument().removeDocumentListener(LWTextFieldPeer.this);
         super.replaceSelection(var1);
         LWTextFieldPeer.this.postTextEvent();
         this.getDocument().addDocumentListener(LWTextFieldPeer.this);
      }

      public boolean hasFocus() {
         return ((TextField)LWTextFieldPeer.this.getTarget()).hasFocus();
      }

      public Point getLocationOnScreen() {
         return LWTextFieldPeer.this.getLocationOnScreen();
      }
   }
}
