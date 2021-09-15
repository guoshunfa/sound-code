package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;

public class INSServerRequestDispatcher implements CorbaServerRequestDispatcher {
   private ORB orb = null;
   private ORBUtilSystemException wrapper;

   public INSServerRequestDispatcher(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
   }

   public IOR locate(ObjectKey var1) {
      String var2 = new String(var1.getBytes(this.orb));
      return this.getINSReference(var2);
   }

   public void dispatch(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      String var3 = new String(var2.getObjectKey().getBytes(this.orb));
      var2.getProtocolHandler().createLocationForward(var2, this.getINSReference(var3), (ServiceContexts)null);
   }

   private IOR getINSReference(String var1) {
      IOR var2 = ORBUtility.getIOR(this.orb.getLocalResolver().resolve(var1));
      if (var2 != null) {
         return var2;
      } else {
         throw this.wrapper.servantNotFound();
      }
   }
}
