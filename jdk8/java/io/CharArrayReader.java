package java.io;

public class CharArrayReader extends Reader {
   protected char[] buf;
   protected int pos;
   protected int markedPos = 0;
   protected int count;

   public CharArrayReader(char[] var1) {
      this.buf = var1;
      this.pos = 0;
      this.count = var1.length;
   }

   public CharArrayReader(char[] var1, int var2, int var3) {
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 >= 0) {
         this.buf = var1;
         this.pos = var2;
         this.count = Math.min(var2 + var3, var1.length);
         this.markedPos = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void ensureOpen() throws IOException {
      if (this.buf == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.pos >= this.count ? -1 : this.buf[this.pos++];
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            if (var3 == 0) {
               return 0;
            } else if (this.pos >= this.count) {
               return -1;
            } else {
               int var5 = this.count - this.pos;
               if (var3 > var5) {
                  var3 = var5;
               }

               if (var3 <= 0) {
                  return 0;
               } else {
                  System.arraycopy(this.buf, this.pos, var1, var2, var3);
                  this.pos += var3;
                  return var3;
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public long skip(long var1) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         long var4 = (long)(this.count - this.pos);
         if (var1 > var4) {
            var1 = var4;
         }

         if (var1 < 0L) {
            return 0L;
         } else {
            this.pos = (int)((long)this.pos + var1);
            return var1;
         }
      }
   }

   public boolean ready() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.count - this.pos > 0;
      }
   }

   public boolean markSupported() {
      return true;
   }

   public void mark(int var1) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         this.markedPos = this.pos;
      }
   }

   public void reset() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         this.pos = this.markedPos;
      }
   }

   public void close() {
      this.buf = null;
   }
}
