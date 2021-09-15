package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingMethodAccessorImpl extends MethodAccessorImpl {
   private MethodAccessorImpl delegate;

   DelegatingMethodAccessorImpl(MethodAccessorImpl var1) {
      this.setDelegate(var1);
   }

   public Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException {
      return this.delegate.invoke(var1, var2);
   }

   void setDelegate(MethodAccessorImpl var1) {
      this.delegate = var1;
   }
}
