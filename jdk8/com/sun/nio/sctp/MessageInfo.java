package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;
import sun.nio.ch.sctp.MessageInfoImpl;

@Exported
public abstract class MessageInfo {
   protected MessageInfo() {
   }

   public static MessageInfo createOutgoing(SocketAddress var0, int var1) {
      if (var1 >= 0 && var1 <= 65536) {
         return new MessageInfoImpl((Association)null, var0, var1);
      } else {
         throw new IllegalArgumentException("Invalid stream number");
      }
   }

   public static MessageInfo createOutgoing(Association var0, SocketAddress var1, int var2) {
      if (var0 == null) {
         throw new IllegalArgumentException("association cannot be null");
      } else if (var2 >= 0 && var2 <= 65536) {
         return new MessageInfoImpl(var0, var1, var2);
      } else {
         throw new IllegalArgumentException("Invalid stream number");
      }
   }

   public abstract SocketAddress address();

   public abstract Association association();

   public abstract int bytes();

   public abstract boolean isComplete();

   public abstract MessageInfo complete(boolean var1);

   public abstract boolean isUnordered();

   public abstract MessageInfo unordered(boolean var1);

   public abstract int payloadProtocolID();

   public abstract MessageInfo payloadProtocolID(int var1);

   public abstract int streamNumber();

   public abstract MessageInfo streamNumber(int var1);

   public abstract long timeToLive();

   public abstract MessageInfo timeToLive(long var1);
}
