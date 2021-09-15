package java.nio.channels;

import java.io.IOException;

public abstract class FileLock implements AutoCloseable {
   private final Channel channel;
   private final long position;
   private final long size;
   private final boolean shared;

   protected FileLock(FileChannel var1, long var2, long var4, boolean var6) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (var4 < 0L) {
         throw new IllegalArgumentException("Negative size");
      } else if (var2 + var4 < 0L) {
         throw new IllegalArgumentException("Negative position + size");
      } else {
         this.channel = var1;
         this.position = var2;
         this.size = var4;
         this.shared = var6;
      }
   }

   protected FileLock(AsynchronousFileChannel var1, long var2, long var4, boolean var6) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (var4 < 0L) {
         throw new IllegalArgumentException("Negative size");
      } else if (var2 + var4 < 0L) {
         throw new IllegalArgumentException("Negative position + size");
      } else {
         this.channel = var1;
         this.position = var2;
         this.size = var4;
         this.shared = var6;
      }
   }

   public final FileChannel channel() {
      return this.channel instanceof FileChannel ? (FileChannel)this.channel : null;
   }

   public Channel acquiredBy() {
      return this.channel;
   }

   public final long position() {
      return this.position;
   }

   public final long size() {
      return this.size;
   }

   public final boolean isShared() {
      return this.shared;
   }

   public final boolean overlaps(long var1, long var3) {
      if (var1 + var3 <= this.position) {
         return false;
      } else {
         return this.position + this.size > var1;
      }
   }

   public abstract boolean isValid();

   public abstract void release() throws IOException;

   public final void close() throws IOException {
      this.release();
   }

   public final String toString() {
      return this.getClass().getName() + "[" + this.position + ":" + this.size + " " + (this.shared ? "shared" : "exclusive") + " " + (this.isValid() ? "valid" : "invalid") + "]";
   }
}
