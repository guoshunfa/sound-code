package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SimpleAsynchronousFileChannelImpl extends AsynchronousFileChannelImpl {
   private static final FileDispatcher nd = new FileDispatcherImpl();
   private final NativeThreadSet threads = new NativeThreadSet(2);

   SimpleAsynchronousFileChannelImpl(FileDescriptor var1, boolean var2, boolean var3, ExecutorService var4) {
      super(var1, var2, var3, var4);
   }

   public static AsynchronousFileChannel open(FileDescriptor var0, boolean var1, boolean var2, ThreadPool var3) {
      ExecutorService var4 = var3 == null ? SimpleAsynchronousFileChannelImpl.DefaultExecutorHolder.defaultExecutor : var3.executor();
      return new SimpleAsynchronousFileChannelImpl(var0, var1, var2, var4);
   }

   public void close() throws IOException {
      synchronized(this.fdObj) {
         if (this.closed) {
            return;
         }

         this.closed = true;
      }

      this.invalidateAllLocks();
      this.threads.signalAndWait();
      this.closeLock.writeLock().lock();
      this.closeLock.writeLock().unlock();
      nd.close(this.fdObj);
   }

   public long size() throws IOException {
      int var1 = this.threads.add();

      try {
         long var2 = 0L;

         try {
            this.begin();

            do {
               var2 = nd.size(this.fdObj);
            } while(var2 == -3L && this.isOpen());

            long var4 = var2;
            return var4;
         } finally {
            this.end(var2 >= 0L);
         }
      } finally {
         this.threads.remove(var1);
      }
   }

   public AsynchronousFileChannel truncate(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative size");
      } else if (!this.writing) {
         throw new NonWritableChannelException();
      } else {
         int var3 = this.threads.add();

         try {
            long var4 = 0L;

            try {
               this.begin();

               do {
                  var4 = nd.size(this.fdObj);
               } while(var4 == -3L && this.isOpen());

               if (var1 < var4 && this.isOpen()) {
                  do {
                     var4 = (long)nd.truncate(this.fdObj, var1);
                  } while(var4 == -3L && this.isOpen());
               }

               SimpleAsynchronousFileChannelImpl var6 = this;
               return var6;
            } finally {
               this.end(var4 > 0L);
            }
         } finally {
            this.threads.remove(var3);
         }
      }
   }

   public void force(boolean var1) throws IOException {
      int var2 = this.threads.add();

      try {
         int var3 = 0;

         try {
            this.begin();

            do {
               var3 = nd.force(this.fdObj, var1);
            } while(var3 == -3 && this.isOpen());
         } finally {
            this.end(var3 >= 0);
         }
      } finally {
         this.threads.remove(var2);
      }

   }

   <A> Future<FileLock> implLock(final long var1, final long var3, final boolean var5, final A var6, final CompletionHandler<FileLock, ? super A> var7) {
      if (var5 && !this.reading) {
         throw new NonReadableChannelException();
      } else if (!var5 && !this.writing) {
         throw new NonWritableChannelException();
      } else {
         final FileLockImpl var8 = this.addToFileLockTable(var1, var3, var5);
         if (var8 == null) {
            ClosedChannelException var15 = new ClosedChannelException();
            if (var7 == null) {
               return CompletedFuture.withFailure(var15);
            } else {
               Invoker.invokeIndirectly((CompletionHandler)var7, (Object)var6, (Object)null, (Throwable)var15, (Executor)this.executor);
               return null;
            }
         } else {
            final PendingFuture var9 = var7 == null ? new PendingFuture(this) : null;
            Runnable var10 = new Runnable() {
               public void run() {
                  Object var1x = null;
                  int var2 = SimpleAsynchronousFileChannelImpl.this.threads.add();

                  try {
                     try {
                        SimpleAsynchronousFileChannelImpl.this.begin();

                        int var3x;
                        do {
                           var3x = SimpleAsynchronousFileChannelImpl.nd.lock(SimpleAsynchronousFileChannelImpl.this.fdObj, true, var1, var3, var5);
                        } while(var3x == 2 && SimpleAsynchronousFileChannelImpl.this.isOpen());

                        if (var3x != 0 || !SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                           throw new AsynchronousCloseException();
                        }
                     } catch (IOException var13) {
                        Object var4 = var13;
                        SimpleAsynchronousFileChannelImpl.this.removeFromFileLockTable(var8);
                        if (!SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                           var4 = new AsynchronousCloseException();
                        }

                        var1x = var4;
                     } finally {
                        SimpleAsynchronousFileChannelImpl.this.end();
                     }
                  } finally {
                     SimpleAsynchronousFileChannelImpl.this.threads.remove(var2);
                  }

                  if (var7 == null) {
                     var9.setResult(var8, (Throwable)var1x);
                  } else {
                     Invoker.invokeUnchecked(var7, var6, var8, (Throwable)var1x);
                  }

               }
            };
            boolean var11 = false;

            try {
               this.executor.execute(var10);
               var11 = true;
            } finally {
               if (!var11) {
                  this.removeFromFileLockTable(var8);
               }

            }

            return var9;
         }
      }
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      if (var5 && !this.reading) {
         throw new NonReadableChannelException();
      } else if (!var5 && !this.writing) {
         throw new NonWritableChannelException();
      } else {
         FileLockImpl var6 = this.addToFileLockTable(var1, var3, var5);
         if (var6 == null) {
            throw new ClosedChannelException();
         } else {
            int var7 = this.threads.add();
            boolean var8 = false;

            FileLockImpl var10;
            try {
               this.begin();

               int var9;
               do {
                  var9 = nd.lock(this.fdObj, false, var1, var3, var5);
               } while(var9 == 2 && this.isOpen());

               if (var9 == 0 && this.isOpen()) {
                  var8 = true;
                  var10 = var6;
                  return var10;
               }

               if (var9 != -1) {
                  if (var9 == 2) {
                     throw new AsynchronousCloseException();
                  }

                  throw new AssertionError();
               }

               var10 = null;
            } finally {
               if (!var8) {
                  this.removeFromFileLockTable(var6);
               }

               this.end();
               this.threads.remove(var7);
            }

            return var10;
         }
      }
   }

   protected void implRelease(FileLockImpl var1) throws IOException {
      nd.release(this.fdObj, var1.position(), var1.size());
   }

   <A> Future<Integer> implRead(final ByteBuffer var1, final long var2, final A var4, final CompletionHandler<Integer, ? super A> var5) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (!this.reading) {
         throw new NonReadableChannelException();
      } else if (var1.isReadOnly()) {
         throw new IllegalArgumentException("Read-only buffer");
      } else if (this.isOpen() && var1.remaining() != 0) {
         final PendingFuture var8 = var5 == null ? new PendingFuture(this) : null;
         Runnable var7 = new Runnable() {
            public void run() {
               int var1x = 0;
               Object var2x = null;
               int var3 = SimpleAsynchronousFileChannelImpl.this.threads.add();

               try {
                  SimpleAsynchronousFileChannelImpl.this.begin();

                  do {
                     var1x = IOUtil.read(SimpleAsynchronousFileChannelImpl.this.fdObj, var1, var2, SimpleAsynchronousFileChannelImpl.nd);
                  } while(var1x == -3 && SimpleAsynchronousFileChannelImpl.this.isOpen());

                  if (var1x < 0 && !SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                     throw new AsynchronousCloseException();
                  }
               } catch (IOException var8x) {
                  Object var4x = var8x;
                  if (!SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                     var4x = new AsynchronousCloseException();
                  }

                  var2x = var4x;
               } finally {
                  SimpleAsynchronousFileChannelImpl.this.end();
                  SimpleAsynchronousFileChannelImpl.this.threads.remove(var3);
               }

               if (var5 == null) {
                  var8.setResult(var1x, (Throwable)var2x);
               } else {
                  Invoker.invokeUnchecked(var5, var4, var1x, (Throwable)var2x);
               }

            }
         };
         this.executor.execute(var7);
         return var8;
      } else {
         ClosedChannelException var6 = this.isOpen() ? null : new ClosedChannelException();
         if (var5 == null) {
            return CompletedFuture.withResult(0, var6);
         } else {
            Invoker.invokeIndirectly((CompletionHandler)var5, (Object)var4, 0, (Throwable)var6, (Executor)this.executor);
            return null;
         }
      }
   }

   <A> Future<Integer> implWrite(final ByteBuffer var1, final long var2, final A var4, final CompletionHandler<Integer, ? super A> var5) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (!this.writing) {
         throw new NonWritableChannelException();
      } else if (this.isOpen() && var1.remaining() != 0) {
         final PendingFuture var8 = var5 == null ? new PendingFuture(this) : null;
         Runnable var7 = new Runnable() {
            public void run() {
               int var1x = 0;
               Object var2x = null;
               int var3 = SimpleAsynchronousFileChannelImpl.this.threads.add();

               try {
                  SimpleAsynchronousFileChannelImpl.this.begin();

                  do {
                     var1x = IOUtil.write(SimpleAsynchronousFileChannelImpl.this.fdObj, var1, var2, SimpleAsynchronousFileChannelImpl.nd);
                  } while(var1x == -3 && SimpleAsynchronousFileChannelImpl.this.isOpen());

                  if (var1x < 0 && !SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                     throw new AsynchronousCloseException();
                  }
               } catch (IOException var8x) {
                  Object var4x = var8x;
                  if (!SimpleAsynchronousFileChannelImpl.this.isOpen()) {
                     var4x = new AsynchronousCloseException();
                  }

                  var2x = var4x;
               } finally {
                  SimpleAsynchronousFileChannelImpl.this.end();
                  SimpleAsynchronousFileChannelImpl.this.threads.remove(var3);
               }

               if (var5 == null) {
                  var8.setResult(var1x, (Throwable)var2x);
               } else {
                  Invoker.invokeUnchecked(var5, var4, var1x, (Throwable)var2x);
               }

            }
         };
         this.executor.execute(var7);
         return var8;
      } else {
         ClosedChannelException var6 = this.isOpen() ? null : new ClosedChannelException();
         if (var5 == null) {
            return CompletedFuture.withResult(0, var6);
         } else {
            Invoker.invokeIndirectly((CompletionHandler)var5, (Object)var4, 0, (Throwable)var6, (Executor)this.executor);
            return null;
         }
      }
   }

   private static class DefaultExecutorHolder {
      static final ExecutorService defaultExecutor = ThreadPool.createDefault().executor();
   }
}
