package com.sun.corba.se.spi.activation;

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

public class _ServerStub extends ObjectImpl implements Server {
   private static String[] __ids = new String[]{"IDL:activation/Server:1.0"};

   public void shutdown() {
      InputStream var1 = null;

      try {
         OutputStream var2 = this._request("shutdown", true);
         var1 = this._invoke(var2);
         return;
      } catch (ApplicationException var8) {
         var1 = var8.getInputStream();
         String var3 = var8.getId();
         throw new MARSHAL(var3);
      } catch (RemarshalException var9) {
         this.shutdown();
      } finally {
         this._releaseReply(var1);
      }

   }

   public void install() {
      InputStream var1 = null;

      try {
         OutputStream var2 = this._request("install", true);
         var1 = this._invoke(var2);
         return;
      } catch (ApplicationException var8) {
         var1 = var8.getInputStream();
         String var3 = var8.getId();
         throw new MARSHAL(var3);
      } catch (RemarshalException var9) {
         this.install();
      } finally {
         this._releaseReply(var1);
      }

   }

   public void uninstall() {
      InputStream var1 = null;

      try {
         OutputStream var2 = this._request("uninstall", true);
         var1 = this._invoke(var2);
         return;
      } catch (ApplicationException var8) {
         var1 = var8.getInputStream();
         String var3 = var8.getId();
         throw new MARSHAL(var3);
      } catch (RemarshalException var9) {
         this.uninstall();
      } finally {
         this._releaseReply(var1);
      }

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
