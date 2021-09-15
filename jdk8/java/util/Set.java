package java.util;

public interface Set<E> extends Collection<E> {
   int size();

   boolean isEmpty();

   boolean contains(Object var1);

   Iterator<E> iterator();

   Object[] toArray();

   <T> T[] toArray(T[] var1);

   boolean add(E var1);

   boolean remove(Object var1);

   boolean containsAll(Collection<?> var1);

   boolean addAll(Collection<? extends E> var1);

   boolean retainAll(Collection<?> var1);

   boolean removeAll(Collection<?> var1);

   void clear();

   boolean equals(Object var1);

   int hashCode();

   default Spliterator<E> spliterator() {
      return Spliterators.spliterator((Collection)this, 1);
   }
}
