package com.sun.management;

import com.sun.jmx.mbeanserver.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public final class MissionControl extends StandardMBean implements MissionControlMXBean {
   private static final ObjectName MBEAN_NAME = Util.newObjectName("com.sun.management:type=MissionControl");
   private MBeanServer server;

   public MissionControl() {
      super(MissionControlMXBean.class, true);
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.server = var1;
      return MBEAN_NAME;
   }

   public void unregisterMBeans() {
      this.doPrivileged(new PrivilegedExceptionAction<Void>() {
         public Void run() {
            MissionControl.FlightRecorderHelper.unregisterWithMBeanServer(MissionControl.this.server);
            return null;
         }
      });
   }

   public void registerMBeans() {
      this.doPrivileged(new PrivilegedExceptionAction<Void>() {
         public Void run() throws MalformedObjectNameException {
            if (!MissionControl.this.server.isRegistered(new ObjectName("com.oracle.jrockit:type=FlightRecorder"))) {
               try {
                  MissionControl.FlightRecorderHelper.registerWithMBeanServer(MissionControl.this.server);
               } catch (IllegalStateException var2) {
               }
            }

            return null;
         }
      });
   }

   private void doPrivileged(PrivilegedExceptionAction<Void> var1) {
      try {
         AccessController.doPrivileged(var1);
      } catch (PrivilegedActionException var3) {
      }

   }

   private static class FlightRecorderHelper {
      static final String MBEAN_NAME = "com.oracle.jrockit:type=FlightRecorder";
      private static final Class<?> FLIGHTRECORDER_CLASS = getClass("com.oracle.jrockit.jfr.FlightRecorder");
      private static final Method REGISTERWITHMBEANSERVER_METHOD;
      private static final Method UNREGISTERWITHMBEANSERVER_METHOD;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, MissionControl.FlightRecorderHelper.class.getClassLoader());
         } catch (ClassNotFoundException var2) {
            throw new InternalError("jfr.jar missing?", var2);
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         try {
            return var0.getMethod(var1, var2);
         } catch (NoSuchMethodException var4) {
            throw new InternalError(var4);
         }
      }

      private static Object invokeStatic(Method var0, Object... var1) {
         try {
            return var0.invoke((Object)null, var1);
         } catch (InvocationTargetException var4) {
            Throwable var3 = var4.getCause();
            if (var3 instanceof RuntimeException) {
               throw (RuntimeException)var3;
            } else if (var3 instanceof Error) {
               throw (Error)var3;
            } else {
               throw new InternalError(var3);
            }
         } catch (IllegalAccessException var5) {
            throw new InternalError(var5);
         }
      }

      static void registerWithMBeanServer(MBeanServer var0) {
         invokeStatic(REGISTERWITHMBEANSERVER_METHOD, var0);
      }

      static void unregisterWithMBeanServer(MBeanServer var0) {
         invokeStatic(UNREGISTERWITHMBEANSERVER_METHOD, var0);
      }

      static {
         REGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "registerWithMBeanServer", MBeanServer.class);
         UNREGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "unregisterWithMBeanServer", MBeanServer.class);
      }
   }
}
