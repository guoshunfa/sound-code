package javax.swing;

import javax.swing.event.ChangeListener;

public interface SingleSelectionModel {
   int getSelectedIndex();

   void setSelectedIndex(int var1);

   void clearSelection();

   boolean isSelected();

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);
}
