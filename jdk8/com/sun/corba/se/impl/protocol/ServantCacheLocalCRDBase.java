package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;

public abstract class ServantCacheLocalCRDBase extends LocalClientRequestDispatcherBase {
   private OAInvocationInfo cachedInfo;
   protected POASystemException wrapper;

   protected ServantCacheLocalCRDBase(ORB var1, int var2, IOR var3) {
      super(var1, var2, var3);
      this.wrapper = POASystemException.get(var1, "rpc.protocol");
   }

   protected synchronized OAInvocationInfo getCachedInfo() {
      if (!this.servantIsLocal) {
         throw this.wrapper.servantMustBeLocal();
      } else {
         if (this.cachedInfo == null) {
            ObjectAdapter var1 = this.oaf.find(this.oaid);
            this.cachedInfo = var1.makeInvocationInfo(this.objectId);
            this.orb.pushInvocationInfo(this.cachedInfo);

            try {
               var1.enter();
               var1.getInvocationServant(this.cachedInfo);
            } catch (ForwardException var7) {
               throw this.wrapper.illegalForwardRequest((Throwable)var7);
            } catch (OADestroyed var8) {
               throw this.wrapper.adapterDestroyed((Throwable)var8);
            } finally {
               var1.returnServant();
               var1.exit();
               this.orb.popInvocationInfo();
            }
         }

         return this.cachedInfo;
      }
   }
}
