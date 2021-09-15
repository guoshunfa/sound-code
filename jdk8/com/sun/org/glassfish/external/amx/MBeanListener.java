package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;

@Taxonomy(
   stability = Stability.UNCOMMITTED
)
public class MBeanListener<T extends MBeanListener.Callback> implements NotificationListener {
   private final String mJMXDomain;
   private final String mType;
   private final String mName;
   private final ObjectName mObjectName;
   private final MBeanServerConnection mMBeanServer;
   private final T mCallback;

   private static void debug(Object o) {
      System.out.println("" + o);
   }

   public String toString() {
      return "MBeanListener: ObjectName=" + this.mObjectName + ", type=" + this.mType + ", name=" + this.mName;
   }

   public String getType() {
      return this.mType;
   }

   public String getName() {
      return this.mName;
   }

   public MBeanServerConnection getMBeanServer() {
      return this.mMBeanServer;
   }

   public T getCallback() {
      return this.mCallback;
   }

   public MBeanListener(MBeanServerConnection server, ObjectName objectName, T callback) {
      this.mMBeanServer = server;
      this.mObjectName = objectName;
      this.mJMXDomain = null;
      this.mType = null;
      this.mName = null;
      this.mCallback = callback;
   }

   public MBeanListener(MBeanServerConnection server, String domain, String type, T callback) {
      this(server, domain, type, (String)null, callback);
   }

   public MBeanListener(MBeanServerConnection server, String domain, String type, String name, T callback) {
      this.mMBeanServer = server;
      this.mJMXDomain = domain;
      this.mType = type;
      this.mName = name;
      this.mObjectName = null;
      this.mCallback = callback;
   }

   private boolean isRegistered(MBeanServerConnection conn, ObjectName objectName) {
      try {
         return conn.isRegistered(objectName);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public void startListening() {
      try {
         this.mMBeanServer.addNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), (NotificationListener)this, (NotificationFilter)null, this);
      } catch (Exception var6) {
         throw new RuntimeException("Can't add NotificationListener", var6);
      }

      if (this.mObjectName != null) {
         if (this.isRegistered(this.mMBeanServer, this.mObjectName)) {
            this.mCallback.mbeanRegistered(this.mObjectName, this);
         }
      } else {
         String props = "type=" + this.mType;
         if (this.mName != null) {
            props = props + "," + "name" + this.mName;
         }

         ObjectName pattern = AMXUtil.newObjectName(this.mJMXDomain + ":" + props);

         try {
            Set<ObjectName> matched = this.mMBeanServer.queryNames(pattern, (QueryExp)null);
            Iterator var4 = matched.iterator();

            while(var4.hasNext()) {
               ObjectName objectName = (ObjectName)var4.next();
               this.mCallback.mbeanRegistered(objectName, this);
            }
         } catch (Exception var7) {
            throw new RuntimeException(var7);
         }
      }

   }

   public void stopListening() {
      try {
         this.mMBeanServer.removeNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), (NotificationListener)this);
      } catch (Exception var2) {
         throw new RuntimeException("Can't remove NotificationListener " + this, var2);
      }
   }

   public void handleNotification(Notification notifIn, Object handback) {
      if (notifIn instanceof MBeanServerNotification) {
         MBeanServerNotification notif = (MBeanServerNotification)notifIn;
         ObjectName objectName = notif.getMBeanName();
         boolean match = false;
         String notifType;
         if (this.mObjectName != null && this.mObjectName.equals(objectName)) {
            match = true;
         } else if (objectName.getDomain().equals(this.mJMXDomain) && this.mType != null && this.mType.equals(objectName.getKeyProperty("type"))) {
            notifType = objectName.getKeyProperty("name");
            if (this.mName != null && this.mName.equals(notifType)) {
               match = true;
            }
         }

         if (match) {
            notifType = notif.getType();
            if ("JMX.mbean.registered".equals(notifType)) {
               this.mCallback.mbeanRegistered(objectName, this);
            } else if ("JMX.mbean.unregistered".equals(notifType)) {
               this.mCallback.mbeanUnregistered(objectName, this);
            }
         }
      }

   }

   public static class CallbackImpl implements MBeanListener.Callback {
      private volatile ObjectName mRegistered;
      private volatile ObjectName mUnregistered;
      private final boolean mStopAtFirst;
      protected final CountDownLatch mLatch;

      public CallbackImpl() {
         this(true);
      }

      public CallbackImpl(boolean stopAtFirst) {
         this.mRegistered = null;
         this.mUnregistered = null;
         this.mLatch = new CountDownLatch(1);
         this.mStopAtFirst = stopAtFirst;
      }

      public ObjectName getRegistered() {
         return this.mRegistered;
      }

      public ObjectName getUnregistered() {
         return this.mUnregistered;
      }

      public void await() {
         try {
            this.mLatch.await();
         } catch (InterruptedException var2) {
            throw new RuntimeException(var2);
         }
      }

      public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
         this.mRegistered = objectName;
         if (this.mStopAtFirst) {
            listener.stopListening();
         }

      }

      public void mbeanUnregistered(ObjectName objectName, MBeanListener listener) {
         this.mUnregistered = objectName;
         if (this.mStopAtFirst) {
            listener.stopListening();
         }

      }
   }

   public interface Callback {
      void mbeanRegistered(ObjectName var1, MBeanListener var2);

      void mbeanUnregistered(ObjectName var1, MBeanListener var2);
   }
}
