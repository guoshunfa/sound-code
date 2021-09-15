package java.beans.beancontext;

import java.util.EventObject;

public abstract class BeanContextEvent extends EventObject {
   private static final long serialVersionUID = 7267998073569045052L;
   protected BeanContext propagatedFrom;

   protected BeanContextEvent(BeanContext var1) {
      super(var1);
   }

   public BeanContext getBeanContext() {
      return (BeanContext)this.getSource();
   }

   public synchronized void setPropagatedFrom(BeanContext var1) {
      this.propagatedFrom = var1;
   }

   public synchronized BeanContext getPropagatedFrom() {
      return this.propagatedFrom;
   }

   public synchronized boolean isPropagated() {
      return this.propagatedFrom != null;
   }
}
