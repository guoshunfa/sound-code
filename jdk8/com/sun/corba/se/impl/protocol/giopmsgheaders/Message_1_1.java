package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_1 extends MessageBase {
   static final int UPPER_THREE_BYTES_OF_INT_MASK = 255;
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
   int magic = 0;
   GIOPVersion GIOP_version = null;
   byte flags = 0;
   byte message_type = 0;
   int message_size = 0;

   Message_1_1() {
   }

   Message_1_1(int var1, GIOPVersion var2, byte var3, byte var4, int var5) {
      this.magic = var1;
      this.GIOP_version = var2;
      this.flags = var3;
      this.message_type = var4;
      this.message_size = var5;
   }

   public GIOPVersion getGIOPVersion() {
      return this.GIOP_version;
   }

   public int getType() {
      return this.message_type;
   }

   public int getSize() {
      return this.message_size;
   }

   public boolean isLittleEndian() {
      return (this.flags & 1) == 1;
   }

   public boolean moreFragmentsToFollow() {
      return (this.flags & 2) == 2;
   }

   public void setThreadPoolToUse(int var1) {
      int var2 = var1 << 2;
      var2 &= 255;
      var2 |= this.flags;
      this.flags = (byte)var2;
   }

   public void setSize(ByteBuffer var1, int var2) {
      this.message_size = var2;
      int var3 = var2 - 12;
      if (!this.isLittleEndian()) {
         var1.put(8, (byte)(var3 >>> 24 & 255));
         var1.put(9, (byte)(var3 >>> 16 & 255));
         var1.put(10, (byte)(var3 >>> 8 & 255));
         var1.put(11, (byte)(var3 >>> 0 & 255));
      } else {
         var1.put(8, (byte)(var3 >>> 0 & 255));
         var1.put(9, (byte)(var3 >>> 8 & 255));
         var1.put(10, (byte)(var3 >>> 16 & 255));
         var1.put(11, (byte)(var3 >>> 24 & 255));
      }

   }

   public FragmentMessage createFragmentMessage() {
      switch(this.message_type) {
      case 2:
      case 5:
      case 6:
         throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
      case 3:
      case 4:
         if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
            throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
         }
      default:
         if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
            return new FragmentMessage_1_1(this);
         } else if (this.GIOP_version.equals(GIOPVersion.V1_2)) {
            return new FragmentMessage_1_2(this);
         } else {
            throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
         }
      }
   }

   public void read(InputStream var1) {
   }

   public void write(OutputStream var1) {
      var1.write_long(this.magic);
      nullCheck(this.GIOP_version);
      this.GIOP_version.write(var1);
      var1.write_octet(this.flags);
      var1.write_octet(this.message_type);
      var1.write_ulong(this.message_size);
   }
}
