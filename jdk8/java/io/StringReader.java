package java.io;

public class StringReader extends Reader {
   private String str;
   private int length;
   private int next = 0;
   private int mark = 0;

   public StringReader(String var1) {
      this.str = var1;
      this.length = var1.length();
   }

   private void ensureOpen() throws IOException {
      if (this.str == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.next >= this.length ? -1 : this.str.charAt(this.next++);
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            if (var3 == 0) {
               return 0;
            } else if (this.next >= this.length) {
               return -1;
            } else {
               int var5 = Math.min(this.length - this.next, var3);
               this.str.getChars(this.next, this.next + var5, var1, var2);
               this.next += var5;
               return var5;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public long skip(long var1) throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         if (this.next >= this.length) {
            return 0L;
         } else {
            long var4 = Math.min((long)(this.length - this.next), var1);
            var4 = Math.max((long)(-this.next), var4);
            this.next = (int)((long)this.next + var4);
            return var4;
         }
      }
   }

   public boolean ready() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return true;
      }
   }

   public boolean markSupported() {
      return true;
   }

   public void mark(int var1) throws IOException {
      if (var1 < 0) {
         throw new IllegalArgumentException("Read-ahead limit < 0");
      } else {
         synchronized(this.lock) {
            this.ensureOpen();
            this.mark = this.next;
         }
      }
   }

   public void reset() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         this.next = this.mark;
      }
   }

   public void close() {
      this.str = null;
   }
}
