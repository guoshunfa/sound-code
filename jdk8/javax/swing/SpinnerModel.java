package javax.swing;

import javax.swing.event.ChangeListener;

public interface SpinnerModel {
   Object getValue();

   void setValue(Object var1);

   Object getNextValue();

   Object getPreviousValue();

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);
}
