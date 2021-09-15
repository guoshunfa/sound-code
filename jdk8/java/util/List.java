package java.util;

import java.util.function.UnaryOperator;

public interface List<E> extends Collection<E> {
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

   boolean addAll(int var1, Collection<? extends E> var2);

   boolean removeAll(Collection<?> var1);

   boolean retainAll(Collection<?> var1);

   default void replaceAll(UnaryOperator<E> var1) {
      Objects.requireNonNull(var1);
      ListIterator var2 = this.listIterator();

      while(var2.hasNext()) {
         var2.set(var1.apply(var2.next()));
      }

   }

   default void sort(Comparator<? super E> var1) {
      Object[] var2 = this.toArray();
      Arrays.sort(var2, var1);
      ListIterator var3 = this.listIterator();
      Object[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         var3.next();
         var3.set(var7);
      }

   }

   void clear();

   boolean equals(Object var1);

   int hashCode();

   E get(int var1);

   E set(int var1, E var2);

   void add(int var1, E var2);

   E remove(int var1);

   int indexOf(Object var1);

   int lastIndexOf(Object var1);

   ListIterator<E> listIterator();

   ListIterator<E> listIterator(int var1);

   List<E> subList(int var1, int var2);

   default Spliterator<E> spliterator() {
      return Spliterators.spliterator((Collection)this, 16);
   }
}
