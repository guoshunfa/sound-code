package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.security.NotificationAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanPermission;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;
import javax.security.auth.Subject;

public class ServerNotifForwarder {
   private final ServerNotifForwarder.NotifForwarderBufferFilter bufferFilter = new ServerNotifForwarder.NotifForwarderBufferFilter();
   private MBeanServer mbeanServer;
   private final String connectionId;
   private final long connectionTimeout;
   private static int listenerCounter = 0;
   private static final int[] listenerCounterLock = new int[0];
   private NotificationBuffer notifBuffer;
   private final Map<ObjectName, Set<ServerNotifForwarder.IdAndFilter>> listenerMap = new HashMap();
   private boolean terminated = false;
   private final int[] terminationLock = new int[0];
   static final String broadcasterClass = NotificationBroadcaster.class.getName();
   private final boolean checkNotificationEmission;
   private final NotificationAccessController notificationAccessController;
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerNotifForwarder");

   public ServerNotifForwarder(MBeanServer var1, Map<String, ?> var2, NotificationBuffer var3, String var4) {
      this.mbeanServer = var1;
      this.notifBuffer = var3;
      this.connectionId = var4;
      this.connectionTimeout = EnvHelp.getServerConnectionTimeout(var2);
      String var5 = (String)var2.get("jmx.remote.x.check.notification.emission");
      this.checkNotificationEmission = EnvHelp.computeBooleanFromString(var5);
      this.notificationAccessController = EnvHelp.getNotificationAccessController(var2);
   }

   public Integer addNotificationListener(final ObjectName var1, NotificationFilter var2) throws InstanceNotFoundException, IOException {
      if (logger.traceOn()) {
         logger.trace("addNotificationListener", "Add a listener at " + var1);
      }

      this.checkState();
      this.checkMBeanPermission(var1, "addNotificationListener");
      if (this.notificationAccessController != null) {
         this.notificationAccessController.addNotificationListener(this.connectionId, var1, this.getSubject());
      }

      try {
         boolean var3 = (Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws InstanceNotFoundException {
               return ServerNotifForwarder.this.mbeanServer.isInstanceOf(var1, ServerNotifForwarder.broadcasterClass);
            }
         });
         if (!var3) {
            throw new IllegalArgumentException("The specified MBean [" + var1 + "] is not a NotificationBroadcaster object.");
         }
      } catch (PrivilegedActionException var11) {
         throw (InstanceNotFoundException)extractException(var11);
      }

      Integer var12 = this.getListenerID();
      ObjectName var4 = var1;
      if (var1.getDomain() == null || var1.getDomain().equals("")) {
         try {
            var4 = ObjectName.getInstance(this.mbeanServer.getDefaultDomain(), var1.getKeyPropertyList());
         } catch (MalformedObjectNameException var10) {
            IOException var6 = new IOException(var10.getMessage());
            var6.initCause(var10);
            throw var6;
         }
      }

      synchronized(this.listenerMap) {
         ServerNotifForwarder.IdAndFilter var13 = new ServerNotifForwarder.IdAndFilter(var12, var2);
         Object var7 = (Set)this.listenerMap.get(var4);
         if (var7 == null) {
            var7 = Collections.singleton(var13);
         } else {
            if (((Set)var7).size() == 1) {
               var7 = new HashSet((Collection)var7);
            }

            ((Set)var7).add(var13);
         }

         this.listenerMap.put(var4, var7);
         return var12;
      }
   }

   public void removeNotificationListener(ObjectName var1, Integer[] var2) throws Exception {
      if (logger.traceOn()) {
         logger.trace("removeNotificationListener", "Remove some listeners from " + var1);
      }

      this.checkState();
      this.checkMBeanPermission(var1, "removeNotificationListener");
      if (this.notificationAccessController != null) {
         this.notificationAccessController.removeNotificationListener(this.connectionId, var1, this.getSubject());
      }

      Exception var3 = null;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         try {
            this.removeNotificationListener(var1, var2[var4]);
         } catch (Exception var6) {
            if (var3 != null) {
               var3 = var6;
            }
         }
      }

      if (var3 != null) {
         throw var3;
      }
   }

   public void removeNotificationListener(ObjectName var1, Integer var2) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (logger.traceOn()) {
         logger.trace("removeNotificationListener", "Remove the listener " + var2 + " from " + var1);
      }

      this.checkState();
      if (var1 != null && !var1.isPattern() && !this.mbeanServer.isRegistered(var1)) {
         throw new InstanceNotFoundException("The MBean " + var1 + " is not registered.");
      } else {
         synchronized(this.listenerMap) {
            Set var4 = (Set)this.listenerMap.get(var1);
            ServerNotifForwarder.IdAndFilter var5 = new ServerNotifForwarder.IdAndFilter(var2, (NotificationFilter)null);
            if (var4 != null && var4.contains(var5)) {
               if (var4.size() == 1) {
                  this.listenerMap.remove(var1);
               } else {
                  var4.remove(var5);
               }

            } else {
               throw new ListenerNotFoundException("Listener not found");
            }
         }
      }
   }

   public NotificationResult fetchNotifs(long var1, long var3, int var5) {
      if (logger.traceOn()) {
         logger.trace("fetchNotifs", "Fetching notifications, the startSequenceNumber is " + var1 + ", the timeout is " + var3 + ", the maxNotifications is " + var5);
      }

      long var7 = Math.min(this.connectionTimeout, var3);

      NotificationResult var6;
      try {
         var6 = this.notifBuffer.fetchNotifications(this.bufferFilter, var1, var7, var5);
         this.snoopOnUnregister(var6);
      } catch (InterruptedException var10) {
         var6 = new NotificationResult(0L, 0L, new TargetedNotification[0]);
      }

      if (logger.traceOn()) {
         logger.trace("fetchNotifs", "Forwarding the notifs: " + var6);
      }

      return var6;
   }

   private void snoopOnUnregister(NotificationResult var1) {
      ArrayList var2 = null;
      synchronized(this.listenerMap) {
         Set var4 = (Set)this.listenerMap.get(MBeanServerDelegate.DELEGATE_NAME);
         if (var4 == null || var4.isEmpty()) {
            return;
         }

         var2 = new ArrayList(var4);
      }

      TargetedNotification[] var3 = var1.getTargetedNotifications();
      int var17 = var3.length;

      for(int var5 = 0; var5 < var17; ++var5) {
         TargetedNotification var6 = var3[var5];
         Integer var7 = var6.getListenerID();
         Iterator var8 = var2.iterator();

         while(var8.hasNext()) {
            ServerNotifForwarder.IdAndFilter var9 = (ServerNotifForwarder.IdAndFilter)var8.next();
            if (var9.id == var7) {
               Notification var10 = var6.getNotification();
               if (var10 instanceof MBeanServerNotification && var10.getType().equals("JMX.mbean.unregistered")) {
                  MBeanServerNotification var11 = (MBeanServerNotification)var10;
                  ObjectName var12 = var11.getMBeanName();
                  synchronized(this.listenerMap) {
                     this.listenerMap.remove(var12);
                  }
               }
            }
         }
      }

   }

   public void terminate() {
      if (logger.traceOn()) {
         logger.trace("terminate", "Be called.");
      }

      synchronized(this.terminationLock) {
         if (this.terminated) {
            return;
         }

         this.terminated = true;
         synchronized(this.listenerMap) {
            this.listenerMap.clear();
         }
      }

      if (logger.traceOn()) {
         logger.trace("terminate", "Terminated.");
      }

   }

   private Subject getSubject() {
      return Subject.getSubject(AccessController.getContext());
   }

   private void checkState() throws IOException {
      synchronized(this.terminationLock) {
         if (this.terminated) {
            throw new IOException("The connection has been terminated.");
         }
      }
   }

   private Integer getListenerID() {
      synchronized(listenerCounterLock) {
         return listenerCounter++;
      }
   }

   public final void checkMBeanPermission(ObjectName var1, String var2) throws InstanceNotFoundException, SecurityException {
      checkMBeanPermission(this.mbeanServer, var1, var2);
   }

   static void checkMBeanPermission(final MBeanServer var0, final ObjectName var1, String var2) throws InstanceNotFoundException, SecurityException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         AccessControlContext var4 = AccessController.getContext();

         ObjectInstance var5;
         try {
            var5 = (ObjectInstance)AccessController.doPrivileged(new PrivilegedExceptionAction<ObjectInstance>() {
               public ObjectInstance run() throws InstanceNotFoundException {
                  return var0.getObjectInstance(var1);
               }
            });
         } catch (PrivilegedActionException var8) {
            throw (InstanceNotFoundException)extractException(var8);
         }

         String var6 = var5.getClassName();
         MBeanPermission var7 = new MBeanPermission(var6, (String)null, var1, var2);
         var3.checkPermission(var7, var4);
      }

   }

   private boolean allowNotificationEmission(ObjectName var1, TargetedNotification var2) {
      try {
         if (this.checkNotificationEmission) {
            this.checkMBeanPermission(var1, "addNotificationListener");
         }

         if (this.notificationAccessController != null) {
            this.notificationAccessController.fetchNotification(this.connectionId, var1, var2.getNotification(), this.getSubject());
         }

         return true;
      } catch (SecurityException var4) {
         if (logger.debugOn()) {
            logger.debug("fetchNotifs", "Notification " + var2.getNotification() + " not forwarded: the caller didn't have the required access rights");
         }

         return false;
      } catch (Exception var5) {
         if (logger.debugOn()) {
            logger.debug("fetchNotifs", "Notification " + var2.getNotification() + " not forwarded: got an unexpected exception: " + var5);
         }

         return false;
      }
   }

   private static Exception extractException(Exception var0) {
      while(var0 instanceof PrivilegedActionException) {
         var0 = ((PrivilegedActionException)var0).getException();
      }

      return var0;
   }

   private static class IdAndFilter {
      private Integer id;
      private NotificationFilter filter;

      IdAndFilter(Integer var1, NotificationFilter var2) {
         this.id = var1;
         this.filter = var2;
      }

      Integer getId() {
         return this.id;
      }

      NotificationFilter getFilter() {
         return this.filter;
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 instanceof ServerNotifForwarder.IdAndFilter && ((ServerNotifForwarder.IdAndFilter)var1).getId().equals(this.getId());
      }
   }

   final class NotifForwarderBufferFilter implements NotificationBufferFilter {
      public void apply(List<TargetedNotification> var1, ObjectName var2, Notification var3) {
         ServerNotifForwarder.IdAndFilter[] var4;
         synchronized(ServerNotifForwarder.this.listenerMap) {
            Set var6 = (Set)ServerNotifForwarder.this.listenerMap.get(var2);
            if (var6 == null) {
               ServerNotifForwarder.logger.debug("bufferFilter", "no listeners for this name");
               return;
            }

            var4 = new ServerNotifForwarder.IdAndFilter[var6.size()];
            var6.toArray(var4);
         }

         ServerNotifForwarder.IdAndFilter[] var5 = var4;
         int var12 = var4.length;

         for(int var7 = 0; var7 < var12; ++var7) {
            ServerNotifForwarder.IdAndFilter var8 = var5[var7];
            NotificationFilter var9 = var8.getFilter();
            if (var9 == null || var9.isNotificationEnabled(var3)) {
               ServerNotifForwarder.logger.debug("bufferFilter", "filter matches");
               TargetedNotification var10 = new TargetedNotification(var3, var8.getId());
               if (ServerNotifForwarder.this.allowNotificationEmission(var2, var10)) {
                  var1.add(var10);
               }
            }
         }

      }
   }
}
