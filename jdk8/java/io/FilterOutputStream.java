package java.io;

public class FilterOutputStream extends OutputStream {
   protected OutputStream out;

   public FilterOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if ((var2 | var3 | var1.length - (var3 + var2) | var2 + var3) < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.write(var1[var2 + var4]);
         }

      }
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      OutputStream var1 = this.out;
      Throwable var2 = null;

      try {
         this.flush();
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               var1.close();
            }
         }

      }

   }
}
