package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;

public class BootstrapServerRequestDispatcher implements CorbaServerRequestDispatcher {
   private ORB orb;
   ORBUtilSystemException wrapper;
   private static final boolean debug = false;

   public BootstrapServerRequestDispatcher(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
   }

   public void dispatch(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      CorbaMessageMediator var3 = null;

      BAD_PARAM var5;
      try {
         MarshalInputStream var4 = (MarshalInputStream)var2.getInputObject();
         String var13 = var2.getOperationName();
         var3 = var2.getProtocolHandler().createResponse(var2, (ServiceContexts)null);
         MarshalOutputStream var6 = (MarshalOutputStream)var3.getOutputObject();
         if (var13.equals("get")) {
            String var7 = var4.read_string();
            Object var8 = this.orb.getLocalResolver().resolve(var7);
            var6.write_Object(var8);
         } else {
            if (!var13.equals("list")) {
               throw this.wrapper.illegalBootstrapOperation(var13);
            }

            Set var14 = this.orb.getLocalResolver().list();
            var6.write_long(var14.size());
            Iterator var15 = var14.iterator();

            while(var15.hasNext()) {
               String var9 = (String)var15.next();
               var6.write_string(var9);
            }
         }
      } catch (SystemException var10) {
         var3 = var2.getProtocolHandler().createSystemExceptionResponse(var2, var10, (ServiceContexts)null);
      } catch (RuntimeException var11) {
         var5 = this.wrapper.bootstrapRuntimeException((Throwable)var11);
         var2.getProtocolHandler().createSystemExceptionResponse(var2, var5, (ServiceContexts)null);
      } catch (Exception var12) {
         var5 = this.wrapper.bootstrapException((Throwable)var12);
         var2.getProtocolHandler().createSystemExceptionResponse(var2, var5, (ServiceContexts)null);
      }

   }

   public IOR locate(ObjectKey var1) {
      return null;
   }

   public int getId() {
      throw this.wrapper.genericNoImpl();
   }
}
