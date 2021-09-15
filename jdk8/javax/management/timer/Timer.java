package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class Timer extends NotificationBroadcasterSupport implements TimerMBean, MBeanRegistration {
   public static final long ONE_SECOND = 1000L;
   public static final long ONE_MINUTE = 60000L;
   public static final long ONE_HOUR = 3600000L;
   public static final long ONE_DAY = 86400000L;
   public static final long ONE_WEEK = 604800000L;
   private final Map<Integer, Object[]> timerTable = new HashMap();
   private boolean sendPastNotifications = false;
   private transient boolean isActive = false;
   private transient long sequenceNumber = 0L;
   private static final int TIMER_NOTIF_INDEX = 0;
   private static final int TIMER_DATE_INDEX = 1;
   private static final int TIMER_PERIOD_INDEX = 2;
   private static final int TIMER_NB_OCCUR_INDEX = 3;
   private static final int ALARM_CLOCK_INDEX = 4;
   private static final int FIXED_RATE_INDEX = 5;
   private volatile int counterID = 0;
   private java.util.Timer timer;

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      return var2;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "preDeregister", "stop the timer");
      this.stop();
   }

   public void postDeregister() {
   }

   public synchronized MBeanNotificationInfo[] getNotificationInfo() {
      TreeSet var1 = new TreeSet();
      Iterator var2 = this.timerTable.values().iterator();

      while(var2.hasNext()) {
         Object[] var3 = (Object[])var2.next();
         TimerNotification var4 = (TimerNotification)var3[0];
         var1.add(var4.getType());
      }

      String[] var5 = (String[])var1.toArray(new String[0]);
      return new MBeanNotificationInfo[]{new MBeanNotificationInfo(var5, TimerNotification.class.getName(), "Notification sent by Timer MBean")};
   }

   public synchronized void start() {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "starting the timer");
      if (!this.isActive) {
         this.timer = new java.util.Timer();
         Date var3 = new Date();
         this.sendPastNotifications(var3, this.sendPastNotifications);
         Iterator var4 = this.timerTable.values().iterator();

         while(var4.hasNext()) {
            Object[] var5 = (Object[])var4.next();
            Date var2 = (Date)var5[1];
            boolean var6 = (Boolean)var5[5];
            TimerAlarmClock var1;
            if (var6) {
               var1 = new TimerAlarmClock(this, var2);
               var5[4] = var1;
               this.timer.schedule(var1, var1.next);
            } else {
               var1 = new TimerAlarmClock(this, var2.getTime() - var3.getTime());
               var5[4] = var1;
               this.timer.schedule(var1, var1.timeout);
            }
         }

         this.isActive = true;
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "timer started");
      } else {
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "the timer is already activated");
      }

   }

   public synchronized void stop() {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "stopping the timer");
      if (this.isActive) {
         Iterator var1 = this.timerTable.values().iterator();

         while(var1.hasNext()) {
            Object[] var2 = (Object[])var1.next();
            TimerAlarmClock var3 = (TimerAlarmClock)var2[4];
            if (var3 != null) {
               var3.cancel();
            }
         }

         this.timer.cancel();
         this.isActive = false;
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "timer stopped");
      } else {
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "the timer is already deactivated");
      }

   }

   public synchronized Integer addNotification(String var1, String var2, Object var3, Date var4, long var5, long var7, boolean var9) throws IllegalArgumentException {
      if (var4 == null) {
         throw new IllegalArgumentException("Timer notification date cannot be null.");
      } else if (var5 >= 0L && var7 >= 0L) {
         Date var10 = new Date();
         if (var10.after(var4)) {
            var4.setTime(var10.getTime());
            if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "update timer notification to add with:\n\tNotification date = " + var4);
            }
         }

         Integer var11 = ++this.counterID;
         TimerNotification var12 = new TimerNotification(var1, this, 0L, 0L, var2, var11);
         var12.setUserData(var3);
         Object[] var13 = new Object[6];
         TimerAlarmClock var14;
         if (var9) {
            var14 = new TimerAlarmClock(this, var4);
         } else {
            var14 = new TimerAlarmClock(this, var4.getTime() - var10.getTime());
         }

         Date var15 = new Date(var4.getTime());
         var13[0] = var12;
         var13[1] = var15;
         var13[2] = var5;
         var13[3] = var7;
         var13[4] = var14;
         var13[5] = var9;
         if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
            StringBuilder var16 = (new StringBuilder()).append("adding timer notification:\n\t").append("Notification source = ").append(var12.getSource()).append("\n\tNotification type = ").append(var12.getType()).append("\n\tNotification ID = ").append((Object)var11).append("\n\tNotification date = ").append((Object)var15).append("\n\tNotification period = ").append(var5).append("\n\tNotification nb of occurrences = ").append(var7).append("\n\tNotification executes at fixed rate = ").append(var9);
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", var16.toString());
         }

         this.timerTable.put(var11, var13);
         if (this.isActive) {
            if (var9) {
               this.timer.schedule(var14, var14.next);
            } else {
               this.timer.schedule(var14, var14.timeout);
            }
         }

         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "timer notification added");
         return var11;
      } else {
         throw new IllegalArgumentException("Negative values for the periodicity");
      }
   }

   public synchronized Integer addNotification(String var1, String var2, Object var3, Date var4, long var5, long var7) throws IllegalArgumentException {
      return this.addNotification(var1, var2, var3, var4, var5, var7, false);
   }

   public synchronized Integer addNotification(String var1, String var2, Object var3, Date var4, long var5) throws IllegalArgumentException {
      return this.addNotification(var1, var2, var3, var4, var5, 0L);
   }

   public synchronized Integer addNotification(String var1, String var2, Object var3, Date var4) throws IllegalArgumentException {
      return this.addNotification(var1, var2, var3, var4, 0L, 0L);
   }

   public synchronized void removeNotification(Integer var1) throws InstanceNotFoundException {
      if (!this.timerTable.containsKey(var1)) {
         throw new InstanceNotFoundException("Timer notification to remove not in the list of notifications");
      } else {
         Object[] var2 = (Object[])this.timerTable.get(var1);
         TimerAlarmClock var3 = (TimerAlarmClock)var2[4];
         if (var3 != null) {
            var3.cancel();
         }

         if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
            StringBuilder var4 = (new StringBuilder()).append("removing timer notification:").append("\n\tNotification source = ").append(((TimerNotification)var2[0]).getSource()).append("\n\tNotification type = ").append(((TimerNotification)var2[0]).getType()).append("\n\tNotification ID = ").append((Object)((TimerNotification)var2[0]).getNotificationID()).append("\n\tNotification date = ").append(var2[1]).append("\n\tNotification period = ").append(var2[2]).append("\n\tNotification nb of occurrences = ").append(var2[3]).append("\n\tNotification executes at fixed rate = ").append(var2[5]);
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", var4.toString());
         }

         this.timerTable.remove(var1);
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", "timer notification removed");
      }
   }

   public synchronized void removeNotifications(String var1) throws InstanceNotFoundException {
      Vector var2 = this.getNotificationIDs(var1);
      if (var2.isEmpty()) {
         throw new InstanceNotFoundException("Timer notifications to remove not in the list of notifications");
      } else {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Integer var4 = (Integer)var3.next();
            this.removeNotification(var4);
         }

      }
   }

   public synchronized void removeAllNotifications() {
      Iterator var2 = this.timerTable.values().iterator();

      while(var2.hasNext()) {
         Object[] var3 = (Object[])var2.next();
         TimerAlarmClock var1 = (TimerAlarmClock)var3[4];
         var1.cancel();
      }

      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "removing all timer notifications");
      this.timerTable.clear();
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "all timer notifications removed");
      this.counterID = 0;
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "timer notification counter ID reset");
   }

   public synchronized int getNbNotifications() {
      return this.timerTable.size();
   }

   public synchronized Vector<Integer> getAllNotificationIDs() {
      return new Vector(this.timerTable.keySet());
   }

   public synchronized Vector<Integer> getNotificationIDs(String var1) {
      Vector var3 = new Vector();
      Iterator var4 = this.timerTable.entrySet().iterator();

      while(true) {
         Map.Entry var5;
         while(true) {
            if (!var4.hasNext()) {
               return var3;
            }

            var5 = (Map.Entry)var4.next();
            Object[] var6 = (Object[])var5.getValue();
            String var2 = ((TimerNotification)var6[0]).getType();
            if (var1 == null) {
               if (var2 == null) {
                  break;
               }
            } else if (var1.equals(var2)) {
               break;
            }
         }

         var3.addElement(var5.getKey());
      }
   }

   public synchronized String getNotificationType(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      return var2 != null ? ((TimerNotification)var2[0]).getType() : null;
   }

   public synchronized String getNotificationMessage(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      return var2 != null ? ((TimerNotification)var2[0]).getMessage() : null;
   }

   public synchronized Object getNotificationUserData(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      return var2 != null ? ((TimerNotification)var2[0]).getUserData() : null;
   }

   public synchronized Date getDate(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      if (var2 != null) {
         Date var3 = (Date)var2[1];
         return new Date(var3.getTime());
      } else {
         return null;
      }
   }

   public synchronized Long getPeriod(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      return var2 != null ? (Long)var2[2] : null;
   }

   public synchronized Long getNbOccurences(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      return var2 != null ? (Long)var2[3] : null;
   }

   public synchronized Boolean getFixedRate(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      if (var2 != null) {
         Boolean var3 = (Boolean)var2[5];
         return var3;
      } else {
         return null;
      }
   }

   public boolean getSendPastNotifications() {
      return this.sendPastNotifications;
   }

   public void setSendPastNotifications(boolean var1) {
      this.sendPastNotifications = var1;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public synchronized boolean isEmpty() {
      return this.timerTable.isEmpty();
   }

   private synchronized void sendPastNotifications(Date var1, boolean var2) {
      ArrayList var6 = new ArrayList(this.timerTable.values());
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         Object[] var8 = (Object[])var7.next();
         TimerNotification var3 = (TimerNotification)var8[0];
         Integer var4 = var3.getNotificationID();

         for(Date var5 = (Date)var8[1]; var1.after(var5) && this.timerTable.containsKey(var4); this.updateTimerTable(var3.getNotificationID())) {
            if (var2) {
               if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
                  StringBuilder var9 = (new StringBuilder()).append("sending past timer notification:").append("\n\tNotification source = ").append(var3.getSource()).append("\n\tNotification type = ").append(var3.getType()).append("\n\tNotification ID = ").append((Object)var3.getNotificationID()).append("\n\tNotification date = ").append((Object)var5).append("\n\tNotification period = ").append(var8[2]).append("\n\tNotification nb of occurrences = ").append(var8[3]).append("\n\tNotification executes at fixed rate = ").append(var8[5]);
                  JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", var9.toString());
               }

               this.sendNotification(var5, var3);
               JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", "past timer notification sent");
            }
         }
      }

   }

   private synchronized void updateTimerTable(Integer var1) {
      Object[] var2 = (Object[])this.timerTable.get(var1);
      Date var3 = (Date)var2[1];
      Long var4 = (Long)var2[2];
      Long var5 = (Long)var2[3];
      Boolean var6 = (Boolean)var2[5];
      TimerAlarmClock var7 = (TimerAlarmClock)var2[4];
      if (var4 != 0L) {
         if (var5 != 0L && var5 <= 1L) {
            if (var7 != null) {
               var7.cancel();
            }

            this.timerTable.remove(var1);
         } else {
            var3.setTime(var3.getTime() + var4);
            var2[3] = Math.max(0L, var5 - 1L);
            var5 = (Long)var2[3];
            if (this.isActive) {
               if (var6) {
                  var7 = new TimerAlarmClock(this, var3);
                  var2[4] = var7;
                  this.timer.schedule(var7, var7.next);
               } else {
                  var7 = new TimerAlarmClock(this, var4);
                  var2[4] = var7;
                  this.timer.schedule(var7, var7.timeout);
               }
            }

            if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
               TimerNotification var8 = (TimerNotification)var2[0];
               StringBuilder var9 = (new StringBuilder()).append("update timer notification with:").append("\n\tNotification source = ").append(var8.getSource()).append("\n\tNotification type = ").append(var8.getType()).append("\n\tNotification ID = ").append((Object)var1).append("\n\tNotification date = ").append((Object)var3).append("\n\tNotification period = ").append((Object)var4).append("\n\tNotification nb of occurrences = ").append((Object)var5).append("\n\tNotification executes at fixed rate = ").append((Object)var6);
               JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "updateTimerTable", var9.toString());
            }
         }
      } else {
         if (var7 != null) {
            var7.cancel();
         }

         this.timerTable.remove(var1);
      }

   }

   void notifyAlarmClock(TimerAlarmClockNotification var1) {
      TimerNotification var2 = null;
      Date var3 = null;
      TimerAlarmClock var4 = (TimerAlarmClock)var1.getSource();
      synchronized(this) {
         Iterator var6 = this.timerTable.values().iterator();

         while(var6.hasNext()) {
            Object[] var7 = (Object[])var6.next();
            if (var7[4] == var4) {
               var2 = (TimerNotification)var7[0];
               var3 = (Date)var7[1];
               break;
            }
         }
      }

      this.sendNotification(var3, var2);
      this.updateTimerTable(var2.getNotificationID());
   }

   void sendNotification(Date var1, TimerNotification var2) {
      if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var3 = (new StringBuilder()).append("sending timer notification:").append("\n\tNotification source = ").append(var2.getSource()).append("\n\tNotification type = ").append(var2.getType()).append("\n\tNotification ID = ").append((Object)var2.getNotificationID()).append("\n\tNotification date = ").append((Object)var1);
         JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", var3.toString());
      }

      long var10;
      synchronized(this) {
         ++this.sequenceNumber;
         var10 = this.sequenceNumber;
      }

      synchronized(var2) {
         var2.setTimeStamp(var1.getTime());
         var2.setSequenceNumber(var10);
         this.sendNotification((TimerNotification)var2.cloneTimerNotification());
      }

      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", "timer notification sent");
   }
}
