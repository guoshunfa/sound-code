package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class RequestMessage_1_2 extends Message_1_2 implements RequestMessage {
   private ORB orb = null;
   private ORBUtilSystemException wrapper = null;
   private byte response_flags = 0;
   private byte[] reserved = null;
   private TargetAddress target = null;
   private String operation = null;
   private ServiceContexts service_contexts = null;
   private ObjectKey objectKey = null;

   RequestMessage_1_2(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
   }

   RequestMessage_1_2(ORB var1, int var2, byte var3, byte[] var4, TargetAddress var5, String var6, ServiceContexts var7) {
      super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)0, 0);
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.request_id = var2;
      this.response_flags = var3;
      this.reserved = var4;
      this.target = var5;
      this.operation = var6;
      this.service_contexts = var7;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public boolean isResponseExpected() {
      return (this.response_flags & 1) == 1;
   }

   public byte[] getReserved() {
      return this.reserved;
   }

   public ObjectKey getObjectKey() {
      if (this.objectKey == null) {
         this.objectKey = MessageBase.extractObjectKey(this.target, this.orb);
      }

      return this.objectKey;
   }

   public String getOperation() {
      return this.operation;
   }

   public Principal getPrincipal() {
      return null;
   }

   public ServiceContexts getServiceContexts() {
      return this.service_contexts;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
      this.response_flags = var1.read_octet();
      this.reserved = new byte[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         this.reserved[var2] = var1.read_octet();
      }

      this.target = TargetAddressHelper.read(var1);
      this.getObjectKey();
      this.operation = var1.read_string();
      this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)var1);
      ((CDRInputStream)var1).setHeaderPadding(true);
   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
      var1.write_octet(this.response_flags);
      nullCheck(this.reserved);
      if (this.reserved.length != 3) {
         throw this.wrapper.badReservedLength(CompletionStatus.COMPLETED_MAYBE);
      } else {
         for(int var2 = 0; var2 < 3; ++var2) {
            var1.write_octet(this.reserved[var2]);
         }

         nullCheck(this.target);
         TargetAddressHelper.write(var1, this.target);
         var1.write_string(this.operation);
         if (this.service_contexts != null) {
            this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)var1, GIOPVersion.V1_2);
         } else {
            ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)var1);
         }

         ((CDROutputStream)var1).setHeaderPadding(true);
      }
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
