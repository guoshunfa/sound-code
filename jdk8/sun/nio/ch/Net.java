package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import jdk.net.NetworkPermission;
import jdk.net.SocketFlow;
import sun.net.ExtendedOptionsImpl;

public class Net {
   static final ProtocolFamily UNSPEC = new ProtocolFamily() {
      public String name() {
         return "UNSPEC";
      }
   };
   private static final boolean exclusiveBind;
   private static final boolean fastLoopback;
   private static volatile boolean checkedIPv6 = false;
   private static volatile boolean isIPv6Available;
   public static final int SHUT_RD = 0;
   public static final int SHUT_WR = 1;
   public static final int SHUT_RDWR = 2;
   public static final short POLLIN;
   public static final short POLLOUT;
   public static final short POLLERR;
   public static final short POLLHUP;
   public static final short POLLNVAL;
   public static final short POLLCONN;

   private Net() {
   }

   static boolean isIPv6Available() {
      if (!checkedIPv6) {
         isIPv6Available = isIPv6Available0();
         checkedIPv6 = true;
      }

      return isIPv6Available;
   }

   static boolean useExclusiveBind() {
      return exclusiveBind;
   }

   static boolean canIPv6SocketJoinIPv4Group() {
      return canIPv6SocketJoinIPv4Group0();
   }

   static boolean canJoin6WithIPv4Group() {
      return canJoin6WithIPv4Group0();
   }

   public static InetSocketAddress checkAddress(SocketAddress var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (!(var0 instanceof InetSocketAddress)) {
         throw new UnsupportedAddressTypeException();
      } else {
         InetSocketAddress var1 = (InetSocketAddress)var0;
         if (var1.isUnresolved()) {
            throw new UnresolvedAddressException();
         } else {
            InetAddress var2 = var1.getAddress();
            if (!(var2 instanceof Inet4Address) && !(var2 instanceof Inet6Address)) {
               throw new IllegalArgumentException("Invalid address type");
            } else {
               return var1;
            }
         }
      }
   }

   static InetSocketAddress asInetSocketAddress(SocketAddress var0) {
      if (!(var0 instanceof InetSocketAddress)) {
         throw new UnsupportedAddressTypeException();
      } else {
         return (InetSocketAddress)var0;
      }
   }

   static void translateToSocketException(Exception var0) throws SocketException {
      if (var0 instanceof SocketException) {
         throw (SocketException)var0;
      } else {
         Object var1 = var0;
         if (var0 instanceof ClosedChannelException) {
            var1 = new SocketException("Socket is closed");
         } else if (var0 instanceof NotYetConnectedException) {
            var1 = new SocketException("Socket is not connected");
         } else if (var0 instanceof AlreadyBoundException) {
            var1 = new SocketException("Already bound");
         } else if (var0 instanceof NotYetBoundException) {
            var1 = new SocketException("Socket is not bound yet");
         } else if (var0 instanceof UnsupportedAddressTypeException) {
            var1 = new SocketException("Unsupported address type");
         } else if (var0 instanceof UnresolvedAddressException) {
            var1 = new SocketException("Unresolved address");
         }

         if (var1 != var0) {
            ((Exception)var1).initCause(var0);
         }

         if (var1 instanceof SocketException) {
            throw (SocketException)var1;
         } else if (var1 instanceof RuntimeException) {
            throw (RuntimeException)var1;
         } else {
            throw new Error("Untranslated exception", (Throwable)var1);
         }
      }
   }

   static void translateException(Exception var0, boolean var1) throws IOException {
      if (var0 instanceof IOException) {
         throw (IOException)var0;
      } else if (var1 && var0 instanceof UnresolvedAddressException) {
         throw new UnknownHostException();
      } else {
         translateToSocketException(var0);
      }
   }

   static void translateException(Exception var0) throws IOException {
      translateException(var0, false);
   }

   static InetSocketAddress getRevealedLocalAddress(InetSocketAddress var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var0 != null && var1 != null) {
         try {
            var1.checkConnect(var0.getAddress().getHostAddress(), -1);
         } catch (SecurityException var3) {
            var0 = getLoopbackAddress(var0.getPort());
         }

         return var0;
      } else {
         return var0;
      }
   }

   static String getRevealedLocalAddressAsString(InetSocketAddress var0) {
      return System.getSecurityManager() == null ? var0.toString() : getLoopbackAddress(var0.getPort()).toString();
   }

   private static InetSocketAddress getLoopbackAddress(int var0) {
      return new InetSocketAddress(InetAddress.getLoopbackAddress(), var0);
   }

   static Inet4Address anyInet4Address(final NetworkInterface var0) {
      return (Inet4Address)AccessController.doPrivileged(new PrivilegedAction<Inet4Address>() {
         public Inet4Address run() {
            Enumeration var1 = var0.getInetAddresses();

            InetAddress var2;
            do {
               if (!var1.hasMoreElements()) {
                  return null;
               }

               var2 = (InetAddress)var1.nextElement();
            } while(!(var2 instanceof Inet4Address));

            return (Inet4Address)var2;
         }
      });
   }

   static int inet4AsInt(InetAddress var0) {
      if (var0 instanceof Inet4Address) {
         byte[] var1 = var0.getAddress();
         int var2 = var1[3] & 255;
         var2 |= var1[2] << 8 & '\uff00';
         var2 |= var1[1] << 16 & 16711680;
         var2 |= var1[0] << 24 & -16777216;
         return var2;
      } else {
         throw new AssertionError("Should not reach here");
      }
   }

   static InetAddress inet4FromInt(int var0) {
      byte[] var1 = new byte[]{(byte)(var0 >>> 24 & 255), (byte)(var0 >>> 16 & 255), (byte)(var0 >>> 8 & 255), (byte)(var0 & 255)};

      try {
         return InetAddress.getByAddress(var1);
      } catch (UnknownHostException var3) {
         throw new AssertionError("Should not reach here");
      }
   }

   static byte[] inet6AsByteArray(InetAddress var0) {
      if (var0 instanceof Inet6Address) {
         return var0.getAddress();
      } else if (var0 instanceof Inet4Address) {
         byte[] var1 = var0.getAddress();
         byte[] var2 = new byte[16];
         var2[10] = -1;
         var2[11] = -1;
         var2[12] = var1[0];
         var2[13] = var1[1];
         var2[14] = var1[2];
         var2[15] = var1[3];
         return var2;
      } else {
         throw new AssertionError("Should not reach here");
      }
   }

   static void setSocketOption(FileDescriptor var0, ProtocolFamily var1, SocketOption<?> var2, Object var3) throws IOException {
      if (var3 == null) {
         throw new IllegalArgumentException("Invalid option value");
      } else {
         Class var4 = var2.type();
         if (var4 == SocketFlow.class) {
            SecurityManager var10 = System.getSecurityManager();
            if (var10 != null) {
               var10.checkPermission(new NetworkPermission("setOption.SO_FLOW_SLA"));
            }

            ExtendedOptionsImpl.setFlowOption(var0, (SocketFlow)var3);
         } else if (var4 != Integer.class && var4 != Boolean.class) {
            throw new AssertionError("Should not reach here");
         } else {
            int var5;
            if (var2 == StandardSocketOptions.SO_RCVBUF || var2 == StandardSocketOptions.SO_SNDBUF) {
               var5 = (Integer)var3;
               if (var5 < 0) {
                  throw new IllegalArgumentException("Invalid send/receive buffer size");
               }
            }

            if (var2 == StandardSocketOptions.SO_LINGER) {
               var5 = (Integer)var3;
               if (var5 < 0) {
                  var3 = -1;
               }

               if (var5 > 65535) {
                  var3 = 65535;
               }
            }

            if (var2 == StandardSocketOptions.IP_TOS) {
               var5 = (Integer)var3;
               if (var5 < 0 || var5 > 255) {
                  throw new IllegalArgumentException("Invalid IP_TOS value");
               }
            }

            if (var2 == StandardSocketOptions.IP_MULTICAST_TTL) {
               var5 = (Integer)var3;
               if (var5 < 0 || var5 > 255) {
                  throw new IllegalArgumentException("Invalid TTL/hop value");
               }
            }

            OptionKey var9 = SocketOptionRegistry.findOption(var2, var1);
            if (var9 == null) {
               throw new AssertionError("Option not found");
            } else {
               int var6;
               boolean var7;
               if (var4 == Integer.class) {
                  var6 = (Integer)var3;
               } else {
                  var7 = (Boolean)var3;
                  var6 = var7 ? 1 : 0;
               }

               var7 = var1 == UNSPEC;
               boolean var8 = var1 == StandardProtocolFamily.INET6;
               setIntOption0(var0, var7, var9.level(), var9.name(), var6, var8);
            }
         }
      }
   }

   static Object getSocketOption(FileDescriptor var0, ProtocolFamily var1, SocketOption<?> var2) throws IOException {
      Class var3 = var2.type();
      if (var3 == SocketFlow.class) {
         SecurityManager var8 = System.getSecurityManager();
         if (var8 != null) {
            var8.checkPermission(new NetworkPermission("getOption.SO_FLOW_SLA"));
         }

         SocketFlow var7 = SocketFlow.create();
         ExtendedOptionsImpl.getFlowOption(var0, var7);
         return var7;
      } else if (var3 != Integer.class && var3 != Boolean.class) {
         throw new AssertionError("Should not reach here");
      } else {
         OptionKey var4 = SocketOptionRegistry.findOption(var2, var1);
         if (var4 == null) {
            throw new AssertionError("Option not found");
         } else {
            boolean var5 = var1 == UNSPEC;
            int var6 = getIntOption0(var0, var5, var4.level(), var4.name());
            if (var3 == Integer.class) {
               return var6;
            } else {
               return var6 == 0 ? Boolean.FALSE : Boolean.TRUE;
            }
         }
      }
   }

   public static boolean isFastTcpLoopbackRequested() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty("jdk.net.useFastTcpLoopback");
         }
      });
      boolean var1;
      if ("".equals(var0)) {
         var1 = true;
      } else {
         var1 = Boolean.parseBoolean(var0);
      }

      return var1;
   }

   private static native boolean isIPv6Available0();

   private static native int isExclusiveBindAvailable();

   private static native boolean canIPv6SocketJoinIPv4Group0();

   private static native boolean canJoin6WithIPv4Group0();

   static FileDescriptor socket(boolean var0) throws IOException {
      return socket(UNSPEC, var0);
   }

   static FileDescriptor socket(ProtocolFamily var0, boolean var1) throws IOException {
      boolean var2 = isIPv6Available() && var0 != StandardProtocolFamily.INET;
      return IOUtil.newFD(socket0(var2, var1, false, fastLoopback));
   }

   static FileDescriptor serverSocket(boolean var0) {
      return IOUtil.newFD(socket0(isIPv6Available(), var0, true, fastLoopback));
   }

   private static native int socket0(boolean var0, boolean var1, boolean var2, boolean var3);

   public static void bind(FileDescriptor var0, InetAddress var1, int var2) throws IOException {
      bind(UNSPEC, var0, var1, var2);
   }

   static void bind(ProtocolFamily var0, FileDescriptor var1, InetAddress var2, int var3) throws IOException {
      boolean var4 = isIPv6Available() && var0 != StandardProtocolFamily.INET;
      bind0(var1, var4, exclusiveBind, var2, var3);
   }

   private static native void bind0(FileDescriptor var0, boolean var1, boolean var2, InetAddress var3, int var4) throws IOException;

   static native void listen(FileDescriptor var0, int var1) throws IOException;

   static int connect(FileDescriptor var0, InetAddress var1, int var2) throws IOException {
      return connect(UNSPEC, var0, var1, var2);
   }

   static int connect(ProtocolFamily var0, FileDescriptor var1, InetAddress var2, int var3) throws IOException {
      boolean var4 = isIPv6Available() && var0 != StandardProtocolFamily.INET;
      return connect0(var4, var1, var2, var3);
   }

   private static native int connect0(boolean var0, FileDescriptor var1, InetAddress var2, int var3) throws IOException;

   static native void shutdown(FileDescriptor var0, int var1) throws IOException;

   private static native int localPort(FileDescriptor var0) throws IOException;

   private static native InetAddress localInetAddress(FileDescriptor var0) throws IOException;

   public static InetSocketAddress localAddress(FileDescriptor var0) throws IOException {
      return new InetSocketAddress(localInetAddress(var0), localPort(var0));
   }

   private static native int remotePort(FileDescriptor var0) throws IOException;

   private static native InetAddress remoteInetAddress(FileDescriptor var0) throws IOException;

   static InetSocketAddress remoteAddress(FileDescriptor var0) throws IOException {
      return new InetSocketAddress(remoteInetAddress(var0), remotePort(var0));
   }

   private static native int getIntOption0(FileDescriptor var0, boolean var1, int var2, int var3) throws IOException;

   private static native void setIntOption0(FileDescriptor var0, boolean var1, int var2, int var3, int var4, boolean var5) throws IOException;

   static native int poll(FileDescriptor var0, int var1, long var2) throws IOException;

   static int join4(FileDescriptor var0, int var1, int var2, int var3) throws IOException {
      return joinOrDrop4(true, var0, var1, var2, var3);
   }

   static void drop4(FileDescriptor var0, int var1, int var2, int var3) throws IOException {
      joinOrDrop4(false, var0, var1, var2, var3);
   }

   private static native int joinOrDrop4(boolean var0, FileDescriptor var1, int var2, int var3, int var4) throws IOException;

   static int block4(FileDescriptor var0, int var1, int var2, int var3) throws IOException {
      return blockOrUnblock4(true, var0, var1, var2, var3);
   }

   static void unblock4(FileDescriptor var0, int var1, int var2, int var3) throws IOException {
      blockOrUnblock4(false, var0, var1, var2, var3);
   }

   private static native int blockOrUnblock4(boolean var0, FileDescriptor var1, int var2, int var3, int var4) throws IOException;

   static int join6(FileDescriptor var0, byte[] var1, int var2, byte[] var3) throws IOException {
      return joinOrDrop6(true, var0, var1, var2, var3);
   }

   static void drop6(FileDescriptor var0, byte[] var1, int var2, byte[] var3) throws IOException {
      joinOrDrop6(false, var0, var1, var2, var3);
   }

   private static native int joinOrDrop6(boolean var0, FileDescriptor var1, byte[] var2, int var3, byte[] var4) throws IOException;

   static int block6(FileDescriptor var0, byte[] var1, int var2, byte[] var3) throws IOException {
      return blockOrUnblock6(true, var0, var1, var2, var3);
   }

   static void unblock6(FileDescriptor var0, byte[] var1, int var2, byte[] var3) throws IOException {
      blockOrUnblock6(false, var0, var1, var2, var3);
   }

   static native int blockOrUnblock6(boolean var0, FileDescriptor var1, byte[] var2, int var3, byte[] var4) throws IOException;

   static native void setInterface4(FileDescriptor var0, int var1) throws IOException;

   static native int getInterface4(FileDescriptor var0) throws IOException;

   static native void setInterface6(FileDescriptor var0, int var1) throws IOException;

   static native int getInterface6(FileDescriptor var0) throws IOException;

   private static native void initIDs();

   static native short pollinValue();

   static native short polloutValue();

   static native short pollerrValue();

   static native short pollhupValue();

   static native short pollnvalValue();

   static native short pollconnValue();

   static {
      IOUtil.load();
      initIDs();
      POLLIN = pollinValue();
      POLLOUT = polloutValue();
      POLLERR = pollerrValue();
      POLLHUP = pollhupValue();
      POLLNVAL = pollnvalValue();
      POLLCONN = pollconnValue();
      int var0 = isExclusiveBindAvailable();
      if (var0 >= 0) {
         String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("sun.net.useExclusiveBind");
            }
         });
         if (var1 != null) {
            exclusiveBind = var1.length() == 0 ? true : Boolean.parseBoolean(var1);
         } else if (var0 == 1) {
            exclusiveBind = true;
         } else {
            exclusiveBind = false;
         }
      } else {
         exclusiveBind = false;
      }

      fastLoopback = isFastTcpLoopbackRequested();
   }
}
