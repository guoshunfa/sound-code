package javax.management;

public class StandardEmitterMBean extends StandardMBean implements NotificationEmitter {
   private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
   private final NotificationEmitter emitter;
   private final MBeanNotificationInfo[] notificationInfo;

   public <T> StandardEmitterMBean(T var1, Class<T> var2, NotificationEmitter var3) {
      this(var1, var2, false, var3);
   }

   public <T> StandardEmitterMBean(T var1, Class<T> var2, boolean var3, NotificationEmitter var4) {
      super(var1, var2, var3);
      if (var4 == null) {
         throw new IllegalArgumentException("Null emitter");
      } else {
         this.emitter = var4;
         MBeanNotificationInfo[] var5 = var4.getNotificationInfo();
         if (var5 != null && var5.length != 0) {
            this.notificationInfo = (MBeanNotificationInfo[])var5.clone();
         } else {
            this.notificationInfo = NO_NOTIFICATION_INFO;
         }

      }
   }

   protected StandardEmitterMBean(Class<?> var1, NotificationEmitter var2) {
      this(var1, false, var2);
   }

   protected StandardEmitterMBean(Class<?> var1, boolean var2, NotificationEmitter var3) {
      super(var1, var2);
      if (var3 == null) {
         throw new IllegalArgumentException("Null emitter");
      } else {
         this.emitter = var3;
         MBeanNotificationInfo[] var4 = var3.getNotificationInfo();
         if (var4 != null && var4.length != 0) {
            this.notificationInfo = (MBeanNotificationInfo[])var4.clone();
         } else {
            this.notificationInfo = NO_NOTIFICATION_INFO;
         }

      }
   }

   public void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      this.emitter.removeNotificationListener(var1);
   }

   public void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      this.emitter.removeNotificationListener(var1, var2, var3);
   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      this.emitter.addNotificationListener(var1, var2, var3);
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      if (this.notificationInfo == null) {
         return NO_NOTIFICATION_INFO;
      } else {
         return this.notificationInfo.length == 0 ? this.notificationInfo : (MBeanNotificationInfo[])this.notificationInfo.clone();
      }
   }

   public void sendNotification(Notification var1) {
      if (this.emitter instanceof NotificationBroadcasterSupport) {
         ((NotificationBroadcasterSupport)this.emitter).sendNotification(var1);
      } else {
         String var2 = "Cannot sendNotification when emitter is not an instance of NotificationBroadcasterSupport: " + this.emitter.getClass().getName();
         throw new ClassCastException(var2);
      }
   }

   MBeanNotificationInfo[] getNotifications(MBeanInfo var1) {
      return this.getNotificationInfo();
   }
}
