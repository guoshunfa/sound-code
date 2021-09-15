package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;

public class MXBeanLookup {
   private static final ThreadLocal<MXBeanLookup> currentLookup = new ThreadLocal();
   private final MBeanServerConnection mbsc;
   private final WeakIdentityHashMap<Object, ObjectName> mxbeanToObjectName = WeakIdentityHashMap.make();
   private final Map<ObjectName, WeakReference<Object>> objectNameToProxy = Util.newMap();
   private static final WeakIdentityHashMap<MBeanServerConnection, WeakReference<MXBeanLookup>> mbscToLookup = WeakIdentityHashMap.make();

   private MXBeanLookup(MBeanServerConnection var1) {
      this.mbsc = var1;
   }

   static MXBeanLookup lookupFor(MBeanServerConnection var0) {
      synchronized(mbscToLookup) {
         WeakReference var2 = (WeakReference)mbscToLookup.get(var0);
         MXBeanLookup var3 = var2 == null ? null : (MXBeanLookup)var2.get();
         if (var3 == null) {
            var3 = new MXBeanLookup(var0);
            mbscToLookup.put(var0, new WeakReference(var3));
         }

         return var3;
      }
   }

   synchronized <T> T objectNameToMXBean(ObjectName var1, Class<T> var2) {
      WeakReference var3 = (WeakReference)this.objectNameToProxy.get(var1);
      Object var4;
      if (var3 != null) {
         var4 = var3.get();
         if (var2.isInstance(var4)) {
            return var2.cast(var4);
         }
      }

      var4 = JMX.newMXBeanProxy(this.mbsc, var1, var2);
      this.objectNameToProxy.put(var1, new WeakReference(var4));
      return var4;
   }

   synchronized ObjectName mxbeanToObjectName(Object var1) throws OpenDataException {
      String var2;
      if (var1 instanceof Proxy) {
         InvocationHandler var3 = Proxy.getInvocationHandler(var1);
         if (var3 instanceof MBeanServerInvocationHandler) {
            MBeanServerInvocationHandler var4 = (MBeanServerInvocationHandler)var3;
            if (var4.getMBeanServerConnection().equals(this.mbsc)) {
               return var4.getObjectName();
            }

            var2 = "proxy for a different MBeanServer";
         } else {
            var2 = "not a JMX proxy";
         }
      } else {
         ObjectName var5 = (ObjectName)this.mxbeanToObjectName.get(var1);
         if (var5 != null) {
            return var5;
         }

         var2 = "not an MXBean registered in this MBeanServer";
      }

      String var6 = var1 == null ? "null" : "object of type " + var1.getClass().getName();
      throw new OpenDataException("Could not convert " + var6 + " to an ObjectName: " + var2);
   }

   synchronized void addReference(ObjectName var1, Object var2) throws InstanceAlreadyExistsException {
      ObjectName var3 = (ObjectName)this.mxbeanToObjectName.get(var2);
      if (var3 != null) {
         String var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jmx.mxbean.multiname")));
         if (!"true".equalsIgnoreCase(var4)) {
            throw new InstanceAlreadyExistsException("MXBean already registered with name " + var3);
         }
      }

      this.mxbeanToObjectName.put(var2, var1);
   }

   synchronized boolean removeReference(ObjectName var1, Object var2) {
      if (var1.equals(this.mxbeanToObjectName.get(var2))) {
         this.mxbeanToObjectName.remove(var2);
         return true;
      } else {
         return false;
      }
   }

   static MXBeanLookup getLookup() {
      return (MXBeanLookup)currentLookup.get();
   }

   static void setLookup(MXBeanLookup var0) {
      currentLookup.set(var0);
   }
}
