package com.sun.management.jmx;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;

/** @deprecated */
@Deprecated
public class Introspector {
   /** @deprecated */
   @Deprecated
   public static synchronized MBeanInfo testCompliance(Class var0) throws NotCompliantMBeanException {
      return com.sun.jmx.mbeanserver.Introspector.testCompliance(var0);
   }

   /** @deprecated */
   @Deprecated
   public static synchronized Class getMBeanInterface(Class var0) {
      return com.sun.jmx.mbeanserver.Introspector.getMBeanInterface(var0);
   }
}
