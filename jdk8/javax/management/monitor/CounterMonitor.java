package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class CounterMonitor extends Monitor implements CounterMonitorMBean {
   private Number modulus;
   private Number offset;
   private boolean notify;
   private boolean differenceMode;
   private Number initThreshold;
   private static final String[] types = new String[]{"jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.counter.threshold"};
   private static final MBeanNotificationInfo[] notifsInfo;

   public CounterMonitor() {
      this.modulus = INTEGER_ZERO;
      this.offset = INTEGER_ZERO;
      this.notify = false;
      this.differenceMode = false;
      this.initThreshold = INTEGER_ZERO;
   }

   public synchronized void start() {
      if (this.isActive()) {
         JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "start", "the monitor is already active");
      } else {
         Iterator var1 = this.observedObjects.iterator();

         while(var1.hasNext()) {
            Monitor.ObservedObject var2 = (Monitor.ObservedObject)var1.next();
            CounterMonitor.CounterMonitorObservedObject var3 = (CounterMonitor.CounterMonitorObservedObject)var2;
            var3.setThreshold(this.initThreshold);
            var3.setModulusExceeded(false);
            var3.setEventAlreadyNotified(false);
            var3.setPreviousScanCounter((Number)null);
         }

         this.doStart();
      }
   }

   public synchronized void stop() {
      this.doStop();
   }

   public synchronized Number getDerivedGauge(ObjectName var1) {
      return (Number)super.getDerivedGauge(var1);
   }

   public synchronized long getDerivedGaugeTimeStamp(ObjectName var1) {
      return super.getDerivedGaugeTimeStamp(var1);
   }

   public synchronized Number getThreshold(ObjectName var1) {
      CounterMonitor.CounterMonitorObservedObject var2 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1);
      if (var2 == null) {
         return null;
      } else {
         return this.offset.longValue() > 0L && this.modulus.longValue() > 0L && var2.getThreshold().longValue() > this.modulus.longValue() ? this.initThreshold : var2.getThreshold();
      }
   }

   public synchronized Number getInitThreshold() {
      return this.initThreshold;
   }

   public synchronized void setInitThreshold(Number var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null threshold");
      } else if (var1.longValue() < 0L) {
         throw new IllegalArgumentException("Negative threshold");
      } else if (!this.initThreshold.equals(var1)) {
         this.initThreshold = var1;
         int var2 = 0;
         Iterator var3 = this.observedObjects.iterator();

         while(var3.hasNext()) {
            Monitor.ObservedObject var4 = (Monitor.ObservedObject)var3.next();
            this.resetAlreadyNotified(var4, var2++, 16);
            CounterMonitor.CounterMonitorObservedObject var5 = (CounterMonitor.CounterMonitorObservedObject)var4;
            var5.setThreshold(var1);
            var5.setModulusExceeded(false);
            var5.setEventAlreadyNotified(false);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public synchronized Number getDerivedGauge() {
      return this.observedObjects.isEmpty() ? null : (Number)((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGauge();
   }

   /** @deprecated */
   @Deprecated
   public synchronized long getDerivedGaugeTimeStamp() {
      return this.observedObjects.isEmpty() ? 0L : ((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGaugeTimeStamp();
   }

   /** @deprecated */
   @Deprecated
   public synchronized Number getThreshold() {
      return this.getThreshold(this.getObservedObject());
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setThreshold(Number var1) throws IllegalArgumentException {
      this.setInitThreshold(var1);
   }

   public synchronized Number getOffset() {
      return this.offset;
   }

   public synchronized void setOffset(Number var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null offset");
      } else if (var1.longValue() < 0L) {
         throw new IllegalArgumentException("Negative offset");
      } else if (!this.offset.equals(var1)) {
         this.offset = var1;
         int var2 = 0;
         Iterator var3 = this.observedObjects.iterator();

         while(var3.hasNext()) {
            Monitor.ObservedObject var4 = (Monitor.ObservedObject)var3.next();
            this.resetAlreadyNotified(var4, var2++, 16);
         }

      }
   }

   public synchronized Number getModulus() {
      return this.modulus;
   }

   public synchronized void setModulus(Number var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null modulus");
      } else if (var1.longValue() < 0L) {
         throw new IllegalArgumentException("Negative modulus");
      } else if (!this.modulus.equals(var1)) {
         this.modulus = var1;
         int var2 = 0;
         Iterator var3 = this.observedObjects.iterator();

         while(var3.hasNext()) {
            Monitor.ObservedObject var4 = (Monitor.ObservedObject)var3.next();
            this.resetAlreadyNotified(var4, var2++, 16);
            CounterMonitor.CounterMonitorObservedObject var5 = (CounterMonitor.CounterMonitorObservedObject)var4;
            var5.setModulusExceeded(false);
         }

      }
   }

   public synchronized boolean getNotify() {
      return this.notify;
   }

   public synchronized void setNotify(boolean var1) {
      if (this.notify != var1) {
         this.notify = var1;
      }
   }

   public synchronized boolean getDifferenceMode() {
      return this.differenceMode;
   }

   public synchronized void setDifferenceMode(boolean var1) {
      if (this.differenceMode != var1) {
         this.differenceMode = var1;
         Iterator var2 = this.observedObjects.iterator();

         while(var2.hasNext()) {
            Monitor.ObservedObject var3 = (Monitor.ObservedObject)var2.next();
            CounterMonitor.CounterMonitorObservedObject var4 = (CounterMonitor.CounterMonitorObservedObject)var3;
            var4.setThreshold(this.initThreshold);
            var4.setModulusExceeded(false);
            var4.setEventAlreadyNotified(false);
            var4.setPreviousScanCounter((Number)null);
         }

      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return (MBeanNotificationInfo[])notifsInfo.clone();
   }

   private synchronized boolean updateDerivedGauge(Object var1, CounterMonitor.CounterMonitorObservedObject var2) {
      boolean var3;
      if (this.differenceMode) {
         if (var2.getPreviousScanCounter() != null) {
            this.setDerivedGaugeWithDifference((Number)var1, (Number)null, var2);
            if (((Number)var2.getDerivedGauge()).longValue() < 0L) {
               if (this.modulus.longValue() > 0L) {
                  this.setDerivedGaugeWithDifference((Number)var1, this.modulus, var2);
               }

               var2.setThreshold(this.initThreshold);
               var2.setEventAlreadyNotified(false);
            }

            var3 = true;
         } else {
            var3 = false;
         }

         var2.setPreviousScanCounter((Number)var1);
      } else {
         var2.setDerivedGauge((Number)var1);
         var3 = true;
      }

      return var3;
   }

   private synchronized MonitorNotification updateNotifications(CounterMonitor.CounterMonitorObservedObject var1) {
      MonitorNotification var2 = null;
      if (!var1.getEventAlreadyNotified()) {
         if (((Number)var1.getDerivedGauge()).longValue() >= var1.getThreshold().longValue()) {
            if (this.notify) {
               var2 = new MonitorNotification("jmx.monitor.counter.threshold", this, 0L, 0L, "", (ObjectName)null, (String)null, (Object)null, var1.getThreshold());
            }

            if (!this.differenceMode) {
               var1.setEventAlreadyNotified(true);
            }
         }
      } else if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
         StringBuilder var3 = (new StringBuilder()).append("The notification:").append("\n\tNotification observed object = ").append((Object)var1.getObservedObject()).append("\n\tNotification observed attribute = ").append(this.getObservedAttribute()).append("\n\tNotification threshold level = ").append((Object)var1.getThreshold()).append("\n\tNotification derived gauge = ").append(var1.getDerivedGauge()).append("\nhas already been sent");
         JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "updateNotifications", var3.toString());
      }

      return var2;
   }

   private synchronized void updateThreshold(CounterMonitor.CounterMonitorObservedObject var1) {
      if (((Number)var1.getDerivedGauge()).longValue() >= var1.getThreshold().longValue()) {
         if (this.offset.longValue() > 0L) {
            long var2;
            for(var2 = var1.getThreshold().longValue(); ((Number)var1.getDerivedGauge()).longValue() >= var2; var2 += this.offset.longValue()) {
            }

            switch(var1.getType()) {
            case INTEGER:
               var1.setThreshold((int)var2);
               break;
            case BYTE:
               var1.setThreshold((byte)((int)var2));
               break;
            case SHORT:
               var1.setThreshold((short)((int)var2));
               break;
            case LONG:
               var1.setThreshold(var2);
               break;
            default:
               JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "updateThreshold", "the threshold type is invalid");
            }

            if (!this.differenceMode && this.modulus.longValue() > 0L && var1.getThreshold().longValue() > this.modulus.longValue()) {
               var1.setModulusExceeded(true);
               var1.setDerivedGaugeExceeded((Number)var1.getDerivedGauge());
            }

            var1.setEventAlreadyNotified(false);
         } else {
            var1.setModulusExceeded(true);
            var1.setDerivedGaugeExceeded((Number)var1.getDerivedGauge());
         }
      }

   }

   private synchronized void setDerivedGaugeWithDifference(Number var1, Number var2, CounterMonitor.CounterMonitorObservedObject var3) {
      long var4 = var1.longValue() - var3.getPreviousScanCounter().longValue();
      if (var2 != null) {
         var4 += this.modulus.longValue();
      }

      switch(var3.getType()) {
      case INTEGER:
         var3.setDerivedGauge((int)var4);
         break;
      case BYTE:
         var3.setDerivedGauge((byte)((int)var4));
         break;
      case SHORT:
         var3.setDerivedGauge((short)((int)var4));
         break;
      case LONG:
         var3.setDerivedGauge(var4);
         break;
      default:
         JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
      }

   }

   Monitor.ObservedObject createObservedObject(ObjectName var1) {
      CounterMonitor.CounterMonitorObservedObject var2 = new CounterMonitor.CounterMonitorObservedObject(var1);
      var2.setThreshold(this.initThreshold);
      var2.setModulusExceeded(false);
      var2.setEventAlreadyNotified(false);
      var2.setPreviousScanCounter((Number)null);
      return var2;
   }

   synchronized boolean isComparableTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      CounterMonitor.CounterMonitorObservedObject var4 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return false;
      } else {
         if (var3 instanceof Integer) {
            var4.setType(Monitor.NumericalType.INTEGER);
         } else if (var3 instanceof Byte) {
            var4.setType(Monitor.NumericalType.BYTE);
         } else if (var3 instanceof Short) {
            var4.setType(Monitor.NumericalType.SHORT);
         } else {
            if (!(var3 instanceof Long)) {
               return false;
            }

            var4.setType(Monitor.NumericalType.LONG);
         }

         return true;
      }
   }

   synchronized Comparable<?> getDerivedGaugeFromComparable(ObjectName var1, String var2, Comparable<?> var3) {
      CounterMonitor.CounterMonitorObservedObject var4 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return null;
      } else {
         if (var4.getModulusExceeded() && ((Number)var4.getDerivedGauge()).longValue() < var4.getDerivedGaugeExceeded().longValue()) {
            var4.setThreshold(this.initThreshold);
            var4.setModulusExceeded(false);
            var4.setEventAlreadyNotified(false);
         }

         var4.setDerivedGaugeValid(this.updateDerivedGauge(var3, var4));
         return (Comparable)var4.getDerivedGauge();
      }
   }

   synchronized void onErrorNotification(MonitorNotification var1) {
      CounterMonitor.CounterMonitorObservedObject var2 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1.getObservedObject());
      if (var2 != null) {
         var2.setModulusExceeded(false);
         var2.setEventAlreadyNotified(false);
         var2.setPreviousScanCounter((Number)null);
      }
   }

   synchronized MonitorNotification buildAlarmNotification(ObjectName var1, String var2, Comparable<?> var3) {
      CounterMonitor.CounterMonitorObservedObject var4 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return null;
      } else {
         MonitorNotification var5;
         if (var4.getDerivedGaugeValid()) {
            var5 = this.updateNotifications(var4);
            this.updateThreshold(var4);
         } else {
            var5 = null;
         }

         return var5;
      }
   }

   synchronized boolean isThresholdTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      CounterMonitor.CounterMonitorObservedObject var4 = (CounterMonitor.CounterMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return false;
      } else {
         Class var5 = classForType(var4.getType());
         return var5.isInstance(var4.getThreshold()) && isValidForType(this.offset, var5) && isValidForType(this.modulus, var5);
      }
   }

   static {
      notifsInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the CounterMonitor MBean")};
   }

   static class CounterMonitorObservedObject extends Monitor.ObservedObject {
      private Number threshold;
      private Number previousScanCounter;
      private boolean modulusExceeded;
      private Number derivedGaugeExceeded;
      private boolean derivedGaugeValid;
      private boolean eventAlreadyNotified;
      private Monitor.NumericalType type;

      public CounterMonitorObservedObject(ObjectName var1) {
         super(var1);
      }

      public final synchronized Number getThreshold() {
         return this.threshold;
      }

      public final synchronized void setThreshold(Number var1) {
         this.threshold = var1;
      }

      public final synchronized Number getPreviousScanCounter() {
         return this.previousScanCounter;
      }

      public final synchronized void setPreviousScanCounter(Number var1) {
         this.previousScanCounter = var1;
      }

      public final synchronized boolean getModulusExceeded() {
         return this.modulusExceeded;
      }

      public final synchronized void setModulusExceeded(boolean var1) {
         this.modulusExceeded = var1;
      }

      public final synchronized Number getDerivedGaugeExceeded() {
         return this.derivedGaugeExceeded;
      }

      public final synchronized void setDerivedGaugeExceeded(Number var1) {
         this.derivedGaugeExceeded = var1;
      }

      public final synchronized boolean getDerivedGaugeValid() {
         return this.derivedGaugeValid;
      }

      public final synchronized void setDerivedGaugeValid(boolean var1) {
         this.derivedGaugeValid = var1;
      }

      public final synchronized boolean getEventAlreadyNotified() {
         return this.eventAlreadyNotified;
      }

      public final synchronized void setEventAlreadyNotified(boolean var1) {
         this.eventAlreadyNotified = var1;
      }

      public final synchronized Monitor.NumericalType getType() {
         return this.type;
      }

      public final synchronized void setType(Monitor.NumericalType var1) {
         this.type = var1;
      }
   }
}
