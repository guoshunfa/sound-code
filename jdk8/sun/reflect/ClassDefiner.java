package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import sun.misc.Unsafe;

class ClassDefiner {
   static final Unsafe unsafe = Unsafe.getUnsafe();

   static Class<?> defineClass(String var0, byte[] var1, int var2, int var3, final ClassLoader var4) {
      ClassLoader var5 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return new DelegatingClassLoader(var4);
         }
      });
      return unsafe.defineClass(var0, var1, var2, var3, var5, (ProtectionDomain)null);
   }
}
