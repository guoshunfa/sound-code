package javax.swing;

import java.awt.Component;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class ProgressMonitorInputStream extends FilterInputStream {
   private ProgressMonitor monitor;
   private int nread = 0;
   private int size = 0;

   public ProgressMonitorInputStream(Component var1, Object var2, InputStream var3) {
      super(var3);

      try {
         this.size = var3.available();
      } catch (IOException var5) {
         this.size = 0;
      }

      this.monitor = new ProgressMonitor(var1, var2, (String)null, 0, this.size);
   }

   public ProgressMonitor getProgressMonitor() {
      return this.monitor;
   }

   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 >= 0) {
         this.monitor.setProgress(++this.nread);
      }

      if (this.monitor.isCanceled()) {
         InterruptedIOException var2 = new InterruptedIOException("progress");
         var2.bytesTransferred = this.nread;
         throw var2;
      } else {
         return var1;
      }
   }

   public int read(byte[] var1) throws IOException {
      int var2 = this.in.read(var1);
      if (var2 > 0) {
         this.monitor.setProgress(this.nread += var2);
      }

      if (this.monitor.isCanceled()) {
         InterruptedIOException var3 = new InterruptedIOException("progress");
         var3.bytesTransferred = this.nread;
         throw var3;
      } else {
         return var2;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (var4 > 0) {
         this.monitor.setProgress(this.nread += var4);
      }

      if (this.monitor.isCanceled()) {
         InterruptedIOException var5 = new InterruptedIOException("progress");
         var5.bytesTransferred = this.nread;
         throw var5;
      } else {
         return var4;
      }
   }

   public long skip(long var1) throws IOException {
      long var3 = this.in.skip(var1);
      if (var3 > 0L) {
         this.monitor.setProgress(this.nread = (int)((long)this.nread + var3));
      }

      return var3;
   }

   public void close() throws IOException {
      this.in.close();
      this.monitor.close();
   }

   public synchronized void reset() throws IOException {
      this.in.reset();
      this.nread = this.size - this.in.available();
      this.monitor.setProgress(this.nread);
   }
}
