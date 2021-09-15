package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpSocketOption;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class SctpChannelImpl extends SctpChannel {
   private static final String message = "SCTP not supported on this platform";

   public SctpChannelImpl(SelectorProvider var1) {
      super(var1);
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Association association() {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpChannel bind(SocketAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpChannel bindAddress(InetAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpChannel unbindAddress(InetAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public boolean connect(SocketAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public boolean connect(SocketAddress var1, int var2, int var3) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public boolean isConnectionPending() {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public boolean finishConnect() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Set<SocketAddress> getAllLocalAddresses() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Set<SocketAddress> getRemoteAddresses() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpChannel shutdown() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public <T> T getOption(SctpSocketOption<T> var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public <T> SctpChannel setOption(SctpSocketOption<T> var1, T var2) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Set<SctpSocketOption<?>> supportedOptions() {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public <T> MessageInfo receive(ByteBuffer var1, T var2, NotificationHandler<T> var3) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public int send(ByteBuffer var1, MessageInfo var2) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public void implCloseSelectableChannel() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }
}
