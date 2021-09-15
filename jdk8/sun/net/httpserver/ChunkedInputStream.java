package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class ChunkedInputStream extends LeftOverInputStream {
   private int remaining;
   private boolean needToReadHeader = true;
   static final char CR = '\r';
   static final char LF = '\n';
   private static final int MAX_CHUNK_HEADER_SIZE = 2050;

   ChunkedInputStream(ExchangeImpl var1, InputStream var2) {
      super(var1, var2);
   }

   private int numeric(char[] var1, int var2) throws IOException {
      assert var1.length >= var2;

      int var3 = 0;

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var1[var4];
         boolean var6 = false;
         int var7;
         if (var5 >= '0' && var5 <= '9') {
            var7 = var5 - 48;
         } else if (var5 >= 'a' && var5 <= 'f') {
            var7 = var5 - 97 + 10;
         } else {
            if (var5 < 'A' || var5 > 'F') {
               throw new IOException("invalid chunk length");
            }

            var7 = var5 - 65 + 10;
         }

         var3 = var3 * 16 + var7;
      }

      return var3;
   }

   private int readChunkHeader() throws IOException {
      boolean var1 = false;
      char[] var3 = new char[16];
      int var4 = 0;
      boolean var5 = false;
      int var6 = 0;

      int var2;
      while((var2 = this.in.read()) != -1) {
         char var7 = (char)var2;
         ++var6;
         if (var4 == var3.length - 1 || var6 > 2050) {
            throw new IOException("invalid chunk header");
         }

         if (var1) {
            if (var7 == '\n') {
               int var8 = this.numeric(var3, var4);
               return var8;
            }

            var1 = false;
            if (!var5) {
               var3[var4++] = var7;
            }
         } else if (var7 == '\r') {
            var1 = true;
         } else if (var7 == ';') {
            var5 = true;
         } else if (!var5) {
            var3[var4++] = var7;
         }
      }

      throw new IOException("end of stream reading chunk header");
   }

   protected int readImpl(byte[] var1, int var2, int var3) throws IOException {
      if (this.eof) {
         return -1;
      } else {
         if (this.needToReadHeader) {
            this.remaining = this.readChunkHeader();
            if (this.remaining == 0) {
               this.eof = true;
               this.consumeCRLF();
               this.t.getServerImpl().requestCompleted(this.t.getConnection());
               return -1;
            }

            this.needToReadHeader = false;
         }

         if (var3 > this.remaining) {
            var3 = this.remaining;
         }

         int var4 = this.in.read(var1, var2, var3);
         if (var4 > -1) {
            this.remaining -= var4;
         }

         if (this.remaining == 0) {
            this.needToReadHeader = true;
            this.consumeCRLF();
         }

         return var4;
      }
   }

   private void consumeCRLF() throws IOException {
      char var1 = (char)this.in.read();
      if (var1 != '\r') {
         throw new IOException("invalid chunk end");
      } else {
         var1 = (char)this.in.read();
         if (var1 != '\n') {
            throw new IOException("invalid chunk end");
         }
      }
   }

   public int available() throws IOException {
      if (!this.eof && !this.closed) {
         int var1 = this.in.available();
         return var1 > this.remaining ? this.remaining : var1;
      } else {
         return 0;
      }
   }

   public boolean isDataBuffered() throws IOException {
      assert this.eof;

      return this.in.available() > 0;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) {
   }

   public void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }
}
