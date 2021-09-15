package org.omg.PortableServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class _ServantLocatorStub extends ObjectImpl implements ServantLocator {
   public static final Class _opsClass = ServantLocatorOperations.class;
   private static String[] __ids = new String[]{"IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0"};

   public Servant preinvoke(byte[] var1, POA var2, String var3, CookieHolder var4) throws ForwardRequest {
      ServantObject var5 = this._servant_preinvoke("preinvoke", _opsClass);
      ServantLocatorOperations var6 = (ServantLocatorOperations)var5.servant;

      Servant var7;
      try {
         var7 = var6.preinvoke(var1, var2, var3, var4);
      } finally {
         this._servant_postinvoke(var5);
      }

      return var7;
   }

   public void postinvoke(byte[] var1, POA var2, String var3, Object var4, Servant var5) {
      ServantObject var6 = this._servant_preinvoke("postinvoke", _opsClass);
      ServantLocatorOperations var7 = (ServantLocatorOperations)var6.servant;

      try {
         var7.postinvoke(var1, var2, var3, var4, var5);
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
