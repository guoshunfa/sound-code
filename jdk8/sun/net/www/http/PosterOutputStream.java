package sun.net.www.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PosterOutputStream extends ByteArrayOutputStream {
   private boolean closed;

   public PosterOutputStream() {
      super(256);
   }

   public synchronized void write(int var1) {
      if (!this.closed) {
         super.write(var1);
      }
   }

   public synchronized void write(byte[] var1, int var2, int var3) {
      if (!this.closed) {
         super.write(var1, var2, var3);
      }
   }

   public synchronized void reset() {
      if (!this.closed) {
         super.reset();
      }
   }

   public synchronized void close() throws IOException {
      this.closed = true;
      super.close();
   }
}
