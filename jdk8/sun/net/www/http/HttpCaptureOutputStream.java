package sun.net.www.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpCaptureOutputStream extends FilterOutputStream {
   private HttpCapture capture = null;

   public HttpCaptureOutputStream(OutputStream var1, HttpCapture var2) {
      super(var1);
      this.capture = var2;
   }

   public void write(int var1) throws IOException {
      this.capture.sent(var1);
      this.out.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      byte[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         this.capture.sent(var5);
      }

      this.out.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      for(int var4 = var2; var4 < var3; ++var4) {
         this.capture.sent(var1[var4]);
      }

      this.out.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      try {
         this.capture.flush();
      } catch (IOException var2) {
      }

      super.flush();
   }
}
