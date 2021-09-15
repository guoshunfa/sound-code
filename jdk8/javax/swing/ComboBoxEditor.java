package javax.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

public interface ComboBoxEditor {
   Component getEditorComponent();

   void setItem(Object var1);

   Object getItem();

   void selectAll();

   void addActionListener(ActionListener var1);

   void removeActionListener(ActionListener var1);
}
