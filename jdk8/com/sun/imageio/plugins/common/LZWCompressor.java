package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.stream.ImageOutputStream;

public class LZWCompressor {
   int codeSize;
   int clearCode;
   int endOfInfo;
   int numBits;
   int limit;
   short prefix;
   BitFile bf;
   LZWStringTable lzss;
   boolean tiffFudge;

   public LZWCompressor(ImageOutputStream var1, int var2, boolean var3) throws IOException {
      this.bf = new BitFile(var1, !var3);
      this.codeSize = var2;
      this.tiffFudge = var3;
      this.clearCode = 1 << var2;
      this.endOfInfo = this.clearCode + 1;
      this.numBits = var2 + 1;
      this.limit = (1 << this.numBits) - 1;
      if (this.tiffFudge) {
         --this.limit;
      }

      this.prefix = -1;
      this.lzss = new LZWStringTable();
      this.lzss.clearTable(var2);
      this.bf.writeBits(this.clearCode, this.numBits);
   }

   public void compress(byte[] var1, int var2, int var3) throws IOException {
      int var7 = var2 + var3;

      for(int var4 = var2; var4 < var7; ++var4) {
         byte var5 = var1[var4];
         short var6;
         if ((var6 = this.lzss.findCharString(this.prefix, var5)) != -1) {
            this.prefix = var6;
         } else {
            this.bf.writeBits(this.prefix, this.numBits);
            if (this.lzss.addCharString(this.prefix, var5) > this.limit) {
               if (this.numBits == 12) {
                  this.bf.writeBits(this.clearCode, this.numBits);
                  this.lzss.clearTable(this.codeSize);
                  this.numBits = this.codeSize + 1;
               } else {
                  ++this.numBits;
               }

               this.limit = (1 << this.numBits) - 1;
               if (this.tiffFudge) {
                  --this.limit;
               }
            }

            this.prefix = (short)((short)var5 & 255);
         }
      }

   }

   public void flush() throws IOException {
      if (this.prefix != -1) {
         this.bf.writeBits(this.prefix, this.numBits);
      }

      this.bf.writeBits(this.endOfInfo, this.numBits);
      this.bf.flush();
   }

   public void dump(PrintStream var1) {
      this.lzss.dump(var1);
   }
}
