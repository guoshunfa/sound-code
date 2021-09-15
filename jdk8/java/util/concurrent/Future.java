package java.util.concurrent;

public interface Future<V> {
   boolean cancel(boolean var1);

   boolean isCancelled();

   boolean isDone();

   V get() throws InterruptedException, ExecutionException;

   V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException;
}
