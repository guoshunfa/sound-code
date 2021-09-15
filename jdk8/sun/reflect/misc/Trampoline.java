package sun.reflect.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;

class Trampoline {
   private static void ensureInvocableMethod(Method var0) throws InvocationTargetException {
      Class var1 = var0.getDeclaringClass();
      if (var1.equals(AccessController.class) || var1.equals(Method.class) || var1.getName().startsWith("java.lang.invoke.")) {
         throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported"));
      }
   }

   private static Object invoke(Method var0, Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
      ensureInvocableMethod(var0);
      return var0.invoke(var1, var2);
   }

   static {
      if (Trampoline.class.getClassLoader() == null) {
         throw new Error("Trampoline must not be defined by the bootstrap classloader");
      }
   }
}
