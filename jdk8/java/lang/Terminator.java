package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;

class Terminator {
   private static SignalHandler handler = null;

   static void setup() {
      if (handler == null) {
         SignalHandler var0 = new SignalHandler() {
            public void handle(Signal var1) {
               Shutdown.exit(var1.getNumber() + 128);
            }
         };
         handler = var0;

         try {
            Signal.handle(new Signal("HUP"), var0);
         } catch (IllegalArgumentException var4) {
         }

         try {
            Signal.handle(new Signal("INT"), var0);
         } catch (IllegalArgumentException var3) {
         }

         try {
            Signal.handle(new Signal("TERM"), var0);
         } catch (IllegalArgumentException var2) {
         }

      }
   }

   static void teardown() {
   }
}
