package java.util;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Collection<E> extends Iterable<E> {
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

   boolean removeAll(Collection<?> var1);

   default boolean removeIf(Predicate<? super E> var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(Collection<?> var1);

   void clear();

   boolean equals(Object var1);

   int hashCode();

   default Spliterator<E> spliterator() {
      return Spliterators.spliterator((Collection)this, 0);
   }

   default Stream<E> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   default Stream<E> parallelStream() {
      return StreamSupport.stream(this.spliterator(), true);
   }
}
