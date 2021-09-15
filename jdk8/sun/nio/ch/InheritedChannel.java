package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;

class InheritedChannel {
   private static final int UNKNOWN = -1;
   private static final int SOCK_STREAM = 1;
   private static final int SOCK_DGRAM = 2;
   private static final int O_RDONLY = 0;
   private static final int O_WRONLY = 1;
   private static final int O_RDWR = 2;
   private static int devnull = -1;
   private static boolean haveChannel = false;
   private static Channel channel = null;

   private static void detachIOStreams() {
      try {
         dup2(devnull, 0);
         dup2(devnull, 1);
         dup2(devnull, 2);
      } catch (IOException var1) {
         throw new InternalError(var1);
      }
   }

   private static void checkAccess(Channel var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("inheritedChannel"));
      }

   }

   private static Channel createChannel() throws IOException {
      int var0 = dup(0);
      int var1 = soType0(var0);
      if (var1 != 1 && var1 != 2) {
         close0(var0);
         return null;
      } else {
         Class[] var2 = new Class[]{Integer.TYPE};
         Constructor var3 = Reflect.lookupConstructor("java.io.FileDescriptor", var2);
         Object[] var4 = new Object[]{new Integer(var0)};
         FileDescriptor var5 = (FileDescriptor)Reflect.invoke(var3, var4);
         SelectorProvider var6 = SelectorProvider.provider();

         assert var6 instanceof SelectorProviderImpl;

         Object var7;
         if (var1 == 1) {
            InetAddress var8 = peerAddress0(var0);
            if (var8 == null) {
               var7 = new InheritedChannel.InheritedServerSocketChannelImpl(var6, var5);
            } else {
               int var9 = peerPort0(var0);

               assert var9 > 0;

               InetSocketAddress var10 = new InetSocketAddress(var8, var9);
               var7 = new InheritedChannel.InheritedSocketChannelImpl(var6, var5, var10);
            }
         } else {
            var7 = new InheritedChannel.InheritedDatagramChannelImpl(var6, var5);
         }

         return (Channel)var7;
      }
   }

   public static synchronized Channel getChannel() throws IOException {
      if (devnull < 0) {
         devnull = open0("/dev/null", 2);
      }

      if (!haveChannel) {
         channel = createChannel();
         haveChannel = true;
      }

      if (channel != null) {
         checkAccess(channel);
      }

      return channel;
   }

   private static native int dup(int var0) throws IOException;

   private static native void dup2(int var0, int var1) throws IOException;

   private static native int open0(String var0, int var1) throws IOException;

   private static native void close0(int var0) throws IOException;

   private static native int soType0(int var0);

   private static native InetAddress peerAddress0(int var0);

   private static native int peerPort0(int var0);

   static {
      IOUtil.load();
   }

   public static class InheritedDatagramChannelImpl extends DatagramChannelImpl {
      InheritedDatagramChannelImpl(SelectorProvider var1, FileDescriptor var2) throws IOException {
         super(var1, var2);
      }

      protected void implCloseSelectableChannel() throws IOException {
         super.implCloseSelectableChannel();
         InheritedChannel.detachIOStreams();
      }
   }

   public static class InheritedServerSocketChannelImpl extends ServerSocketChannelImpl {
      InheritedServerSocketChannelImpl(SelectorProvider var1, FileDescriptor var2) throws IOException {
         super(var1, var2, true);
      }

      protected void implCloseSelectableChannel() throws IOException {
         super.implCloseSelectableChannel();
         InheritedChannel.detachIOStreams();
      }
   }

   public static class InheritedSocketChannelImpl extends SocketChannelImpl {
      InheritedSocketChannelImpl(SelectorProvider var1, FileDescriptor var2, InetSocketAddress var3) throws IOException {
         super(var1, var2, var3);
      }

      protected void implCloseSelectableChannel() throws IOException {
         super.implCloseSelectableChannel();
         InheritedChannel.detachIOStreams();
      }
   }
}
