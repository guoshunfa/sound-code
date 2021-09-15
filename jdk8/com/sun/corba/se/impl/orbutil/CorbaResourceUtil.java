package com.sun.corba.se.impl.orbutil;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CorbaResourceUtil {
   private static boolean resourcesInitialized = false;
   private static ResourceBundle resources;

   public static String getString(String var0) {
      if (!resourcesInitialized) {
         initResources();
      }

      try {
         return resources.getString(var0);
      } catch (MissingResourceException var2) {
         return null;
      }
   }

   public static String getText(String var0) {
      String var1 = getString(var0);
      if (var1 == null) {
         var1 = "no text found: \"" + var0 + "\"";
      }

      return var1;
   }

   public static String getText(String var0, int var1) {
      return getText(var0, Integer.toString(var1), (String)null, (String)null);
   }

   public static String getText(String var0, String var1) {
      return getText(var0, var1, (String)null, (String)null);
   }

   public static String getText(String var0, String var1, String var2) {
      return getText(var0, var1, var2, (String)null);
   }

   public static String getText(String var0, String var1, String var2, String var3) {
      String var4 = getString(var0);
      if (var4 == null) {
         var4 = "no text found: key = \"" + var0 + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
      }

      String[] var5 = new String[]{var1 != null ? var1.toString() : "null", var2 != null ? var2.toString() : "null", var3 != null ? var3.toString() : "null"};
      return MessageFormat.format(var4, (Object[])var5);
   }

   private static void initResources() {
      try {
         resources = ResourceBundle.getBundle("com.sun.corba.se.impl.orbutil.resources.sunorb");
         resourcesInitialized = true;
      } catch (MissingResourceException var1) {
         throw new Error("fatal: missing resource bundle: " + var1.getClassName());
      }
   }
}
