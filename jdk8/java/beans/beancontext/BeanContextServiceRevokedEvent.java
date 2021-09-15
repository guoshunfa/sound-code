package java.beans.beancontext;

public class BeanContextServiceRevokedEvent extends BeanContextEvent {
   private static final long serialVersionUID = -1295543154724961754L;
   protected Class serviceClass;
   private boolean invalidateRefs;

   public BeanContextServiceRevokedEvent(BeanContextServices var1, Class var2, boolean var3) {
      super(var1);
      this.serviceClass = var2;
      this.invalidateRefs = var3;
   }

   public BeanContextServices getSourceAsBeanContextServices() {
      return (BeanContextServices)this.getBeanContext();
   }

   public Class getServiceClass() {
      return this.serviceClass;
   }

   public boolean isServiceClass(Class var1) {
      return this.serviceClass.equals(var1);
   }

   public boolean isCurrentServiceInvalidNow() {
      return this.invalidateRefs;
   }
}
