package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

class IsA extends SpecialMethod {
   public boolean isNonExistentMethod() {
      return false;
   }

   public String getName() {
      return "_is_a";
   }

   public CorbaMessageMediator invoke(Object var1, CorbaMessageMediator var2, byte[] var3, ObjectAdapter var4) {
      if (var1 != null && !(var1 instanceof NullServant)) {
         String[] var9 = var4.getInterfaces(var1, var3);
         String var10 = ((InputStream)var2.getInputObject()).read_string();
         boolean var7 = false;

         for(int var8 = 0; var8 < var9.length; ++var8) {
            if (var9[var8].equals(var10)) {
               var7 = true;
               break;
            }
         }

         CorbaMessageMediator var11 = var2.getProtocolHandler().createResponse(var2, (ServiceContexts)null);
         ((OutputStream)var11.getOutputObject()).write_boolean(var7);
         return var11;
      } else {
         ORB var5 = (ORB)var2.getBroker();
         ORBUtilSystemException var6 = ORBUtilSystemException.get(var5, "oa.invocation");
         return var2.getProtocolHandler().createSystemExceptionResponse(var2, var6.badSkeleton(), (ServiceContexts)null);
      }
   }
}
