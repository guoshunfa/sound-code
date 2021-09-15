package javax.imageio.stream;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryCacheImageOutputStream extends ImageOutputStreamImpl {
   private OutputStream stream;
   private MemoryCache cache = new MemoryCache();

   public MemoryCacheImageOutputStream(OutputStream var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("stream == null!");
      } else {
         this.stream = var1;
      }
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      int var1 = this.cache.read(this.streamPos);
      if (var1 != -1) {
         ++this.streamPos;
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.checkClosed();
      if (var1 == null) {
         throw new NullPointerException("b == null!");
      } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         this.bitOffset = 0;
         if (var3 == 0) {
            return 0;
         } else {
            long var4 = this.cache.getLength() - this.streamPos;
            if (var4 <= 0L) {
               return -1;
            } else {
               var3 = (int)Math.min(var4, (long)var3);
               this.cache.read(var1, var2, var3, this.streamPos);
               this.streamPos += (long)var3;
               return var3;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
      }
   }

   public void write(int var1) throws IOException {
      this.flushBits();
      this.cache.write(var1, this.streamPos);
      ++this.streamPos;
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.flushBits();
      this.cache.write(var1, var2, var3, this.streamPos);
      this.streamPos += (long)var3;
   }

   public long length() {
      try {
         this.checkClosed();
         return this.cache.getLength();
      } catch (IOException var2) {
         return -1L;
      }
   }

   public boolean isCached() {
      return true;
   }

   public boolean isCachedFile() {
      return false;
   }

   public boolean isCachedMemory() {
      return true;
   }

   public void close() throws IOException {
      long var1 = this.cache.getLength();
      this.seek(var1);
      this.flushBefore(var1);
      super.close();
      this.cache.reset();
      this.cache = null;
      this.stream = null;
   }

   public void flushBefore(long var1) throws IOException {
      long var3 = this.flushedPos;
      super.flushBefore(var1);
      long var5 = this.flushedPos - var3;
      this.cache.writeToStream(this.stream, var3, var5);
      this.cache.disposeBefore(this.flushedPos);
      this.stream.flush();
   }
}
