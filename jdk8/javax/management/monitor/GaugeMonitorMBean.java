package javax.management.monitor;

import javax.management.ObjectName;

public interface GaugeMonitorMBean extends MonitorMBean {
   /** @deprecated */
   @Deprecated
   Number getDerivedGauge();

   /** @deprecated */
   @Deprecated
   long getDerivedGaugeTimeStamp();

   Number getDerivedGauge(ObjectName var1);

   long getDerivedGaugeTimeStamp(ObjectName var1);

   Number getHighThreshold();

   Number getLowThreshold();

   void setThresholds(Number var1, Number var2) throws IllegalArgumentException;

   boolean getNotifyHigh();

   void setNotifyHigh(boolean var1);

   boolean getNotifyLow();

   void setNotifyLow(boolean var1);

   boolean getDifferenceMode();

   void setDifferenceMode(boolean var1);
}
