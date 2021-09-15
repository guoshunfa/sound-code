package org.omg.stub.javax.management.remote.rmi;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.SerializablePermission;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import javax.management.remote.rmi.RMIConnection;
import javax.management.remote.rmi.RMIServer;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;

public class _RMIServer_Stub extends Stub implements RMIServer {
   private static final String[] _type_ids = new String[]{"RMI:javax.management.remote.rmi.RMIServer:0000000000000000"};
   private transient boolean _instantiated;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class class$javax$management$remote$rmi$RMIServer;
   // $FF: synthetic field
   static Class class$javax$management$remote$rmi$RMIConnection;
   // $FF: synthetic field
   static Class class$java$io$IOException;

   public _RMIServer_Stub() {
      this(checkPermission());
      this._instantiated = true;
   }

   private _RMIServer_Stub(Void var1) {
      this._instantiated = false;
   }

   public String[] _ids() {
      return (String[])_type_ids.clone();
   }

   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new SerializablePermission("enableSubclassImplementation"));
      }

      return null;
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public String getVersion() throws RemoteException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var2;
         if (!Util.isLocal(this)) {
            try {
               InputStream var25 = null;

               try {
                  OutputStream var5 = this._request("_get_version", true);
                  var25 = (InputStream)this._invoke(var5);
                  var2 = (String)var25.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  return var2;
               } catch (ApplicationException var21) {
                  var25 = (InputStream)var21.getInputStream();
                  String var26 = var25.read_string();
                  throw new UnexpectedException(var26);
               } catch (RemarshalException var22) {
                  var2 = this.getVersion();
               } finally {
                  this._releaseReply(var25);
               }

               return var2;
            } catch (SystemException var24) {
               throw Util.mapSystemException(var24);
            }
         } else {
            ServantObject var1 = this._servant_preinvoke("_get_version", class$javax$management$remote$rmi$RMIServer != null ? class$javax$management$remote$rmi$RMIServer : (class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer")));
            if (var1 == null) {
               return this.getVersion();
            } else {
               try {
                  var2 = ((RMIServer)var1.servant).getVersion();
               } catch (Throwable var19) {
                  Throwable var6 = (Throwable)Util.copyObject(var19, this._orb());
                  throw Util.wrapException(var6);
               } finally {
                  this._servant_postinvoke(var1);
               }

               return var2;
            }
         }
      }
   }

   public RMIConnection newClient(Object var1) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         RMIConnection var3;
         if (!Util.isLocal(this)) {
            try {
               InputStream var26 = null;

               try {
                  OutputStream var27 = this._request("newClient", true);
                  Util.writeAny(var27, var1);
                  var26 = (InputStream)this._invoke(var27);
                  var3 = (RMIConnection)PortableRemoteObject.narrow(var26.read_Object(), class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
                  return var3;
               } catch (ApplicationException var22) {
                  var26 = (InputStream)var22.getInputStream();
                  String var29 = var26.read_string();
                  if (var29.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var26.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var29);
               } catch (RemarshalException var23) {
                  var3 = this.newClient(var1);
               } finally {
                  this._releaseReply(var26);
               }

               return var3;
            } catch (SystemException var25) {
               throw Util.mapSystemException(var25);
            }
         } else {
            ServantObject var2 = this._servant_preinvoke("newClient", class$javax$management$remote$rmi$RMIServer != null ? class$javax$management$remote$rmi$RMIServer : (class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer")));
            if (var2 == null) {
               return this.newClient(var1);
            } else {
               try {
                  Object var6 = Util.copyObject(var1, this._orb());
                  RMIConnection var28 = ((RMIServer)var2.servant).newClient(var6);
                  var3 = (RMIConnection)Util.copyObject(var28, this._orb());
               } catch (Throwable var20) {
                  Throwable var7 = (Throwable)Util.copyObject(var20, this._orb());
                  if (var7 instanceof IOException) {
                     throw (IOException)var7;
                  }

                  throw Util.wrapException(var7);
               } finally {
                  this._servant_postinvoke(var2);
               }

               return var3;
            }
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      checkPermission();
      var1.defaultReadObject();
      this._instantiated = true;
   }
}
