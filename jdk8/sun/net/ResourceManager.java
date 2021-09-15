package sun.net;

import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetPropertyAction;

public class ResourceManager {
   private static final int DEFAULT_MAX_SOCKETS = 25;
   private static final int maxSockets;
   private static final AtomicInteger numSockets;

   public static void beforeUdpCreate() throws SocketException {
      if (System.getSecurityManager() != null && numSockets.incrementAndGet() > maxSockets) {
         numSockets.decrementAndGet();
         throw new SocketException("maximum number of DatagramSockets reached");
      }
   }

   public static void afterUdpClose() {
      if (System.getSecurityManager() != null) {
         numSockets.decrementAndGet();
      }

   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.net.maxDatagramSockets")));
      int var1 = 25;

      try {
         if (var0 != null) {
            var1 = Integer.parseInt(var0);
         }
      } catch (NumberFormatException var3) {
      }

      maxSockets = var1;
      numSockets = new AtomicInteger(0);
   }
}
