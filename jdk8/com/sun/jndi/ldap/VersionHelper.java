package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URL;

abstract class VersionHelper {
   private static VersionHelper helper = null;

   static VersionHelper getVersionHelper() {
      return helper;
   }

   abstract ClassLoader getURLClassLoader(String[] var1) throws MalformedURLException;

   protected static URL[] getUrlArray(String[] var0) throws MalformedURLException {
      URL[] var1 = new URL[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = new URL(var0[var2]);
      }

      return var1;
   }

   abstract Class<?> loadClass(String var1) throws ClassNotFoundException;

   abstract Thread createThread(Runnable var1);

   static {
      try {
         Class.forName("java.net.URLClassLoader");
         Class.forName("java.security.PrivilegedAction");
         helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper12").newInstance();
      } catch (Exception var2) {
      }

      if (helper == null) {
         try {
            helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper11").newInstance();
         } catch (Exception var1) {
         }
      }

   }
}
