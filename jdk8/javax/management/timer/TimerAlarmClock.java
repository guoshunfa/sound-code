package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;

class TimerAlarmClock extends TimerTask {
   Timer listener = null;
   long timeout = 10000L;
   Date next = null;

   public TimerAlarmClock(Timer var1, long var2) {
      this.listener = var1;
      this.timeout = Math.max(0L, var2);
   }

   public TimerAlarmClock(Timer var1, Date var2) {
      this.listener = var1;
      this.next = var2;
   }

   public void run() {
      try {
         TimerAlarmClockNotification var1 = new TimerAlarmClockNotification(this);
         this.listener.notifyAlarmClock(var1);
      } catch (Exception var2) {
         JmxProperties.TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run", (String)"Got unexpected exception when sending a notification", (Throwable)var2);
      }

   }
}
