package com.sun.corba.se.spi.activation;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ActivatorImplBase extends ObjectImpl implements Activator, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         int var6;
         switch(var5) {
         case 0:
            try {
               var6 = ServerIdHelper.read(var2);
               Server var28 = ServerHelper.read(var2);
               this.active(var6, var28);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var24) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var24);
            }
            break;
         case 1:
            try {
               var6 = ServerIdHelper.read(var2);
               String var26 = ORBidHelper.read(var2);
               EndPointInfo[] var8 = EndpointInfoListHelper.read(var2);
               this.registerEndpoints(var6, var26, var8);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var21) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var21);
            } catch (NoSuchEndPoint var22) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var22);
            } catch (ORBAlreadyRegistered var23) {
               var4 = var3.createExceptionReply();
               ORBAlreadyRegisteredHelper.write(var4, var23);
            }
            break;
         case 2:
            Object var25 = null;
            int[] var27 = this.getActiveServers();
            var4 = var3.createReply();
            ServerIdsHelper.write(var4, var27);
            break;
         case 3:
            try {
               var6 = ServerIdHelper.read(var2);
               this.activate(var6);
               var4 = var3.createReply();
            } catch (ServerAlreadyActive var18) {
               var4 = var3.createExceptionReply();
               ServerAlreadyActiveHelper.write(var4, var18);
            } catch (ServerNotRegistered var19) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var19);
            } catch (ServerHeldDown var20) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var20);
            }
            break;
         case 4:
            try {
               var6 = ServerIdHelper.read(var2);
               this.shutdown(var6);
               var4 = var3.createReply();
            } catch (ServerNotActive var16) {
               var4 = var3.createExceptionReply();
               ServerNotActiveHelper.write(var4, var16);
            } catch (ServerNotRegistered var17) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var17);
            }
            break;
         case 5:
            try {
               var6 = ServerIdHelper.read(var2);
               this.install(var6);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var13) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var13);
            } catch (ServerHeldDown var14) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var14);
            } catch (ServerAlreadyInstalled var15) {
               var4 = var3.createExceptionReply();
               ServerAlreadyInstalledHelper.write(var4, var15);
            }
            break;
         case 6:
            try {
               var6 = ServerIdHelper.read(var2);
               String[] var7 = null;
               var7 = this.getORBNames(var6);
               var4 = var3.createReply();
               ORBidListHelper.write(var4, var7);
            } catch (ServerNotRegistered var12) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var12);
            }
            break;
         case 7:
            try {
               var6 = ServerIdHelper.read(var2);
               this.uninstall(var6);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var9) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var9);
            } catch (ServerHeldDown var10) {
               var4 = var3.createExceptionReply();
               ServerHeldDownHelper.write(var4, var10);
            } catch (ServerAlreadyUninstalled var11) {
               var4 = var3.createExceptionReply();
               ServerAlreadyUninstalledHelper.write(var4, var11);
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
      __ids = new String[]{"IDL:activation/Activator:1.0"};
   }
}
