package java.awt.peer;

import java.awt.im.InputMethodRequests;

public interface TextComponentPeer extends ComponentPeer {
   void setEditable(boolean var1);

   String getText();

   void setText(String var1);

   int getSelectionStart();

   int getSelectionEnd();

   void select(int var1, int var2);

   void setCaretPosition(int var1);

   int getCaretPosition();

   InputMethodRequests getInputMethodRequests();
}
