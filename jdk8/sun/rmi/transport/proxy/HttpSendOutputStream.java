package sun.rmi.transport.proxy;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpSendOutputStream extends FilterOutputStream {
   HttpSendSocket owner;

   public HttpSendOutputStream(OutputStream var1, HttpSendSocket var2) throws IOException {
      super(var1);
      this.owner = var2;
   }

   public void deactivate() {
      this.out = null;
   }

   public void write(int var1) throws IOException {
      if (this.out == null) {
         this.out = this.owner.writeNotify();
      }

      this.out.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 != 0) {
         if (this.out == null) {
            this.out = this.owner.writeNotify();
         }

         this.out.write(var1, var2, var3);
      }
   }

   public void flush() throws IOException {
      if (this.out != null) {
         this.out.flush();
      }

   }

   public void close() throws IOException {
      this.flush();
      this.owner.close();
   }
}
