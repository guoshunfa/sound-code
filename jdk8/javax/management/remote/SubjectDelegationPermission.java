package javax.management.remote;

import java.security.BasicPermission;

public final class SubjectDelegationPermission extends BasicPermission {
   private static final long serialVersionUID = 1481618113008682343L;

   public SubjectDelegationPermission(String var1) {
      super(var1);
   }

   public SubjectDelegationPermission(String var1, String var2) {
      super(var1, var2);
      if (var2 != null) {
         throw new IllegalArgumentException("Non-null actions");
      }
   }
}
