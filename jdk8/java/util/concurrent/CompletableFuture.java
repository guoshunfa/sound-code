package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import sun.misc.Unsafe;

public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
   volatile Object result;
   volatile CompletableFuture.Completion stack;
   static final CompletableFuture.AltResult NIL = new CompletableFuture.AltResult((Throwable)null);
   private static final boolean useCommonPool = ForkJoinPool.getCommonPoolParallelism() > 1;
   private static final Executor asyncPool;
   static final int SYNC = 0;
   static final int ASYNC = 1;
   static final int NESTED = -1;
   private static final Unsafe UNSAFE;
   private static final long RESULT;
   private static final long STACK;
   private static final long NEXT;

   final boolean internalComplete(Object var1) {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, var1);
   }

   final boolean casStack(CompletableFuture.Completion var1, CompletableFuture.Completion var2) {
      return UNSAFE.compareAndSwapObject(this, STACK, var1, var2);
   }

   final boolean tryPushStack(CompletableFuture.Completion var1) {
      CompletableFuture.Completion var2 = this.stack;
      lazySetNext(var1, var2);
      return UNSAFE.compareAndSwapObject(this, STACK, var2, var1);
   }

   final void pushStack(CompletableFuture.Completion var1) {
      while(!this.tryPushStack(var1)) {
      }

   }

   final boolean completeNull() {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, NIL);
   }

   final Object encodeValue(T var1) {
      return var1 == null ? NIL : var1;
   }

   final boolean completeValue(T var1) {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, var1 == null ? NIL : var1);
   }

   static CompletableFuture.AltResult encodeThrowable(Throwable var0) {
      return new CompletableFuture.AltResult((Throwable)(var0 instanceof CompletionException ? var0 : new CompletionException(var0)));
   }

   final boolean completeThrowable(Throwable var1) {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, encodeThrowable(var1));
   }

   static Object encodeThrowable(Throwable var0, Object var1) {
      if (!(var0 instanceof CompletionException)) {
         var0 = new CompletionException((Throwable)var0);
      } else if (var1 instanceof CompletableFuture.AltResult && var0 == ((CompletableFuture.AltResult)var1).ex) {
         return var1;
      }

      return new CompletableFuture.AltResult((Throwable)var0);
   }

   final boolean completeThrowable(Throwable var1, Object var2) {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, encodeThrowable(var1, var2));
   }

   Object encodeOutcome(T var1, Throwable var2) {
      return var2 == null ? (var1 == null ? NIL : var1) : encodeThrowable(var2);
   }

   static Object encodeRelay(Object var0) {
      Throwable var1;
      return var0 instanceof CompletableFuture.AltResult && (var1 = ((CompletableFuture.AltResult)var0).ex) != null && !(var1 instanceof CompletionException) ? new CompletableFuture.AltResult(new CompletionException(var1)) : var0;
   }

   final boolean completeRelay(Object var1) {
      return UNSAFE.compareAndSwapObject(this, RESULT, (Object)null, encodeRelay(var1));
   }

   private static <T> T reportGet(Object var0) throws InterruptedException, ExecutionException {
      if (var0 == null) {
         throw new InterruptedException();
      } else if (var0 instanceof CompletableFuture.AltResult) {
         Throwable var1;
         if ((var1 = ((CompletableFuture.AltResult)var0).ex) == null) {
            return null;
         } else if (var1 instanceof CancellationException) {
            throw (CancellationException)var1;
         } else {
            Throwable var2;
            if (var1 instanceof CompletionException && (var2 = var1.getCause()) != null) {
               var1 = var2;
            }

            throw new ExecutionException(var1);
         }
      } else {
         return var0;
      }
   }

   private static <T> T reportJoin(Object var0) {
      if (var0 instanceof CompletableFuture.AltResult) {
         Throwable var1;
         if ((var1 = ((CompletableFuture.AltResult)var0).ex) == null) {
            return null;
         } else if (var1 instanceof CancellationException) {
            throw (CancellationException)var1;
         } else if (var1 instanceof CompletionException) {
            throw (CompletionException)var1;
         } else {
            throw new CompletionException(var1);
         }
      } else {
         return var0;
      }
   }

   static Executor screenExecutor(Executor var0) {
      if (!useCommonPool && var0 == ForkJoinPool.commonPool()) {
         return asyncPool;
      } else if (var0 == null) {
         throw new NullPointerException();
      } else {
         return var0;
      }
   }

   static void lazySetNext(CompletableFuture.Completion var0, CompletableFuture.Completion var1) {
      UNSAFE.putOrderedObject(var0, NEXT, var1);
   }

   final void postComplete() {
      CompletableFuture var1 = this;

      while(true) {
         CompletableFuture.Completion var2;
         if ((var2 = var1.stack) == null) {
            if (var1 == this) {
               break;
            }

            var1 = this;
            if ((var2 = this.stack) == null) {
               break;
            }
         }

         CompletableFuture.Completion var4;
         if (var1.casStack(var2, var4 = var2.next)) {
            if (var4 != null) {
               if (var1 != this) {
                  this.pushStack(var2);
                  continue;
               }

               var2.next = null;
            }

            CompletableFuture var3;
            var1 = (var3 = var2.tryFire(-1)) == null ? this : var3;
         }
      }

   }

   final void cleanStack() {
      CompletableFuture.Completion var1 = null;
      CompletableFuture.Completion var2 = this.stack;

      while(var2 != null) {
         CompletableFuture.Completion var3 = var2.next;
         if (var2.isLive()) {
            var1 = var2;
            var2 = var3;
         } else if (var1 == null) {
            this.casStack(var2, var3);
            var2 = this.stack;
         } else {
            var1.next = var3;
            if (var1.isLive()) {
               var2 = var3;
            } else {
               var1 = null;
               var2 = this.stack;
            }
         }
      }

   }

   final void push(CompletableFuture.UniCompletion<?, ?> var1) {
      if (var1 != null) {
         while(this.result == null && !this.tryPushStack(var1)) {
            lazySetNext(var1, (CompletableFuture.Completion)null);
         }
      }

   }

   final CompletableFuture<T> postFire(CompletableFuture<?> var1, int var2) {
      if (var1 != null && var1.stack != null) {
         if (var2 >= 0 && var1.result != null) {
            var1.postComplete();
         } else {
            var1.cleanStack();
         }
      }

      if (this.result != null && this.stack != null) {
         if (var2 < 0) {
            return this;
         }

         this.postComplete();
      }

      return null;
   }

   final <S> boolean uniApply(CompletableFuture<S> var1, Function<? super S, ? extends T> var2, CompletableFuture.UniApply<S, T> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            if (var4 instanceof CompletableFuture.AltResult) {
               Throwable var5;
               if ((var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
                  this.completeThrowable(var5, var4);
                  return true;
               }

               var4 = null;
            }

            try {
               if (var3 != null && !var3.claim()) {
                  return false;
               }

               this.completeValue(var2.apply(var4));
            } catch (Throwable var7) {
               this.completeThrowable(var7);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <V> CompletableFuture<V> uniApplyStage(Executor var1, Function<? super T, ? extends V> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (var1 != null || !var3.uniApply(this, var2, (CompletableFuture.UniApply)null)) {
            CompletableFuture.UniApply var4 = new CompletableFuture.UniApply(var1, var3, this, var2);
            this.push(var4);
            var4.tryFire(0);
         }

         return var3;
      }
   }

   final <S> boolean uniAccept(CompletableFuture<S> var1, Consumer<? super S> var2, CompletableFuture.UniAccept<S> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            if (var4 instanceof CompletableFuture.AltResult) {
               Throwable var5;
               if ((var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
                  this.completeThrowable(var5, var4);
                  return true;
               }

               var4 = null;
            }

            try {
               if (var3 != null && !var3.claim()) {
                  return false;
               }

               var2.accept(var4);
               this.completeNull();
            } catch (Throwable var7) {
               this.completeThrowable(var7);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<Void> uniAcceptStage(Executor var1, Consumer<? super T> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (var1 != null || !var3.uniAccept(this, var2, (CompletableFuture.UniAccept)null)) {
            CompletableFuture.UniAccept var4 = new CompletableFuture.UniAccept(var1, var3, this, var2);
            this.push(var4);
            var4.tryFire(0);
         }

         return var3;
      }
   }

   final boolean uniRun(CompletableFuture<?> var1, Runnable var2, CompletableFuture.UniRun<?> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            Throwable var5;
            if (var4 instanceof CompletableFuture.AltResult && (var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
               this.completeThrowable(var5, var4);
            } else {
               try {
                  if (var3 != null && !var3.claim()) {
                     return false;
                  }

                  var2.run();
                  this.completeNull();
               } catch (Throwable var7) {
                  this.completeThrowable(var7);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<Void> uniRunStage(Executor var1, Runnable var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (var1 != null || !var3.uniRun(this, var2, (CompletableFuture.UniRun)null)) {
            CompletableFuture.UniRun var4 = new CompletableFuture.UniRun(var1, var3, this, var2);
            this.push(var4);
            var4.tryFire(0);
         }

         return var3;
      }
   }

   final boolean uniWhenComplete(CompletableFuture<T> var1, BiConsumer<? super T, ? super Throwable> var2, CompletableFuture.UniWhenComplete<T> var3) {
      Throwable var6 = null;
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            try {
               if (var3 != null && !var3.claim()) {
                  return false;
               }

               Object var5;
               if (var4 instanceof CompletableFuture.AltResult) {
                  var6 = ((CompletableFuture.AltResult)var4).ex;
                  var5 = null;
               } else {
                  var5 = var4;
               }

               var2.accept(var5, var6);
               if (var6 == null) {
                  this.internalComplete(var4);
                  return true;
               }
            } catch (Throwable var8) {
               if (var6 == null) {
                  var6 = var8;
               }
            }

            this.completeThrowable(var6, var4);
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<T> uniWhenCompleteStage(Executor var1, BiConsumer<? super T, ? super Throwable> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (var1 != null || !var3.uniWhenComplete(this, var2, (CompletableFuture.UniWhenComplete)null)) {
            CompletableFuture.UniWhenComplete var4 = new CompletableFuture.UniWhenComplete(var1, var3, this, var2);
            this.push(var4);
            var4.tryFire(0);
         }

         return var3;
      }
   }

   final <S> boolean uniHandle(CompletableFuture<S> var1, BiFunction<? super S, Throwable, ? extends T> var2, CompletableFuture.UniHandle<S, T> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            try {
               if (var3 != null && !var3.claim()) {
                  return false;
               }

               Object var5;
               Throwable var6;
               if (var4 instanceof CompletableFuture.AltResult) {
                  var6 = ((CompletableFuture.AltResult)var4).ex;
                  var5 = null;
               } else {
                  var6 = null;
                  var5 = var4;
               }

               this.completeValue(var2.apply(var5, var6));
            } catch (Throwable var8) {
               this.completeThrowable(var8);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <V> CompletableFuture<V> uniHandleStage(Executor var1, BiFunction<? super T, Throwable, ? extends V> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (var1 != null || !var3.uniHandle(this, var2, (CompletableFuture.UniHandle)null)) {
            CompletableFuture.UniHandle var4 = new CompletableFuture.UniHandle(var1, var3, this, var2);
            this.push(var4);
            var4.tryFire(0);
         }

         return var3;
      }
   }

   final boolean uniExceptionally(CompletableFuture<T> var1, Function<? super Throwable, ? extends T> var2, CompletableFuture.UniExceptionally<T> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            try {
               Throwable var5;
               if (var4 instanceof CompletableFuture.AltResult && (var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
                  if (var3 != null && !var3.claim()) {
                     return false;
                  }

                  this.completeValue(var2.apply(var5));
               } else {
                  this.internalComplete(var4);
               }
            } catch (Throwable var7) {
               this.completeThrowable(var7);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<T> uniExceptionallyStage(Function<Throwable, ? extends T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var2 = new CompletableFuture();
         if (!var2.uniExceptionally(this, var1, (CompletableFuture.UniExceptionally)null)) {
            CompletableFuture.UniExceptionally var3 = new CompletableFuture.UniExceptionally(var2, this, var1);
            this.push(var3);
            var3.tryFire(0);
         }

         return var2;
      }
   }

   final boolean uniRelay(CompletableFuture<T> var1) {
      Object var2;
      if (var1 != null && (var2 = var1.result) != null) {
         if (this.result == null) {
            this.completeRelay(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   final <S> boolean uniCompose(CompletableFuture<S> var1, Function<? super S, ? extends CompletionStage<T>> var2, CompletableFuture.UniCompose<S, T> var3) {
      Object var4;
      if (var1 != null && (var4 = var1.result) != null && var2 != null) {
         if (this.result == null) {
            if (var4 instanceof CompletableFuture.AltResult) {
               Throwable var5;
               if ((var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
                  this.completeThrowable(var5, var4);
                  return true;
               }

               var4 = null;
            }

            try {
               if (var3 != null && !var3.claim()) {
                  return false;
               }

               CompletableFuture var7 = ((CompletionStage)var2.apply(var4)).toCompletableFuture();
               if (var7.result == null || !this.uniRelay(var7)) {
                  CompletableFuture.UniRelay var8 = new CompletableFuture.UniRelay(this, var7);
                  var7.push(var8);
                  var8.tryFire(0);
                  if (this.result == null) {
                     return false;
                  }
               }
            } catch (Throwable var9) {
               this.completeThrowable(var9);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <V> CompletableFuture<V> uniComposeStage(Executor var1, Function<? super T, ? extends CompletionStage<V>> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         Object var3;
         if (var1 == null && (var3 = this.result) != null) {
            if (var3 instanceof CompletableFuture.AltResult) {
               Throwable var4;
               if ((var4 = ((CompletableFuture.AltResult)var3).ex) != null) {
                  return new CompletableFuture(encodeThrowable(var4, var3));
               }

               var3 = null;
            }

            try {
               CompletableFuture var11 = ((CompletionStage)var2.apply(var3)).toCompletableFuture();
               Object var7 = var11.result;
               if (var7 != null) {
                  return new CompletableFuture(encodeRelay(var7));
               } else {
                  CompletableFuture var8 = new CompletableFuture();
                  CompletableFuture.UniRelay var9 = new CompletableFuture.UniRelay(var8, var11);
                  var11.push(var9);
                  var9.tryFire(0);
                  return var8;
               }
            } catch (Throwable var10) {
               return new CompletableFuture(encodeThrowable(var10));
            }
         } else {
            CompletableFuture var5 = new CompletableFuture();
            CompletableFuture.UniCompose var6 = new CompletableFuture.UniCompose(var1, var5, this, var2);
            this.push(var6);
            var6.tryFire(0);
            return var5;
         }
      }
   }

   final void bipush(CompletableFuture<?> var1, CompletableFuture.BiCompletion<?, ?, ?> var2) {
      if (var2 != null) {
         while(true) {
            Object var3;
            if ((var3 = this.result) != null || this.tryPushStack(var2)) {
               if (var1 != null && var1 != this && var1.result == null) {
                  Object var4 = var3 != null ? var2 : new CompletableFuture.CoCompletion(var2);

                  while(var1.result == null && !var1.tryPushStack((CompletableFuture.Completion)var4)) {
                     lazySetNext((CompletableFuture.Completion)var4, (CompletableFuture.Completion)null);
                  }
               }
               break;
            }

            lazySetNext(var2, (CompletableFuture.Completion)null);
         }
      }

   }

   final CompletableFuture<T> postFire(CompletableFuture<?> var1, CompletableFuture<?> var2, int var3) {
      if (var2 != null && var2.stack != null) {
         if (var3 >= 0 && var2.result != null) {
            var2.postComplete();
         } else {
            var2.cleanStack();
         }
      }

      return this.postFire(var1, var3);
   }

   final <R, S> boolean biApply(CompletableFuture<R> var1, CompletableFuture<S> var2, BiFunction<? super R, ? super S, ? extends T> var3, CompletableFuture.BiApply<R, S, T> var4) {
      Object var5;
      Object var6;
      if (var1 != null && (var5 = var1.result) != null && var2 != null && (var6 = var2.result) != null && var3 != null) {
         if (this.result == null) {
            Throwable var7;
            if (var5 instanceof CompletableFuture.AltResult) {
               if ((var7 = ((CompletableFuture.AltResult)var5).ex) != null) {
                  this.completeThrowable(var7, var5);
                  return true;
               }

               var5 = null;
            }

            if (var6 instanceof CompletableFuture.AltResult) {
               if ((var7 = ((CompletableFuture.AltResult)var6).ex) != null) {
                  this.completeThrowable(var7, var6);
                  return true;
               }

               var6 = null;
            }

            try {
               if (var4 != null && !var4.claim()) {
                  return false;
               }

               this.completeValue(var3.apply(var5, var6));
            } catch (Throwable var10) {
               this.completeThrowable(var10);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <U, V> CompletableFuture<V> biApplyStage(Executor var1, CompletionStage<U> var2, BiFunction<? super T, ? super U, ? extends V> var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.biApply(this, var4, var3, (CompletableFuture.BiApply)null)) {
            CompletableFuture.BiApply var6 = new CompletableFuture.BiApply(var1, var5, this, var4, var3);
            this.bipush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   final <R, S> boolean biAccept(CompletableFuture<R> var1, CompletableFuture<S> var2, BiConsumer<? super R, ? super S> var3, CompletableFuture.BiAccept<R, S> var4) {
      Object var5;
      Object var6;
      if (var1 != null && (var5 = var1.result) != null && var2 != null && (var6 = var2.result) != null && var3 != null) {
         if (this.result == null) {
            Throwable var7;
            if (var5 instanceof CompletableFuture.AltResult) {
               if ((var7 = ((CompletableFuture.AltResult)var5).ex) != null) {
                  this.completeThrowable(var7, var5);
                  return true;
               }

               var5 = null;
            }

            if (var6 instanceof CompletableFuture.AltResult) {
               if ((var7 = ((CompletableFuture.AltResult)var6).ex) != null) {
                  this.completeThrowable(var7, var6);
                  return true;
               }

               var6 = null;
            }

            try {
               if (var4 != null && !var4.claim()) {
                  return false;
               }

               var3.accept(var5, var6);
               this.completeNull();
            } catch (Throwable var10) {
               this.completeThrowable(var10);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <U> CompletableFuture<Void> biAcceptStage(Executor var1, CompletionStage<U> var2, BiConsumer<? super T, ? super U> var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.biAccept(this, var4, var3, (CompletableFuture.BiAccept)null)) {
            CompletableFuture.BiAccept var6 = new CompletableFuture.BiAccept(var1, var5, this, var4, var3);
            this.bipush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   final boolean biRun(CompletableFuture<?> var1, CompletableFuture<?> var2, Runnable var3, CompletableFuture.BiRun<?, ?> var4) {
      Object var5;
      Object var6;
      if (var1 != null && (var5 = var1.result) != null && var2 != null && (var6 = var2.result) != null && var3 != null) {
         if (this.result == null) {
            Throwable var7;
            if (var5 instanceof CompletableFuture.AltResult && (var7 = ((CompletableFuture.AltResult)var5).ex) != null) {
               this.completeThrowable(var7, var5);
            } else if (var6 instanceof CompletableFuture.AltResult && (var7 = ((CompletableFuture.AltResult)var6).ex) != null) {
               this.completeThrowable(var7, var6);
            } else {
               try {
                  if (var4 != null && !var4.claim()) {
                     return false;
                  }

                  var3.run();
                  this.completeNull();
               } catch (Throwable var9) {
                  this.completeThrowable(var9);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<Void> biRunStage(Executor var1, CompletionStage<?> var2, Runnable var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.biRun(this, var4, var3, (CompletableFuture.BiRun)null)) {
            CompletableFuture.BiRun var6 = new CompletableFuture.BiRun(var1, var5, this, var4, var3);
            this.bipush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   boolean biRelay(CompletableFuture<?> var1, CompletableFuture<?> var2) {
      Object var3;
      Object var4;
      if (var1 != null && (var3 = var1.result) != null && var2 != null && (var4 = var2.result) != null) {
         if (this.result == null) {
            Throwable var5;
            if (var3 instanceof CompletableFuture.AltResult && (var5 = ((CompletableFuture.AltResult)var3).ex) != null) {
               this.completeThrowable(var5, var3);
            } else if (var4 instanceof CompletableFuture.AltResult && (var5 = ((CompletableFuture.AltResult)var4).ex) != null) {
               this.completeThrowable(var5, var4);
            } else {
               this.completeNull();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static CompletableFuture<Void> andTree(CompletableFuture<?>[] var0, int var1, int var2) {
      CompletableFuture var3 = new CompletableFuture();
      if (var1 > var2) {
         var3.result = NIL;
      } else {
         int var6 = var1 + var2 >>> 1;
         CompletableFuture var10000 = var1 == var6 ? var0[var1] : andTree(var0, var1, var6);
         CompletableFuture var4 = var10000;
         CompletableFuture var5;
         if (var10000 == null || (var5 = var1 == var2 ? var4 : (var2 == var6 + 1 ? var0[var2] : andTree(var0, var6 + 1, var2))) == null) {
            throw new NullPointerException();
         }

         if (!var3.biRelay(var4, var5)) {
            CompletableFuture.BiRelay var7 = new CompletableFuture.BiRelay(var3, var4, var5);
            var4.bipush(var5, var7);
            var7.tryFire(0);
         }
      }

      return var3;
   }

   final void orpush(CompletableFuture<?> var1, CompletableFuture.BiCompletion<?, ?, ?> var2) {
      if (var2 != null) {
         while((var1 == null || var1.result == null) && this.result == null) {
            if (this.tryPushStack(var2)) {
               if (var1 != null && var1 != this && var1.result == null) {
                  CompletableFuture.CoCompletion var3 = new CompletableFuture.CoCompletion(var2);

                  while(this.result == null && var1.result == null && !var1.tryPushStack(var3)) {
                     lazySetNext(var3, (CompletableFuture.Completion)null);
                  }
               }
               break;
            }

            lazySetNext(var2, (CompletableFuture.Completion)null);
         }
      }

   }

   final <R, S extends R> boolean orApply(CompletableFuture<R> var1, CompletableFuture<S> var2, Function<? super R, ? extends T> var3, CompletableFuture.OrApply<R, S, T> var4) {
      Object var5;
      if (var1 != null && var2 != null && ((var5 = var1.result) != null || (var5 = var2.result) != null) && var3 != null) {
         if (this.result == null) {
            try {
               if (var4 != null && !var4.claim()) {
                  return false;
               }

               if (var5 instanceof CompletableFuture.AltResult) {
                  Throwable var6;
                  if ((var6 = ((CompletableFuture.AltResult)var5).ex) != null) {
                     this.completeThrowable(var6, var5);
                     return true;
                  }

                  var5 = null;
               }

               this.completeValue(var3.apply(var5));
            } catch (Throwable var8) {
               this.completeThrowable(var8);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <U extends T, V> CompletableFuture<V> orApplyStage(Executor var1, CompletionStage<U> var2, Function<? super T, ? extends V> var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.orApply(this, var4, var3, (CompletableFuture.OrApply)null)) {
            CompletableFuture.OrApply var6 = new CompletableFuture.OrApply(var1, var5, this, var4, var3);
            this.orpush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   final <R, S extends R> boolean orAccept(CompletableFuture<R> var1, CompletableFuture<S> var2, Consumer<? super R> var3, CompletableFuture.OrAccept<R, S> var4) {
      Object var5;
      if (var1 != null && var2 != null && ((var5 = var1.result) != null || (var5 = var2.result) != null) && var3 != null) {
         if (this.result == null) {
            try {
               if (var4 != null && !var4.claim()) {
                  return false;
               }

               if (var5 instanceof CompletableFuture.AltResult) {
                  Throwable var6;
                  if ((var6 = ((CompletableFuture.AltResult)var5).ex) != null) {
                     this.completeThrowable(var6, var5);
                     return true;
                  }

                  var5 = null;
               }

               var3.accept(var5);
               this.completeNull();
            } catch (Throwable var8) {
               this.completeThrowable(var8);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private <U extends T> CompletableFuture<Void> orAcceptStage(Executor var1, CompletionStage<U> var2, Consumer<? super T> var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.orAccept(this, var4, var3, (CompletableFuture.OrAccept)null)) {
            CompletableFuture.OrAccept var6 = new CompletableFuture.OrAccept(var1, var5, this, var4, var3);
            this.orpush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   final boolean orRun(CompletableFuture<?> var1, CompletableFuture<?> var2, Runnable var3, CompletableFuture.OrRun<?, ?> var4) {
      Object var5;
      if (var1 != null && var2 != null && ((var5 = var1.result) != null || (var5 = var2.result) != null) && var3 != null) {
         if (this.result == null) {
            try {
               if (var4 != null && !var4.claim()) {
                  return false;
               }

               Throwable var6;
               if (var5 instanceof CompletableFuture.AltResult && (var6 = ((CompletableFuture.AltResult)var5).ex) != null) {
                  this.completeThrowable(var6, var5);
               } else {
                  var3.run();
                  this.completeNull();
               }
            } catch (Throwable var8) {
               this.completeThrowable(var8);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<Void> orRunStage(Executor var1, CompletionStage<?> var2, Runnable var3) {
      CompletableFuture var4;
      if (var3 != null && (var4 = var2.toCompletableFuture()) != null) {
         CompletableFuture var5 = new CompletableFuture();
         if (var1 != null || !var5.orRun(this, var4, var3, (CompletableFuture.OrRun)null)) {
            CompletableFuture.OrRun var6 = new CompletableFuture.OrRun(var1, var5, this, var4, var3);
            this.orpush(var4, var6);
            var6.tryFire(0);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   final boolean orRelay(CompletableFuture<?> var1, CompletableFuture<?> var2) {
      Object var3;
      if (var1 != null && var2 != null && ((var3 = var1.result) != null || (var3 = var2.result) != null)) {
         if (this.result == null) {
            this.completeRelay(var3);
         }

         return true;
      } else {
         return false;
      }
   }

   static CompletableFuture<Object> orTree(CompletableFuture<?>[] var0, int var1, int var2) {
      CompletableFuture var3 = new CompletableFuture();
      if (var1 <= var2) {
         int var6 = var1 + var2 >>> 1;
         CompletableFuture var10000 = var1 == var6 ? var0[var1] : orTree(var0, var1, var6);
         CompletableFuture var4 = var10000;
         CompletableFuture var5;
         if (var10000 == null || (var5 = var1 == var2 ? var4 : (var2 == var6 + 1 ? var0[var2] : orTree(var0, var6 + 1, var2))) == null) {
            throw new NullPointerException();
         }

         if (!var3.orRelay(var4, var5)) {
            CompletableFuture.OrRelay var7 = new CompletableFuture.OrRelay(var3, var4, var5);
            var4.orpush(var5, var7);
            var7.tryFire(0);
         }
      }

      return var3;
   }

   static <U> CompletableFuture<U> asyncSupplyStage(Executor var0, Supplier<U> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var2 = new CompletableFuture();
         var0.execute(new CompletableFuture.AsyncSupply(var2, var1));
         return var2;
      }
   }

   static CompletableFuture<Void> asyncRunStage(Executor var0, Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         CompletableFuture var2 = new CompletableFuture();
         var0.execute(new CompletableFuture.AsyncRun(var2, var1));
         return var2;
      }
   }

   private Object waitingGet(boolean var1) {
      CompletableFuture.Signaller var2 = null;
      boolean var3 = false;
      int var4 = -1;

      Object var5;
      while((var5 = this.result) == null) {
         if (var4 < 0) {
            var4 = Runtime.getRuntime().availableProcessors() > 1 ? 256 : 0;
         } else if (var4 > 0) {
            if (ThreadLocalRandom.nextSecondarySeed() >= 0) {
               --var4;
            }
         } else if (var2 == null) {
            var2 = new CompletableFuture.Signaller(var1, 0L, 0L);
         } else if (!var3) {
            var3 = this.tryPushStack(var2);
         } else {
            if (var1 && var2.interruptControl < 0) {
               var2.thread = null;
               this.cleanStack();
               return null;
            }

            if (var2.thread != null && this.result == null) {
               try {
                  ForkJoinPool.managedBlock(var2);
               } catch (InterruptedException var7) {
                  var2.interruptControl = -1;
               }
            }
         }
      }

      if (var2 != null) {
         var2.thread = null;
         if (var2.interruptControl < 0) {
            if (var1) {
               var5 = null;
            } else {
               Thread.currentThread().interrupt();
            }
         }
      }

      this.postComplete();
      return var5;
   }

   private Object timedGet(long var1) throws TimeoutException {
      if (Thread.interrupted()) {
         return null;
      } else if (var1 <= 0L) {
         throw new TimeoutException();
      } else {
         long var3 = System.nanoTime() + var1;
         CompletableFuture.Signaller var5 = new CompletableFuture.Signaller(true, var1, var3 == 0L ? 1L : var3);
         boolean var6 = false;

         while(true) {
            Object var7;
            while((var7 = this.result) == null) {
               if (var6) {
                  if (var5.interruptControl < 0 || var5.nanos <= 0L) {
                     var5.thread = null;
                     this.cleanStack();
                     if (var5.interruptControl < 0) {
                        return null;
                     } else {
                        throw new TimeoutException();
                     }
                  }

                  if (var5.thread != null && this.result == null) {
                     try {
                        ForkJoinPool.managedBlock(var5);
                     } catch (InterruptedException var9) {
                        var5.interruptControl = -1;
                     }
                  }
               } else {
                  var6 = this.tryPushStack(var5);
               }
            }

            if (var5.interruptControl < 0) {
               var7 = null;
            }

            var5.thread = null;
            this.postComplete();
            return var7;
         }
      }
   }

   public CompletableFuture() {
   }

   private CompletableFuture(Object var1) {
      this.result = var1;
   }

   public static <U> CompletableFuture<U> supplyAsync(Supplier<U> var0) {
      return asyncSupplyStage(asyncPool, var0);
   }

   public static <U> CompletableFuture<U> supplyAsync(Supplier<U> var0, Executor var1) {
      return asyncSupplyStage(screenExecutor(var1), var0);
   }

   public static CompletableFuture<Void> runAsync(Runnable var0) {
      return asyncRunStage(asyncPool, var0);
   }

   public static CompletableFuture<Void> runAsync(Runnable var0, Executor var1) {
      return asyncRunStage(screenExecutor(var1), var0);
   }

   public static <U> CompletableFuture<U> completedFuture(U var0) {
      return new CompletableFuture(var0 == null ? NIL : var0);
   }

   public boolean isDone() {
      return this.result != null;
   }

   public T get() throws InterruptedException, ExecutionException {
      Object var1;
      return reportGet((var1 = this.result) == null ? this.waitingGet(true) : var1);
   }

   public T get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      long var5 = var3.toNanos(var1);
      Object var4;
      return reportGet((var4 = this.result) == null ? this.timedGet(var5) : var4);
   }

   public T join() {
      Object var1;
      return reportJoin((var1 = this.result) == null ? this.waitingGet(false) : var1);
   }

   public T getNow(T var1) {
      Object var2;
      return (var2 = this.result) == null ? var1 : reportJoin(var2);
   }

   public boolean complete(T var1) {
      boolean var2 = this.completeValue(var1);
      this.postComplete();
      return var2;
   }

   public boolean completeExceptionally(Throwable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = this.internalComplete(new CompletableFuture.AltResult(var1));
         this.postComplete();
         return var2;
      }
   }

   public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> var1) {
      return this.uniApplyStage((Executor)null, var1);
   }

   public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> var1) {
      return this.uniApplyStage(asyncPool, var1);
   }

   public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> var1, Executor var2) {
      return this.uniApplyStage(screenExecutor(var2), var1);
   }

   public CompletableFuture<Void> thenAccept(Consumer<? super T> var1) {
      return this.uniAcceptStage((Executor)null, var1);
   }

   public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> var1) {
      return this.uniAcceptStage(asyncPool, var1);
   }

   public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> var1, Executor var2) {
      return this.uniAcceptStage(screenExecutor(var2), var1);
   }

   public CompletableFuture<Void> thenRun(Runnable var1) {
      return this.uniRunStage((Executor)null, var1);
   }

   public CompletableFuture<Void> thenRunAsync(Runnable var1) {
      return this.uniRunStage(asyncPool, var1);
   }

   public CompletableFuture<Void> thenRunAsync(Runnable var1, Executor var2) {
      return this.uniRunStage(screenExecutor(var2), var1);
   }

   public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> var1, BiFunction<? super T, ? super U, ? extends V> var2) {
      return this.biApplyStage((Executor)null, var1, var2);
   }

   public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> var1, BiFunction<? super T, ? super U, ? extends V> var2) {
      return this.biApplyStage(asyncPool, var1, var2);
   }

   public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> var1, BiFunction<? super T, ? super U, ? extends V> var2, Executor var3) {
      return this.biApplyStage(screenExecutor(var3), var1, var2);
   }

   public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> var1, BiConsumer<? super T, ? super U> var2) {
      return this.biAcceptStage((Executor)null, var1, var2);
   }

   public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> var1, BiConsumer<? super T, ? super U> var2) {
      return this.biAcceptStage(asyncPool, var1, var2);
   }

   public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> var1, BiConsumer<? super T, ? super U> var2, Executor var3) {
      return this.biAcceptStage(screenExecutor(var3), var1, var2);
   }

   public CompletableFuture<Void> runAfterBoth(CompletionStage<?> var1, Runnable var2) {
      return this.biRunStage((Executor)null, var1, var2);
   }

   public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> var1, Runnable var2) {
      return this.biRunStage(asyncPool, var1, var2);
   }

   public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> var1, Runnable var2, Executor var3) {
      return this.biRunStage(screenExecutor(var3), var1, var2);
   }

   public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> var1, Function<? super T, U> var2) {
      return this.orApplyStage((Executor)null, var1, var2);
   }

   public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> var1, Function<? super T, U> var2) {
      return this.orApplyStage(asyncPool, var1, var2);
   }

   public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> var1, Function<? super T, U> var2, Executor var3) {
      return this.orApplyStage(screenExecutor(var3), var1, var2);
   }

   public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> var1, Consumer<? super T> var2) {
      return this.orAcceptStage((Executor)null, var1, var2);
   }

   public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> var1, Consumer<? super T> var2) {
      return this.orAcceptStage(asyncPool, var1, var2);
   }

   public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> var1, Consumer<? super T> var2, Executor var3) {
      return this.orAcceptStage(screenExecutor(var3), var1, var2);
   }

   public CompletableFuture<Void> runAfterEither(CompletionStage<?> var1, Runnable var2) {
      return this.orRunStage((Executor)null, var1, var2);
   }

   public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> var1, Runnable var2) {
      return this.orRunStage(asyncPool, var1, var2);
   }

   public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> var1, Runnable var2, Executor var3) {
      return this.orRunStage(screenExecutor(var3), var1, var2);
   }

   public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> var1) {
      return this.uniComposeStage((Executor)null, var1);
   }

   public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> var1) {
      return this.uniComposeStage(asyncPool, var1);
   }

   public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> var1, Executor var2) {
      return this.uniComposeStage(screenExecutor(var2), var1);
   }

   public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> var1) {
      return this.uniWhenCompleteStage((Executor)null, var1);
   }

   public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> var1) {
      return this.uniWhenCompleteStage(asyncPool, var1);
   }

   public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> var1, Executor var2) {
      return this.uniWhenCompleteStage(screenExecutor(var2), var1);
   }

   public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> var1) {
      return this.uniHandleStage((Executor)null, var1);
   }

   public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> var1) {
      return this.uniHandleStage(asyncPool, var1);
   }

   public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> var1, Executor var2) {
      return this.uniHandleStage(screenExecutor(var2), var1);
   }

   public CompletableFuture<T> toCompletableFuture() {
      return this;
   }

   public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> var1) {
      return this.uniExceptionallyStage(var1);
   }

   public static CompletableFuture<Void> allOf(CompletableFuture<?>... var0) {
      return andTree(var0, 0, var0.length - 1);
   }

   public static CompletableFuture<Object> anyOf(CompletableFuture<?>... var0) {
      return orTree(var0, 0, var0.length - 1);
   }

   public boolean cancel(boolean var1) {
      boolean var2 = this.result == null && this.internalComplete(new CompletableFuture.AltResult(new CancellationException()));
      this.postComplete();
      return var2 || this.isCancelled();
   }

   public boolean isCancelled() {
      Object var1;
      return (var1 = this.result) instanceof CompletableFuture.AltResult && ((CompletableFuture.AltResult)var1).ex instanceof CancellationException;
   }

   public boolean isCompletedExceptionally() {
      Object var1;
      return (var1 = this.result) instanceof CompletableFuture.AltResult && var1 != NIL;
   }

   public void obtrudeValue(T var1) {
      this.result = var1 == null ? NIL : var1;
      this.postComplete();
   }

   public void obtrudeException(Throwable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.result = new CompletableFuture.AltResult(var1);
         this.postComplete();
      }
   }

   public int getNumberOfDependents() {
      int var1 = 0;

      for(CompletableFuture.Completion var2 = this.stack; var2 != null; var2 = var2.next) {
         ++var1;
      }

      return var1;
   }

   public String toString() {
      Object var1 = this.result;
      int var2;
      return super.toString() + (var1 == null ? ((var2 = this.getNumberOfDependents()) == 0 ? "[Not completed]" : "[Not completed, " + var2 + " dependents]") : (var1 instanceof CompletableFuture.AltResult && ((CompletableFuture.AltResult)var1).ex != null ? "[Completed exceptionally]" : "[Completed normally]"));
   }

   static {
      asyncPool = (Executor)(useCommonPool ? ForkJoinPool.commonPool() : new CompletableFuture.ThreadPerTaskExecutor());

      try {
         Unsafe var0;
         UNSAFE = var0 = Unsafe.getUnsafe();
         Class var1 = CompletableFuture.class;
         RESULT = var0.objectFieldOffset(var1.getDeclaredField("result"));
         STACK = var0.objectFieldOffset(var1.getDeclaredField("stack"));
         NEXT = var0.objectFieldOffset(CompletableFuture.Completion.class.getDeclaredField("next"));
      } catch (Exception var2) {
         throw new Error(var2);
      }
   }

   static final class Signaller extends CompletableFuture.Completion implements ForkJoinPool.ManagedBlocker {
      long nanos;
      final long deadline;
      volatile int interruptControl;
      volatile Thread thread = Thread.currentThread();

      Signaller(boolean var1, long var2, long var4) {
         this.interruptControl = var1 ? 1 : 0;
         this.nanos = var2;
         this.deadline = var4;
      }

      final CompletableFuture<?> tryFire(int var1) {
         Thread var2;
         if ((var2 = this.thread) != null) {
            this.thread = null;
            LockSupport.unpark(var2);
         }

         return null;
      }

      public boolean isReleasable() {
         if (this.thread == null) {
            return true;
         } else {
            if (Thread.interrupted()) {
               int var1 = this.interruptControl;
               this.interruptControl = -1;
               if (var1 > 0) {
                  return true;
               }
            }

            if (this.deadline == 0L || this.nanos > 0L && (this.nanos = this.deadline - System.nanoTime()) > 0L) {
               return false;
            } else {
               this.thread = null;
               return true;
            }
         }
      }

      public boolean block() {
         if (this.isReleasable()) {
            return true;
         } else {
            if (this.deadline == 0L) {
               LockSupport.park(this);
            } else if (this.nanos > 0L) {
               LockSupport.parkNanos(this, this.nanos);
            }

            return this.isReleasable();
         }
      }

      final boolean isLive() {
         return this.thread != null;
      }
   }

   static final class AsyncRun extends ForkJoinTask<Void> implements Runnable, CompletableFuture.AsynchronousCompletionTask {
      CompletableFuture<Void> dep;
      Runnable fn;

      AsyncRun(CompletableFuture<Void> var1, Runnable var2) {
         this.dep = var1;
         this.fn = var2;
      }

      public final Void getRawResult() {
         return null;
      }

      public final void setRawResult(Void var1) {
      }

      public final boolean exec() {
         this.run();
         return true;
      }

      public void run() {
         CompletableFuture var1;
         Runnable var2;
         if ((var1 = this.dep) != null && (var2 = this.fn) != null) {
            this.dep = null;
            this.fn = null;
            if (var1.result == null) {
               try {
                  var2.run();
                  var1.completeNull();
               } catch (Throwable var4) {
                  var1.completeThrowable(var4);
               }
            }

            var1.postComplete();
         }

      }
   }

   static final class AsyncSupply<T> extends ForkJoinTask<Void> implements Runnable, CompletableFuture.AsynchronousCompletionTask {
      CompletableFuture<T> dep;
      Supplier<T> fn;

      AsyncSupply(CompletableFuture<T> var1, Supplier<T> var2) {
         this.dep = var1;
         this.fn = var2;
      }

      public final Void getRawResult() {
         return null;
      }

      public final void setRawResult(Void var1) {
      }

      public final boolean exec() {
         this.run();
         return true;
      }

      public void run() {
         CompletableFuture var1;
         Supplier var2;
         if ((var1 = this.dep) != null && (var2 = this.fn) != null) {
            this.dep = null;
            this.fn = null;
            if (var1.result == null) {
               try {
                  var1.completeValue(var2.get());
               } catch (Throwable var4) {
                  var1.completeThrowable(var4);
               }
            }

            var1.postComplete();
         }

      }
   }

   static final class OrRelay<T, U> extends CompletableFuture.BiCompletion<T, U, Object> {
      OrRelay(CompletableFuture<Object> var1, CompletableFuture<T> var2, CompletableFuture<U> var3) {
         super((Executor)null, var1, var2, var3);
      }

      final CompletableFuture<Object> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.orRelay(var3 = this.src, var4 = this.snd)) {
            this.src = null;
            this.snd = null;
            this.dep = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class OrRun<T, U> extends CompletableFuture.BiCompletion<T, U, Void> {
      Runnable fn;

      OrRun(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, Runnable var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.orRun(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class OrAccept<T, U extends T> extends CompletableFuture.BiCompletion<T, U, Void> {
      Consumer<? super T> fn;

      OrAccept(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, Consumer<? super T> var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.orAccept(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class OrApply<T, U extends T, V> extends CompletableFuture.BiCompletion<T, U, V> {
      Function<? super T, ? extends V> fn;

      OrApply(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, Function<? super T, ? extends V> var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<V> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.orApply(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class BiRelay<T, U> extends CompletableFuture.BiCompletion<T, U, Void> {
      BiRelay(CompletableFuture<Void> var1, CompletableFuture<T> var2, CompletableFuture<U> var3) {
         super((Executor)null, var1, var2, var3);
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.biRelay(var3 = this.src, var4 = this.snd)) {
            this.src = null;
            this.snd = null;
            this.dep = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class BiRun<T, U> extends CompletableFuture.BiCompletion<T, U, Void> {
      Runnable fn;

      BiRun(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, Runnable var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.biRun(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class BiAccept<T, U> extends CompletableFuture.BiCompletion<T, U, Void> {
      BiConsumer<? super T, ? super U> fn;

      BiAccept(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, BiConsumer<? super T, ? super U> var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.biAccept(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class BiApply<T, U, V> extends CompletableFuture.BiCompletion<T, U, V> {
      BiFunction<? super T, ? super U, ? extends V> fn;

      BiApply(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, CompletableFuture<U> var4, BiFunction<? super T, ? super U, ? extends V> var5) {
         super(var1, var2, var3, var4);
         this.fn = var5;
      }

      final CompletableFuture<V> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         CompletableFuture var4;
         if ((var2 = this.dep) != null && var2.biApply(var3 = this.src, var4 = this.snd, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.snd = null;
            this.fn = null;
            return var2.postFire(var3, var4, var1);
         } else {
            return null;
         }
      }
   }

   static final class CoCompletion extends CompletableFuture.Completion {
      CompletableFuture.BiCompletion<?, ?, ?> base;

      CoCompletion(CompletableFuture.BiCompletion<?, ?, ?> var1) {
         this.base = var1;
      }

      final CompletableFuture<?> tryFire(int var1) {
         CompletableFuture.BiCompletion var2;
         CompletableFuture var3;
         if ((var2 = this.base) != null && (var3 = var2.tryFire(var1)) != null) {
            this.base = null;
            return var3;
         } else {
            return null;
         }
      }

      final boolean isLive() {
         CompletableFuture.BiCompletion var1;
         return (var1 = this.base) != null && var1.dep != null;
      }
   }

   abstract static class BiCompletion<T, U, V> extends CompletableFuture.UniCompletion<T, V> {
      CompletableFuture<U> snd;

      BiCompletion(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, CompletableFuture<U> var4) {
         super(var1, var2, var3);
         this.snd = var4;
      }
   }

   static final class UniCompose<T, V> extends CompletableFuture.UniCompletion<T, V> {
      Function<? super T, ? extends CompletionStage<V>> fn;

      UniCompose(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, Function<? super T, ? extends CompletionStage<V>> var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<V> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniCompose(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniRelay<T> extends CompletableFuture.UniCompletion<T, T> {
      UniRelay(CompletableFuture<T> var1, CompletableFuture<T> var2) {
         super((Executor)null, var1, var2);
      }

      final CompletableFuture<T> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniRelay(var3 = this.src)) {
            this.src = null;
            this.dep = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniExceptionally<T> extends CompletableFuture.UniCompletion<T, T> {
      Function<? super Throwable, ? extends T> fn;

      UniExceptionally(CompletableFuture<T> var1, CompletableFuture<T> var2, Function<? super Throwable, ? extends T> var3) {
         super((Executor)null, var1, var2);
         this.fn = var3;
      }

      final CompletableFuture<T> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniExceptionally(var3 = this.src, this.fn, this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniHandle<T, V> extends CompletableFuture.UniCompletion<T, V> {
      BiFunction<? super T, Throwable, ? extends V> fn;

      UniHandle(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, BiFunction<? super T, Throwable, ? extends V> var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<V> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniHandle(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniWhenComplete<T> extends CompletableFuture.UniCompletion<T, T> {
      BiConsumer<? super T, ? super Throwable> fn;

      UniWhenComplete(Executor var1, CompletableFuture<T> var2, CompletableFuture<T> var3, BiConsumer<? super T, ? super Throwable> var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<T> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniWhenComplete(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniRun<T> extends CompletableFuture.UniCompletion<T, Void> {
      Runnable fn;

      UniRun(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, Runnable var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniRun(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniAccept<T> extends CompletableFuture.UniCompletion<T, Void> {
      Consumer<? super T> fn;

      UniAccept(Executor var1, CompletableFuture<Void> var2, CompletableFuture<T> var3, Consumer<? super T> var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<Void> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniAccept(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   static final class UniApply<T, V> extends CompletableFuture.UniCompletion<T, V> {
      Function<? super T, ? extends V> fn;

      UniApply(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3, Function<? super T, ? extends V> var4) {
         super(var1, var2, var3);
         this.fn = var4;
      }

      final CompletableFuture<V> tryFire(int var1) {
         CompletableFuture var2;
         CompletableFuture var3;
         if ((var2 = this.dep) != null && var2.uniApply(var3 = this.src, this.fn, var1 > 0 ? null : this)) {
            this.dep = null;
            this.src = null;
            this.fn = null;
            return var2.postFire(var3, var1);
         } else {
            return null;
         }
      }
   }

   abstract static class UniCompletion<T, V> extends CompletableFuture.Completion {
      Executor executor;
      CompletableFuture<V> dep;
      CompletableFuture<T> src;

      UniCompletion(Executor var1, CompletableFuture<V> var2, CompletableFuture<T> var3) {
         this.executor = var1;
         this.dep = var2;
         this.src = var3;
      }

      final boolean claim() {
         Executor var1 = this.executor;
         if (this.compareAndSetForkJoinTaskTag((short)0, (short)1)) {
            if (var1 == null) {
               return true;
            }

            this.executor = null;
            var1.execute(this);
         }

         return false;
      }

      final boolean isLive() {
         return this.dep != null;
      }
   }

   abstract static class Completion extends ForkJoinTask<Void> implements Runnable, CompletableFuture.AsynchronousCompletionTask {
      volatile CompletableFuture.Completion next;

      abstract CompletableFuture<?> tryFire(int var1);

      abstract boolean isLive();

      public final void run() {
         this.tryFire(1);
      }

      public final boolean exec() {
         this.tryFire(1);
         return true;
      }

      public final Void getRawResult() {
         return null;
      }

      public final void setRawResult(Void var1) {
      }
   }

   static final class ThreadPerTaskExecutor implements Executor {
      public void execute(Runnable var1) {
         (new Thread(var1)).start();
      }
   }

   public interface AsynchronousCompletionTask {
   }

   static final class AltResult {
      final Throwable ex;

      AltResult(Throwable var1) {
         this.ex = var1;
      }
   }
}
