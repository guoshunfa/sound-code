package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.sun.nio.sctp.SctpSocketOption;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class SctpServerChannelImpl extends SctpServerChannel {
   private static final String message = "SCTP not supported on this platform";

   public SctpServerChannelImpl(SelectorProvider var1) {
      super(var1);
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpChannel accept() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpServerChannel bind(SocketAddress var1, int var2) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpServerChannel bindAddress(InetAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public SctpServerChannel unbindAddress(InetAddress var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Set<SocketAddress> getAllLocalAddresses() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public <T> T getOption(SctpSocketOption<T> var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public <T> SctpServerChannel setOption(SctpSocketOption<T> var1, T var2) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public Set<SctpSocketOption<?>> supportedOptions() {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }

   public void implCloseSelectableChannel() throws IOException {
      throw new UnsupportedOperationException("SCTP not supported on this platform");
   }
}
