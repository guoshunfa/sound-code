package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_2 extends CDRInputStream_1_1 {
   protected boolean headerPadding;
   protected boolean restoreHeaderPadding;

   void setHeaderPadding(boolean var1) {
      this.headerPadding = var1;
   }

   public void mark(int var1) {
      super.mark(var1);
      this.restoreHeaderPadding = this.headerPadding;
   }

   public void reset() {
      super.reset();
      this.headerPadding = this.restoreHeaderPadding;
      this.restoreHeaderPadding = false;
   }

   public CDRInputStreamBase dup() {
      CDRInputStreamBase var1 = super.dup();
      ((CDRInputStream_1_2)var1).headerPadding = this.headerPadding;
      return var1;
   }

   protected void alignAndCheck(int var1, int var2) {
      if (this.headerPadding) {
         this.headerPadding = false;
         this.alignOnBoundary(8);
      }

      this.checkBlockLength(var1, var2);
      int var3 = this.computeAlignment(this.bbwi.position(), var1);
      this.bbwi.position(this.bbwi.position() + var3);
      if (this.bbwi.position() + var2 > this.bbwi.buflen) {
         this.grow(1, var2);
      }

   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_2;
   }

   public char read_wchar() {
      byte var1 = this.read_octet();
      char[] var2 = this.getConvertedChars(var1, this.getWCharConverter());
      if (this.getWCharConverter().getNumChars() > 1) {
         throw this.wrapper.btcResultMoreThanOneChar();
      } else {
         return var2[0];
      }
   }

   public String read_wstring() {
      int var1 = this.read_long();
      if (var1 == 0) {
         return new String("");
      } else {
         this.checkForNegativeLength(var1);
         return new String(this.getConvertedChars(var1, this.getWCharConverter()), 0, this.getWCharConverter().getNumChars());
      }
   }
}
