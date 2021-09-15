package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.ArrayNotificationBuffer;
import com.sun.jmx.remote.internal.NotificationBuffer;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.Subject;

public abstract class RMIServerImpl implements Closeable, RMIServer {
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIServerImpl");
   private final List<WeakReference<RMIConnection>> clientList = new ArrayList();
   private ClassLoader cl;
   private MBeanServer mbeanServer;
   private final Map<String, ?> env;
   private RMIConnectorServer connServer;
   private static int connectionIdNumber;
   private NotificationBuffer notifBuffer;

   public RMIServerImpl(Map<String, ?> var1) {
      this.env = var1 == null ? Collections.emptyMap() : var1;
   }

   void setRMIConnectorServer(RMIConnectorServer var1) throws IOException {
      this.connServer = var1;
   }

   protected abstract void export() throws IOException;

   public abstract Remote toStub() throws IOException;

   public synchronized void setDefaultClassLoader(ClassLoader var1) {
      this.cl = var1;
   }

   public synchronized ClassLoader getDefaultClassLoader() {
      return this.cl;
   }

   public synchronized void setMBeanServer(MBeanServer var1) {
      this.mbeanServer = var1;
   }

   public synchronized MBeanServer getMBeanServer() {
      return this.mbeanServer;
   }

   public String getVersion() {
      try {
         return "1.0 java_runtime_" + System.getProperty("java.runtime.version");
      } catch (SecurityException var2) {
         return "1.0 ";
      }
   }

   public RMIConnection newClient(Object var1) throws IOException {
      return this.doNewClient(var1);
   }

   RMIConnection doNewClient(Object var1) throws IOException {
      boolean var2 = logger.traceOn();
      if (var2) {
         logger.trace("newClient", "making new client");
      }

      if (this.getMBeanServer() == null) {
         throw new IllegalStateException("Not attached to an MBean server");
      } else {
         Subject var3 = null;
         Object var4 = (JMXAuthenticator)this.env.get("jmx.remote.authenticator");
         if (var4 == null && (this.env.get("jmx.remote.x.password.file") != null || this.env.get("jmx.remote.x.login.config") != null)) {
            var4 = new JMXPluggableAuthenticator(this.env);
         }

         if (var4 != null) {
            if (var2) {
               logger.trace("newClient", "got authenticator: " + var4.getClass().getName());
            }

            try {
               var3 = ((JMXAuthenticator)var4).authenticate(var1);
            } catch (SecurityException var12) {
               logger.trace("newClient", "Authentication failed: " + var12);
               throw var12;
            }
         }

         if (var2) {
            if (var3 != null) {
               logger.trace("newClient", "subject is not null");
            } else {
               logger.trace("newClient", "no subject");
            }
         }

         String var5 = makeConnectionId(this.getProtocol(), var3);
         if (var2) {
            logger.trace("newClient", "making new connection: " + var5);
         }

         RMIConnection var6 = this.makeClient(var5, var3);
         this.dropDeadReferences();
         WeakReference var7 = new WeakReference(var6);
         synchronized(this.clientList) {
            this.clientList.add(var7);
         }

         this.connServer.connectionOpened(var5, "Connection opened", (Object)null);
         synchronized(this.clientList) {
            if (!this.clientList.contains(var7)) {
               throw new IOException("The connection is refused.");
            }
         }

         if (var2) {
            logger.trace("newClient", "new connection done: " + var5);
         }

         return var6;
      }
   }

   protected abstract RMIConnection makeClient(String var1, Subject var2) throws IOException;

   protected abstract void closeClient(RMIConnection var1) throws IOException;

   protected abstract String getProtocol();

   protected void clientClosed(RMIConnection var1) throws IOException {
      boolean var2 = logger.debugOn();
      if (var2) {
         logger.trace("clientClosed", "client=" + var1);
      }

      if (var1 == null) {
         throw new NullPointerException("Null client");
      } else {
         synchronized(this.clientList) {
            this.dropDeadReferences();
            Iterator var4 = this.clientList.iterator();

            while(var4.hasNext()) {
               WeakReference var5 = (WeakReference)var4.next();
               if (var5.get() == var1) {
                  var4.remove();
                  break;
               }
            }
         }

         if (var2) {
            logger.trace("clientClosed", "closing client.");
         }

         this.closeClient(var1);
         if (var2) {
            logger.trace("clientClosed", "sending notif");
         }

         this.connServer.connectionClosed(var1.getConnectionId(), "Client connection closed", (Object)null);
         if (var2) {
            logger.trace("clientClosed", "done");
         }

      }
   }

   public synchronized void close() throws IOException {
      boolean var1 = logger.traceOn();
      boolean var2 = logger.debugOn();
      if (var1) {
         logger.trace("close", "closing");
      }

      IOException var3 = null;

      try {
         if (var2) {
            logger.debug("close", "closing Server");
         }

         this.closeServer();
      } catch (IOException var12) {
         if (var1) {
            logger.trace("close", "Failed to close server: " + var12);
         }

         if (var2) {
            logger.debug("close", (Throwable)var12);
         }

         var3 = var12;
      }

      if (var2) {
         logger.debug("close", "closing Clients");
      }

      while(true) {
         synchronized(this.clientList) {
            if (var2) {
               logger.debug("close", "droping dead references");
            }

            this.dropDeadReferences();
            if (var2) {
               logger.debug("close", "client count: " + this.clientList.size());
            }

            if (this.clientList.size() == 0) {
               break;
            }

            Iterator var5 = this.clientList.iterator();

            while(var5.hasNext()) {
               WeakReference var6 = (WeakReference)var5.next();
               RMIConnection var7 = (RMIConnection)var6.get();
               var5.remove();
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (IOException var10) {
                     if (var1) {
                        logger.trace("close", "Failed to close client: " + var10);
                     }

                     if (var2) {
                        logger.debug("close", (Throwable)var10);
                     }

                     if (var3 == null) {
                        var3 = var10;
                     }
                  }
                  break;
               }
            }
         }
      }

      if (this.notifBuffer != null) {
         this.notifBuffer.dispose();
      }

      if (var3 != null) {
         if (var1) {
            logger.trace("close", "close failed.");
         }

         throw var3;
      } else {
         if (var1) {
            logger.trace("close", "closed.");
         }

      }
   }

   protected abstract void closeServer() throws IOException;

   private static synchronized String makeConnectionId(String var0, Subject var1) {
      ++connectionIdNumber;
      String var2 = "";

      try {
         var2 = RemoteServer.getClientHost();
         if (var2.contains(":")) {
            var2 = "[" + var2 + "]";
         }
      } catch (ServerNotActiveException var9) {
         logger.trace("makeConnectionId", "getClientHost", var9);
      }

      StringBuilder var3 = new StringBuilder();
      var3.append(var0).append(":");
      if (var2.length() > 0) {
         var3.append("//").append(var2);
      }

      var3.append(" ");
      if (var1 != null) {
         Set var4 = var1.getPrincipals();
         String var5 = "";

         for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = ";") {
            Principal var7 = (Principal)var6.next();
            String var8 = var7.getName().replace(' ', '_').replace(';', ':');
            var3.append(var5).append(var8);
         }
      }

      var3.append(" ").append(connectionIdNumber);
      if (logger.traceOn()) {
         logger.trace("newConnectionId", "connectionId=" + var3);
      }

      return var3.toString();
   }

   private void dropDeadReferences() {
      synchronized(this.clientList) {
         Iterator var2 = this.clientList.iterator();

         while(var2.hasNext()) {
            WeakReference var3 = (WeakReference)var2.next();
            if (var3.get() == null) {
               var2.remove();
            }
         }

      }
   }

   synchronized NotificationBuffer getNotifBuffer() {
      if (this.notifBuffer == null) {
         this.notifBuffer = ArrayNotificationBuffer.getNotificationBuffer(this.mbeanServer, this.env);
      }

      return this.notifBuffer;
   }
}
