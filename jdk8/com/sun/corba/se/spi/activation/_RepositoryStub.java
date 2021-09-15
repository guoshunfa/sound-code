package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
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

public class _RepositoryStub extends ObjectImpl implements Repository {
   private static String[] __ids = new String[]{"IDL:activation/Repository:1.0"};

   public int registerServer(ServerDef var1) throws ServerAlreadyRegistered, BadServerDefinition {
      InputStream var2 = null;

      int var4;
      try {
         OutputStream var3 = this._request("registerServer", true);
         ServerDefHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = ServerIdHelper.read(var2);
         int var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/ServerAlreadyRegistered:1.0")) {
            throw ServerAlreadyRegisteredHelper.read(var2);
         }

         if (var13.equals("IDL:activation/BadServerDefinition:1.0")) {
            throw BadServerDefinitionHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.registerServer(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public void unregisterServer(int var1) throws ServerNotRegistered {
      InputStream var2 = null;

      try {
         OutputStream var3 = this._request("unregisterServer", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         return;
      } catch (ApplicationException var9) {
         var2 = var9.getInputStream();
         String var4 = var9.getId();
         if (var4.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var4);
      } catch (RemarshalException var10) {
         this.unregisterServer(var1);
      } finally {
         this._releaseReply(var2);
      }

   }

   public ServerDef getServer(int var1) throws ServerNotRegistered {
      InputStream var2 = null;

      ServerDef var4;
      try {
         OutputStream var3 = this._request("getServer", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = ServerDefHelper.read(var2);
         ServerDef var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.getServer(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public boolean isInstalled(int var1) throws ServerNotRegistered {
      InputStream var2 = null;

      boolean var4;
      try {
         OutputStream var3 = this._request("isInstalled", true);
         ServerIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = var2.read_boolean();
         boolean var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.isInstalled(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public void install(int var1) throws ServerNotRegistered, ServerAlreadyInstalled {
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

   public void uninstall(int var1) throws ServerNotRegistered, ServerAlreadyUninstalled {
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

   public int[] listRegisteredServers() {
      InputStream var1 = null;

      int[] var3;
      try {
         OutputStream var2 = this._request("listRegisteredServers", true);
         var1 = this._invoke(var2);
         var3 = ServerIdsHelper.read(var1);
         int[] var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.listRegisteredServers();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public String[] getApplicationNames() {
      InputStream var1 = null;

      String[] var3;
      try {
         OutputStream var2 = this._request("getApplicationNames", true);
         var1 = this._invoke(var2);
         var3 = StringSeqHelper.read(var1);
         String[] var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.getApplicationNames();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public int getServerID(String var1) throws ServerNotRegistered {
      InputStream var2 = null;

      int var4;
      try {
         OutputStream var3 = this._request("getServerID", true);
         var3.write_string(var1);
         var2 = this._invoke(var3);
         var4 = ServerIdHelper.read(var2);
         int var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         if (var13.equals("IDL:activation/ServerNotRegistered:1.0")) {
            throw ServerNotRegisteredHelper.read(var2);
         }

         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.getServerID(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
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
