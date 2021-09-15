package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_2 extends Message_1_2 implements LocateRequestMessage {
   private ORB orb = null;
   private ObjectKey objectKey = null;
   private TargetAddress target = null;

   LocateRequestMessage_1_2(ORB var1) {
      this.orb = var1;
   }

   LocateRequestMessage_1_2(ORB var1, int var2, TargetAddress var3) {
      super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)3, 0);
      this.orb = var1;
      this.request_id = var2;
      this.target = var3;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public ObjectKey getObjectKey() {
      if (this.objectKey == null) {
         this.objectKey = MessageBase.extractObjectKey(this.target, this.orb);
      }

      return this.objectKey;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
      this.target = TargetAddressHelper.read(var1);
      this.getObjectKey();
   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
      nullCheck(this.target);
      TargetAddressHelper.write(var1, this.target);
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
