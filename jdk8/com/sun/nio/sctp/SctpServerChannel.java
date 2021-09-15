package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpServerChannelImpl;

@Exported
public abstract class SctpServerChannel extends AbstractSelectableChannel {
   protected SctpServerChannel(SelectorProvider var1) {
      super(var1);
   }

   public static SctpServerChannel open() throws IOException {
      return new SctpServerChannelImpl((SelectorProvider)null);
   }

   public abstract SctpChannel accept() throws IOException;

   public final SctpServerChannel bind(SocketAddress var1) throws IOException {
      return this.bind(var1, 0);
   }

   public abstract SctpServerChannel bind(SocketAddress var1, int var2) throws IOException;

   public abstract SctpServerChannel bindAddress(InetAddress var1) throws IOException;

   public abstract SctpServerChannel unbindAddress(InetAddress var1) throws IOException;

   public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;

   public abstract <T> T getOption(SctpSocketOption<T> var1) throws IOException;

   public abstract <T> SctpServerChannel setOption(SctpSocketOption<T> var1, T var2) throws IOException;

   public abstract Set<SctpSocketOption<?>> supportedOptions();

   public final int validOps() {
      return 16;
   }
}
