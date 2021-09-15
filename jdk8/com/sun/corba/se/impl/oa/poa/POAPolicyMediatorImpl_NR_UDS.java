package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class POAPolicyMediatorImpl_NR_UDS extends POAPolicyMediatorBase {
   private Servant defaultServant;

   POAPolicyMediatorImpl_NR_UDS(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (var1.retainServants()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      } else if (!var1.useDefaultServant()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      } else {
         this.defaultServant = null;
      }
   }

   protected Object internalGetServant(byte[] var1, String var2) throws ForwardRequest {
      if (this.defaultServant == null) {
         throw this.poa.invocationWrapper().poaNoDefaultServant();
      } else {
         return this.defaultServant;
      }
   }

   public void returnServant() {
   }

   public void etherealizeAll() {
   }

   public void clearAOM() {
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

   public final void activateObject(byte[] var1, Servant var2) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive {
      throw new WrongPolicy();
   }

   public Servant deactivateObject(byte[] var1) throws ObjectNotActive, WrongPolicy {
      throw new WrongPolicy();
   }

   public byte[] servantToId(Servant var1) throws ServantNotActive, WrongPolicy {
      throw new WrongPolicy();
   }

   public Servant idToServant(byte[] var1) throws WrongPolicy, ObjectNotActive {
      if (this.defaultServant != null) {
         return this.defaultServant;
      } else {
         throw new ObjectNotActive();
      }
   }
}
