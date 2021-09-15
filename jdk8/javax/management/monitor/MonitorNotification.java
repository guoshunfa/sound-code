package javax.management.monitor;

import javax.management.Notification;
import javax.management.ObjectName;

public class MonitorNotification extends Notification {
   public static final String OBSERVED_OBJECT_ERROR = "jmx.monitor.error.mbean";
   public static final String OBSERVED_ATTRIBUTE_ERROR = "jmx.monitor.error.attribute";
   public static final String OBSERVED_ATTRIBUTE_TYPE_ERROR = "jmx.monitor.error.type";
   public static final String THRESHOLD_ERROR = "jmx.monitor.error.threshold";
   public static final String RUNTIME_ERROR = "jmx.monitor.error.runtime";
   public static final String THRESHOLD_VALUE_EXCEEDED = "jmx.monitor.counter.threshold";
   public static final String THRESHOLD_HIGH_VALUE_EXCEEDED = "jmx.monitor.gauge.high";
   public static final String THRESHOLD_LOW_VALUE_EXCEEDED = "jmx.monitor.gauge.low";
   public static final String STRING_TO_COMPARE_VALUE_MATCHED = "jmx.monitor.string.matches";
   public static final String STRING_TO_COMPARE_VALUE_DIFFERED = "jmx.monitor.string.differs";
   private static final long serialVersionUID = -4608189663661929204L;
   private ObjectName observedObject = null;
   private String observedAttribute = null;
   private Object derivedGauge = null;
   private Object trigger = null;

   MonitorNotification(String var1, Object var2, long var3, long var5, String var7, ObjectName var8, String var9, Object var10, Object var11) {
      super(var1, var2, var3, var5, var7);
      this.observedObject = var8;
      this.observedAttribute = var9;
      this.derivedGauge = var10;
      this.trigger = var11;
   }

   public ObjectName getObservedObject() {
      return this.observedObject;
   }

   public String getObservedAttribute() {
      return this.observedAttribute;
   }

   public Object getDerivedGauge() {
      return this.derivedGauge;
   }

   public Object getTrigger() {
      return this.trigger;
   }
}
