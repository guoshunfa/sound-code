package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract class AsynchronousFileChannelImpl extends AsynchronousFileChannel {
   protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
   protected volatile boolean closed;
   protected final FileDescriptor fdObj;
   protected final boolean reading;
   protected final boolean writing;
   protected final ExecutorService executor;
   private volatile FileLockTable fileLockTable;

   protected AsynchronousFileChannelImpl(FileDescriptor var1, boolean var2, boolean var3, ExecutorService var4) {
      this.fdObj = var1;
      this.reading = var2;
      this.writing = var3;
      this.executor = var4;
   }

   final ExecutorService executor() {
      return this.executor;
   }

   public final boolean isOpen() {
      return !this.closed;
   }

   protected final void begin() throws IOException {
      this.closeLock.readLock().lock();
      if (this.closed) {
         throw new ClosedChannelException();
      }
   }

   protected final void end() {
      this.closeLock.readLock().unlock();
   }

   protected final void end(boolean var1) throws IOException {
      this.end();
      if (!var1 && !this.isOpen()) {
         throw new AsynchronousCloseException();
      }
   }

   abstract <A> Future<FileLock> implLock(long var1, long var3, boolean var5, A var6, CompletionHandler<FileLock, ? super A> var7);

   public final Future<FileLock> lock(long var1, long var3, boolean var5) {
      return this.implLock(var1, var3, var5, (Object)null, (CompletionHandler)null);
   }

   public final <A> void lock(long var1, long var3, boolean var5, A var6, CompletionHandler<FileLock, ? super A> var7) {
      if (var7 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.implLock(var1, var3, var5, var6, var7);
      }
   }

   final void ensureFileLockTableInitialized() throws IOException {
      if (this.fileLockTable == null) {
         synchronized(this) {
            if (this.fileLockTable == null) {
               this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fdObj);
            }
         }
      }

   }

   final void invalidateAllLocks() throws IOException {
      if (this.fileLockTable != null) {
         Iterator var1 = this.fileLockTable.removeAll().iterator();

         while(var1.hasNext()) {
            FileLock var2 = (FileLock)var1.next();
            synchronized(var2) {
               if (var2.isValid()) {
                  FileLockImpl var4 = (FileLockImpl)var2;
                  this.implRelease(var4);
                  var4.invalidate();
               }
            }
         }
      }

   }

   protected final FileLockImpl addToFileLockTable(long var1, long var3, boolean var5) {
      FileLockImpl var6;
      try {
         this.closeLock.readLock().lock();
         if (this.closed) {
            Object var7 = null;
            return (FileLockImpl)var7;
         }

         try {
            this.ensureFileLockTableInitialized();
         } catch (IOException var11) {
            throw new AssertionError(var11);
         }

         var6 = new FileLockImpl(this, var1, var3, var5);
         this.fileLockTable.add(var6);
      } finally {
         this.end();
      }

      return var6;
   }

   protected final void removeFromFileLockTable(FileLockImpl var1) {
      this.fileLockTable.remove(var1);
   }

   protected abstract void implRelease(FileLockImpl var1) throws IOException;

   final void release(FileLockImpl var1) throws IOException {
      try {
         this.begin();
         this.implRelease(var1);
         this.removeFromFileLockTable(var1);
      } finally {
         this.end();
      }

   }

   abstract <A> Future<Integer> implRead(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5);

   public final Future<Integer> read(ByteBuffer var1, long var2) {
      return this.implRead(var1, var2, (Object)null, (CompletionHandler)null);
   }

   public final <A> void read(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      if (var5 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.implRead(var1, var2, var4, var5);
      }
   }

   abstract <A> Future<Integer> implWrite(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5);

   public final Future<Integer> write(ByteBuffer var1, long var2) {
      return this.implWrite(var1, var2, (Object)null, (CompletionHandler)null);
   }

   public final <A> void write(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      if (var5 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.implWrite(var1, var2, var4, var5);
      }
   }
}
