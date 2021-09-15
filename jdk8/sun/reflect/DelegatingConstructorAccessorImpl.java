package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingConstructorAccessorImpl extends ConstructorAccessorImpl {
   private ConstructorAccessorImpl delegate;

   DelegatingConstructorAccessorImpl(ConstructorAccessorImpl var1) {
      this.setDelegate(var1);
   }

   public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
      return this.delegate.newInstance(var1);
   }

   void setDelegate(ConstructorAccessorImpl var1) {
      this.delegate = var1;
   }
}
