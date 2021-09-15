package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateReplyMessage_1_2 extends Message_1_2 implements LocateReplyMessage {
   private ORB orb = null;
   private ORBUtilSystemException wrapper = null;
   private int reply_status = 0;
   private IOR ior = null;
   private String exClassName = null;
   private int minorCode = 0;
   private CompletionStatus completionStatus = null;
   private short addrDisposition = 0;

   LocateReplyMessage_1_2(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
   }

   LocateReplyMessage_1_2(ORB var1, int var2, int var3, IOR var4) {
      super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)4, 0);
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.request_id = var2;
      this.reply_status = var3;
      this.ior = var4;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public int getReplyStatus() {
      return this.reply_status;
   }

   public short getAddrDisposition() {
      return this.addrDisposition;
   }

   public SystemException getSystemException(String var1) {
      return MessageBase.getSystemException(this.exClassName, this.minorCode, this.completionStatus, var1, this.wrapper);
   }

   public IOR getIOR() {
      return this.ior;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.request_id = var1.read_ulong();
      this.reply_status = var1.read_long();
      isValidReplyStatus(this.reply_status);
      if (this.reply_status == 4) {
         String var2 = var1.read_string();
         this.exClassName = ORBUtility.classNameOf(var2);
         this.minorCode = var1.read_long();
         int var3 = var1.read_long();
         switch(var3) {
         case 0:
            this.completionStatus = CompletionStatus.COMPLETED_YES;
            break;
         case 1:
            this.completionStatus = CompletionStatus.COMPLETED_NO;
            break;
         case 2:
            this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
            break;
         default:
            throw this.wrapper.badCompletionStatusInLocateReply((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var3));
         }
      } else if (this.reply_status != 2 && this.reply_status != 3) {
         if (this.reply_status == 5) {
            this.addrDisposition = AddressingDispositionHelper.read(var1);
         }
      } else {
         CDRInputStream var4 = (CDRInputStream)var1;
         this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)var4);
      }

   }

   public void write(OutputStream var1) {
      super.write(var1);
      var1.write_ulong(this.request_id);
      var1.write_long(this.reply_status);
   }

   public static void isValidReplyStatus(int var0) {
      switch(var0) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
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
