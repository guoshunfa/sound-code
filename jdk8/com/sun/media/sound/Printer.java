package com.sun.media.sound;

final class Printer {
   static final boolean err = false;
   static final boolean debug = false;
   static final boolean trace = false;
   static final boolean verbose = false;
   static final boolean release = false;
   static final boolean SHOW_THREADID = false;
   static final boolean SHOW_TIMESTAMP = false;
   private static long startTime = 0L;

   private Printer() {
   }

   public static void err(String var0) {
   }

   public static void debug(String var0) {
   }

   public static void trace(String var0) {
   }

   public static void verbose(String var0) {
   }

   public static void release(String var0) {
   }

   public static void println(String var0) {
      String var1 = "";
      System.out.println(var1 + var0);
   }

   public static void println() {
      System.out.println();
   }
}
