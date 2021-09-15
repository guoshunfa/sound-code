package javax.swing;

import javax.swing.event.ChangeListener;

public interface BoundedRangeModel {
   int getMinimum();

   void setMinimum(int var1);

   int getMaximum();

   void setMaximum(int var1);

   int getValue();

   void setValue(int var1);

   void setValueIsAdjusting(boolean var1);

   boolean getValueIsAdjusting();

   int getExtent();

   void setExtent(int var1);

   void setRangeProperties(int var1, int var2, int var3, int var4, boolean var5);

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);
}
