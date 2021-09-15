package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Future;

public abstract class AsynchronousServerSocketChannel implements AsynchronousChannel, NetworkChannel {
   private final AsynchronousChannelProvider provider;

   protected AsynchronousServerSocketChannel(AsynchronousChannelProvider var1) {
      this.provider = var1;
   }

   public final AsynchronousChannelProvider provider() {
      return this.provider;
   }

   public static AsynchronousServerSocketChannel open(AsynchronousChannelGroup var0) throws IOException {
      AsynchronousChannelProvider var1 = var0 == null ? AsynchronousChannelProvider.provider() : var0.provider();
      return var1.openAsynchronousServerSocketChannel(var0);
   }

   public static AsynchronousServerSocketChannel open() throws IOException {
      return open((AsynchronousChannelGroup)null);
   }

   public final AsynchronousServerSocketChannel bind(SocketAddress var1) throws IOException {
      return this.bind(var1, 0);
   }

   public abstract AsynchronousServerSocketChannel bind(SocketAddress var1, int var2) throws IOException;

   public abstract <T> AsynchronousServerSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   public abstract <A> void accept(A var1, CompletionHandler<AsynchronousSocketChannel, ? super A> var2);

   public abstract Future<AsynchronousSocketChannel> accept();

   public abstract SocketAddress getLocalAddress() throws IOException;
}
