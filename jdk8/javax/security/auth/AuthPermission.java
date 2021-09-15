package javax.security.auth;

import java.security.BasicPermission;

public final class AuthPermission extends BasicPermission {
   private static final long serialVersionUID = 5806031445061587174L;

   public AuthPermission(String var1) {
      super("createLoginContext".equals(var1) ? "createLoginContext.*" : var1);
   }

   public AuthPermission(String var1, String var2) {
      super("createLoginContext".equals(var1) ? "createLoginContext.*" : var1, var2);
   }
}
