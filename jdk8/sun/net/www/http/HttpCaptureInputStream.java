package sun.net.www.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpCaptureInputStream extends FilterInputStream {
   private HttpCapture capture = null;

   public HttpCaptureInputStream(InputStream var1, HttpCapture var2) {
      super(var1);
      this.capture = var2;
   }

   public int read() throws IOException {
      int var1 = super.read();
      this.capture.received(var1);
      return var1;
   }

   public void close() throws IOException {
      try {
         this.capture.flush();
      } catch (IOException var2) {
      }

      super.close();
   }

   public int read(byte[] var1) throws IOException {
      int var2 = super.read(var1);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.capture.received(var1[var3]);
      }

      return var2;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = super.read(var1, var2, var3);

      for(int var5 = 0; var5 < var4; ++var5) {
         this.capture.received(var1[var2 + var5]);
      }

      return var4;
   }
}
