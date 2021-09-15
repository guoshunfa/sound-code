package java.io;

public class StringWriter extends Writer {
   private StringBuffer buf;

   public StringWriter() {
      this.buf = new StringBuffer();
      this.lock = this.buf;
   }

   public StringWriter(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative buffer size");
      } else {
         this.buf = new StringBuffer(var1);
         this.lock = this.buf;
      }
   }

   public void write(int var1) {
      this.buf.append((char)var1);
   }

   public void write(char[] var1, int var2, int var3) {
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            this.buf.append(var1, var2, var3);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(String var1) {
      this.buf.append(var1);
   }

   public void write(String var1, int var2, int var3) {
      this.buf.append(var1.substring(var2, var2 + var3));
   }

   public StringWriter append(CharSequence var1) {
      if (var1 == null) {
         this.write("null");
      } else {
         this.write(var1.toString());
      }

      return this;
   }

   public StringWriter append(CharSequence var1, int var2, int var3) {
      Object var4 = var1 == null ? "null" : var1;
      this.write(((CharSequence)var4).subSequence(var2, var3).toString());
      return this;
   }

   public StringWriter append(char var1) {
      this.write(var1);
      return this;
   }

   public String toString() {
      return this.buf.toString();
   }

   public StringBuffer getBuffer() {
      return this.buf;
   }

   public void flush() {
   }

   public void close() throws IOException {
   }
}
