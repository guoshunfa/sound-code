package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.ReflectUtil;

class NativeConstructorAccessorImpl extends ConstructorAccessorImpl {
   private final Constructor<?> c;
   private DelegatingConstructorAccessorImpl parent;
   private int numInvocations;

   NativeConstructorAccessorImpl(Constructor<?> var1) {
      this.c = var1;
   }

   public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
      if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.c.getDeclaringClass())) {
         ConstructorAccessorImpl var2 = (ConstructorAccessorImpl)(new MethodAccessorGenerator()).generateConstructor(this.c.getDeclaringClass(), this.c.getParameterTypes(), this.c.getExceptionTypes(), this.c.getModifiers());
         this.parent.setDelegate(var2);
      }

      return newInstance0(this.c, var1);
   }

   void setParent(DelegatingConstructorAccessorImpl var1) {
      this.parent = var1;
   }

   private static native Object newInstance0(Constructor<?> var0, Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
