package sun.net.www;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.net.ProgressSource;
import sun.net.www.http.ChunkedInputStream;

public class MeteredStream extends FilterInputStream {
   protected boolean closed = false;
   protected long expected;
   protected long count = 0L;
   protected long markedCount = 0L;
   protected int markLimit = -1;
   protected ProgressSource pi;

   public MeteredStream(InputStream var1, ProgressSource var2, long var3) {
      super(var1);
      this.pi = var2;
      this.expected = var3;
      if (var2 != null) {
         var2.updateProgress(0L, var3);
      }

   }

   private final void justRead(long var1) throws IOException {
      if (var1 == -1L) {
         if (!this.isMarked()) {
            this.close();
         }

      } else {
         this.count += var1;
         if (this.count - this.markedCount > (long)this.markLimit) {
            this.markLimit = -1;
         }

         if (this.pi != null) {
            this.pi.updateProgress(this.count, this.expected);
         }

         if (!this.isMarked()) {
            if (this.expected > 0L && this.count >= this.expected) {
               this.close();
            }

         }
      }
   }

   private boolean isMarked() {
      if (this.markLimit < 0) {
         return false;
      } else {
         return this.count - this.markedCount <= (long)this.markLimit;
      }
   }

   public synchronized int read() throws IOException {
      if (this.closed) {
         return -1;
      } else {
         int var1 = this.in.read();
         if (var1 != -1) {
            this.justRead(1L);
         } else {
            this.justRead((long)var1);
         }

         return var1;
      }
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         return -1;
      } else {
         int var4 = this.in.read(var1, var2, var3);
         this.justRead((long)var4);
         return var4;
      }
   }

   public synchronized long skip(long var1) throws IOException {
      if (this.closed) {
         return 0L;
      } else {
         if (this.in instanceof ChunkedInputStream) {
            var1 = this.in.skip(var1);
         } else {
            long var3 = var1 > this.expected - this.count ? this.expected - this.count : var1;
            var1 = this.in.skip(var3);
         }

         this.justRead(var1);
         return var1;
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         if (this.pi != null) {
            this.pi.finishTracking();
         }

         this.closed = true;
         this.in.close();
      }
   }

   public synchronized int available() throws IOException {
      return this.closed ? 0 : this.in.available();
   }

   public synchronized void mark(int var1) {
      if (!this.closed) {
         super.mark(var1);
         this.markedCount = this.count;
         this.markLimit = var1;
      }
   }

   public synchronized void reset() throws IOException {
      if (!this.closed) {
         if (!this.isMarked()) {
            throw new IOException("Resetting to an invalid mark");
         } else {
            this.count = this.markedCount;
            super.reset();
         }
      }
   }

   public boolean markSupported() {
      return this.closed ? false : super.markSupported();
   }

   protected void finalize() throws Throwable {
      try {
         this.close();
         if (this.pi != null) {
            this.pi.close();
         }
      } finally {
         super.finalize();
      }

   }
}
