package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.Properties;

final class PropUtil {
   private PropUtil() {
   }

   public static boolean getBooleanSystemProperty(String name, boolean def) {
      try {
         return getBoolean(getProp(System.getProperties(), name), def);
      } catch (SecurityException var4) {
         try {
            String value = System.getProperty(name);
            if (value == null) {
               return def;
            } else if (def) {
               return !value.equalsIgnoreCase("false");
            } else {
               return value.equalsIgnoreCase("true");
            }
         } catch (SecurityException var3) {
            return def;
         }
      }
   }

   private static Object getProp(Properties props, String name) {
      Object val = props.get(name);
      return val != null ? val : props.getProperty(name);
   }

   private static boolean getBoolean(Object value, boolean def) {
      if (value == null) {
         return def;
      } else if (value instanceof String) {
         if (def) {
            return !((String)value).equalsIgnoreCase("false");
         } else {
            return ((String)value).equalsIgnoreCase("true");
         }
      } else {
         return value instanceof Boolean ? (Boolean)value : def;
      }
   }
}
