package org.omg.PortableServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;

public class _ServantActivatorStub extends ObjectImpl implements ServantActivator {
   public static final Class _opsClass = ServantActivatorOperations.class;
   private static String[] __ids = new String[]{"IDL:omg.org/PortableServer/ServantActivator:2.3", "IDL:omg.org/PortableServer/ServantManager:1.0"};

   public Servant incarnate(byte[] var1, POA var2) throws ForwardRequest {
      ServantObject var3 = this._servant_preinvoke("incarnate", _opsClass);
      ServantActivatorOperations var4 = (ServantActivatorOperations)var3.servant;

      Servant var5;
      try {
         var5 = var4.incarnate(var1, var2);
      } finally {
         this._servant_postinvoke(var3);
      }

      return var5;
   }

   public void etherealize(byte[] var1, POA var2, Servant var3, boolean var4, boolean var5) {
      ServantObject var6 = this._servant_preinvoke("etherealize", _opsClass);
      ServantActivatorOperations var7 = (ServantActivatorOperations)var6.servant;

      try {
         var7.etherealize(var1, var2, var3, var4, var5);
      } finally {
         this._servant_postinvoke(var6);
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
