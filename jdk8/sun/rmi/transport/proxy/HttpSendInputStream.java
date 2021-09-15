package sun.rmi.transport.proxy;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpSendInputStream extends FilterInputStream {
   HttpSendSocket owner;

   public HttpSendInputStream(InputStream var1, HttpSendSocket var2) throws IOException {
      super(var1);
      this.owner = var2;
   }

   public void deactivate() {
      this.in = null;
   }

   public int read() throws IOException {
      if (this.in == null) {
         this.in = this.owner.readNotify();
      }

      return this.in.read();
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else {
         if (this.in == null) {
            this.in = this.owner.readNotify();
         }

         return this.in.read(var1, var2, var3);
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 == 0L) {
         return 0L;
      } else {
         if (this.in == null) {
            this.in = this.owner.readNotify();
         }

         return this.in.skip(var1);
      }
   }

   public int available() throws IOException {
      if (this.in == null) {
         this.in = this.owner.readNotify();
      }

      return this.in.available();
   }

   public void close() throws IOException {
      this.owner.close();
   }

   public synchronized void mark(int var1) {
      if (this.in == null) {
         try {
            this.in = this.owner.readNotify();
         } catch (IOException var3) {
            return;
         }
      }

      this.in.mark(var1);
   }

   public synchronized void reset() throws IOException {
      if (this.in == null) {
         this.in = this.owner.readNotify();
      }

      this.in.reset();
   }

   public boolean markSupported() {
      if (this.in == null) {
         try {
            this.in = this.owner.readNotify();
         } catch (IOException var2) {
            return false;
         }
      }

      return this.in.markSupported();
   }
}
