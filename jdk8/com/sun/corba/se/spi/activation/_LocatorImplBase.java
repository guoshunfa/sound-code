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

public abstract class _LocatorImplBase extends ObjectImpl implements Locator, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         String var7;
         int var18;
         ServerLocationPerORB var22;
         switch(var5) {
         case 0:
            try {
               var18 = ServerIdHelper.read(var2);
               var7 = var2.read_string();
               var22 = null;
               ServerLocation var23 = this.locateServer(var18, var7);
               var4 = var3.createReply();
               ServerLocationHelper.write(var4, var23);
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
         case 1:
            try {
               var18 = ServerIdHelper.read(var2);
               var7 = ORBidHelper.read(var2);
               var22 = null;
               var22 = this.locateServerForORB(var18, var7);
               var4 = var3.createReply();
               ServerLocationPerORBHelper.write(var4, var22);
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
         case 2:
            try {
               String var17 = var2.read_string();
               boolean var19 = false;
               int var20 = this.getEndpoint(var17);
               var4 = var3.createReply();
               var4.write_long(var20);
            } catch (NoSuchEndPoint var10) {
               var4 = var3.createExceptionReply();
               NoSuchEndPointHelper.write(var4, var10);
            }
            break;
         case 3:
            try {
               ServerLocationPerORB var6 = ServerLocationPerORBHelper.read(var2);
               var7 = var2.read_string();
               boolean var8 = false;
               int var21 = this.getServerPortForType(var6, var7);
               var4 = var3.createReply();
               var4.write_long(var21);
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
      _methods.put("locateServer", new Integer(0));
      _methods.put("locateServerForORB", new Integer(1));
      _methods.put("getEndpoint", new Integer(2));
      _methods.put("getServerPortForType", new Integer(3));
      __ids = new String[]{"IDL:activation/Locator:1.0"};
   }
}
