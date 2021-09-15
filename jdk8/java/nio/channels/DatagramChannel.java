package java.nio.channels;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class DatagramChannel extends AbstractSelectableChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, MulticastChannel {
   protected DatagramChannel(SelectorProvider var1) {
      super(var1);
   }

   public static DatagramChannel open() throws IOException {
      return SelectorProvider.provider().openDatagramChannel();
   }

   public static DatagramChannel open(ProtocolFamily var0) throws IOException {
      return SelectorProvider.provider().openDatagramChannel(var0);
   }

   public final int validOps() {
      return 5;
   }

   public abstract DatagramChannel bind(SocketAddress var1) throws IOException;

   public abstract <T> DatagramChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   public abstract DatagramSocket socket();

   public abstract boolean isConnected();

   public abstract DatagramChannel connect(SocketAddress var1) throws IOException;

   public abstract DatagramChannel disconnect() throws IOException;

   public abstract SocketAddress getRemoteAddress() throws IOException;

   public abstract SocketAddress receive(ByteBuffer var1) throws IOException;

   public abstract int send(ByteBuffer var1, SocketAddress var2) throws IOException;

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
