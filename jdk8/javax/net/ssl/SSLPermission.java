package javax.net.ssl;

import java.security.BasicPermission;

public final class SSLPermission extends BasicPermission {
   private static final long serialVersionUID = -3456898025505876775L;

   public SSLPermission(String var1) {
      super(var1);
   }

   public SSLPermission(String var1, String var2) {
      super(var1, var2);
   }
}
