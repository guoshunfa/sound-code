package com.sun.xml.internal.ws.server;

import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class RewritingMOM implements ManagedObjectManager {
   private final ManagedObjectManager mom;
   private static final String gmbalQuotingCharsRegex = "\n|\\|\"|\\*|\\?|:|=|,";
   private static final String replacementChar = "-";

   RewritingMOM(ManagedObjectManager mom) {
      this.mom = mom;
   }

   private String rewrite(String x) {
      return x.replaceAll("\n|\\|\"|\\*|\\?|:|=|,", "-");
   }

   public void suspendJMXRegistration() {
      this.mom.suspendJMXRegistration();
   }

   public void resumeJMXRegistration() {
      this.mom.resumeJMXRegistration();
   }

   public GmbalMBean createRoot() {
      return this.mom.createRoot();
   }

   public GmbalMBean createRoot(Object root) {
      return this.mom.createRoot(root);
   }

   public GmbalMBean createRoot(Object root, String name) {
      return this.mom.createRoot(root, this.rewrite(name));
   }

   public Object getRoot() {
      return this.mom.getRoot();
   }

   public GmbalMBean register(Object parent, Object obj, String name) {
      return this.mom.register(parent, obj, this.rewrite(name));
   }

   public GmbalMBean register(Object parent, Object obj) {
      return this.mom.register(parent, obj);
   }

   public GmbalMBean registerAtRoot(Object obj, String name) {
      return this.mom.registerAtRoot(obj, this.rewrite(name));
   }

   public GmbalMBean registerAtRoot(Object obj) {
      return this.mom.registerAtRoot(obj);
   }

   public void unregister(Object obj) {
      this.mom.unregister(obj);
   }

   public ObjectName getObjectName(Object obj) {
      return this.mom.getObjectName(obj);
   }

   public AMXClient getAMXClient(Object obj) {
      return this.mom.getAMXClient(obj);
   }

   public Object getObject(ObjectName oname) {
      return this.mom.getObject(oname);
   }

   public void stripPrefix(String... str) {
      this.mom.stripPrefix(str);
   }

   public void stripPackagePrefix() {
      this.mom.stripPackagePrefix();
   }

   public String getDomain() {
      return this.mom.getDomain();
   }

   public void setMBeanServer(MBeanServer server) {
      this.mom.setMBeanServer(server);
   }

   public MBeanServer getMBeanServer() {
      return this.mom.getMBeanServer();
   }

   public void setResourceBundle(ResourceBundle rb) {
      this.mom.setResourceBundle(rb);
   }

   public ResourceBundle getResourceBundle() {
      return this.mom.getResourceBundle();
   }

   public void addAnnotation(AnnotatedElement element, Annotation annotation) {
      this.mom.addAnnotation(element, annotation);
   }

   public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel level) {
      this.mom.setRegistrationDebug(level);
   }

   public void setRuntimeDebug(boolean flag) {
      this.mom.setRuntimeDebug(flag);
   }

   public void setTypelibDebug(int level) {
      this.mom.setTypelibDebug(level);
   }

   public String dumpSkeleton(Object obj) {
      return this.mom.dumpSkeleton(obj);
   }

   public void suppressDuplicateRootReport(boolean suppressReport) {
      this.mom.suppressDuplicateRootReport(suppressReport);
   }

   public void close() throws IOException {
      this.mom.close();
   }

   public void setJMXRegistrationDebug(boolean x) {
      this.mom.setJMXRegistrationDebug(x);
   }

   public boolean isManagedObject(Object x) {
      return this.mom.isManagedObject(x);
   }
}
