package java.util;

import java.security.AccessController;
import sun.util.logging.PlatformLogger;

final class Tripwire {
   private static final String TRIPWIRE_PROPERTY = "org.openjdk.java.util.stream.tripwire";
   static final boolean ENABLED = (Boolean)AccessController.doPrivileged(() -> {
      return Boolean.getBoolean("org.openjdk.java.util.stream.tripwire");
   });

   private Tripwire() {
   }

   static void trip(Class<?> var0, String var1) {
      PlatformLogger.getLogger(var0.getName()).warning(var1, var0.getName());
   }
}
