package sun.security.tools.policytool;

class MBeanPerm extends Perm {
   public MBeanPerm() {
      super("MBeanPermission", "javax.management.MBeanPermission", new String[0], new String[]{"addNotificationListener", "getAttribute", "getClassLoader", "getClassLoaderFor", "getClassLoaderRepository", "getDomains", "getMBeanInfo", "getObjectInstance", "instantiate", "invoke", "isInstanceOf", "queryMBeans", "queryNames", "registerMBean", "removeNotificationListener", "setAttribute", "unregisterMBean"});
   }
}
