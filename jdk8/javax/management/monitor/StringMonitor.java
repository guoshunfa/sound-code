package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class StringMonitor extends Monitor implements StringMonitorMBean {
   private String stringToCompare = "";
   private boolean notifyMatch = false;
   private boolean notifyDiffer = false;
   private static final String[] types = new String[]{"jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.string.matches", "jmx.monitor.string.differs"};
   private static final MBeanNotificationInfo[] notifsInfo;
   private static final int MATCHING = 0;
   private static final int DIFFERING = 1;
   private static final int MATCHING_OR_DIFFERING = 2;

   public synchronized void start() {
      if (this.isActive()) {
         JmxProperties.MONITOR_LOGGER.logp(Level.FINER, StringMonitor.class.getName(), "start", "the monitor is already active");
      } else {
         Iterator var1 = this.observedObjects.iterator();

         while(var1.hasNext()) {
            Monitor.ObservedObject var2 = (Monitor.ObservedObject)var1.next();
            StringMonitor.StringMonitorObservedObject var3 = (StringMonitor.StringMonitorObservedObject)var2;
            var3.setStatus(2);
         }

         this.doStart();
      }
   }

   public synchronized void stop() {
      this.doStop();
   }

   public synchronized String getDerivedGauge(ObjectName var1) {
      return (String)super.getDerivedGauge(var1);
   }

   public synchronized long getDerivedGaugeTimeStamp(ObjectName var1) {
      return super.getDerivedGaugeTimeStamp(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized String getDerivedGauge() {
      return this.observedObjects.isEmpty() ? null : (String)((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGauge();
   }

   /** @deprecated */
   @Deprecated
   public synchronized long getDerivedGaugeTimeStamp() {
      return this.observedObjects.isEmpty() ? 0L : ((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGaugeTimeStamp();
   }

   public synchronized String getStringToCompare() {
      return this.stringToCompare;
   }

   public synchronized void setStringToCompare(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null string to compare");
      } else if (!this.stringToCompare.equals(var1)) {
         this.stringToCompare = var1;
         Iterator var2 = this.observedObjects.iterator();

         while(var2.hasNext()) {
            Monitor.ObservedObject var3 = (Monitor.ObservedObject)var2.next();
            StringMonitor.StringMonitorObservedObject var4 = (StringMonitor.StringMonitorObservedObject)var3;
            var4.setStatus(2);
         }

      }
   }

   public synchronized boolean getNotifyMatch() {
      return this.notifyMatch;
   }

   public synchronized void setNotifyMatch(boolean var1) {
      if (this.notifyMatch != var1) {
         this.notifyMatch = var1;
      }
   }

   public synchronized boolean getNotifyDiffer() {
      return this.notifyDiffer;
   }

   public synchronized void setNotifyDiffer(boolean var1) {
      if (this.notifyDiffer != var1) {
         this.notifyDiffer = var1;
      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return (MBeanNotificationInfo[])notifsInfo.clone();
   }

   Monitor.ObservedObject createObservedObject(ObjectName var1) {
      StringMonitor.StringMonitorObservedObject var2 = new StringMonitor.StringMonitorObservedObject(var1);
      var2.setStatus(2);
      return var2;
   }

   synchronized boolean isComparableTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      return var3 instanceof String;
   }

   synchronized void onErrorNotification(MonitorNotification var1) {
      StringMonitor.StringMonitorObservedObject var2 = (StringMonitor.StringMonitorObservedObject)this.getObservedObject(var1.getObservedObject());
      if (var2 != null) {
         var2.setStatus(2);
      }
   }

   synchronized MonitorNotification buildAlarmNotification(ObjectName var1, String var2, Comparable<?> var3) {
      String var4 = null;
      String var5 = null;
      String var6 = null;
      StringMonitor.StringMonitorObservedObject var7 = (StringMonitor.StringMonitorObservedObject)this.getObservedObject(var1);
      if (var7 == null) {
         return null;
      } else {
         if (var7.getStatus() == 2) {
            if (var7.getDerivedGauge().equals(this.stringToCompare)) {
               if (this.notifyMatch) {
                  var4 = "jmx.monitor.string.matches";
                  var5 = "";
                  var6 = this.stringToCompare;
               }

               var7.setStatus(1);
            } else {
               if (this.notifyDiffer) {
                  var4 = "jmx.monitor.string.differs";
                  var5 = "";
                  var6 = this.stringToCompare;
               }

               var7.setStatus(0);
            }
         } else if (var7.getStatus() == 0) {
            if (var7.getDerivedGauge().equals(this.stringToCompare)) {
               if (this.notifyMatch) {
                  var4 = "jmx.monitor.string.matches";
                  var5 = "";
                  var6 = this.stringToCompare;
               }

               var7.setStatus(1);
            }
         } else if (var7.getStatus() == 1 && !var7.getDerivedGauge().equals(this.stringToCompare)) {
            if (this.notifyDiffer) {
               var4 = "jmx.monitor.string.differs";
               var5 = "";
               var6 = this.stringToCompare;
            }

            var7.setStatus(0);
         }

         return new MonitorNotification(var4, this, 0L, 0L, var5, (ObjectName)null, (String)null, (Object)null, var6);
      }
   }

   static {
      notifsInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the StringMonitor MBean")};
   }

   static class StringMonitorObservedObject extends Monitor.ObservedObject {
      private int status;

      public StringMonitorObservedObject(ObjectName var1) {
         super(var1);
      }

      public final synchronized int getStatus() {
         return this.status;
      }

      public final synchronized void setStatus(int var1) {
         this.status = var1;
      }
   }
}
