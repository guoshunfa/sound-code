package sun.net.www.http;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ChunkedOutputStream extends PrintStream {
   static final int DEFAULT_CHUNK_SIZE = 4096;
   private static final byte[] CRLF = new byte[]{13, 10};
   private static final int CRLF_SIZE;
   private static final byte[] FOOTER;
   private static final int FOOTER_SIZE;
   private static final byte[] EMPTY_CHUNK_HEADER;
   private static final int EMPTY_CHUNK_HEADER_SIZE;
   private byte[] buf;
   private int size;
   private int count;
   private int spaceInCurrentChunk;
   private PrintStream out;
   private int preferredChunkDataSize;
   private int preferedHeaderSize;
   private int preferredChunkGrossSize;
   private byte[] completeHeader;

   private static int getHeaderSize(int var0) {
      return Integer.toHexString(var0).length() + CRLF_SIZE;
   }

   private static byte[] getHeader(int var0) {
      try {
         String var1 = Integer.toHexString(var0);
         byte[] var2 = var1.getBytes("US-ASCII");
         byte[] var3 = new byte[getHeaderSize(var0)];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = var2[var4];
         }

         var3[var2.length] = CRLF[0];
         var3[var2.length + 1] = CRLF[1];
         return var3;
      } catch (UnsupportedEncodingException var5) {
         throw new InternalError(var5.getMessage(), var5);
      }
   }

   public ChunkedOutputStream(PrintStream var1) {
      this(var1, 4096);
   }

   public ChunkedOutputStream(PrintStream var1, int var2) {
      super((OutputStream)var1);
      this.out = var1;
      if (var2 <= 0) {
         var2 = 4096;
      }

      if (var2 > 0) {
         int var3 = var2 - getHeaderSize(var2) - FOOTER_SIZE;
         if (getHeaderSize(var3 + 1) < getHeaderSize(var2)) {
            ++var3;
         }

         var2 = var3;
      }

      if (var2 > 0) {
         this.preferredChunkDataSize = var2;
      } else {
         this.preferredChunkDataSize = 4096 - getHeaderSize(4096) - FOOTER_SIZE;
      }

      this.preferedHeaderSize = getHeaderSize(this.preferredChunkDataSize);
      this.preferredChunkGrossSize = this.preferedHeaderSize + this.preferredChunkDataSize + FOOTER_SIZE;
      this.completeHeader = getHeader(this.preferredChunkDataSize);
      this.buf = new byte[this.preferredChunkGrossSize];
      this.reset();
   }

   private void flush(boolean var1) {
      if (this.spaceInCurrentChunk == 0) {
         this.out.write(this.buf, 0, this.preferredChunkGrossSize);
         this.out.flush();
         this.reset();
      } else if (var1) {
         if (this.size > 0) {
            int var2 = this.preferedHeaderSize - getHeaderSize(this.size);
            System.arraycopy(getHeader(this.size), 0, this.buf, var2, getHeaderSize(this.size));
            this.buf[this.count++] = FOOTER[0];
            this.buf[this.count++] = FOOTER[1];
            this.out.write(this.buf, var2, this.count - var2);
         } else {
            this.out.write(EMPTY_CHUNK_HEADER, 0, EMPTY_CHUNK_HEADER_SIZE);
         }

         this.out.flush();
         this.reset();
      }

   }

   public boolean checkError() {
      return this.out.checkError();
   }

   private void ensureOpen() {
      if (this.out == null) {
         this.setError();
      }

   }

   public synchronized void write(byte[] var1, int var2, int var3) {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            int var4 = var3;
            int var5 = var2;

            do {
               if (var4 >= this.spaceInCurrentChunk) {
                  for(int var6 = 0; var6 < this.completeHeader.length; ++var6) {
                     this.buf[var6] = this.completeHeader[var6];
                  }

                  System.arraycopy(var1, var5, this.buf, this.count, this.spaceInCurrentChunk);
                  var5 += this.spaceInCurrentChunk;
                  var4 -= this.spaceInCurrentChunk;
                  this.count += this.spaceInCurrentChunk;
                  this.buf[this.count++] = FOOTER[0];
                  this.buf[this.count++] = FOOTER[1];
                  this.spaceInCurrentChunk = 0;
                  this.flush(false);
                  if (this.checkError()) {
                     break;
                  }
               } else {
                  System.arraycopy(var1, var5, this.buf, this.count, var4);
                  this.count += var4;
                  this.size += var4;
                  this.spaceInCurrentChunk -= var4;
                  var4 = 0;
               }
            } while(var4 > 0);

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void write(int var1) {
      byte[] var2 = new byte[]{(byte)var1};
      this.write(var2, 0, 1);
   }

   public synchronized void reset() {
      this.count = this.preferedHeaderSize;
      this.size = 0;
      this.spaceInCurrentChunk = this.preferredChunkDataSize;
   }

   public int size() {
      return this.size;
   }

   public synchronized void close() {
      this.ensureOpen();
      if (this.size > 0) {
         this.flush(true);
      }

      this.flush(true);
      this.out = null;
   }

   public synchronized void flush() {
      this.ensureOpen();
      if (this.size > 0) {
         this.flush(true);
      }

   }

   static {
      CRLF_SIZE = CRLF.length;
      FOOTER = CRLF;
      FOOTER_SIZE = CRLF_SIZE;
      EMPTY_CHUNK_HEADER = getHeader(0);
      EMPTY_CHUNK_HEADER_SIZE = getHeaderSize(0);
   }
}
