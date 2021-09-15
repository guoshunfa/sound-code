package com.sun.corba.se.impl.orbutil.concurrent;

public class SyncUtil {
   private SyncUtil() {
   }

   public static void acquire(Sync var0) {
      boolean var1 = false;

      while(!var1) {
         try {
            var0.acquire();
            var1 = true;
         } catch (InterruptedException var3) {
            var1 = false;
         }
      }

   }
}
