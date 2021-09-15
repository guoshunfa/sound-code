package java.util;

public interface SortedSet<E> extends Set<E> {
   Comparator<? super E> comparator();

   SortedSet<E> subSet(E var1, E var2);

   SortedSet<E> headSet(E var1);

   SortedSet<E> tailSet(E var1);

   E first();

   E last();

   default Spliterator<E> spliterator() {
      return new Spliterators.IteratorSpliterator<E>(this, 21) {
         public Comparator<? super E> getComparator() {
            return SortedSet.this.comparator();
         }
      };
   }
}
