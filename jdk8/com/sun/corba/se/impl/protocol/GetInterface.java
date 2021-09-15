package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;

class GetInterface extends SpecialMethod {
   public boolean isNonExistentMethod() {
      return false;
   }

   public String getName() {
      return "_interface";
   }

   public CorbaMessageMediator invoke(Object var1, CorbaMessageMediator var2, byte[] var3, ObjectAdapter var4) {
      ORB var5 = (ORB)var2.getBroker();
      ORBUtilSystemException var6 = ORBUtilSystemException.get(var5, "oa.invocation");
      return var1 != null && !(var1 instanceof NullServant) ? var2.getProtocolHandler().createSystemExceptionResponse(var2, var6.getinterfaceNotImplemented(), (ServiceContexts)null) : var2.getProtocolHandler().createSystemExceptionResponse(var2, var6.badSkeleton(), (ServiceContexts)null);
   }
}
