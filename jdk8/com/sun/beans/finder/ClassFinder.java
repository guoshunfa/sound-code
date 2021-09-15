package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;

public final class ClassFinder {
   public static Class<?> findClass(String var0) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var0);

      try {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();
         if (var1 == null) {
            var1 = ClassLoader.getSystemClassLoader();
         }

         if (var1 != null) {
            return Class.forName(var0, false, var1);
         }
      } catch (ClassNotFoundException var2) {
      } catch (SecurityException var3) {
      }

      return Class.forName(var0);
   }

   public static Class<?> findClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var0);
      if (var1 != null) {
         try {
            return Class.forName(var0, false, var1);
         } catch (ClassNotFoundException var3) {
         } catch (SecurityException var4) {
         }
      }

      return findClass(var0);
   }

   public static Class<?> resolveClass(String var0) throws ClassNotFoundException {
      return resolveClass(var0, (ClassLoader)null);
   }

   public static Class<?> resolveClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      Class var2 = PrimitiveTypeMap.getType(var0);
      return var2 == null ? findClass(var0, var1) : var2;
   }

   private ClassFinder() {
   }
}
