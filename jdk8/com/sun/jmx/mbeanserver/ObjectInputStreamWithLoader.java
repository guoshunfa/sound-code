package com.sun.jmx.mbeanserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import sun.reflect.misc.ReflectUtil;

class ObjectInputStreamWithLoader extends ObjectInputStream {
   private ClassLoader loader;

   public ObjectInputStreamWithLoader(InputStream var1, ClassLoader var2) throws IOException {
      super(var1);
      this.loader = var2;
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      if (this.loader == null) {
         return super.resolveClass(var1);
      } else {
         String var2 = var1.getName();
         ReflectUtil.checkPackageAccess(var2);
         return Class.forName(var2, false, this.loader);
      }
   }
}
