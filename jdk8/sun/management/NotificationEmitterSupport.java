package sun.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

abstract class NotificationEmitterSupport implements NotificationEmitter {
   private Object listenerLock = new Object();
   private List<NotificationEmitterSupport.ListenerInfo> listenerList = Collections.emptyList();

   protected NotificationEmitterSupport() {
   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Listener can't be null");
      } else {
         synchronized(this.listenerLock) {
            ArrayList var5 = new ArrayList(this.listenerList.size() + 1);
            var5.addAll(this.listenerList);
            var5.add(new NotificationEmitterSupport.ListenerInfo(var1, var2, var3));
            this.listenerList = var5;
         }
      }
   }

   public void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      synchronized(this.listenerLock) {
         ArrayList var3 = new ArrayList(this.listenerList);

         for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
            NotificationEmitterSupport.ListenerInfo var5 = (NotificationEmitterSupport.ListenerInfo)var3.get(var4);
            if (var5.listener == var1) {
               var3.remove(var4);
            }
         }

         if (var3.size() == this.listenerList.size()) {
            throw new ListenerNotFoundException("Listener not registered");
         } else {
            this.listenerList = var3;
         }
      }
   }

   public void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      boolean var4 = false;
      synchronized(this.listenerLock) {
         ArrayList var6 = new ArrayList(this.listenerList);
         int var7 = var6.size();

         for(int var8 = 0; var8 < var7; ++var8) {
            NotificationEmitterSupport.ListenerInfo var9 = (NotificationEmitterSupport.ListenerInfo)var6.get(var8);
            if (var9.listener == var1) {
               var4 = true;
               if (var9.filter == var2 && var9.handback == var3) {
                  var6.remove(var8);
                  this.listenerList = var6;
                  return;
               }
            }
         }
      }

      if (var4) {
         throw new ListenerNotFoundException("Listener not registered with this filter and handback");
      } else {
         throw new ListenerNotFoundException("Listener not registered");
      }
   }

   void sendNotification(Notification var1) {
      if (var1 != null) {
         List var2;
         synchronized(this.listenerLock) {
            var2 = this.listenerList;
         }

         int var3 = var2.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            NotificationEmitterSupport.ListenerInfo var5 = (NotificationEmitterSupport.ListenerInfo)var2.get(var4);
            if (var5.filter == null || var5.filter.isNotificationEnabled(var1)) {
               try {
                  var5.listener.handleNotification(var1, var5.handback);
               } catch (Exception var7) {
                  var7.printStackTrace();
                  throw new AssertionError("Error in invoking listener");
               }
            }
         }

      }
   }

   boolean hasListeners() {
      synchronized(this.listenerLock) {
         return !this.listenerList.isEmpty();
      }
   }

   public abstract MBeanNotificationInfo[] getNotificationInfo();

   private class ListenerInfo {
      public NotificationListener listener;
      NotificationFilter filter;
      Object handback;

      public ListenerInfo(NotificationListener var2, NotificationFilter var3, Object var4) {
         this.listener = var2;
         this.filter = var3;
         this.handback = var4;
      }
   }
}
