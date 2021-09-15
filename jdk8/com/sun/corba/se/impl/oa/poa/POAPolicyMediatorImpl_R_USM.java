package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import java.util.Set;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class POAPolicyMediatorImpl_R_USM extends POAPolicyMediatorBase_R {
   protected ServantActivator activator = null;

   POAPolicyMediatorImpl_R_USM(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (!var1.useServantManager()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      }
   }

   private AOMEntry enterEntry(ActiveObjectMap.Key var1) {
      AOMEntry var2 = null;

      boolean var3;
      do {
         var3 = false;
         var2 = this.activeObjectMap.get(var1);

         try {
            var2.enter();
         } catch (Exception var5) {
            var3 = true;
         }
      } while(var3);

      return var2;
   }

   protected Object internalGetServant(byte[] var1, String var2) throws ForwardRequest {
      if (this.poa.getDebug()) {
         ORBUtility.dprint((Object)this, "Calling POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa + " operation=" + var2);
      }

      Object var6;
      try {
         ActiveObjectMap.Key var3 = new ActiveObjectMap.Key(var1);
         AOMEntry var4 = this.enterEntry(var3);
         Object var5 = this.activeObjectMap.getServant(var4);
         if (var5 != null) {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: servant already activated");
            }

            var6 = var5;
            return var6;
         }

         if (this.activator == null) {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: no servant activator in POA");
            }

            var4.incarnateFailure();
            throw this.poa.invocationWrapper().poaNoServantManager();
         }

         try {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: upcall to incarnate");
            }

            this.poa.unlock();
            var5 = this.activator.incarnate(var1, this.poa);
            if (var5 == null) {
               var5 = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned());
            }
         } catch (ForwardRequest var19) {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: incarnate threw ForwardRequest");
            }

            throw var19;
         } catch (SystemException var20) {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: incarnate threw SystemException " + var20);
            }

            throw var20;
         } catch (Throwable var21) {
            if (this.poa.getDebug()) {
               ORBUtility.dprint((Object)this, "internalGetServant: incarnate threw Throwable " + var21);
            }

            throw this.poa.invocationWrapper().poaServantActivatorLookupFailed(var21);
         } finally {
            this.poa.lock();
            if (var5 != null && !(var5 instanceof NullServant)) {
               if (this.isUnique && this.activeObjectMap.contains((Servant)var5)) {
                  if (this.poa.getDebug()) {
                     ORBUtility.dprint((Object)this, "internalGetServant: servant already assigned to ID");
                  }

                  var4.incarnateFailure();
                  throw this.poa.invocationWrapper().poaServantNotUnique();
               }

               if (this.poa.getDebug()) {
                  ORBUtility.dprint((Object)this, "internalGetServant: incarnate complete");
               }

               var4.incarnateComplete();
               this.activateServant(var3, var4, (Servant)var5);
            } else {
               if (this.poa.getDebug()) {
                  ORBUtility.dprint((Object)this, "internalGetServant: incarnate failed");
               }

               var4.incarnateFailure();
            }

         }

         var6 = var5;
      } finally {
         if (this.poa.getDebug()) {
            ORBUtility.dprint((Object)this, "Exiting POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa);
         }

      }

      return var6;
   }

   public void returnServant() {
      OAInvocationInfo var1 = this.orb.peekInvocationInfo();
      byte[] var2 = var1.id();
      ActiveObjectMap.Key var3 = new ActiveObjectMap.Key(var2);
      AOMEntry var4 = this.activeObjectMap.get(var3);
      var4.exit();
   }

   public void etherealizeAll() {
      if (this.activator != null) {
         Set var1 = this.activeObjectMap.keySet();
         ActiveObjectMap.Key[] var2 = (ActiveObjectMap.Key[])((ActiveObjectMap.Key[])var1.toArray(new ActiveObjectMap.Key[var1.size()]));

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            ActiveObjectMap.Key var4 = var2[var3];
            AOMEntry var5 = this.activeObjectMap.get(var4);
            Servant var6 = this.activeObjectMap.getServant(var5);
            if (var6 != null) {
               boolean var7 = this.activeObjectMap.hasMultipleIDs(var5);
               var5.startEtherealize((Thread)null);

               try {
                  this.poa.unlock();

                  try {
                     this.activator.etherealize(var4.id, this.poa, var6, true, var7);
                  } catch (Exception var12) {
                  }
               } finally {
                  this.poa.lock();
                  var5.etherealizeComplete();
               }
            }
         }
      }

   }

   public ServantManager getServantManager() throws WrongPolicy {
      return this.activator;
   }

   public void setServantManager(ServantManager var1) throws WrongPolicy {
      if (this.activator != null) {
         throw this.poa.invocationWrapper().servantManagerAlreadySet();
      } else if (var1 instanceof ServantActivator) {
         this.activator = (ServantActivator)var1;
      } else {
         throw this.poa.invocationWrapper().servantManagerBadType();
      }
   }

   public Servant getDefaultServant() throws NoServant, WrongPolicy {
      throw new WrongPolicy();
   }

   public void setDefaultServant(Servant var1) throws WrongPolicy {
      throw new WrongPolicy();
   }

   public void deactivateHelper(ActiveObjectMap.Key var1, AOMEntry var2, Servant var3) throws ObjectNotActive, WrongPolicy {
      if (this.activator == null) {
         throw this.poa.invocationWrapper().poaNoServantManager();
      } else {
         POAPolicyMediatorImpl_R_USM.Etherealizer var4 = new POAPolicyMediatorImpl_R_USM.Etherealizer(this, var1, var2, var3, this.poa.getDebug());
         var2.startEtherealize(var4);
      }
   }

   public Servant idToServant(byte[] var1) throws WrongPolicy, ObjectNotActive {
      ActiveObjectMap.Key var2 = new ActiveObjectMap.Key(var1);
      AOMEntry var3 = this.activeObjectMap.get(var2);
      Servant var4 = this.activeObjectMap.getServant(var3);
      if (var4 != null) {
         return var4;
      } else {
         throw new ObjectNotActive();
      }
   }

   class Etherealizer extends Thread {
      private POAPolicyMediatorImpl_R_USM mediator;
      private ActiveObjectMap.Key key;
      private AOMEntry entry;
      private Servant servant;
      private boolean debug;

      public Etherealizer(POAPolicyMediatorImpl_R_USM var2, ActiveObjectMap.Key var3, AOMEntry var4, Servant var5, boolean var6) {
         this.mediator = var2;
         this.key = var3;
         this.entry = var4;
         this.servant = var5;
         this.debug = var6;
      }

      public void run() {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling Etherealizer.run on key " + this.key);
         }

         try {
            try {
               this.mediator.activator.etherealize(this.key.id, this.mediator.poa, this.servant, false, this.mediator.activeObjectMap.hasMultipleIDs(this.entry));
            } catch (Exception var12) {
            }

            try {
               this.mediator.poa.lock();
               this.entry.etherealizeComplete();
               this.mediator.activeObjectMap.remove(this.key);
               POAManagerImpl var1 = (POAManagerImpl)this.mediator.poa.the_POAManager();
               POAFactory var2 = var1.getFactory();
               var2.unregisterPOAForServant(this.mediator.poa, this.servant);
            } finally {
               this.mediator.poa.unlock();
            }
         } finally {
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Exiting Etherealizer.run");
            }

         }

      }
   }
}
