package java.awt;

import java.awt.event.ItemListener;

public interface ItemSelectable {
   Object[] getSelectedObjects();

   void addItemListener(ItemListener var1);

   void removeItemListener(ItemListener var1);
}
