package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.SharedSecrets;

final class VersionHelper12 extends VersionHelper {
   private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
   private static final String trustURLCodebase = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
      public String run() {
         return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
      }
   });

   ClassLoader getURLClassLoader(String[] var1) throws MalformedURLException {
      ClassLoader var2 = this.getContextClassLoader();
      return (ClassLoader)(var1 != null && "true".equalsIgnoreCase(trustURLCodebase) ? URLClassLoader.newInstance(getUrlArray(var1), var2) : var2);
   }

   Class<?> loadClass(String var1) throws ClassNotFoundException {
      ClassLoader var2 = this.getContextClassLoader();
      return Class.forName(var1, true, var2);
   }

   private ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   Thread createThread(final Runnable var1) {
      final AccessControlContext var2 = AccessController.getContext();
      return (Thread)AccessController.doPrivileged(new PrivilegedAction<Thread>() {
         public Thread run() {
            return SharedSecrets.getJavaLangAccess().newThreadWithAcc(var1, var2);
         }
      });
   }
}
