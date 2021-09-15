package sun.awt.datatransfer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectOutputStream extends ObjectOutputStream {
   private final Map<Set<String>, ClassLoader> map = new HashMap();

   ClassLoaderObjectOutputStream(OutputStream var1) throws IOException {
      super(var1);
   }

   protected void annotateClass(final Class<?> var1) throws IOException {
      ClassLoader var2 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var1.getClassLoader();
         }
      });
      HashSet var3 = new HashSet(1);
      var3.add(var1.getName());
      this.map.put(var3, var2);
   }

   protected void annotateProxyClass(final Class<?> var1) throws IOException {
      ClassLoader var2 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var1.getClassLoader();
         }
      });
      Class[] var3 = var1.getInterfaces();
      HashSet var4 = new HashSet(var3.length);

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4.add(var3[var5].getName());
      }

      this.map.put(var4, var2);
   }

   Map<Set<String>, ClassLoader> getClassLoaderMap() {
      return new HashMap(this.map);
   }
}
