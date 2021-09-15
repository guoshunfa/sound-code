package javax.management.modelmbean;

import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.PersistentMBean;
import javax.management.RuntimeOperationsException;

public interface ModelMBean extends DynamicMBean, PersistentMBean, ModelMBeanNotificationBroadcaster {
   void setModelMBeanInfo(ModelMBeanInfo var1) throws MBeanException, RuntimeOperationsException;

   void setManagedResource(Object var1, String var2) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException;
}
