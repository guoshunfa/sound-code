package java.io;

import java.util.Arrays;

public class ByteArrayOutputStream extends OutputStream {
   protected byte[] buf;
   protected int count;
   private static final int MAX_ARRAY_SIZE = 2147483639;

   public ByteArrayOutputStream() {
      this(32);
   }

   public ByteArrayOutputStream(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative initial size: " + var1);
      } else {
         this.buf = new byte[var1];
      }
   }

   private void ensureCapacity(int var1) {
      if (var1 - this.buf.length > 0) {
         this.grow(var1);
      }

   }

   private void grow(int var1) {
      int var2 = this.buf.length;
      int var3 = var2 << 1;
      if (var3 - var1 < 0) {
         var3 = var1;
      }

      if (var3 - 2147483639 > 0) {
         var3 = hugeCapacity(var1);
      }

      this.buf = Arrays.copyOf(this.buf, var3);
   }

   private static int hugeCapacity(int var0) {
      if (var0 < 0) {
         throw new OutOfMemoryError();
      } else {
         return var0 > 2147483639 ? Integer.MAX_VALUE : 2147483639;
      }
   }

   public synchronized void write(int var1) {
      this.ensureCapacity(this.count + 1);
      this.buf[this.count] = (byte)var1;
      ++this.count;
   }

   public synchronized void write(byte[] var1, int var2, int var3) {
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 - var1.length <= 0) {
         this.ensureCapacity(this.count + var3);
         System.arraycopy(var1, var2, this.buf, this.count, var3);
         this.count += var3;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void writeTo(OutputStream var1) throws IOException {
      var1.write(this.buf, 0, this.count);
   }

   public synchronized void reset() {
      this.count = 0;
   }

   public synchronized byte[] toByteArray() {
      return Arrays.copyOf(this.buf, this.count);
   }

   public synchronized int size() {
      return this.count;
   }

   public synchronized String toString() {
      return new String(this.buf, 0, this.count);
   }

   public synchronized String toString(String var1) throws UnsupportedEncodingException {
      return new String(this.buf, 0, this.count, var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized String toString(int var1) {
      return new String(this.buf, var1, 0, this.count);
   }

   public void close() throws IOException {
   }
}
