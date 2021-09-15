package sun.management;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import sun.management.jdp.JdpController;
import sun.management.jdp.JdpException;
import sun.management.jmxremote.ConnectorBootstrap;
import sun.misc.VMSupport;

public class Agent {
   private static Properties mgmtProps;
   private static ResourceBundle messageRB;
   private static final String CONFIG_FILE = "com.sun.management.config.file";
   private static final String SNMP_PORT = "com.sun.management.snmp.port";
   private static final String JMXREMOTE = "com.sun.management.jmxremote";
   private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
   private static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
   private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
   private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
   private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
   private static final String JDP_DEFAULT_ADDRESS = "224.0.23.178";
   private static final int JDP_DEFAULT_PORT = 7095;
   private static JMXConnectorServer jmxServer = null;

   private static Properties parseString(String var0) {
      Properties var1 = new Properties();
      if (var0 != null && !var0.trim().equals("")) {
         String[] var2 = var0.split(",");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            String[] var6 = var5.split("=", 2);
            String var7 = var6[0].trim();
            String var8 = var6.length > 1 ? var6[1].trim() : "";
            if (!var7.startsWith("com.sun.management.")) {
               error("agent.err.invalid.option", var7);
            }

            var1.setProperty(var7, var8);
         }
      }

      return var1;
   }

   public static void premain(String var0) throws Exception {
      agentmain(var0);
   }

   public static void agentmain(String var0) throws Exception {
      if (var0 == null || var0.length() == 0) {
         var0 = "com.sun.management.jmxremote";
      }

      Properties var1 = parseString(var0);
      Properties var2 = new Properties();
      String var3 = var1.getProperty("com.sun.management.config.file");
      readConfiguration(var3, var2);
      var2.putAll(var1);
      startAgent(var2);
   }

   private static synchronized void startLocalManagementAgent() {
      Properties var0 = VMSupport.getAgentProperties();
      if (var0.get("com.sun.management.jmxremote.localConnectorAddress") == null) {
         JMXConnectorServer var1 = ConnectorBootstrap.startLocalConnectorServer();
         String var2 = var1.getAddress().toString();
         var0.put("com.sun.management.jmxremote.localConnectorAddress", var2);

         try {
            ConnectorAddressLink.export(var2);
         } catch (Exception var4) {
            warning("agent.err.exportaddress.failed", var4.getMessage());
         }
      }

   }

   private static synchronized void startRemoteManagementAgent(String var0) throws Exception {
      if (jmxServer != null) {
         throw new RuntimeException(getText("agent.err.invalid.state", "Agent already started"));
      } else {
         try {
            Properties var1 = parseString(var0);
            Properties var2 = new Properties();
            String var3 = System.getProperty("com.sun.management.config.file");
            readConfiguration(var3, var2);
            Properties var4 = System.getProperties();
            synchronized(var4) {
               var2.putAll(var4);
            }

            String var5 = var1.getProperty("com.sun.management.config.file");
            if (var5 != null) {
               readConfiguration(var5, var2);
            }

            var2.putAll(var1);
            String var6 = var2.getProperty("com.sun.management.enableThreadContentionMonitoring");
            if (var6 != null) {
               java.lang.management.ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
            }

            String var7 = var2.getProperty("com.sun.management.jmxremote.port");
            if (var7 == null) {
               throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[]{"No port specified"});
            }

            jmxServer = ConnectorBootstrap.startRemoteConnectorServer(var7, var2);
            startDiscoveryService(var2);
         } catch (AgentConfigurationError var9) {
            error(var9);
         }

      }
   }

   private static synchronized void stopRemoteManagementAgent() throws Exception {
      JdpController.stopDiscoveryService();
      if (jmxServer != null) {
         ConnectorBootstrap.unexportRegistry();
         jmxServer.stop();
         jmxServer = null;
      }

   }

   private static void startAgent(Properties var0) throws Exception {
      String var1 = var0.getProperty("com.sun.management.snmp.port");
      String var2 = var0.getProperty("com.sun.management.jmxremote");
      String var3 = var0.getProperty("com.sun.management.jmxremote.port");
      String var4 = var0.getProperty("com.sun.management.enableThreadContentionMonitoring");
      if (var4 != null) {
         java.lang.management.ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
      }

      try {
         if (var1 != null) {
            loadSnmpAgent(var1, var0);
         }

         if (var2 != null || var3 != null) {
            if (var3 != null) {
               jmxServer = ConnectorBootstrap.startRemoteConnectorServer(var3, var0);
               startDiscoveryService(var0);
            }

            startLocalManagementAgent();
         }
      } catch (AgentConfigurationError var6) {
         error(var6);
      } catch (Exception var7) {
         error(var7);
      }

   }

   private static void startDiscoveryService(Properties var0) throws IOException {
      String var1 = var0.getProperty("com.sun.management.jdp.port");
      String var2 = var0.getProperty("com.sun.management.jdp.address");
      String var3 = var0.getProperty("com.sun.management.jmxremote.autodiscovery");
      boolean var4 = false;
      if (var3 == null) {
         var4 = var1 != null;
      } else {
         try {
            var4 = Boolean.parseBoolean(var3);
         } catch (NumberFormatException var17) {
            throw new AgentConfigurationError("Couldn't parse autodiscovery argument");
         }
      }

      if (var4) {
         InetAddress var5;
         try {
            var5 = var2 == null ? InetAddress.getByName("224.0.23.178") : InetAddress.getByName(var2);
         } catch (UnknownHostException var16) {
            throw new AgentConfigurationError("Unable to broadcast to requested address", var16);
         }

         int var6 = 7095;
         if (var1 != null) {
            try {
               var6 = Integer.parseInt(var1);
            } catch (NumberFormatException var15) {
               throw new AgentConfigurationError("Couldn't parse JDP port argument");
            }
         }

         String var7 = var0.getProperty("com.sun.management.jmxremote.port");
         String var8 = var0.getProperty("com.sun.management.jmxremote.rmi.port");
         JMXServiceURL var9 = jmxServer.getAddress();
         String var10 = var9.getHost();
         String var11 = var8 != null ? String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", var10, var8, var10, var7) : String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", var10, var7);
         String var12 = var0.getProperty("com.sun.management.jdp.name");

         try {
            JdpController.startDiscoveryService(var5, var6, var12, var11);
         } catch (JdpException var14) {
            throw new AgentConfigurationError("Couldn't start JDP service", var14);
         }
      }

   }

   public static Properties loadManagementProperties() {
      Properties var0 = new Properties();
      String var1 = System.getProperty("com.sun.management.config.file");
      readConfiguration(var1, var0);
      Properties var2 = System.getProperties();
      synchronized(var2) {
         var0.putAll(var2);
         return var0;
      }
   }

   public static synchronized Properties getManagementProperties() {
      if (mgmtProps == null) {
         String var0 = System.getProperty("com.sun.management.config.file");
         String var1 = System.getProperty("com.sun.management.snmp.port");
         String var2 = System.getProperty("com.sun.management.jmxremote");
         String var3 = System.getProperty("com.sun.management.jmxremote.port");
         if (var0 == null && var1 == null && var2 == null && var3 == null) {
            return null;
         }

         mgmtProps = loadManagementProperties();
      }

      return mgmtProps;
   }

   private static void loadSnmpAgent(String var0, Properties var1) {
      try {
         Class var2 = Class.forName("sun.management.snmp.AdaptorBootstrap", true, (ClassLoader)null);
         Method var6 = var2.getMethod("initialize", String.class, Properties.class);
         var6.invoke((Object)null, var0, var1);
      } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException var4) {
         throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", var4);
      } catch (InvocationTargetException var5) {
         Throwable var3 = var5.getCause();
         if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else if (var3 instanceof Error) {
            throw (Error)var3;
         } else {
            throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", var3);
         }
      }
   }

   private static void readConfiguration(String var0, Properties var1) {
      if (var0 == null) {
         String var2 = System.getProperty("java.home");
         if (var2 == null) {
            throw new Error("Can't find java.home ??");
         }

         StringBuffer var3 = new StringBuffer(var2);
         var3.append(File.separator).append("lib");
         var3.append(File.separator).append("management");
         var3.append(File.separator).append("management.properties");
         var0 = var3.toString();
      }

      File var21 = new File(var0);
      if (!var21.exists()) {
         error("agent.err.configfile.notfound", var0);
      }

      FileInputStream var22 = null;

      try {
         var22 = new FileInputStream(var21);
         BufferedInputStream var4 = new BufferedInputStream(var22);
         var1.load((InputStream)var4);
      } catch (FileNotFoundException var17) {
         error("agent.err.configfile.failed", var17.getMessage());
      } catch (IOException var18) {
         error("agent.err.configfile.failed", var18.getMessage());
      } catch (SecurityException var19) {
         error("agent.err.configfile.access.denied", var0);
      } finally {
         if (var22 != null) {
            try {
               var22.close();
            } catch (IOException var16) {
               error("agent.err.configfile.closed.failed", var0);
            }
         }

      }

   }

   public static void startAgent() throws Exception {
      String var0 = System.getProperty("com.sun.management.agent.class");
      if (var0 == null) {
         Properties var10 = getManagementProperties();
         if (var10 != null) {
            startAgent(var10);
         }

      } else {
         String[] var1 = var0.split(":");
         if (var1.length < 1 || var1.length > 2) {
            error("agent.err.invalid.agentclass", "\"" + var0 + "\"");
         }

         String var2 = var1[0];
         String var3 = var1.length == 2 ? var1[1] : null;
         if (var2 == null || var2.length() == 0) {
            error("agent.err.invalid.agentclass", "\"" + var0 + "\"");
         }

         if (var2 != null) {
            try {
               Class var4 = ClassLoader.getSystemClassLoader().loadClass(var2);
               Method var11 = var4.getMethod("premain", String.class);
               var11.invoke((Object)null, var3);
            } catch (ClassNotFoundException var6) {
               error("agent.err.agentclass.notfound", "\"" + var2 + "\"");
            } catch (NoSuchMethodException var7) {
               error("agent.err.premain.notfound", "\"" + var2 + "\"");
            } catch (SecurityException var8) {
               error("agent.err.agentclass.access.denied");
            } catch (Exception var9) {
               String var5 = var9.getCause() == null ? var9.getMessage() : var9.getCause().getMessage();
               error("agent.err.agentclass.failed", var5);
            }
         }

      }
   }

   public static void error(String var0) {
      String var1 = getText(var0);
      System.err.print(getText("agent.err.error") + ": " + var1);
      throw new RuntimeException(var1);
   }

   public static void error(String var0, String var1) {
      String var2 = getText(var0);
      System.err.print(getText("agent.err.error") + ": " + var2);
      System.err.println(": " + var1);
      throw new RuntimeException(var2 + ": " + var1);
   }

   public static void error(Exception var0) {
      var0.printStackTrace();
      System.err.println(getText("agent.err.exception") + ": " + var0.toString());
      throw new RuntimeException(var0);
   }

   public static void error(AgentConfigurationError var0) {
      String var1 = getText(var0.getError());
      String[] var2 = var0.getParams();
      System.err.print(getText("agent.err.error") + ": " + var1);
      if (var2 != null && var2.length != 0) {
         StringBuffer var3 = new StringBuffer(var2[0]);

         for(int var4 = 1; var4 < var2.length; ++var4) {
            var3.append(" " + var2[var4]);
         }

         System.err.println(": " + var3);
      }

      var0.printStackTrace();
      throw new RuntimeException(var0);
   }

   public static void warning(String var0, String var1) {
      System.err.print(getText("agent.err.warning") + ": " + getText(var0));
      System.err.println(": " + var1);
   }

   private static void initResource() {
      try {
         messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
      } catch (MissingResourceException var1) {
         throw new Error("Fatal: Resource for management agent is missing");
      }
   }

   public static String getText(String var0) {
      if (messageRB == null) {
         initResource();
      }

      try {
         return messageRB.getString(var0);
      } catch (MissingResourceException var2) {
         return "Missing management agent resource bundle: key = \"" + var0 + "\"";
      }
   }

   public static String getText(String var0, String... var1) {
      if (messageRB == null) {
         initResource();
      }

      String var2 = messageRB.getString(var0);
      if (var2 == null) {
         var2 = "missing resource key: key = \"" + var0 + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
      }

      return MessageFormat.format(var2, (Object[])var1);
   }
}
