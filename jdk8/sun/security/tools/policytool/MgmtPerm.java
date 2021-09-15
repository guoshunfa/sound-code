package sun.security.tools.policytool;

class MgmtPerm extends Perm {
   public MgmtPerm() {
      super("ManagementPermission", "java.lang.management.ManagementPermission", new String[]{"control", "monitor"}, (String[])null);
   }
}
