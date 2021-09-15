package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

class NativeMethodAccessorImpl extends MethodAccessorImpl {
   private final Method method;
   private DelegatingMethodAccessorImpl parent;
   private int numInvocations;

   NativeMethodAccessorImpl(Method var1) {
      this.method = var1;
   }

   public Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException {
      if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.method.getDeclaringClass())) {
         MethodAccessorImpl var3 = (MethodAccessorImpl)(new MethodAccessorGenerator()).generateMethod(this.method.getDeclaringClass(), this.method.getName(), this.method.getParameterTypes(), this.method.getReturnType(), this.method.getExceptionTypes(), this.method.getModifiers());
         this.parent.setDelegate(var3);
      }

      return invoke0(this.method, var1, var2);
   }

   void setParent(DelegatingMethodAccessorImpl var1) {
      this.parent = var1;
   }

   private static native Object invoke0(Method var0, Object var1, Object[] var2);
}
