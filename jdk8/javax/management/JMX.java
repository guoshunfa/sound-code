package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class JMX {
   static final JMX proof = new JMX();
   public static final String DEFAULT_VALUE_FIELD = "defaultValue";
   public static final String IMMUTABLE_INFO_FIELD = "immutableInfo";
   public static final String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";
   public static final String LEGAL_VALUES_FIELD = "legalValues";
   public static final String MAX_VALUE_FIELD = "maxValue";
   public static final String MIN_VALUE_FIELD = "minValue";
   public static final String MXBEAN_FIELD = "mxbean";
   public static final String OPEN_TYPE_FIELD = "openType";
   public static final String ORIGINAL_TYPE_FIELD = "originalType";

   private JMX() {
   }

   public static <T> T newMBeanProxy(MBeanServerConnection var0, ObjectName var1, Class<T> var2) {
      return newMBeanProxy(var0, var1, var2, false);
   }

   public static <T> T newMBeanProxy(MBeanServerConnection var0, ObjectName var1, Class<T> var2, boolean var3) {
      return createProxy(var0, var1, var2, var3, false);
   }

   public static <T> T newMXBeanProxy(MBeanServerConnection var0, ObjectName var1, Class<T> var2) {
      return newMXBeanProxy(var0, var1, var2, false);
   }

   public static <T> T newMXBeanProxy(MBeanServerConnection var0, ObjectName var1, Class<T> var2, boolean var3) {
      return createProxy(var0, var1, var2, var3, true);
   }

   public static boolean isMXBeanInterface(Class<?> var0) {
      if (!var0.isInterface()) {
         return false;
      } else if (!Modifier.isPublic(var0.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN) {
         return false;
      } else {
         MXBean var1 = (MXBean)var0.getAnnotation(MXBean.class);
         return var1 != null ? var1.value() : var0.getName().endsWith("MXBean");
      }
   }

   private static <T> T createProxy(MBeanServerConnection var0, ObjectName var1, Class<T> var2, boolean var3, boolean var4) {
      try {
         if (var4) {
            Introspector.testComplianceMXBeanInterface(var2);
         } else {
            Introspector.testComplianceMBeanInterface(var2);
         }
      } catch (NotCompliantMBeanException var8) {
         throw new IllegalArgumentException(var8);
      }

      MBeanServerInvocationHandler var5 = new MBeanServerInvocationHandler(var0, var1, var4);
      Class[] var6;
      if (var3) {
         var6 = new Class[]{var2, NotificationEmitter.class};
      } else {
         var6 = new Class[]{var2};
      }

      Object var7 = Proxy.newProxyInstance(var2.getClassLoader(), var6, var5);
      return var2.cast(var7);
   }
}
