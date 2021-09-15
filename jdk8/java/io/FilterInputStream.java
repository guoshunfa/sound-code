package java.io;

public class FilterInputStream extends InputStream {
   protected volatile InputStream in;

   protected FilterInputStream(InputStream var1) {
      this.in = var1;
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.in.read(var1, var2, var3);
   }

   public long skip(long var1) throws IOException {
      return this.in.skip(var1);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public synchronized void mark(int var1) {
      this.in.mark(var1);
   }

   public synchronized void reset() throws IOException {
      this.in.reset();
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }
}
