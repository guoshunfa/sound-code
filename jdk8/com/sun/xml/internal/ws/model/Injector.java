package com.sun.xml.internal.ws.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

final class Injector {
   private static final Logger LOGGER = Logger.getLogger(Injector.class.getName());
   private static final Method defineClass;
   private static final Method resolveClass;
   private static final Method getPackage;
   private static final Method definePackage;

   static synchronized Class inject(ClassLoader cl, String className, byte[] image) {
      try {
         return cl.loadClass(className);
      } catch (ClassNotFoundException var8) {
         try {
            int packIndex = className.lastIndexOf(46);
            if (packIndex != -1) {
               String pkgname = className.substring(0, packIndex);
               Package pkg = (Package)getPackage.invoke(cl, pkgname);
               if (pkg == null) {
                  definePackage.invoke(cl, pkgname, null, null, null, null, null, null, null);
               }
            }

            Class c = (Class)defineClass.invoke(cl, className.replace('/', '.'), image, 0, image.length);
            resolveClass.invoke(cl, c);
            return c;
         } catch (IllegalAccessException var6) {
            LOGGER.log(Level.FINE, (String)("Unable to inject " + className), (Throwable)var6);
            throw new WebServiceException(var6);
         } catch (InvocationTargetException var7) {
            LOGGER.log(Level.FINE, (String)("Unable to inject " + className), (Throwable)var7);
            throw new WebServiceException(var7);
         }
      }
   }

   static {
      try {
         defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
         resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
         getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
         definePackage = ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
      } catch (NoSuchMethodException var1) {
         throw new NoSuchMethodError(var1.getMessage());
      }

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Injector.defineClass.setAccessible(true);
            Injector.resolveClass.setAccessible(true);
            Injector.getPackage.setAccessible(true);
            Injector.definePackage.setAccessible(true);
            return null;
         }
      });
   }
}
