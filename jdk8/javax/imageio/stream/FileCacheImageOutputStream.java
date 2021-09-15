package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class FileCacheImageOutputStream extends ImageOutputStreamImpl {
   private OutputStream stream;
   private File cacheFile;
   private RandomAccessFile cache;
   private long maxStreamPos = 0L;
   private final StreamCloser.CloseAction closeAction;

   public FileCacheImageOutputStream(OutputStream var1, File var2) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("stream == null!");
      } else if (var2 != null && !var2.isDirectory()) {
         throw new IllegalArgumentException("Not a directory!");
      } else {
         this.stream = var1;
         if (var2 == null) {
            this.cacheFile = Files.createTempFile("imageio", ".tmp").toFile();
         } else {
            this.cacheFile = Files.createTempFile(var2.toPath(), "imageio", ".tmp").toFile();
         }

         this.cache = new RandomAccessFile(this.cacheFile, "rw");
         this.closeAction = StreamCloser.createCloseAction(this);
         StreamCloser.addToQueue(this.closeAction);
      }
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      int var1 = this.cache.read();
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
            int var4 = this.cache.read(var1, var2, var3);
            if (var4 != -1) {
               this.streamPos += (long)var4;
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
      }
   }

   public void write(int var1) throws IOException {
      this.flushBits();
      this.cache.write(var1);
      ++this.streamPos;
      this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.flushBits();
      this.cache.write(var1, var2, var3);
      this.streamPos += (long)var3;
      this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
   }

   public long length() {
      try {
         this.checkClosed();
         return this.cache.length();
      } catch (IOException var2) {
         return -1L;
      }
   }

   public void seek(long var1) throws IOException {
      this.checkClosed();
      if (var1 < this.flushedPos) {
         throw new IndexOutOfBoundsException();
      } else {
         this.cache.seek(var1);
         this.streamPos = this.cache.getFilePointer();
         this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
         this.bitOffset = 0;
      }
   }

   public boolean isCached() {
      return true;
   }

   public boolean isCachedFile() {
      return true;
   }

   public boolean isCachedMemory() {
      return false;
   }

   public void close() throws IOException {
      this.maxStreamPos = this.cache.length();
      this.seek(this.maxStreamPos);
      this.flushBefore(this.maxStreamPos);
      super.close();
      this.cache.close();
      this.cache = null;
      this.cacheFile.delete();
      this.cacheFile = null;
      this.stream.flush();
      this.stream = null;
      StreamCloser.removeFromQueue(this.closeAction);
   }

   public void flushBefore(long var1) throws IOException {
      long var3 = this.flushedPos;
      super.flushBefore(var1);
      long var5 = this.flushedPos - var3;
      if (var5 > 0L) {
         short var7 = 512;
         byte[] var8 = new byte[var7];
         this.cache.seek(var3);

         while(var5 > 0L) {
            int var9 = (int)Math.min(var5, (long)var7);
            this.cache.readFully(var8, 0, var9);
            this.stream.write(var8, 0, var9);
            var5 -= (long)var9;
         }

         this.stream.flush();
      }

   }
}
