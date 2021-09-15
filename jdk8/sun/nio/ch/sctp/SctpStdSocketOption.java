package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpSocketOption;

public class SctpStdSocketOption<T> implements SctpSocketOption<T> {
   public static final int SCTP_DISABLE_FRAGMENTS = 1;
   public static final int SCTP_EXPLICIT_COMPLETE = 2;
   public static final int SCTP_FRAGMENT_INTERLEAVE = 3;
   public static final int SCTP_NODELAY = 4;
   public static final int SO_SNDBUF = 5;
   public static final int SO_RCVBUF = 6;
   public static final int SO_LINGER = 7;
   private final String name;
   private final Class<T> type;
   private int constValue;

   public SctpStdSocketOption(String var1, Class<T> var2) {
      this.name = var1;
      this.type = var2;
   }

   public SctpStdSocketOption(String var1, Class<T> var2, int var3) {
      this.name = var1;
      this.type = var2;
      this.constValue = var3;
   }

   public String name() {
      return this.name;
   }

   public Class<T> type() {
      return this.type;
   }

   public String toString() {
      return this.name;
   }

   int constValue() {
      return this.constValue;
   }
}
