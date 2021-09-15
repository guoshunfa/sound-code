package java.awt;

import java.security.BasicPermission;

public final class AWTPermission extends BasicPermission {
   private static final long serialVersionUID = 8890392402588814465L;

   public AWTPermission(String var1) {
      super(var1);
   }

   public AWTPermission(String var1, String var2) {
      super(var1, var2);
   }
}
