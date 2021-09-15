package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Collections {
   private static final int BINARYSEARCH_THRESHOLD = 5000;
   private static final int REVERSE_THRESHOLD = 18;
   private static final int SHUFFLE_THRESHOLD = 5;
   private static final int FILL_THRESHOLD = 25;
   private static final int ROTATE_THRESHOLD = 100;
   private static final int COPY_THRESHOLD = 10;
   private static final int REPLACEALL_THRESHOLD = 11;
   private static final int INDEXOFSUBLIST_THRESHOLD = 35;
   private static Random r;
   public static final Set EMPTY_SET = new Collections.EmptySet();
   public static final List EMPTY_LIST = new Collections.EmptyList();
   public static final Map EMPTY_MAP = new Collections.EmptyMap();

   private Collections() {
   }

   public static <T extends Comparable<? super T>> void sort(List<T> var0) {
      var0.sort((Comparator)null);
   }

   public static <T> void sort(List<T> var0, Comparator<? super T> var1) {
      var0.sort(var1);
   }

   public static <T> int binarySearch(List<? extends Comparable<? super T>> var0, T var1) {
      return !(var0 instanceof RandomAccess) && var0.size() >= 5000 ? iteratorBinarySearch(var0, var1) : indexedBinarySearch(var0, var1);
   }

   private static <T> int indexedBinarySearch(List<? extends Comparable<? super T>> var0, T var1) {
      int var2 = 0;
      int var3 = var0.size() - 1;

      while(var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         Comparable var5 = (Comparable)var0.get(var4);
         int var6 = var5.compareTo(var1);
         if (var6 < 0) {
            var2 = var4 + 1;
         } else {
            if (var6 <= 0) {
               return var4;
            }

            var3 = var4 - 1;
         }
      }

      return -(var2 + 1);
   }

   private static <T> int iteratorBinarySearch(List<? extends Comparable<? super T>> var0, T var1) {
      int var2 = 0;
      int var3 = var0.size() - 1;
      ListIterator var4 = var0.listIterator();

      while(var2 <= var3) {
         int var5 = var2 + var3 >>> 1;
         Comparable var6 = (Comparable)get(var4, var5);
         int var7 = var6.compareTo(var1);
         if (var7 < 0) {
            var2 = var5 + 1;
         } else {
            if (var7 <= 0) {
               return var5;
            }

            var3 = var5 - 1;
         }
      }

      return -(var2 + 1);
   }

   private static <T> T get(ListIterator<? extends T> var0, int var1) {
      Object var2 = null;
      int var3 = var0.nextIndex();
      if (var3 <= var1) {
         do {
            var2 = var0.next();
         } while(var3++ < var1);
      } else {
         do {
            var2 = var0.previous();
            --var3;
         } while(var3 > var1);
      }

      return var2;
   }

   public static <T> int binarySearch(List<? extends T> var0, T var1, Comparator<? super T> var2) {
      if (var2 == null) {
         return binarySearch(var0, var1);
      } else {
         return !(var0 instanceof RandomAccess) && var0.size() >= 5000 ? iteratorBinarySearch(var0, var1, var2) : indexedBinarySearch(var0, var1, var2);
      }
   }

   private static <T> int indexedBinarySearch(List<? extends T> var0, T var1, Comparator<? super T> var2) {
      int var3 = 0;
      int var4 = var0.size() - 1;

      while(var3 <= var4) {
         int var5 = var3 + var4 >>> 1;
         Object var6 = var0.get(var5);
         int var7 = var2.compare(var6, var1);
         if (var7 < 0) {
            var3 = var5 + 1;
         } else {
            if (var7 <= 0) {
               return var5;
            }

            var4 = var5 - 1;
         }
      }

      return -(var3 + 1);
   }

   private static <T> int iteratorBinarySearch(List<? extends T> var0, T var1, Comparator<? super T> var2) {
      int var3 = 0;
      int var4 = var0.size() - 1;
      ListIterator var5 = var0.listIterator();

      while(var3 <= var4) {
         int var6 = var3 + var4 >>> 1;
         Object var7 = get(var5, var6);
         int var8 = var2.compare(var7, var1);
         if (var8 < 0) {
            var3 = var6 + 1;
         } else {
            if (var8 <= 0) {
               return var6;
            }

            var4 = var6 - 1;
         }
      }

      return -(var3 + 1);
   }

   public static void reverse(List<?> var0) {
      int var1 = var0.size();
      int var4;
      if (var1 >= 18 && !(var0 instanceof RandomAccess)) {
         ListIterator var7 = var0.listIterator();
         ListIterator var8 = var0.listIterator(var1);
         var4 = 0;

         for(int var5 = var0.size() >> 1; var4 < var5; ++var4) {
            Object var6 = var7.next();
            var7.set(var8.previous());
            var8.set(var6);
         }
      } else {
         int var2 = 0;
         int var3 = var1 >> 1;

         for(var4 = var1 - 1; var2 < var3; --var4) {
            swap(var0, var2, var4);
            ++var2;
         }
      }

   }

   public static void shuffle(List<?> var0) {
      Random var1 = r;
      if (var1 == null) {
         r = var1 = new Random();
      }

      shuffle(var0, var1);
   }

   public static void shuffle(List<?> var0, Random var1) {
      int var2 = var0.size();
      if (var2 >= 5 && !(var0 instanceof RandomAccess)) {
         Object[] var6 = var0.toArray();

         for(int var4 = var2; var4 > 1; --var4) {
            swap(var6, var4 - 1, var1.nextInt(var4));
         }

         ListIterator var7 = var0.listIterator();

         for(int var5 = 0; var5 < var6.length; ++var5) {
            var7.next();
            var7.set(var6[var5]);
         }
      } else {
         for(int var3 = var2; var3 > 1; --var3) {
            swap(var0, var3 - 1, var1.nextInt(var3));
         }
      }

   }

   public static void swap(List<?> var0, int var1, int var2) {
      var0.set(var1, var0.set(var2, var0.get(var1)));
   }

   private static void swap(Object[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static <T> void fill(List<? super T> var0, T var1) {
      int var2 = var0.size();
      if (var2 >= 25 && !(var0 instanceof RandomAccess)) {
         ListIterator var5 = var0.listIterator();

         for(int var4 = 0; var4 < var2; ++var4) {
            var5.next();
            var5.set(var1);
         }
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            var0.set(var3, var1);
         }
      }

   }

   public static <T> void copy(List<? super T> var0, List<? extends T> var1) {
      int var2 = var1.size();
      if (var2 > var0.size()) {
         throw new IndexOutOfBoundsException("Source does not fit in dest");
      } else {
         if (var2 < 10 || var1 instanceof RandomAccess && var0 instanceof RandomAccess) {
            for(int var6 = 0; var6 < var2; ++var6) {
               var0.set(var6, var1.get(var6));
            }
         } else {
            ListIterator var3 = var0.listIterator();
            ListIterator var4 = var1.listIterator();

            for(int var5 = 0; var5 < var2; ++var5) {
               var3.next();
               var3.set(var4.next());
            }
         }

      }
   }

   public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> var0) {
      Iterator var1 = var0.iterator();
      Object var2 = var1.next();

      while(var1.hasNext()) {
         Object var3 = var1.next();
         if (((Comparable)var3).compareTo(var2) < 0) {
            var2 = var3;
         }
      }

      return var2;
   }

   public static <T> T min(Collection<? extends T> var0, Comparator<? super T> var1) {
      if (var1 == null) {
         return min(var0);
      } else {
         Iterator var2 = var0.iterator();
         Object var3 = var2.next();

         while(var2.hasNext()) {
            Object var4 = var2.next();
            if (var1.compare(var4, var3) < 0) {
               var3 = var4;
            }
         }

         return var3;
      }
   }

   public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> var0) {
      Iterator var1 = var0.iterator();
      Object var2 = var1.next();

      while(var1.hasNext()) {
         Object var3 = var1.next();
         if (((Comparable)var3).compareTo(var2) > 0) {
            var2 = var3;
         }
      }

      return var2;
   }

   public static <T> T max(Collection<? extends T> var0, Comparator<? super T> var1) {
      if (var1 == null) {
         return max(var0);
      } else {
         Iterator var2 = var0.iterator();
         Object var3 = var2.next();

         while(var2.hasNext()) {
            Object var4 = var2.next();
            if (var1.compare(var4, var3) > 0) {
               var3 = var4;
            }
         }

         return var3;
      }
   }

   public static void rotate(List<?> var0, int var1) {
      if (!(var0 instanceof RandomAccess) && var0.size() >= 100) {
         rotate2(var0, var1);
      } else {
         rotate1(var0, var1);
      }

   }

   private static <T> void rotate1(List<T> var0, int var1) {
      int var2 = var0.size();
      if (var2 != 0) {
         var1 %= var2;
         if (var1 < 0) {
            var1 += var2;
         }

         if (var1 != 0) {
            int var3 = 0;

            for(int var4 = 0; var4 != var2; ++var3) {
               Object var5 = var0.get(var3);
               int var6 = var3;

               do {
                  var6 += var1;
                  if (var6 >= var2) {
                     var6 -= var2;
                  }

                  var5 = var0.set(var6, var5);
                  ++var4;
               } while(var6 != var3);
            }

         }
      }
   }

   private static void rotate2(List<?> var0, int var1) {
      int var2 = var0.size();
      if (var2 != 0) {
         int var3 = -var1 % var2;
         if (var3 < 0) {
            var3 += var2;
         }

         if (var3 != 0) {
            reverse(var0.subList(0, var3));
            reverse(var0.subList(var3, var2));
            reverse(var0);
         }
      }
   }

   public static <T> boolean replaceAll(List<T> var0, T var1, T var2) {
      boolean var3 = false;
      int var4 = var0.size();
      if (var4 >= 11 && !(var0 instanceof RandomAccess)) {
         ListIterator var7 = var0.listIterator();
         int var6;
         if (var1 == null) {
            for(var6 = 0; var6 < var4; ++var6) {
               if (var7.next() == null) {
                  var7.set(var2);
                  var3 = true;
               }
            }
         } else {
            for(var6 = 0; var6 < var4; ++var6) {
               if (var1.equals(var7.next())) {
                  var7.set(var2);
                  var3 = true;
               }
            }
         }
      } else {
         int var5;
         if (var1 == null) {
            for(var5 = 0; var5 < var4; ++var5) {
               if (var0.get(var5) == null) {
                  var0.set(var5, var2);
                  var3 = true;
               }
            }
         } else {
            for(var5 = 0; var5 < var4; ++var5) {
               if (var1.equals(var0.get(var5))) {
                  var0.set(var5, var2);
                  var3 = true;
               }
            }
         }
      }

      return var3;
   }

   public static int indexOfSubList(List<?> var0, List<?> var1) {
      int var2 = var0.size();
      int var3 = var1.size();
      int var4 = var2 - var3;
      int var6;
      if (var2 < 35 || var0 instanceof RandomAccess && var1 instanceof RandomAccess) {
         label60:
         for(int var10 = 0; var10 <= var4; ++var10) {
            var6 = 0;

            for(int var11 = var10; var6 < var3; ++var11) {
               if (!eq(var1.get(var6), var0.get(var11))) {
                  continue label60;
               }

               ++var6;
            }

            return var10;
         }
      } else {
         ListIterator var5 = var0.listIterator();

         label48:
         for(var6 = 0; var6 <= var4; ++var6) {
            ListIterator var7 = var1.listIterator();

            for(int var8 = 0; var8 < var3; ++var8) {
               if (!eq(var7.next(), var5.next())) {
                  for(int var9 = 0; var9 < var8; ++var9) {
                     var5.previous();
                  }
                  continue label48;
               }
            }

            return var6;
         }
      }

      return -1;
   }

   public static int lastIndexOfSubList(List<?> var0, List<?> var1) {
      int var2 = var0.size();
      int var3 = var1.size();
      int var4 = var2 - var3;
      int var6;
      if (var2 >= 35 && !(var0 instanceof RandomAccess)) {
         if (var4 < 0) {
            return -1;
         }

         ListIterator var10 = var0.listIterator(var4);

         label54:
         for(var6 = var4; var6 >= 0; --var6) {
            ListIterator var11 = var1.listIterator();

            for(int var8 = 0; var8 < var3; ++var8) {
               if (!eq(var11.next(), var10.next())) {
                  if (var6 != 0) {
                     for(int var9 = 0; var9 <= var8 + 1; ++var9) {
                        var10.previous();
                     }
                  }
                  continue label54;
               }
            }

            return var6;
         }
      } else {
         label66:
         for(int var5 = var4; var5 >= 0; --var5) {
            var6 = 0;

            for(int var7 = var5; var6 < var3; ++var7) {
               if (!eq(var1.get(var6), var0.get(var7))) {
                  continue label66;
               }

               ++var6;
            }

            return var5;
         }
      }

      return -1;
   }

   public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> var0) {
      return new Collections.UnmodifiableCollection(var0);
   }

   public static <T> Set<T> unmodifiableSet(Set<? extends T> var0) {
      return new Collections.UnmodifiableSet(var0);
   }

   public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> var0) {
      return new Collections.UnmodifiableSortedSet(var0);
   }

   public static <T> NavigableSet<T> unmodifiableNavigableSet(NavigableSet<T> var0) {
      return new Collections.UnmodifiableNavigableSet(var0);
   }

   public static <T> List<T> unmodifiableList(List<? extends T> var0) {
      return (List)(var0 instanceof RandomAccess ? new Collections.UnmodifiableRandomAccessList(var0) : new Collections.UnmodifiableList(var0));
   }

   public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> var0) {
      return new Collections.UnmodifiableMap(var0);
   }

   public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> var0) {
      return new Collections.UnmodifiableSortedMap(var0);
   }

   public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> var0) {
      return new Collections.UnmodifiableNavigableMap(var0);
   }

   public static <T> Collection<T> synchronizedCollection(Collection<T> var0) {
      return new Collections.SynchronizedCollection(var0);
   }

   static <T> Collection<T> synchronizedCollection(Collection<T> var0, Object var1) {
      return new Collections.SynchronizedCollection(var0, var1);
   }

   public static <T> Set<T> synchronizedSet(Set<T> var0) {
      return new Collections.SynchronizedSet(var0);
   }

   static <T> Set<T> synchronizedSet(Set<T> var0, Object var1) {
      return new Collections.SynchronizedSet(var0, var1);
   }

   public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> var0) {
      return new Collections.SynchronizedSortedSet(var0);
   }

   public static <T> NavigableSet<T> synchronizedNavigableSet(NavigableSet<T> var0) {
      return new Collections.SynchronizedNavigableSet(var0);
   }

   public static <T> List<T> synchronizedList(List<T> var0) {
      return (List)(var0 instanceof RandomAccess ? new Collections.SynchronizedRandomAccessList(var0) : new Collections.SynchronizedList(var0));
   }

   static <T> List<T> synchronizedList(List<T> var0, Object var1) {
      return (List)(var0 instanceof RandomAccess ? new Collections.SynchronizedRandomAccessList(var0, var1) : new Collections.SynchronizedList(var0, var1));
   }

   public static <K, V> Map<K, V> synchronizedMap(Map<K, V> var0) {
      return new Collections.SynchronizedMap(var0);
   }

   public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> var0) {
      return new Collections.SynchronizedSortedMap(var0);
   }

   public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> var0) {
      return new Collections.SynchronizedNavigableMap(var0);
   }

   public static <E> Collection<E> checkedCollection(Collection<E> var0, Class<E> var1) {
      return new Collections.CheckedCollection(var0, var1);
   }

   static <T> T[] zeroLengthArray(Class<T> var0) {
      return (Object[])((Object[])Array.newInstance(var0, 0));
   }

   public static <E> Queue<E> checkedQueue(Queue<E> var0, Class<E> var1) {
      return new Collections.CheckedQueue(var0, var1);
   }

   public static <E> Set<E> checkedSet(Set<E> var0, Class<E> var1) {
      return new Collections.CheckedSet(var0, var1);
   }

   public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> var0, Class<E> var1) {
      return new Collections.CheckedSortedSet(var0, var1);
   }

   public static <E> NavigableSet<E> checkedNavigableSet(NavigableSet<E> var0, Class<E> var1) {
      return new Collections.CheckedNavigableSet(var0, var1);
   }

   public static <E> List<E> checkedList(List<E> var0, Class<E> var1) {
      return (List)(var0 instanceof RandomAccess ? new Collections.CheckedRandomAccessList(var0, var1) : new Collections.CheckedList(var0, var1));
   }

   public static <K, V> Map<K, V> checkedMap(Map<K, V> var0, Class<K> var1, Class<V> var2) {
      return new Collections.CheckedMap(var0, var1, var2);
   }

   public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> var0, Class<K> var1, Class<V> var2) {
      return new Collections.CheckedSortedMap(var0, var1, var2);
   }

   public static <K, V> NavigableMap<K, V> checkedNavigableMap(NavigableMap<K, V> var0, Class<K> var1, Class<V> var2) {
      return new Collections.CheckedNavigableMap(var0, var1, var2);
   }

   public static <T> Iterator<T> emptyIterator() {
      return Collections.EmptyIterator.EMPTY_ITERATOR;
   }

   public static <T> ListIterator<T> emptyListIterator() {
      return Collections.EmptyListIterator.EMPTY_ITERATOR;
   }

   public static <T> Enumeration<T> emptyEnumeration() {
      return Collections.EmptyEnumeration.EMPTY_ENUMERATION;
   }

   public static final <T> Set<T> emptySet() {
      return EMPTY_SET;
   }

   public static <E> SortedSet<E> emptySortedSet() {
      return Collections.UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
   }

   public static <E> NavigableSet<E> emptyNavigableSet() {
      return Collections.UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
   }

   public static final <T> List<T> emptyList() {
      return EMPTY_LIST;
   }

   public static final <K, V> Map<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static final <K, V> SortedMap<K, V> emptySortedMap() {
      return Collections.UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
   }

   public static final <K, V> NavigableMap<K, V> emptyNavigableMap() {
      return Collections.UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
   }

   public static <T> Set<T> singleton(T var0) {
      return new Collections.SingletonSet(var0);
   }

   static <E> Iterator<E> singletonIterator(final E var0) {
      return new Iterator<E>() {
         private boolean hasNext = true;

         public boolean hasNext() {
            return this.hasNext;
         }

         public E next() {
            if (this.hasNext) {
               this.hasNext = false;
               return var0;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }

         public void forEachRemaining(Consumer<? super E> var1) {
            Objects.requireNonNull(var1);
            if (this.hasNext) {
               var1.accept(var0);
               this.hasNext = false;
            }

         }
      };
   }

   static <T> Spliterator<T> singletonSpliterator(final T var0) {
      return new Spliterator<T>() {
         long est = 1L;

         public Spliterator<T> trySplit() {
            return null;
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);
            if (this.est > 0L) {
               --this.est;
               var1.accept(var0);
               return true;
            } else {
               return false;
            }
         }

         public void forEachRemaining(Consumer<? super T> var1) {
            this.tryAdvance(var1);
         }

         public long estimateSize() {
            return this.est;
         }

         public int characteristics() {
            int var1 = var0 != null ? 256 : 0;
            return var1 | 64 | 16384 | 1024 | 1 | 16;
         }
      };
   }

   public static <T> List<T> singletonList(T var0) {
      return new Collections.SingletonList(var0);
   }

   public static <K, V> Map<K, V> singletonMap(K var0, V var1) {
      return new Collections.SingletonMap(var0, var1);
   }

   public static <T> List<T> nCopies(int var0, T var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException("List length = " + var0);
      } else {
         return new Collections.CopiesList(var0, var1);
      }
   }

   public static <T> Comparator<T> reverseOrder() {
      return Collections.ReverseComparator.REVERSE_ORDER;
   }

   public static <T> Comparator<T> reverseOrder(Comparator<T> var0) {
      if (var0 == null) {
         return reverseOrder();
      } else {
         return (Comparator)(var0 instanceof Collections.ReverseComparator2 ? ((Collections.ReverseComparator2)var0).cmp : new Collections.ReverseComparator2(var0));
      }
   }

   public static <T> Enumeration<T> enumeration(final Collection<T> var0) {
      return new Enumeration<T>() {
         private final Iterator<T> i = var0.iterator();

         public boolean hasMoreElements() {
            return this.i.hasNext();
         }

         public T nextElement() {
            return this.i.next();
         }
      };
   }

   public static <T> ArrayList<T> list(Enumeration<T> var0) {
      ArrayList var1 = new ArrayList();

      while(var0.hasMoreElements()) {
         var1.add(var0.nextElement());
      }

      return var1;
   }

   static boolean eq(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   public static int frequency(Collection<?> var0, Object var1) {
      int var2 = 0;
      Iterator var3;
      Object var4;
      if (var1 == null) {
         var3 = var0.iterator();

         while(var3.hasNext()) {
            var4 = var3.next();
            if (var4 == null) {
               ++var2;
            }
         }
      } else {
         var3 = var0.iterator();

         while(var3.hasNext()) {
            var4 = var3.next();
            if (var1.equals(var4)) {
               ++var2;
            }
         }
      }

      return var2;
   }

   public static boolean disjoint(Collection<?> var0, Collection<?> var1) {
      Collection var2 = var1;
      Collection var3 = var0;
      if (var0 instanceof Set) {
         var3 = var1;
         var2 = var0;
      } else if (!(var1 instanceof Set)) {
         int var4 = var0.size();
         int var5 = var1.size();
         if (var4 == 0 || var5 == 0) {
            return true;
         }

         if (var4 > var5) {
            var3 = var1;
            var2 = var0;
         }
      }

      Iterator var6 = var3.iterator();

      Object var7;
      do {
         if (!var6.hasNext()) {
            return true;
         }

         var7 = var6.next();
      } while(!var2.contains(var7));

      return false;
   }

   @SafeVarargs
   public static <T> boolean addAll(Collection<? super T> var0, T... var1) {
      boolean var2 = false;
      Object[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         var2 |= var0.add(var6);
      }

      return var2;
   }

   public static <E> Set<E> newSetFromMap(Map<E, Boolean> var0) {
      return new Collections.SetFromMap(var0);
   }

   public static <T> Queue<T> asLifoQueue(Deque<T> var0) {
      return new Collections.AsLIFOQueue(var0);
   }

   static class AsLIFOQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
      private static final long serialVersionUID = 1802017725587941708L;
      private final Deque<E> q;

      AsLIFOQueue(Deque<E> var1) {
         this.q = var1;
      }

      public boolean add(E var1) {
         this.q.addFirst(var1);
         return true;
      }

      public boolean offer(E var1) {
         return this.q.offerFirst(var1);
      }

      public E poll() {
         return this.q.pollFirst();
      }

      public E remove() {
         return this.q.removeFirst();
      }

      public E peek() {
         return this.q.peekFirst();
      }

      public E element() {
         return this.q.getFirst();
      }

      public void clear() {
         this.q.clear();
      }

      public int size() {
         return this.q.size();
      }

      public boolean isEmpty() {
         return this.q.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.q.contains(var1);
      }

      public boolean remove(Object var1) {
         return this.q.remove(var1);
      }

      public Iterator<E> iterator() {
         return this.q.iterator();
      }

      public Object[] toArray() {
         return this.q.toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.q.toArray(var1);
      }

      public String toString() {
         return this.q.toString();
      }

      public boolean containsAll(Collection<?> var1) {
         return this.q.containsAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.q.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.q.retainAll(var1);
      }

      public void forEach(Consumer<? super E> var1) {
         this.q.forEach(var1);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         return this.q.removeIf(var1);
      }

      public Spliterator<E> spliterator() {
         return this.q.spliterator();
      }

      public Stream<E> stream() {
         return this.q.stream();
      }

      public Stream<E> parallelStream() {
         return this.q.parallelStream();
      }
   }

   private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable {
      private final Map<E, Boolean> m;
      private transient Set<E> s;
      private static final long serialVersionUID = 2454657854757543876L;

      SetFromMap(Map<E, Boolean> var1) {
         if (!var1.isEmpty()) {
            throw new IllegalArgumentException("Map is non-empty");
         } else {
            this.m = var1;
            this.s = var1.keySet();
         }
      }

      public void clear() {
         this.m.clear();
      }

      public int size() {
         return this.m.size();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.m.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return this.m.remove(var1) != null;
      }

      public boolean add(E var1) {
         return this.m.put(var1, Boolean.TRUE) == null;
      }

      public Iterator<E> iterator() {
         return this.s.iterator();
      }

      public Object[] toArray() {
         return this.s.toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.s.toArray(var1);
      }

      public String toString() {
         return this.s.toString();
      }

      public int hashCode() {
         return this.s.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this || this.s.equals(var1);
      }

      public boolean containsAll(Collection<?> var1) {
         return this.s.containsAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.s.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.s.retainAll(var1);
      }

      public void forEach(Consumer<? super E> var1) {
         this.s.forEach(var1);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         return this.s.removeIf(var1);
      }

      public Spliterator<E> spliterator() {
         return this.s.spliterator();
      }

      public Stream<E> stream() {
         return this.s.stream();
      }

      public Stream<E> parallelStream() {
         return this.s.parallelStream();
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.s = this.m.keySet();
      }
   }

   private static class ReverseComparator2<T> implements Comparator<T>, Serializable {
      private static final long serialVersionUID = 4374092139857L;
      final Comparator<T> cmp;

      ReverseComparator2(Comparator<T> var1) {
         assert var1 != null;

         this.cmp = var1;
      }

      public int compare(T var1, T var2) {
         return this.cmp.compare(var2, var1);
      }

      public boolean equals(Object var1) {
         return var1 == this || var1 instanceof Collections.ReverseComparator2 && this.cmp.equals(((Collections.ReverseComparator2)var1).cmp);
      }

      public int hashCode() {
         return this.cmp.hashCode() ^ Integer.MIN_VALUE;
      }

      public Comparator<T> reversed() {
         return this.cmp;
      }
   }

   private static class ReverseComparator implements Comparator<Comparable<Object>>, Serializable {
      private static final long serialVersionUID = 7207038068494060240L;
      static final Collections.ReverseComparator REVERSE_ORDER = new Collections.ReverseComparator();

      public int compare(Comparable<Object> var1, Comparable<Object> var2) {
         return var2.compareTo(var1);
      }

      private Object readResolve() {
         return Collections.reverseOrder();
      }

      public Comparator<Comparable<Object>> reversed() {
         return Comparator.naturalOrder();
      }
   }

   private static class CopiesList<E> extends AbstractList<E> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 2739099268398711800L;
      final int n;
      final E element;

      CopiesList(int var1, E var2) {
         assert var1 >= 0;

         this.n = var1;
         this.element = var2;
      }

      public int size() {
         return this.n;
      }

      public boolean contains(Object var1) {
         return this.n != 0 && Collections.eq(var1, this.element);
      }

      public int indexOf(Object var1) {
         return this.contains(var1) ? 0 : -1;
      }

      public int lastIndexOf(Object var1) {
         return this.contains(var1) ? this.n - 1 : -1;
      }

      public E get(int var1) {
         if (var1 >= 0 && var1 < this.n) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.n);
         }
      }

      public Object[] toArray() {
         Object[] var1 = new Object[this.n];
         if (this.element != null) {
            Arrays.fill(var1, 0, this.n, this.element);
         }

         return var1;
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = this.n;
         if (var1.length < var2) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var2));
            if (this.element != null) {
               Arrays.fill(var1, 0, var2, this.element);
            }
         } else {
            Arrays.fill(var1, 0, var2, this.element);
            if (var1.length > var2) {
               var1[var2] = null;
            }
         }

         return var1;
      }

      public List<E> subList(int var1, int var2) {
         if (var1 < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + var1);
         } else if (var2 > this.n) {
            throw new IndexOutOfBoundsException("toIndex = " + var2);
         } else if (var1 > var2) {
            throw new IllegalArgumentException("fromIndex(" + var1 + ") > toIndex(" + var2 + ")");
         } else {
            return new Collections.CopiesList(var2 - var1, this.element);
         }
      }

      public Stream<E> stream() {
         return IntStream.range(0, this.n).mapToObj((var1) -> {
            return this.element;
         });
      }

      public Stream<E> parallelStream() {
         return IntStream.range(0, this.n).parallel().mapToObj((var1) -> {
            return this.element;
         });
      }

      public Spliterator<E> spliterator() {
         return this.stream().spliterator();
      }
   }

   private static class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
      private static final long serialVersionUID = -6979724477215052911L;
      private final K k;
      private final V v;
      private transient Set<K> keySet;
      private transient Set<Map.Entry<K, V>> entrySet;
      private transient Collection<V> values;

      SingletonMap(K var1, V var2) {
         this.k = var1;
         this.v = var2;
      }

      public int size() {
         return 1;
      }

      public boolean isEmpty() {
         return false;
      }

      public boolean containsKey(Object var1) {
         return Collections.eq(var1, this.k);
      }

      public boolean containsValue(Object var1) {
         return Collections.eq(var1, this.v);
      }

      public V get(Object var1) {
         return Collections.eq(var1, this.k) ? this.v : null;
      }

      public Set<K> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.singleton(this.k);
         }

         return this.keySet;
      }

      public Set<Map.Entry<K, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = Collections.singleton(new AbstractMap.SimpleImmutableEntry(this.k, this.v));
         }

         return this.entrySet;
      }

      public Collection<V> values() {
         if (this.values == null) {
            this.values = Collections.singleton(this.v);
         }

         return this.values;
      }

      public V getOrDefault(Object var1, V var2) {
         return Collections.eq(var1, this.k) ? this.v : var2;
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         var1.accept(this.k, this.v);
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V replace(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   private static class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 3093736618740652951L;
      private final E element;

      SingletonList(E var1) {
         this.element = var1;
      }

      public Iterator<E> iterator() {
         return Collections.singletonIterator(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean contains(Object var1) {
         return Collections.eq(var1, this.element);
      }

      public E get(int var1) {
         if (var1 != 0) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: 1");
         } else {
            return this.element;
         }
      }

      public void forEach(Consumer<? super E> var1) {
         var1.accept(this.element);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         throw new UnsupportedOperationException();
      }

      public void replaceAll(UnaryOperator<E> var1) {
         throw new UnsupportedOperationException();
      }

      public void sort(Comparator<? super E> var1) {
      }

      public Spliterator<E> spliterator() {
         return Collections.singletonSpliterator(this.element);
      }
   }

   private static class SingletonSet<E> extends AbstractSet<E> implements Serializable {
      private static final long serialVersionUID = 3193687207550431679L;
      private final E element;

      SingletonSet(E var1) {
         this.element = var1;
      }

      public Iterator<E> iterator() {
         return Collections.singletonIterator(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean contains(Object var1) {
         return Collections.eq(var1, this.element);
      }

      public void forEach(Consumer<? super E> var1) {
         var1.accept(this.element);
      }

      public Spliterator<E> spliterator() {
         return Collections.singletonSpliterator(this.element);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         throw new UnsupportedOperationException();
      }
   }

   private static class EmptyMap<K, V> extends AbstractMap<K, V> implements Serializable {
      private static final long serialVersionUID = 6428348081105594320L;

      private EmptyMap() {
      }

      public int size() {
         return 0;
      }

      public boolean isEmpty() {
         return true;
      }

      public boolean containsKey(Object var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public V get(Object var1) {
         return null;
      }

      public Set<K> keySet() {
         return Collections.emptySet();
      }

      public Collection<V> values() {
         return Collections.emptySet();
      }

      public Set<Map.Entry<K, V>> entrySet() {
         return Collections.emptySet();
      }

      public boolean equals(Object var1) {
         return var1 instanceof Map && ((Map)var1).isEmpty();
      }

      public int hashCode() {
         return 0;
      }

      public V getOrDefault(Object var1, V var2) {
         return var2;
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         Objects.requireNonNull(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         Objects.requireNonNull(var1);
      }

      public V putIfAbsent(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V replace(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      private Object readResolve() {
         return Collections.EMPTY_MAP;
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }

   private static class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 8842843931221139166L;

      private EmptyList() {
      }

      public Iterator<E> iterator() {
         return Collections.emptyIterator();
      }

      public ListIterator<E> listIterator() {
         return Collections.emptyListIterator();
      }

      public int size() {
         return 0;
      }

      public boolean isEmpty() {
         return true;
      }

      public boolean contains(Object var1) {
         return false;
      }

      public boolean containsAll(Collection<?> var1) {
         return var1.isEmpty();
      }

      public Object[] toArray() {
         return new Object[0];
      }

      public <T> T[] toArray(T[] var1) {
         if (var1.length > 0) {
            var1[0] = null;
         }

         return var1;
      }

      public E get(int var1) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }

      public boolean equals(Object var1) {
         return var1 instanceof List && ((List)var1).isEmpty();
      }

      public int hashCode() {
         return 1;
      }

      public boolean removeIf(Predicate<? super E> var1) {
         Objects.requireNonNull(var1);
         return false;
      }

      public void replaceAll(UnaryOperator<E> var1) {
         Objects.requireNonNull(var1);
      }

      public void sort(Comparator<? super E> var1) {
      }

      public void forEach(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
      }

      public Spliterator<E> spliterator() {
         return Spliterators.emptySpliterator();
      }

      private Object readResolve() {
         return Collections.EMPTY_LIST;
      }

      // $FF: synthetic method
      EmptyList(Object var1) {
         this();
      }
   }

   private static class EmptySet<E> extends AbstractSet<E> implements Serializable {
      private static final long serialVersionUID = 1582296315990362920L;

      private EmptySet() {
      }

      public Iterator<E> iterator() {
         return Collections.emptyIterator();
      }

      public int size() {
         return 0;
      }

      public boolean isEmpty() {
         return true;
      }

      public boolean contains(Object var1) {
         return false;
      }

      public boolean containsAll(Collection<?> var1) {
         return var1.isEmpty();
      }

      public Object[] toArray() {
         return new Object[0];
      }

      public <T> T[] toArray(T[] var1) {
         if (var1.length > 0) {
            var1[0] = null;
         }

         return var1;
      }

      public void forEach(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         Objects.requireNonNull(var1);
         return false;
      }

      public Spliterator<E> spliterator() {
         return Spliterators.emptySpliterator();
      }

      private Object readResolve() {
         return Collections.EMPTY_SET;
      }

      // $FF: synthetic method
      EmptySet(Object var1) {
         this();
      }
   }

   private static class EmptyEnumeration<E> implements Enumeration<E> {
      static final Collections.EmptyEnumeration<Object> EMPTY_ENUMERATION = new Collections.EmptyEnumeration();

      public boolean hasMoreElements() {
         return false;
      }

      public E nextElement() {
         throw new NoSuchElementException();
      }
   }

   private static class EmptyListIterator<E> extends Collections.EmptyIterator<E> implements ListIterator<E> {
      static final Collections.EmptyListIterator<Object> EMPTY_ITERATOR = new Collections.EmptyListIterator();

      private EmptyListIterator() {
         super(null);
      }

      public boolean hasPrevious() {
         return false;
      }

      public E previous() {
         throw new NoSuchElementException();
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }

      public void set(E var1) {
         throw new IllegalStateException();
      }

      public void add(E var1) {
         throw new UnsupportedOperationException();
      }
   }

   private static class EmptyIterator<E> implements Iterator<E> {
      static final Collections.EmptyIterator<Object> EMPTY_ITERATOR = new Collections.EmptyIterator();

      private EmptyIterator() {
      }

      public boolean hasNext() {
         return false;
      }

      public E next() {
         throw new NoSuchElementException();
      }

      public void remove() {
         throw new IllegalStateException();
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
      }

      // $FF: synthetic method
      EmptyIterator(Object var1) {
         this();
      }
   }

   static class CheckedNavigableMap<K, V> extends Collections.CheckedSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
      private static final long serialVersionUID = -4852462692372534096L;
      private final NavigableMap<K, V> nm;

      CheckedNavigableMap(NavigableMap<K, V> var1, Class<K> var2, Class<V> var3) {
         super(var1, var2, var3);
         this.nm = var1;
      }

      public Comparator<? super K> comparator() {
         return this.nm.comparator();
      }

      public K firstKey() {
         return this.nm.firstKey();
      }

      public K lastKey() {
         return this.nm.lastKey();
      }

      public Map.Entry<K, V> lowerEntry(K var1) {
         Map.Entry var2 = this.nm.lowerEntry(var1);
         return null != var2 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var2, this.valueType) : null;
      }

      public K lowerKey(K var1) {
         return this.nm.lowerKey(var1);
      }

      public Map.Entry<K, V> floorEntry(K var1) {
         Map.Entry var2 = this.nm.floorEntry(var1);
         return null != var2 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var2, this.valueType) : null;
      }

      public K floorKey(K var1) {
         return this.nm.floorKey(var1);
      }

      public Map.Entry<K, V> ceilingEntry(K var1) {
         Map.Entry var2 = this.nm.ceilingEntry(var1);
         return null != var2 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var2, this.valueType) : null;
      }

      public K ceilingKey(K var1) {
         return this.nm.ceilingKey(var1);
      }

      public Map.Entry<K, V> higherEntry(K var1) {
         Map.Entry var2 = this.nm.higherEntry(var1);
         return null != var2 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var2, this.valueType) : null;
      }

      public K higherKey(K var1) {
         return this.nm.higherKey(var1);
      }

      public Map.Entry<K, V> firstEntry() {
         Map.Entry var1 = this.nm.firstEntry();
         return null != var1 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var1, this.valueType) : null;
      }

      public Map.Entry<K, V> lastEntry() {
         Map.Entry var1 = this.nm.lastEntry();
         return null != var1 ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var1, this.valueType) : null;
      }

      public Map.Entry<K, V> pollFirstEntry() {
         Map.Entry var1 = this.nm.pollFirstEntry();
         return null == var1 ? null : new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var1, this.valueType);
      }

      public Map.Entry<K, V> pollLastEntry() {
         Map.Entry var1 = this.nm.pollLastEntry();
         return null == var1 ? null : new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var1, this.valueType);
      }

      public NavigableMap<K, V> descendingMap() {
         return Collections.checkedNavigableMap(this.nm.descendingMap(), this.keyType, this.valueType);
      }

      public NavigableSet<K> keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet<K> navigableKeySet() {
         return Collections.checkedNavigableSet(this.nm.navigableKeySet(), this.keyType);
      }

      public NavigableSet<K> descendingKeySet() {
         return Collections.checkedNavigableSet(this.nm.descendingKeySet(), this.keyType);
      }

      public NavigableMap<K, V> subMap(K var1, K var2) {
         return Collections.checkedNavigableMap(this.nm.subMap(var1, true, var2, false), this.keyType, this.valueType);
      }

      public NavigableMap<K, V> headMap(K var1) {
         return Collections.checkedNavigableMap(this.nm.headMap(var1, false), this.keyType, this.valueType);
      }

      public NavigableMap<K, V> tailMap(K var1) {
         return Collections.checkedNavigableMap(this.nm.tailMap(var1, true), this.keyType, this.valueType);
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         return Collections.checkedNavigableMap(this.nm.subMap(var1, var2, var3, var4), this.keyType, this.valueType);
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         return Collections.checkedNavigableMap(this.nm.headMap(var1, var2), this.keyType, this.valueType);
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         return Collections.checkedNavigableMap(this.nm.tailMap(var1, var2), this.keyType, this.valueType);
      }
   }

   static class CheckedSortedMap<K, V> extends Collections.CheckedMap<K, V> implements SortedMap<K, V>, Serializable {
      private static final long serialVersionUID = 1599671320688067438L;
      private final SortedMap<K, V> sm;

      CheckedSortedMap(SortedMap<K, V> var1, Class<K> var2, Class<V> var3) {
         super(var1, var2, var3);
         this.sm = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sm.comparator();
      }

      public K firstKey() {
         return this.sm.firstKey();
      }

      public K lastKey() {
         return this.sm.lastKey();
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         return Collections.checkedSortedMap(this.sm.subMap(var1, var2), this.keyType, this.valueType);
      }

      public SortedMap<K, V> headMap(K var1) {
         return Collections.checkedSortedMap(this.sm.headMap(var1), this.keyType, this.valueType);
      }

      public SortedMap<K, V> tailMap(K var1) {
         return Collections.checkedSortedMap(this.sm.tailMap(var1), this.keyType, this.valueType);
      }
   }

   private static class CheckedMap<K, V> implements Map<K, V>, Serializable {
      private static final long serialVersionUID = 5742860141034234728L;
      private final Map<K, V> m;
      final Class<K> keyType;
      final Class<V> valueType;
      private transient Set<Map.Entry<K, V>> entrySet;

      private void typeCheck(Object var1, Object var2) {
         if (var1 != null && !this.keyType.isInstance(var1)) {
            throw new ClassCastException(this.badKeyMsg(var1));
         } else if (var2 != null && !this.valueType.isInstance(var2)) {
            throw new ClassCastException(this.badValueMsg(var2));
         }
      }

      private BiFunction<? super K, ? super V, ? extends V> typeCheck(BiFunction<? super K, ? super V, ? extends V> var1) {
         Objects.requireNonNull(var1);
         return (var2, var3) -> {
            Object var4 = var1.apply(var2, var3);
            this.typeCheck(var2, var4);
            return var4;
         };
      }

      private String badKeyMsg(Object var1) {
         return "Attempt to insert " + var1.getClass() + " key into map with key type " + this.keyType;
      }

      private String badValueMsg(Object var1) {
         return "Attempt to insert " + var1.getClass() + " value into map with value type " + this.valueType;
      }

      CheckedMap(Map<K, V> var1, Class<K> var2, Class<V> var3) {
         this.m = (Map)Objects.requireNonNull(var1);
         this.keyType = (Class)Objects.requireNonNull(var2);
         this.valueType = (Class)Objects.requireNonNull(var3);
      }

      public int size() {
         return this.m.size();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public boolean containsKey(Object var1) {
         return this.m.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         return this.m.containsValue(var1);
      }

      public V get(Object var1) {
         return this.m.get(var1);
      }

      public V remove(Object var1) {
         return this.m.remove(var1);
      }

      public void clear() {
         this.m.clear();
      }

      public Set<K> keySet() {
         return this.m.keySet();
      }

      public Collection<V> values() {
         return this.m.values();
      }

      public boolean equals(Object var1) {
         return var1 == this || this.m.equals(var1);
      }

      public int hashCode() {
         return this.m.hashCode();
      }

      public String toString() {
         return this.m.toString();
      }

      public V put(K var1, V var2) {
         this.typeCheck(var1, var2);
         return this.m.put(var1, var2);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         Object[] var2 = var1.entrySet().toArray();
         ArrayList var3 = new ArrayList(var2.length);
         Object[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var4[var6];
            Map.Entry var8 = (Map.Entry)var7;
            Object var9 = var8.getKey();
            Object var10 = var8.getValue();
            this.typeCheck(var9, var10);
            var3.add(new AbstractMap.SimpleImmutableEntry(var9, var10));
         }

         Iterator var11 = var3.iterator();

         while(var11.hasNext()) {
            Map.Entry var12 = (Map.Entry)var11.next();
            this.m.put(var12.getKey(), var12.getValue());
         }

      }

      public Set<Map.Entry<K, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = new Collections.CheckedMap.CheckedEntrySet(this.m.entrySet(), this.valueType);
         }

         return this.entrySet;
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         this.m.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         this.m.replaceAll(this.typeCheck(var1));
      }

      public V putIfAbsent(K var1, V var2) {
         this.typeCheck(var1, var2);
         return this.m.putIfAbsent(var1, var2);
      }

      public boolean remove(Object var1, Object var2) {
         return this.m.remove(var1, var2);
      }

      public boolean replace(K var1, V var2, V var3) {
         this.typeCheck(var1, var3);
         return this.m.replace(var1, var2, var3);
      }

      public V replace(K var1, V var2) {
         this.typeCheck(var1, var2);
         return this.m.replace(var1, var2);
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         Objects.requireNonNull(var2);
         return this.m.computeIfAbsent(var1, (var2x) -> {
            Object var3 = var2.apply(var2x);
            this.typeCheck(var2x, var3);
            return var3;
         });
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         return this.m.computeIfPresent(var1, this.typeCheck(var2));
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         return this.m.compute(var1, this.typeCheck(var2));
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         Objects.requireNonNull(var3);
         return this.m.merge(var1, var2, (var2x, var3x) -> {
            Object var4 = var3.apply(var2x, var3x);
            this.typeCheck((Object)null, var4);
            return var4;
         });
      }

      static class CheckedEntrySet<K, V> implements Set<Map.Entry<K, V>> {
         private final Set<Map.Entry<K, V>> s;
         private final Class<V> valueType;

         CheckedEntrySet(Set<Map.Entry<K, V>> var1, Class<V> var2) {
            this.s = var1;
            this.valueType = var2;
         }

         public int size() {
            return this.s.size();
         }

         public boolean isEmpty() {
            return this.s.isEmpty();
         }

         public String toString() {
            return this.s.toString();
         }

         public int hashCode() {
            return this.s.hashCode();
         }

         public void clear() {
            this.s.clear();
         }

         public boolean add(Map.Entry<K, V> var1) {
            throw new UnsupportedOperationException();
         }

         public boolean addAll(Collection<? extends Map.Entry<K, V>> var1) {
            throw new UnsupportedOperationException();
         }

         public Iterator<Map.Entry<K, V>> iterator() {
            final Iterator var1 = this.s.iterator();
            final Class var2 = this.valueType;
            return new Iterator<Map.Entry<K, V>>() {
               public boolean hasNext() {
                  return var1.hasNext();
               }

               public void remove() {
                  var1.remove();
               }

               public Map.Entry<K, V> next() {
                  return Collections.CheckedMap.CheckedEntrySet.checkedEntry((Map.Entry)var1.next(), var2);
               }
            };
         }

         public Object[] toArray() {
            Object[] var1 = this.s.toArray();
            Object[] var2 = Collections.CheckedMap.CheckedEntrySet.CheckedEntry.class.isInstance(var1.getClass().getComponentType()) ? var1 : new Object[var1.length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
               var2[var3] = checkedEntry((Map.Entry)var1[var3], this.valueType);
            }

            return var2;
         }

         public <T> T[] toArray(T[] var1) {
            Object[] var2 = this.s.toArray(var1.length == 0 ? var1 : Arrays.copyOf((Object[])var1, 0));

            for(int var3 = 0; var3 < var2.length; ++var3) {
               var2[var3] = checkedEntry((Map.Entry)var2[var3], this.valueType);
            }

            if (var2.length > var1.length) {
               return var2;
            } else {
               System.arraycopy(var2, 0, var1, 0, var2.length);
               if (var1.length > var2.length) {
                  var1[var2.length] = null;
               }

               return var1;
            }
         }

         public boolean contains(Object var1) {
            if (!(var1 instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry var2 = (Map.Entry)var1;
               return this.s.contains(var2 instanceof Collections.CheckedMap.CheckedEntrySet.CheckedEntry ? var2 : checkedEntry(var2, this.valueType));
            }
         }

         public boolean containsAll(Collection<?> var1) {
            Iterator var2 = var1.iterator();

            Object var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = var2.next();
            } while(this.contains(var3));

            return false;
         }

         public boolean remove(Object var1) {
            return !(var1 instanceof Map.Entry) ? false : this.s.remove(new AbstractMap.SimpleImmutableEntry((Map.Entry)var1));
         }

         public boolean removeAll(Collection<?> var1) {
            return this.batchRemove(var1, false);
         }

         public boolean retainAll(Collection<?> var1) {
            return this.batchRemove(var1, true);
         }

         private boolean batchRemove(Collection<?> var1, boolean var2) {
            Objects.requireNonNull(var1);
            boolean var3 = false;
            Iterator var4 = this.iterator();

            while(var4.hasNext()) {
               if (var1.contains(var4.next()) != var2) {
                  var4.remove();
                  var3 = true;
               }
            }

            return var3;
         }

         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Set)) {
               return false;
            } else {
               Set var2 = (Set)var1;
               return var2.size() == this.s.size() && this.containsAll(var2);
            }
         }

         static <K, V, T> Collections.CheckedMap.CheckedEntrySet.CheckedEntry<K, V, T> checkedEntry(Map.Entry<K, V> var0, Class<T> var1) {
            return new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(var0, var1);
         }

         private static class CheckedEntry<K, V, T> implements Map.Entry<K, V> {
            private final Map.Entry<K, V> e;
            private final Class<T> valueType;

            CheckedEntry(Map.Entry<K, V> var1, Class<T> var2) {
               this.e = (Map.Entry)Objects.requireNonNull(var1);
               this.valueType = (Class)Objects.requireNonNull(var2);
            }

            public K getKey() {
               return this.e.getKey();
            }

            public V getValue() {
               return this.e.getValue();
            }

            public int hashCode() {
               return this.e.hashCode();
            }

            public String toString() {
               return this.e.toString();
            }

            public V setValue(V var1) {
               if (var1 != null && !this.valueType.isInstance(var1)) {
                  throw new ClassCastException(this.badValueMsg(var1));
               } else {
                  return this.e.setValue(var1);
               }
            }

            private String badValueMsg(Object var1) {
               return "Attempt to insert " + var1.getClass() + " value into map with value type " + this.valueType;
            }

            public boolean equals(Object var1) {
               if (var1 == this) {
                  return true;
               } else {
                  return !(var1 instanceof Map.Entry) ? false : this.e.equals(new AbstractMap.SimpleImmutableEntry((Map.Entry)var1));
               }
            }
         }
      }
   }

   static class CheckedRandomAccessList<E> extends Collections.CheckedList<E> implements RandomAccess {
      private static final long serialVersionUID = 1638200125423088369L;

      CheckedRandomAccessList(List<E> var1, Class<E> var2) {
         super(var1, var2);
      }

      public List<E> subList(int var1, int var2) {
         return new Collections.CheckedRandomAccessList(this.list.subList(var1, var2), this.type);
      }
   }

   static class CheckedList<E> extends Collections.CheckedCollection<E> implements List<E> {
      private static final long serialVersionUID = 65247728283967356L;
      final List<E> list;

      CheckedList(List<E> var1, Class<E> var2) {
         super(var1, var2);
         this.list = var1;
      }

      public boolean equals(Object var1) {
         return var1 == this || this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public E get(int var1) {
         return this.list.get(var1);
      }

      public E remove(int var1) {
         return this.list.remove(var1);
      }

      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }

      public E set(int var1, E var2) {
         return this.list.set(var1, this.typeCheck(var2));
      }

      public void add(int var1, E var2) {
         this.list.add(var1, this.typeCheck(var2));
      }

      public boolean addAll(int var1, Collection<? extends E> var2) {
         return this.list.addAll(var1, this.checkedCopyOf(var2));
      }

      public ListIterator<E> listIterator() {
         return this.listIterator(0);
      }

      public ListIterator<E> listIterator(int var1) {
         final ListIterator var2 = this.list.listIterator(var1);
         return new ListIterator<E>() {
            public boolean hasNext() {
               return var2.hasNext();
            }

            public E next() {
               return var2.next();
            }

            public boolean hasPrevious() {
               return var2.hasPrevious();
            }

            public E previous() {
               return var2.previous();
            }

            public int nextIndex() {
               return var2.nextIndex();
            }

            public int previousIndex() {
               return var2.previousIndex();
            }

            public void remove() {
               var2.remove();
            }

            public void set(E var1) {
               var2.set(CheckedList.this.typeCheck(var1));
            }

            public void add(E var1) {
               var2.add(CheckedList.this.typeCheck(var1));
            }

            public void forEachRemaining(Consumer<? super E> var1) {
               var2.forEachRemaining(var1);
            }
         };
      }

      public List<E> subList(int var1, int var2) {
         return new Collections.CheckedList(this.list.subList(var1, var2), this.type);
      }

      public void replaceAll(UnaryOperator<E> var1) {
         Objects.requireNonNull(var1);
         this.list.replaceAll((var2) -> {
            return this.typeCheck(var1.apply(var2));
         });
      }

      public void sort(Comparator<? super E> var1) {
         this.list.sort(var1);
      }
   }

   static class CheckedNavigableSet<E> extends Collections.CheckedSortedSet<E> implements NavigableSet<E>, Serializable {
      private static final long serialVersionUID = -5429120189805438922L;
      private final NavigableSet<E> ns;

      CheckedNavigableSet(NavigableSet<E> var1, Class<E> var2) {
         super(var1, var2);
         this.ns = var1;
      }

      public E lower(E var1) {
         return this.ns.lower(var1);
      }

      public E floor(E var1) {
         return this.ns.floor(var1);
      }

      public E ceiling(E var1) {
         return this.ns.ceiling(var1);
      }

      public E higher(E var1) {
         return this.ns.higher(var1);
      }

      public E pollFirst() {
         return this.ns.pollFirst();
      }

      public E pollLast() {
         return this.ns.pollLast();
      }

      public NavigableSet<E> descendingSet() {
         return Collections.checkedNavigableSet(this.ns.descendingSet(), this.type);
      }

      public Iterator<E> descendingIterator() {
         return Collections.checkedNavigableSet(this.ns.descendingSet(), this.type).iterator();
      }

      public NavigableSet<E> subSet(E var1, E var2) {
         return Collections.checkedNavigableSet(this.ns.subSet(var1, true, var2, false), this.type);
      }

      public NavigableSet<E> headSet(E var1) {
         return Collections.checkedNavigableSet(this.ns.headSet(var1, false), this.type);
      }

      public NavigableSet<E> tailSet(E var1) {
         return Collections.checkedNavigableSet(this.ns.tailSet(var1, true), this.type);
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         return Collections.checkedNavigableSet(this.ns.subSet(var1, var2, var3, var4), this.type);
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         return Collections.checkedNavigableSet(this.ns.headSet(var1, var2), this.type);
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         return Collections.checkedNavigableSet(this.ns.tailSet(var1, var2), this.type);
      }
   }

   static class CheckedSortedSet<E> extends Collections.CheckedSet<E> implements SortedSet<E>, Serializable {
      private static final long serialVersionUID = 1599911165492914959L;
      private final SortedSet<E> ss;

      CheckedSortedSet(SortedSet<E> var1, Class<E> var2) {
         super(var1, var2);
         this.ss = var1;
      }

      public Comparator<? super E> comparator() {
         return this.ss.comparator();
      }

      public E first() {
         return this.ss.first();
      }

      public E last() {
         return this.ss.last();
      }

      public SortedSet<E> subSet(E var1, E var2) {
         return Collections.checkedSortedSet(this.ss.subSet(var1, var2), this.type);
      }

      public SortedSet<E> headSet(E var1) {
         return Collections.checkedSortedSet(this.ss.headSet(var1), this.type);
      }

      public SortedSet<E> tailSet(E var1) {
         return Collections.checkedSortedSet(this.ss.tailSet(var1), this.type);
      }
   }

   static class CheckedSet<E> extends Collections.CheckedCollection<E> implements Set<E>, Serializable {
      private static final long serialVersionUID = 4694047833775013803L;

      CheckedSet(Set<E> var1, Class<E> var2) {
         super(var1, var2);
      }

      public boolean equals(Object var1) {
         return var1 == this || this.c.equals(var1);
      }

      public int hashCode() {
         return this.c.hashCode();
      }
   }

   static class CheckedQueue<E> extends Collections.CheckedCollection<E> implements Queue<E>, Serializable {
      private static final long serialVersionUID = 1433151992604707767L;
      final Queue<E> queue;

      CheckedQueue(Queue<E> var1, Class<E> var2) {
         super(var1, var2);
         this.queue = var1;
      }

      public E element() {
         return this.queue.element();
      }

      public boolean equals(Object var1) {
         return var1 == this || this.c.equals(var1);
      }

      public int hashCode() {
         return this.c.hashCode();
      }

      public E peek() {
         return this.queue.peek();
      }

      public E poll() {
         return this.queue.poll();
      }

      public E remove() {
         return this.queue.remove();
      }

      public boolean offer(E var1) {
         return this.queue.offer(this.typeCheck(var1));
      }
   }

   static class CheckedCollection<E> implements Collection<E>, Serializable {
      private static final long serialVersionUID = 1578914078182001775L;
      final Collection<E> c;
      final Class<E> type;
      private E[] zeroLengthElementArray;

      E typeCheck(Object var1) {
         if (var1 != null && !this.type.isInstance(var1)) {
            throw new ClassCastException(this.badElementMsg(var1));
         } else {
            return var1;
         }
      }

      private String badElementMsg(Object var1) {
         return "Attempt to insert " + var1.getClass() + " element into collection with element type " + this.type;
      }

      CheckedCollection(Collection<E> var1, Class<E> var2) {
         this.c = (Collection)Objects.requireNonNull(var1, (String)"c");
         this.type = (Class)Objects.requireNonNull(var2, (String)"type");
      }

      public int size() {
         return this.c.size();
      }

      public boolean isEmpty() {
         return this.c.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.c.contains(var1);
      }

      public Object[] toArray() {
         return this.c.toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.c.toArray(var1);
      }

      public String toString() {
         return this.c.toString();
      }

      public boolean remove(Object var1) {
         return this.c.remove(var1);
      }

      public void clear() {
         this.c.clear();
      }

      public boolean containsAll(Collection<?> var1) {
         return this.c.containsAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.c.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.c.retainAll(var1);
      }

      public Iterator<E> iterator() {
         final Iterator var1 = this.c.iterator();
         return new Iterator<E>() {
            public boolean hasNext() {
               return var1.hasNext();
            }

            public E next() {
               return var1.next();
            }

            public void remove() {
               var1.remove();
            }
         };
      }

      public boolean add(E var1) {
         return this.c.add(this.typeCheck(var1));
      }

      private E[] zeroLengthElementArray() {
         return this.zeroLengthElementArray != null ? this.zeroLengthElementArray : (this.zeroLengthElementArray = Collections.zeroLengthArray(this.type));
      }

      Collection<E> checkedCopyOf(Collection<? extends E> var1) {
         Object[] var2;
         try {
            Object[] var3 = this.zeroLengthElementArray();
            var2 = var1.toArray(var3);
            if (var2.getClass() != var3.getClass()) {
               var2 = Arrays.copyOf(var2, var2.length, var3.getClass());
            }
         } catch (ArrayStoreException var8) {
            var2 = (Object[])var1.toArray().clone();
            Object[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Object var7 = var4[var6];
               this.typeCheck(var7);
            }
         }

         return Arrays.asList(var2);
      }

      public boolean addAll(Collection<? extends E> var1) {
         return this.c.addAll(this.checkedCopyOf(var1));
      }

      public void forEach(Consumer<? super E> var1) {
         this.c.forEach(var1);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         return this.c.removeIf(var1);
      }

      public Spliterator<E> spliterator() {
         return this.c.spliterator();
      }

      public Stream<E> stream() {
         return this.c.stream();
      }

      public Stream<E> parallelStream() {
         return this.c.parallelStream();
      }
   }

   static class SynchronizedNavigableMap<K, V> extends Collections.SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
      private static final long serialVersionUID = 699392247599746807L;
      private final NavigableMap<K, V> nm;

      SynchronizedNavigableMap(NavigableMap<K, V> var1) {
         super(var1);
         this.nm = var1;
      }

      SynchronizedNavigableMap(NavigableMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.nm = var1;
      }

      public Map.Entry<K, V> lowerEntry(K var1) {
         synchronized(this.mutex) {
            return this.nm.lowerEntry(var1);
         }
      }

      public K lowerKey(K var1) {
         synchronized(this.mutex) {
            return this.nm.lowerKey(var1);
         }
      }

      public Map.Entry<K, V> floorEntry(K var1) {
         synchronized(this.mutex) {
            return this.nm.floorEntry(var1);
         }
      }

      public K floorKey(K var1) {
         synchronized(this.mutex) {
            return this.nm.floorKey(var1);
         }
      }

      public Map.Entry<K, V> ceilingEntry(K var1) {
         synchronized(this.mutex) {
            return this.nm.ceilingEntry(var1);
         }
      }

      public K ceilingKey(K var1) {
         synchronized(this.mutex) {
            return this.nm.ceilingKey(var1);
         }
      }

      public Map.Entry<K, V> higherEntry(K var1) {
         synchronized(this.mutex) {
            return this.nm.higherEntry(var1);
         }
      }

      public K higherKey(K var1) {
         synchronized(this.mutex) {
            return this.nm.higherKey(var1);
         }
      }

      public Map.Entry<K, V> firstEntry() {
         synchronized(this.mutex) {
            return this.nm.firstEntry();
         }
      }

      public Map.Entry<K, V> lastEntry() {
         synchronized(this.mutex) {
            return this.nm.lastEntry();
         }
      }

      public Map.Entry<K, V> pollFirstEntry() {
         synchronized(this.mutex) {
            return this.nm.pollFirstEntry();
         }
      }

      public Map.Entry<K, V> pollLastEntry() {
         synchronized(this.mutex) {
            return this.nm.pollLastEntry();
         }
      }

      public NavigableMap<K, V> descendingMap() {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.descendingMap(), this.mutex);
         }
      }

      public NavigableSet<K> keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet<K> navigableKeySet() {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.nm.navigableKeySet(), this.mutex);
         }
      }

      public NavigableSet<K> descendingKeySet() {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.nm.descendingKeySet(), this.mutex);
         }
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.subMap(var1, true, var2, false), this.mutex);
         }
      }

      public SortedMap<K, V> headMap(K var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.headMap(var1, false), this.mutex);
         }
      }

      public SortedMap<K, V> tailMap(K var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.tailMap(var1, true), this.mutex);
         }
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.subMap(var1, var2, var3, var4), this.mutex);
         }
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.headMap(var1, var2), this.mutex);
         }
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableMap(this.nm.tailMap(var1, var2), this.mutex);
         }
      }
   }

   static class SynchronizedSortedMap<K, V> extends Collections.SynchronizedMap<K, V> implements SortedMap<K, V> {
      private static final long serialVersionUID = -8798146769416483793L;
      private final SortedMap<K, V> sm;

      SynchronizedSortedMap(SortedMap<K, V> var1) {
         super(var1);
         this.sm = var1;
      }

      SynchronizedSortedMap(SortedMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.sm = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.mutex) {
            return this.sm.comparator();
         }
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedMap(this.sm.subMap(var1, var2), this.mutex);
         }
      }

      public SortedMap<K, V> headMap(K var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedMap(this.sm.headMap(var1), this.mutex);
         }
      }

      public SortedMap<K, V> tailMap(K var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedMap(this.sm.tailMap(var1), this.mutex);
         }
      }

      public K firstKey() {
         synchronized(this.mutex) {
            return this.sm.firstKey();
         }
      }

      public K lastKey() {
         synchronized(this.mutex) {
            return this.sm.lastKey();
         }
      }
   }

   private static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
      private static final long serialVersionUID = 1978198479659022715L;
      private final Map<K, V> m;
      final Object mutex;
      private transient Set<K> keySet;
      private transient Set<Map.Entry<K, V>> entrySet;
      private transient Collection<V> values;

      SynchronizedMap(Map<K, V> var1) {
         this.m = (Map)Objects.requireNonNull(var1);
         this.mutex = this;
      }

      SynchronizedMap(Map<K, V> var1, Object var2) {
         this.m = var1;
         this.mutex = var2;
      }

      public int size() {
         synchronized(this.mutex) {
            return this.m.size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.m.isEmpty();
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.mutex) {
            return this.m.containsKey(var1);
         }
      }

      public boolean containsValue(Object var1) {
         synchronized(this.mutex) {
            return this.m.containsValue(var1);
         }
      }

      public V get(Object var1) {
         synchronized(this.mutex) {
            return this.m.get(var1);
         }
      }

      public V put(K var1, V var2) {
         synchronized(this.mutex) {
            return this.m.put(var1, var2);
         }
      }

      public V remove(Object var1) {
         synchronized(this.mutex) {
            return this.m.remove(var1);
         }
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         synchronized(this.mutex) {
            this.m.putAll(var1);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.m.clear();
         }
      }

      public Set<K> keySet() {
         synchronized(this.mutex) {
            if (this.keySet == null) {
               this.keySet = new Collections.SynchronizedSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      public Set<Map.Entry<K, V>> entrySet() {
         synchronized(this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = new Collections.SynchronizedSet(this.m.entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      public Collection<V> values() {
         synchronized(this.mutex) {
            if (this.values == null) {
               this.values = new Collections.SynchronizedCollection(this.m.values(), this.mutex);
            }

            return this.values;
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.m.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.m.hashCode();
         }
      }

      public String toString() {
         synchronized(this.mutex) {
            return this.m.toString();
         }
      }

      public V getOrDefault(Object var1, V var2) {
         synchronized(this.mutex) {
            return this.m.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized(this.mutex) {
            this.m.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         synchronized(this.mutex) {
            this.m.replaceAll(var1);
         }
      }

      public V putIfAbsent(K var1, V var2) {
         synchronized(this.mutex) {
            return this.m.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, Object var2) {
         synchronized(this.mutex) {
            return this.m.remove(var1, var2);
         }
      }

      public boolean replace(K var1, V var2, V var3) {
         synchronized(this.mutex) {
            return this.m.replace(var1, var2, var3);
         }
      }

      public V replace(K var1, V var2) {
         synchronized(this.mutex) {
            return this.m.replace(var1, var2);
         }
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.m.computeIfAbsent(var1, var2);
         }
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.m.computeIfPresent(var1, var2);
         }
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.m.compute(var1, var2);
         }
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.mutex) {
            return this.m.merge(var1, var2, var3);
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.mutex) {
            var1.defaultWriteObject();
         }
      }
   }

   static class SynchronizedRandomAccessList<E> extends Collections.SynchronizedList<E> implements RandomAccess {
      private static final long serialVersionUID = 1530674583602358482L;

      SynchronizedRandomAccessList(List<E> var1) {
         super(var1);
      }

      SynchronizedRandomAccessList(List<E> var1, Object var2) {
         super(var1, var2);
      }

      public List<E> subList(int var1, int var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.mutex);
         }
      }

      private Object writeReplace() {
         return new Collections.SynchronizedList(this.list);
      }
   }

   static class SynchronizedList<E> extends Collections.SynchronizedCollection<E> implements List<E> {
      private static final long serialVersionUID = -7754090372962971524L;
      final List<E> list;

      SynchronizedList(List<E> var1) {
         super(var1);
         this.list = var1;
      }

      SynchronizedList(List<E> var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.list.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.list.hashCode();
         }
      }

      public E get(int var1) {
         synchronized(this.mutex) {
            return this.list.get(var1);
         }
      }

      public E set(int var1, E var2) {
         synchronized(this.mutex) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, E var2) {
         synchronized(this.mutex) {
            this.list.add(var1, var2);
         }
      }

      public E remove(int var1) {
         synchronized(this.mutex) {
            return this.list.remove(var1);
         }
      }

      public int indexOf(Object var1) {
         synchronized(this.mutex) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(Object var1) {
         synchronized(this.mutex) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends E> var2) {
         synchronized(this.mutex) {
            return this.list.addAll(var1, var2);
         }
      }

      public ListIterator<E> listIterator() {
         return this.list.listIterator();
      }

      public ListIterator<E> listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public List<E> subList(int var1, int var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedList(this.list.subList(var1, var2), this.mutex);
         }
      }

      public void replaceAll(UnaryOperator<E> var1) {
         synchronized(this.mutex) {
            this.list.replaceAll(var1);
         }
      }

      public void sort(Comparator<? super E> var1) {
         synchronized(this.mutex) {
            this.list.sort(var1);
         }
      }

      private Object readResolve() {
         return this.list instanceof RandomAccess ? new Collections.SynchronizedRandomAccessList(this.list) : this;
      }
   }

   static class SynchronizedNavigableSet<E> extends Collections.SynchronizedSortedSet<E> implements NavigableSet<E> {
      private static final long serialVersionUID = -5505529816273629798L;
      private final NavigableSet<E> ns;

      SynchronizedNavigableSet(NavigableSet<E> var1) {
         super(var1);
         this.ns = var1;
      }

      SynchronizedNavigableSet(NavigableSet<E> var1, Object var2) {
         super(var1, var2);
         this.ns = var1;
      }

      public E lower(E var1) {
         synchronized(this.mutex) {
            return this.ns.lower(var1);
         }
      }

      public E floor(E var1) {
         synchronized(this.mutex) {
            return this.ns.floor(var1);
         }
      }

      public E ceiling(E var1) {
         synchronized(this.mutex) {
            return this.ns.ceiling(var1);
         }
      }

      public E higher(E var1) {
         synchronized(this.mutex) {
            return this.ns.higher(var1);
         }
      }

      public E pollFirst() {
         synchronized(this.mutex) {
            return this.ns.pollFirst();
         }
      }

      public E pollLast() {
         synchronized(this.mutex) {
            return this.ns.pollLast();
         }
      }

      public NavigableSet<E> descendingSet() {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.descendingSet(), this.mutex);
         }
      }

      public Iterator<E> descendingIterator() {
         synchronized(this.mutex) {
            return this.descendingSet().iterator();
         }
      }

      public NavigableSet<E> subSet(E var1, E var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.subSet(var1, true, var2, false), this.mutex);
         }
      }

      public NavigableSet<E> headSet(E var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.headSet(var1, false), this.mutex);
         }
      }

      public NavigableSet<E> tailSet(E var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.tailSet(var1, true), this.mutex);
         }
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.subSet(var1, var2, var3, var4), this.mutex);
         }
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.headSet(var1, var2), this.mutex);
         }
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedNavigableSet(this.ns.tailSet(var1, var2), this.mutex);
         }
      }
   }

   static class SynchronizedSortedSet<E> extends Collections.SynchronizedSet<E> implements SortedSet<E> {
      private static final long serialVersionUID = 8695801310862127406L;
      private final SortedSet<E> ss;

      SynchronizedSortedSet(SortedSet<E> var1) {
         super(var1);
         this.ss = var1;
      }

      SynchronizedSortedSet(SortedSet<E> var1, Object var2) {
         super(var1, var2);
         this.ss = var1;
      }

      public Comparator<? super E> comparator() {
         synchronized(this.mutex) {
            return this.ss.comparator();
         }
      }

      public SortedSet<E> subSet(E var1, E var2) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedSet(this.ss.subSet(var1, var2), this.mutex);
         }
      }

      public SortedSet<E> headSet(E var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedSet(this.ss.headSet(var1), this.mutex);
         }
      }

      public SortedSet<E> tailSet(E var1) {
         synchronized(this.mutex) {
            return new Collections.SynchronizedSortedSet(this.ss.tailSet(var1), this.mutex);
         }
      }

      public E first() {
         synchronized(this.mutex) {
            return this.ss.first();
         }
      }

      public E last() {
         synchronized(this.mutex) {
            return this.ss.last();
         }
      }
   }

   static class SynchronizedSet<E> extends Collections.SynchronizedCollection<E> implements Set<E> {
      private static final long serialVersionUID = 487447009682186044L;

      SynchronizedSet(Set<E> var1) {
         super(var1);
      }

      SynchronizedSet(Set<E> var1, Object var2) {
         super(var1, var2);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.c.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.c.hashCode();
         }
      }
   }

   static class SynchronizedCollection<E> implements Collection<E>, Serializable {
      private static final long serialVersionUID = 3053995032091335093L;
      final Collection<E> c;
      final Object mutex;

      SynchronizedCollection(Collection<E> var1) {
         this.c = (Collection)Objects.requireNonNull(var1);
         this.mutex = this;
      }

      SynchronizedCollection(Collection<E> var1, Object var2) {
         this.c = (Collection)Objects.requireNonNull(var1);
         this.mutex = Objects.requireNonNull(var2);
      }

      public int size() {
         synchronized(this.mutex) {
            return this.c.size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.c.isEmpty();
         }
      }

      public boolean contains(Object var1) {
         synchronized(this.mutex) {
            return this.c.contains(var1);
         }
      }

      public Object[] toArray() {
         synchronized(this.mutex) {
            return this.c.toArray();
         }
      }

      public <T> T[] toArray(T[] var1) {
         synchronized(this.mutex) {
            return this.c.toArray(var1);
         }
      }

      public Iterator<E> iterator() {
         return this.c.iterator();
      }

      public boolean add(E var1) {
         synchronized(this.mutex) {
            return this.c.add(var1);
         }
      }

      public boolean remove(Object var1) {
         synchronized(this.mutex) {
            return this.c.remove(var1);
         }
      }

      public boolean containsAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.c.containsAll(var1);
         }
      }

      public boolean addAll(Collection<? extends E> var1) {
         synchronized(this.mutex) {
            return this.c.addAll(var1);
         }
      }

      public boolean removeAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.c.removeAll(var1);
         }
      }

      public boolean retainAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.c.retainAll(var1);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.c.clear();
         }
      }

      public String toString() {
         synchronized(this.mutex) {
            return this.c.toString();
         }
      }

      public void forEach(Consumer<? super E> var1) {
         synchronized(this.mutex) {
            this.c.forEach(var1);
         }
      }

      public boolean removeIf(Predicate<? super E> var1) {
         synchronized(this.mutex) {
            return this.c.removeIf(var1);
         }
      }

      public Spliterator<E> spliterator() {
         return this.c.spliterator();
      }

      public Stream<E> stream() {
         return this.c.stream();
      }

      public Stream<E> parallelStream() {
         return this.c.parallelStream();
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.mutex) {
            var1.defaultWriteObject();
         }
      }
   }

   static class UnmodifiableNavigableMap<K, V> extends Collections.UnmodifiableSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
      private static final long serialVersionUID = -4858195264774772197L;
      private static final Collections.UnmodifiableNavigableMap.EmptyNavigableMap<?, ?> EMPTY_NAVIGABLE_MAP = new Collections.UnmodifiableNavigableMap.EmptyNavigableMap();
      private final NavigableMap<K, ? extends V> nm;

      UnmodifiableNavigableMap(NavigableMap<K, ? extends V> var1) {
         super(var1);
         this.nm = var1;
      }

      public K lowerKey(K var1) {
         return this.nm.lowerKey(var1);
      }

      public K floorKey(K var1) {
         return this.nm.floorKey(var1);
      }

      public K ceilingKey(K var1) {
         return this.nm.ceilingKey(var1);
      }

      public K higherKey(K var1) {
         return this.nm.higherKey(var1);
      }

      public Map.Entry<K, V> lowerEntry(K var1) {
         Map.Entry var2 = this.nm.lowerEntry(var1);
         return null != var2 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var2) : null;
      }

      public Map.Entry<K, V> floorEntry(K var1) {
         Map.Entry var2 = this.nm.floorEntry(var1);
         return null != var2 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var2) : null;
      }

      public Map.Entry<K, V> ceilingEntry(K var1) {
         Map.Entry var2 = this.nm.ceilingEntry(var1);
         return null != var2 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var2) : null;
      }

      public Map.Entry<K, V> higherEntry(K var1) {
         Map.Entry var2 = this.nm.higherEntry(var1);
         return null != var2 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var2) : null;
      }

      public Map.Entry<K, V> firstEntry() {
         Map.Entry var1 = this.nm.firstEntry();
         return null != var1 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var1) : null;
      }

      public Map.Entry<K, V> lastEntry() {
         Map.Entry var1 = this.nm.lastEntry();
         return null != var1 ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var1) : null;
      }

      public Map.Entry<K, V> pollFirstEntry() {
         throw new UnsupportedOperationException();
      }

      public Map.Entry<K, V> pollLastEntry() {
         throw new UnsupportedOperationException();
      }

      public NavigableMap<K, V> descendingMap() {
         return Collections.unmodifiableNavigableMap(this.nm.descendingMap());
      }

      public NavigableSet<K> navigableKeySet() {
         return Collections.unmodifiableNavigableSet(this.nm.navigableKeySet());
      }

      public NavigableSet<K> descendingKeySet() {
         return Collections.unmodifiableNavigableSet(this.nm.descendingKeySet());
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         return Collections.unmodifiableNavigableMap(this.nm.subMap(var1, var2, var3, var4));
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         return Collections.unmodifiableNavigableMap(this.nm.headMap(var1, var2));
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         return Collections.unmodifiableNavigableMap(this.nm.tailMap(var1, var2));
      }

      private static class EmptyNavigableMap<K, V> extends Collections.UnmodifiableNavigableMap<K, V> implements Serializable {
         private static final long serialVersionUID = -2239321462712562324L;

         EmptyNavigableMap() {
            super(new TreeMap());
         }

         public NavigableSet<K> navigableKeySet() {
            return Collections.emptyNavigableSet();
         }

         private Object readResolve() {
            return Collections.UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
         }
      }
   }

   static class UnmodifiableSortedMap<K, V> extends Collections.UnmodifiableMap<K, V> implements SortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -8806743815996713206L;
      private final SortedMap<K, ? extends V> sm;

      UnmodifiableSortedMap(SortedMap<K, ? extends V> var1) {
         super(var1);
         this.sm = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sm.comparator();
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         return new Collections.UnmodifiableSortedMap(this.sm.subMap(var1, var2));
      }

      public SortedMap<K, V> headMap(K var1) {
         return new Collections.UnmodifiableSortedMap(this.sm.headMap(var1));
      }

      public SortedMap<K, V> tailMap(K var1) {
         return new Collections.UnmodifiableSortedMap(this.sm.tailMap(var1));
      }

      public K firstKey() {
         return this.sm.firstKey();
      }

      public K lastKey() {
         return this.sm.lastKey();
      }
   }

   private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
      private static final long serialVersionUID = -1034234728574286014L;
      private final Map<? extends K, ? extends V> m;
      private transient Set<K> keySet;
      private transient Set<Map.Entry<K, V>> entrySet;
      private transient Collection<V> values;

      UnmodifiableMap(Map<? extends K, ? extends V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.m = var1;
         }
      }

      public int size() {
         return this.m.size();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public boolean containsKey(Object var1) {
         return this.m.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         return this.m.containsValue(var1);
      }

      public V get(Object var1) {
         return this.m.get(var1);
      }

      public V put(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Set<K> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.m.keySet());
         }

         return this.keySet;
      }

      public Set<Map.Entry<K, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = new Collections.UnmodifiableMap.UnmodifiableEntrySet(this.m.entrySet());
         }

         return this.entrySet;
      }

      public Collection<V> values() {
         if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.m.values());
         }

         return this.values;
      }

      public boolean equals(Object var1) {
         return var1 == this || this.m.equals(var1);
      }

      public int hashCode() {
         return this.m.hashCode();
      }

      public String toString() {
         return this.m.toString();
      }

      public V getOrDefault(Object var1, V var2) {
         return this.m.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         this.m.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V replace(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      static class UnmodifiableEntrySet<K, V> extends Collections.UnmodifiableSet<Map.Entry<K, V>> {
         private static final long serialVersionUID = 7854390611657943733L;

         UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> var1) {
            super(var1);
         }

         static <K, V> Consumer<Map.Entry<K, V>> entryConsumer(Consumer<? super Map.Entry<K, V>> var0) {
            return (var1) -> {
               var0.accept(new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(var1));
            };
         }

         public void forEach(Consumer<? super Map.Entry<K, V>> var1) {
            Objects.requireNonNull(var1);
            this.c.forEach(entryConsumer(var1));
         }

         public Spliterator<Map.Entry<K, V>> spliterator() {
            return new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator(this.c.spliterator());
         }

         public Stream<Map.Entry<K, V>> stream() {
            return StreamSupport.stream(this.spliterator(), false);
         }

         public Stream<Map.Entry<K, V>> parallelStream() {
            return StreamSupport.stream(this.spliterator(), true);
         }

         public Iterator<Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
               private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i;

               {
                  this.i = UnmodifiableEntrySet.this.c.iterator();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public Map.Entry<K, V> next() {
                  return new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry((Map.Entry)this.i.next());
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         public Object[] toArray() {
            Object[] var1 = this.c.toArray();

            for(int var2 = 0; var2 < var1.length; ++var2) {
               var1[var2] = new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry((Map.Entry)var1[var2]);
            }

            return var1;
         }

         public <T> T[] toArray(T[] var1) {
            Object[] var2 = this.c.toArray(var1.length == 0 ? var1 : Arrays.copyOf((Object[])var1, 0));

            for(int var3 = 0; var3 < var2.length; ++var3) {
               var2[var3] = new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry((Map.Entry)var2[var3]);
            }

            if (var2.length > var1.length) {
               return (Object[])var2;
            } else {
               System.arraycopy(var2, 0, var1, 0, var2.length);
               if (var1.length > var2.length) {
                  var1[var2.length] = null;
               }

               return var1;
            }
         }

         public boolean contains(Object var1) {
            return !(var1 instanceof Map.Entry) ? false : this.c.contains(new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry((Map.Entry)var1));
         }

         public boolean containsAll(Collection<?> var1) {
            Iterator var2 = var1.iterator();

            Object var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = var2.next();
            } while(this.contains(var3));

            return false;
         }

         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Set)) {
               return false;
            } else {
               Set var2 = (Set)var1;
               return var2.size() != this.c.size() ? false : this.containsAll(var2);
            }
         }

         private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {
            private Map.Entry<? extends K, ? extends V> e;

            UnmodifiableEntry(Map.Entry<? extends K, ? extends V> var1) {
               this.e = (Map.Entry)Objects.requireNonNull(var1);
            }

            public K getKey() {
               return this.e.getKey();
            }

            public V getValue() {
               return this.e.getValue();
            }

            public V setValue(V var1) {
               throw new UnsupportedOperationException();
            }

            public int hashCode() {
               return this.e.hashCode();
            }

            public boolean equals(Object var1) {
               if (this == var1) {
                  return true;
               } else if (!(var1 instanceof Map.Entry)) {
                  return false;
               } else {
                  Map.Entry var2 = (Map.Entry)var1;
                  return Collections.eq(this.e.getKey(), var2.getKey()) && Collections.eq(this.e.getValue(), var2.getValue());
               }
            }

            public String toString() {
               return this.e.toString();
            }
         }

         static final class UnmodifiableEntrySetSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
            final Spliterator<Map.Entry<K, V>> s;

            UnmodifiableEntrySetSpliterator(Spliterator<Map.Entry<K, V>> var1) {
               this.s = var1;
            }

            public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
               Objects.requireNonNull(var1);
               return this.s.tryAdvance(Collections.UnmodifiableMap.UnmodifiableEntrySet.entryConsumer(var1));
            }

            public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
               Objects.requireNonNull(var1);
               this.s.forEachRemaining(Collections.UnmodifiableMap.UnmodifiableEntrySet.entryConsumer(var1));
            }

            public Spliterator<Map.Entry<K, V>> trySplit() {
               Spliterator var1 = this.s.trySplit();
               return var1 == null ? null : new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator(var1);
            }

            public long estimateSize() {
               return this.s.estimateSize();
            }

            public long getExactSizeIfKnown() {
               return this.s.getExactSizeIfKnown();
            }

            public int characteristics() {
               return this.s.characteristics();
            }

            public boolean hasCharacteristics(int var1) {
               return this.s.hasCharacteristics(var1);
            }

            public Comparator<? super Map.Entry<K, V>> getComparator() {
               return this.s.getComparator();
            }
         }
      }
   }

   static class UnmodifiableRandomAccessList<E> extends Collections.UnmodifiableList<E> implements RandomAccess {
      private static final long serialVersionUID = -2542308836966382001L;

      UnmodifiableRandomAccessList(List<? extends E> var1) {
         super(var1);
      }

      public List<E> subList(int var1, int var2) {
         return new Collections.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }

      private Object writeReplace() {
         return new Collections.UnmodifiableList(this.list);
      }
   }

   static class UnmodifiableList<E> extends Collections.UnmodifiableCollection<E> implements List<E> {
      private static final long serialVersionUID = -283967356065247728L;
      final List<? extends E> list;

      UnmodifiableList(List<? extends E> var1) {
         super(var1);
         this.list = var1;
      }

      public boolean equals(Object var1) {
         return var1 == this || this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public E get(int var1) {
         return this.list.get(var1);
      }

      public E set(int var1, E var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, E var2) {
         throw new UnsupportedOperationException();
      }

      public E remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends E> var2) {
         throw new UnsupportedOperationException();
      }

      public void replaceAll(UnaryOperator<E> var1) {
         throw new UnsupportedOperationException();
      }

      public void sort(Comparator<? super E> var1) {
         throw new UnsupportedOperationException();
      }

      public ListIterator<E> listIterator() {
         return this.listIterator(0);
      }

      public ListIterator<E> listIterator(final int var1) {
         return new ListIterator<E>() {
            private final ListIterator<? extends E> i;

            {
               this.i = UnmodifiableList.this.list.listIterator(var1);
            }

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public E next() {
               return this.i.next();
            }

            public boolean hasPrevious() {
               return this.i.hasPrevious();
            }

            public E previous() {
               return this.i.previous();
            }

            public int nextIndex() {
               return this.i.nextIndex();
            }

            public int previousIndex() {
               return this.i.previousIndex();
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }

            public void set(E var1x) {
               throw new UnsupportedOperationException();
            }

            public void add(E var1x) {
               throw new UnsupportedOperationException();
            }

            public void forEachRemaining(Consumer<? super E> var1x) {
               this.i.forEachRemaining(var1x);
            }
         };
      }

      public List<E> subList(int var1, int var2) {
         return new Collections.UnmodifiableList(this.list.subList(var1, var2));
      }

      private Object readResolve() {
         return this.list instanceof RandomAccess ? new Collections.UnmodifiableRandomAccessList(this.list) : this;
      }
   }

   static class UnmodifiableNavigableSet<E> extends Collections.UnmodifiableSortedSet<E> implements NavigableSet<E>, Serializable {
      private static final long serialVersionUID = -6027448201786391929L;
      private static final NavigableSet<?> EMPTY_NAVIGABLE_SET = new Collections.UnmodifiableNavigableSet.EmptyNavigableSet();
      private final NavigableSet<E> ns;

      UnmodifiableNavigableSet(NavigableSet<E> var1) {
         super(var1);
         this.ns = var1;
      }

      public E lower(E var1) {
         return this.ns.lower(var1);
      }

      public E floor(E var1) {
         return this.ns.floor(var1);
      }

      public E ceiling(E var1) {
         return this.ns.ceiling(var1);
      }

      public E higher(E var1) {
         return this.ns.higher(var1);
      }

      public E pollFirst() {
         throw new UnsupportedOperationException();
      }

      public E pollLast() {
         throw new UnsupportedOperationException();
      }

      public NavigableSet<E> descendingSet() {
         return new Collections.UnmodifiableNavigableSet(this.ns.descendingSet());
      }

      public Iterator<E> descendingIterator() {
         return this.descendingSet().iterator();
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         return new Collections.UnmodifiableNavigableSet(this.ns.subSet(var1, var2, var3, var4));
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         return new Collections.UnmodifiableNavigableSet(this.ns.headSet(var1, var2));
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         return new Collections.UnmodifiableNavigableSet(this.ns.tailSet(var1, var2));
      }

      private static class EmptyNavigableSet<E> extends Collections.UnmodifiableNavigableSet<E> implements Serializable {
         private static final long serialVersionUID = -6291252904449939134L;

         public EmptyNavigableSet() {
            super(new TreeSet());
         }

         private Object readResolve() {
            return Collections.UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
         }
      }
   }

   static class UnmodifiableSortedSet<E> extends Collections.UnmodifiableSet<E> implements SortedSet<E>, Serializable {
      private static final long serialVersionUID = -4929149591599911165L;
      private final SortedSet<E> ss;

      UnmodifiableSortedSet(SortedSet<E> var1) {
         super(var1);
         this.ss = var1;
      }

      public Comparator<? super E> comparator() {
         return this.ss.comparator();
      }

      public SortedSet<E> subSet(E var1, E var2) {
         return new Collections.UnmodifiableSortedSet(this.ss.subSet(var1, var2));
      }

      public SortedSet<E> headSet(E var1) {
         return new Collections.UnmodifiableSortedSet(this.ss.headSet(var1));
      }

      public SortedSet<E> tailSet(E var1) {
         return new Collections.UnmodifiableSortedSet(this.ss.tailSet(var1));
      }

      public E first() {
         return this.ss.first();
      }

      public E last() {
         return this.ss.last();
      }
   }

   static class UnmodifiableSet<E> extends Collections.UnmodifiableCollection<E> implements Set<E>, Serializable {
      private static final long serialVersionUID = -9215047833775013803L;

      UnmodifiableSet(Set<? extends E> var1) {
         super(var1);
      }

      public boolean equals(Object var1) {
         return var1 == this || this.c.equals(var1);
      }

      public int hashCode() {
         return this.c.hashCode();
      }
   }

   static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
      private static final long serialVersionUID = 1820017752578914078L;
      final Collection<? extends E> c;

      UnmodifiableCollection(Collection<? extends E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.c = var1;
         }
      }

      public int size() {
         return this.c.size();
      }

      public boolean isEmpty() {
         return this.c.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.c.contains(var1);
      }

      public Object[] toArray() {
         return this.c.toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.c.toArray(var1);
      }

      public String toString() {
         return this.c.toString();
      }

      public Iterator<E> iterator() {
         return new Iterator<E>() {
            private final Iterator<? extends E> i;

            {
               this.i = UnmodifiableCollection.this.c.iterator();
            }

            public boolean hasNext() {
               return this.i.hasNext();
            }

            public E next() {
               return this.i.next();
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }

            public void forEachRemaining(Consumer<? super E> var1) {
               this.i.forEachRemaining(var1);
            }
         };
      }

      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsAll(Collection<?> var1) {
         return this.c.containsAll(var1);
      }

      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public void forEach(Consumer<? super E> var1) {
         this.c.forEach(var1);
      }

      public boolean removeIf(Predicate<? super E> var1) {
         throw new UnsupportedOperationException();
      }

      public Spliterator<E> spliterator() {
         return this.c.spliterator();
      }

      public Stream<E> stream() {
         return this.c.stream();
      }

      public Stream<E> parallelStream() {
         return this.c.parallelStream();
      }
   }
}
