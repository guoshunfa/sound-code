package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_2 extends Message_1_2 implements FragmentMessage {
   FragmentMessage_1_2() {
   }

   FragmentMessage_1_2(int var1) {
      super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)7, 0);
      this.message_type = 7;
      this.request_id = var1;
   }

   FragmentMessage_1_2(Message_1_1 var1) {
      this.magic = var1.magic;
      this.GIOP_version = var1.GIOP_version;
      this.flags = var1.flags;
      this.message_type = 7;
      this.message_size = 0;
      switch(var1.message_type) {
      case 0:
         this.request_id = ((RequestMessage)var1).getRequestId();
         break;
      case 1:
         this.request_id = ((ReplyMessage)var1).getRequestId();
      case 2:
      case 5:
      case 6:
      default:
         break;
      case 3:
         this.request_id = ((LocateRequestMessage)var1).getRequestId();
         break;
      case 4:
         this.request_id = ((LocateReplyMessage)var1).getRequestId();
         break;
      case 7:
         this.request_id = ((FragmentMessage)var1).getRequestId();
      }

   }

   public int getRequestId() {
      return this.request_id;
   }

   public int getHeaderLength() {
      return 16;
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
      var1.handleInput(this);
   }
}
