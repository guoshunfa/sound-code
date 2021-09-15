package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_1 extends CDROutputStream_1_0 {
   protected int fragmentOffset = 0;

   protected void alignAndReserve(int var1, int var2) {
      int var3 = this.computeAlignment(var1);
      if (this.bbwi.position() + var2 + var3 > this.bbwi.buflen) {
         this.grow(var1, var2);
         var3 = this.computeAlignment(var1);
      }

      this.bbwi.position(this.bbwi.position() + var3);
   }

   protected void grow(int var1, int var2) {
      int var3 = this.bbwi.position();
      super.grow(var1, var2);
      if (this.bbwi.fragmented) {
         this.bbwi.fragmented = false;
         this.fragmentOffset += var3 - this.bbwi.position();
      }

   }

   public int get_offset() {
      return this.bbwi.position() + this.fragmentOffset;
   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_1;
   }

   public void write_wchar(char var1) {
      CodeSetConversion.CTBConverter var2 = this.getWCharConverter();
      var2.convert(var1);
      if (var2.getNumBytes() != 2) {
         throw this.wrapper.badGiop11Ctb(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.alignAndReserve(var2.getAlignment(), var2.getNumBytes());
         this.parent.write_octet_array(var2.getBytes(), 0, var2.getNumBytes());
      }
   }

   public void write_wstring(String var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         int var2 = var1.length() + 1;
         this.write_long(var2);
         CodeSetConversion.CTBConverter var3 = this.getWCharConverter();
         var3.convert(var1);
         this.internalWriteOctetArray(var3.getBytes(), 0, var3.getNumBytes());
         this.write_short((short)0);
      }
   }
}
