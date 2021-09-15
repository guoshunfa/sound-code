package java.net;

public final class StandardSocketOptions {
   public static final SocketOption<Boolean> SO_BROADCAST = new StandardSocketOptions.StdSocketOption("SO_BROADCAST", Boolean.class);
   public static final SocketOption<Boolean> SO_KEEPALIVE = new StandardSocketOptions.StdSocketOption("SO_KEEPALIVE", Boolean.class);
   public static final SocketOption<Integer> SO_SNDBUF = new StandardSocketOptions.StdSocketOption("SO_SNDBUF", Integer.class);
   public static final SocketOption<Integer> SO_RCVBUF = new StandardSocketOptions.StdSocketOption("SO_RCVBUF", Integer.class);
   public static final SocketOption<Boolean> SO_REUSEADDR = new StandardSocketOptions.StdSocketOption("SO_REUSEADDR", Boolean.class);
   public static final SocketOption<Integer> SO_LINGER = new StandardSocketOptions.StdSocketOption("SO_LINGER", Integer.class);
   public static final SocketOption<Integer> IP_TOS = new StandardSocketOptions.StdSocketOption("IP_TOS", Integer.class);
   public static final SocketOption<NetworkInterface> IP_MULTICAST_IF = new StandardSocketOptions.StdSocketOption("IP_MULTICAST_IF", NetworkInterface.class);
   public static final SocketOption<Integer> IP_MULTICAST_TTL = new StandardSocketOptions.StdSocketOption("IP_MULTICAST_TTL", Integer.class);
   public static final SocketOption<Boolean> IP_MULTICAST_LOOP = new StandardSocketOptions.StdSocketOption("IP_MULTICAST_LOOP", Boolean.class);
   public static final SocketOption<Boolean> TCP_NODELAY = new StandardSocketOptions.StdSocketOption("TCP_NODELAY", Boolean.class);

   private StandardSocketOptions() {
   }

   private static class StdSocketOption<T> implements SocketOption<T> {
      private final String name;
      private final Class<T> type;

      StdSocketOption(String var1, Class<T> var2) {
         this.name = var1;
         this.type = var2;
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
   }
}
