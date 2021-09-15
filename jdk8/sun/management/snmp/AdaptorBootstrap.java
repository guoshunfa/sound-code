package sun.management.snmp;

import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.IPAcl.SnmpAcl;
import com.sun.jmx.snmp.daemon.CommunicationException;
import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import sun.management.Agent;
import sun.management.AgentConfigurationError;
import sun.management.FileSystem;
import sun.management.snmp.jvminstr.JVM_MANAGEMENT_MIB_IMPL;
import sun.management.snmp.jvminstr.NotificationTarget;
import sun.management.snmp.jvminstr.NotificationTargetImpl;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public final class AdaptorBootstrap {
   private static final MibLogger log = new MibLogger(AdaptorBootstrap.class);
   private SnmpAdaptorServer adaptor;
   private JVM_MANAGEMENT_MIB_IMPL jvmmib;

   private AdaptorBootstrap(SnmpAdaptorServer var1, JVM_MANAGEMENT_MIB_IMPL var2) {
      this.jvmmib = var2;
      this.adaptor = var1;
   }

   private static String getDefaultFileName(String var0) {
      String var1 = File.separator;
      return System.getProperty("java.home") + var1 + "lib" + var1 + "management" + var1 + var0;
   }

   private static List<NotificationTarget> getTargetList(InetAddressAcl var0, int var1) {
      ArrayList var2 = new ArrayList();
      if (var0 != null) {
         if (log.isDebugOn()) {
            log.debug("getTargetList", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.processing"));
         }

         Enumeration var3 = var0.getTrapDestinations();

         while(var3.hasMoreElements()) {
            InetAddress var4 = (InetAddress)var3.nextElement();

            NotificationTargetImpl var7;
            for(Enumeration var5 = var0.getTrapCommunities(var4); var5.hasMoreElements(); var2.add(var7)) {
               String var6 = (String)var5.nextElement();
               var7 = new NotificationTargetImpl(var4, var1, var6);
               if (log.isDebugOn()) {
                  log.debug("getTargetList", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.adding", var7.toString()));
               }
            }
         }
      }

      return var2;
   }

   public static synchronized AdaptorBootstrap initialize() {
      Properties var0 = Agent.loadManagementProperties();
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.getProperty("com.sun.management.snmp.port");
         return initialize(var1, var0);
      }
   }

   public static synchronized AdaptorBootstrap initialize(String var0, Properties var1) {
      if (var0.length() == 0) {
         var0 = "161";
      }

      int var2;
      try {
         var2 = Integer.parseInt(var0);
      } catch (NumberFormatException var14) {
         throw new AgentConfigurationError("agent.err.invalid.snmp.port", var14, new String[]{var0});
      }

      if (var2 < 0) {
         throw new AgentConfigurationError("agent.err.invalid.snmp.port", new String[]{var0});
      } else {
         String var3 = var1.getProperty("com.sun.management.snmp.trap", "162");

         int var4;
         try {
            var4 = Integer.parseInt(var3);
         } catch (NumberFormatException var13) {
            throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", var13, new String[]{var3});
         }

         if (var4 < 0) {
            throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", new String[]{var3});
         } else {
            String var5 = var1.getProperty("com.sun.management.snmp.interface", "localhost");
            String var6 = getDefaultFileName("snmp.acl");
            String var7 = var1.getProperty("com.sun.management.snmp.acl.file", var6);
            String var8 = var1.getProperty("com.sun.management.snmp.acl", "true");
            boolean var9 = Boolean.valueOf(var8);
            if (var9) {
               checkAclFile(var7);
            }

            AdaptorBootstrap var10 = null;

            try {
               var10 = getAdaptorBootstrap(var2, var4, var5, var9, var7);
               return var10;
            } catch (Exception var12) {
               throw new AgentConfigurationError("agent.err.exception", var12, new String[]{var12.getMessage()});
            }
         }
      }
   }

   private static AdaptorBootstrap getAdaptorBootstrap(int var0, int var1, String var2, boolean var3, String var4) {
      InetAddress var5;
      try {
         var5 = InetAddress.getByName(var2);
      } catch (UnknownHostException var15) {
         throw new AgentConfigurationError("agent.err.unknown.snmp.interface", var15, new String[]{var2});
      }

      if (log.isDebugOn()) {
         log.debug("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.starting\n\tcom.sun.management.snmp.port=" + var0 + "\n\t" + "com.sun.management.snmp.trap" + "=" + var1 + "\n\t" + "com.sun.management.snmp.interface" + "=" + var5 + (var3 ? "\n\tcom.sun.management.snmp.acl.file=" + var4 : "\n\tNo ACL") + ""));
      }

      SnmpAcl var6;
      try {
         var6 = var3 ? new SnmpAcl(System.getProperty("user.name"), var4) : null;
      } catch (UnknownHostException var14) {
         throw new AgentConfigurationError("agent.err.unknown.snmp.interface", var14, new String[]{var14.getMessage()});
      }

      SnmpAdaptorServer var7 = new SnmpAdaptorServer(var6, var0, var5);
      var7.setUserDataFactory(new JvmContextFactory());
      var7.setTrapPort(var1);
      JVM_MANAGEMENT_MIB_IMPL var8 = new JVM_MANAGEMENT_MIB_IMPL();

      try {
         var8.init();
      } catch (IllegalAccessException var13) {
         throw new AgentConfigurationError("agent.err.snmp.mib.init.failed", var13, new String[]{var13.getMessage()});
      }

      var8.addTargets(getTargetList(var6, var1));

      try {
         var7.start(Long.MAX_VALUE);
      } catch (Exception var16) {
         Object var10 = var16;
         if (var16 instanceof CommunicationException) {
            Throwable var11 = var16.getCause();
            if (var11 != null) {
               var10 = var11;
            }
         }

         throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", (Throwable)var10, new String[]{var5 + ":" + var0, "(" + ((Throwable)var10).getMessage() + ")"});
      }

      if (!var7.isActive()) {
         throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", new String[]{var5 + ":" + var0});
      } else {
         try {
            var7.addMib(var8);
            var8.setSnmpAdaptor(var7);
         } catch (RuntimeException var12) {
            (new AdaptorBootstrap(var7, var8)).terminate();
            throw var12;
         }

         log.debug("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize1"));
         log.config("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize2", var5.toString(), Integer.toString(var7.getPort())));
         return new AdaptorBootstrap(var7, var8);
      }
   }

   private static void checkAclFile(String var0) {
      if (var0 != null && var0.length() != 0) {
         File var1 = new File(var0);
         if (!var1.exists()) {
            throw new AgentConfigurationError("agent.err.acl.file.notfound", new String[]{var0});
         } else if (!var1.canRead()) {
            throw new AgentConfigurationError("agent.err.acl.file.not.readable", new String[]{var0});
         } else {
            FileSystem var2 = FileSystem.open();

            try {
               if (var2.supportsFileSecurity(var1) && !var2.isAccessUserOnly(var1)) {
                  throw new AgentConfigurationError("agent.err.acl.file.access.notrestricted", new String[]{var0});
               }
            } catch (IOException var4) {
               throw new AgentConfigurationError("agent.err.acl.file.read.failed", new String[]{var0});
            }
         }
      } else {
         throw new AgentConfigurationError("agent.err.acl.file.notset");
      }
   }

   public synchronized int getPort() {
      return this.adaptor != null ? this.adaptor.getPort() : 0;
   }

   public synchronized void terminate() {
      if (this.adaptor != null) {
         try {
            this.jvmmib.terminate();
         } catch (Exception var11) {
            log.debug("jmxremote.AdaptorBootstrap.getTargetList.terminate", var11.toString());
         } finally {
            this.jvmmib = null;
         }

         try {
            this.adaptor.stop();
         } finally {
            this.adaptor = null;
         }

      }
   }

   public interface PropertyNames {
      String PORT = "com.sun.management.snmp.port";
      String CONFIG_FILE_NAME = "com.sun.management.config.file";
      String TRAP_PORT = "com.sun.management.snmp.trap";
      String USE_ACL = "com.sun.management.snmp.acl";
      String ACL_FILE_NAME = "com.sun.management.snmp.acl.file";
      String BIND_ADDRESS = "com.sun.management.snmp.interface";
   }

   public interface DefaultValues {
      String PORT = "161";
      String CONFIG_FILE_NAME = "management.properties";
      String TRAP_PORT = "162";
      String USE_ACL = "true";
      String ACL_FILE_NAME = "snmp.acl";
      String BIND_ADDRESS = "localhost";
   }
}
