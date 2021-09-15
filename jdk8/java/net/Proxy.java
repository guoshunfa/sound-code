package java.net;

public class Proxy {
   private Proxy.Type type;
   private SocketAddress sa;
   public static final Proxy NO_PROXY = new Proxy();

   private Proxy() {
      this.type = Proxy.Type.DIRECT;
      this.sa = null;
   }

   public Proxy(Proxy.Type var1, SocketAddress var2) {
      if (var1 != Proxy.Type.DIRECT && var2 instanceof InetSocketAddress) {
         this.type = var1;
         this.sa = var2;
      } else {
         throw new IllegalArgumentException("type " + var1 + " is not compatible with address " + var2);
      }
   }

   public Proxy.Type type() {
      return this.type;
   }

   public SocketAddress address() {
      return this.sa;
   }

   public String toString() {
      return this.type() == Proxy.Type.DIRECT ? "DIRECT" : this.type() + " @ " + this.address();
   }

   public final boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Proxy) {
         Proxy var2 = (Proxy)var1;
         if (var2.type() == this.type()) {
            if (this.address() == null) {
               return var2.address() == null;
            } else {
               return this.address().equals(var2.address());
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return this.address() == null ? this.type().hashCode() : this.type().hashCode() + this.address().hashCode();
   }

   public static enum Type {
      DIRECT,
      HTTP,
      SOCKS;
   }
}
