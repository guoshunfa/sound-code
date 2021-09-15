package org.omg.CosNaming;

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

public class _BindingIteratorStub extends ObjectImpl implements BindingIterator {
   private static String[] __ids = new String[]{"IDL:omg.org/CosNaming/BindingIterator:1.0"};

   public boolean next_one(BindingHolder var1) {
      InputStream var2 = null;

      boolean var4;
      try {
         OutputStream var3 = this._request("next_one", true);
         var2 = this._invoke(var3);
         var4 = var2.read_boolean();
         var1.value = BindingHelper.read(var2);
         boolean var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.next_one(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public boolean next_n(int var1, BindingListHolder var2) {
      InputStream var3 = null;

      boolean var5;
      try {
         OutputStream var4 = this._request("next_n", true);
         var4.write_ulong(var1);
         var3 = this._invoke(var4);
         var5 = var3.read_boolean();
         var2.value = BindingListHelper.read(var3);
         boolean var6 = var5;
         return var6;
      } catch (ApplicationException var11) {
         var3 = var11.getInputStream();
         String var14 = var11.getId();
         throw new MARSHAL(var14);
      } catch (RemarshalException var12) {
         var5 = this.next_n(var1, var2);
      } finally {
         this._releaseReply(var3);
      }

      return var5;
   }

   public void destroy() {
      InputStream var1 = null;

      try {
         OutputStream var2 = this._request("destroy", true);
         var1 = this._invoke(var2);
         return;
      } catch (ApplicationException var8) {
         var1 = var8.getInputStream();
         String var3 = var8.getId();
         throw new MARSHAL(var3);
      } catch (RemarshalException var9) {
         this.destroy();
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
