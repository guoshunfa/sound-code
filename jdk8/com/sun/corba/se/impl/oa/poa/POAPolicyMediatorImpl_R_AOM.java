package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class POAPolicyMediatorImpl_R_AOM extends POAPolicyMediatorBase_R {
   POAPolicyMediatorImpl_R_AOM(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (!var1.useActiveMapOnly()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      }
   }

   protected Object internalGetServant(byte[] var1, String var2) throws ForwardRequest {
      Object var3 = this.internalIdToServant(var1);
      if (var3 == null) {
         var3 = new NullServantImpl(this.poa.invocationWrapper().nullServant());
      }

      return var3;
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
      throw new WrongPolicy();
   }

   public void setDefaultServant(Servant var1) throws WrongPolicy {
      throw new WrongPolicy();
   }

   public Servant idToServant(byte[] var1) throws WrongPolicy, ObjectNotActive {
      Servant var2 = this.internalIdToServant(var1);
      if (var2 == null) {
         throw new ObjectNotActive();
      } else {
         return var2;
      }
   }
}
