package com.sun.xml.internal.bind.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecureLoader {
   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   static ClassLoader getClassClassLoader(final Class c) {
      return System.getSecurityManager() == null ? c.getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return c.getClassLoader();
         }
      });
   }

   static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }
}
