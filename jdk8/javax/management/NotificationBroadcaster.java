package javax.management;

public interface NotificationBroadcaster {
   void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws IllegalArgumentException;

   void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException;

   MBeanNotificationInfo[] getNotificationInfo();
}
