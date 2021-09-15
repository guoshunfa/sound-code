package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;

public interface Repository extends Serializable {
   void storeClass(JavaClass var1);

   void removeClass(JavaClass var1);

   JavaClass findClass(String var1);

   JavaClass loadClass(String var1) throws ClassNotFoundException;

   JavaClass loadClass(Class var1) throws ClassNotFoundException;

   void clear();
}
