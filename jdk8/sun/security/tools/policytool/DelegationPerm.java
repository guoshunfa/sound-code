package sun.security.tools.policytool;

class DelegationPerm extends Perm {
   public DelegationPerm() {
      super("DelegationPermission", "javax.security.auth.kerberos.DelegationPermission", new String[0], (String[])null);
   }
}
