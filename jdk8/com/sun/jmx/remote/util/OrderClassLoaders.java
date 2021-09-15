package com.sun.jmx.remote.util;

import sun.reflect.misc.ReflectUtil;

public class OrderClassLoaders extends ClassLoader {
   private ClassLoader cl2;

   public OrderClassLoaders(ClassLoader var1, ClassLoader var2) {
      super(var1);
      this.cl2 = var2;
   }

   protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var1);

      try {
         return super.loadClass(var1, var2);
      } catch (ClassNotFoundException var4) {
         if (this.cl2 != null) {
            return this.cl2.loadClass(var1);
         } else {
            throw var4;
         }
      }
   }
}
