package com.sun.corba.se.impl.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.corba.Bridge;

class JDKClassLoader {
   private static final JDKClassLoader.JDKClassLoaderCache classCache = new JDKClassLoader.JDKClassLoaderCache();
   private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {
         return Bridge.get();
      }
   });

   static Class loadClass(Class var0, String var1) throws ClassNotFoundException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.length() == 0) {
         throw new ClassNotFoundException();
      } else {
         ClassLoader var2;
         if (var0 != null) {
            var2 = var0.getClassLoader();
         } else {
            var2 = bridge.getLatestUserDefinedLoader();
         }

         Object var3 = classCache.createKey(var1, var2);
         if (classCache.knownToFail(var3)) {
            throw new ClassNotFoundException(var1);
         } else {
            try {
               return Class.forName(var1, false, var2);
            } catch (ClassNotFoundException var5) {
               classCache.recordFailure(var3);
               throw var5;
            }
         }
      }
   }

   private static class JDKClassLoaderCache {
      private final Map cache;
      private static final Object KNOWN_TO_FAIL = new Object();

      private JDKClassLoaderCache() {
         this.cache = Collections.synchronizedMap(new WeakHashMap());
      }

      public final void recordFailure(Object var1) {
         this.cache.put(var1, KNOWN_TO_FAIL);
      }

      public final Object createKey(String var1, ClassLoader var2) {
         return new JDKClassLoader.JDKClassLoaderCache.CacheKey(var1, var2);
      }

      public final boolean knownToFail(Object var1) {
         return this.cache.get(var1) == KNOWN_TO_FAIL;
      }

      // $FF: synthetic method
      JDKClassLoaderCache(Object var1) {
         this();
      }

      private static class CacheKey {
         String className;
         ClassLoader loader;

         public CacheKey(String var1, ClassLoader var2) {
            this.className = var1;
            this.loader = var2;
         }

         public int hashCode() {
            return this.loader == null ? this.className.hashCode() : this.className.hashCode() ^ this.loader.hashCode();
         }

         public boolean equals(Object var1) {
            try {
               if (var1 == null) {
                  return false;
               } else {
                  JDKClassLoader.JDKClassLoaderCache.CacheKey var2 = (JDKClassLoader.JDKClassLoaderCache.CacheKey)var1;
                  return this.className.equals(var2.className) && this.loader == var2.loader;
               }
            } catch (ClassCastException var3) {
               return false;
            }
         }
      }
   }
}
