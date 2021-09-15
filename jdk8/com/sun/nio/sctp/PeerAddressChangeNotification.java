package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;

@Exported
public abstract class PeerAddressChangeNotification implements Notification {
   protected PeerAddressChangeNotification() {
   }

   public abstract SocketAddress address();

   public abstract Association association();

   public abstract PeerAddressChangeNotification.AddressChangeEvent event();

   @Exported
   public static enum AddressChangeEvent {
      ADDR_AVAILABLE,
      ADDR_UNREACHABLE,
      ADDR_REMOVED,
      ADDR_ADDED,
      ADDR_MADE_PRIMARY,
      ADDR_CONFIRMED;
   }
}
