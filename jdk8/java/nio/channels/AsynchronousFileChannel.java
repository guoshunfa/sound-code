package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AsynchronousFileChannel implements AsynchronousChannel {
   private static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];

   protected AsynchronousFileChannel() {
   }

   public static AsynchronousFileChannel open(Path var0, Set<? extends OpenOption> var1, ExecutorService var2, FileAttribute<?>... var3) throws IOException {
      FileSystemProvider var4 = var0.getFileSystem().provider();
      return var4.newAsynchronousFileChannel(var0, var1, var2, var3);
   }

   public static AsynchronousFileChannel open(Path var0, OpenOption... var1) throws IOException {
      HashSet var2 = new HashSet(var1.length);
      Collections.addAll(var2, var1);
      return open(var0, var2, (ExecutorService)null, NO_ATTRIBUTES);
   }

   public abstract long size() throws IOException;

   public abstract AsynchronousFileChannel truncate(long var1) throws IOException;

   public abstract void force(boolean var1) throws IOException;

   public abstract <A> void lock(long var1, long var3, boolean var5, A var6, CompletionHandler<FileLock, ? super A> var7);

   public final <A> void lock(A var1, CompletionHandler<FileLock, ? super A> var2) {
      this.lock(0L, Long.MAX_VALUE, false, var1, var2);
   }

   public abstract Future<FileLock> lock(long var1, long var3, boolean var5);

   public final Future<FileLock> lock() {
      return this.lock(0L, Long.MAX_VALUE, false);
   }

   public abstract FileLock tryLock(long var1, long var3, boolean var5) throws IOException;

   public final FileLock tryLock() throws IOException {
      return this.tryLock(0L, Long.MAX_VALUE, false);
   }

   public abstract <A> void read(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5);

   public abstract Future<Integer> read(ByteBuffer var1, long var2);

   public abstract <A> void write(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5);

   public abstract Future<Integer> write(ByteBuffer var1, long var2);
}
