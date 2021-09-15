package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerManagerImplBase extends ObjectImpl implements ServerManager, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         ServerLocationPerORB var6;
         String var7;
         int var34;
         ServerLocationPerORB var38;
         switch(var5) {
         case 0:
            try {
               var34 = ServerIdHelper.read(var2);
               Server var41 = ServerHelper.read(var2);
               this.active(var34, var41);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var32) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var32);
            }
            break;
         case 1:
            try {
               var34 = ServerIdHelper.read(var2);
               var7 = ORBidHelper.read(var2);
               EndPointInfo[] var43 = EndpointInfoListHelper.read(var2);
               this.registerEndpoints(var34, var7, var43);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var29) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var29);
            } catch (NoSuchEndPoint var30) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var30);
            } catch (ORBAlreadyRegistered var31) {
               var4 = var3.createExceptionReply();
               ORBAlreadyRegisteredHelper.write(var4, var31);
            }
            break;
         case 2:
            var6 = null;
            int[] var40 = this.getActiveServers();
            var4 = var3.createReply();
            ServerIdsHelper.write(var4, var40);
            break;
         case 3:
            try {
               var34 = ServerIdHelper.read(var2);
               this.activate(var34);
               var4 = var3.createReply();
            } catch (ServerAlreadyActive var26) {
               var4 = var3.createExceptionReply();
               ServerAlreadyActiveHelper.write(var4, var26);
            } catch (ServerNotRegistered var27) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var27);
            } catch (ServerHeldDown var28) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var28);
            }
            break;
         case 4:
            try {
               var34 = ServerIdHelper.read(var2);
               this.shutdown(var34);
               var4 = var3.createReply();
            } catch (ServerNotActive var24) {
               var4 = var3.createExceptionReply();
               ServerNotActiveHelper.write(var4, var24);
            } catch (ServerNotRegistered var25) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var25);
            }
            break;
         case 5:
            try {
               var34 = ServerIdHelper.read(var2);
               this.install(var34);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var21) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var21);
            } catch (ServerHeldDown var22) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var22);
            } catch (ServerAlreadyInstalled var23) {
               var4 = var3.createExceptionReply();
               ServerAlreadyInstalledHelper.write(var4, var23);
            }
            break;
         case 6:
            try {
               var34 = ServerIdHelper.read(var2);
               var7 = null;
               String[] var39 = this.getORBNames(var34);
               var4 = var3.createReply();
               ORBidListHelper.write(var4, var39);
            } catch (ServerNotRegistered var20) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var20);
            }
            break;
         case 7:
            try {
               var34 = ServerIdHelper.read(var2);
               this.uninstall(var34);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var17) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var17);
            } catch (ServerHeldDown var18) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var18);
            } catch (ServerAlreadyUninstalled var19) {
               var4 = var3.createExceptionReply();
               ServerAlreadyUninstalledHelper.write(var4, var19);
            }
            break;
         case 8:
            try {
               var34 = ServerIdHelper.read(var2);
               var7 = var2.read_string();
               var38 = null;
               ServerLocation var42 = this.locateServer(var34, var7);
               var4 = var3.createReply();
               ServerLocationHelper.write(var4, var42);
            } catch (NoSuchEndPoint var14) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var14);
            } catch (ServerNotRegistered var15) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var15);
            } catch (ServerHeldDown var16) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var16);
            }
            break;
         case 9:
            try {
               var34 = ServerIdHelper.read(var2);
               var7 = ORBidHelper.read(var2);
               var38 = null;
               var38 = this.locateServerForORB(var34, var7);
               var4 = var3.createReply();
               ServerLocationPerORBHelper.write(var4, var38);
            } catch (InvalidORBid var11) {
               var4 = var3.createExceptionReply();
               InvalidORBidHelper.write(var4, var11);
            } catch (ServerNotRegistered var12) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var12);
            } catch (ServerHeldDown var13) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var13);
            }
            break;
         case 10:
            try {
               String var33 = var2.read_string();
               boolean var35 = false;
               int var36 = this.getEndpoint(var33);
               var4 = var3.createReply();
               var4.write_long(var36);
            } catch (NoSuchEndPoint var10) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var10);
            }
            break;
         case 11:
            try {
               var6 = ServerLocationPerORBHelper.read(var2);
               var7 = var2.read_string();
               boolean var8 = false;
               int var37 = this.getServerPortForType(var6, var7);
               var4 = var3.createReply();
               var4.write_long(var37);
            } catch (NoSuchEndPoint var9) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var9);
            }
            break;
         default:
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
         }

         return var4;
      }
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   static {
      _methods.put("active", new Integer(0));
      _methods.put("registerEndpoints", new Integer(1));
      _methods.put("getActiveServers", new Integer(2));
      _methods.put("activate", new Integer(3));
      _methods.put("shutdown", new Integer(4));
      _methods.put("install", new Integer(5));
      _methods.put("getORBNames", new Integer(6));
      _methods.put("uninstall", new Integer(7));
      _methods.put("locateServer", new Integer(8));
      _methods.put("locateServerForORB", new Integer(9));
      _methods.put("getEndpoint", new Integer(10));
      _methods.put("getServerPortForType", new Integer(11));
      __ids = new String[]{"IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0"};
   }
}
