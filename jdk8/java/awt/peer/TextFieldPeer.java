package java.awt.peer;

import java.awt.Dimension;

public interface TextFieldPeer extends TextComponentPeer {
   void setEchoChar(char var1);

   Dimension getPreferredSize(int var1);

   Dimension getMinimumSize(int var1);
}
