package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class ServerSocketChannel extends AbstractSelectableChannel implements NetworkChannel {
   protected ServerSocketChannel(SelectorProvider var1) {
      super(var1);
   }

   public static ServerSocketChannel open() throws IOException {
      return SelectorProvider.provider().openServerSocketChannel();
   }

   public final int validOps() {
      return 16;
   }

   public final ServerSocketChannel bind(SocketAddress var1) throws IOException {
      return this.bind(var1, 0);
   }

   public abstract ServerSocketChannel bind(SocketAddress var1, int var2) throws IOException;

   public abstract <T> ServerSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   public abstract ServerSocket socket();

   public abstract SocketChannel accept() throws IOException;

   public abstract SocketAddress getLocalAddress() throws IOException;
}
