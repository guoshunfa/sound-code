package javax.management.monitor;

import javax.management.ObjectName;

public interface CounterMonitorMBean extends MonitorMBean {
   /** @deprecated */
   @Deprecated
   Number getDerivedGauge();

   /** @deprecated */
   @Deprecated
   long getDerivedGaugeTimeStamp();

   /** @deprecated */
   @Deprecated
   Number getThreshold();

   /** @deprecated */
   @Deprecated
   void setThreshold(Number var1) throws IllegalArgumentException;

   Number getDerivedGauge(ObjectName var1);

   long getDerivedGaugeTimeStamp(ObjectName var1);

   Number getThreshold(ObjectName var1);

   Number getInitThreshold();

   void setInitThreshold(Number var1) throws IllegalArgumentException;

   Number getOffset();

   void setOffset(Number var1) throws IllegalArgumentException;

   Number getModulus();

   void setModulus(Number var1) throws IllegalArgumentException;

   boolean getNotify();

   void setNotify(boolean var1);

   boolean getDifferenceMode();

   void setDifferenceMode(boolean var1);
}
