package com.sun.jmx.remote.security;

import javax.management.Notification;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public interface NotificationAccessController {
   void addNotificationListener(String var1, ObjectName var2, Subject var3) throws SecurityException;

   void removeNotificationListener(String var1, ObjectName var2, Subject var3) throws SecurityException;

   void fetchNotification(String var1, ObjectName var2, Notification var3, Subject var4) throws SecurityException;
}
