package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.util.Properties;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INTERNAL;

public class ORBD {
   private int initSvcPort;
   protected File dbDir;
   private String dbDirName;
   protected Locator locator;
   protected Activator activator;
   protected RepositoryImpl repository;
   private static String[][] orbServers = new String[][]{{""}};

   protected void initializeBootNaming(ORB var1) {
      this.initSvcPort = var1.getORBData().getORBInitialPort();
      Object var2;
      if (var1.getORBData().getLegacySocketFactory() == null) {
         var2 = new SocketOrChannelAcceptorImpl(var1, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
      } else {
         var2 = new SocketFactoryAcceptorImpl(var1, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
      }

      var1.getCorbaTransportManager().registerAcceptor((Acceptor)var2);
   }

   protected ORB createORB(String[] var1) {
      Properties var2 = System.getProperties();
      var2.put("com.sun.CORBA.POA.ORBServerId", "1000");
      var2.put("com.sun.CORBA.POA.ORBPersistentServerPort", var2.getProperty("com.sun.CORBA.activation.Port", Integer.toString(1049)));
      var2.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      return (ORB)ORB.init(var1, var2);
   }

   private void run(String[] var1) {
      try {
         this.processArgs(var1);
         ORB var2 = this.createORB(var1);
         if (var2.orbdDebugFlag) {
            System.out.println("ORBD begins initialization.");
         }

         boolean var3 = this.createSystemDirs("orb.db");
         this.startActivationObjects(var2);
         if (var3) {
            this.installOrbServers(this.getRepository(), this.getActivator());
         }

         if (var2.orbdDebugFlag) {
            System.out.println("ORBD is ready.");
            System.out.println("ORBD serverid: " + System.getProperty("com.sun.CORBA.POA.ORBServerId"));
            System.out.println("activation dbdir: " + System.getProperty("com.sun.CORBA.activation.DbDir"));
            System.out.println("activation port: " + System.getProperty("com.sun.CORBA.activation.Port"));
            String var4 = System.getProperty("com.sun.CORBA.activation.ServerPollingTime");
            if (var4 == null) {
               var4 = Integer.toString(1000);
            }

            System.out.println("activation Server Polling Time: " + var4 + " milli-seconds ");
            String var5 = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
            if (var5 == null) {
               var5 = Integer.toString(1000);
            }

            System.out.println("activation Server Startup Delay: " + var5 + " milli-seconds ");
         }

         NameServiceStartThread var9 = new NameServiceStartThread(var2, this.dbDir);
         var9.start();
         var2.run();
      } catch (COMM_FAILURE var6) {
         System.out.println(CorbaResourceUtil.getText("orbd.commfailure"));
         System.out.println((Object)var6);
         var6.printStackTrace();
      } catch (INTERNAL var7) {
         System.out.println(CorbaResourceUtil.getText("orbd.internalexception"));
         System.out.println((Object)var7);
         var7.printStackTrace();
      } catch (Exception var8) {
         System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
         System.out.println((Object)var8);
         var8.printStackTrace();
      }

   }

   private void processArgs(String[] var1) {
      Properties var2 = System.getProperties();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3].equals("-port")) {
            if (var3 + 1 < var1.length) {
               ++var3;
               var2.put("com.sun.CORBA.activation.Port", var1[var3]);
            } else {
               System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            }
         } else if (var1[var3].equals("-defaultdb")) {
            if (var3 + 1 < var1.length) {
               ++var3;
               var2.put("com.sun.CORBA.activation.DbDir", var1[var3]);
            } else {
               System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            }
         } else if (var1[var3].equals("-serverid")) {
            if (var3 + 1 < var1.length) {
               ++var3;
               var2.put("com.sun.CORBA.POA.ORBServerId", var1[var3]);
            } else {
               System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            }
         } else if (var1[var3].equals("-serverPollingTime")) {
            if (var3 + 1 < var1.length) {
               ++var3;
               var2.put("com.sun.CORBA.activation.ServerPollingTime", var1[var3]);
            } else {
               System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            }
         } else if (var1[var3].equals("-serverStartupDelay")) {
            if (var3 + 1 < var1.length) {
               ++var3;
               var2.put("com.sun.CORBA.activation.ServerStartupDelay", var1[var3]);
            } else {
               System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            }
         }
      }

   }

   protected boolean createSystemDirs(String var1) {
      boolean var2 = false;
      Properties var3 = System.getProperties();
      String var4 = var3.getProperty("file.separator");
      this.dbDir = new File(var3.getProperty("com.sun.CORBA.activation.DbDir", var3.getProperty("user.dir") + var4 + var1));
      this.dbDirName = this.dbDir.getAbsolutePath();
      var3.put("com.sun.CORBA.activation.DbDir", this.dbDirName);
      if (!this.dbDir.exists()) {
         this.dbDir.mkdir();
         var2 = true;
      }

      File var5 = new File(this.dbDir, "logs");
      if (!var5.exists()) {
         var5.mkdir();
      }

      return var2;
   }

   protected File getDbDir() {
      return this.dbDir;
   }

   protected String getDbDirName() {
      return this.dbDirName;
   }

   protected void startActivationObjects(ORB var1) throws Exception {
      this.initializeBootNaming(var1);
      this.repository = new RepositoryImpl(var1, this.dbDir, var1.orbdDebugFlag);
      var1.register_initial_reference("ServerRepository", this.repository);
      ServerManagerImpl var2 = new ServerManagerImpl(var1, var1.getCorbaTransportManager(), this.repository, this.getDbDirName(), var1.orbdDebugFlag);
      this.locator = LocatorHelper.narrow(var2);
      var1.register_initial_reference("ServerLocator", this.locator);
      this.activator = ActivatorHelper.narrow(var2);
      var1.register_initial_reference("ServerActivator", this.activator);
      new TransientNameService(var1, "TNameService");
   }

   protected Locator getLocator() {
      return this.locator;
   }

   protected Activator getActivator() {
      return this.activator;
   }

   protected RepositoryImpl getRepository() {
      return this.repository;
   }

   protected void installOrbServers(RepositoryImpl var1, Activator var2) {
      for(int var6 = 0; var6 < orbServers.length; ++var6) {
         try {
            String[] var4 = orbServers[var6];
            ServerDef var5 = new ServerDef(var4[1], var4[2], var4[3], var4[4], var4[5]);
            int var3 = Integer.valueOf(orbServers[var6][0]);
            var1.registerServer(var5, var3);
            var2.activate(var3);
         } catch (Exception var8) {
         }
      }

   }

   public static void main(String[] var0) {
      ORBD var1 = new ORBD();
      var1.run(var0);
   }
}
