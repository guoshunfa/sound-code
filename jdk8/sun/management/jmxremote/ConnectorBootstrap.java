package sun.management.jmxremote;

import com.sun.jmx.remote.internal.RMIExporter;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.security.auth.Subject;
import sun.management.Agent;
import sun.management.AgentConfigurationError;
import sun.management.ConnectorAddressLink;
import sun.management.FileSystem;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;

public final class ConnectorBootstrap {
   private static Registry registry = null;
   private static final ClassLogger log = new ClassLogger(ConnectorBootstrap.class.getPackage().getName(), "ConnectorBootstrap");

   public static void unexportRegistry() {
      try {
         if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
            registry = null;
         }
      } catch (NoSuchObjectException var1) {
      }

   }

   public static synchronized JMXConnectorServer initialize() {
      Properties var0 = Agent.loadManagementProperties();
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.getProperty("com.sun.management.jmxremote.port");
         return startRemoteConnectorServer(var1, var0);
      }
   }

   public static synchronized JMXConnectorServer initialize(String var0, Properties var1) {
      return startRemoteConnectorServer(var0, var1);
   }

   public static synchronized JMXConnectorServer startRemoteConnectorServer(String var0, Properties var1) {
      int var2;
      try {
         var2 = Integer.parseInt(var0);
      } catch (NumberFormatException var28) {
         throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", var28, new String[]{var0});
      }

      if (var2 < 0) {
         throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[]{var0});
      } else {
         int var3 = 0;
         String var4 = var1.getProperty("com.sun.management.jmxremote.rmi.port");

         try {
            if (var4 != null) {
               var3 = Integer.parseInt(var4);
            }
         } catch (NumberFormatException var29) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", var29, new String[]{var4});
         }

         if (var3 < 0) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", new String[]{var4});
         } else {
            String var5 = var1.getProperty("com.sun.management.jmxremote.authenticate", "true");
            boolean var6 = Boolean.valueOf(var5);
            String var7 = var1.getProperty("com.sun.management.jmxremote.ssl", "true");
            boolean var8 = Boolean.valueOf(var7);
            String var9 = var1.getProperty("com.sun.management.jmxremote.registry.ssl", "false");
            boolean var10 = Boolean.valueOf(var9);
            String var11 = var1.getProperty("com.sun.management.jmxremote.ssl.enabled.cipher.suites");
            String[] var12 = null;
            if (var11 != null) {
               StringTokenizer var13 = new StringTokenizer(var11, ",");
               int var14 = var13.countTokens();
               var12 = new String[var14];

               for(int var15 = 0; var15 < var14; ++var15) {
                  var12[var15] = var13.nextToken();
               }
            }

            String var30 = var1.getProperty("com.sun.management.jmxremote.ssl.enabled.protocols");
            String[] var31 = null;
            if (var30 != null) {
               StringTokenizer var32 = new StringTokenizer(var30, ",");
               int var16 = var32.countTokens();
               var31 = new String[var16];

               for(int var17 = 0; var17 < var16; ++var17) {
                  var31[var17] = var32.nextToken();
               }
            }

            String var33 = var1.getProperty("com.sun.management.jmxremote.ssl.need.client.auth", "false");
            boolean var34 = Boolean.valueOf(var33);
            String var35 = var1.getProperty("com.sun.management.jmxremote.ssl.config.file");
            String var18 = null;
            String var19 = null;
            String var20 = null;
            if (var6) {
               var18 = var1.getProperty("com.sun.management.jmxremote.login.config");
               if (var18 == null) {
                  var19 = var1.getProperty("com.sun.management.jmxremote.password.file", getDefaultFileName("jmxremote.password"));
                  checkPasswordFile(var19);
               }

               var20 = var1.getProperty("com.sun.management.jmxremote.access.file", getDefaultFileName("jmxremote.access"));
               checkAccessFile(var20);
            }

            String var21 = var1.getProperty("com.sun.management.jmxremote.host");
            if (log.debugOn()) {
               log.debug("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.starting") + "\n\t" + "com.sun.management.jmxremote.port" + "=" + var2 + (var21 == null ? "" : "\n\tcom.sun.management.jmxremote.host=" + var21) + "\n\t" + "com.sun.management.jmxremote.rmi.port" + "=" + var3 + "\n\t" + "com.sun.management.jmxremote.ssl" + "=" + var8 + "\n\t" + "com.sun.management.jmxremote.registry.ssl" + "=" + var10 + "\n\t" + "com.sun.management.jmxremote.ssl.config.file" + "=" + var35 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.cipher.suites" + "=" + var11 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.protocols" + "=" + var30 + "\n\t" + "com.sun.management.jmxremote.ssl.need.client.auth" + "=" + var34 + "\n\t" + "com.sun.management.jmxremote.authenticate" + "=" + var6 + (var6 ? (var18 == null ? "\n\tcom.sun.management.jmxremote.password.file=" + var19 : "\n\tcom.sun.management.jmxremote.login.config=" + var18) : "\n\t" + Agent.getText("jmxremote.ConnectorBootstrap.noAuthentication")) + (var6 ? "\n\tcom.sun.management.jmxremote.access.file=" + var20 : "") + "");
            }

            MBeanServer var22 = ManagementFactory.getPlatformMBeanServer();
            JMXConnectorServer var23 = null;
            JMXServiceURL var24 = null;

            try {
               ConnectorBootstrap.JMXConnectorServerData var25 = exportMBeanServer(var22, var2, var3, var8, var10, var35, var12, var31, var34, var6, var18, var19, var20, var21);
               var23 = var25.jmxConnectorServer;
               var24 = var25.jmxRemoteURL;
               log.config("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.ready", var24.toString()));
            } catch (Exception var27) {
               throw new AgentConfigurationError("agent.err.exception", var27, new String[]{var27.toString()});
            }

            try {
               HashMap var36 = new HashMap();
               var36.put("remoteAddress", var24.toString());
               var36.put("authenticate", var5);
               var36.put("ssl", var7);
               var36.put("sslRegistry", var9);
               var36.put("sslNeedClientAuth", var33);
               ConnectorAddressLink.exportRemote(var36);
            } catch (Exception var26) {
               log.debug("startRemoteConnectorServer", (Throwable)var26);
            }

            return var23;
         }
      }
   }

   public static JMXConnectorServer startLocalConnectorServer() {
      System.setProperty("java.rmi.server.randomIDs", "true");
      HashMap var0 = new HashMap();
      var0.put("com.sun.jmx.remote.rmi.exporter", new ConnectorBootstrap.PermanentExporter());
      var0.put("jmx.remote.rmi.server.credential.types", new String[]{String[].class.getName(), String.class.getName()});
      String var1 = "localhost";
      InetAddress var2 = null;

      try {
         var2 = InetAddress.getByName(var1);
         var1 = var2.getHostAddress();
      } catch (UnknownHostException var10) {
      }

      if (var2 == null || !var2.isLoopbackAddress()) {
         var1 = "127.0.0.1";
      }

      MBeanServer var3 = ManagementFactory.getPlatformMBeanServer();

      try {
         JMXServiceURL var4 = new JMXServiceURL("rmi", var1, 0);
         Properties var5 = Agent.getManagementProperties();
         if (var5 == null) {
            var5 = new Properties();
         }

         String var6 = var5.getProperty("com.sun.management.jmxremote.local.only", "true");
         boolean var7 = Boolean.valueOf(var6);
         if (var7) {
            var0.put("jmx.remote.rmi.server.socket.factory", new LocalRMIServerSocketFactory());
         }

         JMXConnectorServer var8 = JMXConnectorServerFactory.newJMXConnectorServer(var4, var0, var3);
         var8.start();
         return var8;
      } catch (Exception var9) {
         throw new AgentConfigurationError("agent.err.exception", var9, new String[]{var9.toString()});
      }
   }

   private static void checkPasswordFile(String var0) {
      if (var0 != null && var0.length() != 0) {
         File var1 = new File(var0);
         if (!var1.exists()) {
            throw new AgentConfigurationError("agent.err.password.file.notfound", new String[]{var0});
         } else if (!var1.canRead()) {
            throw new AgentConfigurationError("agent.err.password.file.not.readable", new String[]{var0});
         } else {
            FileSystem var2 = FileSystem.open();

            try {
               if (var2.supportsFileSecurity(var1) && !var2.isAccessUserOnly(var1)) {
                  String var3 = Agent.getText("jmxremote.ConnectorBootstrap.password.readonly", var0);
                  log.config("startRemoteConnectorServer", var3);
                  throw new AgentConfigurationError("agent.err.password.file.access.notrestricted", new String[]{var0});
               }
            } catch (IOException var4) {
               throw new AgentConfigurationError("agent.err.password.file.read.failed", var4, new String[]{var0});
            }
         }
      } else {
         throw new AgentConfigurationError("agent.err.password.file.notset");
      }
   }

   private static void checkAccessFile(String var0) {
      if (var0 != null && var0.length() != 0) {
         File var1 = new File(var0);
         if (!var1.exists()) {
            throw new AgentConfigurationError("agent.err.access.file.notfound", new String[]{var0});
         } else if (!var1.canRead()) {
            throw new AgentConfigurationError("agent.err.access.file.not.readable", new String[]{var0});
         }
      } else {
         throw new AgentConfigurationError("agent.err.access.file.notset");
      }
   }

   private static void checkRestrictedFile(String var0) {
      if (var0 != null && var0.length() != 0) {
         File var1 = new File(var0);
         if (!var1.exists()) {
            throw new AgentConfigurationError("agent.err.file.not.found", new String[]{var0});
         } else if (!var1.canRead()) {
            throw new AgentConfigurationError("agent.err.file.not.readable", new String[]{var0});
         } else {
            FileSystem var2 = FileSystem.open();

            try {
               if (var2.supportsFileSecurity(var1) && !var2.isAccessUserOnly(var1)) {
                  String var3 = Agent.getText("jmxremote.ConnectorBootstrap.file.readonly", var0);
                  log.config("startRemoteConnectorServer", var3);
                  throw new AgentConfigurationError("agent.err.file.access.not.restricted", new String[]{var0});
               }
            } catch (IOException var4) {
               throw new AgentConfigurationError("agent.err.file.read.failed", var4, new String[]{var0});
            }
         }
      } else {
         throw new AgentConfigurationError("agent.err.file.not.set");
      }
   }

   private static String getDefaultFileName(String var0) {
      String var1 = File.separator;
      return System.getProperty("java.home") + var1 + "lib" + var1 + "management" + var1 + var0;
   }

   private static SslRMIServerSocketFactory createSslRMIServerSocketFactory(String var0, String[] var1, String[] var2, boolean var3, String var4) {
      if (var0 == null) {
         return new ConnectorBootstrap.HostAwareSslSocketFactory(var1, var2, var3, var4);
      } else {
         checkRestrictedFile(var0);

         try {
            Properties var5 = new Properties();
            FileInputStream var6 = new FileInputStream(var0);
            Throwable var7 = null;

            try {
               BufferedInputStream var8 = new BufferedInputStream(var6);
               var5.load((InputStream)var8);
            } catch (Throwable var61) {
               var7 = var61;
               throw var61;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var57) {
                        var7.addSuppressed(var57);
                     }
                  } else {
                     var6.close();
                  }
               }

            }

            String var66 = var5.getProperty("javax.net.ssl.keyStore");
            String var67 = var5.getProperty("javax.net.ssl.keyStorePassword", "");
            String var68 = var5.getProperty("javax.net.ssl.trustStore");
            String var9 = var5.getProperty("javax.net.ssl.trustStorePassword", "");
            char[] var10 = null;
            if (var67.length() != 0) {
               var10 = var67.toCharArray();
            }

            char[] var11 = null;
            if (var9.length() != 0) {
               var11 = var9.toCharArray();
            }

            KeyStore var12 = null;
            if (var66 != null) {
               var12 = KeyStore.getInstance(KeyStore.getDefaultType());
               FileInputStream var13 = new FileInputStream(var66);
               Throwable var14 = null;

               try {
                  var12.load(var13, var10);
               } catch (Throwable var60) {
                  var14 = var60;
                  throw var60;
               } finally {
                  if (var13 != null) {
                     if (var14 != null) {
                        try {
                           var13.close();
                        } catch (Throwable var56) {
                           var14.addSuppressed(var56);
                        }
                     } else {
                        var13.close();
                     }
                  }

               }
            }

            KeyManagerFactory var69 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            var69.init(var12, var10);
            KeyStore var70 = null;
            if (var68 != null) {
               var70 = KeyStore.getInstance(KeyStore.getDefaultType());
               FileInputStream var15 = new FileInputStream(var68);
               Throwable var16 = null;

               try {
                  var70.load(var15, var11);
               } catch (Throwable var59) {
                  var16 = var59;
                  throw var59;
               } finally {
                  if (var15 != null) {
                     if (var16 != null) {
                        try {
                           var15.close();
                        } catch (Throwable var58) {
                           var16.addSuppressed(var58);
                        }
                     } else {
                        var15.close();
                     }
                  }

               }
            }

            TrustManagerFactory var71 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var71.init(var70);
            SSLContext var72 = SSLContext.getInstance("SSL");
            var72.init(var69.getKeyManagers(), var71.getTrustManagers(), (SecureRandom)null);
            return new ConnectorBootstrap.HostAwareSslSocketFactory(var72, var1, var2, var3, var4);
         } catch (Exception var65) {
            throw new AgentConfigurationError("agent.err.exception", var65, new String[]{var65.toString()});
         }
      }
   }

   private static ConnectorBootstrap.JMXConnectorServerData exportMBeanServer(MBeanServer var0, int var1, int var2, boolean var3, boolean var4, String var5, String[] var6, String[] var7, boolean var8, boolean var9, String var10, String var11, String var12, String var13) throws IOException, MalformedURLException {
      System.setProperty("java.rmi.server.randomIDs", "true");
      JMXServiceURL var14 = new JMXServiceURL("rmi", var13, var2);
      HashMap var15 = new HashMap();
      ConnectorBootstrap.PermanentExporter var16 = new ConnectorBootstrap.PermanentExporter();
      var15.put("com.sun.jmx.remote.rmi.exporter", var16);
      var15.put("jmx.remote.rmi.server.credential.types", new String[]{String[].class.getName(), String.class.getName()});
      boolean var17 = var13 != null && !var3;
      if (var9) {
         if (var10 != null) {
            var15.put("jmx.remote.x.login.config", var10);
         }

         if (var11 != null) {
            var15.put("jmx.remote.x.password.file", var11);
         }

         var15.put("jmx.remote.x.access.file", var12);
         if (var15.get("jmx.remote.x.password.file") != null || var15.get("jmx.remote.x.login.config") != null) {
            var15.put("jmx.remote.authenticator", new ConnectorBootstrap.AccessFileCheckerAuthenticator(var15));
         }
      }

      SslRMIClientSocketFactory var18 = null;
      Object var19 = null;
      if (var3 || var4) {
         var18 = new SslRMIClientSocketFactory();
         var19 = createSslRMIServerSocketFactory(var5, var6, var7, var8, var13);
      }

      if (var3) {
         var15.put("jmx.remote.rmi.client.socket.factory", var18);
         var15.put("jmx.remote.rmi.server.socket.factory", var19);
      }

      if (var17) {
         var19 = new ConnectorBootstrap.HostAwareSocketFactory(var13);
         var15.put("jmx.remote.rmi.server.socket.factory", var19);
      }

      JMXConnectorServer var20 = null;

      try {
         var20 = JMXConnectorServerFactory.newJMXConnectorServer(var14, var15, var0);
         var20.start();
      } catch (IOException var24) {
         if (var20 != null && var20.getAddress() != null) {
            throw new AgentConfigurationError("agent.err.connector.server.io.error", var24, new String[]{var20.getAddress().toString()});
         }

         throw new AgentConfigurationError("agent.err.connector.server.io.error", var24, new String[]{var14.toString()});
      }

      if (var4) {
         registry = new SingleEntryRegistry(var1, var18, (RMIServerSocketFactory)var19, "jmxrmi", var16.firstExported);
      } else if (var17) {
         registry = new SingleEntryRegistry(var1, var18, (RMIServerSocketFactory)var19, "jmxrmi", var16.firstExported);
      } else {
         registry = new SingleEntryRegistry(var1, "jmxrmi", var16.firstExported);
      }

      int var21 = ((UnicastRef)((RemoteObject)registry).getRef()).getLiveRef().getPort();
      String var22 = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", var14.getHost(), var21);
      JMXServiceURL var23 = new JMXServiceURL(var22);
      return new ConnectorBootstrap.JMXConnectorServerData(var20, var23);
   }

   private ConnectorBootstrap() {
   }

   private static class SslServerSocket extends ServerSocket {
      private static SSLSocketFactory defaultSSLSocketFactory;
      private final String[] enabledCipherSuites;
      private final String[] enabledProtocols;
      private final boolean needClientAuth;
      private final SSLContext context;

      private SslServerSocket(int var1, SSLContext var2, String[] var3, String[] var4, boolean var5) throws IOException {
         super(var1);
         this.enabledProtocols = var4;
         this.enabledCipherSuites = var3;
         this.needClientAuth = var5;
         this.context = var2;
      }

      private SslServerSocket(int var1, int var2, InetAddress var3, SSLContext var4, String[] var5, String[] var6, boolean var7) throws IOException {
         super(var1, var2, var3);
         this.enabledProtocols = var6;
         this.enabledCipherSuites = var5;
         this.needClientAuth = var7;
         this.context = var4;
      }

      public Socket accept() throws IOException {
         SSLSocketFactory var1 = this.context == null ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
         Socket var2 = super.accept();
         SSLSocket var3 = (SSLSocket)var1.createSocket(var2, var2.getInetAddress().getHostName(), var2.getPort(), true);
         var3.setUseClientMode(false);
         if (this.enabledCipherSuites != null) {
            var3.setEnabledCipherSuites(this.enabledCipherSuites);
         }

         if (this.enabledProtocols != null) {
            var3.setEnabledProtocols(this.enabledProtocols);
         }

         var3.setNeedClientAuth(this.needClientAuth);
         return var3;
      }

      private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
         if (defaultSSLSocketFactory == null) {
            defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            return defaultSSLSocketFactory;
         } else {
            return defaultSSLSocketFactory;
         }
      }

      // $FF: synthetic method
      SslServerSocket(int var1, int var2, InetAddress var3, SSLContext var4, String[] var5, String[] var6, boolean var7, Object var8) throws IOException {
         this(var1, var2, var3, var4, var5, var6, var7);
      }

      // $FF: synthetic method
      SslServerSocket(int var1, SSLContext var2, String[] var3, String[] var4, boolean var5, Object var6) throws IOException {
         this(var1, var2, var3, var4, var5);
      }
   }

   private static class HostAwareSslSocketFactory extends SslRMIServerSocketFactory {
      private final String bindAddress;
      private final String[] enabledCipherSuites;
      private final String[] enabledProtocols;
      private final boolean needClientAuth;
      private final SSLContext context;

      private HostAwareSslSocketFactory(String[] var1, String[] var2, boolean var3, String var4) throws IllegalArgumentException {
         this((SSLContext)null, var1, var2, var3, var4);
      }

      private HostAwareSslSocketFactory(SSLContext var1, String[] var2, String[] var3, boolean var4, String var5) throws IllegalArgumentException {
         this.context = var1;
         this.bindAddress = var5;
         this.enabledProtocols = var3;
         this.enabledCipherSuites = var2;
         this.needClientAuth = var4;
         checkValues(var1, var2, var3);
      }

      public ServerSocket createServerSocket(int var1) throws IOException {
         if (this.bindAddress != null) {
            try {
               InetAddress var2 = InetAddress.getByName(this.bindAddress);
               return new ConnectorBootstrap.SslServerSocket(var1, 0, var2, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
            } catch (UnknownHostException var3) {
               return new ConnectorBootstrap.SslServerSocket(var1, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
            }
         } else {
            return new ConnectorBootstrap.SslServerSocket(var1, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
         }
      }

      private static void checkValues(SSLContext var0, String[] var1, String[] var2) throws IllegalArgumentException {
         SSLSocketFactory var3 = var0 == null ? (SSLSocketFactory)SSLSocketFactory.getDefault() : var0.getSocketFactory();
         SSLSocket var4 = null;
         if (var1 != null || var2 != null) {
            try {
               var4 = (SSLSocket)var3.createSocket();
            } catch (Exception var7) {
               throw (IllegalArgumentException)(new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported")).initCause(var7);
            }
         }

         if (var1 != null) {
            var4.setEnabledCipherSuites(var1);
         }

         if (var2 != null) {
            var4.setEnabledProtocols(var2);
         }

      }

      // $FF: synthetic method
      HostAwareSslSocketFactory(String[] var1, String[] var2, boolean var3, String var4, Object var5) throws IllegalArgumentException {
         this(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      HostAwareSslSocketFactory(SSLContext var1, String[] var2, String[] var3, boolean var4, String var5, Object var6) throws IllegalArgumentException {
         this(var1, var2, var3, var4, var5);
      }
   }

   private static class HostAwareSocketFactory implements RMIServerSocketFactory {
      private final String bindAddress;

      private HostAwareSocketFactory(String var1) {
         this.bindAddress = var1;
      }

      public ServerSocket createServerSocket(int var1) throws IOException {
         if (this.bindAddress == null) {
            return new ServerSocket(var1);
         } else {
            try {
               InetAddress var2 = InetAddress.getByName(this.bindAddress);
               return new ServerSocket(var1, 0, var2);
            } catch (UnknownHostException var3) {
               return new ServerSocket(var1);
            }
         }
      }

      // $FF: synthetic method
      HostAwareSocketFactory(String var1, Object var2) {
         this(var1);
      }
   }

   private static class AccessFileCheckerAuthenticator implements JMXAuthenticator {
      private final Map<String, Object> environment;
      private final Properties properties;
      private final String accessFile;

      public AccessFileCheckerAuthenticator(Map<String, Object> var1) throws IOException {
         this.environment = var1;
         this.accessFile = (String)var1.get("jmx.remote.x.access.file");
         this.properties = propertiesFromFile(this.accessFile);
      }

      public Subject authenticate(Object var1) {
         JMXPluggableAuthenticator var2 = new JMXPluggableAuthenticator(this.environment);
         Subject var3 = var2.authenticate(var1);
         this.checkAccessFileEntries(var3);
         return var3;
      }

      private void checkAccessFileEntries(Subject var1) {
         if (var1 == null) {
            throw new SecurityException("Access denied! No matching entries found in the access file [" + this.accessFile + "] as the authenticated Subject is null");
         } else {
            Set var2 = var1.getPrincipals();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Principal var4 = (Principal)var3.next();
               if (this.properties.containsKey(var4.getName())) {
                  return;
               }
            }

            HashSet var6 = new HashSet();
            Iterator var7 = var2.iterator();

            while(var7.hasNext()) {
               Principal var5 = (Principal)var7.next();
               var6.add(var5.getName());
            }

            throw new SecurityException("Access denied! No entries found in the access file [" + this.accessFile + "] for any of the authenticated identities " + var6);
         }
      }

      private static Properties propertiesFromFile(String var0) throws IOException {
         Properties var1 = new Properties();
         if (var0 == null) {
            return var1;
         } else {
            FileInputStream var2 = new FileInputStream(var0);
            Throwable var3 = null;

            try {
               var1.load((InputStream)var2);
            } catch (Throwable var12) {
               var3 = var12;
               throw var12;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var1;
         }
      }
   }

   private static class PermanentExporter implements RMIExporter {
      Remote firstExported;

      private PermanentExporter() {
      }

      public Remote exportObject(Remote var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) throws RemoteException {
         synchronized(this) {
            if (this.firstExported == null) {
               this.firstExported = var1;
            }
         }

         Object var5;
         if (var3 == null && var4 == null) {
            var5 = new UnicastServerRef(var2);
         } else {
            var5 = new UnicastServerRef2(var2, var3, var4);
         }

         return ((UnicastServerRef)var5).exportObject(var1, (Object)null, true);
      }

      public boolean unexportObject(Remote var1, boolean var2) throws NoSuchObjectException {
         return UnicastRemoteObject.unexportObject(var1, var2);
      }

      // $FF: synthetic method
      PermanentExporter(Object var1) {
         this();
      }
   }

   private static class JMXConnectorServerData {
      JMXConnectorServer jmxConnectorServer;
      JMXServiceURL jmxRemoteURL;

      public JMXConnectorServerData(JMXConnectorServer var1, JMXServiceURL var2) {
         this.jmxConnectorServer = var1;
         this.jmxRemoteURL = var2;
      }
   }

   public interface PropertyNames {
      String PORT = "com.sun.management.jmxremote.port";
      String HOST = "com.sun.management.jmxremote.host";
      String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
      String CONFIG_FILE_NAME = "com.sun.management.config.file";
      String USE_LOCAL_ONLY = "com.sun.management.jmxremote.local.only";
      String USE_SSL = "com.sun.management.jmxremote.ssl";
      String USE_REGISTRY_SSL = "com.sun.management.jmxremote.registry.ssl";
      String USE_AUTHENTICATION = "com.sun.management.jmxremote.authenticate";
      String PASSWORD_FILE_NAME = "com.sun.management.jmxremote.password.file";
      String ACCESS_FILE_NAME = "com.sun.management.jmxremote.access.file";
      String LOGIN_CONFIG_NAME = "com.sun.management.jmxremote.login.config";
      String SSL_ENABLED_CIPHER_SUITES = "com.sun.management.jmxremote.ssl.enabled.cipher.suites";
      String SSL_ENABLED_PROTOCOLS = "com.sun.management.jmxremote.ssl.enabled.protocols";
      String SSL_NEED_CLIENT_AUTH = "com.sun.management.jmxremote.ssl.need.client.auth";
      String SSL_CONFIG_FILE_NAME = "com.sun.management.jmxremote.ssl.config.file";
   }

   public interface DefaultValues {
      String PORT = "0";
      String CONFIG_FILE_NAME = "management.properties";
      String USE_SSL = "true";
      String USE_LOCAL_ONLY = "true";
      String USE_REGISTRY_SSL = "false";
      String USE_AUTHENTICATION = "true";
      String PASSWORD_FILE_NAME = "jmxremote.password";
      String ACCESS_FILE_NAME = "jmxremote.access";
      String SSL_NEED_CLIENT_AUTH = "false";
   }
}
