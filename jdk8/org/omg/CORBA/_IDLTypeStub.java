package org.omg.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _IDLTypeStub extends ObjectImpl implements IDLType {
   private static String[] __ids = new String[]{"IDL:omg.org/CORBA/IDLType:1.0", "IDL:omg.org/CORBA/IRObject:1.0"};

   public _IDLTypeStub() {
   }

   public _IDLTypeStub(Delegate var1) {
      this._set_delegate(var1);
   }

   public TypeCode type() {
      InputStream var1 = null;

      TypeCode var3;
      try {
         OutputStream var2 = this._request("_get_type", true);
         var1 = this._invoke(var2);
         var3 = var1.read_TypeCode();
         TypeCode var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.type();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public DefinitionKind def_kind() {
      InputStream var1 = null;

      DefinitionKind var3;
      try {
         OutputStream var2 = this._request("_get_def_kind", true);
         var1 = this._invoke(var2);
         var3 = DefinitionKindHelper.read(var1);
         DefinitionKind var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.def_kind();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public void destroy() {
      InputStream var1 = null;

      try {
         OutputStream var2 = this._request("destroy", true);
         var1 = this._invoke(var2);
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

   private void readObject(ObjectInputStream var1) {
      try {
         String var2 = var1.readUTF();
         Object var3 = ORB.init().string_to_object(var2);
         Delegate var4 = ((ObjectImpl)var3)._get_delegate();
         this._set_delegate(var4);
      } catch (IOException var5) {
      }

   }

   private void writeObject(ObjectOutputStream var1) {
      try {
         String var2 = ORB.init().object_to_string(this);
         var1.writeUTF(var2);
      } catch (IOException var3) {
      }

   }
}
