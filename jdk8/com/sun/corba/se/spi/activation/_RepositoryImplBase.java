package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _RepositoryImplBase extends ObjectImpl implements Repository, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         String var6;
         boolean var7;
         int var20;
         int var21;
         switch(var5) {
         case 0:
            try {
               ServerDef var23 = ServerDefHelper.read(var2);
               var7 = false;
               var20 = this.registerServer(var23);
               var4 = var3.createReply();
               var4.write_long(var20);
            } catch (ServerAlreadyRegistered var16) {
               var4 = var3.createExceptionReply();
               ServerAlreadyRegisteredHelper.write(var4, var16);
            } catch (BadServerDefinition var17) {
               var4 = var3.createExceptionReply();
               BadServerDefinitionHelper.write(var4, var17);
            }
            break;
         case 1:
            try {
               var21 = ServerIdHelper.read(var2);
               this.unregisterServer(var21);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var15) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var15);
            }
            break;
         case 2:
            try {
               var21 = ServerIdHelper.read(var2);
               ServerDef var22 = null;
               var22 = this.getServer(var21);
               var4 = var3.createReply();
               ServerDefHelper.write(var4, var22);
            } catch (ServerNotRegistered var14) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var14);
            }
            break;
         case 3:
            try {
               var21 = ServerIdHelper.read(var2);
               var7 = false;
               var7 = this.isInstalled(var21);
               var4 = var3.createReply();
               var4.write_boolean(var7);
            } catch (ServerNotRegistered var13) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var13);
            }
            break;
         case 4:
            try {
               var21 = ServerIdHelper.read(var2);
               this.install(var21);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var11) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var11);
            } catch (ServerAlreadyInstalled var12) {
               var4 = var3.createExceptionReply();
               ServerAlreadyInstalledHelper.write(var4, var12);
            }
            break;
         case 5:
            try {
               var21 = ServerIdHelper.read(var2);
               this.uninstall(var21);
               var4 = var3.createReply();
            } catch (ServerNotRegistered var9) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var9);
            } catch (ServerAlreadyUninstalled var10) {
               var4 = var3.createExceptionReply();
               ServerAlreadyUninstalledHelper.write(var4, var10);
            }
            break;
         case 6:
            var6 = null;
            int[] var19 = this.listRegisteredServers();
            var4 = var3.createReply();
            ServerIdsHelper.write(var4, var19);
            break;
         case 7:
            var6 = null;
            String[] var18 = this.getApplicationNames();
            var4 = var3.createReply();
            StringSeqHelper.write(var4, var18);
            break;
         case 8:
            try {
               var6 = var2.read_string();
               var7 = false;
               var20 = this.getServerID(var6);
               var4 = var3.createReply();
               var4.write_long(var20);
            } catch (ServerNotRegistered var8) {
               var4 = var3.createExceptionReply();
               ServerNotRegisteredHelper.write(var4, var8);
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
      _methods.put("registerServer", new Integer(0));
      _methods.put("unregisterServer", new Integer(1));
      _methods.put("getServer", new Integer(2));
      _methods.put("isInstalled", new Integer(3));
      _methods.put("install", new Integer(4));
      _methods.put("uninstall", new Integer(5));
      _methods.put("listRegisteredServers", new Integer(6));
      _methods.put("getApplicationNames", new Integer(7));
      _methods.put("getServerID", new Integer(8));
      __ids = new String[]{"IDL:activation/Repository:1.0"};
   }
}
