package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class RequestMessage_1_0 extends Message_1_0 implements RequestMessage {
   private ORB orb = null;
   private ServiceContexts service_contexts = null;
   private int request_id = 0;
   private boolean response_expected = false;
   private byte[] object_key = null;
   private String operation = null;
   private Principal requesting_principal = null;
   private ObjectKey objectKey = null;

   RequestMessage_1_0(ORB var1) {
      this.orb = var1;
   }

   RequestMessage_1_0(ORB var1, ServiceContexts var2, int var3, boolean var4, byte[] var5, String var6, Principal var7) {
      super(1195986768, false, (byte)0, 0);
      this.orb = var1;
      this.service_contexts = var2;
      this.request_id = var3;
      this.response_expected = var4;
      this.object_key = var5;
      this.operation = var6;
      this.requesting_principal = var7;
   }

   public ServiceContexts getServiceContexts() {
      return this.service_contexts;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public boolean isResponseExpected() {
      return this.response_expected;
   }

   public byte[] getReserved() {
      return null;
   }

   public ObjectKey getObjectKey() {
      if (this.objectKey == null) {
         this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb);
      }

      return this.objectKey;
   }

   public String getOperation() {
      return this.operation;
   }

   public Principal getPrincipal() {
      return this.requesting_principal;
   }

   public void setThreadPoolToUse(int var1) {
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)var1);
      this.request_id = var1.read_ulong();
      this.response_expected = var1.read_boolean();
      int var2 = var1.read_long();
      this.object_key = new byte[var2];
      var1.read_octet_array(this.object_key, 0, var2);
      this.operation = var1.read_string();
      this.requesting_principal = var1.read_Principal();
   }

   public void write(OutputStream var1) {
      super.write(var1);
      if (this.service_contexts != null) {
         this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)var1, GIOPVersion.V1_0);
      } else {
         ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)var1);
      }

      var1.write_ulong(this.request_id);
      var1.write_boolean(this.response_expected);
      nullCheck(this.object_key);
      var1.write_long(this.object_key.length);
      var1.write_octet_array(this.object_key, 0, this.object_key.length);
      var1.write_string(this.operation);
      if (this.requesting_principal != null) {
         var1.write_Principal(this.requesting_principal);
      } else {
         var1.write_long(0);
      }

   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
