package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class ChunkedOutputStream extends FilterOutputStream {
   private boolean closed = false;
   static final int CHUNK_SIZE = 4096;
   static final int OFFSET = 6;
   private int pos = 6;
   private int count = 0;
   private byte[] buf = new byte[4104];
   ExchangeImpl t;

   ChunkedOutputStream(ExchangeImpl var1, OutputStream var2) {
      super(var2);
      this.t = var1;
   }

   public void write(int var1) throws IOException {
      if (this.closed) {
         throw new StreamClosedException();
      } else {
         this.buf[this.pos++] = (byte)var1;
         ++this.count;
         if (this.count == 4096) {
            this.writeChunk();
         }

         assert this.count < 4096;

      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new StreamClosedException();
      } else {
         int var4 = 4096 - this.count;
         if (var3 > var4) {
            System.arraycopy(var1, var2, this.buf, this.pos, var4);
            this.count = 4096;
            this.writeChunk();
            var3 -= var4;
            var2 += var4;

            while(var3 >= 4096) {
               System.arraycopy(var1, var2, this.buf, 6, 4096);
               var3 -= 4096;
               var2 += 4096;
               this.count = 4096;
               this.writeChunk();
            }
         }

         if (var3 > 0) {
            System.arraycopy(var1, var2, this.buf, this.pos, var3);
            this.count += var3;
            this.pos += var3;
         }

         if (this.count == 4096) {
            this.writeChunk();
         }

      }
   }

   private void writeChunk() throws IOException {
      char[] var1 = Integer.toHexString(this.count).toCharArray();
      int var2 = var1.length;
      int var3 = 4 - var2;

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         this.buf[var3 + var4] = (byte)var1[var4];
      }

      this.buf[var3 + var4++] = 13;
      this.buf[var3 + var4++] = 10;
      this.buf[var3 + var4++ + this.count] = 13;
      this.buf[var3 + var4++ + this.count] = 10;
      this.out.write(this.buf, var3, var4 + this.count);
      this.count = 0;
      this.pos = 6;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.flush();

         try {
            this.writeChunk();
            this.out.flush();
            LeftOverInputStream var1 = this.t.getOriginalInputStream();
            if (!var1.isClosed()) {
               var1.close();
            }
         } catch (IOException var5) {
         } finally {
            this.closed = true;
         }

         WriteFinishedEvent var7 = new WriteFinishedEvent(this.t);
         this.t.getHttpContext().getServerImpl().addEvent(var7);
      }
   }

   public void flush() throws IOException {
      if (this.closed) {
         throw new StreamClosedException();
      } else {
         if (this.count > 0) {
            this.writeChunk();
         }

         this.out.flush();
      }
   }
}
