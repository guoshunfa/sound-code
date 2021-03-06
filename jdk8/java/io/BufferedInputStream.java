package java.io;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class BufferedInputStream extends FilterInputStream {
   private static int DEFAULT_BUFFER_SIZE = 8192;
   private static int MAX_BUFFER_SIZE = 2147483639;
   protected volatile byte[] buf;
   private static final AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater.newUpdater(BufferedInputStream.class, byte[].class, "buf");
   protected int count;
   protected int pos;
   protected int markpos;
   protected int marklimit;

   private InputStream getInIfOpen() throws IOException {
      InputStream var1 = this.in;
      if (var1 == null) {
         throw new IOException("Stream closed");
      } else {
         return var1;
      }
   }

   private byte[] getBufIfOpen() throws IOException {
      byte[] var1 = this.buf;
      if (var1 == null) {
         throw new IOException("Stream closed");
      } else {
         return var1;
      }
   }

   public BufferedInputStream(InputStream var1) {
      this(var1, DEFAULT_BUFFER_SIZE);
   }

   public BufferedInputStream(InputStream var1, int var2) {
      super(var1);
      this.markpos = -1;
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.buf = new byte[var2];
      }
   }

   private void fill() throws IOException {
      byte[] var1 = this.getBufIfOpen();
      int var2;
      if (this.markpos < 0) {
         this.pos = 0;
      } else if (this.pos >= var1.length) {
         if (this.markpos > 0) {
            var2 = this.pos - this.markpos;
            System.arraycopy(var1, this.markpos, var1, 0, var2);
            this.pos = var2;
            this.markpos = 0;
         } else if (var1.length >= this.marklimit) {
            this.markpos = -1;
            this.pos = 0;
         } else {
            if (var1.length >= MAX_BUFFER_SIZE) {
               throw new OutOfMemoryError("Required array size too large");
            }

            var2 = this.pos <= MAX_BUFFER_SIZE - this.pos ? this.pos * 2 : MAX_BUFFER_SIZE;
            if (var2 > this.marklimit) {
               var2 = this.marklimit;
            }

            byte[] var3 = new byte[var2];
            System.arraycopy(var1, 0, var3, 0, this.pos);
            if (!bufUpdater.compareAndSet(this, var1, var3)) {
               throw new IOException("Stream closed");
            }

            var1 = var3;
         }
      }

      this.count = this.pos;
      var2 = this.getInIfOpen().read(var1, this.pos, var1.length - this.pos);
      if (var2 > 0) {
         this.count = var2 + this.pos;
      }

   }

   public synchronized int read() throws IOException {
      if (this.pos >= this.count) {
         this.fill();
         if (this.pos >= this.count) {
            return -1;
         }
      }

      return this.getBufIfOpen()[this.pos++] & 255;
   }

   private int read1(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.count - this.pos;
      if (var4 <= 0) {
         if (var3 >= this.getBufIfOpen().length && this.markpos < 0) {
            return this.getInIfOpen().read(var1, var2, var3);
         }

         this.fill();
         var4 = this.count - this.pos;
         if (var4 <= 0) {
            return -1;
         }
      }

      int var5 = var4 < var3 ? var4 : var3;
      System.arraycopy(this.getBufIfOpen(), this.pos, var1, var2, var5);
      this.pos += var5;
      return var5;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      this.getBufIfOpen();
      if ((var2 | var3 | var2 + var3 | var1.length - (var2 + var3)) < 0) {
         throw new IndexOutOfBoundsException();
      } else if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         InputStream var6;
         do {
            int var5 = this.read1(var1, var2 + var4, var3 - var4);
            if (var5 <= 0) {
               return var4 == 0 ? var5 : var4;
            }

            var4 += var5;
            if (var4 >= var3) {
               return var4;
            }

            var6 = this.in;
         } while(var6 == null || var6.available() > 0);

         return var4;
      }
   }

   public synchronized long skip(long var1) throws IOException {
      this.getBufIfOpen();
      if (var1 <= 0L) {
         return 0L;
      } else {
         long var3 = (long)(this.count - this.pos);
         if (var3 <= 0L) {
            if (this.markpos < 0) {
               return this.getInIfOpen().skip(var1);
            }

            this.fill();
            var3 = (long)(this.count - this.pos);
            if (var3 <= 0L) {
               return 0L;
            }
         }

         long var5 = var3 < var1 ? var3 : var1;
         this.pos = (int)((long)this.pos + var5);
         return var5;
      }
   }

   public synchronized int available() throws IOException {
      int var1 = this.count - this.pos;
      int var2 = this.getInIfOpen().available();
      return var1 > Integer.MAX_VALUE - var2 ? Integer.MAX_VALUE : var1 + var2;
   }

   public synchronized void mark(int var1) {
      this.marklimit = var1;
      this.markpos = this.pos;
   }

   public synchronized void reset() throws IOException {
      this.getBufIfOpen();
      if (this.markpos < 0) {
         throw new IOException("Resetting to invalid mark");
      } else {
         this.pos = this.markpos;
      }
   }

   public boolean markSupported() {
      return true;
   }

   public void close() throws IOException {
      while(true) {
         byte[] var1;
         if ((var1 = this.buf) != null) {
            if (!bufUpdater.compareAndSet(this, var1, (Object)null)) {
               continue;
            }

            InputStream var2 = this.in;
            this.in = null;
            if (var2 != null) {
               var2.close();
            }

            return;
         }

         return;
      }
   }
}
