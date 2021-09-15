package java.util.concurrent;

import java.util.Collection;
import java.util.List;

public interface ExecutorService extends Executor {
   void shutdown();

   List<Runnable> shutdownNow();

   boolean isShutdown();

   boolean isTerminated();

   boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException;

   <T> Future<T> submit(Callable<T> var1);

   <T> Future<T> submit(Runnable var1, T var2);

   Future<?> submit(Runnable var1);

   <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException;

   <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException;

   <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException;

   <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException;
}
