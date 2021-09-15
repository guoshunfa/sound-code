package javax.management.timer;

import javax.management.Notification;

public class TimerNotification extends Notification {
   private static final long serialVersionUID = 1798492029603825750L;
   private Integer notificationID;

   public TimerNotification(String var1, Object var2, long var3, long var5, String var7, Integer var8) {
      super(var1, var2, var3, var5, var7);
      this.notificationID = var8;
   }

   public Integer getNotificationID() {
      return this.notificationID;
   }

   Object cloneTimerNotification() {
      TimerNotification var1 = new TimerNotification(this.getType(), this.getSource(), this.getSequenceNumber(), this.getTimeStamp(), this.getMessage(), this.notificationID);
      var1.setUserData(this.getUserData());
      return var1;
   }
}
