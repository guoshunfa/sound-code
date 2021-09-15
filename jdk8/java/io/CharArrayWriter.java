package java.io;

import java.util.Arrays;

public class CharArrayWriter extends Writer {
   protected char[] buf;
   protected int count;

   public CharArrayWriter() {
      this(32);
   }

   public CharArrayWriter(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative initial size: " + var1);
      } else {
         this.buf = new char[var1];
      }
   }

   public void write(int var1) {
      synchronized(this.lock) {
         int var3 = this.count + 1;
         if (var3 > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, var3));
         }

         this.buf[this.count] = (char)var1;
         this.count = var3;
      }
   }

   public void write(char[] var1, int var2, int var3) {
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            synchronized(this.lock) {
               int var5 = this.count + var3;
               if (var5 > this.buf.length) {
                  this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, var5));
               }

               System.arraycopy(var1, var2, this.buf, this.count, var3);
               this.count = var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(String var1, int var2, int var3) {
      synchronized(this.lock) {
         int var5 = this.count + var3;
         if (var5 > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, var5));
         }

         var1.getChars(var2, var2 + var3, this.buf, this.count);
         this.count = var5;
      }
   }

   public void writeTo(Writer var1) throws IOException {
      synchronized(this.lock) {
         var1.write((char[])this.buf, 0, this.count);
      }
   }

   public CharArrayWriter append(CharSequence var1) {
      String var2 = var1 == null ? "null" : var1.toString();
      this.write((String)var2, 0, var2.length());
      return this;
   }

   public CharArrayWriter append(CharSequence var1, int var2, int var3) {
      String var4 = ((CharSequence)(var1 == null ? "null" : var1)).subSequence(var2, var3).toString();
      this.write((String)var4, 0, var4.length());
      return this;
   }

   public CharArrayWriter append(char var1) {
      this.write(var1);
      return this;
   }

   public void reset() {
      this.count = 0;
   }

   public char[] toCharArray() {
      synchronized(this.lock) {
         return Arrays.copyOf(this.buf, this.count);
      }
   }

   public int size() {
      return this.count;
   }

   public String toString() {
      synchronized(this.lock) {
         return new String(this.buf, 0, this.count);
      }
   }

   public void flush() {
   }

   public void close() {
   }
}
