package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation._RepositoryImplBase;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class RepositoryImpl extends _RepositoryImplBase implements Serializable {
   private static final long serialVersionUID = 8458417785209341858L;
   private transient boolean debug = false;
   static final int illegalServerId = -1;
   private transient RepositoryImpl.RepositoryDB db = null;
   transient ORB orb = null;
   transient ActivationSystemException wrapper;

   RepositoryImpl(ORB var1, File var2, boolean var3) {
      this.debug = var3;
      this.orb = var1;
      this.wrapper = ActivationSystemException.get(var1, "orbd.repository");
      File var4 = new File(var2, "servers.db");
      if (!var4.exists()) {
         this.db = new RepositoryImpl.RepositoryDB(var4);
         this.db.flush();
      } else {
         try {
            FileInputStream var5 = new FileInputStream(var4);
            ObjectInputStream var6 = new ObjectInputStream(var5);
            this.db = (RepositoryImpl.RepositoryDB)var6.readObject();
            var6.close();
         } catch (Exception var7) {
            throw this.wrapper.cannotReadRepositoryDb((Throwable)var7);
         }
      }

      var1.connect(this);
   }

   private String printServerDef(ServerDef var1) {
      return "ServerDef[applicationName=" + var1.applicationName + " serverName=" + var1.serverName + " serverClassPath=" + var1.serverClassPath + " serverArgs=" + var1.serverArgs + " serverVmArgs=" + var1.serverVmArgs + "]";
   }

   public int registerServer(ServerDef var1, int var2) throws ServerAlreadyRegistered {
      RepositoryImpl.DBServerDef var4 = null;
      synchronized(this.db) {
         Enumeration var6 = this.db.serverTable.elements();

         do {
            if (!var6.hasMoreElements()) {
               int var3;
               if (var2 == -1) {
                  var3 = this.db.incrementServerIdCounter();
               } else {
                  var3 = var2;
               }

               var4 = new RepositoryImpl.DBServerDef(var1, var3);
               this.db.serverTable.put(new Integer(var3), var4);
               this.db.flush();
               if (this.debug) {
                  if (var2 == -1) {
                     System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(var1) + " with new serverId " + var3);
                  } else {
                     System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(var1) + " with assigned serverId " + var3);
                  }
               }

               return var3;
            }

            var4 = (RepositoryImpl.DBServerDef)var6.nextElement();
         } while(!var1.applicationName.equals(var4.applicationName));

         if (this.debug) {
            System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(var1) + " with " + (var2 == -1 ? "a new server Id" : "server Id " + var2) + " FAILED because it is already registered.");
         }

         throw new ServerAlreadyRegistered(var4.id);
      }
   }

   public int registerServer(ServerDef var1) throws ServerAlreadyRegistered, BadServerDefinition {
      LegacyServerSocketEndPointInfo var2 = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
      int var3 = ((SocketOrChannelAcceptor)var2).getServerSocket().getLocalPort();
      ServerTableEntry var4 = new ServerTableEntry(this.wrapper, -1, var1, var3, "", true, this.debug);
      switch(var4.verify()) {
      case 0:
         return this.registerServer(var1, -1);
      case 1:
         throw new BadServerDefinition("main class not found.");
      case 2:
         throw new BadServerDefinition("no main method found.");
      case 3:
         throw new BadServerDefinition("server application error.");
      default:
         throw new BadServerDefinition("unknown Exception.");
      }
   }

   public void unregisterServer(int var1) throws ServerNotRegistered {
      RepositoryImpl.DBServerDef var2 = null;
      Integer var3 = new Integer(var1);
      synchronized(this.db) {
         var2 = (RepositoryImpl.DBServerDef)this.db.serverTable.get(var3);
         if (var2 == null) {
            if (this.debug) {
               System.out.println("RepositoryImpl: unregisterServer for serverId " + var1 + " called: server not registered");
            }

            throw new ServerNotRegistered();
         }

         this.db.serverTable.remove(var3);
         this.db.flush();
      }

      if (this.debug) {
         System.out.println("RepositoryImpl: unregisterServer for serverId " + var1 + " called");
      }

   }

   private RepositoryImpl.DBServerDef getDBServerDef(int var1) throws ServerNotRegistered {
      Integer var2 = new Integer(var1);
      RepositoryImpl.DBServerDef var3 = (RepositoryImpl.DBServerDef)this.db.serverTable.get(var2);
      if (var3 == null) {
         throw new ServerNotRegistered(var1);
      } else {
         return var3;
      }
   }

   public ServerDef getServer(int var1) throws ServerNotRegistered {
      RepositoryImpl.DBServerDef var2 = this.getDBServerDef(var1);
      ServerDef var3 = new ServerDef(var2.applicationName, var2.name, var2.classPath, var2.args, var2.vmArgs);
      if (this.debug) {
         System.out.println("RepositoryImpl: getServer for serverId " + var1 + " returns " + this.printServerDef(var3));
      }

      return var3;
   }

   public boolean isInstalled(int var1) throws ServerNotRegistered {
      RepositoryImpl.DBServerDef var2 = this.getDBServerDef(var1);
      return var2.isInstalled;
   }

   public void install(int var1) throws ServerNotRegistered, ServerAlreadyInstalled {
      RepositoryImpl.DBServerDef var2 = this.getDBServerDef(var1);
      if (var2.isInstalled) {
         throw new ServerAlreadyInstalled(var1);
      } else {
         var2.isInstalled = true;
         this.db.flush();
      }
   }

   public void uninstall(int var1) throws ServerNotRegistered, ServerAlreadyUninstalled {
      RepositoryImpl.DBServerDef var2 = this.getDBServerDef(var1);
      if (!var2.isInstalled) {
         throw new ServerAlreadyUninstalled(var1);
      } else {
         var2.isInstalled = false;
         this.db.flush();
      }
   }

   public int[] listRegisteredServers() {
      synchronized(this.db) {
         int var2 = 0;
         int[] var3 = new int[this.db.serverTable.size()];

         RepositoryImpl.DBServerDef var5;
         for(Enumeration var4 = this.db.serverTable.elements(); var4.hasMoreElements(); var3[var2++] = var5.id) {
            var5 = (RepositoryImpl.DBServerDef)var4.nextElement();
         }

         if (this.debug) {
            StringBuffer var9 = new StringBuffer();

            for(int var6 = 0; var6 < var3.length; ++var6) {
               var9.append(' ');
               var9.append(var3[var6]);
            }

            System.out.println("RepositoryImpl: listRegisteredServers returns" + var9.toString());
         }

         return var3;
      }
   }

   public int getServerID(String var1) throws ServerNotRegistered {
      synchronized(this.db) {
         int var3 = -1;
         Enumeration var4 = this.db.serverTable.keys();

         while(var4.hasMoreElements()) {
            Integer var5 = (Integer)var4.nextElement();
            RepositoryImpl.DBServerDef var6 = (RepositoryImpl.DBServerDef)this.db.serverTable.get(var5);
            if (var6.applicationName.equals(var1)) {
               var3 = var5;
               break;
            }
         }

         if (this.debug) {
            System.out.println("RepositoryImpl: getServerID for " + var1 + " is " + var3);
         }

         if (var3 == -1) {
            throw new ServerNotRegistered();
         } else {
            return var3;
         }
      }
   }

   public String[] getApplicationNames() {
      synchronized(this.db) {
         Vector var2 = new Vector();
         Enumeration var3 = this.db.serverTable.keys();

         while(var3.hasMoreElements()) {
            Integer var4 = (Integer)var3.nextElement();
            RepositoryImpl.DBServerDef var5 = (RepositoryImpl.DBServerDef)this.db.serverTable.get(var4);
            if (!var5.applicationName.equals("")) {
               var2.addElement(var5.applicationName);
            }
         }

         String[] var8 = new String[var2.size()];

         for(int var9 = 0; var9 < var2.size(); ++var9) {
            var8[var9] = (String)var2.elementAt(var9);
         }

         if (this.debug) {
            StringBuffer var10 = new StringBuffer();

            for(int var11 = 0; var11 < var8.length; ++var11) {
               var10.append(' ');
               var10.append(var8[var11]);
            }

            System.out.println("RepositoryImpl: getApplicationNames returns " + var10.toString());
         }

         return var8;
      }
   }

   public static void main(String[] var0) {
      boolean var1 = false;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].equals("-debug")) {
            var1 = true;
         }
      }

      try {
         Properties var7 = new Properties();
         var7.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
         ORB var3 = (ORB)ORB.init(var0, var7);
         String var4 = System.getProperty("com.sun.CORBA.activation.db", "db");
         new RepositoryImpl(var3, new File(var4), var1);
         var3.run();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   class DBServerDef implements Serializable {
      String applicationName;
      String name;
      String classPath;
      String args;
      String vmArgs;
      boolean isInstalled;
      int id;

      public String toString() {
         return "DBServerDef(applicationName=" + this.applicationName + ", name=" + this.name + ", classPath=" + this.classPath + ", args=" + this.args + ", vmArgs=" + this.vmArgs + ", id=" + this.id + ", isInstalled=" + this.isInstalled + ")";
      }

      DBServerDef(ServerDef var2, int var3) {
         this.applicationName = var2.applicationName;
         this.name = var2.serverName;
         this.classPath = var2.serverClassPath;
         this.args = var2.serverArgs;
         this.vmArgs = var2.serverVmArgs;
         this.id = var3;
         this.isInstalled = false;
      }
   }

   class RepositoryDB implements Serializable {
      File db;
      Hashtable serverTable;
      Integer serverIdCounter;

      RepositoryDB(File var2) {
         this.db = var2;
         this.serverTable = new Hashtable(255);
         this.serverIdCounter = new Integer(256);
      }

      int incrementServerIdCounter() {
         int var1 = this.serverIdCounter;
         ++var1;
         this.serverIdCounter = new Integer(var1);
         return var1;
      }

      void flush() {
         try {
            this.db.delete();
            FileOutputStream var1 = new FileOutputStream(this.db);
            ObjectOutputStream var2 = new ObjectOutputStream(var1);
            var2.writeObject(this);
            var2.flush();
            var2.close();
         } catch (Exception var3) {
            throw RepositoryImpl.this.wrapper.cannotWriteRepositoryDb((Throwable)var3);
         }
      }
   }
}
