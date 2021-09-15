package javax.management.remote.rmi;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.util.Set;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

public interface RMIConnection extends Closeable, Remote {
   String getConnectionId() throws IOException;

   void close() throws IOException;

   ObjectInstance createMBean(String var1, ObjectName var2, Subject var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;

   ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Subject var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;

   ObjectInstance createMBean(String var1, ObjectName var2, MarshalledObject var3, String[] var4, Subject var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;

   ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, MarshalledObject var4, String[] var5, Subject var6) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;

   void unregisterMBean(ObjectName var1, Subject var2) throws InstanceNotFoundException, MBeanRegistrationException, IOException;

   ObjectInstance getObjectInstance(ObjectName var1, Subject var2) throws InstanceNotFoundException, IOException;

   Set<ObjectInstance> queryMBeans(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException;

   Set<ObjectName> queryNames(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException;

   boolean isRegistered(ObjectName var1, Subject var2) throws IOException;

   Integer getMBeanCount(Subject var1) throws IOException;

   Object getAttribute(ObjectName var1, String var2, Subject var3) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;

   AttributeList getAttributes(ObjectName var1, String[] var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException;

   void setAttribute(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;

   AttributeList setAttributes(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException;

   Object invoke(ObjectName var1, String var2, MarshalledObject var3, String[] var4, Subject var5) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;

   String getDefaultDomain(Subject var1) throws IOException;

   String[] getDomains(Subject var1) throws IOException;

   MBeanInfo getMBeanInfo(ObjectName var1, Subject var2) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;

   boolean isInstanceOf(ObjectName var1, String var2, Subject var3) throws InstanceNotFoundException, IOException;

   void addNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, IOException;

   void removeNotificationListener(ObjectName var1, ObjectName var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException;

   void removeNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, ListenerNotFoundException, IOException;

   Integer[] addNotificationListeners(ObjectName[] var1, MarshalledObject[] var2, Subject[] var3) throws InstanceNotFoundException, IOException;

   void removeNotificationListeners(ObjectName var1, Integer[] var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException;

   NotificationResult fetchNotifications(long var1, int var3, long var4) throws IOException;
}
