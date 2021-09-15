package java.net;

import java.security.BasicPermission;

public final class NetPermission extends BasicPermission {
   private static final long serialVersionUID = -8343910153355041693L;

   public NetPermission(String var1) {
      super(var1);
   }

   public NetPermission(String var1, String var2) {
      super(var1, var2);
   }
}
