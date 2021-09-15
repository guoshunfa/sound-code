package javax.swing;

public interface ComboBoxModel<E> extends ListModel<E> {
   void setSelectedItem(Object var1);

   Object getSelectedItem();
}
