package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class MinimalServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase {
   public MinimalServantCacheLocalCRDImpl(ORB var1, int var2, IOR var3) {
      super(var1, var2, var3);
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      OAInvocationInfo var4 = this.getCachedInfo();
      return this.checkForCompatibleServant(var4, var3) ? var4 : null;
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
   }
}
