package sun.net.www.http;

import java.net.URL;

class KeepAliveKey {
   private String protocol = null;
   private String host = null;
   private int port = 0;
   private Object obj = null;

   public KeepAliveKey(URL var1, Object var2) {
      this.protocol = var1.getProtocol();
      this.host = var1.getHost();
      this.port = var1.getPort();
      this.obj = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof KeepAliveKey)) {
         return false;
      } else {
         KeepAliveKey var2 = (KeepAliveKey)var1;
         return this.host.equals(var2.host) && this.port == var2.port && this.protocol.equals(var2.protocol) && this.obj == var2.obj;
      }
   }

   public int hashCode() {
      String var1 = this.protocol + this.host + this.port;
      return this.obj == null ? var1.hashCode() : var1.hashCode() + this.obj.hashCode();
   }
}
