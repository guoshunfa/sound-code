package javax.management.monitor;

import javax.management.ObjectName;

public interface StringMonitorMBean extends MonitorMBean {
   /** @deprecated */
   @Deprecated
   String getDerivedGauge();

   /** @deprecated */
   @Deprecated
   long getDerivedGaugeTimeStamp();

   String getDerivedGauge(ObjectName var1);

   long getDerivedGaugeTimeStamp(ObjectName var1);

   String getStringToCompare();

   void setStringToCompare(String var1) throws IllegalArgumentException;

   boolean getNotifyMatch();

   void setNotifyMatch(boolean var1);

   boolean getNotifyDiffer();

   void setNotifyDiffer(boolean var1);
}
