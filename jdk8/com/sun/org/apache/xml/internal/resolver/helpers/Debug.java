package com.sun.org.apache.xml.internal.resolver.helpers;

public class Debug {
   protected int debug = 0;

   public void setDebug(int newDebug) {
      this.debug = newDebug;
   }

   public int getDebug() {
      return this.debug;
   }

   public void message(int level, String message) {
      if (this.debug >= level) {
         System.out.println(message);
      }

   }

   public void message(int level, String message, String spec) {
      if (this.debug >= level) {
         System.out.println(message + ": " + spec);
      }

   }

   public void message(int level, String message, String spec1, String spec2) {
      if (this.debug >= level) {
         System.out.println(message + ": " + spec1);
         System.out.println("\t" + spec2);
      }

   }
}
