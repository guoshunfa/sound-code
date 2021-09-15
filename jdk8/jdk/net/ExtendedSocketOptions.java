package jdk.net;

import java.net.SocketOption;
import jdk.Exported;

@Exported
public final class ExtendedSocketOptions {
   public static final SocketOption<SocketFlow> SO_FLOW_SLA = new ExtendedSocketOptions.ExtSocketOption("SO_FLOW_SLA", SocketFlow.class);

   private ExtendedSocketOptions() {
   }

   private static class ExtSocketOption<T> implements SocketOption<T> {
      private final String name;
      private final Class<T> type;

      ExtSocketOption(String var1, Class<T> var2) {
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
