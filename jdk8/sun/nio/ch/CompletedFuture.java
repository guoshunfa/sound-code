package sun.nio.ch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class CompletedFuture<V> implements Future<V> {
   private final V result;
   private final Throwable exc;

   private CompletedFuture(V var1, Throwable var2) {
      this.result = var1;
      this.exc = var2;
   }

   static <V> CompletedFuture<V> withResult(V var0) {
      return new CompletedFuture(var0, (Throwable)null);
   }

   static <V> CompletedFuture<V> withFailure(Throwable var0) {
      if (!(var0 instanceof IOException) && !(var0 instanceof SecurityException)) {
         var0 = new IOException((Throwable)var0);
      }

      return new CompletedFuture((Object)null, (Throwable)var0);
   }

   static <V> CompletedFuture<V> withResult(V var0, Throwable var1) {
      return var1 == null ? withResult(var0) : withFailure(var1);
   }

   public V get() throws ExecutionException {
      if (this.exc != null) {
         throw new ExecutionException(this.exc);
      } else {
         return this.result;
      }
   }

   public V get(long var1, TimeUnit var3) throws ExecutionException {
      if (var3 == null) {
         throw new NullPointerException();
      } else if (this.exc != null) {
         throw new ExecutionException(this.exc);
      } else {
         return this.result;
      }
   }

   public boolean isCancelled() {
      return false;
   }

   public boolean isDone() {
      return true;
   }

   public boolean cancel(boolean var1) {
      return false;
   }
}
