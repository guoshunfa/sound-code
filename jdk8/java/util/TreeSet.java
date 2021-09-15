package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
   private transient NavigableMap<E, Object> m;
   private static final Object PRESENT = new Object();
   private static final long serialVersionUID = -2479143000061671589L;

   TreeSet(NavigableMap<E, Object> var1) {
      this.m = var1;
   }

   public TreeSet() {
      this((NavigableMap)(new TreeMap()));
   }

   public TreeSet(Comparator<? super E> var1) {
      this((NavigableMap)(new TreeMap(var1)));
   }

   public TreeSet(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   public TreeSet(SortedSet<E> var1) {
      this(var1.comparator());
      this.addAll(var1);
   }

   public Iterator<E> iterator() {
      return this.m.navigableKeySet().iterator();
   }

   public Iterator<E> descendingIterator() {
      return this.m.descendingKeySet().iterator();
   }

   public NavigableSet<E> descendingSet() {
      return new TreeSet(this.m.descendingMap());
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
      return this.m.put(var1, PRESENT) == null;
   }

   public boolean remove(Object var1) {
      return this.m.remove(var1) == PRESENT;
   }

   public void clear() {
      this.m.clear();
   }

   public boolean addAll(Collection<? extends E> var1) {
      if (this.m.size() == 0 && var1.size() > 0 && var1 instanceof SortedSet && this.m instanceof TreeMap) {
         SortedSet var2 = (SortedSet)var1;
         TreeMap var3 = (TreeMap)this.m;
         Comparator var4 = var2.comparator();
         Comparator var5 = var3.comparator();
         if (var4 == var5 || var4 != null && var4.equals(var5)) {
            var3.addAllForTreeSet(var2, PRESENT);
            return true;
         }
      }

      return super.addAll(var1);
   }

   public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
      return new TreeSet(this.m.subMap(var1, var2, var3, var4));
   }

   public NavigableSet<E> headSet(E var1, boolean var2) {
      return new TreeSet(this.m.headMap(var1, var2));
   }

   public NavigableSet<E> tailSet(E var1, boolean var2) {
      return new TreeSet(this.m.tailMap(var1, var2));
   }

   public SortedSet<E> subSet(E var1, E var2) {
      return this.subSet(var1, true, var2, false);
   }

   public SortedSet<E> headSet(E var1) {
      return this.headSet(var1, false);
   }

   public SortedSet<E> tailSet(E var1) {
      return this.tailSet(var1, true);
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

   public Object clone() {
      TreeSet var1;
      try {
         var1 = (TreeSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }

      var1.m = new TreeMap(this.m);
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.m.comparator());
      var1.writeInt(this.m.size());
      Iterator var2 = this.m.keySet().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.writeObject(var3);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      TreeMap var3 = new TreeMap(var2);
      this.m = var3;
      int var4 = var1.readInt();
      var3.readTreeSet(var4, var1, PRESENT);
   }

   public Spliterator<E> spliterator() {
      return TreeMap.keySpliteratorFor(this.m);
   }
}
