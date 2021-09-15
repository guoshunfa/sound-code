package com.sun.jmx.remote.internal;

import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;

public interface NotificationBufferFilter {
   void apply(List<TargetedNotification> var1, ObjectName var2, Notification var3);
}
