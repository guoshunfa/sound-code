package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public interface NotificationHandler<T> {
   HandlerResult handleNotification(Notification var1, T var2);
}
