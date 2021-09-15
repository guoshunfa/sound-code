package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class POAPolicyMediatorImpl_NR_USM extends POAPolicyMediatorBase {
   private ServantLocator locator;

   POAPolicyMediatorImpl_NR_USM(Policies var1, POAImpl var2) {
      super(var1, var2);
      if (var1.retainServants()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      } else if (!var1.useServantManager()) {
         throw var2.invocationWrapper().policyMediatorBadPolicyInFactory();
      } else {
         this.locator = null;
      }
   }

   protected Object internalGetServant(byte[] var1, String var2) throws ForwardRequest {
      if (this.locator == null) {
         throw this.poa.invocationWrapper().poaNoServantManager();
      } else {
         CookieHolder var3 = this.orb.peekInvocationInfo().getCookieHolder();

         Object var4;
         try {
            this.poa.unlock();
            var4 = this.locator.preinvoke(var1, this.poa, var2, var3);
            if (var4 == null) {
               var4 = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned());
            } else {
               this.setDelegate((Servant)var4, var1);
            }
         } finally {
            this.poa.lock();
         }

         return var4;
      }
   }

   public void returnServant() {
      OAInvocationInfo var1 = this.orb.peekInvocationInfo();
      if (this.locator != null) {
         try {
            this.poa.unlock();
            this.locator.postinvoke(var1.id(), (POA)((POA)var1.oa()), var1.getOperation(), var1.getCookieHolder().value, (Servant)((Servant)var1.getServantContainer()));
         } finally {
            this.poa.lock();
         }

      }
   }

   public void etherealizeAll() {
   }

   public void clearAOM() {
   }

   public ServantManager getServantManager() throws WrongPolicy {
      return this.locator;
   }

   public void setServantManager(ServantManager var1) throws WrongPolicy {
      if (this.locator != null) {
         throw this.poa.invocationWrapper().servantManagerAlreadySet();
      } else if (var1 instanceof ServantLocator) {
         this.locator = (ServantLocator)var1;
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
      throw new WrongPolicy();
   }
}
