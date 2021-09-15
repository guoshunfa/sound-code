package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_2 extends Message_1_1 {
   protected int request_id = 0;

   Message_1_2() {
   }

   Message_1_2(int var1, GIOPVersion var2, byte var3, byte var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   public void unmarshalRequestID(ByteBuffer var1) {
      int var2;
      int var3;
      int var4;
      int var5;
      if (!this.isLittleEndian()) {
         var2 = var1.get(12) << 24 & -16777216;
         var3 = var1.get(13) << 16 & 16711680;
         var4 = var1.get(14) << 8 & '\uff00';
         var5 = var1.get(15) << 0 & 255;
      } else {
         var2 = var1.get(15) << 24 & -16777216;
         var3 = var1.get(14) << 16 & 16711680;
         var4 = var1.get(13) << 8 & '\uff00';
         var5 = var1.get(12) << 0 & 255;
      }

      this.request_id = var2 | var3 | var4 | var5;
   }

   public void write(OutputStream var1) {
      if (this.encodingVersion == 0) {
         super.write(var1);
      } else {
         GIOPVersion var2 = this.GIOP_version;
         this.GIOP_version = GIOPVersion.getInstance((byte)13, this.encodingVersion);
         super.write(var1);
         this.GIOP_version = var2;
      }
   }
}
