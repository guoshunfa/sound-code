package sun.awt.image;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class PNGFilterInputStream extends FilterInputStream {
   PNGImageDecoder owner;
   public InputStream underlyingInputStream;

   public PNGFilterInputStream(PNGImageDecoder var1, InputStream var2) {
      super(var2);
      this.underlyingInputStream = this.in;
      this.owner = var1;
   }

   public int available() throws IOException {
      return this.owner.limit - this.owner.pos + this.in.available();
   }

   public boolean markSupported() {
      return false;
   }

   public int read() throws IOException {
      if (this.owner.chunkLength <= 0 && !this.owner.getData()) {
         return -1;
      } else {
         --this.owner.chunkLength;
         return this.owner.inbuf[this.owner.chunkStart++] & 255;
      }
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.owner.chunkLength <= 0 && !this.owner.getData()) {
         return -1;
      } else {
         if (this.owner.chunkLength < var3) {
            var3 = this.owner.chunkLength;
         }

         System.arraycopy(this.owner.inbuf, this.owner.chunkStart, var1, var2, var3);
         PNGImageDecoder var10000 = this.owner;
         var10000.chunkLength -= var3;
         var10000 = this.owner;
         var10000.chunkStart += var3;
         return var3;
      }
   }

   public long skip(long var1) throws IOException {
      int var3;
      for(var3 = 0; (long)var3 < var1 && this.read() >= 0; ++var3) {
      }

      return (long)var3;
   }
}
