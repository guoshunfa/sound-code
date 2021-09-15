package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_1 extends Message_1_1 implements FragmentMessage {
   FragmentMessage_1_1() {
   }

   FragmentMessage_1_1(Message_1_1 var1) {
      this.magic = var1.magic;
      this.GIOP_version = var1.GIOP_version;
      this.flags = var1.flags;
      this.message_type = 7;
      this.message_size = 0;
   }

   public int getRequestId() {
      return -1;
   }

   public int getHeaderLength() {
      return 12;
   }

   public void read(InputStream var1) {
      super.read(var1);
   }

   public void write(OutputStream var1) {
      super.write(var1);
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
