package java.util;

public interface NavigableSet<E> extends SortedSet<E> {
   E lower(E var1);

   E floor(E var1);

   E ceiling(E var1);

   E higher(E var1);

   E pollFirst();

   E pollLast();

   Iterator<E> iterator();

   NavigableSet<E> descendingSet();

   Iterator<E> descendingIterator();

   NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4);

   NavigableSet<E> headSet(E var1, boolean var2);

   NavigableSet<E> tailSet(E var1, boolean var2);

   SortedSet<E> subSet(E var1, E var2);

   SortedSet<E> headSet(E var1);

   SortedSet<E> tailSet(E var1);
}
