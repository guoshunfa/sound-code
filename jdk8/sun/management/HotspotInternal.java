package sun.management;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class HotspotInternal implements HotspotInternalMBean, MBeanRegistration {
   private static final String HOTSPOT_INTERNAL_MBEAN_NAME = "sun.management:type=HotspotInternal";
   private static ObjectName objName = Util.newObjectName("sun.management:type=HotspotInternal");
   private MBeanServer server = null;

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      ManagementFactoryHelper.registerInternalMBeans(var1);
      this.server = var1;
      return objName;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
      ManagementFactoryHelper.unregisterInternalMBeans(this.server);
   }

   public void postDeregister() {
   }
}
