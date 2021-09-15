package java.awt.peer;

import java.awt.Dimension;

public interface ListPeer extends ComponentPeer {
   int[] getSelectedIndexes();

   void add(String var1, int var2);

   void delItems(int var1, int var2);

   void removeAll();

   void select(int var1);

   void deselect(int var1);

   void makeVisible(int var1);

   void setMultipleMode(boolean var1);

   Dimension getPreferredSize(int var1);

   Dimension getMinimumSize(int var1);
}
