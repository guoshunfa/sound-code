package javax.swing;

import javax.swing.event.ListDataListener;

public interface ListModel<E> {
   int getSize();

   E getElementAt(int var1);

   void addListDataListener(ListDataListener var1);

   void removeListDataListener(ListDataListener var1);
}
