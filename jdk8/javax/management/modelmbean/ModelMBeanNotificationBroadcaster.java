package javax.management.modelmbean;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.RuntimeOperationsException;

public interface ModelMBeanNotificationBroadcaster extends NotificationBroadcaster {
   void sendNotification(Notification var1) throws MBeanException, RuntimeOperationsException;

   void sendNotification(String var1) throws MBeanException, RuntimeOperationsException;

   void sendAttributeChangeNotification(AttributeChangeNotification var1) throws MBeanException, RuntimeOperationsException;

   void sendAttributeChangeNotification(Attribute var1, Attribute var2) throws MBeanException, RuntimeOperationsException;

   void addAttributeChangeNotificationListener(NotificationListener var1, String var2, Object var3) throws MBeanException, RuntimeOperationsException, IllegalArgumentException;

   void removeAttributeChangeNotificationListener(NotificationListener var1, String var2) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException;
}
