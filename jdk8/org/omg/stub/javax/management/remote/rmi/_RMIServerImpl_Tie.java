package org.omg.stub.javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.Remote;
import javax.management.remote.rmi.RMIConnection;
import javax.management.remote.rmi.RMIServerImpl;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class _RMIServerImpl_Tie extends ObjectImpl implements Tie {
   private volatile RMIServerImpl target = null;
   private static final String[] _type_ids = new String[]{"RMI:javax.management.remote.rmi.RMIServer:0000000000000000"};
   // $FF: synthetic field
   static Class class$java$io$IOException;
   // $FF: synthetic field
   static Class class$java$lang$String;

   public String[] _ids() {
      return (String[])_type_ids.clone();
   }

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) throws SystemException {
      try {
         RMIServerImpl var4 = this.target;
         if (var4 == null) {
            throw new IOException();
         } else {
            org.omg.CORBA_2_3.portable.InputStream var5 = (org.omg.CORBA_2_3.portable.InputStream)var2;
            switch(var1.length()) {
            case 9:
               if (var1.equals("newClient")) {
                  Object var14 = Util.readAny(var5);

                  RMIConnection var15;
                  try {
                     var15 = var4.newClient(var14);
                  } catch (IOException var11) {
                     String var9 = "IDL:java/io/IOEx:1.0";
                     org.omg.CORBA_2_3.portable.OutputStream var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var9);
                     var10.write_value(var11, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var10;
                  }

                  OutputStream var8 = var3.createReply();
                  Util.writeRemoteObject(var8, var15);
                  return var8;
               }
            case 12:
               if (var1.equals("_get_version")) {
                  String var6 = var4.getVersion();
                  org.omg.CORBA_2_3.portable.OutputStream var7 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var7.write_value(var6, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  return var7;
               }
            case 10:
            case 11:
            default:
               throw new BAD_OPERATION();
            }
         }
      } catch (SystemException var12) {
         throw var12;
      } catch (Throwable var13) {
         throw new UnknownException(var13);
      }
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public void deactivate() {
      this._orb().disconnect(this);
      this._set_delegate((Delegate)null);
      this.target = null;
   }

   public Remote getTarget() {
      return this.target;
   }

   public ORB orb() {
      return this._orb();
   }

   public void orb(ORB var1) {
      var1.connect(this);
   }

   public void setTarget(Remote var1) {
      this.target = (RMIServerImpl)var1;
   }

   public org.omg.CORBA.Object thisObject() {
      return this;
   }
}
