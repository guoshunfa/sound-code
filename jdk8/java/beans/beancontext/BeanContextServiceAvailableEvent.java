package java.beans.beancontext;

import java.util.Iterator;

public class BeanContextServiceAvailableEvent extends BeanContextEvent {
   private static final long serialVersionUID = -5333985775656400778L;
   protected Class serviceClass;

   public BeanContextServiceAvailableEvent(BeanContextServices var1, Class var2) {
      super(var1);
      this.serviceClass = var2;
   }

   public BeanContextServices getSourceAsBeanContextServices() {
      return (BeanContextServices)this.getBeanContext();
   }

   public Class getServiceClass() {
      return this.serviceClass;
   }

   public Iterator getCurrentServiceSelectors() {
      return ((BeanContextServices)this.getSource()).getCurrentServiceSelectors(this.serviceClass);
   }
}
