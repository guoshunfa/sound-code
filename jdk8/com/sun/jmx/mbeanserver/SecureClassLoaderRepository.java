package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

final class SecureClassLoaderRepository implements ClassLoaderRepository {
   private final ClassLoaderRepository clr;

   public SecureClassLoaderRepository(ClassLoaderRepository var1) {
      this.clr = var1;
   }

   public final Class<?> loadClass(String var1) throws ClassNotFoundException {
      return this.clr.loadClass(var1);
   }

   public final Class<?> loadClassWithout(ClassLoader var1, String var2) throws ClassNotFoundException {
      return this.clr.loadClassWithout(var1, var2);
   }

   public final Class<?> loadClassBefore(ClassLoader var1, String var2) throws ClassNotFoundException {
      return this.clr.loadClassBefore(var1, var2);
   }
}
