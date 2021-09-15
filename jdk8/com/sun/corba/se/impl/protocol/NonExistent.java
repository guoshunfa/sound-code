package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.portable.OutputStream;

class NonExistent extends SpecialMethod {
   public boolean isNonExistentMethod() {
      return true;
   }

   public String getName() {
      return "_non_existent";
   }

   public CorbaMessageMediator invoke(Object var1, CorbaMessageMediator var2, byte[] var3, ObjectAdapter var4) {
      boolean var5 = var1 == null || var1 instanceof NullServant;
      CorbaMessageMediator var6 = var2.getProtocolHandler().createResponse(var2, (ServiceContexts)null);
      ((OutputStream)var6.getOutputObject()).write_boolean(var5);
      return var6;
   }
}
