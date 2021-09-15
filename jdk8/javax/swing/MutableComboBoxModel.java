package javax.swing;

public interface MutableComboBoxModel<E> extends ComboBoxModel<E> {
   void addElement(E var1);

   void removeElement(Object var1);

   void insertElementAt(E var1, int var2);

   void removeElementAt(int var1);
}
