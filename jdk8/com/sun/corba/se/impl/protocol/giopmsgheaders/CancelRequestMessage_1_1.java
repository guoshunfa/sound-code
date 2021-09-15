package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class CancelRequestMessage_1_1 extends Message_1_1 implements CancelRequestMessage {
   private int request_id = 0;

   CancelRequestMessage_1_1() {
   }

   CancelRequestMessage_1_1(int var1) {
      super(1195986768, GIOPVersion.V1_1, (byte)0, (byte)2, 4);
      this.request_id = var1;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput((CancelRequestMessage)this);
   }
}
