package com.sun.nio.sctp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import jdk.Exported;

@Exported
public abstract class SendFailedNotification implements Notification {
   protected SendFailedNotification() {
   }

   public abstract Association association();

   public abstract SocketAddress address();

   public abstract ByteBuffer buffer();

   public abstract int errorCode();

   public abstract int streamNumber();
}
