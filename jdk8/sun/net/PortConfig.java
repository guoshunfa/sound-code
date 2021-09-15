package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PortConfig {
   private static int defaultUpper;
   private static int defaultLower;
   private static final int upper;
   private static final int lower;

   private PortConfig() {
   }

   static native int getLower0();

   static native int getUpper0();

   public static int getLower() {
      return lower;
   }

   public static int getUpper() {
      return upper;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            String var1 = System.getProperty("os.name");
            if (var1.startsWith("Linux")) {
               PortConfig.defaultLower = 32768;
               PortConfig.defaultUpper = 61000;
            } else if (var1.startsWith("SunOS")) {
               PortConfig.defaultLower = 32768;
               PortConfig.defaultUpper = 65535;
            } else if (var1.contains("OS X")) {
               PortConfig.defaultLower = 49152;
               PortConfig.defaultUpper = 65535;
            } else {
               if (!var1.startsWith("AIX")) {
                  throw new InternalError("sun.net.PortConfig: unknown OS");
               }

               PortConfig.defaultLower = 32768;
               PortConfig.defaultUpper = 65535;
            }

            return null;
         }
      });
      int var0 = getLower0();
      if (var0 == -1) {
         var0 = defaultLower;
      }

      lower = var0;
      var0 = getUpper0();
      if (var0 == -1) {
         var0 = defaultUpper;
      }

      upper = var0;
   }
}
