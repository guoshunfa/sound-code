package com.sun.management.jmx;

import javax.management.Notification;
import javax.management.NotificationFilter;

/** @deprecated */
@Deprecated
public class TraceFilter implements NotificationFilter {
   protected int levels;
   protected int types;

   public TraceFilter(int var1, int var2) throws IllegalArgumentException {
      this.levels = var1;
      this.types = var2;
   }

   public boolean isNotificationEnabled(Notification var1) {
      return false;
   }

   public int getLevels() {
      return this.levels;
   }

   public int getTypes() {
      return this.types;
   }
}
