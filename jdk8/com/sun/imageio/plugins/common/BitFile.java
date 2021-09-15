package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class BitFile {
   ImageOutputStream output;
   byte[] buffer;
   int index;
   int bitsLeft;
   boolean blocks = false;

   public BitFile(ImageOutputStream var1, boolean var2) {
      this.output = var1;
      this.blocks = var2;
      this.buffer = new byte[256];
      this.index = 0;
      this.bitsLeft = 8;
   }

   public void flush() throws IOException {
      int var1 = this.index + (this.bitsLeft == 8 ? 0 : 1);
      if (var1 > 0) {
         if (this.blocks) {
            this.output.write(var1);
         }

         this.output.write(this.buffer, 0, var1);
         this.buffer[0] = 0;
         this.index = 0;
         this.bitsLeft = 8;
      }

   }

   public void writeBits(int var1, int var2) throws IOException {
      int var3 = 0;
      short var4 = 255;

      do {
         if (this.index == 254 && this.bitsLeft == 0 || this.index > 254) {
            if (this.blocks) {
               this.output.write(var4);
            }

            this.output.write(this.buffer, 0, var4);
            this.buffer[0] = 0;
            this.index = 0;
            this.bitsLeft = 8;
         }

         byte[] var10000;
         int var10001;
         if (var2 <= this.bitsLeft) {
            if (this.blocks) {
               var10000 = this.buffer;
               var10001 = this.index;
               var10000[var10001] = (byte)(var10000[var10001] | (var1 & (1 << var2) - 1) << 8 - this.bitsLeft);
               var3 += var2;
               this.bitsLeft -= var2;
               var2 = 0;
            } else {
               var10000 = this.buffer;
               var10001 = this.index;
               var10000[var10001] = (byte)(var10000[var10001] | (var1 & (1 << var2) - 1) << this.bitsLeft - var2);
               var3 += var2;
               this.bitsLeft -= var2;
               var2 = 0;
            }
         } else if (this.blocks) {
            var10000 = this.buffer;
            var10001 = this.index;
            var10000[var10001] = (byte)(var10000[var10001] | (var1 & (1 << this.bitsLeft) - 1) << 8 - this.bitsLeft);
            var3 += this.bitsLeft;
            var1 >>= this.bitsLeft;
            var2 -= this.bitsLeft;
            this.buffer[++this.index] = 0;
            this.bitsLeft = 8;
         } else {
            int var5 = var1 >>> var2 - this.bitsLeft & (1 << this.bitsLeft) - 1;
            var10000 = this.buffer;
            var10001 = this.index;
            var10000[var10001] = (byte)(var10000[var10001] | var5);
            var2 -= this.bitsLeft;
            var3 += this.bitsLeft;
            this.buffer[++this.index] = 0;
            this.bitsLeft = 8;
         }
      } while(var2 != 0);

   }
}
