package com.sun.org.glassfish.external.amx;

import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public final class AMXGlassfish {
   public static final String DEFAULT_JMX_DOMAIN = "amx";
   public static final AMXGlassfish DEFAULT = new AMXGlassfish("amx");
   private final String mJMXDomain;
   private final ObjectName mDomainRoot;

   public AMXGlassfish(String jmxDomain) {
      this.mJMXDomain = jmxDomain;
      this.mDomainRoot = this.newObjectName("", "domain-root", (String)null);
   }

   public static String getGlassfishVersion() {
      String version = System.getProperty("glassfish.version");
      return version;
   }

   public String amxJMXDomain() {
      return this.mJMXDomain;
   }

   public String amxSupportDomain() {
      return this.amxJMXDomain() + "-support";
   }

   public String dasName() {
      return "server";
   }

   public String dasConfig() {
      return this.dasName() + "-config";
   }

   public ObjectName domainRoot() {
      return this.mDomainRoot;
   }

   public ObjectName monitoringRoot() {
      return this.newObjectName("/", "mon", (String)null);
   }

   public ObjectName serverMon(String serverName) {
      return this.newObjectName("/mon", "server-mon", serverName);
   }

   public ObjectName serverMonForDAS() {
      return this.serverMon("server");
   }

   public ObjectName newObjectName(String pp, String type, String name) {
      String props = prop("pp", pp) + "," + prop("type", type);
      if (name != null) {
         props = props + "," + prop("name", name);
      }

      return this.newObjectName(props);
   }

   public ObjectName newObjectName(String s) {
      String name = s;
      if (!s.startsWith(this.amxJMXDomain())) {
         name = this.amxJMXDomain() + ":" + s;
      }

      return AMXUtil.newObjectName(name);
   }

   private static String prop(String key, String value) {
      return key + "=" + value;
   }

   public ObjectName getBootAMXMBeanObjectName() {
      return AMXUtil.newObjectName(this.amxSupportDomain() + ":type=boot-amx");
   }

   public void invokeBootAMX(MBeanServerConnection conn) {
      try {
         conn.invoke(this.getBootAMXMBeanObjectName(), "bootAMX", (Object[])null, (String[])null);
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new RuntimeException(var3);
      }
   }

   private static void invokeWaitAMXReady(MBeanServerConnection conn, ObjectName objectName) {
      try {
         conn.invoke(objectName, "waitAMXReady", (Object[])null, (String[])null);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public <T extends MBeanListener.Callback> MBeanListener<T> listenForDomainRoot(MBeanServerConnection server, T callback) {
      MBeanListener<T> listener = new MBeanListener(server, this.domainRoot(), callback);
      listener.startListening();
      return listener;
   }

   public ObjectName waitAMXReady(MBeanServerConnection server) {
      AMXGlassfish.WaitForDomainRootListenerCallback callback = new AMXGlassfish.WaitForDomainRootListenerCallback(server);
      this.listenForDomainRoot(server, callback);
      callback.await();
      return callback.getRegistered();
   }

   public <T extends MBeanListener.Callback> MBeanListener<T> listenForBootAMX(MBeanServerConnection server, T callback) {
      MBeanListener<T> listener = new MBeanListener(server, this.getBootAMXMBeanObjectName(), callback);
      listener.startListening();
      return listener;
   }

   public ObjectName bootAMX(MBeanServerConnection conn) throws IOException {
      ObjectName domainRoot = this.domainRoot();
      if (!conn.isRegistered(domainRoot)) {
         AMXGlassfish.BootAMXCallback callback = new AMXGlassfish.BootAMXCallback(conn);
         this.listenForBootAMX(conn, callback);
         callback.await();
         this.invokeBootAMX(conn);
         AMXGlassfish.WaitForDomainRootListenerCallback drCallback = new AMXGlassfish.WaitForDomainRootListenerCallback(conn);
         this.listenForDomainRoot(conn, drCallback);
         drCallback.await();
         invokeWaitAMXReady(conn, domainRoot);
      } else {
         invokeWaitAMXReady(conn, domainRoot);
      }

      return domainRoot;
   }

   public ObjectName bootAMX(MBeanServer server) {
      try {
         return this.bootAMX((MBeanServerConnection)server);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static class BootAMXCallback extends MBeanListener.CallbackImpl {
      private final MBeanServerConnection mConn;

      public BootAMXCallback(MBeanServerConnection conn) {
         this.mConn = conn;
      }

      public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
         super.mbeanRegistered(objectName, listener);
         this.mLatch.countDown();
      }
   }

   private static final class WaitForDomainRootListenerCallback extends MBeanListener.CallbackImpl {
      private final MBeanServerConnection mConn;

      public WaitForDomainRootListenerCallback(MBeanServerConnection conn) {
         this.mConn = conn;
      }

      public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
         super.mbeanRegistered(objectName, listener);
         AMXGlassfish.invokeWaitAMXReady(this.mConn, objectName);
         this.mLatch.countDown();
      }
   }
}
