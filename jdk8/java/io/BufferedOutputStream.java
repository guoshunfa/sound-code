package java.io;

public class BufferedOutputStream extends FilterOutputStream {
   protected byte[] buf;
   protected int count;

   public BufferedOutputStream(OutputStream var1) {
      this(var1, 8192);
   }

   public BufferedOutputStream(OutputStream var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.buf = new byte[var2];
      }
   }

   private void flushBuffer() throws IOException {
      if (this.count > 0) {
         this.out.write(this.buf, 0, this.count);
         this.count = 0;
      }

   }

   public synchronized void write(int var1) throws IOException {
      if (this.count >= this.buf.length) {
         this.flushBuffer();
      }

      this.buf[this.count++] = (byte)var1;
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 >= this.buf.length) {
         this.flushBuffer();
         this.out.write(var1, var2, var3);
      } else {
         if (var3 > this.buf.length - this.count) {
            this.flushBuffer();
         }

         System.arraycopy(var1, var2, this.buf, this.count, var3);
         this.count += var3;
      }
   }

   public synchronized void flush() throws IOException {
      this.flushBuffer();
      this.out.flush();
   }
}
