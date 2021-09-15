package sun.rmi.transport.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpOutputStream extends ByteArrayOutputStream {
   protected OutputStream out;
   boolean responseSent = false;
   private static byte[] emptyData = new byte[]{0};

   public HttpOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public synchronized void close() throws IOException {
      if (!this.responseSent) {
         if (this.size() == 0) {
            this.write(emptyData);
         }

         DataOutputStream var1 = new DataOutputStream(this.out);
         var1.writeBytes("Content-type: application/octet-stream\r\n");
         var1.writeBytes("Content-length: " + this.size() + "\r\n");
         var1.writeBytes("\r\n");
         this.writeTo(var1);
         var1.flush();
         this.reset();
         this.responseSent = true;
      }

   }
}
