package javax.management.openmbean;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

public interface OpenMBeanInfo {
   String getClassName();

   String getDescription();

   MBeanAttributeInfo[] getAttributes();

   MBeanOperationInfo[] getOperations();

   MBeanConstructorInfo[] getConstructors();

   MBeanNotificationInfo[] getNotifications();

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
