package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation._ServerManagerImplBase;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ServerManagerImpl extends _ServerManagerImplBase implements BadServerIdHandler {
   HashMap serverTable;
   Repository repository;
   CorbaTransportManager transportManager;
   int initialPort;
   ORB orb;
   ActivationSystemException wrapper;
   String dbDirName;
   boolean debug = false;
   private int serverStartupDelay;

   ServerManagerImpl(ORB var1, CorbaTransportManager var2, Repository var3, String var4, boolean var5) {
      this.orb = var1;
      this.wrapper = ActivationSystemException.get(var1, "orbd.activator");
      this.transportManager = var2;
      this.repository = var3;
      this.dbDirName = var4;
      this.debug = var5;
      LegacyServerSocketEndPointInfo var6 = var1.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
      this.initialPort = ((SocketOrChannelAcceptor)var6).getServerSocket().getLocalPort();
      this.serverTable = new HashMap(256);
      this.serverStartupDelay = 1000;
      String var7 = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
      if (var7 != null) {
         try {
            this.serverStartupDelay = Integer.parseInt(var7);
         } catch (Exception var9) {
         }
      }

      Class var8 = var1.getORBData().getBadServerIdHandler();
      if (var8 == null) {
         var1.setBadServerIdHandler(this);
      } else {
         var1.initBadServerIdHandler();
      }

      var1.connect(this);
      ProcessMonitorThread.start(this.serverTable);
   }

   public void activate(int var1) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
      Integer var4 = new Integer(var1);
      ServerTableEntry var3;
      synchronized(this.serverTable) {
         var3 = (ServerTableEntry)this.serverTable.get(var4);
      }

      if (var3 != null && var3.isActive()) {
         if (this.debug) {
            System.out.println("ServerManagerImpl: activate for server Id " + var1 + " failed because server is already active. entry = " + var3);
         }

         throw new ServerAlreadyActive(var1);
      } else {
         try {
            var3 = this.getEntry(var1);
            if (this.debug) {
               System.out.println("ServerManagerImpl: locateServer called with  serverId=" + var1 + " endpointType=" + "IIOP_CLEAR_TEXT" + " block=false");
            }

            ServerLocation var2 = this.locateServer(var3, "IIOP_CLEAR_TEXT", false);
            if (this.debug) {
               System.out.println("ServerManagerImpl: activate for server Id " + var1 + " found location " + var2.hostname + " and activated it");
            }
         } catch (NoSuchEndPoint var8) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: activate for server Id  threw NoSuchEndpoint exception, which was ignored");
            }
         }

      }
   }

   public void active(int var1, Server var2) throws ServerNotRegistered {
      Integer var4 = new Integer(var1);
      synchronized(this.serverTable) {
         ServerTableEntry var3 = (ServerTableEntry)this.serverTable.get(var4);
         if (var3 == null) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: active for server Id " + var1 + " called, but no such server is registered.");
            }

            throw this.wrapper.serverNotExpectedToRegister();
         } else {
            if (this.debug) {
               System.out.println("ServerManagerImpl: active for server Id " + var1 + " called.  This server is now active.");
            }

            var3.register(var2);
         }
      }
   }

   public void registerEndpoints(int var1, String var2, EndPointInfo[] var3) throws NoSuchEndPoint, ServerNotRegistered, ORBAlreadyRegistered {
      Integer var5 = new Integer(var1);
      synchronized(this.serverTable) {
         ServerTableEntry var4 = (ServerTableEntry)this.serverTable.get(var5);
         if (var4 == null) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: registerEndpoint for server Id " + var1 + " called, but no such server is registered.");
            }

            throw this.wrapper.serverNotExpectedToRegister();
         } else {
            if (this.debug) {
               System.out.println("ServerManagerImpl: registerEndpoints for server Id " + var1 + " called.  This server is now active.");
            }

            var4.registerPorts(var2, var3);
         }
      }
   }

   public int[] getActiveServers() {
      Object var2 = null;
      int[] var10;
      synchronized(this.serverTable) {
         ArrayList var4 = new ArrayList(0);
         Iterator var5 = this.serverTable.keySet().iterator();

         ServerTableEntry var1;
         try {
            while(var5.hasNext()) {
               Integer var6 = (Integer)var5.next();
               var1 = (ServerTableEntry)this.serverTable.get(var6);
               if (var1.isValid() && var1.isActive()) {
                  var4.add(var1);
               }
            }
         } catch (NoSuchElementException var8) {
         }

         var10 = new int[var4.size()];
         int var12 = 0;

         while(true) {
            if (var12 >= var4.size()) {
               break;
            }

            var1 = (ServerTableEntry)var4.get(var12);
            var10[var12] = var1.getServerId();
            ++var12;
         }
      }

      if (this.debug) {
         StringBuffer var3 = new StringBuffer();

         for(int var11 = 0; var11 < var10.length; ++var11) {
            var3.append(' ');
            var3.append(var10[var11]);
         }

         System.out.println("ServerManagerImpl: getActiveServers returns" + var3.toString());
      }

      return var10;
   }

   public void shutdown(int var1) throws ServerNotActive {
      Integer var3 = new Integer(var1);
      synchronized(this.serverTable) {
         ServerTableEntry var2 = (ServerTableEntry)this.serverTable.remove(var3);
         if (var2 == null) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: shutdown for server Id " + var1 + " throws ServerNotActive.");
            }

            throw new ServerNotActive(var1);
         } else {
            try {
               var2.destroy();
               if (this.debug) {
                  System.out.println("ServerManagerImpl: shutdown for server Id " + var1 + " completed.");
               }
            } catch (Exception var7) {
               if (this.debug) {
                  System.out.println("ServerManagerImpl: shutdown for server Id " + var1 + " threw exception " + var7);
               }
            }

         }
      }
   }

   private ServerTableEntry getEntry(int var1) throws ServerNotRegistered {
      Integer var2 = new Integer(var1);
      ServerTableEntry var3 = null;
      synchronized(this.serverTable) {
         var3 = (ServerTableEntry)this.serverTable.get(var2);
         if (this.debug) {
            if (var3 == null) {
               System.out.println("ServerManagerImpl: getEntry: no active server found.");
            } else {
               System.out.println("ServerManagerImpl: getEntry:  active server found " + var3 + ".");
            }
         }

         if (var3 != null && !var3.isValid()) {
            this.serverTable.remove(var2);
            var3 = null;
         }

         if (var3 == null) {
            ServerDef var5 = this.repository.getServer(var1);
            var3 = new ServerTableEntry(this.wrapper, var1, var5, this.initialPort, this.dbDirName, false, this.debug);
            this.serverTable.put(var2, var3);
            var3.activate();
         }

         return var3;
      }
   }

   private ServerLocation locateServer(ServerTableEntry var1, String var2, boolean var3) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
      ServerLocation var4 = new ServerLocation();
      if (var3) {
         ORBPortInfo[] var5;
         try {
            var5 = var1.lookup(var2);
         } catch (Exception var9) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: locateServer: server held down");
            }

            throw new ServerHeldDown(var1.getServerId());
         }

         String var6 = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
         var4.hostname = var6;
         int var7;
         if (var5 != null) {
            var7 = var5.length;
         } else {
            var7 = 0;
         }

         var4.ports = new ORBPortInfo[var7];

         for(int var8 = 0; var8 < var7; ++var8) {
            var4.ports[var8] = new ORBPortInfo(var5[var8].orbId, var5[var8].port);
            if (this.debug) {
               System.out.println("ServerManagerImpl: locateServer: server located at location " + var4.hostname + " ORBid  " + var5[var8].orbId + " Port " + var5[var8].port);
            }
         }
      }

      return var4;
   }

   private ServerLocationPerORB locateServerForORB(ServerTableEntry var1, String var2, boolean var3) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
      ServerLocationPerORB var4 = new ServerLocationPerORB();
      if (var3) {
         EndPointInfo[] var5;
         try {
            var5 = var1.lookupForORB(var2);
         } catch (InvalidORBid var9) {
            throw var9;
         } catch (Exception var10) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: locateServerForORB: server held down");
            }

            throw new ServerHeldDown(var1.getServerId());
         }

         String var6 = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
         var4.hostname = var6;
         int var7;
         if (var5 != null) {
            var7 = var5.length;
         } else {
            var7 = 0;
         }

         var4.ports = new EndPointInfo[var7];

         for(int var8 = 0; var8 < var7; ++var8) {
            var4.ports[var8] = new EndPointInfo(var5[var8].endpointType, var5[var8].port);
            if (this.debug) {
               System.out.println("ServerManagerImpl: locateServer: server located at location " + var4.hostname + " endpointType  " + var5[var8].endpointType + " Port " + var5[var8].port);
            }
         }
      }

      return var4;
   }

   public String[] getORBNames(int var1) throws ServerNotRegistered {
      try {
         ServerTableEntry var2 = this.getEntry(var1);
         return var2.getORBList();
      } catch (Exception var3) {
         throw new ServerNotRegistered(var1);
      }
   }

   private ServerTableEntry getRunningEntry(int var1) throws ServerNotRegistered {
      ServerTableEntry var2 = this.getEntry(var1);

      try {
         ORBPortInfo[] var3 = var2.lookup("IIOP_CLEAR_TEXT");
         return var2;
      } catch (Exception var4) {
         return null;
      }
   }

   public void install(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled {
      ServerTableEntry var2 = this.getRunningEntry(var1);
      if (var2 != null) {
         this.repository.install(var1);
         var2.install();
      }

   }

   public void uninstall(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled {
      ServerTableEntry var2 = (ServerTableEntry)this.serverTable.get(new Integer(var1));
      if (var2 != null) {
         var2 = (ServerTableEntry)this.serverTable.remove(new Integer(var1));
         if (var2 == null) {
            if (this.debug) {
               System.out.println("ServerManagerImpl: shutdown for server Id " + var1 + " throws ServerNotActive.");
            }

            throw new ServerHeldDown(var1);
         }

         var2.uninstall();
      }

   }

   public ServerLocation locateServer(int var1, String var2) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
      ServerTableEntry var3 = this.getEntry(var1);
      if (this.debug) {
         System.out.println("ServerManagerImpl: locateServer called with  serverId=" + var1 + " endpointType=" + var2 + " block=true");
      }

      return this.locateServer(var3, var2, true);
   }

   public ServerLocationPerORB locateServerForORB(int var1, String var2) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
      ServerTableEntry var3 = this.getEntry(var1);
      if (this.debug) {
         System.out.println("ServerManagerImpl: locateServerForORB called with  serverId=" + var1 + " orbId=" + var2 + " block=true");
      }

      return this.locateServerForORB(var3, var2, true);
   }

   public void handle(ObjectKey var1) {
      IOR var2 = null;
      ObjectKeyTemplate var4 = var1.getTemplate();
      int var5 = var4.getServerId();
      String var6 = var4.getORBId();

      try {
         ServerTableEntry var7 = this.getEntry(var5);
         ServerLocationPerORB var3 = this.locateServerForORB(var7, var6, true);
         if (this.debug) {
            System.out.println("ServerManagerImpl: handle called for server id" + var5 + "  orbid  " + var6);
         }

         int var8 = 0;
         EndPointInfo[] var9 = var3.ports;

         for(int var10 = 0; var10 < var9.length; ++var10) {
            if (var9[var10].endpointType.equals("IIOP_CLEAR_TEXT")) {
               var8 = var9[var10].port;
               break;
            }
         }

         IIOPAddress var15 = IIOPFactories.makeIIOPAddress(this.orb, var3.hostname, var8);
         IIOPProfileTemplate var11 = IIOPFactories.makeIIOPProfileTemplate(this.orb, GIOPVersion.V1_2, var15);
         if (GIOPVersion.V1_2.supportsIORIIOPProfileComponents()) {
            var11.add(IIOPFactories.makeCodeSetsComponent(this.orb));
            var11.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
         }

         IORTemplate var12 = IORFactories.makeIORTemplate(var4);
         var12.add(var11);
         var2 = var12.makeIOR(this.orb, "IDL:org/omg/CORBA/Object:1.0", var1.getId());
      } catch (Exception var14) {
         throw this.wrapper.errorInBadServerIdHandler((Throwable)var14);
      }

      if (this.debug) {
         System.out.println("ServerManagerImpl: handle throws ForwardException");
      }

      try {
         Thread.sleep((long)this.serverStartupDelay);
      } catch (Exception var13) {
         System.out.println("Exception = " + var13);
         var13.printStackTrace();
      }

      throw new ForwardException(this.orb, var2);
   }

   public int getEndpoint(String var1) throws NoSuchEndPoint {
      return this.orb.getLegacyServerSocketManager().legacyGetTransientServerPort(var1);
   }

   public int getServerPortForType(ServerLocationPerORB var1, String var2) throws NoSuchEndPoint {
      EndPointInfo[] var3 = var1.ports;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].endpointType.equals(var2)) {
            return var3[var4].port;
         }
      }

      throw new NoSuchEndPoint();
   }
}
