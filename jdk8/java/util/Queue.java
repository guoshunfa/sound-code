package java.util;

public interface Queue<E> extends Collection<E> {
   boolean add(E var1);

   boolean offer(E var1);

   E remove();

   E poll();

   E element();

   E peek();
}
