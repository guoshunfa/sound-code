package javax.management.timer;

import javax.management.Notification;

class TimerAlarmClockNotification extends Notification {
   private static final long serialVersionUID = -4841061275673620641L;

   public TimerAlarmClockNotification(TimerAlarmClock var1) {
      super("", var1, 0L);
   }
}
