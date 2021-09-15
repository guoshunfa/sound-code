package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class SubImageInputStream extends ImageInputStreamImpl {
   ImageInputStream stream;
   long startingPos;
   int startingLength;
   int length;

   public SubImageInputStream(ImageInputStream var1, int var2) throws IOException {
      this.stream = var1;
      this.startingPos = var1.getStreamPosition();
      this.startingLength = this.length = var2;
   }

   public int read() throws IOException {
      if (this.length == 0) {
         return -1;
      } else {
         --this.length;
         return this.stream.read();
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.length == 0) {
         return -1;
      } else {
         var3 = Math.min(var3, this.length);
         int var4 = this.stream.read(var1, var2, var3);
         this.length -= var4;
         return var4;
      }
   }

   public long length() {
      return (long)this.startingLength;
   }

   public void seek(long var1) throws IOException {
      this.stream.seek(var1 - this.startingPos);
      this.streamPos = var1;
   }

   protected void finalize() throws Throwable {
   }
}
