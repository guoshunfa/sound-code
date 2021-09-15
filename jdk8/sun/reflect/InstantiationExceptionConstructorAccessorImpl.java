package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class InstantiationExceptionConstructorAccessorImpl extends ConstructorAccessorImpl {
   private final String message;

   InstantiationExceptionConstructorAccessorImpl(String var1) {
      this.message = var1;
   }

   public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
      if (this.message == null) {
         throw new InstantiationException();
      } else {
         throw new InstantiationException(this.message);
      }
   }
}
