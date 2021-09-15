package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_0 extends MessageBase {
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
   int magic = 0;
   GIOPVersion GIOP_version = null;
   boolean byte_order = false;
   byte message_type = 0;
   int message_size = 0;

   Message_1_0() {
   }

   Message_1_0(int var1, boolean var2, byte var3, int var4) {
      this.magic = var1;
      this.GIOP_version = GIOPVersion.V1_0;
      this.byte_order = var2;
      this.message_type = var3;
      this.message_size = var4;
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
      return this.byte_order;
   }

   public boolean moreFragmentsToFollow() {
      return false;
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
      throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
   }

   public void read(InputStream var1) {
   }

   public void write(OutputStream var1) {
      var1.write_long(this.magic);
      nullCheck(this.GIOP_version);
      this.GIOP_version.write(var1);
      var1.write_boolean(this.byte_order);
      var1.write_octet(this.message_type);
      var1.write_ulong(this.message_size);
   }
}
