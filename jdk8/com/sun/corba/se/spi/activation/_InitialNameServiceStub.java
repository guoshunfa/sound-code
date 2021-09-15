package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _InitialNameServiceStub extends ObjectImpl implements InitialNameService {
   private static String[] __ids = new String[]{"IDL:activation/InitialNameService:1.0"};

   public void bind(String var1, Object var2, boolean var3) throws NameAlreadyBound {
      InputStream var4 = null;

      try {
         OutputStream var5 = this._request("bind", true);
         var5.write_string(var1);
         ObjectHelper.write(var5, var2);
         var5.write_boolean(var3);
         var4 = this._invoke(var5);
         return;
      } catch (ApplicationException var11) {
         var4 = var11.getInputStream();
         String var6 = var11.getId();
         if (var6.equals("IDL:activation/InitialNameService/NameAlreadyBound:1.0")) {
            throw NameAlreadyBoundHelper.read(var4);
         }

         throw new MARSHAL(var6);
      } catch (RemarshalException var12) {
         this.bind(var1, var2, var3);
      } finally {
         this._releaseReply(var4);
      }

   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      String var2 = var1.readUTF();
      java.lang.Object var3 = null;
      java.lang.Object var4 = null;
      ORB var5 = ORB.init((String[])var3, (Properties)var4);

      try {
         Object var6 = var5.string_to_object(var2);
         Delegate var7 = ((ObjectImpl)var6)._get_delegate();
         this._set_delegate(var7);
      } finally {
         var5.destroy();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      java.lang.Object var2 = null;
      java.lang.Object var3 = null;
      ORB var4 = ORB.init((String[])var2, (Properties)var3);

      try {
         String var5 = var4.object_to_string(this);
         var1.writeUTF(var5);
      } finally {
         var4.destroy();
      }

   }
}
