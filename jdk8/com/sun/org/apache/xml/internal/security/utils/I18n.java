package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.Init;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
   public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
   private static ResourceBundle resourceBundle;
   private static boolean alreadyInitialized = false;

   private I18n() {
   }

   public static String translate(String var0, Object[] var1) {
      return getExceptionMessage(var0, var1);
   }

   public static String translate(String var0) {
      return getExceptionMessage(var0);
   }

   public static String getExceptionMessage(String var0) {
      try {
         return resourceBundle.getString(var0);
      } catch (Throwable var2) {
         return Init.isInitialized() ? "No message with ID \"" + var0 + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"" : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
      }
   }

   public static String getExceptionMessage(String var0, Exception var1) {
      try {
         Object[] var2 = new Object[]{var1.getMessage()};
         return MessageFormat.format(resourceBundle.getString(var0), var2);
      } catch (Throwable var3) {
         return Init.isInitialized() ? "No message with ID \"" + var0 + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\". Original Exception was a " + var1.getClass().getName() + " and message " + var1.getMessage() : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
      }
   }

   public static String getExceptionMessage(String var0, Object[] var1) {
      try {
         return MessageFormat.format(resourceBundle.getString(var0), var1);
      } catch (Throwable var3) {
         return Init.isInitialized() ? "No message with ID \"" + var0 + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"" : "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
      }
   }

   public static synchronized void init(String var0, String var1) {
      if (!alreadyInitialized) {
         resourceBundle = ResourceBundle.getBundle("com/sun/org/apache/xml/internal/security/resource/xmlsecurity", new Locale(var0, var1));
         alreadyInitialized = true;
      }
   }
}
