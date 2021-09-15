package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_1 extends Message_1_1 implements LocateRequestMessage {
   private ORB orb = null;
   private int request_id = 0;
   private byte[] object_key = null;
   private ObjectKey objectKey = null;

   LocateRequestMessage_1_1(ORB var1) {
      this.orb = var1;
   }

   LocateRequestMessage_1_1(ORB var1, int var2, byte[] var3) {
      super(1195986768, GIOPVersion.V1_1, (byte)0, (byte)3, 0);
      this.orb = var1;
      this.request_id = var2;
      this.object_key = var3;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public ObjectKey getObjectKey() {
      if (this.objectKey == null) {
         this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb);
      }

      return this.objectKey;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
      int var2 = var1.read_long();
      this.object_key = new byte[var2];
      var1.read_octet_array(this.object_key, 0, var2);
   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
      nullCheck(this.object_key);
      var1.write_long(this.object_key.length);
      var1.write_octet_array(this.object_key, 0, this.object_key.length);
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
