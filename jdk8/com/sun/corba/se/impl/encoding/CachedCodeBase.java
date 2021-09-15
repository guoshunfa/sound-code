package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import java.util.Iterator;

public class CachedCodeBase extends _CodeBaseImplBase {
   private Hashtable implementations;
   private Hashtable fvds;
   private Hashtable bases;
   private volatile CodeBase delegate;
   private CorbaConnection conn;
   private static Object iorMapLock = new Object();
   private static Hashtable<IOR, CodeBase> iorMap = new Hashtable();

   public static synchronized void cleanCache(ORB var0) {
      synchronized(iorMapLock) {
         Iterator var2 = iorMap.keySet().iterator();

         while(var2.hasNext()) {
            IOR var3 = (IOR)var2.next();
            if (var3.getORB() == var0) {
               iorMap.remove(var3);
            }
         }

      }
   }

   public CachedCodeBase(CorbaConnection var1) {
      this.conn = var1;
   }

   public Repository get_ir() {
      return null;
   }

   public synchronized String implementation(String var1) {
      String var2 = null;
      if (this.implementations == null) {
         this.implementations = new Hashtable();
      } else {
         var2 = (String)this.implementations.get(var1);
      }

      if (var2 == null && this.connectedCodeBase()) {
         var2 = this.delegate.implementation(var1);
         if (var2 != null) {
            this.implementations.put(var1, var2);
         }
      }

      return var2;
   }

   public synchronized String[] implementations(String[] var1) {
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = this.implementation(var1[var3]);
      }

      return var2;
   }

   public synchronized FullValueDescription meta(String var1) {
      FullValueDescription var2 = null;
      if (this.fvds == null) {
         this.fvds = new Hashtable();
      } else {
         var2 = (FullValueDescription)this.fvds.get(var1);
      }

      if (var2 == null && this.connectedCodeBase()) {
         var2 = this.delegate.meta(var1);
         if (var2 != null) {
            this.fvds.put(var1, var2);
         }
      }

      return var2;
   }

   public synchronized FullValueDescription[] metas(String[] var1) {
      FullValueDescription[] var2 = new FullValueDescription[var1.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = this.meta(var1[var3]);
      }

      return var2;
   }

   public synchronized String[] bases(String var1) {
      String[] var2 = null;
      if (this.bases == null) {
         this.bases = new Hashtable();
      } else {
         var2 = (String[])((String[])this.bases.get(var1));
      }

      if (var2 == null && this.connectedCodeBase()) {
         var2 = this.delegate.bases(var1);
         if (var2 != null) {
            this.bases.put(var1, var2);
         }
      }

      return var2;
   }

   private synchronized boolean connectedCodeBase() {
      if (this.delegate != null) {
         return true;
      } else if (this.conn.getCodeBaseIOR() == null) {
         if (this.conn.getBroker().transportDebugFlag) {
            this.conn.dprint("CodeBase unavailable on connection: " + this.conn);
         }

         return false;
      } else {
         synchronized(iorMapLock) {
            if (this.delegate != null) {
               return true;
            } else {
               this.delegate = (CodeBase)iorMap.get(this.conn.getCodeBaseIOR());
               if (this.delegate != null) {
                  return true;
               } else {
                  this.delegate = CodeBaseHelper.narrow(this.getObjectFromIOR());
                  iorMap.put(this.conn.getCodeBaseIOR(), this.delegate);
                  return true;
               }
            }
         }
      }
   }

   private final org.omg.CORBA.Object getObjectFromIOR() {
      return CDRInputStream_1_0.internalIORToObject(this.conn.getCodeBaseIOR(), (PresentationManager.StubFactory)null, this.conn.getBroker());
   }
}
