package com.sun.xml.internal.bind;

import java.util.logging.Logger;

public final class Util {
   private Util() {
   }

   public static Logger getClassLogger() {
      try {
         StackTraceElement[] trace = (new Exception()).getStackTrace();
         return Logger.getLogger(trace[1].getClassName());
      } catch (SecurityException var1) {
         return Logger.getLogger("com.sun.xml.internal.bind");
      }
   }

   public static String getSystemProperty(String name) {
      try {
         return System.getProperty(name);
      } catch (SecurityException var2) {
         return null;
      }
   }
}
