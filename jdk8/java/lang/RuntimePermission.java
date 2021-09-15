package java.lang;

import java.security.BasicPermission;

public final class RuntimePermission extends BasicPermission {
   private static final long serialVersionUID = 7399184964622342223L;

   public RuntimePermission(String var1) {
      super(var1);
   }

   public RuntimePermission(String var1, String var2) {
      super(var1, var2);
   }
}
