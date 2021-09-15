package sun.net;

import java.io.FileDescriptor;
import java.net.SocketOption;
import java.security.AccessController;
import jdk.net.NetworkPermission;
import jdk.net.SocketFlow;

public class ExtendedOptionsImpl {
   private ExtendedOptionsImpl() {
   }

   public static void checkSetOptionPermission(SocketOption<?> var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = "setOption." + var0.name();
         var1.checkPermission(new NetworkPermission(var2));
      }
   }

   public static void checkGetOptionPermission(SocketOption<?> var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = "getOption." + var0.name();
         var1.checkPermission(new NetworkPermission(var2));
      }
   }

   public static void checkValueType(Object var0, Class<?> var1) {
      if (!var1.isAssignableFrom(var0.getClass())) {
         String var2 = "Found: " + var0.getClass().toString() + " Expected: " + var1.toString();
         throw new IllegalArgumentException(var2);
      }
   }

   private static native void init();

   public static native void setFlowOption(FileDescriptor var0, SocketFlow var1);

   public static native void getFlowOption(FileDescriptor var0, SocketFlow var1);

   public static native boolean flowSupported();

   static {
      AccessController.doPrivileged(() -> {
         System.loadLibrary("net");
         return null;
      });
      init();
   }
}
