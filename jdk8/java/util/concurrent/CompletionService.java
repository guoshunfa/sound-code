package java.util.concurrent;

public interface CompletionService<V> {
   Future<V> submit(Callable<V> var1);

   Future<V> submit(Runnable var1, V var2);

   Future<V> take() throws InterruptedException;

   Future<V> poll();

   Future<V> poll(long var1, TimeUnit var3) throws InterruptedException;
}
