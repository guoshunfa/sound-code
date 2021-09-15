package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import sun.misc.Unsafe;

public class ConcurrentSkipListSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
   private static final long serialVersionUID = -2479143111061671589L;
   private final ConcurrentNavigableMap<E, Object> m;
   private static final Unsafe UNSAFE;
   private static final long mapOffset;

   public ConcurrentSkipListSet() {
      this.m = new ConcurrentSkipListMap();
   }

   public ConcurrentSkipListSet(Comparator<? super E> var1) {
      this.m = new ConcurrentSkipListMap(var1);
   }

   public ConcurrentSkipListSet(Collection<? extends E> var1) {
      this.m = new ConcurrentSkipListMap();
      this.addAll(var1);
   }

   public ConcurrentSkipListSet(SortedSet<E> var1) {
      this.m = new ConcurrentSkipListMap(var1.comparator());
      this.addAll(var1);
   }

   ConcurrentSkipListSet(ConcurrentNavigableMap<E, Object> var1) {
      this.m = var1;
   }

   public ConcurrentSkipListSet<E> clone() {
      try {
         ConcurrentSkipListSet var1 = (ConcurrentSkipListSet)super.clone();
         var1.setMap(new ConcurrentSkipListMap(this.m));
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
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

   public boolean add(E var1) {
      return this.m.putIfAbsent(var1, Boolean.TRUE) == null;
   }

   public boolean remove(Object var1) {
      return this.m.remove(var1, Boolean.TRUE);
   }

   public void clear() {
      this.m.clear();
   }

   public Iterator<E> iterator() {
      return this.m.navigableKeySet().iterator();
   }

   public Iterator<E> descendingIterator() {
      return this.m.descendingKeySet().iterator();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Collection var2 = (Collection)var1;

         try {
            return this.containsAll(var2) && var2.containsAll(this);
         } catch (ClassCastException var4) {
            return false;
         } catch (NullPointerException var5) {
            return false;
         }
      }
   }

   public boolean removeAll(Collection<?> var1) {
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (this.remove(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   public E lower(E var1) {
      return this.m.lowerKey(var1);
   }

   public E floor(E var1) {
      return this.m.floorKey(var1);
   }

   public E ceiling(E var1) {
      return this.m.ceilingKey(var1);
   }

   public E higher(E var1) {
      return this.m.higherKey(var1);
   }

   public E pollFirst() {
      Map.Entry var1 = this.m.pollFirstEntry();
      return var1 == null ? null : var1.getKey();
   }

   public E pollLast() {
      Map.Entry var1 = this.m.pollLastEntry();
      return var1 == null ? null : var1.getKey();
   }

   public Comparator<? super E> comparator() {
      return this.m.comparator();
   }

   public E first() {
      return this.m.firstKey();
   }

   public E last() {
      return this.m.lastKey();
   }

   public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
      return new ConcurrentSkipListSet(this.m.subMap(var1, var2, var3, var4));
   }

   public NavigableSet<E> headSet(E var1, boolean var2) {
      return new ConcurrentSkipListSet(this.m.headMap(var1, var2));
   }

   public NavigableSet<E> tailSet(E var1, boolean var2) {
      return new ConcurrentSkipListSet(this.m.tailMap(var1, var2));
   }

   public NavigableSet<E> subSet(E var1, E var2) {
      return this.subSet(var1, true, var2, false);
   }

   public NavigableSet<E> headSet(E var1) {
      return this.headSet(var1, false);
   }

   public NavigableSet<E> tailSet(E var1) {
      return this.tailSet(var1, true);
   }

   public NavigableSet<E> descendingSet() {
      return new ConcurrentSkipListSet(this.m.descendingMap());
   }

   public Spliterator<E> spliterator() {
      return (Spliterator)(this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).keySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).keyIterator());
   }

   private void setMap(ConcurrentNavigableMap<E, Object> var1) {
      UNSAFE.putObjectVolatile(this, mapOffset, var1);
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = ConcurrentSkipListSet.class;
         mapOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("m"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
