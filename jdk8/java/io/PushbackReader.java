package java.io;

public class PushbackReader extends FilterReader {
   private char[] buf;
   private int pos;

   public PushbackReader(Reader var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("size <= 0");
      } else {
         this.buf = new char[var2];
         this.pos = var2;
      }
   }

   public PushbackReader(Reader var1) {
      this(var1, 1);
   }

   private void ensureOpen() throws IOException {
      if (this.buf == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.pos < this.buf.length ? this.buf[this.pos++] : super.read();
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();

         int var10000;
         try {
            if (var3 <= 0) {
               if (var3 < 0) {
                  throw new IndexOutOfBoundsException();
               }

               if (var2 >= 0 && var2 <= var1.length) {
                  byte var9 = 0;
                  return var9;
               }

               throw new IndexOutOfBoundsException();
            }

            int var5 = this.buf.length - this.pos;
            if (var5 > 0) {
               if (var3 < var5) {
                  var5 = var3;
               }

               System.arraycopy(this.buf, this.pos, var1, var2, var5);
               this.pos += var5;
               var2 += var5;
               var3 -= var5;
            }

            if (var3 > 0) {
               var3 = super.read(var1, var2, var3);
               if (var3 == -1) {
                  var10000 = var5 == 0 ? -1 : var5;
                  return var10000;
               }

               var10000 = var5 + var3;
               return var10000;
            }

            var10000 = var5;
         } catch (ArrayIndexOutOfBoundsException var7) {
            throw new IndexOutOfBoundsException();
         }

         return var10000;
      }
   }

   public void unread(int var1) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.pos == 0) {
            throw new IOException("Pushback buffer overflow");
         } else {
            this.buf[--this.pos] = (char)var1;
         }
      }
   }

   public void unread(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (var3 > this.pos) {
            throw new IOException("Pushback buffer overflow");
         } else {
            this.pos -= var3;
            System.arraycopy(var1, var2, this.buf, this.pos, var3);
         }
      }
   }

   public void unread(char[] var1) throws IOException {
      this.unread(var1, 0, var1.length);
   }

   public boolean ready() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.pos < this.buf.length || super.ready();
      }
   }

   public void mark(int var1) throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }

   public void close() throws IOException {
      super.close();
      this.buf = null;
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         synchronized(this.lock) {
            this.ensureOpen();
            int var4 = this.buf.length - this.pos;
            if (var4 > 0) {
               if (var1 <= (long)var4) {
                  this.pos = (int)((long)this.pos + var1);
                  return var1;
               }

               this.pos = this.buf.length;
               var1 -= (long)var4;
            }

            return (long)var4 + super.skip(var1);
         }
      }
   }
}
