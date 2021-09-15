package javax.swing;

import javax.swing.event.ListSelectionListener;

public interface ListSelectionModel {
   int SINGLE_SELECTION = 0;
   int SINGLE_INTERVAL_SELECTION = 1;
   int MULTIPLE_INTERVAL_SELECTION = 2;

   void setSelectionInterval(int var1, int var2);

   void addSelectionInterval(int var1, int var2);

   void removeSelectionInterval(int var1, int var2);

   int getMinSelectionIndex();

   int getMaxSelectionIndex();

   boolean isSelectedIndex(int var1);

   int getAnchorSelectionIndex();

   void setAnchorSelectionIndex(int var1);

   int getLeadSelectionIndex();

   void setLeadSelectionIndex(int var1);

   void clearSelection();

   boolean isSelectionEmpty();

   void insertIndexInterval(int var1, int var2, boolean var3);

   void removeIndexInterval(int var1, int var2);

   void setValueIsAdjusting(boolean var1);

   boolean getValueIsAdjusting();

   void setSelectionMode(int var1);

   int getSelectionMode();

   void addListSelectionListener(ListSelectionListener var1);

   void removeListSelectionListener(ListSelectionListener var1);
}
