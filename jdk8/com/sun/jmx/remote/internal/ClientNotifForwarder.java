package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.NotSerializableException;
import java.rmi.UnmarshalException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;
import javax.security.auth.Subject;

public abstract class ClientNotifForwarder {
   private final AccessControlContext acc;
   private static int threadId;
   private final ClassLoader defaultClassLoader;
   private final Executor executor;
   private final Map<Integer, ClientListenerInfo> infoList;
   private long clientSequenceNumber;
   private final int maxNotifications;
   private final long timeout;
   private Integer mbeanRemovedNotifID;
   private Thread currentFetchThread;
   private static final int STARTING = 0;
   private static final int STARTED = 1;
   private static final int STOPPING = 2;
   private static final int STOPPED = 3;
   private static final int TERMINATED = 4;
   private int state;
   private boolean beingReconnected;
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ClientNotifForwarder");

   public ClientNotifForwarder(Map var1) {
      this((ClassLoader)null, var1);
   }

   public ClientNotifForwarder(ClassLoader var1, Map<String, ?> var2) {
      this.infoList = new HashMap();
      this.clientSequenceNumber = -1L;
      this.mbeanRemovedNotifID = null;
      this.state = 3;
      this.beingReconnected = false;
      this.maxNotifications = EnvHelp.getMaxFetchNotifNumber(var2);
      this.timeout = EnvHelp.getFetchTimeout(var2);
      Object var3 = (Executor)var2.get("jmx.remote.x.fetch.notifications.executor");
      if (var3 == null) {
         var3 = new ClientNotifForwarder.LinearExecutor();
      } else if (logger.traceOn()) {
         logger.trace("ClientNotifForwarder", "executor is " + var3);
      }

      this.defaultClassLoader = var1;
      this.executor = (Executor)var3;
      this.acc = AccessController.getContext();
   }

   protected abstract NotificationResult fetchNotifs(long var1, int var3, long var4) throws IOException, ClassNotFoundException;

   protected abstract Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException;

   protected abstract void removeListenerForMBeanRemovedNotif(Integer var1) throws IOException, InstanceNotFoundException, ListenerNotFoundException;

   protected abstract void lostNotifs(String var1, long var2);

   public synchronized void addNotificationListener(Integer var1, ObjectName var2, NotificationListener var3, NotificationFilter var4, Object var5, Subject var6) throws IOException, InstanceNotFoundException {
      if (logger.traceOn()) {
         logger.trace("addNotificationListener", "Add the listener " + var3 + " at " + var2);
      }

      this.infoList.put(var1, new ClientListenerInfo(var1, var2, var3, var4, var5, var6));
      this.init(false);
   }

   public synchronized Integer[] removeNotificationListener(ObjectName var1, NotificationListener var2) throws ListenerNotFoundException, IOException {
      this.beforeRemove();
      if (logger.traceOn()) {
         logger.trace("removeNotificationListener", "Remove the listener " + var2 + " from " + var1);
      }

      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList(this.infoList.values());

      for(int var5 = var4.size() - 1; var5 >= 0; --var5) {
         ClientListenerInfo var6 = (ClientListenerInfo)var4.get(var5);
         if (var6.sameAs(var1, var2)) {
            var3.add(var6.getListenerID());
            this.infoList.remove(var6.getListenerID());
         }
      }

      if (var3.isEmpty()) {
         throw new ListenerNotFoundException("Listener not found");
      } else {
         return (Integer[])var3.toArray(new Integer[0]);
      }
   }

   public synchronized Integer removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws ListenerNotFoundException, IOException {
      if (logger.traceOn()) {
         logger.trace("removeNotificationListener", "Remove the listener " + var2 + " from " + var1);
      }

      this.beforeRemove();
      Integer var5 = null;
      ArrayList var6 = new ArrayList(this.infoList.values());

      for(int var7 = var6.size() - 1; var7 >= 0; --var7) {
         ClientListenerInfo var8 = (ClientListenerInfo)var6.get(var7);
         if (var8.sameAs(var1, var2, var3, var4)) {
            var5 = var8.getListenerID();
            this.infoList.remove(var5);
            break;
         }
      }

      if (var5 == null) {
         throw new ListenerNotFoundException("Listener not found");
      } else {
         return var5;
      }
   }

   public synchronized Integer[] removeNotificationListener(ObjectName var1) {
      if (logger.traceOn()) {
         logger.trace("removeNotificationListener", "Remove all listeners registered at " + var1);
      }

      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList(this.infoList.values());

      for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
         ClientListenerInfo var5 = (ClientListenerInfo)var3.get(var4);
         if (var5.sameAs(var1)) {
            var2.add(var5.getListenerID());
            this.infoList.remove(var5.getListenerID());
         }
      }

      return (Integer[])var2.toArray(new Integer[0]);
   }

   public synchronized ClientListenerInfo[] preReconnection() throws IOException {
      if (this.state != 4 && !this.beingReconnected) {
         ClientListenerInfo[] var1 = (ClientListenerInfo[])this.infoList.values().toArray(new ClientListenerInfo[0]);
         this.beingReconnected = true;
         this.infoList.clear();
         return var1;
      } else {
         throw new IOException("Illegal state.");
      }
   }

   public synchronized void postReconnection(ClientListenerInfo[] var1) throws IOException {
      if (this.state != 4) {
         while(this.state == 2) {
            try {
               this.wait();
            } catch (InterruptedException var7) {
               IOException var3 = new IOException(var7.toString());
               EnvHelp.initCause(var3, var7);
               throw var3;
            }
         }

         boolean var2 = logger.traceOn();
         int var9 = var1.length;

         for(int var4 = 0; var4 < var9; ++var4) {
            if (var2) {
               logger.trace("addNotificationListeners", "Add a listener at " + var1[var4].getListenerID());
            }

            this.infoList.put(var1[var4].getListenerID(), var1[var4]);
         }

         this.beingReconnected = false;
         this.notifyAll();
         if (this.currentFetchThread != Thread.currentThread() && this.state != 0 && this.state != 1) {
            while(this.state == 2) {
               try {
                  this.wait();
               } catch (InterruptedException var6) {
                  IOException var5 = new IOException(var6.toString());
                  EnvHelp.initCause(var5, var6);
                  throw var5;
               }
            }

            if (var1.length > 0) {
               this.init(true);
            } else if (this.infoList.size() > 0) {
               this.init(false);
            }
         } else {
            try {
               this.mbeanRemovedNotifID = this.addListenerForMBeanRemovedNotif();
            } catch (Exception var8) {
               if (logger.traceOn()) {
                  logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", var8);
               }
            }
         }

      }
   }

   public synchronized void terminate() {
      if (this.state != 4) {
         if (logger.traceOn()) {
            logger.trace("terminate", "Terminating...");
         }

         if (this.state == 1) {
            this.infoList.clear();
         }

         this.setState(4);
      }
   }

   private synchronized void setState(int var1) {
      if (this.state != 4) {
         this.state = var1;
         this.notifyAll();
      }
   }

   private synchronized void init(boolean var1) throws IOException {
      switch(this.state) {
      case 0:
         return;
      case 1:
         return;
      case 2:
         if (this.beingReconnected) {
            return;
         } else {
            while(this.state == 2) {
               try {
                  this.wait();
               } catch (InterruptedException var5) {
                  IOException var3 = new IOException(var5.toString());
                  EnvHelp.initCause(var3, var5);
                  throw var3;
               }
            }

            this.init(var1);
            return;
         }
      case 3:
         if (this.beingReconnected) {
            return;
         } else {
            if (logger.traceOn()) {
               logger.trace("init", "Initializing...");
            }

            if (!var1) {
               try {
                  NotificationResult var2 = this.fetchNotifs(-1L, 0, 0L);
                  if (this.state != 3) {
                     return;
                  }

                  this.clientSequenceNumber = var2.getNextSequenceNumber();
               } catch (ClassNotFoundException var4) {
                  logger.warning("init", "Impossible exception: " + var4);
                  logger.debug("init", (Throwable)var4);
               }
            }

            try {
               this.mbeanRemovedNotifID = this.addListenerForMBeanRemovedNotif();
            } catch (Exception var6) {
               if (logger.traceOn()) {
                  logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", var6);
               }
            }

            this.setState(0);
            this.executor.execute(new ClientNotifForwarder.NotifFetcher());
            return;
         }
      case 4:
         throw new IOException("The ClientNotifForwarder has been terminated.");
      default:
         throw new IOException("Unknown state.");
      }
   }

   private synchronized void beforeRemove() throws IOException {
      while(this.beingReconnected) {
         if (this.state == 4) {
            throw new IOException("Terminated.");
         }

         try {
            this.wait();
         } catch (InterruptedException var3) {
            IOException var2 = new IOException(var3.toString());
            EnvHelp.initCause(var2, var3);
            throw var2;
         }
      }

      if (this.state == 4) {
         throw new IOException("Terminated.");
      }
   }

   private class NotifFetcher implements Runnable {
      private volatile boolean alreadyLogged;

      private NotifFetcher() {
         this.alreadyLogged = false;
      }

      private void logOnce(String var1, SecurityException var2) {
         if (!this.alreadyLogged) {
            ClientNotifForwarder.logger.config("setContextClassLoader", var1);
            if (var2 != null) {
               ClientNotifForwarder.logger.fine("setContextClassLoader", (Throwable)var2);
            }

            this.alreadyLogged = true;
         }
      }

      private final ClassLoader setContextClassLoader(final ClassLoader var1) {
         AccessControlContext var2 = ClientNotifForwarder.this.acc;
         if (var2 == null) {
            this.logOnce("AccessControlContext must not be null.", (SecurityException)null);
            throw new SecurityException("AccessControlContext must not be null");
         } else {
            return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
               public ClassLoader run() {
                  try {
                     ClassLoader var1x = Thread.currentThread().getContextClassLoader();
                     if (var1 == var1x) {
                        return var1x;
                     } else {
                        Thread.currentThread().setContextClassLoader(var1);
                        return var1x;
                     }
                  } catch (SecurityException var2) {
                     NotifFetcher.this.logOnce("Permission to set ContextClassLoader missing. Notifications will not be dispatched. Please check your Java policy configuration: " + var2, var2);
                     throw var2;
                  }
               }
            }, var2);
         }
      }

      public void run() {
         ClassLoader var1;
         if (ClientNotifForwarder.this.defaultClassLoader != null) {
            var1 = this.setContextClassLoader(ClientNotifForwarder.this.defaultClassLoader);
         } else {
            var1 = null;
         }

         try {
            this.doRun();
         } finally {
            if (ClientNotifForwarder.this.defaultClassLoader != null) {
               this.setContextClassLoader(var1);
            }

         }

      }

      private void doRun() {
         synchronized(ClientNotifForwarder.this) {
            ClientNotifForwarder.this.currentFetchThread = Thread.currentThread();
            if (ClientNotifForwarder.this.state == 0) {
               ClientNotifForwarder.this.setState(1);
            }
         }

         NotificationResult var1 = null;
         if (!this.shouldStop() && (var1 = this.fetchNotifs()) != null) {
            TargetedNotification[] var2 = var1.getTargetedNotifications();
            int var3 = var2.length;
            long var6 = 0L;
            HashMap var4;
            Integer var5;
            synchronized(ClientNotifForwarder.this) {
               if (ClientNotifForwarder.this.clientSequenceNumber >= 0L) {
                  var6 = var1.getEarliestSequenceNumber() - ClientNotifForwarder.this.clientSequenceNumber;
               }

               ClientNotifForwarder.this.clientSequenceNumber = var1.getNextSequenceNumber();
               var4 = new HashMap();

               for(int var9 = 0; var9 < var3; ++var9) {
                  TargetedNotification var10 = var2[var9];
                  Integer var11 = var10.getListenerID();
                  if (!var11.equals(ClientNotifForwarder.this.mbeanRemovedNotifID)) {
                     ClientListenerInfo var12 = (ClientListenerInfo)ClientNotifForwarder.this.infoList.get(var11);
                     if (var12 != null) {
                        var4.put(var11, var12);
                     }
                  } else {
                     Notification var24 = var10.getNotification();
                     if (var24 instanceof MBeanServerNotification && var24.getType().equals("JMX.mbean.unregistered")) {
                        MBeanServerNotification var14 = (MBeanServerNotification)var24;
                        ObjectName var15 = var14.getMBeanName();
                        ClientNotifForwarder.this.removeNotificationListener(var15);
                     }
                  }
               }

               var5 = ClientNotifForwarder.this.mbeanRemovedNotifID;
            }

            if (var6 > 0L) {
               String var8 = "May have lost up to " + var6 + " notification" + (var6 == 1L ? "" : "s");
               ClientNotifForwarder.this.lostNotifs(var8, var6);
               ClientNotifForwarder.logger.trace("NotifFetcher.run", var8);
            }

            for(int var22 = 0; var22 < var3; ++var22) {
               TargetedNotification var23 = var2[var22];
               this.dispatchNotification(var23, var5, var4);
            }
         }

         synchronized(ClientNotifForwarder.this) {
            ClientNotifForwarder.this.currentFetchThread = null;
         }

         if (var1 == null && ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("NotifFetcher-run", "Recieved null object as notifs, stops fetching because the notification server is terminated.");
         }

         if (var1 != null && !this.shouldStop()) {
            ClientNotifForwarder.this.executor.execute(this);
         } else {
            ClientNotifForwarder.this.setState(3);

            try {
               ClientNotifForwarder.this.removeListenerForMBeanRemovedNotif(ClientNotifForwarder.this.mbeanRemovedNotifID);
            } catch (Exception var19) {
               if (ClientNotifForwarder.logger.traceOn()) {
                  ClientNotifForwarder.logger.trace("NotifFetcher-run", "removeListenerForMBeanRemovedNotif", var19);
               }
            }
         }

      }

      void dispatchNotification(TargetedNotification var1, Integer var2, Map<Integer, ClientListenerInfo> var3) {
         Notification var4 = var1.getNotification();
         Integer var5 = var1.getListenerID();
         if (!var5.equals(var2)) {
            ClientListenerInfo var6 = (ClientListenerInfo)var3.get(var5);
            if (var6 == null) {
               ClientNotifForwarder.logger.trace("NotifFetcher.dispatch", "Listener ID not in map");
            } else {
               NotificationListener var7 = var6.getListener();
               Object var8 = var6.getHandback();

               try {
                  var7.handleNotification(var4, var8);
               } catch (RuntimeException var11) {
                  ClientNotifForwarder.logger.trace("NotifFetcher-run", "Failed to forward a notification to a listener", var11);
               }

            }
         }
      }

      private NotificationResult fetchNotifs() {
         try {
            NotificationResult var1 = ClientNotifForwarder.this.fetchNotifs(ClientNotifForwarder.this.clientSequenceNumber, ClientNotifForwarder.this.maxNotifications, ClientNotifForwarder.this.timeout);
            if (ClientNotifForwarder.logger.traceOn()) {
               ClientNotifForwarder.logger.trace("NotifFetcher-run", "Got notifications from the server: " + var1);
            }

            return var1;
         } catch (NotSerializableException | UnmarshalException | ClassNotFoundException var2) {
            ClientNotifForwarder.logger.trace("NotifFetcher.fetchNotifs", (Throwable)var2);
            return this.fetchOneNotif();
         } catch (IOException var3) {
            if (!this.shouldStop()) {
               ClientNotifForwarder.logger.error("NotifFetcher-run", "Failed to fetch notification, stopping thread. Error is: " + var3, var3);
               ClientNotifForwarder.logger.debug("NotifFetcher-run", (Throwable)var3);
            }

            return null;
         }
      }

      private NotificationResult fetchOneNotif() {
         ClientNotifForwarder var1 = ClientNotifForwarder.this;
         long var2 = ClientNotifForwarder.this.clientSequenceNumber;
         int var4 = 0;
         NotificationResult var5 = null;
         long var6 = -1L;

         while(true) {
            if (var5 == null && !this.shouldStop()) {
               NotificationResult var14;
               try {
                  var14 = var1.fetchNotifs(var2, 0, 0L);
               } catch (ClassNotFoundException var10) {
                  ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Impossible exception: " + var10);
                  ClientNotifForwarder.logger.debug("NotifFetcher.fetchOneNotif", (Throwable)var10);
                  return null;
               } catch (IOException var11) {
                  if (!this.shouldStop()) {
                     ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", (Throwable)var11);
                  }

                  return null;
               }

               if (!this.shouldStop() && var14 != null) {
                  var2 = var14.getNextSequenceNumber();
                  if (var6 < 0L) {
                     var6 = var14.getEarliestSequenceNumber();
                  }

                  try {
                     var5 = var1.fetchNotifs(var2, 1, 0L);
                     continue;
                  } catch (NotSerializableException | UnmarshalException | ClassNotFoundException var12) {
                     ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification: " + var12.toString());
                     if (ClientNotifForwarder.logger.traceOn()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification.", var12);
                     }

                     ++var4;
                     ++var2;
                     continue;
                  } catch (Exception var13) {
                     if (!this.shouldStop()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", (Throwable)var13);
                     }

                     return null;
                  }
               }

               return null;
            }

            if (var4 > 0) {
               String var8 = "Dropped " + var4 + " notification" + (var4 == 1 ? "" : "s") + " because classes were missing locally or incompatible";
               ClientNotifForwarder.this.lostNotifs(var8, (long)var4);
               if (var5 != null) {
                  var5 = new NotificationResult(var6, var5.getNextSequenceNumber(), var5.getTargetedNotifications());
               }
            }

            return var5;
         }
      }

      private boolean shouldStop() {
         synchronized(ClientNotifForwarder.this) {
            if (ClientNotifForwarder.this.state != 1) {
               return true;
            } else if (ClientNotifForwarder.this.infoList.size() == 0) {
               ClientNotifForwarder.this.setState(2);
               return true;
            } else {
               return false;
            }
         }
      }

      // $FF: synthetic method
      NotifFetcher(Object var2) {
         this();
      }
   }

   private static class LinearExecutor implements Executor {
      private Runnable command;
      private Thread thread;

      private LinearExecutor() {
      }

      public synchronized void execute(Runnable var1) {
         if (this.command != null) {
            throw new IllegalArgumentException("More than one command");
         } else {
            this.command = var1;
            if (this.thread == null) {
               this.thread = new Thread() {
                  public void run() {
                     while(true) {
                        Runnable var1;
                        synchronized(LinearExecutor.this) {
                           if (LinearExecutor.this.command == null) {
                              LinearExecutor.this.thread = null;
                              return;
                           }

                           var1 = LinearExecutor.this.command;
                           LinearExecutor.this.command = null;
                        }

                        var1.run();
                     }
                  }
               };
               this.thread.setDaemon(true);
               this.thread.setName("ClientNotifForwarder-" + ++ClientNotifForwarder.threadId);
               this.thread.start();
            }

         }
      }

      // $FF: synthetic method
      LinearExecutor(Object var1) {
         this();
      }
   }
}
