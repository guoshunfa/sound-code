package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.omg.CORBA.SystemException;

public class ServerTableEntry {
   private static final int DE_ACTIVATED = 0;
   private static final int ACTIVATING = 1;
   private static final int ACTIVATED = 2;
   private static final int RUNNING = 3;
   private static final int HELD_DOWN = 4;
   private static final long waitTime = 2000L;
   private static final int ActivationRetryMax = 5;
   private int state;
   private int serverId;
   private HashMap orbAndPortInfo;
   private Server serverObj;
   private ServerDef serverDef;
   private Process process;
   private int activateRetryCount = 0;
   private String activationCmd;
   private ActivationSystemException wrapper;
   private static String javaHome = System.getProperty("java.home");
   private static String classPath = System.getProperty("java.class.path");
   private static String fileSep = System.getProperty("file.separator");
   private static String pathSep = System.getProperty("path.separator");
   private boolean debug = false;

   private String printState() {
      String var1 = "UNKNOWN";
      switch(this.state) {
      case 0:
         var1 = "DE_ACTIVATED";
         break;
      case 1:
         var1 = "ACTIVATING  ";
         break;
      case 2:
         var1 = "ACTIVATED   ";
         break;
      case 3:
         var1 = "RUNNING     ";
         break;
      case 4:
         var1 = "HELD_DOWN   ";
      }

      return var1;
   }

   public String toString() {
      return "ServerTableEntry[state=" + this.printState() + " serverId=" + this.serverId + " activateRetryCount=" + this.activateRetryCount + "]";
   }

   ServerTableEntry(ActivationSystemException var1, int var2, ServerDef var3, int var4, String var5, boolean var6, boolean var7) {
      this.wrapper = var1;
      this.serverId = var2;
      this.serverDef = var3;
      this.debug = var7;
      this.orbAndPortInfo = new HashMap(255);
      this.activateRetryCount = 0;
      this.state = 1;
      this.activationCmd = javaHome + fileSep + "bin" + fileSep + "java " + var3.serverVmArgs + " -Dioser=" + System.getProperty("ioser") + " -D" + "org.omg.CORBA.ORBInitialPort" + "=" + var4 + " -D" + "com.sun.CORBA.activation.DbDir" + "=" + var5 + " -D" + "com.sun.CORBA.POA.ORBActivated" + "=true -D" + "com.sun.CORBA.POA.ORBServerId" + "=" + var2 + " -D" + "com.sun.CORBA.POA.ORBServerName" + "=" + var3.serverName + " " + (var6 ? "-Dcom.sun.CORBA.activation.ORBServerVerify=true " : "") + "-classpath " + classPath + (var3.serverClassPath.equals("") ? "" : pathSep) + var3.serverClassPath + " com.sun.corba.se.impl.activation.ServerMain " + var3.serverArgs + (var7 ? " -debug" : "");
      if (var7) {
         System.out.println("ServerTableEntry constructed with activation command " + this.activationCmd);
      }

   }

   public int verify() {
      try {
         if (this.debug) {
            System.out.println("Server being verified w/" + this.activationCmd);
         }

         this.process = Runtime.getRuntime().exec(this.activationCmd);
         int var1 = this.process.waitFor();
         if (this.debug) {
            this.printDebug("verify", "returns " + ServerMain.printResult(var1));
         }

         return var1;
      } catch (Exception var2) {
         if (this.debug) {
            this.printDebug("verify", "returns unknown error because of exception " + var2);
         }

         return 4;
      }
   }

   private void printDebug(String var1, String var2) {
      System.out.println("ServerTableEntry: method  =" + var1);
      System.out.println("ServerTableEntry: server  =" + this.serverId);
      System.out.println("ServerTableEntry: state   =" + this.printState());
      System.out.println("ServerTableEntry: message =" + var2);
      System.out.println();
   }

   synchronized void activate() throws SystemException {
      this.state = 2;

      try {
         if (this.debug) {
            this.printDebug("activate", "activating server");
         }

         this.process = Runtime.getRuntime().exec(this.activationCmd);
      } catch (Exception var2) {
         this.deActivate();
         if (this.debug) {
            this.printDebug("activate", "throwing premature process exit");
         }

         throw this.wrapper.unableToStartProcess();
      }
   }

   synchronized void register(Server var1) {
      if (this.state == 2) {
         this.serverObj = var1;
         if (this.debug) {
            this.printDebug("register", "process registered back");
         }

      } else {
         if (this.debug) {
            this.printDebug("register", "throwing premature process exit");
         }

         throw this.wrapper.serverNotExpectedToRegister();
      }
   }

   synchronized void registerPorts(String var1, EndPointInfo[] var2) throws ORBAlreadyRegistered {
      if (this.orbAndPortInfo.containsKey(var1)) {
         throw new ORBAlreadyRegistered(var1);
      } else {
         int var3 = var2.length;
         EndPointInfo[] var4 = new EndPointInfo[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = new EndPointInfo(var2[var5].endpointType, var2[var5].port);
            if (this.debug) {
               System.out.println("registering type: " + var4[var5].endpointType + "  port  " + var4[var5].port);
            }
         }

         this.orbAndPortInfo.put(var1, var4);
         if (this.state == 2) {
            this.state = 3;
            this.notifyAll();
         }

         if (this.debug) {
            this.printDebug("registerPorts", "process registered Ports");
         }

      }
   }

   void install() {
      Server var1 = null;
      synchronized(this) {
         if (this.state != 3) {
            throw this.wrapper.serverNotRunning();
         }

         var1 = this.serverObj;
      }

      if (var1 != null) {
         var1.install();
      }

   }

   void uninstall() {
      Server var1 = null;
      Process var2 = null;
      synchronized(this) {
         var1 = this.serverObj;
         var2 = this.process;
         if (this.state != 3) {
            throw this.wrapper.serverNotRunning();
         }

         this.deActivate();
      }

      try {
         if (var1 != null) {
            var1.shutdown();
            var1.uninstall();
         }

         if (var2 != null) {
            var2.destroy();
         }
      } catch (Exception var5) {
      }

   }

   synchronized void holdDown() {
      this.state = 4;
      if (this.debug) {
         this.printDebug("holdDown", "server held down");
      }

      this.notifyAll();
   }

   synchronized void deActivate() {
      this.state = 0;
      if (this.debug) {
         this.printDebug("deActivate", "server deactivated");
      }

      this.notifyAll();
   }

   synchronized void checkProcessHealth() {
      if (this.state == 3) {
         try {
            int var1 = this.process.exitValue();
         } catch (IllegalThreadStateException var4) {
            return;
         }

         synchronized(this) {
            this.orbAndPortInfo.clear();
            this.deActivate();
         }
      }

   }

   synchronized boolean isValid() {
      if (this.state != 1 && this.state != 4) {
         try {
            int var1 = this.process.exitValue();
         } catch (IllegalThreadStateException var2) {
            return true;
         }

         if (this.state == 2) {
            if (this.activateRetryCount < 5) {
               if (this.debug) {
                  this.printDebug("isValid", "reactivating server");
               }

               ++this.activateRetryCount;
               this.activate();
               return true;
            } else {
               if (this.debug) {
                  this.printDebug("isValid", "holding server down");
               }

               this.holdDown();
               return true;
            }
         } else {
            this.deActivate();
            return false;
         }
      } else {
         if (this.debug) {
            this.printDebug("isValid", "returns true");
         }

         return true;
      }
   }

   synchronized ORBPortInfo[] lookup(String var1) throws ServerHeldDown {
      while(this.state == 1 || this.state == 2) {
         try {
            this.wait(2000L);
            if (this.isValid()) {
               continue;
            }
            break;
         } catch (Exception var9) {
         }
      }

      ORBPortInfo[] var2 = null;
      if (this.state == 3) {
         var2 = new ORBPortInfo[this.orbAndPortInfo.size()];
         Iterator var3 = this.orbAndPortInfo.keySet().iterator();

         try {
            for(int var4 = 0; var3.hasNext(); ++var4) {
               String var7 = (String)var3.next();
               EndPointInfo[] var8 = (EndPointInfo[])((EndPointInfo[])this.orbAndPortInfo.get(var7));
               int var6 = -1;

               for(int var5 = 0; var5 < var8.length; ++var5) {
                  if (this.debug) {
                     System.out.println("lookup num-ports " + var8.length + "   " + var8[var5].endpointType + "   " + var8[var5].port);
                  }

                  if (var8[var5].endpointType.equals(var1)) {
                     var6 = var8[var5].port;
                     break;
                  }
               }

               var2[var4] = new ORBPortInfo(var7, var6);
            }
         } catch (NoSuchElementException var10) {
         }

         return var2;
      } else {
         if (this.debug) {
            this.printDebug("lookup", "throwing server held down error");
         }

         throw new ServerHeldDown(this.serverId);
      }
   }

   synchronized EndPointInfo[] lookupForORB(String var1) throws ServerHeldDown, InvalidORBid {
      while(this.state == 1 || this.state == 2) {
         try {
            this.wait(2000L);
            if (this.isValid()) {
               continue;
            }
            break;
         } catch (Exception var6) {
         }
      }

      EndPointInfo[] var2 = null;
      if (this.state == 3) {
         try {
            EndPointInfo[] var3 = (EndPointInfo[])((EndPointInfo[])this.orbAndPortInfo.get(var1));
            var2 = new EndPointInfo[var3.length];

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (this.debug) {
                  System.out.println("lookup num-ports " + var3.length + "   " + var3[var4].endpointType + "   " + var3[var4].port);
               }

               var2[var4] = new EndPointInfo(var3[var4].endpointType, var3[var4].port);
            }

            return var2;
         } catch (NoSuchElementException var5) {
            throw new InvalidORBid();
         }
      } else {
         if (this.debug) {
            this.printDebug("lookup", "throwing server held down error");
         }

         throw new ServerHeldDown(this.serverId);
      }
   }

   synchronized String[] getORBList() {
      String[] var1 = new String[this.orbAndPortInfo.size()];
      Iterator var2 = this.orbAndPortInfo.keySet().iterator();

      String var4;
      try {
         for(int var3 = 0; var2.hasNext(); var1[var3++] = var4) {
            var4 = (String)var2.next();
         }
      } catch (NoSuchElementException var5) {
      }

      return var1;
   }

   int getServerId() {
      return this.serverId;
   }

   boolean isActive() {
      return this.state == 3 || this.state == 2;
   }

   synchronized void destroy() {
      Server var1 = null;
      Process var2 = null;
      synchronized(this) {
         var1 = this.serverObj;
         var2 = this.process;
         this.deActivate();
      }

      try {
         if (var1 != null) {
            var1.shutdown();
         }

         if (this.debug) {
            this.printDebug("destroy", "server shutdown successfully");
         }
      } catch (Exception var7) {
         if (this.debug) {
            this.printDebug("destroy", "server shutdown threw exception" + var7);
         }
      }

      try {
         if (var2 != null) {
            var2.destroy();
         }

         if (this.debug) {
            this.printDebug("destroy", "process destroyed successfully");
         }
      } catch (Exception var6) {
         if (this.debug) {
            this.printDebug("destroy", "process destroy threw exception" + var6);
         }
      }

   }
}
