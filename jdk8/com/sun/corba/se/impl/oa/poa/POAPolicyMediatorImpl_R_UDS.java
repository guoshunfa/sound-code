package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class POAPolicyMediatorImpl_R_UDS extends POAPolicyMediatorBase_R {
   private Servant defaultServant = null;

   POAPolicyMediatorImpl_R_UDS(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (!var1.useDefaultServant()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      }
   }

   protected Object internalGetServant(byte[] var1, String var2) throws ForwardRequest {
      Servant var3 = this.internalIdToServant(var1);
      if (var3 == null) {
         var3 = this.defaultServant;
      }

      if (var3 == null) {
         throw this.poa.invocationWrapper().poaNoDefaultServant();
      } else {
         return var3;
      }
   }

   public void etherealizeAll() {
   }

   public ServantManager getServantManager() throws WrongPolicy {
      throw new WrongPolicy();
   }

   public void setServantManager(ServantManager var1) throws WrongPolicy {
      throw new WrongPolicy();
   }

   public Servant getDefaultServant() throws NoServant, WrongPolicy {
      if (this.defaultServant == null) {
         throw new NoServant();
      } else {
         return this.defaultServant;
      }
   }

   public void setDefaultServant(Servant var1) throws WrongPolicy {
      this.defaultServant = var1;
      this.setDelegate(this.defaultServant, "DefaultServant".getBytes());
   }

   public Servant idToServant(byte[] var1) throws WrongPolicy, ObjectNotActive {
      ActiveObjectMap.Key var2 = new ActiveObjectMap.Key(var1);
      Servant var3 = this.internalKeyToServant(var2);
      if (var3 == null && this.defaultServant != null) {
         var3 = this.defaultServant;
      }

      if (var3 == null) {
         throw new ObjectNotActive();
      } else {
         return var3;
      }
   }
}
