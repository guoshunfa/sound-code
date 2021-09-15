package java.lang;

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

class SystemClassLoaderAction implements PrivilegedExceptionAction<ClassLoader> {
   private ClassLoader parent;

   SystemClassLoaderAction(ClassLoader var1) {
      this.parent = var1;
   }

   public ClassLoader run() throws Exception {
      String var1 = System.getProperty("java.system.class.loader");
      if (var1 == null) {
         return this.parent;
      } else {
         Constructor var2 = Class.forName(var1, true, this.parent).getDeclaredConstructor(ClassLoader.class);
         ClassLoader var3 = (ClassLoader)var2.newInstance(this.parent);
         Thread.currentThread().setContextClassLoader(var3);
         return var3;
      }
   }
}
