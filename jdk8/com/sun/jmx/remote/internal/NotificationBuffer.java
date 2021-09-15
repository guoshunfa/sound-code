package com.sun.jmx.remote.internal;

import javax.management.remote.NotificationResult;

public interface NotificationBuffer {
   NotificationResult fetchNotifications(NotificationBufferFilter var1, long var2, long var4, int var6) throws InterruptedException;

   void dispose();
}
