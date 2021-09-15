package java.io;

public abstract class InputStream implements Closeable {
   private static final int MAX_SKIP_BUFFER_SIZE = 2048;

   public abstract int read() throws IOException;

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.read();
            if (var4 == -1) {
               return -1;
            } else {
               var1[var2] = (byte)var4;
               int var5 = 1;

               try {
                  while(var5 < var3) {
                     var4 = this.read();
                     if (var4 == -1) {
                        break;
                     }

                     var1[var2 + var5] = (byte)var4;
                     ++var5;
                  }
               } catch (IOException var7) {
               }

               return var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      long var3 = var1;
      if (var1 <= 0L) {
         return 0L;
      } else {
         int var6 = (int)Math.min(2048L, var1);

         int var5;
         for(byte[] var7 = new byte[var6]; var3 > 0L; var3 -= (long)var5) {
            var5 = this.read(var7, 0, (int)Math.min((long)var6, var3));
            if (var5 < 0) {
               break;
            }
         }

         return var1 - var3;
      }
   }

   public int available() throws IOException {
      return 0;
   }

   public void close() throws IOException {
   }

   public synchronized void mark(int var1) {
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }
}
