package jdk.net;

import jdk.Exported;

@Exported
public class SocketFlow {
   private static final int UNSET = -1;
   public static final int NORMAL_PRIORITY = 1;
   public static final int HIGH_PRIORITY = 2;
   private int priority = 1;
   private long bandwidth = -1L;
   private SocketFlow.Status status;

   private SocketFlow() {
      this.status = SocketFlow.Status.NO_STATUS;
   }

   public static SocketFlow create() {
      return new SocketFlow();
   }

   public SocketFlow priority(int var1) {
      if (var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException("invalid priority");
      } else {
         this.priority = var1;
         return this;
      }
   }

   public SocketFlow bandwidth(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("invalid bandwidth");
      } else {
         this.bandwidth = var1;
         return this;
      }
   }

   public int priority() {
      return this.priority;
   }

   public long bandwidth() {
      return this.bandwidth;
   }

   public SocketFlow.Status status() {
      return this.status;
   }

   @Exported
   public static enum Status {
      NO_STATUS,
      OK,
      NO_PERMISSION,
      NOT_CONNECTED,
      NOT_SUPPORTED,
      ALREADY_CREATED,
      IN_PROGRESS,
      OTHER;
   }
}
