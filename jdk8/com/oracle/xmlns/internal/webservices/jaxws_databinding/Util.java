package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import com.sun.xml.internal.ws.model.RuntimeModelerException;

class Util {
   static String nullSafe(String value) {
      return value == null ? "" : value;
   }

   static <T> T nullSafe(T value, T defaultValue) {
      return value == null ? defaultValue : value;
   }

   static <T extends Enum> T nullSafe(Enum value, T defaultValue) {
      return value == null ? defaultValue : Enum.valueOf(defaultValue.getClass(), value.toString());
   }

   public static Class<?> findClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException var2) {
         throw new RuntimeModelerException("runtime.modeler.external.metadata.generic", new Object[]{var2});
      }
   }
}
