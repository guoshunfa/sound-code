package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;
import sun.nio.ch.sctp.SctpStdSocketOption;

@Exported
public class SctpStandardSocketOptions {
   public static final SctpSocketOption<Boolean> SCTP_DISABLE_FRAGMENTS = new SctpStdSocketOption("SCTP_DISABLE_FRAGMENTS", Boolean.class, 1);
   public static final SctpSocketOption<Boolean> SCTP_EXPLICIT_COMPLETE = new SctpStdSocketOption("SCTP_EXPLICIT_COMPLETE", Boolean.class, 2);
   public static final SctpSocketOption<Integer> SCTP_FRAGMENT_INTERLEAVE = new SctpStdSocketOption("SCTP_FRAGMENT_INTERLEAVE", Integer.class, 3);
   public static final SctpSocketOption<SctpStandardSocketOptions.InitMaxStreams> SCTP_INIT_MAXSTREAMS = new SctpStdSocketOption("SCTP_INIT_MAXSTREAMS", SctpStandardSocketOptions.InitMaxStreams.class);
   public static final SctpSocketOption<Boolean> SCTP_NODELAY = new SctpStdSocketOption("SCTP_NODELAY", Boolean.class, 4);
   public static final SctpSocketOption<SocketAddress> SCTP_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_PRIMARY_ADDR", SocketAddress.class);
   public static final SctpSocketOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_SET_PEER_PRIMARY_ADDR", SocketAddress.class);
   public static final SctpSocketOption<Integer> SO_SNDBUF = new SctpStdSocketOption("SO_SNDBUF", Integer.class, 5);
   public static final SctpSocketOption<Integer> SO_RCVBUF = new SctpStdSocketOption("SO_RCVBUF", Integer.class, 6);
   public static final SctpSocketOption<Integer> SO_LINGER = new SctpStdSocketOption("SO_LINGER", Integer.class, 7);

   private SctpStandardSocketOptions() {
   }

   @Exported
   public static class InitMaxStreams {
      private int maxInStreams;
      private int maxOutStreams;

      private InitMaxStreams(int var1, int var2) {
         this.maxInStreams = var1;
         this.maxOutStreams = var2;
      }

      public static SctpStandardSocketOptions.InitMaxStreams create(int var0, int var1) {
         if (var1 >= 0 && var1 <= 65535) {
            if (var0 >= 0 && var0 <= 65535) {
               return new SctpStandardSocketOptions.InitMaxStreams(var0, var1);
            } else {
               throw new IllegalArgumentException("Invalid maxInStreams value");
            }
         } else {
            throw new IllegalArgumentException("Invalid maxOutStreams value");
         }
      }

      public int maxInStreams() {
         return this.maxInStreams;
      }

      public int maxOutStreams() {
         return this.maxOutStreams;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString()).append(" [");
         var1.append("maxInStreams:").append(this.maxInStreams);
         var1.append("maxOutStreams:").append(this.maxOutStreams).append("]");
         return var1.toString();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1 instanceof SctpStandardSocketOptions.InitMaxStreams) {
            SctpStandardSocketOptions.InitMaxStreams var2 = (SctpStandardSocketOptions.InitMaxStreams)var1;
            if (this.maxInStreams == var2.maxInStreams && this.maxOutStreams == var2.maxOutStreams) {
               return true;
            }
         }

         return false;
      }

      public int hashCode() {
         int var1 = 7 ^ this.maxInStreams ^ this.maxOutStreams;
         return var1;
      }
   }
}
