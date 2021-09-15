package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class ServantManagerImpl extends LocalObject implements ServantLocator {
   private static final long serialVersionUID = 4028710359865748280L;
   private ORB orb;
   private NameService theNameService;
   private File logDir;
   private Hashtable contexts;
   private CounterDB counterDb;
   private int counter;
   private static final String objKeyPrefix = "NC";

   ServantManagerImpl(ORB var1, File var2, NameService var3) {
      this.logDir = var2;
      this.orb = var1;
      this.counterDb = new CounterDB(var2);
      this.contexts = new Hashtable();
      this.theNameService = var3;
   }

   public Servant preinvoke(byte[] var1, POA var2, String var3, CookieHolder var4) throws ForwardRequest {
      String var5 = new String(var1);
      Object var6 = (Servant)this.contexts.get(var5);
      if (var6 == null) {
         var6 = this.readInContext(var5);
      }

      return (Servant)var6;
   }

   public void postinvoke(byte[] var1, POA var2, String var3, Object var4, Servant var5) {
   }

   public NamingContextImpl readInContext(String var1) {
      NamingContextImpl var2 = (NamingContextImpl)this.contexts.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         File var3 = new File(this.logDir, var1);
         if (var3.exists()) {
            try {
               FileInputStream var4 = new FileInputStream(var3);
               ObjectInputStream var5 = new ObjectInputStream(var4);
               var2 = (NamingContextImpl)var5.readObject();
               var2.setORB(this.orb);
               var2.setServantManagerImpl(this);
               var2.setRootNameService(this.theNameService);
               var5.close();
            } catch (Exception var6) {
            }
         }

         if (var2 != null) {
            this.contexts.put(var1, var2);
         }

         return var2;
      }
   }

   public NamingContextImpl addContext(String var1, NamingContextImpl var2) {
      File var3 = new File(this.logDir, var1);
      if (var3.exists()) {
         var2 = this.readInContext(var1);
      } else {
         try {
            FileOutputStream var4 = new FileOutputStream(var3);
            ObjectOutputStream var5 = new ObjectOutputStream(var4);
            var5.writeObject(var2);
            var5.close();
         } catch (Exception var7) {
         }
      }

      try {
         this.contexts.remove(var1);
      } catch (Exception var6) {
      }

      this.contexts.put(var1, var2);
      return var2;
   }

   public void updateContext(String var1, NamingContextImpl var2) {
      File var3 = new File(this.logDir, var1);
      if (var3.exists()) {
         var3.delete();
         var3 = new File(this.logDir, var1);
      }

      try {
         FileOutputStream var4 = new FileOutputStream(var3);
         ObjectOutputStream var5 = new ObjectOutputStream(var4);
         var5.writeObject(var2);
         var5.close();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static String getRootObjectKey() {
      return "NC0";
   }

   public String getNewObjectKey() {
      return "NC" + this.counterDb.getNextCounter();
   }
}
