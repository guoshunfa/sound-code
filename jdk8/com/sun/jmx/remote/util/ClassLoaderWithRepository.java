package com.sun.jmx.remote.util;

import javax.management.loading.ClassLoaderRepository;

public class ClassLoaderWithRepository extends ClassLoader {
   private ClassLoaderRepository repository;
   private ClassLoader cl2;

   public ClassLoaderWithRepository(ClassLoaderRepository var1, ClassLoader var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null ClassLoaderRepository object.");
      } else {
         this.repository = var1;
         this.cl2 = var2;
      }
   }

   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      Class var2;
      try {
         var2 = this.repository.loadClass(var1);
      } catch (ClassNotFoundException var4) {
         if (this.cl2 != null) {
            return this.cl2.loadClass(var1);
         }

         throw var4;
      }

      if (!var2.getName().equals(var1)) {
         if (this.cl2 != null) {
            return this.cl2.loadClass(var1);
         } else {
            throw new ClassNotFoundException(var1);
         }
      } else {
         return var2;
      }
   }
}
