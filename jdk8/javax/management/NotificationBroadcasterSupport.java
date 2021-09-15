package javax.management;

import com.sun.jmx.remote.util.ClassLogger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class NotificationBroadcasterSupport implements NotificationEmitter {
   private List<NotificationBroadcasterSupport.ListenerInfo> listenerList;
   private final Executor executor;
   private final MBeanNotificationInfo[] notifInfo;
   private static final Executor defaultExecutor = new Executor() {
      public void execute(Runnable var1) {
         var1.run();
      }
   };
   private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
   private static final ClassLogger logger = new ClassLogger("javax.management", "NotificationBroadcasterSupport");

   public NotificationBroadcasterSupport() {
      this((Executor)null, (MBeanNotificationInfo[])null);
   }

   public NotificationBroadcasterSupport(Executor var1) {
      this(var1, (MBeanNotificationInfo[])null);
   }

   public NotificationBroadcasterSupport(MBeanNotificationInfo... var1) {
      this((Executor)null, var1);
   }

   public NotificationBroadcasterSupport(Executor var1, MBeanNotificationInfo... var2) {
      this.listenerList = new CopyOnWriteArrayList();
      this.executor = var1 != null ? var1 : defaultExecutor;
      this.notifInfo = var2 == null ? NO_NOTIFICATION_INFO : (MBeanNotificationInfo[])var2.clone();
   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Listener can't be null");
      } else {
         this.listenerList.add(new NotificationBroadcasterSupport.ListenerInfo(var1, var2, var3));
      }
   }

   public void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      NotificationBroadcasterSupport.WildcardListenerInfo var2 = new NotificationBroadcasterSupport.WildcardListenerInfo(var1);
      boolean var3 = this.listenerList.removeAll(Collections.singleton(var2));
      if (!var3) {
         throw new ListenerNotFoundException("Listener not registered");
      }
   }

   public void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      NotificationBroadcasterSupport.ListenerInfo var4 = new NotificationBroadcasterSupport.ListenerInfo(var1, var2, var3);
      boolean var5 = this.listenerList.remove(var4);
      if (!var5) {
         throw new ListenerNotFoundException("Listener not registered (with this filter and handback)");
      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return this.notifInfo.length == 0 ? this.notifInfo : (MBeanNotificationInfo[])this.notifInfo.clone();
   }

   public void sendNotification(Notification var1) {
      if (var1 != null) {
         Iterator var3 = this.listenerList.iterator();

         while(var3.hasNext()) {
            NotificationBroadcasterSupport.ListenerInfo var4 = (NotificationBroadcasterSupport.ListenerInfo)var3.next();

            boolean var2;
            try {
               var2 = var4.filter == null || var4.filter.isNotificationEnabled(var1);
            } catch (Exception var6) {
               if (logger.debugOn()) {
                  logger.debug("sendNotification", (Throwable)var6);
               }
               continue;
            }

            if (var2) {
               this.executor.execute(new NotificationBroadcasterSupport.SendNotifJob(var1, var4));
            }
         }

      }
   }

   protected void handleNotification(NotificationListener var1, Notification var2, Object var3) {
      var1.handleNotification(var2, var3);
   }

   private class SendNotifJob implements Runnable {
      private final Notification notif;
      private final NotificationBroadcasterSupport.ListenerInfo listenerInfo;

      public SendNotifJob(Notification var2, NotificationBroadcasterSupport.ListenerInfo var3) {
         this.notif = var2;
         this.listenerInfo = var3;
      }

      public void run() {
         try {
            NotificationBroadcasterSupport.this.handleNotification(this.listenerInfo.listener, this.notif, this.listenerInfo.handback);
         } catch (Exception var2) {
            if (NotificationBroadcasterSupport.logger.debugOn()) {
               NotificationBroadcasterSupport.logger.debug("SendNotifJob-run", (Throwable)var2);
            }
         }

      }
   }

   private static class WildcardListenerInfo extends NotificationBroadcasterSupport.ListenerInfo {
      WildcardListenerInfo(NotificationListener var1) {
         super(var1, (NotificationFilter)null, (Object)null);
      }

      public boolean equals(Object var1) {
         assert !(var1 instanceof NotificationBroadcasterSupport.WildcardListenerInfo);

         return var1.equals(this);
      }

      public int hashCode() {
         return super.hashCode();
      }
   }

   private static class ListenerInfo {
      NotificationListener listener;
      NotificationFilter filter;
      Object handback;

      ListenerInfo(NotificationListener var1, NotificationFilter var2, Object var3) {
         this.listener = var1;
         this.filter = var2;
         this.handback = var3;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof NotificationBroadcasterSupport.ListenerInfo)) {
            return false;
         } else {
            NotificationBroadcasterSupport.ListenerInfo var2 = (NotificationBroadcasterSupport.ListenerInfo)var1;
            if (var2 instanceof NotificationBroadcasterSupport.WildcardListenerInfo) {
               return var2.listener == this.listener;
            } else {
               return var2.listener == this.listener && var2.filter == this.filter && var2.handback == this.handback;
            }
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.listener);
      }
   }
}
