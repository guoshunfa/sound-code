package java.nio.channels;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class SocketChannel extends AbstractSelectableChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel {
   protected SocketChannel(SelectorProvider var1) {
      super(var1);
   }

   public static SocketChannel open() throws IOException {
      return SelectorProvider.provider().openSocketChannel();
   }

   public static SocketChannel open(SocketAddress var0) throws IOException {
      SocketChannel var1 = open();

      try {
         var1.connect(var0);
      } catch (Throwable var5) {
         try {
            var1.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      assert var1.isConnected();

      return var1;
   }

   public final int validOps() {
      return 13;
   }

   public abstract SocketChannel bind(SocketAddress var1) throws IOException;

   public abstract <T> SocketChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   public abstract SocketChannel shutdownInput() throws IOException;

   public abstract SocketChannel shutdownOutput() throws IOException;

   public abstract Socket socket();

   public abstract boolean isConnected();

   public abstract boolean isConnectionPending();

   public abstract boolean connect(SocketAddress var1) throws IOException;

   public abstract boolean finishConnect() throws IOException;

   public abstract SocketAddress getRemoteAddress() throws IOException;

   public abstract int read(ByteBuffer var1) throws IOException;

   public abstract long read(ByteBuffer[] var1, int var2, int var3) throws IOException;

   public final long read(ByteBuffer[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public abstract int write(ByteBuffer var1) throws IOException;

   public abstract long write(ByteBuffer[] var1, int var2, int var3) throws IOException;

   public final long write(ByteBuffer[] var1) throws IOException {
      return this.write(var1, 0, var1.length);
   }

   public abstract SocketAddress getLocalAddress() throws IOException;
}
