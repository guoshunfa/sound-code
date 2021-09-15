package javax.management.monitor;

import javax.management.ObjectName;

public interface MonitorMBean {
   void start();

   void stop();

   void addObservedObject(ObjectName var1) throws IllegalArgumentException;

   void removeObservedObject(ObjectName var1);

   boolean containsObservedObject(ObjectName var1);

   ObjectName[] getObservedObjects();

   /** @deprecated */
   @Deprecated
   ObjectName getObservedObject();

   /** @deprecated */
   @Deprecated
   void setObservedObject(ObjectName var1);

   String getObservedAttribute();

   void setObservedAttribute(String var1);

   long getGranularityPeriod();

   void setGranularityPeriod(long var1) throws IllegalArgumentException;

   boolean isActive();
}
