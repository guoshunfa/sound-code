package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;

public class GZIPOutputStream extends DeflaterOutputStream {
   protected CRC32 crc;
   private static final int GZIP_MAGIC = 35615;
   private static final int TRAILER_SIZE = 8;

   public GZIPOutputStream(OutputStream var1, int var2) throws IOException {
      this(var1, var2, false);
   }

   public GZIPOutputStream(OutputStream var1, int var2, boolean var3) throws IOException {
      super(var1, new Deflater(-1, true), var2, var3);
      this.crc = new CRC32();
      this.usesDefaultDeflater = true;
      this.writeHeader();
      this.crc.reset();
   }

   public GZIPOutputStream(OutputStream var1) throws IOException {
      this(var1, 512, false);
   }

   public GZIPOutputStream(OutputStream var1, boolean var2) throws IOException {
      this(var1, 512, var2);
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      super.write(var1, var2, var3);
      this.crc.update(var1, var2, var3);
   }

   public void finish() throws IOException {
      if (!this.def.finished()) {
         this.def.finish();

         while(!this.def.finished()) {
            int var1 = this.def.deflate(this.buf, 0, this.buf.length);
            if (this.def.finished() && var1 <= this.buf.length - 8) {
               this.writeTrailer(this.buf, var1);
               var1 += 8;
               this.out.write(this.buf, 0, var1);
               return;
            }

            if (var1 > 0) {
               this.out.write(this.buf, 0, var1);
            }
         }

         byte[] var2 = new byte[8];
         this.writeTrailer(var2, 0);
         this.out.write(var2);
      }

   }

   private void writeHeader() throws IOException {
      this.out.write(new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0});
   }

   private void writeTrailer(byte[] var1, int var2) throws IOException {
      this.writeInt((int)this.crc.getValue(), var1, var2);
      this.writeInt(this.def.getTotalIn(), var1, var2 + 4);
   }

   private void writeInt(int var1, byte[] var2, int var3) throws IOException {
      this.writeShort(var1 & '\uffff', var2, var3);
      this.writeShort(var1 >> 16 & '\uffff', var2, var3 + 2);
   }

   private void writeShort(int var1, byte[] var2, int var3) throws IOException {
      var2[var3] = (byte)(var1 & 255);
      var2[var3 + 1] = (byte)(var1 >> 8 & 255);
   }
}
