package javax.imageio.stream;

import com.sun.imageio.stream.StreamFinalizer;
import java.io.IOException;
import java.io.InputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class MemoryCacheImageInputStream extends ImageInputStreamImpl {
   private InputStream stream;
   private MemoryCache cache = new MemoryCache();
   private final Object disposerReferent;
   private final DisposerRecord disposerRecord;

   public MemoryCacheImageInputStream(InputStream var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("stream == null!");
      } else {
         this.stream = var1;
         this.disposerRecord = new MemoryCacheImageInputStream.StreamDisposerRecord(this.cache);
         if (this.getClass() == MemoryCacheImageInputStream.class) {
            this.disposerReferent = new Object();
            Disposer.addRecord(this.disposerReferent, this.disposerRecord);
         } else {
            this.disposerReferent = new StreamFinalizer(this);
         }

      }
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      long var1 = this.cache.loadFromStream(this.stream, this.streamPos + 1L);
      return var1 >= this.streamPos + 1L ? this.cache.read((long)(this.streamPos++)) : -1;
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
            long var4 = this.cache.loadFromStream(this.stream, this.streamPos + (long)var3);
            var3 = (int)(var4 - this.streamPos);
            if (var3 > 0) {
               this.cache.read(var1, var2, var3, this.streamPos);
               this.streamPos += (long)var3;
               return var3;
            } else {
               return -1;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
      }
   }

   public void flushBefore(long var1) throws IOException {
      super.flushBefore(var1);
      this.cache.disposeBefore(var1);
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
      super.close();
      this.disposerRecord.dispose();
      this.stream = null;
      this.cache = null;
   }

   protected void finalize() throws Throwable {
   }

   private static class StreamDisposerRecord implements DisposerRecord {
      private MemoryCache cache;

      public StreamDisposerRecord(MemoryCache var1) {
         this.cache = var1;
      }

      public synchronized void dispose() {
         if (this.cache != null) {
            this.cache.reset();
            this.cache = null;
         }

      }
   }
}
