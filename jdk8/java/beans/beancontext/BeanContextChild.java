package java.beans.beancontext;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public interface BeanContextChild {
   void setBeanContext(BeanContext var1) throws PropertyVetoException;

   BeanContext getBeanContext();

   void addPropertyChangeListener(String var1, PropertyChangeListener var2);

   void removePropertyChangeListener(String var1, PropertyChangeListener var2);

   void addVetoableChangeListener(String var1, VetoableChangeListener var2);

   void removeVetoableChangeListener(String var1, VetoableChangeListener var2);
}
