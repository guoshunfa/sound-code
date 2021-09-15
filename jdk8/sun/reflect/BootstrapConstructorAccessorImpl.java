package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class BootstrapConstructorAccessorImpl extends ConstructorAccessorImpl {
   private final Constructor<?> constructor;

   BootstrapConstructorAccessorImpl(Constructor<?> var1) {
      this.constructor = var1;
   }

   public Object newInstance(Object[] var1) throws IllegalArgumentException, InvocationTargetException {
      try {
         return UnsafeFieldAccessorImpl.unsafe.allocateInstance(this.constructor.getDeclaringClass());
      } catch (InstantiationException var3) {
         throw new InvocationTargetException(var3);
      }
   }
}
