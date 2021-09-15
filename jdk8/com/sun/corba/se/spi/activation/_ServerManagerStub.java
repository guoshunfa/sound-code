package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _ServerManagerStub extends ObjectImpl implements ServerManager {
   private static String[] __ids = new String[]{"IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0"};

   public void active(int var1, Server var2) throws ServerNotRegistered {
      InputStream var3 = null;

      try {
         OutputStream var4 = this._request("active", true);
         ServerIdHelper.write(var4, var1);
         ServerHelper.write(var4, var2);
         var3 = this._invoke(var4);
         return;
      } catch (ApplicationException var10) {
         var3 = var10.getInputStream();
         String var5 = var10.getId();
         if (var5.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var3);
         }

         throw new MARSHAL(var5);
      } catch (RemarshalException var11) {
         this.active(var1, var2);
      } finally {
         this._releaseReply(var3);
      }

   }

   public void registerEndpoints(int var1, String var2, EndPointInfo[] var3) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered {
      InputStream var4 = null;

      try {
         OutputStream var5 = this._request("registerEndpoints", true);
         ServerIdHelper.write(var5, var1);
         ORBidHelper.write(var5, var2);
         EndpointInfoListHelper.write(var5, var3);
         var4 = this._invoke(var5);
         return;
      } catch (ApplicationException var11) {
         var4 = var11.getInputStream();
         String var6 = var11.getId();
         if (var6.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var4);
         }

         if (var6.equals("IDL:activation/NoSuchEndPoint:1.0")) {
            throw NoSuchEndPointHelper.read(var4);
         }

         if (var6.equals("IDL:activation/ORBAlreadyRegistered:1.0")) {
            throw ORBAlreadyRegisteredHelper.read(var4);
         }

         throw new MARSHAL(var6);
      } catch (RemarshalException var12) {
         this.registerEndpoints(var1, var2, var3);
      } finally {
         this._releaseReply(var4);
      }

   }

   public int[] getActiveServers() {
      InputStream var1 = null;

      int[] var3;
      try {
         OutputStream var2 = this._request("getActiveServers", true);
         var1 = this._invoke(var2);
         var3 = ServerIdsHelper.read(var1);
         int[] var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.getActiveServers();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public void activate(int var1) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
      InputStream var2 = null;

      try {
         OutputStream var3 = this._request("activate", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         return;
      } catch (ApplicationException var9) {
         var2 = var9.getInputStream();
         String var4 = var9.getId();
         if (var4.equals("IDL:activation/ServerAlreadyActive:1.0")) {
            throw ServerAlreadyActiveHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerHeldDown:1.0")) {
            throw ServerHeldDownHelper.read(var2);
         }

         throw new MARSHAL(var4);
      } catch (RemarshalException var10) {
         this.activate(var1);
      } finally {
         this._releaseReply(var2);
      }

   }

   public void shutdown(int var1) throws ServerNotActive, ServerNotRegistered {
      InputStream var2 = null;

      try {
         OutputStream var3 = this._request("shutdown", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         return;
      } catch (ApplicationException var9) {
         var2 = var9.getInputStream();
         String var4 = var9.getId();
         if (var4.equals("IDL:activation/ServerNotActive:1.0")) {
            throw ServerNotActiveHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var4);
      } catch (RemarshalException var10) {
         this.shutdown(var1);
      } finally {
         this._releaseReply(var2);
      }

   }

   public void install(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled {
      InputStream var2 = null;

      try {
         OutputStream var3 = this._request("install", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         return;
      } catch (ApplicationException var9) {
         var2 = var9.getInputStream();
         String var4 = var9.getId();
         if (var4.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerHeldDown:1.0")) {
            throw ServerHeldDownHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerAlreadyInstalled:1.0")) {
            throw ServerAlreadyInstalledHelper.read(var2);
         }

         throw new MARSHAL(var4);
      } catch (RemarshalException var10) {
         this.install(var1);
      } finally {
         this._releaseReply(var2);
      }

   }

   public String[] getORBNames(int var1) throws ServerNotRegistered {
      InputStream var2 = null;

      String[] var4;
      try {
         OutputStream var3 = this._request("getORBNames", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = ORBidListHelper.read(var2);
         String[] var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.getORBNames(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public void uninstall(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled {
      InputStream var2 = null;

      try {
         OutputStream var3 = this._request("uninstall", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         return;
      } catch (ApplicationException var9) {
         var2 = var9.getInputStream();
         String var4 = var9.getId();
         if (var4.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerHeldDown:1.0")) {
            throw ServerHeldDownHelper.read(var2);
         }

         if (var4.equals("IDL:activation/ServerAlreadyUninstalled:1.0")) {
            throw ServerAlreadyUninstalledHelper.read(var2);
         }

         throw new MARSHAL(var4);
      } catch (RemarshalException var10) {
         this.uninstall(var1);
      } finally {
         this._releaseReply(var2);
      }

   }

   public ServerLocation locateServer(int var1, String var2) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
      InputStream var3 = null;

      ServerLocation var5;
      try {
         OutputStream var4 = this._request("locateServer", true);
         ServerIdHelper.write(var4, var1);
         var4.write_string(var2);
         var3 = this._invoke(var4);
         var5 = ServerLocationHelper.read(var3);
         ServerLocation var6 = var5;
         return var6;
      } catch (ApplicationException var11) {
         var3 = var11.getInputStream();
         String var14 = var11.getId();
         if (var14.equals("IDL:activation/NoSuchEndPoint:1.0")) {
            throw NoSuchEndPointHelper.read(var3);
         }

         if (var14.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var3);
         }

         if (var14.equals("IDL:activation/ServerHeldDown:1.0")) {
            throw ServerHeldDownHelper.read(var3);
         }

         throw new MARSHAL(var14);
      } catch (RemarshalException var12) {
         var5 = this.locateServer(var1, var2);
      } finally {
         this._releaseReply(var3);
      }

      return var5;
   }

   public ServerLocationPerORB locateServerForORB(int var1, String var2) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
      InputStream var3 = null;

      ServerLocationPerORB var5;
      try {
         OutputStream var4 = this._request("locateServerForORB", true);
         ServerIdHelper.write(var4, var1);
         ORBidHelper.write(var4, var2);
         var3 = this._invoke(var4);
         var5 = ServerLocationPerORBHelper.read(var3);
         ServerLocationPerORB var6 = var5;
         return var6;
      } catch (ApplicationException var11) {
         var3 = var11.getInputStream();
         String var14 = var11.getId();
         if (var14.equals("IDL:activation/InvalidORBid:1.0")) {
            throw InvalidORBidHelper.read(var3);
         }

         if (var14.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var3);
         }

         if (var14.equals("IDL:activation/ServerHeldDown:1.0")) {
            throw ServerHeldDownHelper.read(var3);
         }

         throw new MARSHAL(var14);
      } catch (RemarshalException var12) {
         var5 = this.locateServerForORB(var1, var2);
      } finally {
         this._releaseReply(var3);
      }

      return var5;
   }

   public int getEndpoint(String var1) throws NoSuchEndPoint {
      InputStream var2 = null;

      int var4;
      try {
         OutputStream var3 = this._request("getEndpoint", true);
         var3.write_string(var1);
         var2 = this._invoke(var3);
         var4 = TCPPortHelper.read(var2);
         int var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/NoSuchEndPoint:1.0")) {
            throw NoSuchEndPointHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.getEndpoint(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public int getServerPortForType(ServerLocationPerORB var1, String var2) throws NoSuchEndPoint {
      InputStream var3 = null;

      int var5;
      try {
         OutputStream var4 = this._request("getServerPortForType", true);
         ServerLocationPerORBHelper.write(var4, var1);
         var4.write_string(var2);
         var3 = this._invoke(var4);
         var5 = TCPPortHelper.read(var3);
         int var6 = var5;
         return var6;
      } catch (ApplicationException var11) {
         var3 = var11.getInputStream();
         String var14 = var11.getId();
         if (var14.equals("IDL:activation/NoSuchEndPoint:1.0")) {
            throw NoSuchEndPointHelper.read(var3);
         }

         throw new MARSHAL(var14);
      } catch (RemarshalException var12) {
         var5 = this.getServerPortForType(var1, var2);
      } finally {
         this._releaseReply(var3);
      }

      return var5;
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      String var2 = var1.readUTF();
      Object var3 = null;
      Object var4 = null;
      ORB var5 = ORB.init((String[])var3, (Properties)var4);

      try {
         org.omg.CORBA.Object var6 = var5.string_to_object(var2);
         Delegate var7 = ((ObjectImpl)var6)._get_delegate();
         this._set_delegate(var7);
      } finally {
         var5.destroy();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object var2 = null;
      Object var3 = null;
      ORB var4 = ORB.init((String[])var2, (Properties)var3);

      try {
         String var5 = var4.object_to_string(this);
         var1.writeUTF(var5);
      } finally {
         var4.destroy();
      }

   }
}
