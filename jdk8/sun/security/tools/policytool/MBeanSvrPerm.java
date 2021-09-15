package sun.security.tools.policytool;

class MBeanSvrPerm extends Perm {
   public MBeanSvrPerm() {
      super("MBeanServerPermission", "javax.management.MBeanServerPermission", new String[]{"createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer"}, (String[])null);
   }
}
