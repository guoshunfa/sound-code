package sun.security.tools.policytool;

class MBeanTrustPerm extends Perm {
   public MBeanTrustPerm() {
      super("MBeanTrustPermission", "javax.management.MBeanTrustPermission", new String[]{"register"}, (String[])null);
   }
}
