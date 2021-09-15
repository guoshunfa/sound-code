package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _CodeBaseStub extends ObjectImpl implements CodeBase {
   private static String[] __ids = new String[]{"IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0"};

   public _CodeBaseStub() {
   }

   public _CodeBaseStub(Delegate var1) {
      this._set_delegate(var1);
   }

   public Repository get_ir() {
      InputStream var1 = null;

      Repository var3;
      try {
         OutputStream var2 = this._request("get_ir", true);
         var1 = this._invoke(var2);
         var3 = RepositoryHelper.read(var1);
         Repository var4 = var3;
         return var4;
      } catch (ApplicationException var9) {
         var1 = var9.getInputStream();
         String var12 = var9.getId();
         throw new MARSHAL(var12);
      } catch (RemarshalException var10) {
         var3 = this.get_ir();
      } finally {
         this._releaseReply(var1);
      }

      return var3;
   }

   public String implementation(String var1) {
      InputStream var2 = null;

      String var4;
      try {
         OutputStream var3 = this._request("implementation", true);
         RepositoryIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = URLHelper.read(var2);
         String var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         var4 = var10.getId();
         throw new MARSHAL(var4);
      } catch (RemarshalException var11) {
         var4 = this.implementation(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public String[] implementations(String[] var1) {
      InputStream var2 = null;

      String[] var4;
      try {
         OutputStream var3 = this._request("implementations", true);
         RepositoryIdSeqHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = URLSeqHelper.read(var2);
         String[] var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.implementations(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public FullValueDescription meta(String var1) {
      InputStream var2 = null;

      FullValueDescription var4;
      try {
         OutputStream var3 = this._request("meta", true);
         RepositoryIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = FullValueDescriptionHelper.read(var2);
         FullValueDescription var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.meta(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public FullValueDescription[] metas(String[] var1) {
      InputStream var2 = null;

      FullValueDescription[] var4;
      try {
         OutputStream var3 = this._request("metas", true);
         RepositoryIdSeqHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = ValueDescSeqHelper.read(var2);
         FullValueDescription[] var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.metas(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
   }

   public String[] bases(String var1) {
      InputStream var2 = null;

      String[] var4;
      try {
         OutputStream var3 = this._request("bases", true);
         RepositoryIdHelper.write(var3, var1);
         var2 = this._invoke(var3);
         var4 = RepositoryIdSeqHelper.read(var2);
         String[] var5 = var4;
         return var5;
      } catch (ApplicationException var10) {
         var2 = var10.getInputStream();
         String var13 = var10.getId();
         throw new MARSHAL(var13);
      } catch (RemarshalException var11) {
         var4 = this.bases(var1);
      } finally {
         this._releaseReply(var2);
      }

      return var4;
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
