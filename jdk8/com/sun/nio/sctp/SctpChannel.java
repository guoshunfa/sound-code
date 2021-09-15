package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpChannelImpl;

@Exported
public abstract class SctpChannel extends AbstractSelectableChannel {
   protected SctpChannel(SelectorProvider var1) {
      super(var1);
   }

   public static SctpChannel open() throws IOException {
      return new SctpChannelImpl((SelectorProvider)null);
   }

   public static SctpChannel open(SocketAddress var0, int var1, int var2) throws IOException {
      SctpChannel var3 = open();
      var3.connect(var0, var1, var2);
      return var3;
   }

   public abstract Association association() throws IOException;

   public abstract SctpChannel bind(SocketAddress var1) throws IOException;

   public abstract SctpChannel bindAddress(InetAddress var1) throws IOException;

   public abstract SctpChannel unbindAddress(InetAddress var1) throws IOException;

   public abstract boolean connect(SocketAddress var1) throws IOException;

   public abstract boolean connect(SocketAddress var1, int var2, int var3) throws IOException;

   public abstract boolean isConnectionPending();

   public abstract boolean finishConnect() throws IOException;

   public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;

   public abstract Set<SocketAddress> getRemoteAddresses() throws IOException;

   public abstract SctpChannel shutdown() throws IOException;

   public abstract <T> T getOption(SctpSocketOption<T> var1) throws IOException;

   public abstract <T> SctpChannel setOption(SctpSocketOption<T> var1, T var2) throws IOException;

   public abstract Set<SctpSocketOption<?>> supportedOptions();

   public final int validOps() {
      return 13;
   }

   public abstract <T> MessageInfo receive(ByteBuffer var1, T var2, NotificationHandler<T> var3) throws IOException;

   public abstract int send(ByteBuffer var1, MessageInfo var2) throws IOException;
}
