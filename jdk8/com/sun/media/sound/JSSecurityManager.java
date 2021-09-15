package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.sound.sampled.AudioPermission;

final class JSSecurityManager {
   private JSSecurityManager() {
   }

   private static boolean hasSecurityManager() {
      return System.getSecurityManager() != null;
   }

   static void checkRecordPermission() throws SecurityException {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new AudioPermission("record"));
      }

   }

   static void loadProperties(final Properties var0, final String var1) {
      if (hasSecurityManager()) {
         try {
            PrivilegedAction var2 = new PrivilegedAction<Void>() {
               public Void run() {
                  JSSecurityManager.loadPropertiesImpl(var0, var1);
                  return null;
               }
            };
            AccessController.doPrivileged(var2);
         } catch (Exception var3) {
            loadPropertiesImpl(var0, var1);
         }
      } else {
         loadPropertiesImpl(var0, var1);
      }

   }

   private static void loadPropertiesImpl(Properties var0, String var1) {
      String var2 = System.getProperty("java.home");

      try {
         if (var2 == null) {
            throw new Error("Can't find java.home ??");
         }

         File var3 = new File(var2, "lib");
         var3 = new File(var3, var1);
         var2 = var3.getCanonicalPath();
         FileInputStream var4 = new FileInputStream(var2);
         BufferedInputStream var5 = new BufferedInputStream(var4);

         try {
            var0.load((InputStream)var5);
         } finally {
            if (var4 != null) {
               var4.close();
            }

         }
      } catch (Throwable var10) {
      }

   }

   static Thread createThread(Runnable var0, String var1, boolean var2, int var3, boolean var4) {
      Thread var5 = new Thread(var0);
      if (var1 != null) {
         var5.setName(var1);
      }

      var5.setDaemon(var2);
      if (var3 >= 0) {
         var5.setPriority(var3);
      }

      if (var4) {
         var5.start();
      }

      return var5;
   }

   static synchronized <T> List<T> getProviders(final Class<T> var0) {
      ArrayList var1 = new ArrayList(7);
      PrivilegedAction var2 = new PrivilegedAction<Iterator<T>>() {
         public Iterator<T> run() {
            return ServiceLoader.load(var0).iterator();
         }
      };
      final Iterator var3 = (Iterator)AccessController.doPrivileged(var2);
      PrivilegedAction var4 = new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return var3.hasNext();
         }
      };

      while((Boolean)AccessController.doPrivileged(var4)) {
         try {
            Object var5 = var3.next();
            if (var0.isInstance(var5)) {
               var1.add(0, var5);
            }
         } catch (Throwable var6) {
         }
      }

      return var1;
   }
}
