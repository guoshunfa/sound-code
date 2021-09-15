package java.security;

import java.nio.ByteBuffer;
import java.util.HashMap;
import sun.security.util.Debug;

public class SecureClassLoader extends ClassLoader {
   private final boolean initialized;
   private final HashMap<CodeSource, ProtectionDomain> pdcache = new HashMap(11);
   private static final Debug debug = Debug.getInstance("scl");

   protected SecureClassLoader(ClassLoader var1) {
      super(var1);
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkCreateClassLoader();
      }

      this.initialized = true;
   }

   protected SecureClassLoader() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkCreateClassLoader();
      }

      this.initialized = true;
   }

   protected final Class<?> defineClass(String var1, byte[] var2, int var3, int var4, CodeSource var5) {
      return this.defineClass(var1, var2, var3, var4, this.getProtectionDomain(var5));
   }

   protected final Class<?> defineClass(String var1, ByteBuffer var2, CodeSource var3) {
      return this.defineClass(var1, var2, this.getProtectionDomain(var3));
   }

   protected PermissionCollection getPermissions(CodeSource var1) {
      this.check();
      return new Permissions();
   }

   private ProtectionDomain getProtectionDomain(CodeSource var1) {
      if (var1 == null) {
         return null;
      } else {
         ProtectionDomain var2 = null;
         synchronized(this.pdcache) {
            var2 = (ProtectionDomain)this.pdcache.get(var1);
            if (var2 == null) {
               PermissionCollection var4 = this.getPermissions(var1);
               var2 = new ProtectionDomain(var1, var4, this, (Principal[])null);
               this.pdcache.put(var1, var2);
               if (debug != null) {
                  debug.println(" getPermissions " + var2);
                  debug.println("");
               }
            }

            return var2;
         }
      }
   }

   private void check() {
      if (!this.initialized) {
         throw new SecurityException("ClassLoader object not initialized");
      }
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
