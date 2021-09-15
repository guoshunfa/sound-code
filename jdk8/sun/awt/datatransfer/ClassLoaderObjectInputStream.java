package sun.awt.datatransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectInputStream extends ObjectInputStream {
   private final Map<Set<String>, ClassLoader> map;

   ClassLoaderObjectInputStream(InputStream var1, Map<Set<String>, ClassLoader> var2) throws IOException {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Null map");
      } else {
         this.map = var2;
      }
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      String var2 = var1.getName();
      HashSet var3 = new HashSet(1);
      var3.add(var2);
      ClassLoader var4 = (ClassLoader)this.map.get(var3);
      return var4 != null ? Class.forName(var2, false, var4) : super.resolveClass(var1);
   }

   protected Class<?> resolveProxyClass(String[] var1) throws IOException, ClassNotFoundException {
      HashSet var2 = new HashSet(var1.length);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.add(var1[var3]);
      }

      ClassLoader var10 = (ClassLoader)this.map.get(var2);
      if (var10 == null) {
         return super.resolveProxyClass(var1);
      } else {
         ClassLoader var4 = null;
         boolean var5 = false;
         Class[] var6 = new Class[var1.length];

         for(int var7 = 0; var7 < var1.length; ++var7) {
            Class var8 = Class.forName(var1[var7], false, var10);
            if ((var8.getModifiers() & 1) == 0) {
               if (var5) {
                  if (var4 != var8.getClassLoader()) {
                     throw new IllegalAccessError("conflicting non-public interface class loaders");
                  }
               } else {
                  var4 = var8.getClassLoader();
                  var5 = true;
               }
            }

            var6[var7] = var8;
         }

         try {
            return Proxy.getProxyClass(var5 ? var4 : var10, var6);
         } catch (IllegalArgumentException var9) {
            throw new ClassNotFoundException((String)null, var9);
         }
      }
   }
}
