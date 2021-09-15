package javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public interface ModelMBeanInfo {
   Descriptor[] getDescriptors(String var1) throws MBeanException, RuntimeOperationsException;

   void setDescriptors(Descriptor[] var1) throws MBeanException, RuntimeOperationsException;

   Descriptor getDescriptor(String var1, String var2) throws MBeanException, RuntimeOperationsException;

   void setDescriptor(Descriptor var1, String var2) throws MBeanException, RuntimeOperationsException;

   Descriptor getMBeanDescriptor() throws MBeanException, RuntimeOperationsException;

   void setMBeanDescriptor(Descriptor var1) throws MBeanException, RuntimeOperationsException;

   ModelMBeanAttributeInfo getAttribute(String var1) throws MBeanException, RuntimeOperationsException;

   ModelMBeanOperationInfo getOperation(String var1) throws MBeanException, RuntimeOperationsException;

   ModelMBeanNotificationInfo getNotification(String var1) throws MBeanException, RuntimeOperationsException;

   Object clone();

   MBeanAttributeInfo[] getAttributes();

   String getClassName();

   MBeanConstructorInfo[] getConstructors();

   String getDescription();

   MBeanNotificationInfo[] getNotifications();

   MBeanOperationInfo[] getOperations();
}
