package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.portable.Delegate;

public class POAFactory implements ObjectAdapterFactory {
   private Map exportedServantsToPOA = new WeakHashMap();
   private Set poaManagers = Collections.synchronizedSet(new HashSet(4));
   private int poaManagerId = 0;
   private int poaId = 0;
   private POAImpl rootPOA = null;
   private DelegateImpl delegateImpl = null;
   private ORB orb = null;
   private POASystemException wrapper;
   private OMGSystemException omgWrapper;
   private boolean isShuttingDown = false;

   public POASystemException getWrapper() {
      return this.wrapper;
   }

   public synchronized POA lookupPOA(Servant var1) {
      return (POA)this.exportedServantsToPOA.get(var1);
   }

   public synchronized void registerPOAForServant(POA var1, Servant var2) {
      this.exportedServantsToPOA.put(var2, var1);
   }

   public synchronized void unregisterPOAForServant(POA var1, Servant var2) {
      this.exportedServantsToPOA.remove(var2);
   }

   public void init(ORB var1) {
      this.orb = var1;
      this.wrapper = POASystemException.get(var1, "oa.lifecycle");
      this.omgWrapper = OMGSystemException.get(var1, "oa.lifecycle");
      this.delegateImpl = new DelegateImpl(var1, this);
      this.registerRootPOA();
      POACurrent var2 = new POACurrent(var1);
      var1.getLocalResolver().register("POACurrent", ClosureFactory.makeConstant(var2));
   }

   public ObjectAdapter find(ObjectAdapterId var1) {
      POA var2 = null;

      try {
         boolean var3 = true;
         Iterator var4 = var1.iterator();
         var2 = this.getRootPOA();

         while(var4.hasNext()) {
            String var5 = (String)((String)var4.next());
            if (var3) {
               if (!var5.equals("RootPOA")) {
                  throw this.wrapper.makeFactoryNotPoa(var5);
               }

               var3 = false;
            } else {
               var2 = var2.find_POA(var5, true);
            }
         }
      } catch (AdapterNonExistent var6) {
         throw this.omgWrapper.noObjectAdaptor((Throwable)var6);
      } catch (OBJECT_NOT_EXIST var7) {
         throw var7;
      } catch (TRANSIENT var8) {
         throw var8;
      } catch (Exception var9) {
         throw this.wrapper.poaLookupError((Throwable)var9);
      }

      if (var2 == null) {
         throw this.wrapper.poaLookupError();
      } else {
         return (ObjectAdapter)var2;
      }
   }

   public void shutdown(boolean var1) {
      Iterator var2 = null;
      synchronized(this) {
         this.isShuttingDown = true;
         var2 = (new HashSet(this.poaManagers)).iterator();
      }

      while(var2.hasNext()) {
         try {
            ((POAManager)var2.next()).deactivate(true, var1);
         } catch (AdapterInactive var5) {
         }
      }

   }

   public synchronized void removePoaManager(POAManager var1) {
      this.poaManagers.remove(var1);
   }

   public synchronized void addPoaManager(POAManager var1) {
      this.poaManagers.add(var1);
   }

   public synchronized int newPOAManagerId() {
      return this.poaManagerId++;
   }

   public void registerRootPOA() {
      Closure var1 = new Closure() {
         public Object evaluate() {
            return POAImpl.makeRootPOA(POAFactory.this.orb);
         }
      };
      this.orb.getLocalResolver().register("RootPOA", ClosureFactory.makeFuture(var1));
   }

   public synchronized POA getRootPOA() {
      if (this.rootPOA == null) {
         if (this.isShuttingDown) {
            throw this.omgWrapper.noObjectAdaptor();
         }

         try {
            org.omg.CORBA.Object var1 = this.orb.resolve_initial_references("RootPOA");
            this.rootPOA = (POAImpl)var1;
         } catch (InvalidName var2) {
            throw this.wrapper.cantResolveRootPoa((Throwable)var2);
         }
      }

      return this.rootPOA;
   }

   public Delegate getDelegateImpl() {
      return this.delegateImpl;
   }

   public synchronized int newPOAId() {
      return this.poaId++;
   }

   public ORB getORB() {
      return this.orb;
   }
}
