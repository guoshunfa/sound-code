package javax.management.remote;

import javax.management.Notification;

public class JMXConnectionNotification extends Notification {
   private static final long serialVersionUID = -2331308725952627538L;
   public static final String OPENED = "jmx.remote.connection.opened";
   public static final String CLOSED = "jmx.remote.connection.closed";
   public static final String FAILED = "jmx.remote.connection.failed";
   public static final String NOTIFS_LOST = "jmx.remote.connection.notifs.lost";
   private final String connectionId;

   public JMXConnectionNotification(String var1, Object var2, String var3, long var4, String var6, Object var7) {
      super((String)nonNull(var1), nonNull(var2), Math.max(0L, var4), System.currentTimeMillis(), var6);
      if (var1 != null && var2 != null && var3 != null) {
         if (var4 < 0L) {
            throw new IllegalArgumentException("Negative sequence number");
         } else {
            this.connectionId = var3;
            this.setUserData(var7);
         }
      } else {
         throw new NullPointerException("Illegal null argument");
      }
   }

   private static Object nonNull(Object var0) {
      return var0 == null ? "" : var0;
   }

   public String getConnectionId() {
      return this.connectionId;
   }
}
