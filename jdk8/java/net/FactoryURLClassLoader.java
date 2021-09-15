package java.net;

import java.security.AccessControlContext;

final class FactoryURLClassLoader extends URLClassLoader {
   FactoryURLClassLoader(URL[] var1, ClassLoader var2, AccessControlContext var3) {
      super(var1, var2, var3);
   }

   FactoryURLClassLoader(URL[] var1, AccessControlContext var2) {
      super(var1, var2);
   }

   public final Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         int var4 = var1.lastIndexOf(46);
         if (var4 != -1) {
            var3.checkPackageAccess(var1.substring(0, var4));
         }
      }

      return super.loadClass(var1, var2);
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
