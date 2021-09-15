package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.security.MBeanServerFileAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RMIConnectorServer extends JMXConnectorServer {
   public static final String JNDI_REBIND_ATTRIBUTE = "jmx.remote.jndi.rebind";
   public static final String RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.client.socket.factory";
   public static final String RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.server.socket.factory";
   private static final char[] intToAlpha = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectorServer");
   private JMXServiceURL address;
   private RMIServerImpl rmiServerImpl;
   private final Map<String, ?> attributes;
   private ClassLoader defaultClassLoader;
   private String boundJndiUrl;
   private static final int CREATED = 0;
   private static final int STARTED = 1;
   private static final int STOPPED = 2;
   private int state;
   private static final Set<RMIConnectorServer> openedServers = new HashSet();

   public RMIConnectorServer(JMXServiceURL var1, Map<String, ?> var2) throws IOException {
      this(var1, var2, (MBeanServer)null);
   }

   public RMIConnectorServer(JMXServiceURL var1, Map<String, ?> var2, MBeanServer var3) throws IOException {
      this(var1, var2, (RMIServerImpl)null, var3);
   }

   public RMIConnectorServer(JMXServiceURL var1, Map<String, ?> var2, RMIServerImpl var3, MBeanServer var4) throws IOException {
      super(var4);
      this.defaultClassLoader = null;
      this.state = 0;
      if (var1 == null) {
         throw new IllegalArgumentException("Null JMXServiceURL");
      } else {
         if (var3 == null) {
            String var5 = var1.getProtocol();
            String var6;
            if (var5 == null || !var5.equals("rmi") && !var5.equals("iiop")) {
               var6 = "Invalid protocol type: " + var5;
               throw new MalformedURLException(var6);
            }

            var6 = var1.getURLPath();
            if (!var6.equals("") && !var6.equals("/") && !var6.startsWith("/jndi/")) {
               throw new MalformedURLException("URL path must be empty or start with /jndi/");
            }
         }

         if (var2 == null) {
            this.attributes = Collections.emptyMap();
         } else {
            EnvHelp.checkAttributes(var2);
            this.attributes = Collections.unmodifiableMap(var2);
         }

         this.address = var1;
         this.rmiServerImpl = var3;
      }
   }

   public JMXConnector toJMXConnector(Map<String, ?> var1) throws IOException {
      if (!this.isActive()) {
         throw new IllegalStateException("Connector is not active");
      } else {
         HashMap var2 = new HashMap(this.attributes == null ? Collections.emptyMap() : this.attributes);
         if (var1 != null) {
            EnvHelp.checkAttributes(var1);
            var2.putAll(var1);
         }

         Map var4 = EnvHelp.filterAttributes(var2);
         RMIServer var3 = (RMIServer)this.rmiServerImpl.toStub();
         return new RMIConnector(var3, var4);
      }
   }

   public synchronized void start() throws IOException {
      boolean var1 = logger.traceOn();
      if (this.state == 1) {
         if (var1) {
            logger.trace("start", "already started");
         }

      } else if (this.state == 2) {
         if (var1) {
            logger.trace("start", "already stopped");
         }

         throw new IOException("The server has been stopped.");
      } else if (this.getMBeanServer() == null) {
         throw new IllegalStateException("This connector server is not attached to an MBean server");
      } else {
         if (this.attributes != null) {
            String var2 = (String)this.attributes.get("jmx.remote.x.access.file");
            if (var2 != null) {
               MBeanServerFileAccessController var3;
               try {
                  var3 = new MBeanServerFileAccessController(var2);
               } catch (IOException var12) {
                  throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(var12.getMessage()), var12);
               }

               this.setMBeanServerForwarder(var3);
            }
         }

         try {
            if (var1) {
               logger.trace("start", "setting default class loader");
            }

            this.defaultClassLoader = EnvHelp.resolveServerClassLoader(this.attributes, this.getMBeanServer());
         } catch (InstanceNotFoundException var14) {
            IllegalArgumentException var16 = new IllegalArgumentException("ClassLoader not found: " + var14);
            throw (IllegalArgumentException)EnvHelp.initCause(var16, var14);
         }

         if (var1) {
            logger.trace("start", "setting RMIServer object");
         }

         RMIServerImpl var15;
         if (this.rmiServerImpl != null) {
            var15 = this.rmiServerImpl;
         } else {
            var15 = this.newServer();
         }

         var15.setMBeanServer(this.getMBeanServer());
         var15.setDefaultClassLoader(this.defaultClassLoader);
         var15.setRMIConnectorServer(this);
         var15.export();

         try {
            if (var1) {
               logger.trace("start", "getting RMIServer object to export");
            }

            RMIServer var17 = objectToBind(var15, this.attributes);
            if (this.address != null && this.address.getURLPath().startsWith("/jndi/")) {
               String var4 = this.address.getURLPath().substring(6);
               if (var1) {
                  logger.trace("start", "Using external directory: " + var4);
               }

               String var5 = (String)this.attributes.get("jmx.remote.jndi.rebind");
               boolean var6 = EnvHelp.computeBooleanFromString(var5);
               if (var1) {
                  logger.trace("start", "jmx.remote.jndi.rebind=" + var6);
               }

               try {
                  if (var1) {
                     logger.trace("start", "binding to " + var4);
                  }

                  Hashtable var7 = EnvHelp.mapToHashtable(this.attributes);
                  this.bind(var4, var7, var17, var6);
                  this.boundJndiUrl = var4;
               } catch (NamingException var11) {
                  throw newIOException("Cannot bind to URL [" + var4 + "]: " + var11, var11);
               }
            } else {
               if (var1) {
                  logger.trace("start", "Encoding URL");
               }

               this.encodeStubInAddress(var17, this.attributes);
               if (var1) {
                  logger.trace("start", "Encoded URL: " + this.address);
               }
            }
         } catch (Exception var13) {
            try {
               var15.close();
            } catch (Exception var9) {
            }

            if (var13 instanceof RuntimeException) {
               throw (RuntimeException)var13;
            }

            if (var13 instanceof IOException) {
               throw (IOException)var13;
            }

            throw newIOException("Got unexpected exception while starting the connector server: " + var13, var13);
         }

         this.rmiServerImpl = var15;
         synchronized(openedServers) {
            openedServers.add(this);
         }

         this.state = 1;
         if (var1) {
            logger.trace("start", "Connector Server Address = " + this.address);
            logger.trace("start", "started.");
         }

      }
   }

   public void stop() throws IOException {
      boolean var1 = logger.traceOn();
      synchronized(this) {
         if (this.state == 2) {
            if (var1) {
               logger.trace("stop", "already stopped.");
            }

            return;
         }

         if (this.state == 0 && var1) {
            logger.trace("stop", "not started yet.");
         }

         if (var1) {
            logger.trace("stop", "stopping.");
         }

         this.state = 2;
      }

      synchronized(openedServers) {
         openedServers.remove(this);
      }

      IOException var2 = null;
      if (this.rmiServerImpl != null) {
         try {
            if (var1) {
               logger.trace("stop", "closing RMI server.");
            }

            this.rmiServerImpl.close();
         } catch (IOException var7) {
            if (var1) {
               logger.trace("stop", "failed to close RMI server: " + var7);
            }

            if (logger.debugOn()) {
               logger.debug("stop", (Throwable)var7);
            }

            var2 = var7;
         }
      }

      if (this.boundJndiUrl != null) {
         try {
            if (var1) {
               logger.trace("stop", "unbind from external directory: " + this.boundJndiUrl);
            }

            Hashtable var3 = EnvHelp.mapToHashtable(this.attributes);
            InitialContext var4 = new InitialContext(var3);
            var4.unbind(this.boundJndiUrl);
            var4.close();
         } catch (NamingException var6) {
            if (var1) {
               logger.trace("stop", "failed to unbind RMI server: " + var6);
            }

            if (logger.debugOn()) {
               logger.debug("stop", (Throwable)var6);
            }

            if (var2 == null) {
               var2 = newIOException("Cannot bind to URL: " + var6, var6);
            }
         }
      }

      if (var2 != null) {
         throw var2;
      } else {
         if (var1) {
            logger.trace("stop", "stopped");
         }

      }
   }

   public synchronized boolean isActive() {
      return this.state == 1;
   }

   public JMXServiceURL getAddress() {
      return !this.isActive() ? null : this.address;
   }

   public Map<String, ?> getAttributes() {
      Map var1 = EnvHelp.filterAttributes(this.attributes);
      return Collections.unmodifiableMap(var1);
   }

   public synchronized void setMBeanServerForwarder(MBeanServerForwarder var1) {
      super.setMBeanServerForwarder(var1);
      if (this.rmiServerImpl != null) {
         this.rmiServerImpl.setMBeanServer(this.getMBeanServer());
      }

   }

   protected void connectionOpened(String var1, String var2, Object var3) {
      super.connectionOpened(var1, var2, var3);
   }

   protected void connectionClosed(String var1, String var2, Object var3) {
      super.connectionClosed(var1, var2, var3);
   }

   protected void connectionFailed(String var1, String var2, Object var3) {
      super.connectionFailed(var1, var2, var3);
   }

   void bind(String var1, Hashtable<?, ?> var2, RMIServer var3, boolean var4) throws NamingException, MalformedURLException {
      InitialContext var5 = new InitialContext(var2);
      if (var4) {
         var5.rebind((String)var1, var3);
      } else {
         var5.bind((String)var1, var3);
      }

      var5.close();
   }

   RMIServerImpl newServer() throws IOException {
      boolean var1 = isIiopURL(this.address, true);
      int var2;
      if (this.address == null) {
         var2 = 0;
      } else {
         var2 = this.address.getPort();
      }

      return var1 ? newIIOPServer(this.attributes) : newJRMPServer(this.attributes, var2);
   }

   private void encodeStubInAddress(RMIServer var1, Map<String, ?> var2) throws IOException {
      String var3;
      String var4;
      int var5;
      if (this.address == null) {
         if (IIOPHelper.isStub(var1)) {
            var3 = "iiop";
         } else {
            var3 = "rmi";
         }

         var4 = null;
         var5 = 0;
      } else {
         var3 = this.address.getProtocol();
         var4 = this.address.getHost().equals("") ? null : this.address.getHost();
         var5 = this.address.getPort();
      }

      String var6 = encodeStub(var1, var2);
      this.address = new JMXServiceURL(var3, var4, var5, var6);
   }

   static boolean isIiopURL(JMXServiceURL var0, boolean var1) throws MalformedURLException {
      String var2 = var0.getProtocol();
      if (var2.equals("rmi")) {
         return false;
      } else if (var2.equals("iiop")) {
         return true;
      } else if (var1) {
         throw new MalformedURLException("URL must have protocol \"rmi\" or \"iiop\": \"" + var2 + "\"");
      } else {
         return false;
      }
   }

   static String encodeStub(RMIServer var0, Map<String, ?> var1) throws IOException {
      return IIOPHelper.isStub(var0) ? "/ior/" + encodeIIOPStub(var0, var1) : "/stub/" + encodeJRMPStub(var0, var1);
   }

   static String encodeJRMPStub(RMIServer var0, Map<String, ?> var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      ObjectOutputStream var3 = new ObjectOutputStream(var2);
      var3.writeObject(var0);
      var3.close();
      byte[] var4 = var2.toByteArray();
      return byteArrayToBase64(var4);
   }

   static String encodeIIOPStub(RMIServer var0, Map<String, ?> var1) throws IOException {
      try {
         Object var2 = IIOPHelper.getOrb(var0);
         return IIOPHelper.objectToString(var2, var0);
      } catch (RuntimeException var3) {
         throw newIOException(var3.getMessage(), var3);
      }
   }

   private static RMIServer objectToBind(RMIServerImpl var0, Map<String, ?> var1) throws IOException {
      return RMIConnector.connectStub((RMIServer)var0.toStub(), var1);
   }

   private static RMIServerImpl newJRMPServer(Map<String, ?> var0, int var1) throws IOException {
      RMIClientSocketFactory var2 = (RMIClientSocketFactory)var0.get("jmx.remote.rmi.client.socket.factory");
      RMIServerSocketFactory var3 = (RMIServerSocketFactory)var0.get("jmx.remote.rmi.server.socket.factory");
      return new RMIJRMPServerImpl(var1, var2, var3, var0);
   }

   private static RMIServerImpl newIIOPServer(Map<String, ?> var0) throws IOException {
      return new RMIIIOPServerImpl(var0);
   }

   private static String byteArrayToBase64(byte[] var0) {
      int var1 = var0.length;
      int var2 = var1 / 3;
      int var3 = var1 - 3 * var2;
      int var4 = 4 * ((var1 + 2) / 3);
      StringBuilder var5 = new StringBuilder(var4);
      int var6 = 0;

      int var7;
      int var8;
      for(var7 = 0; var7 < var2; ++var7) {
         var8 = var0[var6++] & 255;
         int var9 = var0[var6++] & 255;
         int var10 = var0[var6++] & 255;
         var5.append(intToAlpha[var8 >> 2]);
         var5.append(intToAlpha[var8 << 4 & 63 | var9 >> 4]);
         var5.append(intToAlpha[var9 << 2 & 63 | var10 >> 6]);
         var5.append(intToAlpha[var10 & 63]);
      }

      if (var3 != 0) {
         var7 = var0[var6++] & 255;
         var5.append(intToAlpha[var7 >> 2]);
         if (var3 == 1) {
            var5.append(intToAlpha[var7 << 4 & 63]);
            var5.append("==");
         } else {
            var8 = var0[var6++] & 255;
            var5.append(intToAlpha[var7 << 4 & 63 | var8 >> 4]);
            var5.append(intToAlpha[var8 << 2 & 63]);
            var5.append('=');
         }
      }

      return var5.toString();
   }

   private static IOException newIOException(String var0, Throwable var1) {
      IOException var2 = new IOException(var0);
      return (IOException)EnvHelp.initCause(var2, var1);
   }
}
