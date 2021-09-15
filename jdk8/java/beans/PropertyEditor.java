package java.beans;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public interface PropertyEditor {
   void setValue(Object var1);

   Object getValue();

   boolean isPaintable();

   void paintValue(Graphics var1, Rectangle var2);

   String getJavaInitializationString();

   String getAsText();

   void setAsText(String var1) throws IllegalArgumentException;

   String[] getTags();

   Component getCustomEditor();

   boolean supportsCustomEditor();

   void addPropertyChangeListener(PropertyChangeListener var1);

   void removePropertyChangeListener(PropertyChangeListener var1);
}
