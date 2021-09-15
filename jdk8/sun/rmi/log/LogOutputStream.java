package sun.rmi.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class LogOutputStream extends OutputStream {
   private RandomAccessFile raf;

   public LogOutputStream(RandomAccessFile var1) throws IOException {
      this.raf = var1;
   }

   public void write(int var1) throws IOException {
      this.raf.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      this.raf.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.raf.write(var1, var2, var3);
   }

   public final void close() throws IOException {
   }
}
