package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class FullServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase {
   public FullServantCacheLocalCRDImpl(ORB var1, int var2, IOR var3) {
      super(var1, var2, var3);
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      OAInvocationInfo var4 = this.getCachedInfo();
      if (!this.checkForCompatibleServant(var4, var3)) {
         return null;
      } else {
         OAInvocationInfo var5 = new OAInvocationInfo(var4, var2);
         this.orb.pushInvocationInfo(var5);

         try {
            var5.oa().enter();
            return var5;
         } catch (OADestroyed var7) {
            throw this.wrapper.preinvokePoaDestroyed((Throwable)var7);
         }
      }
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
      OAInvocationInfo var3 = this.getCachedInfo();
      var3.oa().exit();
      this.orb.popInvocationInfo();
   }
}
