package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FileChannel extends AbstractInterruptibleChannel implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel {
   private static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];

   protected FileChannel() {
   }

   public static FileChannel open(Path var0, Set<? extends OpenOption> var1, FileAttribute<?>... var2) throws IOException {
      FileSystemProvider var3 = var0.getFileSystem().provider();
      return var3.newFileChannel(var0, var1, var2);
   }

   public static FileChannel open(Path var0, OpenOption... var1) throws IOException {
      HashSet var2 = new HashSet(var1.length);
      Collections.addAll(var2, var1);
      return open(var0, var2, NO_ATTRIBUTES);
   }

   public abstract int read(ByteBuffer var1) throws IOException;

   public abstract long read(ByteBuffer[] var1, int var2, int var3) throws IOException;

   public final long read(ByteBuffer[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public abstract int write(ByteBuffer var1) throws IOException;

   public abstract long write(ByteBuffer[] var1, int var2, int var3) throws IOException;

   public final long write(ByteBuffer[] var1) throws IOException {
      return this.write(var1, 0, var1.length);
   }

   public abstract long position() throws IOException;

   public abstract FileChannel position(long var1) throws IOException;

   public abstract long size() throws IOException;

   public abstract FileChannel truncate(long var1) throws IOException;

   public abstract void force(boolean var1) throws IOException;

   public abstract long transferTo(long var1, long var3, WritableByteChannel var5) throws IOException;

   public abstract long transferFrom(ReadableByteChannel var1, long var2, long var4) throws IOException;

   public abstract int read(ByteBuffer var1, long var2) throws IOException;

   public abstract int write(ByteBuffer var1, long var2) throws IOException;

   public abstract MappedByteBuffer map(FileChannel.MapMode var1, long var2, long var4) throws IOException;

   public abstract FileLock lock(long var1, long var3, boolean var5) throws IOException;

   public final FileLock lock() throws IOException {
      return this.lock(0L, Long.MAX_VALUE, false);
   }

   public abstract FileLock tryLock(long var1, long var3, boolean var5) throws IOException;

   public final FileLock tryLock() throws IOException {
      return this.tryLock(0L, Long.MAX_VALUE, false);
   }

   public static class MapMode {
      public static final FileChannel.MapMode READ_ONLY = new FileChannel.MapMode("READ_ONLY");
      public static final FileChannel.MapMode READ_WRITE = new FileChannel.MapMode("READ_WRITE");
      public static final FileChannel.MapMode PRIVATE = new FileChannel.MapMode("PRIVATE");
      private final String name;

      private MapMode(String var1) {
         this.name = var1;
      }

      public String toString() {
         return this.name;
      }
   }
}
