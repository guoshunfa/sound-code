package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1 extends CDRInputStream_1_0 {
   protected int fragmentOffset = 0;

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_1;
   }

   public CDRInputStreamBase dup() {
      CDRInputStreamBase var1 = super.dup();
      ((CDRInputStream_1_1)var1).fragmentOffset = this.fragmentOffset;
      return var1;
   }

   protected int get_offset() {
      return this.bbwi.position() + this.fragmentOffset;
   }

   protected void alignAndCheck(int var1, int var2) {
      this.checkBlockLength(var1, var2);
      int var3 = this.computeAlignment(this.bbwi.position(), var1);
      if (this.bbwi.position() + var2 + var3 > this.bbwi.buflen) {
         if (this.bbwi.position() + var3 == this.bbwi.buflen) {
            this.bbwi.position(this.bbwi.position() + var3);
         }

         this.grow(var1, var2);
         var3 = this.computeAlignment(this.bbwi.position(), var1);
      }

      this.bbwi.position(this.bbwi.position() + var3);
   }

   protected void grow(int var1, int var2) {
      this.bbwi.needed = var2;
      int var3 = this.bbwi.position();
      this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
      if (this.bbwi.fragmented) {
         this.fragmentOffset += var3 - this.bbwi.position();
         this.markAndResetHandler.fragmentationOccured(this.bbwi);
         this.bbwi.fragmented = false;
      }

   }

   public Object createStreamMemento() {
      return new CDRInputStream_1_1.FragmentableStreamMemento();
   }

   public void restoreInternalState(Object var1) {
      super.restoreInternalState(var1);
      this.fragmentOffset = ((CDRInputStream_1_1.FragmentableStreamMemento)var1).fragmentOffset_;
   }

   public char read_wchar() {
      this.alignAndCheck(2, 2);
      char[] var1 = this.getConvertedChars(2, this.getWCharConverter());
      if (this.getWCharConverter().getNumChars() > 1) {
         throw this.wrapper.btcResultMoreThanOneChar();
      } else {
         return var1[0];
      }
   }

   public String read_wstring() {
      int var1 = this.read_long();
      if (var1 == 0) {
         return new String("");
      } else {
         this.checkForNegativeLength(var1);
         --var1;
         char[] var2 = this.getConvertedChars(var1 * 2, this.getWCharConverter());
         this.read_short();
         return new String(var2, 0, this.getWCharConverter().getNumChars());
      }
   }

   private class FragmentableStreamMemento extends CDRInputStream_1_0.StreamMemento {
      private int fragmentOffset_;

      public FragmentableStreamMemento() {
         super();
         this.fragmentOffset_ = CDRInputStream_1_1.this.fragmentOffset;
      }
   }
}
