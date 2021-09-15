package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

public class ArrayNotificationBuffer implements NotificationBuffer {
   private boolean disposed = false;
   private static final Object globalLock = new Object();
   private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer = new HashMap(1);
   private final Collection<ArrayNotificationBuffer.ShareBuffer> sharers = new HashSet(1);
   private final NotificationListener bufferListener = new ArrayNotificationBuffer.BufferListener();
   private static final QueryExp broadcasterQuery = new ArrayNotificationBuffer.BroadcasterQuery();
   private static final NotificationFilter creationFilter;
   private final NotificationListener creationListener = new NotificationListener() {
      public void handleNotification(Notification var1, Object var2) {
         ArrayNotificationBuffer.logger.debug("creationListener", "handleNotification called");
         ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)var1);
      }
   };
   private static final ClassLogger logger;
   private final MBeanServer mBeanServer;
   private final ArrayQueue<ArrayNotificationBuffer.NamedNotification> queue;
   private int queueSize;
   private long earliestSequenceNumber;
   private long nextSequenceNumber;
   private Set<ObjectName> createdDuringQuery;
   static final String broadcasterClass;

   public static NotificationBuffer getNotificationBuffer(MBeanServer var0, Map<String, ?> var1) {
      if (var1 == null) {
         var1 = Collections.emptyMap();
      }

      int var2 = EnvHelp.getNotifBufferSize(var1);
      ArrayNotificationBuffer var3;
      boolean var4;
      ArrayNotificationBuffer.ShareBuffer var5;
      synchronized(globalLock) {
         var3 = (ArrayNotificationBuffer)mbsToBuffer.get(var0);
         var4 = var3 == null;
         if (var4) {
            var3 = new ArrayNotificationBuffer(var0, var2);
            mbsToBuffer.put(var0, var3);
         }

         var5 = var3.new ShareBuffer(var2);
      }

      if (var4) {
         var3.createListeners();
      }

      return var5;
   }

   static void removeNotificationBuffer(MBeanServer var0) {
      synchronized(globalLock) {
         mbsToBuffer.remove(var0);
      }
   }

   void addSharer(ArrayNotificationBuffer.ShareBuffer var1) {
      synchronized(globalLock) {
         synchronized(this) {
            if (var1.getSize() > this.queueSize) {
               this.resize(var1.getSize());
            }
         }

         this.sharers.add(var1);
      }
   }

   private void removeSharer(ArrayNotificationBuffer.ShareBuffer var1) {
      boolean var2;
      synchronized(globalLock) {
         this.sharers.remove(var1);
         var2 = this.sharers.isEmpty();
         if (var2) {
            removeNotificationBuffer(this.mBeanServer);
         } else {
            int var4 = 0;
            Iterator var5 = this.sharers.iterator();

            while(var5.hasNext()) {
               ArrayNotificationBuffer.ShareBuffer var6 = (ArrayNotificationBuffer.ShareBuffer)var5.next();
               int var7 = var6.getSize();
               if (var7 > var4) {
                  var4 = var7;
               }
            }

            if (var4 < this.queueSize) {
               this.resize(var4);
            }
         }
      }

      if (var2) {
         synchronized(this) {
            this.disposed = true;
            this.notifyAll();
         }

         this.destroyListeners();
      }

   }

   private synchronized void resize(int var1) {
      if (var1 != this.queueSize) {
         while(this.queue.size() > var1) {
            this.dropNotification();
         }

         this.queue.resize(var1);
         this.queueSize = var1;
      }
   }

   private ArrayNotificationBuffer(MBeanServer var1, int var2) {
      if (logger.traceOn()) {
         logger.trace("Constructor", "queueSize=" + var2);
      }

      if (var1 != null && var2 >= 1) {
         this.mBeanServer = var1;
         this.queueSize = var2;
         this.queue = new ArrayQueue(var2);
         this.earliestSequenceNumber = System.currentTimeMillis();
         this.nextSequenceNumber = this.earliestSequenceNumber;
         logger.trace("Constructor", "ends");
      } else {
         throw new IllegalArgumentException("Bad args");
      }
   }

   private synchronized boolean isDisposed() {
      return this.disposed;
   }

   public void dispose() {
      throw new UnsupportedOperationException();
   }

   public NotificationResult fetchNotifications(NotificationBufferFilter var1, long var2, long var4, int var6) throws InterruptedException {
      logger.trace("fetchNotifications", "starts");
      if (var2 >= 0L && !this.isDisposed()) {
         if (var1 != null && var2 >= 0L && var4 >= 0L && var6 >= 0) {
            if (logger.debugOn()) {
               logger.trace("fetchNotifications", "filter=" + var1 + "; startSeq=" + var2 + "; timeout=" + var4 + "; max=" + var6);
            }

            if (var2 > this.nextSequenceNumber()) {
               String var22 = "Start sequence number too big: " + var2 + " > " + this.nextSequenceNumber();
               logger.trace("fetchNotifications", var22);
               throw new IllegalArgumentException(var22);
            } else {
               long var7 = System.currentTimeMillis() + var4;
               if (var7 < 0L) {
                  var7 = Long.MAX_VALUE;
               }

               if (logger.debugOn()) {
                  logger.debug("fetchNotifications", "endTime=" + var7);
               }

               long var9 = -1L;
               long var11 = var2;
               ArrayList var13 = new ArrayList();

               while(true) {
                  logger.debug("fetchNotifications", "main loop starts");
                  ArrayNotificationBuffer.NamedNotification var14;
                  synchronized(this) {
                     if (var9 < 0L) {
                        var9 = this.earliestSequenceNumber();
                        if (logger.debugOn()) {
                           logger.debug("fetchNotifications", "earliestSeq=" + var9);
                        }

                        if (var11 < var9) {
                           var11 = var9;
                           logger.debug("fetchNotifications", "nextSeq=earliestSeq");
                        }
                     } else {
                        var9 = this.earliestSequenceNumber();
                     }

                     if (var11 < var9) {
                        logger.trace("fetchNotifications", "nextSeq=" + var11 + " < earliestSeq=" + var9 + " so may have lost notifs");
                        break;
                     }

                     if (var11 >= this.nextSequenceNumber()) {
                        if (var13.size() > 0) {
                           logger.debug("fetchNotifications", "no more notifs but have some so don't wait");
                           break;
                        }

                        long var25 = var7 - System.currentTimeMillis();
                        if (var25 <= 0L) {
                           logger.debug("fetchNotifications", "timeout");
                           break;
                        }

                        if (this.isDisposed()) {
                           if (logger.debugOn()) {
                              logger.debug("fetchNotifications", "dispose callled, no wait");
                           }

                           return new NotificationResult(this.earliestSequenceNumber(), this.nextSequenceNumber(), new TargetedNotification[0]);
                        }

                        if (logger.debugOn()) {
                           logger.debug("fetchNotifications", "wait(" + var25 + ")");
                        }

                        this.wait(var25);
                        continue;
                     }

                     var14 = this.notificationAt(var11);
                     if (!(var1 instanceof ServerNotifForwarder.NotifForwarderBufferFilter)) {
                        try {
                           ServerNotifForwarder.checkMBeanPermission(this.mBeanServer, var14.getObjectName(), "addNotificationListener");
                        } catch (SecurityException | InstanceNotFoundException var20) {
                           if (logger.debugOn()) {
                              logger.debug("fetchNotifications", "candidate: " + var14 + " skipped. exception " + var20);
                           }

                           ++var11;
                           continue;
                        }
                     }

                     if (logger.debugOn()) {
                        logger.debug("fetchNotifications", "candidate: " + var14);
                        logger.debug("fetchNotifications", "nextSeq now " + var11);
                     }
                  }

                  ObjectName var15 = var14.getObjectName();
                  Notification var16 = var14.getNotification();
                  ArrayList var17 = new ArrayList();
                  logger.debug("fetchNotifications", "applying filter to candidate");
                  var1.apply(var17, var15, var16);
                  if (var17.size() > 0) {
                     if (var6 <= 0) {
                        logger.debug("fetchNotifications", "reached maxNotifications");
                        break;
                     }

                     --var6;
                     if (logger.debugOn()) {
                        logger.debug("fetchNotifications", "add: " + var17);
                     }

                     var13.addAll(var17);
                  }

                  ++var11;
               }

               int var23 = var13.size();
               TargetedNotification[] var24 = new TargetedNotification[var23];
               var13.toArray(var24);
               NotificationResult var26 = new NotificationResult(var9, var11, var24);
               if (logger.debugOn()) {
                  logger.debug("fetchNotifications", var26.toString());
               }

               logger.trace("fetchNotifications", "ends");
               return var26;
            }
         } else {
            logger.trace("fetchNotifications", "Bad args");
            throw new IllegalArgumentException("Bad args to fetch");
         }
      } else {
         synchronized(this) {
            return new NotificationResult(this.earliestSequenceNumber(), this.nextSequenceNumber(), new TargetedNotification[0]);
         }
      }
   }

   synchronized long earliestSequenceNumber() {
      return this.earliestSequenceNumber;
   }

   synchronized long nextSequenceNumber() {
      return this.nextSequenceNumber;
   }

   synchronized void addNotification(ArrayNotificationBuffer.NamedNotification var1) {
      if (logger.traceOn()) {
         logger.trace("addNotification", var1.toString());
      }

      while(this.queue.size() >= this.queueSize) {
         this.dropNotification();
         if (logger.debugOn()) {
            logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + this.earliestSequenceNumber);
         }
      }

      this.queue.add(var1);
      ++this.nextSequenceNumber;
      if (logger.debugOn()) {
         logger.debug("addNotification", "nextSeq=" + this.nextSequenceNumber);
      }

      this.notifyAll();
   }

   private void dropNotification() {
      this.queue.remove(0);
      ++this.earliestSequenceNumber;
   }

   synchronized ArrayNotificationBuffer.NamedNotification notificationAt(long var1) {
      long var3 = var1 - this.earliestSequenceNumber;
      if (var3 >= 0L && var3 <= 2147483647L) {
         return (ArrayNotificationBuffer.NamedNotification)this.queue.get((int)var3);
      } else {
         String var5 = "Bad sequence number: " + var1 + " (earliest " + this.earliestSequenceNumber + ")";
         logger.trace("notificationAt", var5);
         throw new IllegalArgumentException(var5);
      }
   }

   private void createListeners() {
      logger.debug("createListeners", "starts");
      synchronized(this) {
         this.createdDuringQuery = new HashSet();
      }

      try {
         this.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener, creationFilter, (Object)null);
         logger.debug("createListeners", "added creationListener");
      } catch (Exception var6) {
         IllegalArgumentException var3 = new IllegalArgumentException("Can't add listener to MBean server delegate: " + var6);
         EnvHelp.initCause(var3, var6);
         logger.fine("createListeners", "Can't add listener to MBean server delegate: " + var6);
         logger.debug("createListeners", (Throwable)var6);
         throw var3;
      }

      Set var1 = this.queryNames((ObjectName)null, broadcasterQuery);
      HashSet var8 = new HashSet(var1);
      synchronized(this) {
         var8.addAll(this.createdDuringQuery);
         this.createdDuringQuery = null;
      }

      Iterator var2 = var8.iterator();

      while(var2.hasNext()) {
         ObjectName var9 = (ObjectName)var2.next();
         this.addBufferListener(var9);
      }

      logger.debug("createListeners", "ends");
   }

   private void addBufferListener(ObjectName var1) {
      this.checkNoLocks();
      if (logger.debugOn()) {
         logger.debug("addBufferListener", var1.toString());
      }

      try {
         this.addNotificationListener(var1, this.bufferListener, (NotificationFilter)null, var1);
      } catch (Exception var3) {
         logger.trace("addBufferListener", (Throwable)var3);
      }

   }

   private void removeBufferListener(ObjectName var1) {
      this.checkNoLocks();
      if (logger.debugOn()) {
         logger.debug("removeBufferListener", var1.toString());
      }

      try {
         this.removeNotificationListener(var1, this.bufferListener);
      } catch (Exception var3) {
         logger.trace("removeBufferListener", (Throwable)var3);
      }

   }

   private void addNotificationListener(final ObjectName var1, final NotificationListener var2, final NotificationFilter var3, final Object var4) throws Exception {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceNotFoundException {
               ArrayNotificationBuffer.this.mBeanServer.addNotificationListener(var1, var2, var3, var4);
               return null;
            }
         });
      } catch (Exception var6) {
         throw extractException(var6);
      }
   }

   private void removeNotificationListener(final ObjectName var1, final NotificationListener var2) throws Exception {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
               ArrayNotificationBuffer.this.mBeanServer.removeNotificationListener(var1, var2);
               return null;
            }
         });
      } catch (Exception var4) {
         throw extractException(var4);
      }
   }

   private Set<ObjectName> queryNames(final ObjectName var1, final QueryExp var2) {
      PrivilegedAction var3 = new PrivilegedAction<Set<ObjectName>>() {
         public Set<ObjectName> run() {
            return ArrayNotificationBuffer.this.mBeanServer.queryNames(var1, var2);
         }
      };

      try {
         return (Set)AccessController.doPrivileged(var3);
      } catch (RuntimeException var5) {
         logger.fine("queryNames", "Failed to query names: " + var5);
         logger.debug("queryNames", (Throwable)var5);
         throw var5;
      }
   }

   private static boolean isInstanceOf(final MBeanServer var0, final ObjectName var1, final String var2) {
      PrivilegedExceptionAction var3 = new PrivilegedExceptionAction<Boolean>() {
         public Boolean run() throws InstanceNotFoundException {
            return var0.isInstanceOf(var1, var2);
         }
      };

      try {
         return (Boolean)AccessController.doPrivileged(var3);
      } catch (Exception var5) {
         logger.fine("isInstanceOf", "failed: " + var5);
         logger.debug("isInstanceOf", (Throwable)var5);
         return false;
      }
   }

   private void createdNotification(MBeanServerNotification var1) {
      if (!var1.getType().equals("JMX.mbean.registered")) {
         logger.warning("createNotification", "bad type: " + var1.getType());
      } else {
         ObjectName var3 = var1.getMBeanName();
         if (logger.debugOn()) {
            logger.debug("createdNotification", "for: " + var3);
         }

         synchronized(this) {
            if (this.createdDuringQuery != null) {
               this.createdDuringQuery.add(var3);
               return;
            }
         }

         if (isInstanceOf(this.mBeanServer, var3, broadcasterClass)) {
            this.addBufferListener(var3);
            if (this.isDisposed()) {
               this.removeBufferListener(var3);
            }
         }

      }
   }

   private void destroyListeners() {
      this.checkNoLocks();
      logger.debug("destroyListeners", "starts");

      try {
         this.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener);
      } catch (Exception var4) {
         logger.warning("remove listener from MBeanServer delegate", (Throwable)var4);
      }

      Set var1 = this.queryNames((ObjectName)null, broadcasterQuery);

      ObjectName var3;
      for(Iterator var2 = var1.iterator(); var2.hasNext(); this.removeBufferListener(var3)) {
         var3 = (ObjectName)var2.next();
         if (logger.debugOn()) {
            logger.debug("destroyListeners", "remove listener from " + var3);
         }
      }

      logger.debug("destroyListeners", "ends");
   }

   private void checkNoLocks() {
      if (Thread.holdsLock(this) || Thread.holdsLock(globalLock)) {
         logger.warning("checkNoLocks", "lock protocol violation");
      }

   }

   private static Exception extractException(Exception var0) {
      while(var0 instanceof PrivilegedActionException) {
         var0 = ((PrivilegedActionException)var0).getException();
      }

      return var0;
   }

   static {
      NotificationFilterSupport var0 = new NotificationFilterSupport();
      var0.enableType("JMX.mbean.registered");
      creationFilter = var0;
      logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
      broadcasterClass = NotificationBroadcaster.class.getName();
   }

   private static class BroadcasterQuery extends QueryEval implements QueryExp {
      private static final long serialVersionUID = 7378487660587592048L;

      private BroadcasterQuery() {
      }

      public boolean apply(ObjectName var1) {
         MBeanServer var2 = QueryEval.getMBeanServer();
         return ArrayNotificationBuffer.isInstanceOf(var2, var1, ArrayNotificationBuffer.broadcasterClass);
      }

      // $FF: synthetic method
      BroadcasterQuery(Object var1) {
         this();
      }
   }

   private class BufferListener implements NotificationListener {
      private BufferListener() {
      }

      public void handleNotification(Notification var1, Object var2) {
         if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("BufferListener.handleNotification", "notif=" + var1 + "; handback=" + var2);
         }

         ObjectName var3 = (ObjectName)var2;
         ArrayNotificationBuffer.this.addNotification(new ArrayNotificationBuffer.NamedNotification(var3, var1));
      }

      // $FF: synthetic method
      BufferListener(Object var2) {
         this();
      }
   }

   private static class NamedNotification {
      private final ObjectName sender;
      private final Notification notification;

      NamedNotification(ObjectName var1, Notification var2) {
         this.sender = var1;
         this.notification = var2;
      }

      ObjectName getObjectName() {
         return this.sender;
      }

      Notification getNotification() {
         return this.notification;
      }

      public String toString() {
         return "NamedNotification(" + this.sender + ", " + this.notification + ")";
      }
   }

   private class ShareBuffer implements NotificationBuffer {
      private final int size;

      ShareBuffer(int var2) {
         this.size = var2;
         ArrayNotificationBuffer.this.addSharer(this);
      }

      public NotificationResult fetchNotifications(NotificationBufferFilter var1, long var2, long var4, int var6) throws InterruptedException {
         ArrayNotificationBuffer var7 = ArrayNotificationBuffer.this;
         return var7.fetchNotifications(var1, var2, var4, var6);
      }

      public void dispose() {
         ArrayNotificationBuffer.this.removeSharer(this);
      }

      int getSize() {
         return this.size;
      }
   }
}
