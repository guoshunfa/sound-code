package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class UndefLengthOutputStream extends FilterOutputStream {
   private boolean closed = false;
   ExchangeImpl t;

   UndefLengthOutputStream(ExchangeImpl var1, OutputStream var2) {
      super(var2);
      this.t = var1;
   }

   public void write(int var1) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.out.write(var1);
      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.out.write(var1, var2, var3);
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         this.flush();
         LeftOverInputStream var1 = this.t.getOriginalInputStream();
         if (!var1.isClosed()) {
            try {
               var1.close();
            } catch (IOException var3) {
            }
         }

         WriteFinishedEvent var2 = new WriteFinishedEvent(this.t);
         this.t.getHttpContext().getServerImpl().addEvent(var2);
      }
   }
}
