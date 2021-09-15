package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public class SharedCDRClientRequestDispatcherImpl extends CorbaClientRequestDispatcherImpl {
   public InputObject marshalingComplete(Object var1, OutputObject var2) throws ApplicationException, RemarshalException {
      final ORB var3 = null;
      CorbaMessageMediator var4 = null;

      InputObject var15;
      try {
         var4 = (CorbaMessageMediator)var2.getMessageMediator();
         var3 = (ORB)var4.getBroker();
         if (var3.subcontractDebugFlag) {
            this.dprint(".marshalingComplete->: " + this.opAndId(var4));
         }

         CDROutputObject var5 = (CDROutputObject)var2;
         ByteBufferWithInfo var6 = var5.getByteBufferWithInfo();
         var5.getMessageHeader().setSize(var6.byteBuffer, var6.getSize());
         final ByteBuffer var8 = var6.byteBuffer;
         final Message var9 = var5.getMessageHeader();
         CDRInputObject var10 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction<CDRInputObject>() {
            public CDRInputObject run() {
               return new CDRInputObject(var3, (CorbaConnection)null, var8, var9);
            }
         });
         var4.setInputObject(var10);
         var10.setMessageMediator(var4);
         ((CorbaMessageMediatorImpl)var4).handleRequestRequest(var4);

         try {
            var10.close();
         } catch (IOException var19) {
            if (var3.transportDebugFlag) {
               this.dprint(".marshalingComplete: ignoring IOException - " + var19.toString());
            }
         }

         var5 = (CDROutputObject)var4.getOutputObject();
         var6 = var5.getByteBufferWithInfo();
         var5.getMessageHeader().setSize(var6.byteBuffer, var6.getSize());
         final ByteBuffer var12 = var6.byteBuffer;
         final Message var13 = var5.getMessageHeader();
         var10 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction<CDRInputObject>() {
            public CDRInputObject run() {
               return new CDRInputObject(var3, (CorbaConnection)null, var12, var13);
            }
         });
         var4.setInputObject(var10);
         var10.setMessageMediator(var4);
         var10.unmarshalHeader();
         var15 = this.processResponse(var3, var4, var10);
      } finally {
         if (var3.subcontractDebugFlag) {
            this.dprint(".marshalingComplete<-: " + this.opAndId(var4));
         }

      }

      return var15;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", var1);
   }
}
