package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public abstract class POAPolicyMediatorBase implements POAPolicyMediator {
   protected POAImpl poa;
   protected ORB orb;
   private int sysIdCounter;
   private Policies policies;
   private DelegateImpl delegateImpl;
   private int serverid;
   private int scid;
   protected boolean isImplicit;
   protected boolean isUnique;
   protected boolean isSystemId;

   public final Policies getPolicies() {
      return this.policies;
   }

   public final int getScid() {
      return this.scid;
   }

   public final int getServerId() {
      return this.serverid;
   }

   POAPolicyMediatorBase(Policies var1, POAImpl var2) {
      if (var1.isSingleThreaded()) {
         throw var2.invocationWrapper().singleThreadNotSupported();
      } else {
         POAManagerImpl var3 = (POAManagerImpl)((POAManagerImpl)var2.the_POAManager());
         POAFactory var4 = var3.getFactory();
         this.delegateImpl = (DelegateImpl)((DelegateImpl)var4.getDelegateImpl());
         this.policies = var1;
         this.poa = var2;
         this.orb = var2.getORB();
         switch(var1.servantCachingLevel()) {
         case 0:
            this.scid = 32;
            break;
         case 1:
            this.scid = 36;
            break;
         case 2:
            this.scid = 40;
            break;
         case 3:
            this.scid = 44;
         }

         if (var1.isTransient()) {
            this.serverid = this.orb.getTransientServerId();
         } else {
            this.serverid = this.orb.getORBData().getPersistentServerId();
            this.scid = ORBConstants.makePersistent(this.scid);
         }

         this.isImplicit = var1.isImplicitlyActivated();
         this.isUnique = var1.isUniqueIds();
         this.isSystemId = var1.isSystemAssignedIds();
         this.sysIdCounter = 0;
      }
   }

   public final Object getInvocationServant(byte[] var1, String var2) throws ForwardRequest {
      Object var3 = this.internalGetServant(var1, var2);
      return var3;
   }

   protected final void setDelegate(Servant var1, byte[] var2) {
      var1._set_delegate(this.delegateImpl);
   }

   public synchronized byte[] newSystemId() throws WrongPolicy {
      if (!this.isSystemId) {
         throw new WrongPolicy();
      } else {
         byte[] var1 = new byte[8];
         ORBUtility.intToBytes(++this.sysIdCounter, var1, 0);
         ORBUtility.intToBytes(this.poa.getPOAId(), var1, 4);
         return var1;
      }
   }

   protected abstract Object internalGetServant(byte[] var1, String var2) throws ForwardRequest;
}
