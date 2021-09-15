package com.sun.org.glassfish.gmbal;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public interface ManagedObjectManager extends Closeable {
   void suspendJMXRegistration();

   void resumeJMXRegistration();

   boolean isManagedObject(Object var1);

   GmbalMBean createRoot();

   GmbalMBean createRoot(Object var1);

   GmbalMBean createRoot(Object var1, String var2);

   Object getRoot();

   GmbalMBean register(Object var1, Object var2, String var3);

   GmbalMBean register(Object var1, Object var2);

   GmbalMBean registerAtRoot(Object var1, String var2);

   GmbalMBean registerAtRoot(Object var1);

   void unregister(Object var1);

   ObjectName getObjectName(Object var1);

   AMXClient getAMXClient(Object var1);

   Object getObject(ObjectName var1);

   void stripPrefix(String... var1);

   void stripPackagePrefix();

   String getDomain();

   void setMBeanServer(MBeanServer var1);

   MBeanServer getMBeanServer();

   void setResourceBundle(ResourceBundle var1);

   ResourceBundle getResourceBundle();

   void addAnnotation(AnnotatedElement var1, Annotation var2);

   void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel var1);

   void setRuntimeDebug(boolean var1);

   void setTypelibDebug(int var1);

   void setJMXRegistrationDebug(boolean var1);

   String dumpSkeleton(Object var1);

   void suppressDuplicateRootReport(boolean var1);

   public static enum RegistrationDebugLevel {
      NONE,
      NORMAL,
      FINE;
   }
}
