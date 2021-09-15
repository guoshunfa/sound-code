package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class GaugeMonitor extends Monitor implements GaugeMonitorMBean {
   private Number highThreshold;
   private Number lowThreshold;
   private boolean notifyHigh;
   private boolean notifyLow;
   private boolean differenceMode;
   private static final String[] types = new String[]{"jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.gauge.high", "jmx.monitor.gauge.low"};
   private static final MBeanNotificationInfo[] notifsInfo;
   private static final int RISING = 0;
   private static final int FALLING = 1;
   private static final int RISING_OR_FALLING = 2;

   public GaugeMonitor() {
      this.highThreshold = INTEGER_ZERO;
      this.lowThreshold = INTEGER_ZERO;
      this.notifyHigh = false;
      this.notifyLow = false;
      this.differenceMode = false;
   }

   public synchronized void start() {
      if (this.isActive()) {
         JmxProperties.MONITOR_LOGGER.logp(Level.FINER, GaugeMonitor.class.getName(), "start", "the monitor is already active");
      } else {
         Iterator var1 = this.observedObjects.iterator();

         while(var1.hasNext()) {
            Monitor.ObservedObject var2 = (Monitor.ObservedObject)var1.next();
            GaugeMonitor.GaugeMonitorObservedObject var3 = (GaugeMonitor.GaugeMonitorObservedObject)var2;
            var3.setStatus(2);
            var3.setPreviousScanGauge((Number)null);
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

   public synchronized Number getHighThreshold() {
      return this.highThreshold;
   }

   public synchronized Number getLowThreshold() {
      return this.lowThreshold;
   }

   public synchronized void setThresholds(Number var1, Number var2) throws IllegalArgumentException {
      if (var1 != null && var2 != null) {
         if (var1.getClass() != var2.getClass()) {
            throw new IllegalArgumentException("Different type threshold values");
         } else if (this.isFirstStrictlyGreaterThanLast(var2, var1, var1.getClass().getName())) {
            throw new IllegalArgumentException("High threshold less than low threshold");
         } else if (!this.highThreshold.equals(var1) || !this.lowThreshold.equals(var2)) {
            this.highThreshold = var1;
            this.lowThreshold = var2;
            int var3 = 0;
            Iterator var4 = this.observedObjects.iterator();

            while(var4.hasNext()) {
               Monitor.ObservedObject var5 = (Monitor.ObservedObject)var4.next();
               this.resetAlreadyNotified(var5, var3++, 16);
               GaugeMonitor.GaugeMonitorObservedObject var6 = (GaugeMonitor.GaugeMonitorObservedObject)var5;
               var6.setStatus(2);
            }

         }
      } else {
         throw new IllegalArgumentException("Null threshold value");
      }
   }

   public synchronized boolean getNotifyHigh() {
      return this.notifyHigh;
   }

   public synchronized void setNotifyHigh(boolean var1) {
      if (this.notifyHigh != var1) {
         this.notifyHigh = var1;
      }
   }

   public synchronized boolean getNotifyLow() {
      return this.notifyLow;
   }

   public synchronized void setNotifyLow(boolean var1) {
      if (this.notifyLow != var1) {
         this.notifyLow = var1;
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
            GaugeMonitor.GaugeMonitorObservedObject var4 = (GaugeMonitor.GaugeMonitorObservedObject)var3;
            var4.setStatus(2);
            var4.setPreviousScanGauge((Number)null);
         }

      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return (MBeanNotificationInfo[])notifsInfo.clone();
   }

   private synchronized boolean updateDerivedGauge(Object var1, GaugeMonitor.GaugeMonitorObservedObject var2) {
      boolean var3;
      if (this.differenceMode) {
         if (var2.getPreviousScanGauge() != null) {
            this.setDerivedGaugeWithDifference((Number)var1, var2);
            var3 = true;
         } else {
            var3 = false;
         }

         var2.setPreviousScanGauge((Number)var1);
      } else {
         var2.setDerivedGauge((Number)var1);
         var3 = true;
      }

      return var3;
   }

   private synchronized MonitorNotification updateNotifications(GaugeMonitor.GaugeMonitorObservedObject var1) {
      MonitorNotification var2 = null;
      if (var1.getStatus() == 2) {
         if (this.isFirstGreaterThanLast((Number)var1.getDerivedGauge(), this.highThreshold, var1.getType())) {
            if (this.notifyHigh) {
               var2 = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", (ObjectName)null, (String)null, (Object)null, this.highThreshold);
            }

            var1.setStatus(1);
         } else if (this.isFirstGreaterThanLast(this.lowThreshold, (Number)var1.getDerivedGauge(), var1.getType())) {
            if (this.notifyLow) {
               var2 = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", (ObjectName)null, (String)null, (Object)null, this.lowThreshold);
            }

            var1.setStatus(0);
         }
      } else if (var1.getStatus() == 0) {
         if (this.isFirstGreaterThanLast((Number)var1.getDerivedGauge(), this.highThreshold, var1.getType())) {
            if (this.notifyHigh) {
               var2 = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", (ObjectName)null, (String)null, (Object)null, this.highThreshold);
            }

            var1.setStatus(1);
         }
      } else if (var1.getStatus() == 1 && this.isFirstGreaterThanLast(this.lowThreshold, (Number)var1.getDerivedGauge(), var1.getType())) {
         if (this.notifyLow) {
            var2 = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", (ObjectName)null, (String)null, (Object)null, this.lowThreshold);
         }

         var1.setStatus(0);
      }

      return var2;
   }

   private synchronized void setDerivedGaugeWithDifference(Number var1, GaugeMonitor.GaugeMonitorObservedObject var2) {
      Number var3 = var2.getPreviousScanGauge();
      Object var4;
      switch(var2.getType()) {
      case INTEGER:
         var4 = (Integer)var1 - (Integer)var3;
         break;
      case BYTE:
         var4 = (byte)((Byte)var1 - (Byte)var3);
         break;
      case SHORT:
         var4 = (short)((Short)var1 - (Short)var3);
         break;
      case LONG:
         var4 = (Long)var1 - (Long)var3;
         break;
      case FLOAT:
         var4 = (Float)var1 - (Float)var3;
         break;
      case DOUBLE:
         var4 = (Double)var1 - (Double)var3;
         break;
      default:
         JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
         return;
      }

      var2.setDerivedGauge(var4);
   }

   private boolean isFirstGreaterThanLast(Number var1, Number var2, Monitor.NumericalType var3) {
      switch(var3) {
      case INTEGER:
      case BYTE:
      case SHORT:
      case LONG:
         return var1.longValue() >= var2.longValue();
      case FLOAT:
      case DOUBLE:
         return var1.doubleValue() >= var2.doubleValue();
      default:
         JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstGreaterThanLast", "the threshold type is invalid");
         return false;
      }
   }

   private boolean isFirstStrictlyGreaterThanLast(Number var1, Number var2, String var3) {
      if (!var3.equals("java.lang.Integer") && !var3.equals("java.lang.Byte") && !var3.equals("java.lang.Short") && !var3.equals("java.lang.Long")) {
         if (!var3.equals("java.lang.Float") && !var3.equals("java.lang.Double")) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstStrictlyGreaterThanLast", "the threshold type is invalid");
            return false;
         } else {
            return var1.doubleValue() > var2.doubleValue();
         }
      } else {
         return var1.longValue() > var2.longValue();
      }
   }

   Monitor.ObservedObject createObservedObject(ObjectName var1) {
      GaugeMonitor.GaugeMonitorObservedObject var2 = new GaugeMonitor.GaugeMonitorObservedObject(var1);
      var2.setStatus(2);
      var2.setPreviousScanGauge((Number)null);
      return var2;
   }

   synchronized boolean isComparableTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      GaugeMonitor.GaugeMonitorObservedObject var4 = (GaugeMonitor.GaugeMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return false;
      } else {
         if (var3 instanceof Integer) {
            var4.setType(Monitor.NumericalType.INTEGER);
         } else if (var3 instanceof Byte) {
            var4.setType(Monitor.NumericalType.BYTE);
         } else if (var3 instanceof Short) {
            var4.setType(Monitor.NumericalType.SHORT);
         } else if (var3 instanceof Long) {
            var4.setType(Monitor.NumericalType.LONG);
         } else if (var3 instanceof Float) {
            var4.setType(Monitor.NumericalType.FLOAT);
         } else {
            if (!(var3 instanceof Double)) {
               return false;
            }

            var4.setType(Monitor.NumericalType.DOUBLE);
         }

         return true;
      }
   }

   synchronized Comparable<?> getDerivedGaugeFromComparable(ObjectName var1, String var2, Comparable<?> var3) {
      GaugeMonitor.GaugeMonitorObservedObject var4 = (GaugeMonitor.GaugeMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return null;
      } else {
         var4.setDerivedGaugeValid(this.updateDerivedGauge(var3, var4));
         return (Comparable)var4.getDerivedGauge();
      }
   }

   synchronized void onErrorNotification(MonitorNotification var1) {
      GaugeMonitor.GaugeMonitorObservedObject var2 = (GaugeMonitor.GaugeMonitorObservedObject)this.getObservedObject(var1.getObservedObject());
      if (var2 != null) {
         var2.setStatus(2);
         var2.setPreviousScanGauge((Number)null);
      }
   }

   synchronized MonitorNotification buildAlarmNotification(ObjectName var1, String var2, Comparable<?> var3) {
      GaugeMonitor.GaugeMonitorObservedObject var4 = (GaugeMonitor.GaugeMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return null;
      } else {
         MonitorNotification var5;
         if (var4.getDerivedGaugeValid()) {
            var5 = this.updateNotifications(var4);
         } else {
            var5 = null;
         }

         return var5;
      }
   }

   synchronized boolean isThresholdTypeValid(ObjectName var1, String var2, Comparable<?> var3) {
      GaugeMonitor.GaugeMonitorObservedObject var4 = (GaugeMonitor.GaugeMonitorObservedObject)this.getObservedObject(var1);
      if (var4 == null) {
         return false;
      } else {
         Class var5 = classForType(var4.getType());
         return isValidForType(this.highThreshold, var5) && isValidForType(this.lowThreshold, var5);
      }
   }

   static {
      notifsInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the GaugeMonitor MBean")};
   }

   static class GaugeMonitorObservedObject extends Monitor.ObservedObject {
      private boolean derivedGaugeValid;
      private Monitor.NumericalType type;
      private Number previousScanGauge;
      private int status;

      public GaugeMonitorObservedObject(ObjectName var1) {
         super(var1);
      }

      public final synchronized boolean getDerivedGaugeValid() {
         return this.derivedGaugeValid;
      }

      public final synchronized void setDerivedGaugeValid(boolean var1) {
         this.derivedGaugeValid = var1;
      }

      public final synchronized Monitor.NumericalType getType() {
         return this.type;
      }

      public final synchronized void setType(Monitor.NumericalType var1) {
         this.type = var1;
      }

      public final synchronized Number getPreviousScanGauge() {
         return this.previousScanGauge;
      }

      public final synchronized void setPreviousScanGauge(Number var1) {
         this.previousScanGauge = var1;
      }

      public final synchronized int getStatus() {
         return this.status;
      }

      public final synchronized void setStatus(int var1) {
         this.status = var1;
      }
   }
}
