package java.beans;

import java.util.EventListenerProxy;

public class PropertyChangeListenerProxy extends EventListenerProxy<PropertyChangeListener> implements PropertyChangeListener {
   private final String propertyName;

   public PropertyChangeListenerProxy(String var1, PropertyChangeListener var2) {
      super(var2);
      this.propertyName = var1;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      ((PropertyChangeListener)this.getListener()).propertyChange(var1);
   }

   public String getPropertyName() {
      return this.propertyName;
   }
}
