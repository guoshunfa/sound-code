package sun.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import sun.net.sdp.SdpProvider;

public final class NetHooks {
   private static final NetHooks.Provider provider = new SdpProvider();

   public static void beforeTcpBind(FileDescriptor var0, InetAddress var1, int var2) throws IOException {
      provider.implBeforeTcpBind(var0, var1, var2);
   }

   public static void beforeTcpConnect(FileDescriptor var0, InetAddress var1, int var2) throws IOException {
      provider.implBeforeTcpConnect(var0, var1, var2);
   }

   public abstract static class Provider {
      protected Provider() {
      }

      public abstract void implBeforeTcpBind(FileDescriptor var1, InetAddress var2, int var3) throws IOException;

      public abstract void implBeforeTcpConnect(FileDescriptor var1, InetAddress var2, int var3) throws IOException;
   }
}
