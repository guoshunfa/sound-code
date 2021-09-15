package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AsynchronousSocketChannel implements AsynchronousByteChannel, NetworkChannel {
   private final AsynchronousChannelProvider provider;

   protected AsynchronousSocketChannel(AsynchronousChannelProvider var1) {
      this.provider = var1;
   }

   public final AsynchronousChannelProvider provider() {
      return this.provider;
   }

   public static AsynchronousSocketChannel open(AsynchronousChannelGroup var0) throws IOException {
      AsynchronousChannelProvider var1 = var0 == null ? AsynchronousChannelProvider.provider() : var0.provider();
      return var1.openAsynchronousSocketChannel(var0);
   }

   public static AsynchronousSocketChannel open() throws IOException {
      return open((AsynchronousChannelGroup)null);
   }

   public abstract AsynchronousSocketChannel bind(SocketAddress var1) throws IOException;

   public abstract <T> AsynchronousSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   public abstract AsynchronousSocketChannel shutdownInput() throws IOException;

   public abstract AsynchronousSocketChannel shutdownOutput() throws IOException;

   public abstract SocketAddress getRemoteAddress() throws IOException;

   public abstract <A> void connect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3);

   public abstract Future<Void> connect(SocketAddress var1);

   public abstract <A> void read(ByteBuffer var1, long var2, TimeUnit var4, A var5, CompletionHandler<Integer, ? super A> var6);

   public final <A> void read(ByteBuffer var1, A var2, CompletionHandler<Integer, ? super A> var3) {
      this.read(var1, 0L, TimeUnit.MILLISECONDS, var2, var3);
   }

   public abstract Future<Integer> read(ByteBuffer var1);

   public abstract <A> void read(ByteBuffer[] var1, int var2, int var3, long var4, TimeUnit var6, A var7, CompletionHandler<Long, ? super A> var8);

   public abstract <A> void write(ByteBuffer var1, long var2, TimeUnit var4, A var5, CompletionHandler<Integer, ? super A> var6);

   public final <A> void write(ByteBuffer var1, A var2, CompletionHandler<Integer, ? super A> var3) {
      this.write(var1, 0L, TimeUnit.MILLISECONDS, var2, var3);
   }

   public abstract Future<Integer> write(ByteBuffer var1);

   public abstract <A> void write(ByteBuffer[] var1, int var2, int var3, long var4, TimeUnit var6, A var7, CompletionHandler<Long, ? super A> var8);

   public abstract SocketAddress getLocalAddress() throws IOException;
}
