package com.sun.org.apache.xml.internal.security.transforms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ClassLoaderUtils {
   private static final Logger log = Logger.getLogger(ClassLoaderUtils.class.getName());

   private ClassLoaderUtils() {
   }

   static URL getResource(String var0, Class<?> var1) {
      URL var2 = Thread.currentThread().getContextClassLoader().getResource(var0);
      if (var2 == null && var0.startsWith("/")) {
         var2 = Thread.currentThread().getContextClassLoader().getResource(var0.substring(1));
      }

      ClassLoader var3 = ClassLoaderUtils.class.getClassLoader();
      if (var3 == null) {
         var3 = ClassLoader.getSystemClassLoader();
      }

      if (var2 == null) {
         var2 = var3.getResource(var0);
      }

      if (var2 == null && var0.startsWith("/")) {
         var2 = var3.getResource(var0.substring(1));
      }

      if (var2 == null) {
         ClassLoader var4 = var1.getClassLoader();
         if (var4 != null) {
            var2 = var4.getResource(var0);
         }
      }

      if (var2 == null) {
         var2 = var1.getResource(var0);
      }

      return var2 == null && var0 != null && var0.charAt(0) != '/' ? getResource('/' + var0, var1) : var2;
   }

   static List<URL> getResources(String var0, Class<?> var1) {
      ArrayList var2 = new ArrayList();
      Enumeration var3 = new Enumeration<URL>() {
         public boolean hasMoreElements() {
            return false;
         }

         public URL nextElement() {
            return null;
         }
      };

      try {
         var3 = Thread.currentThread().getContextClassLoader().getResources(var0);
      } catch (IOException var11) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var11.getMessage(), (Throwable)var11);
         }
      }

      if (!var3.hasMoreElements() && var0.startsWith("/")) {
         try {
            var3 = Thread.currentThread().getContextClassLoader().getResources(var0.substring(1));
         } catch (IOException var10) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var10.getMessage(), (Throwable)var10);
            }
         }
      }

      ClassLoader var4 = ClassLoaderUtils.class.getClassLoader();
      if (var4 == null) {
         var4 = ClassLoader.getSystemClassLoader();
      }

      if (!var3.hasMoreElements()) {
         try {
            var3 = var4.getResources(var0);
         } catch (IOException var9) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var9.getMessage(), (Throwable)var9);
            }
         }
      }

      if (!var3.hasMoreElements() && var0.startsWith("/")) {
         try {
            var3 = var4.getResources(var0.substring(1));
         } catch (IOException var8) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var8.getMessage(), (Throwable)var8);
            }
         }
      }

      if (!var3.hasMoreElements()) {
         ClassLoader var5 = var1.getClassLoader();
         if (var5 != null) {
            try {
               var3 = var5.getResources(var0);
            } catch (IOException var7) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var7.getMessage(), (Throwable)var7);
               }
            }
         }
      }

      if (!var3.hasMoreElements()) {
         URL var12 = var1.getResource(var0);
         if (var12 != null) {
            var2.add(var12);
         }
      }

      while(var3.hasMoreElements()) {
         var2.add(var3.nextElement());
      }

      return (List)(var2.isEmpty() && var0 != null && var0.charAt(0) != '/' ? getResources('/' + var0, var1) : var2);
   }

   static InputStream getResourceAsStream(String var0, Class<?> var1) {
      URL var2 = getResource(var0, var1);

      try {
         return var2 != null ? var2.openStream() : null;
      } catch (IOException var4) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var4.getMessage(), (Throwable)var4);
         }

         return null;
      }
   }

   static Class<?> loadClass(String var0, Class<?> var1) throws ClassNotFoundException {
      try {
         ClassLoader var2 = Thread.currentThread().getContextClassLoader();
         if (var2 != null) {
            return var2.loadClass(var0);
         }
      } catch (ClassNotFoundException var3) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var3.getMessage(), (Throwable)var3);
         }
      }

      return loadClass2(var0, var1);
   }

   private static Class<?> loadClass2(String var0, Class<?> var1) throws ClassNotFoundException {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var5) {
         try {
            if (ClassLoaderUtils.class.getClassLoader() != null) {
               return ClassLoaderUtils.class.getClassLoader().loadClass(var0);
            }
         } catch (ClassNotFoundException var4) {
            if (var1 != null && var1.getClassLoader() != null) {
               return var1.getClassLoader().loadClass(var0);
            }
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var5.getMessage(), (Throwable)var5);
         }

         throw var5;
      }
   }
}
