package java.security;

public abstract class PolicySpi {
   protected abstract boolean engineImplies(ProtectionDomain var1, Permission var2);

   protected void engineRefresh() {
   }

   protected PermissionCollection engineGetPermissions(CodeSource var1) {
      return Policy.UNSUPPORTED_EMPTY_COLLECTION;
   }

   protected PermissionCollection engineGetPermissions(ProtectionDomain var1) {
      return Policy.UNSUPPORTED_EMPTY_COLLECTION;
   }
}
