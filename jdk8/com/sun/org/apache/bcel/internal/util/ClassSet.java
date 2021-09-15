package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class ClassSet implements Serializable {
   private HashMap _map = new HashMap();

   public boolean add(JavaClass clazz) {
      boolean result = false;
      if (!this._map.containsKey(clazz.getClassName())) {
         result = true;
         this._map.put(clazz.getClassName(), clazz);
      }

      return result;
   }

   public void remove(JavaClass clazz) {
      this._map.remove(clazz.getClassName());
   }

   public boolean empty() {
      return this._map.isEmpty();
   }

   public JavaClass[] toArray() {
      Collection values = this._map.values();
      JavaClass[] classes = new JavaClass[values.size()];
      values.toArray(classes);
      return classes;
   }

   public String[] getClassNames() {
      return (String[])((String[])this._map.keySet().toArray(new String[this._map.keySet().size()]));
   }
}
