package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpMultiChannelImpl;

@Exported
public abstract class SctpMultiChannel extends AbstractSelectableChannel {
   protected SctpMultiChannel(SelectorProvider var1) {
      super(var1);
   }

   public static SctpMultiChannel open() throws IOException {
      return new SctpMultiChannelImpl((SelectorProvider)null);
   }

   public abstract Set<Association> associations() throws IOException;

   public abstract SctpMultiChannel bind(SocketAddress var1, int var2) throws IOException;

   public final SctpMultiChannel bind(SocketAddress var1) throws IOException {
      return this.bind(var1, 0);
   }

   public abstract SctpMultiChannel bindAddress(InetAddress var1) throws IOException;

   public abstract SctpMultiChannel unbindAddress(InetAddress var1) throws IOException;

   public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;

   public abstract Set<SocketAddress> getRemoteAddresses(Association var1) throws IOException;

   public abstract SctpMultiChannel shutdown(Association var1) throws IOException;

   public abstract <T> T getOption(SctpSocketOption<T> var1, Association var2) throws IOException;

   public abstract <T> SctpMultiChannel setOption(SctpSocketOption<T> var1, T var2, Association var3) throws IOException;

   public abstract Set<SctpSocketOption<?>> supportedOptions();

   public final int validOps() {
      return 5;
   }

   public abstract <T> MessageInfo receive(ByteBuffer var1, T var2, NotificationHandler<T> var3) throws IOException;

   public abstract int send(ByteBuffer var1, MessageInfo var2) throws IOException;

   public abstract SctpChannel branch(Association var1) throws IOException;
}
