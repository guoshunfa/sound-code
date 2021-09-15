package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SyntheticRepository implements Repository {
   private static final String DEFAULT_PATH = ClassPath.getClassPath();
   private static HashMap _instances = new HashMap();
   private ClassPath _path = null;
   private HashMap _loadedClasses = new HashMap();

   private SyntheticRepository(ClassPath path) {
      this._path = path;
   }

   public static SyntheticRepository getInstance() {
      return getInstance(ClassPath.SYSTEM_CLASS_PATH);
   }

   public static SyntheticRepository getInstance(ClassPath classPath) {
      SyntheticRepository rep = (SyntheticRepository)_instances.get(classPath);
      if (rep == null) {
         rep = new SyntheticRepository(classPath);
         _instances.put(classPath, rep);
      }

      return rep;
   }

   public void storeClass(JavaClass clazz) {
      this._loadedClasses.put(clazz.getClassName(), clazz);
      clazz.setRepository(this);
   }

   public void removeClass(JavaClass clazz) {
      this._loadedClasses.remove(clazz.getClassName());
   }

   public JavaClass findClass(String className) {
      return (JavaClass)this._loadedClasses.get(className);
   }

   public JavaClass loadClass(String className) throws ClassNotFoundException {
      if (className != null && !className.equals("")) {
         className = className.replace('/', '.');

         try {
            return this.loadClass(this._path.getInputStream(className), className);
         } catch (IOException var3) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + var3.toString());
         }
      } else {
         throw new IllegalArgumentException("Invalid class name " + className);
      }
   }

   public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
      String className = clazz.getName();
      String name = className;
      int i = className.lastIndexOf(46);
      if (i > 0) {
         name = className.substring(i + 1);
      }

      return this.loadClass(clazz.getResourceAsStream(name + ".class"), className);
   }

   private JavaClass loadClass(InputStream is, String className) throws ClassNotFoundException {
      JavaClass clazz = this.findClass(className);
      if (clazz != null) {
         return clazz;
      } else {
         try {
            if (is != null) {
               ClassParser parser = new ClassParser(is, className);
               clazz = parser.parse();
               this.storeClass(clazz);
               return clazz;
            }
         } catch (IOException var5) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + var5.toString());
         }

         throw new ClassNotFoundException("SyntheticRepository could not load " + className);
      }
   }

   public void clear() {
      this._loadedClasses.clear();
   }
}
