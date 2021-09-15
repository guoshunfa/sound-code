package java.net;

import java.io.IOException;
import java.util.Enumeration;

class DefaultInterface {
   private static final NetworkInterface defaultInterface = chooseDefaultInterface();

   static NetworkInterface getDefault() {
      return defaultInterface;
   }

   private static NetworkInterface chooseDefaultInterface() {
      Enumeration var0;
      try {
         var0 = NetworkInterface.getNetworkInterfaces();
      } catch (IOException var7) {
         return null;
      }

      NetworkInterface var1 = null;
      NetworkInterface var2 = null;

      while(var0.hasMoreElements()) {
         NetworkInterface var3 = (NetworkInterface)var0.nextElement();

         try {
            if (var3.isUp() && var3.supportsMulticast()) {
               boolean var4 = var3.isLoopback();
               boolean var5 = var3.isPointToPoint();
               if (!var4 && !var5) {
                  return var3;
               }

               if (var1 == null && var5) {
                  var1 = var3;
               }

               if (var2 == null && var4) {
                  var2 = var3;
               }
            }
         } catch (IOException var6) {
         }
      }

      return var1 != null ? var1 : var2;
   }
}
