package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateReplyMessage_1_0 extends Message_1_0 implements LocateReplyMessage {
   private ORB orb = null;
   private int request_id = 0;
   private int locate_status = 0;
   private IOR ior = null;

   LocateReplyMessage_1_0(ORB var1) {
      this.orb = var1;
   }

   LocateReplyMessage_1_0(ORB var1, int var2, int var3, IOR var4) {
      super(1195986768, false, (byte)4, 0);
      this.orb = var1;
      this.request_id = var2;
      this.locate_status = var3;
      this.ior = var4;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public int getReplyStatus() {
      return this.locate_status;
   }

   public short getAddrDisposition() {
      return 0;
   }

   public SystemException getSystemException(String var1) {
      return null;
   }

   public IOR getIOR() {
      return this.ior;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
      this.locate_status = var1.read_long();
      isValidReplyStatus(this.locate_status);
      if (this.locate_status == 2) {
         CDRInputStream var2 = (CDRInputStream)var1;
         this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)var2);
      }

   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
      var1.write_long(this.locate_status);
   }

   public static void isValidReplyStatus(int var0) {
      switch(var0) {
      case 0:
      case 1:
      case 2:
         return;
      default:
         ORBUtilSystemException var1 = ORBUtilSystemException.get("rpc.protocol");
         throw var1.illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput(this);
   }
}
