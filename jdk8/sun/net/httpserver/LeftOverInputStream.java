package sun.net.httpserver;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class LeftOverInputStream extends FilterInputStream {
   ExchangeImpl t;
   ServerImpl server;
   protected boolean closed = false;
   protected boolean eof = false;
   byte[] one = new byte[1];

   public LeftOverInputStream(ExchangeImpl var1, InputStream var2) {
      super(var2);
      this.t = var1;
      this.server = var1.getServerImpl();
   }

   public boolean isDataBuffered() throws IOException {
      assert this.eof;

      return super.available() > 0;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         if (!this.eof) {
            this.eof = this.drain(ServerConfig.getDrainAmount());
         }

      }
   }

   public boolean isClosed() {
      return this.closed;
   }

   public boolean isEOF() {
      return this.eof;
   }

   protected abstract int readImpl(byte[] var1, int var2, int var3) throws IOException;

   public synchronized int read() throws IOException {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         int var1 = this.readImpl(this.one, 0, 1);
         return var1 != -1 && var1 != 0 ? this.one[0] & 255 : var1;
      }
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("Stream is closed");
      } else {
         return this.readImpl(var1, var2, var3);
      }
   }

   public boolean drain(long var1) throws IOException {
      short var3 = 2048;

      long var5;
      for(byte[] var4 = new byte[var3]; var1 > 0L; var1 -= var5) {
         var5 = (long)this.readImpl(var4, 0, var3);
         if (var5 == -1L) {
            this.eof = true;
            return true;
         }
      }

      return false;
   }
}
