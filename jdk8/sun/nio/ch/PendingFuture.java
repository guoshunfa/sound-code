package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class PendingFuture<V, A> implements Future<V> {
   private static final CancellationException CANCELLED = new CancellationException();
   private final AsynchronousChannel channel;
   private final CompletionHandler<V, ? super A> handler;
   private final A attachment;
   private volatile boolean haveResult;
   private volatile V result;
   private volatile Throwable exc;
   private CountDownLatch latch;
   private Future<?> timeoutTask;
   private volatile Object context;

   PendingFuture(AsynchronousChannel var1, CompletionHandler<V, ? super A> var2, A var3, Object var4) {
      this.channel = var1;
      this.handler = var2;
      this.attachment = var3;
      this.context = var4;
   }

   PendingFuture(AsynchronousChannel var1, CompletionHandler<V, ? super A> var2, A var3) {
      this.channel = var1;
      this.handler = var2;
      this.attachment = var3;
   }

   PendingFuture(AsynchronousChannel var1) {
      this(var1, (CompletionHandler)null, (Object)null);
   }

   PendingFuture(AsynchronousChannel var1, Object var2) {
      this(var1, (CompletionHandler)null, (Object)null, var2);
   }

   AsynchronousChannel channel() {
      return this.channel;
   }

   CompletionHandler<V, ? super A> handler() {
      return this.handler;
   }

   A attachment() {
      return this.attachment;
   }

   void setContext(Object var1) {
      this.context = var1;
   }

   Object getContext() {
      return this.context;
   }

   void setTimeoutTask(Future<?> var1) {
      synchronized(this) {
         if (this.haveResult) {
            var1.cancel(false);
         } else {
            this.timeoutTask = var1;
         }

      }
   }

   private boolean prepareForWait() {
      synchronized(this) {
         if (this.haveResult) {
            return false;
         } else {
            if (this.latch == null) {
               this.latch = new CountDownLatch(1);
            }

            return true;
         }
      }
   }

   void setResult(V var1) {
      synchronized(this) {
         if (!this.haveResult) {
            this.result = var1;
            this.haveResult = true;
            if (this.timeoutTask != null) {
               this.timeoutTask.cancel(false);
            }

            if (this.latch != null) {
               this.latch.countDown();
            }

         }
      }
   }

   void setFailure(Throwable var1) {
      if (!(var1 instanceof IOException) && !(var1 instanceof SecurityException)) {
         var1 = new IOException((Throwable)var1);
      }

      synchronized(this) {
         if (!this.haveResult) {
            this.exc = (Throwable)var1;
            this.haveResult = true;
            if (this.timeoutTask != null) {
               this.timeoutTask.cancel(false);
            }

            if (this.latch != null) {
               this.latch.countDown();
            }

         }
      }
   }

   void setResult(V var1, Throwable var2) {
      if (var2 == null) {
         this.setResult(var1);
      } else {
         this.setFailure(var2);
      }

   }

   public V get() throws ExecutionException, InterruptedException {
      if (!this.haveResult) {
         boolean var1 = this.prepareForWait();
         if (var1) {
            this.latch.await();
         }
      }

      if (this.exc != null) {
         if (this.exc == CANCELLED) {
            throw new CancellationException();
         } else {
            throw new ExecutionException(this.exc);
         }
      } else {
         return this.result;
      }
   }

   public V get(long var1, TimeUnit var3) throws ExecutionException, InterruptedException, TimeoutException {
      if (!this.haveResult) {
         boolean var4 = this.prepareForWait();
         if (var4 && !this.latch.await(var1, var3)) {
            throw new TimeoutException();
         }
      }

      if (this.exc != null) {
         if (this.exc == CANCELLED) {
            throw new CancellationException();
         } else {
            throw new ExecutionException(this.exc);
         }
      } else {
         return this.result;
      }
   }

   Throwable exception() {
      return this.exc != CANCELLED ? this.exc : null;
   }

   V value() {
      return this.result;
   }

   public boolean isCancelled() {
      return this.exc == CANCELLED;
   }

   public boolean isDone() {
      return this.haveResult;
   }

   public boolean cancel(boolean var1) {
      synchronized(this) {
         if (this.haveResult) {
            return false;
         }

         if (this.channel() instanceof Cancellable) {
            ((Cancellable)this.channel()).onCancel(this);
         }

         this.exc = CANCELLED;
         this.haveResult = true;
         if (this.timeoutTask != null) {
            this.timeoutTask.cancel(false);
         }
      }

      if (var1) {
         try {
            this.channel().close();
         } catch (IOException var4) {
         }
      }

      if (this.latch != null) {
         this.latch.countDown();
      }

      return true;
   }
}
