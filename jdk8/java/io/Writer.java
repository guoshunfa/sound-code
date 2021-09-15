package java.io;

public abstract class Writer implements Appendable, Closeable, Flushable {
   private char[] writeBuffer;
   private static final int WRITE_BUFFER_SIZE = 1024;
   protected Object lock;

   protected Writer() {
      this.lock = this;
   }

   protected Writer(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.lock = var1;
      }
   }

   public void write(int var1) throws IOException {
      synchronized(this.lock) {
         if (this.writeBuffer == null) {
            this.writeBuffer = new char[1024];
         }

         this.writeBuffer[0] = (char)var1;
         this.write((char[])this.writeBuffer, 0, 1);
      }
   }

   public void write(char[] var1) throws IOException {
      this.write((char[])var1, 0, var1.length);
   }

   public abstract void write(char[] var1, int var2, int var3) throws IOException;

   public void write(String var1) throws IOException {
      this.write((String)var1, 0, var1.length());
   }

   public void write(String var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         char[] var5;
         if (var3 <= 1024) {
            if (this.writeBuffer == null) {
               this.writeBuffer = new char[1024];
            }

            var5 = this.writeBuffer;
         } else {
            var5 = new char[var3];
         }

         var1.getChars(var2, var2 + var3, var5, 0);
         this.write((char[])var5, 0, var3);
      }
   }

   public Writer append(CharSequence var1) throws IOException {
      if (var1 == null) {
         this.write("null");
      } else {
         this.write(var1.toString());
      }

      return this;
   }

   public Writer append(CharSequence var1, int var2, int var3) throws IOException {
      Object var4 = var1 == null ? "null" : var1;
      this.write(((CharSequence)var4).subSequence(var2, var3).toString());
      return this;
   }

   public Writer append(char var1) throws IOException {
      this.write(var1);
      return this;
   }

   public abstract void flush() throws IOException;

   public abstract void close() throws IOException;
}
