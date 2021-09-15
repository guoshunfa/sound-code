package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.zip.Deflater;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class IDATOutputStream extends ImageOutputStreamImpl {
   private static byte[] chunkType = new byte[]{73, 68, 65, 84};
   private ImageOutputStream stream;
   private int chunkLength;
   private long startPos;
   private CRC crc = new CRC();
   Deflater def = new Deflater(9);
   byte[] buf = new byte[512];
   private int bytesRemaining;

   public IDATOutputStream(ImageOutputStream var1, int var2) throws IOException {
      this.stream = var1;
      this.chunkLength = var2;
      this.startChunk();
   }

   private void startChunk() throws IOException {
      this.crc.reset();
      this.startPos = this.stream.getStreamPosition();
      this.stream.writeInt(-1);
      this.crc.update(chunkType, 0, 4);
      this.stream.write(chunkType, 0, 4);
      this.bytesRemaining = this.chunkLength;
   }

   private void finishChunk() throws IOException {
      this.stream.writeInt(this.crc.getValue());
      long var1 = this.stream.getStreamPosition();
      this.stream.seek(this.startPos);
      this.stream.writeInt((int)(var1 - this.startPos) - 12);
      this.stream.seek(var1);
      this.stream.flushBefore(var1);
   }

   public int read() throws IOException {
      throw new RuntimeException("Method not available");
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      throw new RuntimeException("Method not available");
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 != 0) {
         if (!this.def.finished()) {
            this.def.setInput(var1, var2, var3);

            while(!this.def.needsInput()) {
               this.deflate();
            }
         }

      }
   }

   public void deflate() throws IOException {
      int var1 = this.def.deflate(this.buf, 0, this.buf.length);

      int var3;
      for(int var2 = 0; var1 > 0; this.bytesRemaining -= var3) {
         if (this.bytesRemaining == 0) {
            this.finishChunk();
            this.startChunk();
         }

         var3 = Math.min(var1, this.bytesRemaining);
         this.crc.update(this.buf, var2, var3);
         this.stream.write(this.buf, var2, var3);
         var2 += var3;
         var1 -= var3;
      }

   }

   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)var1};
      this.write(var2, 0, 1);
   }

   public void finish() throws IOException {
      try {
         if (!this.def.finished()) {
            this.def.finish();

            while(!this.def.finished()) {
               this.deflate();
            }
         }

         this.finishChunk();
      } finally {
         this.def.end();
      }

   }

   protected void finalize() throws Throwable {
   }
}
