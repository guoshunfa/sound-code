package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ClassLoaderRepository implements Repository {
   private java.lang.ClassLoader loader;
   private HashMap loadedClasses = new HashMap();

   public ClassLoaderRepository(java.lang.ClassLoader loader) {
      this.loader = loader;
   }

   public void storeClass(JavaClass clazz) {
      this.loadedClasses.put(clazz.getClassName(), clazz);
      clazz.setRepository(this);
   }

   public void removeClass(JavaClass clazz) {
      this.loadedClasses.remove(clazz.getClassName());
   }

   public JavaClass findClass(String className) {
      return this.loadedClasses.containsKey(className) ? (JavaClass)this.loadedClasses.get(className) : null;
   }

   public JavaClass loadClass(String className) throws ClassNotFoundException {
      String classFile = className.replace('.', '/');
      JavaClass RC = this.findClass(className);
      if (RC != null) {
         return RC;
      } else {
         try {
            InputStream is = this.loader.getResourceAsStream(classFile + ".class");
            if (is == null) {
               throw new ClassNotFoundException(className + " not found.");
            } else {
               ClassParser parser = new ClassParser(is, className);
               RC = parser.parse();
               this.storeClass(RC);
               return RC;
            }
         } catch (IOException var6) {
            throw new ClassNotFoundException(var6.toString());
         }
      }
   }

   public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
      return this.loadClass(clazz.getName());
   }

   public void clear() {
      this.loadedClasses.clear();
   }
}
