package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.ArrayList;

public class ClassVector implements Serializable {
   protected ArrayList vec = new ArrayList();

   public void addElement(JavaClass clazz) {
      this.vec.add(clazz);
   }

   public JavaClass elementAt(int index) {
      return (JavaClass)this.vec.get(index);
   }

   public void removeElementAt(int index) {
      this.vec.remove(index);
   }

   public JavaClass[] toArray() {
      JavaClass[] classes = new JavaClass[this.vec.size()];
      this.vec.toArray(classes);
      return classes;
   }
}
