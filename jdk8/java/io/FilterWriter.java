package java.io;

public abstract class FilterWriter extends Writer {
   protected Writer out;

   protected FilterWriter(Writer var1) {
      super(var1);
      this.out = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
   }

   public void write(String var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      this.out.close();
   }
}
