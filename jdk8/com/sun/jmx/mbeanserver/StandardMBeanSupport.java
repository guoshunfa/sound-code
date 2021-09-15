package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class StandardMBeanSupport extends MBeanSupport<Method> {
   public <T> StandardMBeanSupport(T var1, Class<T> var2) throws NotCompliantMBeanException {
      super(var1, var2);
   }

   MBeanIntrospector<Method> getMBeanIntrospector() {
      return StandardMBeanIntrospector.getInstance();
   }

   Object getCookie() {
      return null;
   }

   public void register(MBeanServer var1, ObjectName var2) {
   }

   public void unregister() {
   }

   public MBeanInfo getMBeanInfo() {
      MBeanInfo var1 = super.getMBeanInfo();
      Class var2 = this.getResource().getClass();
      return StandardMBeanIntrospector.isDefinitelyImmutableInfo(var2) ? var1 : new MBeanInfo(var1.getClassName(), var1.getDescription(), var1.getAttributes(), var1.getConstructors(), var1.getOperations(), MBeanIntrospector.findNotifications(this.getResource()), var1.getDescriptor());
   }
}
