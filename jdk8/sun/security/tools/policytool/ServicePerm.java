package sun.security.tools.policytool;

class ServicePerm extends Perm {
   public ServicePerm() {
      super("ServicePermission", "javax.security.auth.kerberos.ServicePermission", new String[0], new String[]{"initiate", "accept"});
   }
}
