package com.sun.corba.se.impl.util;

public final class PackagePrefixChecker {
   private static final String PACKAGE_PREFIX = "org.omg.stub.";

   public static String packagePrefix() {
      return "org.omg.stub.";
   }

   public static String correctPackageName(String var0) {
      if (var0 == null) {
         return var0;
      } else {
         return hasOffendingPrefix(var0) ? "org.omg.stub." + var0 : var0;
      }
   }

   public static boolean isOffendingPackage(String var0) {
      return var0 != null && hasOffendingPrefix(var0);
   }

   public static boolean hasOffendingPrefix(String var0) {
      return var0.startsWith("java.") || var0.equals("java") || var0.startsWith("net.jini.") || var0.equals("net.jini") || var0.startsWith("jini.") || var0.equals("jini") || var0.startsWith("javax.") || var0.equals("javax");
   }

   public static boolean hasBeenPrefixed(String var0) {
      return var0.startsWith(packagePrefix());
   }

   public static String withoutPackagePrefix(String var0) {
      return hasBeenPrefixed(var0) ? var0.substring(packagePrefix().length()) : var0;
   }
}
