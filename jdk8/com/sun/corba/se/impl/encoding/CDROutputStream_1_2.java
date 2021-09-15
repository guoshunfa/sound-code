package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_2 extends CDROutputStream_1_1 {
   protected boolean primitiveAcrossFragmentedChunk = false;
   protected boolean specialChunk = false;
   private boolean headerPadding;

   protected void handleSpecialChunkBegin(int var1) {
      if (this.inBlock && var1 + this.bbwi.position() > this.bbwi.buflen) {
         int var2 = this.bbwi.position();
         this.bbwi.position(this.blockSizeIndex - 4);
         this.writeLongWithoutAlign(var2 - this.blockSizeIndex + var1);
         this.bbwi.position(var2);
         this.specialChunk = true;
      }

   }

   protected void handleSpecialChunkEnd() {
      if (this.inBlock && this.specialChunk) {
         this.inBlock = false;
         this.blockSizeIndex = -1;
         this.blockSizePosition = -1;
         this.start_block();
         this.specialChunk = false;
      }

   }

   private void checkPrimitiveAcrossFragmentedChunk() {
      if (this.primitiveAcrossFragmentedChunk) {
         this.primitiveAcrossFragmentedChunk = false;
         this.inBlock = false;
         this.blockSizeIndex = -1;
         this.blockSizePosition = -1;
         this.start_block();
      }

   }

   public void write_octet(byte var1) {
      super.write_octet(var1);
      this.checkPrimitiveAcrossFragmentedChunk();
   }

   public void write_short(short var1) {
      super.write_short(var1);
      this.checkPrimitiveAcrossFragmentedChunk();
   }

   public void write_long(int var1) {
      super.write_long(var1);
      this.checkPrimitiveAcrossFragmentedChunk();
   }

   public void write_longlong(long var1) {
      super.write_longlong(var1);
      this.checkPrimitiveAcrossFragmentedChunk();
   }

   void setHeaderPadding(boolean var1) {
      this.headerPadding = var1;
   }

   protected void alignAndReserve(int var1, int var2) {
      if (this.headerPadding) {
         this.headerPadding = false;
         this.alignOnBoundary(8);
      }

      this.bbwi.position(this.bbwi.position() + this.computeAlignment(var1));
      if (this.bbwi.position() + var2 > this.bbwi.buflen) {
         this.grow(var1, var2);
      }

   }

   protected void grow(int var1, int var2) {
      int var3 = this.bbwi.position();
      boolean var4 = this.inBlock && !this.specialChunk;
      if (var4) {
         int var5 = this.bbwi.position();
         this.bbwi.position(this.blockSizeIndex - 4);
         this.writeLongWithoutAlign(var5 - this.blockSizeIndex + var2);
         this.bbwi.position(var5);
      }

      this.bbwi.needed = var2;
      this.bufferManagerWrite.overflow(this.bbwi);
      if (this.bbwi.fragmented) {
         this.bbwi.fragmented = false;
         this.fragmentOffset += var3 - this.bbwi.position();
         if (var4) {
            this.primitiveAcrossFragmentedChunk = true;
         }
      }

   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_2;
   }

   public void write_wchar(char var1) {
      CodeSetConversion.CTBConverter var2 = this.getWCharConverter();
      var2.convert(var1);
      this.handleSpecialChunkBegin(1 + var2.getNumBytes());
      this.write_octet((byte)var2.getNumBytes());
      byte[] var3 = var2.getBytes();
      this.internalWriteOctetArray(var3, 0, var2.getNumBytes());
      this.handleSpecialChunkEnd();
   }

   public void write_wchar_array(char[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         CodeSetConversion.CTBConverter var4 = this.getWCharConverter();
         int var5 = 0;
         int var6 = (int)Math.ceil((double)(var4.getMaxBytesPerChar() * (float)var3));
         byte[] var7 = new byte[var6 + var3];

         for(int var8 = 0; var8 < var3; ++var8) {
            var4.convert(var1[var2 + var8]);
            var7[var5++] = (byte)var4.getNumBytes();
            System.arraycopy(var4.getBytes(), 0, var7, var5, var4.getNumBytes());
            var5 += var4.getNumBytes();
         }

         this.handleSpecialChunkBegin(var5);
         this.internalWriteOctetArray(var7, 0, var5);
         this.handleSpecialChunkEnd();
      }
   }

   public void write_wstring(String var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else if (var1.length() == 0) {
         this.write_long(0);
      } else {
         CodeSetConversion.CTBConverter var2 = this.getWCharConverter();
         var2.convert(var1);
         this.handleSpecialChunkBegin(this.computeAlignment(4) + 4 + var2.getNumBytes());
         this.write_long(var2.getNumBytes());
         this.internalWriteOctetArray(var2.getBytes(), 0, var2.getNumBytes());
         this.handleSpecialChunkEnd();
      }
   }
}
