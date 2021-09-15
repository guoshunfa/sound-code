package javax.management;

public class MBeanServerNotification extends Notification {
   private static final long serialVersionUID = 2876477500475969677L;
   public static final String REGISTRATION_NOTIFICATION = "JMX.mbean.registered";
   public static final String UNREGISTRATION_NOTIFICATION = "JMX.mbean.unregistered";
   private final ObjectName objectName;

   public MBeanServerNotification(String var1, Object var2, long var3, ObjectName var5) {
      super(var1, var2, var3);
      this.objectName = var5;
   }

   public ObjectName getMBeanName() {
      return this.objectName;
   }

   public String toString() {
      return super.toString() + "[mbeanName=" + this.objectName + "]";
   }
}
