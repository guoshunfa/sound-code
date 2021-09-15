package com.sun.org.glassfish.gmbal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class ManagedObjectManagerNOPImpl implements ManagedObjectManager {
   static final ManagedObjectManager self = new ManagedObjectManagerNOPImpl();
   private static final GmbalMBean gmb = new GmbalMBeanNOPImpl();

   private ManagedObjectManagerNOPImpl() {
   }

   public void suspendJMXRegistration() {
   }

   public void resumeJMXRegistration() {
   }

   public boolean isManagedObject(Object obj) {
      return false;
   }

   public GmbalMBean createRoot() {
      return gmb;
   }

   public GmbalMBean createRoot(Object root) {
      return gmb;
   }

   public GmbalMBean createRoot(Object root, String name) {
      return gmb;
   }

   public Object getRoot() {
      return null;
   }

   public GmbalMBean register(Object parent, Object obj, String name) {
      return gmb;
   }

   public GmbalMBean register(Object parent, Object obj) {
      return gmb;
   }

   public GmbalMBean registerAtRoot(Object obj, String name) {
      return gmb;
   }

   public GmbalMBean registerAtRoot(Object obj) {
      return gmb;
   }

   public void unregister(Object obj) {
   }

   public ObjectName getObjectName(Object obj) {
      return null;
   }

   public Object getObject(ObjectName oname) {
      return null;
   }

   public void stripPrefix(String... str) {
   }

   public String getDomain() {
      return null;
   }

   public void setMBeanServer(MBeanServer server) {
   }

   public MBeanServer getMBeanServer() {
      return null;
   }

   public void setResourceBundle(ResourceBundle rb) {
   }

   public ResourceBundle getResourceBundle() {
      return null;
   }

   public void addAnnotation(AnnotatedElement element, Annotation annotation) {
   }

   public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel level) {
   }

   public void setRuntimeDebug(boolean flag) {
   }

   public String dumpSkeleton(Object obj) {
      return "";
   }

   public void close() throws IOException {
   }

   public void setTypelibDebug(int level) {
   }

   public void stripPackagePrefix() {
   }

   public void suppressDuplicateRootReport(boolean suppressReport) {
   }

   public AMXClient getAMXClient(Object obj) {
      return null;
   }

   public void setJMXRegistrationDebug(boolean flag) {
   }
}
