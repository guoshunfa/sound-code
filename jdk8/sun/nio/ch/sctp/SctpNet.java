package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpSocketOption;
import com.sun.nio.sctp.SctpStandardSocketOptions;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AlreadyBoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import sun.nio.ch.IOUtil;
import sun.nio.ch.Net;
import sun.security.action.GetPropertyAction;

public class SctpNet {
   static final String osName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));

   private static boolean IPv4MappedAddresses() {
      return "SunOS".equals(osName);
   }

   static boolean throwAlreadyBoundException() throws IOException {
      throw new AlreadyBoundException();
   }

   static void listen(int var0, int var1) throws IOException {
      listen0(var0, var1);
   }

   static int connect(int var0, InetAddress var1, int var2) throws IOException {
      return connect0(var0, var1, var2);
   }

   static void close(int var0) throws IOException {
      close0(var0);
   }

   static void preClose(int var0) throws IOException {
      preClose0(var0);
   }

   static FileDescriptor socket(boolean var0) throws IOException {
      int var1 = socket0(var0);
      return IOUtil.newFD(var1);
   }

   static void bindx(int var0, InetAddress[] var1, int var2, boolean var3) throws IOException {
      bindx(var0, var1, var2, var1.length, var3, IPv4MappedAddresses());
   }

   static Set<SocketAddress> getLocalAddresses(int var0) throws IOException {
      Set var1 = null;
      SocketAddress[] var2 = getLocalAddresses0(var0);
      if (var2 != null) {
         var1 = getRevealedLocalAddressSet(var2);
      }

      return var1;
   }

   private static Set<SocketAddress> getRevealedLocalAddressSet(SocketAddress[] var0) {
      SecurityManager var1 = System.getSecurityManager();
      HashSet var2 = new HashSet(var0.length);
      SocketAddress[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SocketAddress var6 = var3[var5];
         var2.add(getRevealedLocalAddress(var6, var1));
      }

      return var2;
   }

   private static SocketAddress getRevealedLocalAddress(SocketAddress var0, SecurityManager var1) {
      if (var1 != null && var0 != null) {
         InetSocketAddress var2 = (InetSocketAddress)var0;

         try {
            var1.checkConnect(var2.getAddress().getHostAddress(), -1);
            return var0;
         } catch (SecurityException var4) {
            return new InetSocketAddress(InetAddress.getLoopbackAddress(), var2.getPort());
         }
      } else {
         return var0;
      }
   }

   static Set<SocketAddress> getRemoteAddresses(int var0, int var1) throws IOException {
      HashSet var2 = null;
      SocketAddress[] var3 = getRemoteAddresses0(var0, var1);
      if (var3 != null) {
         var2 = new HashSet(var3.length);
         SocketAddress[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            SocketAddress var7 = var4[var6];
            var2.add(var7);
         }
      }

      return var2;
   }

   static <T> void setSocketOption(int var0, SctpSocketOption<T> var1, T var2, int var3) throws IOException {
      if (var2 == null) {
         throw new IllegalArgumentException("Invalid option value");
      } else {
         if (var1.equals(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS)) {
            SctpStandardSocketOptions.InitMaxStreams var4 = (SctpStandardSocketOptions.InitMaxStreams)var2;
            setInitMsgOption0(var0, var4.maxInStreams(), var4.maxOutStreams());
         } else if (!var1.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR) && !var1.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR)) {
            if (!var1.equals(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS) && !var1.equals(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE) && !var1.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE) && !var1.equals(SctpStandardSocketOptions.SCTP_NODELAY) && !var1.equals(SctpStandardSocketOptions.SO_SNDBUF) && !var1.equals(SctpStandardSocketOptions.SO_RCVBUF) && !var1.equals(SctpStandardSocketOptions.SO_LINGER)) {
               throw new AssertionError("Unknown socket option");
            }

            setIntOption(var0, var1, var2);
         } else {
            SocketAddress var6 = (SocketAddress)var2;
            if (var6 == null) {
               throw new IllegalArgumentException("Invalid option value");
            }

            Net.checkAddress(var6);
            InetSocketAddress var5 = (InetSocketAddress)var6;
            if (var1.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) {
               setPrimAddrOption0(var0, var3, var5.getAddress(), var5.getPort());
            } else {
               setPeerPrimAddrOption0(var0, var3, var5.getAddress(), var5.getPort(), IPv4MappedAddresses());
            }
         }

      }
   }

   static Object getSocketOption(int var0, SctpSocketOption<?> var1, int var2) throws IOException {
      if (var1.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR)) {
         throw new IllegalArgumentException("SCTP_SET_PEER_PRIMARY_ADDR cannot be retrieved");
      } else if (var1.equals(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS)) {
         int[] var3 = new int[2];
         getInitMsgOption0(var0, var3);
         return SctpStandardSocketOptions.InitMaxStreams.create(var3[0], var3[1]);
      } else if (var1.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) {
         return getPrimAddrOption0(var0, var2);
      } else if (!var1.equals(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS) && !var1.equals(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE) && !var1.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE) && !var1.equals(SctpStandardSocketOptions.SCTP_NODELAY) && !var1.equals(SctpStandardSocketOptions.SO_SNDBUF) && !var1.equals(SctpStandardSocketOptions.SO_RCVBUF) && !var1.equals(SctpStandardSocketOptions.SO_LINGER)) {
         throw new AssertionError("Unknown socket option");
      } else {
         return getIntOption(var0, var1);
      }
   }

   static void setIntOption(int var0, SctpSocketOption<?> var1, Object var2) throws IOException {
      if (var2 == null) {
         throw new IllegalArgumentException("Invalid option value");
      } else {
         Class var3 = var1.type();
         if (var3 != Integer.class && var3 != Boolean.class) {
            throw new AssertionError("Should not reach here");
         } else {
            int var4;
            if (var1 != SctpStandardSocketOptions.SO_RCVBUF && var1 != SctpStandardSocketOptions.SO_SNDBUF) {
               if (var1 == SctpStandardSocketOptions.SO_LINGER) {
                  var4 = (Integer)var2;
                  if (var4 < 0) {
                     var2 = -1;
                  }

                  if (var4 > 65535) {
                     var2 = 65535;
                  }
               } else if (var1.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE)) {
                  var4 = (Integer)var2;
                  if (var4 < 0 || var4 > 2) {
                     throw new IllegalArgumentException("Invalid value for SCTP_FRAGMENT_INTERLEAVE");
                  }
               }
            } else {
               var4 = (Integer)var2;
               if (var4 < 0) {
                  throw new IllegalArgumentException("Invalid send/receive buffer size");
               }
            }

            if (var3 == Integer.class) {
               var4 = (Integer)var2;
            } else {
               boolean var5 = (Boolean)var2;
               var4 = var5 ? 1 : 0;
            }

            setIntOption0(var0, ((SctpStdSocketOption)var1).constValue(), var4);
         }
      }
   }

   static Object getIntOption(int var0, SctpSocketOption<?> var1) throws IOException {
      Class var2 = var1.type();
      if (var2 != Integer.class && var2 != Boolean.class) {
         throw new AssertionError("Should not reach here");
      } else if (!(var1 instanceof SctpStdSocketOption)) {
         throw new AssertionError("Should not reach here");
      } else {
         int var3 = getIntOption0(var0, ((SctpStdSocketOption)var1).constValue());
         if (var2 == Integer.class) {
            return var3;
         } else {
            return var3 == 0 ? Boolean.FALSE : Boolean.TRUE;
         }
      }
   }

   static void shutdown(int var0, int var1) throws IOException {
      shutdown0(var0, var1);
   }

   static FileDescriptor branch(int var0, int var1) throws IOException {
      int var2 = branch0(var0, var1);
      return IOUtil.newFD(var2);
   }

   static native int socket0(boolean var0) throws IOException;

   static native void listen0(int var0, int var1) throws IOException;

   static native int connect0(int var0, InetAddress var1, int var2) throws IOException;

   static native void close0(int var0) throws IOException;

   static native void preClose0(int var0) throws IOException;

   static native void bindx(int var0, InetAddress[] var1, int var2, int var3, boolean var4, boolean var5) throws IOException;

   static native int getIntOption0(int var0, int var1) throws IOException;

   static native void setIntOption0(int var0, int var1, int var2) throws IOException;

   static native SocketAddress[] getLocalAddresses0(int var0) throws IOException;

   static native SocketAddress[] getRemoteAddresses0(int var0, int var1) throws IOException;

   static native int branch0(int var0, int var1) throws IOException;

   static native void setPrimAddrOption0(int var0, int var1, InetAddress var2, int var3) throws IOException;

   static native void setPeerPrimAddrOption0(int var0, int var1, InetAddress var2, int var3, boolean var4) throws IOException;

   static native SocketAddress getPrimAddrOption0(int var0, int var1) throws IOException;

   static native void getInitMsgOption0(int var0, int[] var1) throws IOException;

   static native void setInitMsgOption0(int var0, int var1, int var2) throws IOException;

   static native void shutdown0(int var0, int var1);

   static native void init();

   static {
      init();
   }
}
