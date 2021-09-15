package sun.net;

import java.net.Proxy;

public final class ApplicationProxy extends Proxy {
   private ApplicationProxy(Proxy var1) {
      super(var1.type(), var1.address());
   }

   public static ApplicationProxy create(Proxy var0) {
      return new ApplicationProxy(var0);
   }
}
