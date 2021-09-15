package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public final class Secrets {
   private Secrets() {
   }

   private static SelectorProvider provider() {
      SelectorProvider var0 = SelectorProvider.provider();
      if (!(var0 instanceof SelectorProviderImpl)) {
         throw new UnsupportedOperationException();
      } else {
         return var0;
      }
   }

   public static SocketChannel newSocketChannel(FileDescriptor var0) {
      try {
         return new SocketChannelImpl(provider(), var0, false);
      } catch (IOException var2) {
         throw new AssertionError(var2);
      }
   }

   public static ServerSocketChannel newServerSocketChannel(FileDescriptor var0) {
      try {
         return new ServerSocketChannelImpl(provider(), var0, false);
      } catch (IOException var2) {
         throw new AssertionError(var2);
      }
   }
}
