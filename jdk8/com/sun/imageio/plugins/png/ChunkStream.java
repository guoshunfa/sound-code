package com.sun.imageio.plugins.png;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class ChunkStream extends ImageOutputStreamImpl {
   private ImageOutputStream stream;
   private long startPos;
   private CRC crc = new CRC();

   public ChunkStream(int var1, ImageOutputStream var2) throws IOException {
      this.stream = var2;
      this.startPos = var2.getStreamPosition();
      var2.writeInt(-1);
      this.writeInt(var1);
   }

   public int read() throws IOException {
      throw new RuntimeException("Method not available");
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      throw new RuntimeException("Method not available");
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.crc.update(var1, var2, var3);
      this.stream.write(var1, var2, var3);
   }

   public void write(int var1) throws IOException {
      this.crc.update(var1);
      this.stream.write(var1);
   }

   public void finish() throws IOException {
      this.stream.writeInt(this.crc.getValue());
      long var1 = this.stream.getStreamPosition();
      this.stream.seek(this.startPos);
      this.stream.writeInt((int)(var1 - this.startPos) - 12);
      this.stream.seek(var1);
      this.stream.flushBefore(var1);
   }

   protected void finalize() throws Throwable {
   }
}
