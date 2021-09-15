package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ReplyMessage_1_1 extends Message_1_1 implements ReplyMessage {
   private ORB orb = null;
   private ORBUtilSystemException wrapper = null;
   private ServiceContexts service_contexts = null;
   private int request_id = 0;
   private int reply_status = 0;
   private IOR ior = null;
   private String exClassName = null;
   private int minorCode = 0;
   private CompletionStatus completionStatus = null;

   ReplyMessage_1_1(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
   }

   ReplyMessage_1_1(ORB var1, ServiceContexts var2, int var3, int var4, IOR var5) {
      super(1195986768, GIOPVersion.V1_1, (byte)0, (byte)1, 0);
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.service_contexts = var2;
      this.request_id = var3;
      this.reply_status = var4;
      this.ior = var5;
   }

   public int getRequestId() {
      return this.request_id;
   }

   public int getReplyStatus() {
      return this.reply_status;
   }

   public short getAddrDisposition() {
      return 0;
   }

   public ServiceContexts getServiceContexts() {
      return this.service_contexts;
   }

   public void setServiceContexts(ServiceContexts var1) {
      this.service_contexts = var1;
   }

   public SystemException getSystemException(String var1) {
      return MessageBase.getSystemException(this.exClassName, this.minorCode, this.completionStatus, var1, this.wrapper);
   }

   public IOR getIOR() {
      return this.ior;
   }

   public void setIOR(IOR var1) {
      this.ior = var1;
   }

   public void read(InputStream var1) {
      super.read(var1);
      this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)var1);
      this.request_id = var1.read_ulong();
      this.reply_status = var1.read_long();
      isValidReplyStatus(this.reply_status);
      if (this.reply_status == 2) {
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
            throw this.wrapper.badCompletionStatusInReply((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var3));
         }
      } else if (this.reply_status != 1 && this.reply_status == 3) {
         CDRInputStream var4 = (CDRInputStream)var1;
         this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)var4);
      }

   }

   public void write(OutputStream var1) {
      super.write(var1);
      if (this.service_contexts != null) {
         this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)var1, GIOPVersion.V1_1);
      } else {
         ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)var1);
      }

      var1.write_ulong(this.request_id);
      var1.write_long(this.reply_status);
   }

   public static void isValidReplyStatus(int var0) {
      switch(var0) {
      case 0:
      case 1:
      case 2:
      case 3:
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
