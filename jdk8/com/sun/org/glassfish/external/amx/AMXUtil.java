package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;

@Taxonomy(
   stability = Stability.UNCOMMITTED
)
public final class AMXUtil {
   private AMXUtil() {
   }

   public static ObjectName newObjectName(String s) {
      try {
         return new ObjectName(s);
      } catch (Exception var2) {
         throw new RuntimeException("bad ObjectName", var2);
      }
   }

   public static ObjectName newObjectName(String domain, String props) {
      return newObjectName(domain + ":" + props);
   }

   public static ObjectName getMBeanServerDelegateObjectName() {
      return newObjectName("JMImplementation:type=MBeanServerDelegate");
   }

   public static String prop(String key, String value) {
      return key + "=" + value;
   }
}
