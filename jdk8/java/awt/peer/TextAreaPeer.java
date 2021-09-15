package java.awt.peer;

import java.awt.Dimension;

public interface TextAreaPeer extends TextComponentPeer {
   void insert(String var1, int var2);

   void replaceRange(String var1, int var2, int var3);

   Dimension getPreferredSize(int var1, int var2);

   Dimension getMinimumSize(int var1, int var2);
}
