package com.sun.jmx.remote.internal;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class ClientListenerInfo {
   private final ObjectName name;
   private final Integer listenerID;
   private final NotificationFilter filter;
   private final NotificationListener listener;
   private final Object handback;
   private final Subject delegationSubject;

   public ClientListenerInfo(Integer var1, ObjectName var2, NotificationListener var3, NotificationFilter var4, Object var5, Subject var6) {
      this.listenerID = var1;
      this.name = var2;
      this.listener = var3;
      this.filter = var4;
      this.handback = var5;
      this.delegationSubject = var6;
   }

   public ObjectName getObjectName() {
      return this.name;
   }

   public Integer getListenerID() {
      return this.listenerID;
   }

   public NotificationFilter getNotificationFilter() {
      return this.filter;
   }

   public NotificationListener getListener() {
      return this.listener;
   }

   public Object getHandback() {
      return this.handback;
   }

   public Subject getDelegationSubject() {
      return this.delegationSubject;
   }

   public boolean sameAs(ObjectName var1) {
      return this.getObjectName().equals(var1);
   }

   public boolean sameAs(ObjectName var1, NotificationListener var2) {
      return this.getObjectName().equals(var1) && this.getListener() == var2;
   }

   public boolean sameAs(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) {
      return this.getObjectName().equals(var1) && this.getListener() == var2 && this.getNotificationFilter() == var3 && this.getHandback() == var4;
   }
}
