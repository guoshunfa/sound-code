package sun.nio.ch;

import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class DefaultAsynchronousChannelProvider {
   private DefaultAsynchronousChannelProvider() {
   }

   private static AsynchronousChannelProvider createProvider(String var0) {
      Class var1;
      try {
         var1 = Class.forName(var0);
      } catch (ClassNotFoundException var4) {
         throw new AssertionError(var4);
      }

      try {
         return (AsynchronousChannelProvider)var1.newInstance();
      } catch (InstantiationException | IllegalAccessException var3) {
         throw new AssertionError(var3);
      }
   }

   public static AsynchronousChannelProvider create() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
      if (var0.equals("SunOS")) {
         return createProvider("sun.nio.ch.SolarisAsynchronousChannelProvider");
      } else if (var0.equals("Linux")) {
         return createProvider("sun.nio.ch.LinuxAsynchronousChannelProvider");
      } else if (var0.contains("OS X")) {
         return createProvider("sun.nio.ch.BsdAsynchronousChannelProvider");
      } else if (var0.equals("AIX")) {
         return createProvider("sun.nio.ch.AixAsynchronousChannelProvider");
      } else {
         throw new InternalError("platform not recognized");
      }
   }
}
