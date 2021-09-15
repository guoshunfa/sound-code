package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public abstract class POAPolicyMediatorBase_R extends POAPolicyMediatorBase {
   protected ActiveObjectMap activeObjectMap;

   POAPolicyMediatorBase_R(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (!var1.retainServants()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      } else {
         this.activeObjectMap = ActiveObjectMap.create(var2, !this.isUnique);
      }
   }

   public void returnServant() {
   }

   public void clearAOM() {
      this.activeObjectMap.clear();
      this.activeObjectMap = null;
   }

   protected Servant internalKeyToServant(ActiveObjectMap.Key var1) {
      AOMEntry var2 = this.activeObjectMap.get(var1);
      return var2 == null ? null : this.activeObjectMap.getServant(var2);
   }

   protected Servant internalIdToServant(byte[] var1) {
      ActiveObjectMap.Key var2 = new ActiveObjectMap.Key(var1);
      return this.internalKeyToServant(var2);
   }

   protected void activateServant(ActiveObjectMap.Key var1, AOMEntry var2, Servant var3) {
      this.setDelegate(var3, var1.id);
      if (this.orb.shutdownDebugFlag) {
         System.out.println("Activating object " + var3 + " with POA " + this.poa);
      }

      this.activeObjectMap.putServant(var3, var2);
      if (Util.isInstanceDefined()) {
         POAManagerImpl var4 = (POAManagerImpl)this.poa.the_POAManager();
         POAFactory var5 = var4.getFactory();
         var5.registerPOAForServant(this.poa, var3);
      }

   }

   public final void activateObject(byte[] var1, Servant var2) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive {
      if (this.isUnique && this.activeObjectMap.contains(var2)) {
         throw new ServantAlreadyActive();
      } else {
         ActiveObjectMap.Key var3 = new ActiveObjectMap.Key(var1);
         AOMEntry var4 = this.activeObjectMap.get(var3);
         var4.activateObject();
         this.activateServant(var3, var4, var2);
      }
   }

   public Servant deactivateObject(byte[] var1) throws ObjectNotActive, WrongPolicy {
      ActiveObjectMap.Key var2 = new ActiveObjectMap.Key(var1);
      return this.deactivateObject(var2);
   }

   protected void deactivateHelper(ActiveObjectMap.Key var1, AOMEntry var2, Servant var3) throws ObjectNotActive, WrongPolicy {
      this.activeObjectMap.remove(var1);
      if (Util.isInstanceDefined()) {
         POAManagerImpl var4 = (POAManagerImpl)this.poa.the_POAManager();
         POAFactory var5 = var4.getFactory();
         var5.unregisterPOAForServant(this.poa, var3);
      }

   }

   public Servant deactivateObject(ActiveObjectMap.Key var1) throws ObjectNotActive, WrongPolicy {
      if (this.orb.poaDebugFlag) {
         ORBUtility.dprint((Object)this, "Calling deactivateObject for key " + var1);
      }

      Servant var4;
      try {
         AOMEntry var2 = this.activeObjectMap.get(var1);
         if (var2 == null) {
            throw new ObjectNotActive();
         }

         Servant var3 = this.activeObjectMap.getServant(var2);
         if (var3 == null) {
            throw new ObjectNotActive();
         }

         if (this.orb.poaDebugFlag) {
            System.out.println("Deactivating object " + var3 + " with POA " + this.poa);
         }

         this.deactivateHelper(var1, var2, var3);
         var4 = var3;
      } finally {
         if (this.orb.poaDebugFlag) {
            ORBUtility.dprint((Object)this, "Exiting deactivateObject");
         }

      }

      return var4;
   }

   public byte[] servantToId(Servant var1) throws ServantNotActive, WrongPolicy {
      if (!this.isUnique && !this.isImplicit) {
         throw new WrongPolicy();
      } else {
         if (this.isUnique) {
            ActiveObjectMap.Key var2 = this.activeObjectMap.getKey(var1);
            if (var2 != null) {
               return var2.id;
            }
         }

         if (this.isImplicit) {
            try {
               byte[] var6 = this.newSystemId();
               this.activateObject(var6, var1);
               return var6;
            } catch (ObjectAlreadyActive var3) {
               throw this.poa.invocationWrapper().servantToIdOaa((Throwable)var3);
            } catch (ServantAlreadyActive var4) {
               throw this.poa.invocationWrapper().servantToIdSaa((Throwable)var4);
            } catch (WrongPolicy var5) {
               throw this.poa.invocationWrapper().servantToIdWp((Throwable)var5);
            }
         } else {
            throw new ServantNotActive();
         }
      }
   }
}
