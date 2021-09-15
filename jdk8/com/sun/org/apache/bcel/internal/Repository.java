package com.sun.org.apache.bcel.internal;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.util.ClassPath;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import java.io.IOException;

public abstract class Repository {
   private static com.sun.org.apache.bcel.internal.util.Repository _repository = SyntheticRepository.getInstance();

   public static com.sun.org.apache.bcel.internal.util.Repository getRepository() {
      return _repository;
   }

   public static void setRepository(com.sun.org.apache.bcel.internal.util.Repository rep) {
      _repository = rep;
   }

   public static JavaClass lookupClass(String class_name) {
      try {
         JavaClass clazz = _repository.findClass(class_name);
         return clazz == null ? _repository.loadClass(class_name) : clazz;
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   public static JavaClass lookupClass(Class clazz) {
      try {
         return _repository.loadClass(clazz);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   public static ClassPath.ClassFile lookupClassFile(String class_name) {
      try {
         return ClassPath.SYSTEM_CLASS_PATH.getClassFile(class_name);
      } catch (IOException var2) {
         return null;
      }
   }

   public static void clearCache() {
      _repository.clear();
   }

   public static JavaClass addClass(JavaClass clazz) {
      JavaClass old = _repository.findClass(clazz.getClassName());
      _repository.storeClass(clazz);
      return old;
   }

   public static void removeClass(String clazz) {
      _repository.removeClass(_repository.findClass(clazz));
   }

   public static void removeClass(JavaClass clazz) {
      _repository.removeClass(clazz);
   }

   public static JavaClass[] getSuperClasses(JavaClass clazz) {
      return clazz.getSuperClasses();
   }

   public static JavaClass[] getSuperClasses(String class_name) {
      JavaClass jc = lookupClass(class_name);
      return jc == null ? null : getSuperClasses(jc);
   }

   public static JavaClass[] getInterfaces(JavaClass clazz) {
      return clazz.getAllInterfaces();
   }

   public static JavaClass[] getInterfaces(String class_name) {
      return getInterfaces(lookupClass(class_name));
   }

   public static boolean instanceOf(JavaClass clazz, JavaClass super_class) {
      return clazz.instanceOf(super_class);
   }

   public static boolean instanceOf(String clazz, String super_class) {
      return instanceOf(lookupClass(clazz), lookupClass(super_class));
   }

   public static boolean instanceOf(JavaClass clazz, String super_class) {
      return instanceOf(clazz, lookupClass(super_class));
   }

   public static boolean instanceOf(String clazz, JavaClass super_class) {
      return instanceOf(lookupClass(clazz), super_class);
   }

   public static boolean implementationOf(JavaClass clazz, JavaClass inter) {
      return clazz.implementationOf(inter);
   }

   public static boolean implementationOf(String clazz, String inter) {
      return implementationOf(lookupClass(clazz), lookupClass(inter));
   }

   public static boolean implementationOf(JavaClass clazz, String inter) {
      return implementationOf(clazz, lookupClass(inter));
   }

   public static boolean implementationOf(String clazz, JavaClass inter) {
      return implementationOf(lookupClass(clazz), inter);
   }
}
