package java.beans;

import java.util.EventListenerProxy;

public class VetoableChangeListenerProxy extends EventListenerProxy<VetoableChangeListener> implements VetoableChangeListener {
   private final String propertyName;

   public VetoableChangeListenerProxy(String var1, VetoableChangeListener var2) {
      super(var2);
      this.propertyName = var1;
   }

   public void vetoableChange(PropertyChangeEvent var1) throws PropertyVetoException {
      ((VetoableChangeListener)this.getListener()).vetoableChange(var1);
   }

   public String getPropertyName() {
      return this.propertyName;
   }
}
