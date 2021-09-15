package java.io;

public abstract class FilterReader extends Reader {
   protected Reader in;

   protected FilterReader(Reader var1) {
      super(var1);
      this.in = var1;
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      return this.in.read(var1, var2, var3);
   }

   public long skip(long var1) throws IOException {
      return this.in.skip(var1);
   }

   public boolean ready() throws IOException {
      return this.in.ready();
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   public void mark(int var1) throws IOException {
      this.in.mark(var1);
   }

   public void reset() throws IOException {
      this.in.reset();
   }

   public void close() throws IOException {
      this.in.close();
   }
}
