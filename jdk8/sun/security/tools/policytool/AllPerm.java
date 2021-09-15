package sun.security.tools.policytool;

class AllPerm extends Perm {
   public AllPerm() {
      super("AllPermission", "java.security.AllPermission", (String[])null, (String[])null);
   }
}
