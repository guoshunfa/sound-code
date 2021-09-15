package com.sun.jmx.mbeanserver;

import java.util.Iterator;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanSupport extends MBeanSupport<ConvertingMethod> {
   private final Object lock = new Object();
   private MXBeanLookup mxbeanLookup;
   private ObjectName objectName;

   public <T> MXBeanSupport(T var1, Class<T> var2) throws NotCompliantMBeanException {
      super(var1, var2);
   }

   MBeanIntrospector<ConvertingMethod> getMBeanIntrospector() {
      return MXBeanIntrospector.getInstance();
   }

   Object getCookie() {
      return this.mxbeanLookup;
   }

   static <T> Class<? super T> findMXBeanInterface(Class<T> var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null resource class");
      } else {
         Set var1 = transitiveInterfaces(var0);
         Set var2 = Util.newSet();
         Iterator var3 = var1.iterator();

         Class var4;
         while(var3.hasNext()) {
            var4 = (Class)var3.next();
            if (JMX.isMXBeanInterface(var4)) {
               var2.add(var4);
            }
         }

         String var7;
         label43:
         while(var2.size() > 1) {
            var3 = var2.iterator();

            while(var3.hasNext()) {
               var4 = (Class)var3.next();
               Iterator var5 = var2.iterator();

               while(var5.hasNext()) {
                  Class var6 = (Class)var5.next();
                  if (var4 != var6 && var6.isAssignableFrom(var4)) {
                     var5.remove();
                     continue label43;
                  }
               }
            }

            var7 = "Class " + var0.getName() + " implements more than one MXBean interface: " + var2;
            throw new IllegalArgumentException(var7);
         }

         if (var2.iterator().hasNext()) {
            return (Class)Util.cast(var2.iterator().next());
         } else {
            var7 = "Class " + var0.getName() + " is not a JMX compliant MXBean";
            throw new IllegalArgumentException(var7);
         }
      }
   }

   private static Set<Class<?>> transitiveInterfaces(Class<?> var0) {
      Set var1 = Util.newSet();
      transitiveInterfaces(var0, var1);
      return var1;
   }

   private static void transitiveInterfaces(Class<?> var0, Set<Class<?>> var1) {
      if (var0 != null) {
         if (var0.isInterface()) {
            var1.add(var0);
         }

         transitiveInterfaces(var0.getSuperclass(), var1);
         Class[] var2 = var0.getInterfaces();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class var5 = var2[var4];
            transitiveInterfaces(var5, var1);
         }

      }
   }

   public void register(MBeanServer var1, ObjectName var2) throws InstanceAlreadyExistsException {
      if (var2 == null) {
         throw new IllegalArgumentException("Null object name");
      } else {
         synchronized(this.lock) {
            this.mxbeanLookup = MXBeanLookup.lookupFor(var1);
            this.mxbeanLookup.addReference(var2, this.getResource());
            this.objectName = var2;
         }
      }
   }

   public void unregister() {
      synchronized(this.lock) {
         if (this.mxbeanLookup != null && this.mxbeanLookup.removeReference(this.objectName, this.getResource())) {
            this.objectName = null;
         }

      }
   }
}
