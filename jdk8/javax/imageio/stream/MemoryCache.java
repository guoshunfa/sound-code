package javax.imageio.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class MemoryCache {
   private static final int BUFFER_LENGTH = 8192;
   private ArrayList cache = new ArrayList();
   private long cacheStart = 0L;
   private long length = 0L;

   private byte[] getCacheBlock(long var1) throws IOException {
      long var3 = var1 - this.cacheStart;
      if (var3 > 2147483647L) {
         throw new IOException("Cache addressing limit exceeded!");
      } else {
         return (byte[])((byte[])this.cache.get((int)var3));
      }
   }

   public long loadFromStream(InputStream var1, long var2) throws IOException {
      if (var2 < this.length) {
         return var2;
      } else {
         int var4 = (int)(this.length % 8192L);
         byte[] var5 = null;
         long var6 = var2 - this.length;
         if (var4 != 0) {
            var5 = this.getCacheBlock(this.length / 8192L);
         }

         while(var6 > 0L) {
            if (var5 == null) {
               try {
                  var5 = new byte[8192];
               } catch (OutOfMemoryError var10) {
                  throw new IOException("No memory left for cache!");
               }

               var4 = 0;
            }

            int var8 = 8192 - var4;
            int var9 = (int)Math.min(var6, (long)var8);
            var9 = var1.read(var5, var4, var9);
            if (var9 == -1) {
               return this.length;
            }

            if (var4 == 0) {
               this.cache.add(var5);
            }

            var6 -= (long)var9;
            this.length += (long)var9;
            var4 += var9;
            if (var4 >= 8192) {
               var5 = null;
            }
         }

         return var2;
      }
   }

   public void writeToStream(OutputStream var1, long var2, long var4) throws IOException {
      if (var2 + var4 > this.length) {
         throw new IndexOutOfBoundsException("Argument out of cache");
      } else if (var2 >= 0L && var4 >= 0L) {
         if (var4 != 0L) {
            long var6 = var2 / 8192L;
            if (var6 < this.cacheStart) {
               throw new IndexOutOfBoundsException("pos already disposed");
            } else {
               int var8 = (int)(var2 % 8192L);

               int var10;
               for(byte[] var9 = this.getCacheBlock(var6++); var4 > 0L; var4 -= (long)var10) {
                  if (var9 == null) {
                     var9 = this.getCacheBlock(var6++);
                     var8 = 0;
                  }

                  var10 = (int)Math.min(var4, (long)(8192 - var8));
                  var1.write(var9, var8, var10);
                  var9 = null;
               }

            }
         }
      } else {
         throw new IndexOutOfBoundsException("Negative pos or len");
      }
   }

   private void pad(long var1) throws IOException {
      long var3 = this.cacheStart + (long)this.cache.size() - 1L;
      long var5 = var1 / 8192L;
      long var7 = var5 - var3;

      for(long var9 = 0L; var9 < var7; ++var9) {
         try {
            this.cache.add(new byte[8192]);
         } catch (OutOfMemoryError var12) {
            throw new IOException("No memory left for cache!");
         }
      }

   }

   public void write(byte[] var1, int var2, int var3, long var4) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("b == null!");
      } else if (var2 >= 0 && var3 >= 0 && var4 >= 0L && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         long var6 = var4 + (long)var3 - 1L;
         if (var6 >= this.length) {
            this.pad(var6);
            this.length = var6 + 1L;
         }

         for(int var8 = (int)(var4 % 8192L); var3 > 0; var8 = 0) {
            byte[] var9 = this.getCacheBlock(var4 / 8192L);
            int var10 = Math.min(var3, 8192 - var8);
            System.arraycopy(var1, var2, var9, var8, var10);
            var4 += (long)var10;
            var2 += var10;
            var3 -= var10;
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(int var1, long var2) throws IOException {
      if (var2 < 0L) {
         throw new ArrayIndexOutOfBoundsException("pos < 0");
      } else {
         if (var2 >= this.length) {
            this.pad(var2);
            this.length = var2 + 1L;
         }

         byte[] var4 = this.getCacheBlock(var2 / 8192L);
         int var5 = (int)(var2 % 8192L);
         var4[var5] = (byte)var1;
      }
   }

   public long getLength() {
      return this.length;
   }

   public int read(long var1) throws IOException {
      if (var1 >= this.length) {
         return -1;
      } else {
         byte[] var3 = this.getCacheBlock(var1 / 8192L);
         return var3 == null ? -1 : var3[(int)(var1 % 8192L)] & 255;
      }
   }

   public void read(byte[] var1, int var2, int var3, long var4) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("b == null!");
      } else if (var2 >= 0 && var3 >= 0 && var4 >= 0L && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var4 + (long)var3 > this.length) {
            throw new IndexOutOfBoundsException();
         } else {
            long var6 = var4 / 8192L;

            for(int var8 = (int)var4 % 8192; var3 > 0; var8 = 0) {
               int var9 = Math.min(var3, 8192 - var8);
               byte[] var10 = this.getCacheBlock(var6++);
               System.arraycopy(var10, var8, var1, var2, var9);
               var3 -= var9;
               var2 += var9;
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void disposeBefore(long var1) {
      long var3 = var1 / 8192L;
      if (var3 < this.cacheStart) {
         throw new IndexOutOfBoundsException("pos already disposed");
      } else {
         long var5 = Math.min(var3 - this.cacheStart, (long)this.cache.size());

         for(long var7 = 0L; var7 < var5; ++var7) {
            this.cache.remove(0);
         }

         this.cacheStart = var3;
      }
   }

   public void reset() {
      this.cache.clear();
      this.cacheStart = 0L;
      this.length = 0L;
   }
}
