package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class JDKBridge {
   private static final String LOCAL_CODEBASE_KEY = "java.rmi.server.codebase";
   private static final String USE_CODEBASE_ONLY_KEY = "java.rmi.server.useCodebaseOnly";
   private static String localCodebase = null;
   private static boolean useCodebaseOnly;

   public static String getLocalCodebase() {
      return localCodebase;
   }

   public static boolean useCodebaseOnly() {
      return useCodebaseOnly;
   }

   public static Class loadClass(String var0, String var1, ClassLoader var2) throws ClassNotFoundException {
      if (var2 == null) {
         return loadClassM(var0, var1, useCodebaseOnly);
      } else {
         try {
            return loadClassM(var0, var1, useCodebaseOnly);
         } catch (ClassNotFoundException var4) {
            return var2.loadClass(var0);
         }
      }
   }

   public static Class loadClass(String var0, String var1) throws ClassNotFoundException {
      return loadClass(var0, var1, (ClassLoader)null);
   }

   public static Class loadClass(String var0) throws ClassNotFoundException {
      return loadClass(var0, (String)null, (ClassLoader)null);
   }

   public static final void main(String[] var0) {
      System.out.println("1.2 VM");
   }

   public static synchronized void setCodebaseProperties() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.codebase")));
      if (var0 != null && var0.trim().length() > 0) {
         localCodebase = var0;
      }

      var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.useCodebaseOnly")));
      if (var0 != null && var0.trim().length() > 0) {
         useCodebaseOnly = Boolean.valueOf(var0);
      }

   }

   public static synchronized void setLocalCodebase(String var0) {
      localCodebase = var0;
   }

   private static Class loadClassM(String var0, String var1, boolean var2) throws ClassNotFoundException {
      try {
         return JDKClassLoader.loadClass((Class)null, var0);
      } catch (ClassNotFoundException var5) {
         try {
            return !var2 && var1 != null ? RMIClassLoader.loadClass(var1, var0) : RMIClassLoader.loadClass(var0);
         } catch (MalformedURLException var4) {
            var0 = var0 + ": " + var4.toString();
            throw new ClassNotFoundException(var0);
         }
      }
   }

   static {
      setCodebaseProperties();
   }
}
