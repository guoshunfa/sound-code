package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

public interface BlockingQueue<E> extends Queue<E> {
   boolean add(E var1);

   boolean offer(E var1);

   void put(E var1) throws InterruptedException;

   boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException;

   E take() throws InterruptedException;

   E poll(long var1, TimeUnit var3) throws InterruptedException;

   int remainingCapacity();

   boolean remove(Object var1);

   boolean contains(Object var1);

   int drainTo(Collection<? super E> var1);

   int drainTo(Collection<? super E> var1, int var2);
}
