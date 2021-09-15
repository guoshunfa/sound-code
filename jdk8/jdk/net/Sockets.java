package jdk.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import jdk.Exported;
import sun.net.ExtendedOptionsImpl;

@Exported
public class Sockets {
   private static final HashMap<Class<?>, Set<SocketOption<?>>> options = new HashMap();
   private static Method siSetOption;
   private static Method siGetOption;
   private static Method dsiSetOption;
   private static Method dsiGetOption;

   private static void initMethods() {
      try {
         Class var0 = Class.forName("java.net.SocketSecrets");
         siSetOption = var0.getDeclaredMethod("setOption", Object.class, SocketOption.class, Object.class);
         siSetOption.setAccessible(true);
         siGetOption = var0.getDeclaredMethod("getOption", Object.class, SocketOption.class);
         siGetOption.setAccessible(true);
         dsiSetOption = var0.getDeclaredMethod("setOption", DatagramSocket.class, SocketOption.class, Object.class);
         dsiSetOption.setAccessible(true);
         dsiGetOption = var0.getDeclaredMethod("getOption", DatagramSocket.class, SocketOption.class);
         dsiGetOption.setAccessible(true);
      } catch (ReflectiveOperationException var1) {
         throw new InternalError(var1);
      }
   }

   private static <T> void invokeSet(Method var0, Object var1, SocketOption<T> var2, T var3) throws IOException {
      try {
         var0.invoke((Object)null, var1, var2, var3);
      } catch (Exception var6) {
         if (var6 instanceof InvocationTargetException) {
            Throwable var5 = ((InvocationTargetException)var6).getTargetException();
            if (var5 instanceof IOException) {
               throw (IOException)var5;
            }

            if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            }
         }

         throw new RuntimeException(var6);
      }
   }

   private static <T> T invokeGet(Method var0, Object var1, SocketOption<T> var2) throws IOException {
      try {
         return var0.invoke((Object)null, var1, var2);
      } catch (Exception var5) {
         if (var5 instanceof InvocationTargetException) {
            Throwable var4 = ((InvocationTargetException)var5).getTargetException();
            if (var4 instanceof IOException) {
               throw (IOException)var4;
            }

            if (var4 instanceof RuntimeException) {
               throw (RuntimeException)var4;
            }
         }

         throw new RuntimeException(var5);
      }
   }

   private Sockets() {
   }

   public static <T> void setOption(Socket var0, SocketOption<T> var1, T var2) throws IOException {
      if (!isSupported(Socket.class, var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         invokeSet(siSetOption, var0, var1, var2);
      }
   }

   public static <T> T getOption(Socket var0, SocketOption<T> var1) throws IOException {
      if (!isSupported(Socket.class, var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         return invokeGet(siGetOption, var0, var1);
      }
   }

   public static <T> void setOption(ServerSocket var0, SocketOption<T> var1, T var2) throws IOException {
      if (!isSupported(ServerSocket.class, var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         invokeSet(siSetOption, var0, var1, var2);
      }
   }

   public static <T> T getOption(ServerSocket var0, SocketOption<T> var1) throws IOException {
      if (!isSupported(ServerSocket.class, var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         return invokeGet(siGetOption, var0, var1);
      }
   }

   public static <T> void setOption(DatagramSocket var0, SocketOption<T> var1, T var2) throws IOException {
      if (!isSupported(var0.getClass(), var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         invokeSet(dsiSetOption, var0, var1, var2);
      }
   }

   public static <T> T getOption(DatagramSocket var0, SocketOption<T> var1) throws IOException {
      if (!isSupported(var0.getClass(), var1)) {
         throw new UnsupportedOperationException(var1.name());
      } else {
         return invokeGet(dsiGetOption, var0, var1);
      }
   }

   public static Set<SocketOption<?>> supportedOptions(Class<?> var0) {
      Set var1 = (Set)options.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("unknown socket type");
      } else {
         return var1;
      }
   }

   private static boolean isSupported(Class<?> var0, SocketOption<?> var1) {
      Set var2 = supportedOptions(var0);
      return var2.contains(var1);
   }

   private static void initOptionSets() {
      boolean var0 = ExtendedOptionsImpl.flowSupported();
      HashSet var1 = new HashSet();
      var1.add(StandardSocketOptions.SO_KEEPALIVE);
      var1.add(StandardSocketOptions.SO_SNDBUF);
      var1.add(StandardSocketOptions.SO_RCVBUF);
      var1.add(StandardSocketOptions.SO_REUSEADDR);
      var1.add(StandardSocketOptions.SO_LINGER);
      var1.add(StandardSocketOptions.IP_TOS);
      var1.add(StandardSocketOptions.TCP_NODELAY);
      if (var0) {
         var1.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }

      Set var2 = Collections.unmodifiableSet(var1);
      options.put(Socket.class, var2);
      var1 = new HashSet();
      var1.add(StandardSocketOptions.SO_RCVBUF);
      var1.add(StandardSocketOptions.SO_REUSEADDR);
      var1.add(StandardSocketOptions.IP_TOS);
      var2 = Collections.unmodifiableSet(var1);
      options.put(ServerSocket.class, var2);
      var1 = new HashSet();
      var1.add(StandardSocketOptions.SO_SNDBUF);
      var1.add(StandardSocketOptions.SO_RCVBUF);
      var1.add(StandardSocketOptions.SO_REUSEADDR);
      var1.add(StandardSocketOptions.IP_TOS);
      if (var0) {
         var1.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }

      var2 = Collections.unmodifiableSet(var1);
      options.put(DatagramSocket.class, var2);
      var1 = new HashSet();
      var1.add(StandardSocketOptions.SO_SNDBUF);
      var1.add(StandardSocketOptions.SO_RCVBUF);
      var1.add(StandardSocketOptions.SO_REUSEADDR);
      var1.add(StandardSocketOptions.IP_TOS);
      var1.add(StandardSocketOptions.IP_MULTICAST_IF);
      var1.add(StandardSocketOptions.IP_MULTICAST_TTL);
      var1.add(StandardSocketOptions.IP_MULTICAST_LOOP);
      if (var0) {
         var1.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }

      var2 = Collections.unmodifiableSet(var1);
      options.put(MulticastSocket.class, var2);
   }

   static {
      initOptionSets();
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Sockets.initMethods();
            return null;
         }
      });
   }
}
