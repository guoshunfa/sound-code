package com.sun.management.jmx;

import java.io.IOException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/** @deprecated */
@Deprecated
public class Trace {
   public static final int LEVEL_TRACE = 1;
   public static final int LEVEL_DEBUG = 2;
   public static final int INFO_MBEANSERVER = 1;
   public static final int INFO_MLET = 2;
   public static final int INFO_MONITOR = 4;
   public static final int INFO_TIMER = 8;
   public static final int INFO_ADAPTOR_HTML = 16;
   public static final int INFO_MISC = 32;
   public static final int INFO_RELATION = 64;
   public static final int INFO_MODELMBEAN = 128;
   public static final int INFO_ALL = 255;
   protected static final String UNKOWNTYPE = "Unknown type";

   public static boolean isSelected(int var0, int var1) {
      return false;
   }

   public static void parseTraceProperties() throws IOException {
   }

   public static boolean send(int var0, int var1, String var2, String var3, String var4) {
      return false;
   }

   public static boolean send(int var0, int var1, String var2, String var3, Throwable var4) {
      return false;
   }

   public static void addNotificationListener(NotificationListener var0, NotificationFilter var1, Object var2) throws IllegalArgumentException {
   }

   public static void addNotificationListener(TraceListener var0, Object var1) throws IllegalArgumentException {
   }

   public static void removeNotificationListener(NotificationListener var0) {
   }

   public static void removeAllListeners() {
   }

   protected static String getRIType(int var0) {
      return getType(var0);
   }

   static String getType(int var0) {
      return "Unknown type";
   }

   static String getLevel(int var0) {
      return "Unknown level";
   }
}
