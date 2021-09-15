package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, Serializable {
   private final Comparator<? super K> comparator;
   private transient TreeMap.Entry<K, V> root;
   private transient int size = 0;
   private transient int modCount = 0;
   private transient TreeMap<K, V>.EntrySet entrySet;
   private transient TreeMap.KeySet<K> navigableKeySet;
   private transient NavigableMap<K, V> descendingMap;
   private static final Object UNBOUNDED = new Object();
   private static final boolean RED = false;
   private static final boolean BLACK = true;
   private static final long serialVersionUID = 919286545866124006L;

   public TreeMap() {
      this.comparator = null;
   }

   public TreeMap(Comparator<? super K> var1) {
      this.comparator = var1;
   }

   public TreeMap(Map<? extends K, ? extends V> var1) {
      this.comparator = null;
      this.putAll(var1);
   }

   public TreeMap(SortedMap<K, ? extends V> var1) {
      this.comparator = var1.comparator();

      try {
         this.buildFromSorted(var1.size(), var1.entrySet().iterator(), (ObjectInputStream)null, (Object)null);
      } catch (IOException var3) {
      } catch (ClassNotFoundException var4) {
      }

   }

   public int size() {
      return this.size;
   }

   public boolean containsKey(Object var1) {
      return this.getEntry(var1) != null;
   }

   public boolean containsValue(Object var1) {
      for(TreeMap.Entry var2 = this.getFirstEntry(); var2 != null; var2 = successor(var2)) {
         if (valEquals(var1, var2.value)) {
            return true;
         }
      }

      return false;
   }

   public V get(Object var1) {
      TreeMap.Entry var2 = this.getEntry(var1);
      return var2 == null ? null : var2.value;
   }

   public Comparator<? super K> comparator() {
      return this.comparator;
   }

   public K firstKey() {
      return key(this.getFirstEntry());
   }

   public K lastKey() {
      return key(this.getLastEntry());
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      int var2 = var1.size();
      if (this.size == 0 && var2 != 0 && var1 instanceof SortedMap) {
         Comparator var3 = ((SortedMap)var1).comparator();
         if (var3 == this.comparator || var3 != null && var3.equals(this.comparator)) {
            ++this.modCount;

            try {
               this.buildFromSorted(var2, var1.entrySet().iterator(), (ObjectInputStream)null, (Object)null);
            } catch (IOException var5) {
            } catch (ClassNotFoundException var6) {
            }

            return;
         }
      }

      super.putAll(var1);
   }

   final TreeMap.Entry<K, V> getEntry(Object var1) {
      if (this.comparator != null) {
         return this.getEntryUsingComparator(var1);
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         Comparable var2 = (Comparable)var1;
         TreeMap.Entry var3 = this.root;

         while(var3 != null) {
            int var4 = var2.compareTo(var3.key);
            if (var4 < 0) {
               var3 = var3.left;
            } else {
               if (var4 <= 0) {
                  return var3;
               }

               var3 = var3.right;
            }
         }

         return null;
      }
   }

   final TreeMap.Entry<K, V> getEntryUsingComparator(Object var1) {
      Object var2 = var1;
      Comparator var3 = this.comparator;
      if (var3 != null) {
         TreeMap.Entry var4 = this.root;

         while(var4 != null) {
            int var5 = var3.compare(var2, var4.key);
            if (var5 < 0) {
               var4 = var4.left;
            } else {
               if (var5 <= 0) {
                  return var4;
               }

               var4 = var4.right;
            }
         }
      }

      return null;
   }

   final TreeMap.Entry<K, V> getCeilingEntry(K var1) {
      TreeMap.Entry var2 = this.root;

      while(var2 != null) {
         int var3 = this.compare(var1, var2.key);
         if (var3 < 0) {
            if (var2.left == null) {
               return var2;
            }

            var2 = var2.left;
         } else {
            if (var3 <= 0) {
               return var2;
            }

            if (var2.right == null) {
               TreeMap.Entry var4 = var2.parent;

               for(TreeMap.Entry var5 = var2; var4 != null && var5 == var4.right; var4 = var4.parent) {
                  var5 = var4;
               }

               return var4;
            }

            var2 = var2.right;
         }
      }

      return null;
   }

   final TreeMap.Entry<K, V> getFloorEntry(K var1) {
      TreeMap.Entry var2 = this.root;

      while(var2 != null) {
         int var3 = this.compare(var1, var2.key);
         if (var3 > 0) {
            if (var2.right == null) {
               return var2;
            }

            var2 = var2.right;
         } else {
            if (var3 >= 0) {
               return var2;
            }

            if (var2.left == null) {
               TreeMap.Entry var4 = var2.parent;

               for(TreeMap.Entry var5 = var2; var4 != null && var5 == var4.left; var4 = var4.parent) {
                  var5 = var4;
               }

               return var4;
            }

            var2 = var2.left;
         }
      }

      return null;
   }

   final TreeMap.Entry<K, V> getHigherEntry(K var1) {
      TreeMap.Entry var2 = this.root;

      while(var2 != null) {
         int var3 = this.compare(var1, var2.key);
         if (var3 < 0) {
            if (var2.left == null) {
               return var2;
            }

            var2 = var2.left;
         } else {
            if (var2.right == null) {
               TreeMap.Entry var4 = var2.parent;

               for(TreeMap.Entry var5 = var2; var4 != null && var5 == var4.right; var4 = var4.parent) {
                  var5 = var4;
               }

               return var4;
            }

            var2 = var2.right;
         }
      }

      return null;
   }

   final TreeMap.Entry<K, V> getLowerEntry(K var1) {
      TreeMap.Entry var2 = this.root;

      while(var2 != null) {
         int var3 = this.compare(var1, var2.key);
         if (var3 > 0) {
            if (var2.right == null) {
               return var2;
            }

            var2 = var2.right;
         } else {
            if (var2.left == null) {
               TreeMap.Entry var4 = var2.parent;

               for(TreeMap.Entry var5 = var2; var4 != null && var5 == var4.left; var4 = var4.parent) {
                  var5 = var4;
               }

               return var4;
            }

            var2 = var2.left;
         }
      }

      return null;
   }

   public V put(K var1, V var2) {
      TreeMap.Entry var3 = this.root;
      if (var3 == null) {
         this.compare(var1, var1);
         this.root = new TreeMap.Entry(var1, var2, (TreeMap.Entry)null);
         this.size = 1;
         ++this.modCount;
         return null;
      } else {
         Comparator var6 = this.comparator;
         int var4;
         TreeMap.Entry var5;
         if (var6 != null) {
            do {
               var5 = var3;
               var4 = var6.compare(var1, var3.key);
               if (var4 < 0) {
                  var3 = var3.left;
               } else {
                  if (var4 <= 0) {
                     return var3.setValue(var2);
                  }

                  var3 = var3.right;
               }
            } while(var3 != null);
         } else {
            if (var1 == null) {
               throw new NullPointerException();
            }

            Comparable var7 = (Comparable)var1;

            do {
               var5 = var3;
               var4 = var7.compareTo(var3.key);
               if (var4 < 0) {
                  var3 = var3.left;
               } else {
                  if (var4 <= 0) {
                     return var3.setValue(var2);
                  }

                  var3 = var3.right;
               }
            } while(var3 != null);
         }

         TreeMap.Entry var8 = new TreeMap.Entry(var1, var2, var5);
         if (var4 < 0) {
            var5.left = var8;
         } else {
            var5.right = var8;
         }

         this.fixAfterInsertion(var8);
         ++this.size;
         ++this.modCount;
         return null;
      }
   }

   public V remove(Object var1) {
      TreeMap.Entry var2 = this.getEntry(var1);
      if (var2 == null) {
         return null;
      } else {
         Object var3 = var2.value;
         this.deleteEntry(var2);
         return var3;
      }
   }

   public void clear() {
      ++this.modCount;
      this.size = 0;
      this.root = null;
   }

   public Object clone() {
      TreeMap var1;
      try {
         var1 = (TreeMap)super.clone();
      } catch (CloneNotSupportedException var5) {
         throw new InternalError(var5);
      }

      var1.root = null;
      var1.size = 0;
      var1.modCount = 0;
      var1.entrySet = null;
      var1.navigableKeySet = null;
      var1.descendingMap = null;

      try {
         var1.buildFromSorted(this.size, this.entrySet().iterator(), (ObjectInputStream)null, (Object)null);
      } catch (IOException var3) {
      } catch (ClassNotFoundException var4) {
      }

      return var1;
   }

   public Map.Entry<K, V> firstEntry() {
      return exportEntry(this.getFirstEntry());
   }

   public Map.Entry<K, V> lastEntry() {
      return exportEntry(this.getLastEntry());
   }

   public Map.Entry<K, V> pollFirstEntry() {
      TreeMap.Entry var1 = this.getFirstEntry();
      Map.Entry var2 = exportEntry(var1);
      if (var1 != null) {
         this.deleteEntry(var1);
      }

      return var2;
   }

   public Map.Entry<K, V> pollLastEntry() {
      TreeMap.Entry var1 = this.getLastEntry();
      Map.Entry var2 = exportEntry(var1);
      if (var1 != null) {
         this.deleteEntry(var1);
      }

      return var2;
   }

   public Map.Entry<K, V> lowerEntry(K var1) {
      return exportEntry(this.getLowerEntry(var1));
   }

   public K lowerKey(K var1) {
      return keyOrNull(this.getLowerEntry(var1));
   }

   public Map.Entry<K, V> floorEntry(K var1) {
      return exportEntry(this.getFloorEntry(var1));
   }

   public K floorKey(K var1) {
      return keyOrNull(this.getFloorEntry(var1));
   }

   public Map.Entry<K, V> ceilingEntry(K var1) {
      return exportEntry(this.getCeilingEntry(var1));
   }

   public K ceilingKey(K var1) {
      return keyOrNull(this.getCeilingEntry(var1));
   }

   public Map.Entry<K, V> higherEntry(K var1) {
      return exportEntry(this.getHigherEntry(var1));
   }

   public K higherKey(K var1) {
      return keyOrNull(this.getHigherEntry(var1));
   }

   public Set<K> keySet() {
      return this.navigableKeySet();
   }

   public NavigableSet<K> navigableKeySet() {
      TreeMap.KeySet var1 = this.navigableKeySet;
      return var1 != null ? var1 : (this.navigableKeySet = new TreeMap.KeySet(this));
   }

   public NavigableSet<K> descendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new TreeMap.Values();
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      TreeMap.EntrySet var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new TreeMap.EntrySet());
   }

   public NavigableMap<K, V> descendingMap() {
      NavigableMap var1 = this.descendingMap;
      return var1 != null ? var1 : (this.descendingMap = new TreeMap.DescendingSubMap(this, true, (Object)null, true, true, (Object)null, true));
   }

   public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
      return new TreeMap.AscendingSubMap(this, false, var1, var2, false, var3, var4);
   }

   public NavigableMap<K, V> headMap(K var1, boolean var2) {
      return new TreeMap.AscendingSubMap(this, true, (Object)null, true, false, var1, var2);
   }

   public NavigableMap<K, V> tailMap(K var1, boolean var2) {
      return new TreeMap.AscendingSubMap(this, false, var1, var2, true, (Object)null, true);
   }

   public SortedMap<K, V> subMap(K var1, K var2) {
      return this.subMap(var1, true, var2, false);
   }

   public SortedMap<K, V> headMap(K var1) {
      return this.headMap(var1, false);
   }

   public SortedMap<K, V> tailMap(K var1) {
      return this.tailMap(var1, true);
   }

   public boolean replace(K var1, V var2, V var3) {
      TreeMap.Entry var4 = this.getEntry(var1);
      if (var4 != null && Objects.equals(var2, var4.value)) {
         var4.value = var3;
         return true;
      } else {
         return false;
      }
   }

   public V replace(K var1, V var2) {
      TreeMap.Entry var3 = this.getEntry(var1);
      if (var3 != null) {
         Object var4 = var3.value;
         var3.value = var2;
         return var4;
      } else {
         return null;
      }
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;

      for(TreeMap.Entry var3 = this.getFirstEntry(); var3 != null; var3 = successor(var3)) {
         var1.accept(var3.key, var3.value);
         if (var2 != this.modCount) {
            throw new ConcurrentModificationException();
         }
      }

   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;

      for(TreeMap.Entry var3 = this.getFirstEntry(); var3 != null; var3 = successor(var3)) {
         var3.value = var1.apply(var3.key, var3.value);
         if (var2 != this.modCount) {
            throw new ConcurrentModificationException();
         }
      }

   }

   Iterator<K> keyIterator() {
      return new TreeMap.KeyIterator(this.getFirstEntry());
   }

   Iterator<K> descendingKeyIterator() {
      return new TreeMap.DescendingKeyIterator(this.getLastEntry());
   }

   final int compare(Object var1, Object var2) {
      return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
   }

   static final boolean valEquals(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   static <K, V> Map.Entry<K, V> exportEntry(TreeMap.Entry<K, V> var0) {
      return var0 == null ? null : new AbstractMap.SimpleImmutableEntry(var0);
   }

   static <K, V> K keyOrNull(TreeMap.Entry<K, V> var0) {
      return var0 == null ? null : var0.key;
   }

   static <K> K key(TreeMap.Entry<K, ?> var0) {
      if (var0 == null) {
         throw new NoSuchElementException();
      } else {
         return var0.key;
      }
   }

   final TreeMap.Entry<K, V> getFirstEntry() {
      TreeMap.Entry var1 = this.root;
      if (var1 != null) {
         while(var1.left != null) {
            var1 = var1.left;
         }
      }

      return var1;
   }

   final TreeMap.Entry<K, V> getLastEntry() {
      TreeMap.Entry var1 = this.root;
      if (var1 != null) {
         while(var1.right != null) {
            var1 = var1.right;
         }
      }

      return var1;
   }

   static <K, V> TreeMap.Entry<K, V> successor(TreeMap.Entry<K, V> var0) {
      if (var0 == null) {
         return null;
      } else {
         TreeMap.Entry var1;
         if (var0.right != null) {
            for(var1 = var0.right; var1.left != null; var1 = var1.left) {
            }

            return var1;
         } else {
            var1 = var0.parent;

            for(TreeMap.Entry var2 = var0; var1 != null && var2 == var1.right; var1 = var1.parent) {
               var2 = var1;
            }

            return var1;
         }
      }
   }

   static <K, V> TreeMap.Entry<K, V> predecessor(TreeMap.Entry<K, V> var0) {
      if (var0 == null) {
         return null;
      } else {
         TreeMap.Entry var1;
         if (var0.left != null) {
            for(var1 = var0.left; var1.right != null; var1 = var1.right) {
            }

            return var1;
         } else {
            var1 = var0.parent;

            for(TreeMap.Entry var2 = var0; var1 != null && var2 == var1.left; var1 = var1.parent) {
               var2 = var1;
            }

            return var1;
         }
      }
   }

   private static <K, V> boolean colorOf(TreeMap.Entry<K, V> var0) {
      return var0 == null ? true : var0.color;
   }

   private static <K, V> TreeMap.Entry<K, V> parentOf(TreeMap.Entry<K, V> var0) {
      return var0 == null ? null : var0.parent;
   }

   private static <K, V> void setColor(TreeMap.Entry<K, V> var0, boolean var1) {
      if (var0 != null) {
         var0.color = var1;
      }

   }

   private static <K, V> TreeMap.Entry<K, V> leftOf(TreeMap.Entry<K, V> var0) {
      return var0 == null ? null : var0.left;
   }

   private static <K, V> TreeMap.Entry<K, V> rightOf(TreeMap.Entry<K, V> var0) {
      return var0 == null ? null : var0.right;
   }

   private void rotateLeft(TreeMap.Entry<K, V> var1) {
      if (var1 != null) {
         TreeMap.Entry var2 = var1.right;
         var1.right = var2.left;
         if (var2.left != null) {
            var2.left.parent = var1;
         }

         var2.parent = var1.parent;
         if (var1.parent == null) {
            this.root = var2;
         } else if (var1.parent.left == var1) {
            var1.parent.left = var2;
         } else {
            var1.parent.right = var2;
         }

         var2.left = var1;
         var1.parent = var2;
      }

   }

   private void rotateRight(TreeMap.Entry<K, V> var1) {
      if (var1 != null) {
         TreeMap.Entry var2 = var1.left;
         var1.left = var2.right;
         if (var2.right != null) {
            var2.right.parent = var1;
         }

         var2.parent = var1.parent;
         if (var1.parent == null) {
            this.root = var2;
         } else if (var1.parent.right == var1) {
            var1.parent.right = var2;
         } else {
            var1.parent.left = var2;
         }

         var2.right = var1;
         var1.parent = var2;
      }

   }

   private void fixAfterInsertion(TreeMap.Entry<K, V> var1) {
      var1.color = false;

      while(var1 != null && var1 != this.root && !var1.parent.color) {
         TreeMap.Entry var2;
         if (parentOf(var1) == leftOf(parentOf(parentOf(var1)))) {
            var2 = rightOf(parentOf(parentOf(var1)));
            if (!colorOf(var2)) {
               setColor(parentOf(var1), true);
               setColor(var2, true);
               setColor(parentOf(parentOf(var1)), false);
               var1 = parentOf(parentOf(var1));
            } else {
               if (var1 == rightOf(parentOf(var1))) {
                  var1 = parentOf(var1);
                  this.rotateLeft(var1);
               }

               setColor(parentOf(var1), true);
               setColor(parentOf(parentOf(var1)), false);
               this.rotateRight(parentOf(parentOf(var1)));
            }
         } else {
            var2 = leftOf(parentOf(parentOf(var1)));
            if (!colorOf(var2)) {
               setColor(parentOf(var1), true);
               setColor(var2, true);
               setColor(parentOf(parentOf(var1)), false);
               var1 = parentOf(parentOf(var1));
            } else {
               if (var1 == leftOf(parentOf(var1))) {
                  var1 = parentOf(var1);
                  this.rotateRight(var1);
               }

               setColor(parentOf(var1), true);
               setColor(parentOf(parentOf(var1)), false);
               this.rotateLeft(parentOf(parentOf(var1)));
            }
         }
      }

      this.root.color = true;
   }

   private void deleteEntry(TreeMap.Entry<K, V> var1) {
      ++this.modCount;
      --this.size;
      TreeMap.Entry var2;
      if (var1.left != null && var1.right != null) {
         var2 = successor(var1);
         var1.key = var2.key;
         var1.value = var2.value;
         var1 = var2;
      }

      var2 = var1.left != null ? var1.left : var1.right;
      if (var2 != null) {
         var2.parent = var1.parent;
         if (var1.parent == null) {
            this.root = var2;
         } else if (var1 == var1.parent.left) {
            var1.parent.left = var2;
         } else {
            var1.parent.right = var2;
         }

         var1.left = var1.right = var1.parent = null;
         if (var1.color) {
            this.fixAfterDeletion(var2);
         }
      } else if (var1.parent == null) {
         this.root = null;
      } else {
         if (var1.color) {
            this.fixAfterDeletion(var1);
         }

         if (var1.parent != null) {
            if (var1 == var1.parent.left) {
               var1.parent.left = null;
            } else if (var1 == var1.parent.right) {
               var1.parent.right = null;
            }

            var1.parent = null;
         }
      }

   }

   private void fixAfterDeletion(TreeMap.Entry<K, V> var1) {
      while(var1 != this.root && colorOf(var1)) {
         TreeMap.Entry var2;
         if (var1 == leftOf(parentOf(var1))) {
            var2 = rightOf(parentOf(var1));
            if (!colorOf(var2)) {
               setColor(var2, true);
               setColor(parentOf(var1), false);
               this.rotateLeft(parentOf(var1));
               var2 = rightOf(parentOf(var1));
            }

            if (colorOf(leftOf(var2)) && colorOf(rightOf(var2))) {
               setColor(var2, false);
               var1 = parentOf(var1);
            } else {
               if (colorOf(rightOf(var2))) {
                  setColor(leftOf(var2), true);
                  setColor(var2, false);
                  this.rotateRight(var2);
                  var2 = rightOf(parentOf(var1));
               }

               setColor(var2, colorOf(parentOf(var1)));
               setColor(parentOf(var1), true);
               setColor(rightOf(var2), true);
               this.rotateLeft(parentOf(var1));
               var1 = this.root;
            }
         } else {
            var2 = leftOf(parentOf(var1));
            if (!colorOf(var2)) {
               setColor(var2, true);
               setColor(parentOf(var1), false);
               this.rotateRight(parentOf(var1));
               var2 = leftOf(parentOf(var1));
            }

            if (colorOf(rightOf(var2)) && colorOf(leftOf(var2))) {
               setColor(var2, false);
               var1 = parentOf(var1);
            } else {
               if (colorOf(leftOf(var2))) {
                  setColor(rightOf(var2), true);
                  setColor(var2, false);
                  this.rotateLeft(var2);
                  var2 = leftOf(parentOf(var1));
               }

               setColor(var2, colorOf(parentOf(var1)));
               setColor(parentOf(var1), true);
               setColor(leftOf(var2), true);
               this.rotateRight(parentOf(var1));
               var1 = this.root;
            }
         }
      }

      setColor(var1, true);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size);
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      this.buildFromSorted(var2, (Iterator)null, var1, (Object)null);
   }

   void readTreeSet(int var1, ObjectInputStream var2, V var3) throws IOException, ClassNotFoundException {
      this.buildFromSorted(var1, (Iterator)null, var2, var3);
   }

   void addAllForTreeSet(SortedSet<? extends K> var1, V var2) {
      try {
         this.buildFromSorted(var1.size(), var1.iterator(), (ObjectInputStream)null, var2);
      } catch (IOException var4) {
      } catch (ClassNotFoundException var5) {
      }

   }

   private void buildFromSorted(int var1, Iterator<?> var2, ObjectInputStream var3, V var4) throws IOException, ClassNotFoundException {
      this.size = var1;
      this.root = this.buildFromSorted(0, 0, var1 - 1, computeRedLevel(var1), var2, var3, var4);
   }

   private final TreeMap.Entry<K, V> buildFromSorted(int var1, int var2, int var3, int var4, Iterator<?> var5, ObjectInputStream var6, V var7) throws IOException, ClassNotFoundException {
      if (var3 < var2) {
         return null;
      } else {
         int var8 = var2 + var3 >>> 1;
         TreeMap.Entry var9 = null;
         if (var2 < var8) {
            var9 = this.buildFromSorted(var1 + 1, var2, var8 - 1, var4, var5, var6, var7);
         }

         Object var10;
         Object var11;
         if (var5 != null) {
            if (var7 == null) {
               Map.Entry var12 = (Map.Entry)var5.next();
               var10 = var12.getKey();
               var11 = var12.getValue();
            } else {
               var10 = var5.next();
               var11 = var7;
            }
         } else {
            var10 = var6.readObject();
            var11 = var7 != null ? var7 : var6.readObject();
         }

         TreeMap.Entry var14 = new TreeMap.Entry(var10, var11, (TreeMap.Entry)null);
         if (var1 == var4) {
            var14.color = false;
         }

         if (var9 != null) {
            var14.left = var9;
            var9.parent = var14;
         }

         if (var8 < var3) {
            TreeMap.Entry var13 = this.buildFromSorted(var1 + 1, var8 + 1, var3, var4, var5, var6, var7);
            var14.right = var13;
            var13.parent = var14;
         }

         return var14;
      }
   }

   private static int computeRedLevel(int var0) {
      int var1 = 0;

      for(int var2 = var0 - 1; var2 >= 0; var2 = var2 / 2 - 1) {
         ++var1;
      }

      return var1;
   }

   static <K> Spliterator<K> keySpliteratorFor(NavigableMap<K, ?> var0) {
      if (var0 instanceof TreeMap) {
         TreeMap var5 = (TreeMap)var0;
         return var5.keySpliterator();
      } else {
         if (var0 instanceof TreeMap.DescendingSubMap) {
            TreeMap.DescendingSubMap var1 = (TreeMap.DescendingSubMap)var0;
            TreeMap var2 = var1.m;
            if (var1 == var2.descendingMap) {
               return var2.descendingKeySpliterator();
            }
         }

         TreeMap.NavigableSubMap var4 = (TreeMap.NavigableSubMap)var0;
         return var4.keySpliterator();
      }
   }

   final Spliterator<K> keySpliterator() {
      return new TreeMap.KeySpliterator(this, (TreeMap.Entry)null, (TreeMap.Entry)null, 0, -1, 0);
   }

   final Spliterator<K> descendingKeySpliterator() {
      return new TreeMap.DescendingKeySpliterator(this, (TreeMap.Entry)null, (TreeMap.Entry)null, 0, -2, 0);
   }

   static final class EntrySpliterator<K, V> extends TreeMap.TreeMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
      EntrySpliterator(TreeMap<K, V> var1, TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public TreeMap.EntrySpliterator<K, V> trySplit() {
         if (this.est < 0) {
            this.getEstimate();
         }

         int var1 = this.side;
         TreeMap.Entry var2 = this.current;
         TreeMap.Entry var3 = this.fence;
         TreeMap.Entry var4 = var2 != null && var2 != var3 ? (var1 == 0 ? this.tree.root : (var1 > 0 ? var2.right : (var1 < 0 && var3 != null ? var3.left : null))) : null;
         if (var4 != null && var4 != var2 && var4 != var3 && this.tree.compare(var2.key, var4.key) < 0) {
            this.side = 1;
            return new TreeMap.EntrySpliterator(this.tree, var2, this.current = var4, -1, this.est >>>= 1, this.expectedModCount);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2 = this.fence;
            TreeMap.Entry var3;
            if ((var3 = this.current) != null && var3 != var2) {
               this.current = var2;

               TreeMap.Entry var4;
               do {
                  var1.accept(var3);
                  TreeMap.Entry var5;
                  if ((var4 = var3.right) != null) {
                     while((var5 = var4.left) != null) {
                        var4 = var5;
                     }
                  } else {
                     while((var4 = var3.parent) != null && var3 == var4.right) {
                        var3 = var4;
                     }
                  }

                  var3 = var4;
               } while(var4 != null && var4 != var2);

               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2;
            if ((var2 = this.current) != null && var2 != this.fence) {
               this.current = TreeMap.successor(var2);
               var1.accept(var2);
               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public int characteristics() {
         return (this.side == 0 ? 64 : 0) | 1 | 4 | 16;
      }

      public Comparator<Map.Entry<K, V>> getComparator() {
         return this.tree.comparator != null ? Map.Entry.comparingByKey(this.tree.comparator) : (Comparator)((Serializable)((var0x, var1x) -> {
            Comparable var2 = (Comparable)var0x.getKey();
            return var2.compareTo(var1x.getKey());
         }));
      }

      // $FF: synthetic method
      private static Object $deserializeLambda$(SerializedLambda var0) {
         String var1 = var0.getImplMethodName();
         byte var2 = -1;
         switch(var1.hashCode()) {
         case 1620203517:
            if (var1.equals("lambda$getComparator$d5a01062$1")) {
               var2 = 0;
            }
         default:
            switch(var2) {
            case 0:
               if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/TreeMap$EntrySpliterator") && var0.getImplMethodSignature().equals("(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
                  return (var0x, var1x) -> {
                     Comparable var2 = (Comparable)var0x.getKey();
                     return var2.compareTo(var1x.getKey());
                  };
               }
            default:
               throw new IllegalArgumentException("Invalid lambda deserialization");
            }
         }
      }
   }

   static final class ValueSpliterator<K, V> extends TreeMap.TreeMapSpliterator<K, V> implements Spliterator<V> {
      ValueSpliterator(TreeMap<K, V> var1, TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public TreeMap.ValueSpliterator<K, V> trySplit() {
         if (this.est < 0) {
            this.getEstimate();
         }

         int var1 = this.side;
         TreeMap.Entry var2 = this.current;
         TreeMap.Entry var3 = this.fence;
         TreeMap.Entry var4 = var2 != null && var2 != var3 ? (var1 == 0 ? this.tree.root : (var1 > 0 ? var2.right : (var1 < 0 && var3 != null ? var3.left : null))) : null;
         if (var4 != null && var4 != var2 && var4 != var3 && this.tree.compare(var2.key, var4.key) < 0) {
            this.side = 1;
            return new TreeMap.ValueSpliterator(this.tree, var2, this.current = var4, -1, this.est >>>= 1, this.expectedModCount);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2 = this.fence;
            TreeMap.Entry var3;
            if ((var3 = this.current) != null && var3 != var2) {
               this.current = var2;

               TreeMap.Entry var4;
               do {
                  var1.accept(var3.value);
                  TreeMap.Entry var5;
                  if ((var4 = var3.right) != null) {
                     while((var5 = var4.left) != null) {
                        var4 = var5;
                     }
                  } else {
                     while((var4 = var3.parent) != null && var3 == var4.right) {
                        var3 = var4;
                     }
                  }

                  var3 = var4;
               } while(var4 != null && var4 != var2);

               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2;
            if ((var2 = this.current) != null && var2 != this.fence) {
               this.current = TreeMap.successor(var2);
               var1.accept(var2.value);
               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public int characteristics() {
         return (this.side == 0 ? 64 : 0) | 16;
      }
   }

   static final class DescendingKeySpliterator<K, V> extends TreeMap.TreeMapSpliterator<K, V> implements Spliterator<K> {
      DescendingKeySpliterator(TreeMap<K, V> var1, TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public TreeMap.DescendingKeySpliterator<K, V> trySplit() {
         if (this.est < 0) {
            this.getEstimate();
         }

         int var1 = this.side;
         TreeMap.Entry var2 = this.current;
         TreeMap.Entry var3 = this.fence;
         TreeMap.Entry var4 = var2 != null && var2 != var3 ? (var1 == 0 ? this.tree.root : (var1 < 0 ? var2.left : (var1 > 0 && var3 != null ? var3.right : null))) : null;
         if (var4 != null && var4 != var2 && var4 != var3 && this.tree.compare(var2.key, var4.key) > 0) {
            this.side = 1;
            return new TreeMap.DescendingKeySpliterator(this.tree, var2, this.current = var4, -1, this.est >>>= 1, this.expectedModCount);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2 = this.fence;
            TreeMap.Entry var3;
            if ((var3 = this.current) != null && var3 != var2) {
               this.current = var2;

               TreeMap.Entry var4;
               do {
                  var1.accept(var3.key);
                  TreeMap.Entry var5;
                  if ((var4 = var3.left) != null) {
                     while((var5 = var4.right) != null) {
                        var4 = var5;
                     }
                  } else {
                     while((var4 = var3.parent) != null && var3 == var4.left) {
                        var3 = var4;
                     }
                  }

                  var3 = var4;
               } while(var4 != null && var4 != var2);

               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2;
            if ((var2 = this.current) != null && var2 != this.fence) {
               this.current = TreeMap.predecessor(var2);
               var1.accept(var2.key);
               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public int characteristics() {
         return (this.side == 0 ? 64 : 0) | 1 | 16;
      }
   }

   static final class KeySpliterator<K, V> extends TreeMap.TreeMapSpliterator<K, V> implements Spliterator<K> {
      KeySpliterator(TreeMap<K, V> var1, TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public TreeMap.KeySpliterator<K, V> trySplit() {
         if (this.est < 0) {
            this.getEstimate();
         }

         int var1 = this.side;
         TreeMap.Entry var2 = this.current;
         TreeMap.Entry var3 = this.fence;
         TreeMap.Entry var4 = var2 != null && var2 != var3 ? (var1 == 0 ? this.tree.root : (var1 > 0 ? var2.right : (var1 < 0 && var3 != null ? var3.left : null))) : null;
         if (var4 != null && var4 != var2 && var4 != var3 && this.tree.compare(var2.key, var4.key) < 0) {
            this.side = 1;
            return new TreeMap.KeySpliterator(this.tree, var2, this.current = var4, -1, this.est >>>= 1, this.expectedModCount);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2 = this.fence;
            TreeMap.Entry var3;
            if ((var3 = this.current) != null && var3 != var2) {
               this.current = var2;

               TreeMap.Entry var4;
               do {
                  var1.accept(var3.key);
                  TreeMap.Entry var5;
                  if ((var4 = var3.right) != null) {
                     while((var5 = var4.left) != null) {
                        var4 = var5;
                     }
                  } else {
                     while((var4 = var3.parent) != null && var3 == var4.right) {
                        var3 = var4;
                     }
                  }

                  var3 = var4;
               } while(var4 != null && var4 != var2);

               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.est < 0) {
               this.getEstimate();
            }

            TreeMap.Entry var2;
            if ((var2 = this.current) != null && var2 != this.fence) {
               this.current = TreeMap.successor(var2);
               var1.accept(var2.key);
               if (this.tree.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public int characteristics() {
         return (this.side == 0 ? 64 : 0) | 1 | 4 | 16;
      }

      public final Comparator<? super K> getComparator() {
         return this.tree.comparator;
      }
   }

   static class TreeMapSpliterator<K, V> {
      final TreeMap<K, V> tree;
      TreeMap.Entry<K, V> current;
      TreeMap.Entry<K, V> fence;
      int side;
      int est;
      int expectedModCount;

      TreeMapSpliterator(TreeMap<K, V> var1, TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3, int var4, int var5, int var6) {
         this.tree = var1;
         this.current = var2;
         this.fence = var3;
         this.side = var4;
         this.est = var5;
         this.expectedModCount = var6;
      }

      final int getEstimate() {
         int var1;
         if ((var1 = this.est) < 0) {
            TreeMap var2;
            if ((var2 = this.tree) != null) {
               this.current = var1 == -1 ? var2.getFirstEntry() : var2.getLastEntry();
               var1 = this.est = var2.size;
               this.expectedModCount = var2.modCount;
            } else {
               var1 = this.est = 0;
            }
         }

         return var1;
      }

      public final long estimateSize() {
         return (long)this.getEstimate();
      }
   }

   static final class Entry<K, V> implements Map.Entry<K, V> {
      K key;
      V value;
      TreeMap.Entry<K, V> left;
      TreeMap.Entry<K, V> right;
      TreeMap.Entry<K, V> parent;
      boolean color = true;

      Entry(K var1, V var2, TreeMap.Entry<K, V> var3) {
         this.key = var1;
         this.value = var2;
         this.parent = var3;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return TreeMap.valEquals(this.key, var2.getKey()) && TreeMap.valEquals(this.value, var2.getValue());
         }
      }

      public int hashCode() {
         int var1 = this.key == null ? 0 : this.key.hashCode();
         int var2 = this.value == null ? 0 : this.value.hashCode();
         return var1 ^ var2;
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   private class SubMap extends AbstractMap<K, V> implements SortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -6520786458950516097L;
      private boolean fromStart = false;
      private boolean toEnd = false;
      private K fromKey;
      private K toKey;

      private Object readResolve() {
         return new TreeMap.AscendingSubMap(TreeMap.this, this.fromStart, this.fromKey, true, this.toEnd, this.toKey, false);
      }

      public Set<Map.Entry<K, V>> entrySet() {
         throw new InternalError();
      }

      public K lastKey() {
         throw new InternalError();
      }

      public K firstKey() {
         throw new InternalError();
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         throw new InternalError();
      }

      public SortedMap<K, V> headMap(K var1) {
         throw new InternalError();
      }

      public SortedMap<K, V> tailMap(K var1) {
         throw new InternalError();
      }

      public Comparator<? super K> comparator() {
         throw new InternalError();
      }
   }

   static final class DescendingSubMap<K, V> extends TreeMap.NavigableSubMap<K, V> {
      private static final long serialVersionUID = 912986545866120460L;
      private final Comparator<? super K> reverseComparator;

      DescendingSubMap(TreeMap<K, V> var1, boolean var2, K var3, boolean var4, boolean var5, K var6, boolean var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
         this.reverseComparator = Collections.reverseOrder(this.m.comparator);
      }

      public Comparator<? super K> comparator() {
         return this.reverseComparator;
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("fromKey out of range");
         } else if (!this.inRange(var3, var4)) {
            throw new IllegalArgumentException("toKey out of range");
         } else {
            return new TreeMap.DescendingSubMap(this.m, false, var3, var4, false, var1, var2);
         }
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("toKey out of range");
         } else {
            return new TreeMap.DescendingSubMap(this.m, false, var1, var2, this.toEnd, this.hi, this.hiInclusive);
         }
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("fromKey out of range");
         } else {
            return new TreeMap.DescendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, false, var1, var2);
         }
      }

      public NavigableMap<K, V> descendingMap() {
         NavigableMap var1 = this.descendingMapView;
         return var1 != null ? var1 : (this.descendingMapView = new TreeMap.AscendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, this.toEnd, this.hi, this.hiInclusive));
      }

      Iterator<K> keyIterator() {
         return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this.absHighest(), this.absLowFence());
      }

      Spliterator<K> keySpliterator() {
         return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this.absHighest(), this.absLowFence());
      }

      Iterator<K> descendingKeyIterator() {
         return new TreeMap.NavigableSubMap.SubMapKeyIterator(this.absLowest(), this.absHighFence());
      }

      public Set<Map.Entry<K, V>> entrySet() {
         TreeMap.NavigableSubMap.EntrySetView var1 = this.entrySetView;
         return var1 != null ? var1 : (this.entrySetView = new TreeMap.DescendingSubMap.DescendingEntrySetView());
      }

      TreeMap.Entry<K, V> subLowest() {
         return this.absHighest();
      }

      TreeMap.Entry<K, V> subHighest() {
         return this.absLowest();
      }

      TreeMap.Entry<K, V> subCeiling(K var1) {
         return this.absFloor(var1);
      }

      TreeMap.Entry<K, V> subHigher(K var1) {
         return this.absLower(var1);
      }

      TreeMap.Entry<K, V> subFloor(K var1) {
         return this.absCeiling(var1);
      }

      TreeMap.Entry<K, V> subLower(K var1) {
         return this.absHigher(var1);
      }

      final class DescendingEntrySetView extends TreeMap.NavigableSubMap<K, V>.EntrySetView {
         DescendingEntrySetView() {
            super();
         }

         public Iterator<Map.Entry<K, V>> iterator() {
            return DescendingSubMap.this.new DescendingSubMapEntryIterator(DescendingSubMap.this.absHighest(), DescendingSubMap.this.absLowFence());
         }
      }
   }

   static final class AscendingSubMap<K, V> extends TreeMap.NavigableSubMap<K, V> {
      private static final long serialVersionUID = 912986545866124060L;

      AscendingSubMap(TreeMap<K, V> var1, boolean var2, K var3, boolean var4, boolean var5, K var6, boolean var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public Comparator<? super K> comparator() {
         return this.m.comparator();
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("fromKey out of range");
         } else if (!this.inRange(var3, var4)) {
            throw new IllegalArgumentException("toKey out of range");
         } else {
            return new TreeMap.AscendingSubMap(this.m, false, var1, var2, false, var3, var4);
         }
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("toKey out of range");
         } else {
            return new TreeMap.AscendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, false, var1, var2);
         }
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         if (!this.inRange(var1, var2)) {
            throw new IllegalArgumentException("fromKey out of range");
         } else {
            return new TreeMap.AscendingSubMap(this.m, false, var1, var2, this.toEnd, this.hi, this.hiInclusive);
         }
      }

      public NavigableMap<K, V> descendingMap() {
         NavigableMap var1 = this.descendingMapView;
         return var1 != null ? var1 : (this.descendingMapView = new TreeMap.DescendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, this.toEnd, this.hi, this.hiInclusive));
      }

      Iterator<K> keyIterator() {
         return new TreeMap.NavigableSubMap.SubMapKeyIterator(this.absLowest(), this.absHighFence());
      }

      Spliterator<K> keySpliterator() {
         return new TreeMap.NavigableSubMap.SubMapKeyIterator(this.absLowest(), this.absHighFence());
      }

      Iterator<K> descendingKeyIterator() {
         return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this.absHighest(), this.absLowFence());
      }

      public Set<Map.Entry<K, V>> entrySet() {
         TreeMap.NavigableSubMap.EntrySetView var1 = this.entrySetView;
         return var1 != null ? var1 : (this.entrySetView = new TreeMap.AscendingSubMap.AscendingEntrySetView());
      }

      TreeMap.Entry<K, V> subLowest() {
         return this.absLowest();
      }

      TreeMap.Entry<K, V> subHighest() {
         return this.absHighest();
      }

      TreeMap.Entry<K, V> subCeiling(K var1) {
         return this.absCeiling(var1);
      }

      TreeMap.Entry<K, V> subHigher(K var1) {
         return this.absHigher(var1);
      }

      TreeMap.Entry<K, V> subFloor(K var1) {
         return this.absFloor(var1);
      }

      TreeMap.Entry<K, V> subLower(K var1) {
         return this.absLower(var1);
      }

      final class AscendingEntrySetView extends TreeMap.NavigableSubMap<K, V>.EntrySetView {
         AscendingEntrySetView() {
            super();
         }

         public Iterator<Map.Entry<K, V>> iterator() {
            return AscendingSubMap.this.new SubMapEntryIterator(AscendingSubMap.this.absLowest(), AscendingSubMap.this.absHighFence());
         }
      }
   }

   abstract static class NavigableSubMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Serializable {
      private static final long serialVersionUID = -2102997345730753016L;
      final TreeMap<K, V> m;
      final K lo;
      final K hi;
      final boolean fromStart;
      final boolean toEnd;
      final boolean loInclusive;
      final boolean hiInclusive;
      transient NavigableMap<K, V> descendingMapView;
      transient TreeMap.NavigableSubMap<K, V>.EntrySetView entrySetView;
      transient TreeMap.KeySet<K> navigableKeySetView;

      NavigableSubMap(TreeMap<K, V> var1, boolean var2, K var3, boolean var4, boolean var5, K var6, boolean var7) {
         if (!var2 && !var5) {
            if (var1.compare(var3, var6) > 0) {
               throw new IllegalArgumentException("fromKey > toKey");
            }
         } else {
            if (!var2) {
               var1.compare(var3, var3);
            }

            if (!var5) {
               var1.compare(var6, var6);
            }
         }

         this.m = var1;
         this.fromStart = var2;
         this.lo = var3;
         this.loInclusive = var4;
         this.toEnd = var5;
         this.hi = var6;
         this.hiInclusive = var7;
      }

      final boolean tooLow(Object var1) {
         if (!this.fromStart) {
            int var2 = this.m.compare(var1, this.lo);
            if (var2 < 0 || var2 == 0 && !this.loInclusive) {
               return true;
            }
         }

         return false;
      }

      final boolean tooHigh(Object var1) {
         if (!this.toEnd) {
            int var2 = this.m.compare(var1, this.hi);
            if (var2 > 0 || var2 == 0 && !this.hiInclusive) {
               return true;
            }
         }

         return false;
      }

      final boolean inRange(Object var1) {
         return !this.tooLow(var1) && !this.tooHigh(var1);
      }

      final boolean inClosedRange(Object var1) {
         return (this.fromStart || this.m.compare(var1, this.lo) >= 0) && (this.toEnd || this.m.compare(this.hi, var1) >= 0);
      }

      final boolean inRange(Object var1, boolean var2) {
         return var2 ? this.inRange(var1) : this.inClosedRange(var1);
      }

      final TreeMap.Entry<K, V> absLowest() {
         TreeMap.Entry var1 = this.fromStart ? this.m.getFirstEntry() : (this.loInclusive ? this.m.getCeilingEntry(this.lo) : this.m.getHigherEntry(this.lo));
         return var1 != null && !this.tooHigh(var1.key) ? var1 : null;
      }

      final TreeMap.Entry<K, V> absHighest() {
         TreeMap.Entry var1 = this.toEnd ? this.m.getLastEntry() : (this.hiInclusive ? this.m.getFloorEntry(this.hi) : this.m.getLowerEntry(this.hi));
         return var1 != null && !this.tooLow(var1.key) ? var1 : null;
      }

      final TreeMap.Entry<K, V> absCeiling(K var1) {
         if (this.tooLow(var1)) {
            return this.absLowest();
         } else {
            TreeMap.Entry var2 = this.m.getCeilingEntry(var1);
            return var2 != null && !this.tooHigh(var2.key) ? var2 : null;
         }
      }

      final TreeMap.Entry<K, V> absHigher(K var1) {
         if (this.tooLow(var1)) {
            return this.absLowest();
         } else {
            TreeMap.Entry var2 = this.m.getHigherEntry(var1);
            return var2 != null && !this.tooHigh(var2.key) ? var2 : null;
         }
      }

      final TreeMap.Entry<K, V> absFloor(K var1) {
         if (this.tooHigh(var1)) {
            return this.absHighest();
         } else {
            TreeMap.Entry var2 = this.m.getFloorEntry(var1);
            return var2 != null && !this.tooLow(var2.key) ? var2 : null;
         }
      }

      final TreeMap.Entry<K, V> absLower(K var1) {
         if (this.tooHigh(var1)) {
            return this.absHighest();
         } else {
            TreeMap.Entry var2 = this.m.getLowerEntry(var1);
            return var2 != null && !this.tooLow(var2.key) ? var2 : null;
         }
      }

      final TreeMap.Entry<K, V> absHighFence() {
         return this.toEnd ? null : (this.hiInclusive ? this.m.getHigherEntry(this.hi) : this.m.getCeilingEntry(this.hi));
      }

      final TreeMap.Entry<K, V> absLowFence() {
         return this.fromStart ? null : (this.loInclusive ? this.m.getLowerEntry(this.lo) : this.m.getFloorEntry(this.lo));
      }

      abstract TreeMap.Entry<K, V> subLowest();

      abstract TreeMap.Entry<K, V> subHighest();

      abstract TreeMap.Entry<K, V> subCeiling(K var1);

      abstract TreeMap.Entry<K, V> subHigher(K var1);

      abstract TreeMap.Entry<K, V> subFloor(K var1);

      abstract TreeMap.Entry<K, V> subLower(K var1);

      abstract Iterator<K> keyIterator();

      abstract Spliterator<K> keySpliterator();

      abstract Iterator<K> descendingKeyIterator();

      public boolean isEmpty() {
         return this.fromStart && this.toEnd ? this.m.isEmpty() : this.entrySet().isEmpty();
      }

      public int size() {
         return this.fromStart && this.toEnd ? this.m.size() : this.entrySet().size();
      }

      public final boolean containsKey(Object var1) {
         return this.inRange(var1) && this.m.containsKey(var1);
      }

      public final V put(K var1, V var2) {
         if (!this.inRange(var1)) {
            throw new IllegalArgumentException("key out of range");
         } else {
            return this.m.put(var1, var2);
         }
      }

      public final V get(Object var1) {
         return !this.inRange(var1) ? null : this.m.get(var1);
      }

      public final V remove(Object var1) {
         return !this.inRange(var1) ? null : this.m.remove(var1);
      }

      public final Map.Entry<K, V> ceilingEntry(K var1) {
         return TreeMap.exportEntry(this.subCeiling(var1));
      }

      public final K ceilingKey(K var1) {
         return TreeMap.keyOrNull(this.subCeiling(var1));
      }

      public final Map.Entry<K, V> higherEntry(K var1) {
         return TreeMap.exportEntry(this.subHigher(var1));
      }

      public final K higherKey(K var1) {
         return TreeMap.keyOrNull(this.subHigher(var1));
      }

      public final Map.Entry<K, V> floorEntry(K var1) {
         return TreeMap.exportEntry(this.subFloor(var1));
      }

      public final K floorKey(K var1) {
         return TreeMap.keyOrNull(this.subFloor(var1));
      }

      public final Map.Entry<K, V> lowerEntry(K var1) {
         return TreeMap.exportEntry(this.subLower(var1));
      }

      public final K lowerKey(K var1) {
         return TreeMap.keyOrNull(this.subLower(var1));
      }

      public final K firstKey() {
         return TreeMap.key(this.subLowest());
      }

      public final K lastKey() {
         return TreeMap.key(this.subHighest());
      }

      public final Map.Entry<K, V> firstEntry() {
         return TreeMap.exportEntry(this.subLowest());
      }

      public final Map.Entry<K, V> lastEntry() {
         return TreeMap.exportEntry(this.subHighest());
      }

      public final Map.Entry<K, V> pollFirstEntry() {
         TreeMap.Entry var1 = this.subLowest();
         Map.Entry var2 = TreeMap.exportEntry(var1);
         if (var1 != null) {
            this.m.deleteEntry(var1);
         }

         return var2;
      }

      public final Map.Entry<K, V> pollLastEntry() {
         TreeMap.Entry var1 = this.subHighest();
         Map.Entry var2 = TreeMap.exportEntry(var1);
         if (var1 != null) {
            this.m.deleteEntry(var1);
         }

         return var2;
      }

      public final NavigableSet<K> navigableKeySet() {
         TreeMap.KeySet var1 = this.navigableKeySetView;
         return var1 != null ? var1 : (this.navigableKeySetView = new TreeMap.KeySet(this));
      }

      public final Set<K> keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet<K> descendingKeySet() {
         return this.descendingMap().navigableKeySet();
      }

      public final SortedMap<K, V> subMap(K var1, K var2) {
         return this.subMap(var1, true, var2, false);
      }

      public final SortedMap<K, V> headMap(K var1) {
         return this.headMap(var1, false);
      }

      public final SortedMap<K, V> tailMap(K var1) {
         return this.tailMap(var1, true);
      }

      final class DescendingSubMapKeyIterator extends TreeMap.NavigableSubMap<K, V>.SubMapIterator<K> implements Spliterator<K> {
         DescendingSubMapKeyIterator(TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3) {
            super(var2, var3);
         }

         public K next() {
            return this.prevEntry().key;
         }

         public void remove() {
            this.removeDescending();
         }

         public Spliterator<K> trySplit() {
            return null;
         }

         public void forEachRemaining(Consumer<? super K> var1) {
            while(this.hasNext()) {
               var1.accept(this.next());
            }

         }

         public boolean tryAdvance(Consumer<? super K> var1) {
            if (this.hasNext()) {
               var1.accept(this.next());
               return true;
            } else {
               return false;
            }
         }

         public long estimateSize() {
            return Long.MAX_VALUE;
         }

         public int characteristics() {
            return 17;
         }
      }

      final class SubMapKeyIterator extends TreeMap.NavigableSubMap<K, V>.SubMapIterator<K> implements Spliterator<K> {
         SubMapKeyIterator(TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3) {
            super(var2, var3);
         }

         public K next() {
            return this.nextEntry().key;
         }

         public void remove() {
            this.removeAscending();
         }

         public Spliterator<K> trySplit() {
            return null;
         }

         public void forEachRemaining(Consumer<? super K> var1) {
            while(this.hasNext()) {
               var1.accept(this.next());
            }

         }

         public boolean tryAdvance(Consumer<? super K> var1) {
            if (this.hasNext()) {
               var1.accept(this.next());
               return true;
            } else {
               return false;
            }
         }

         public long estimateSize() {
            return Long.MAX_VALUE;
         }

         public int characteristics() {
            return 21;
         }

         public final Comparator<? super K> getComparator() {
            return NavigableSubMap.this.comparator();
         }
      }

      final class DescendingSubMapEntryIterator extends TreeMap.NavigableSubMap<K, V>.SubMapIterator<Map.Entry<K, V>> {
         DescendingSubMapEntryIterator(TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3) {
            super(var2, var3);
         }

         public Map.Entry<K, V> next() {
            return this.prevEntry();
         }

         public void remove() {
            this.removeDescending();
         }
      }

      final class SubMapEntryIterator extends TreeMap.NavigableSubMap<K, V>.SubMapIterator<Map.Entry<K, V>> {
         SubMapEntryIterator(TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3) {
            super(var2, var3);
         }

         public Map.Entry<K, V> next() {
            return this.nextEntry();
         }

         public void remove() {
            this.removeAscending();
         }
      }

      abstract class SubMapIterator<T> implements Iterator<T> {
         TreeMap.Entry<K, V> lastReturned;
         TreeMap.Entry<K, V> next;
         final Object fenceKey;
         int expectedModCount;

         SubMapIterator(TreeMap.Entry<K, V> var2, TreeMap.Entry<K, V> var3) {
            this.expectedModCount = NavigableSubMap.this.m.modCount;
            this.lastReturned = null;
            this.next = var2;
            this.fenceKey = var3 == null ? TreeMap.UNBOUNDED : var3.key;
         }

         public final boolean hasNext() {
            return this.next != null && this.next.key != this.fenceKey;
         }

         final TreeMap.Entry<K, V> nextEntry() {
            TreeMap.Entry var1 = this.next;
            if (var1 != null && var1.key != this.fenceKey) {
               if (NavigableSubMap.this.m.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  this.next = TreeMap.successor(var1);
                  this.lastReturned = var1;
                  return var1;
               }
            } else {
               throw new NoSuchElementException();
            }
         }

         final TreeMap.Entry<K, V> prevEntry() {
            TreeMap.Entry var1 = this.next;
            if (var1 != null && var1.key != this.fenceKey) {
               if (NavigableSubMap.this.m.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  this.next = TreeMap.predecessor(var1);
                  this.lastReturned = var1;
                  return var1;
               }
            } else {
               throw new NoSuchElementException();
            }
         }

         final void removeAscending() {
            if (this.lastReturned == null) {
               throw new IllegalStateException();
            } else if (NavigableSubMap.this.m.modCount != this.expectedModCount) {
               throw new ConcurrentModificationException();
            } else {
               if (this.lastReturned.left != null && this.lastReturned.right != null) {
                  this.next = this.lastReturned;
               }

               NavigableSubMap.this.m.deleteEntry(this.lastReturned);
               this.lastReturned = null;
               this.expectedModCount = NavigableSubMap.this.m.modCount;
            }
         }

         final void removeDescending() {
            if (this.lastReturned == null) {
               throw new IllegalStateException();
            } else if (NavigableSubMap.this.m.modCount != this.expectedModCount) {
               throw new ConcurrentModificationException();
            } else {
               NavigableSubMap.this.m.deleteEntry(this.lastReturned);
               this.lastReturned = null;
               this.expectedModCount = NavigableSubMap.this.m.modCount;
            }
         }
      }

      abstract class EntrySetView extends AbstractSet<Map.Entry<K, V>> {
         private transient int size = -1;
         private transient int sizeModCount;

         public int size() {
            if (NavigableSubMap.this.fromStart && NavigableSubMap.this.toEnd) {
               return NavigableSubMap.this.m.size();
            } else {
               if (this.size == -1 || this.sizeModCount != NavigableSubMap.this.m.modCount) {
                  this.sizeModCount = NavigableSubMap.this.m.modCount;
                  this.size = 0;
                  Iterator var1 = this.iterator();

                  while(var1.hasNext()) {
                     ++this.size;
                     var1.next();
                  }
               }

               return this.size;
            }
         }

         public boolean isEmpty() {
            TreeMap.Entry var1 = NavigableSubMap.this.absLowest();
            return var1 == null || NavigableSubMap.this.tooHigh(var1.key);
         }

         public boolean contains(Object var1) {
            if (!(var1 instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry var2 = (Map.Entry)var1;
               Object var3 = var2.getKey();
               if (!NavigableSubMap.this.inRange(var3)) {
                  return false;
               } else {
                  TreeMap.Entry var4 = NavigableSubMap.this.m.getEntry(var3);
                  return var4 != null && TreeMap.valEquals(var4.getValue(), var2.getValue());
               }
            }
         }

         public boolean remove(Object var1) {
            if (!(var1 instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry var2 = (Map.Entry)var1;
               Object var3 = var2.getKey();
               if (!NavigableSubMap.this.inRange(var3)) {
                  return false;
               } else {
                  TreeMap.Entry var4 = NavigableSubMap.this.m.getEntry(var3);
                  if (var4 != null && TreeMap.valEquals(var4.getValue(), var2.getValue())) {
                     NavigableSubMap.this.m.deleteEntry(var4);
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   final class DescendingKeyIterator extends TreeMap<K, V>.PrivateEntryIterator<K> {
      DescendingKeyIterator(TreeMap.Entry<K, V> var2) {
         super(var2);
      }

      public K next() {
         return this.prevEntry().key;
      }

      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else if (TreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            TreeMap.this.deleteEntry(this.lastReturned);
            this.lastReturned = null;
            this.expectedModCount = TreeMap.this.modCount;
         }
      }
   }

   final class KeyIterator extends TreeMap<K, V>.PrivateEntryIterator<K> {
      KeyIterator(TreeMap.Entry<K, V> var2) {
         super(var2);
      }

      public K next() {
         return this.nextEntry().key;
      }
   }

   final class ValueIterator extends TreeMap<K, V>.PrivateEntryIterator<V> {
      ValueIterator(TreeMap.Entry<K, V> var2) {
         super(var2);
      }

      public V next() {
         return this.nextEntry().value;
      }
   }

   final class EntryIterator extends TreeMap<K, V>.PrivateEntryIterator<Map.Entry<K, V>> {
      EntryIterator(TreeMap.Entry<K, V> var2) {
         super(var2);
      }

      public Map.Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   abstract class PrivateEntryIterator<T> implements Iterator<T> {
      TreeMap.Entry<K, V> next;
      TreeMap.Entry<K, V> lastReturned;
      int expectedModCount;

      PrivateEntryIterator(TreeMap.Entry<K, V> var2) {
         this.expectedModCount = TreeMap.this.modCount;
         this.lastReturned = null;
         this.next = var2;
      }

      public final boolean hasNext() {
         return this.next != null;
      }

      final TreeMap.Entry<K, V> nextEntry() {
         TreeMap.Entry var1 = this.next;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else if (TreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.next = TreeMap.successor(var1);
            this.lastReturned = var1;
            return var1;
         }
      }

      final TreeMap.Entry<K, V> prevEntry() {
         TreeMap.Entry var1 = this.next;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else if (TreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.next = TreeMap.predecessor(var1);
            this.lastReturned = var1;
            return var1;
         }
      }

      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else if (TreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            if (this.lastReturned.left != null && this.lastReturned.right != null) {
               this.next = this.lastReturned;
            }

            TreeMap.this.deleteEntry(this.lastReturned);
            this.expectedModCount = TreeMap.this.modCount;
            this.lastReturned = null;
         }
      }
   }

   static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
      private final NavigableMap<E, ?> m;

      KeySet(NavigableMap<E, ?> var1) {
         this.m = var1;
      }

      public Iterator<E> iterator() {
         return this.m instanceof TreeMap ? ((TreeMap)this.m).keyIterator() : ((TreeMap.NavigableSubMap)this.m).keyIterator();
      }

      public Iterator<E> descendingIterator() {
         return this.m instanceof TreeMap ? ((TreeMap)this.m).descendingKeyIterator() : ((TreeMap.NavigableSubMap)this.m).descendingKeyIterator();
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

      public void clear() {
         this.m.clear();
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

      public E first() {
         return this.m.firstKey();
      }

      public E last() {
         return this.m.lastKey();
      }

      public Comparator<? super E> comparator() {
         return this.m.comparator();
      }

      public E pollFirst() {
         Map.Entry var1 = this.m.pollFirstEntry();
         return var1 == null ? null : var1.getKey();
      }

      public E pollLast() {
         Map.Entry var1 = this.m.pollLastEntry();
         return var1 == null ? null : var1.getKey();
      }

      public boolean remove(Object var1) {
         int var2 = this.size();
         this.m.remove(var1);
         return this.size() != var2;
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         return new TreeMap.KeySet(this.m.subMap(var1, var2, var3, var4));
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         return new TreeMap.KeySet(this.m.headMap(var1, var2));
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         return new TreeMap.KeySet(this.m.tailMap(var1, var2));
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

      public NavigableSet<E> descendingSet() {
         return new TreeMap.KeySet(this.m.descendingMap());
      }

      public Spliterator<E> spliterator() {
         return TreeMap.keySpliteratorFor(this.m);
      }
   }

   class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      public Iterator<Map.Entry<K, V>> iterator() {
         return TreeMap.this.new EntryIterator(TreeMap.this.getFirstEntry());
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getValue();
            TreeMap.Entry var4 = TreeMap.this.getEntry(var2.getKey());
            return var4 != null && TreeMap.valEquals(var4.getValue(), var3);
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getValue();
            TreeMap.Entry var4 = TreeMap.this.getEntry(var2.getKey());
            if (var4 != null && TreeMap.valEquals(var4.getValue(), var3)) {
               TreeMap.this.deleteEntry(var4);
               return true;
            } else {
               return false;
            }
         }
      }

      public int size() {
         return TreeMap.this.size();
      }

      public void clear() {
         TreeMap.this.clear();
      }

      public Spliterator<Map.Entry<K, V>> spliterator() {
         return new TreeMap.EntrySpliterator(TreeMap.this, (TreeMap.Entry)null, (TreeMap.Entry)null, 0, -1, 0);
      }
   }

   class Values extends AbstractCollection<V> {
      public Iterator<V> iterator() {
         return TreeMap.this.new ValueIterator(TreeMap.this.getFirstEntry());
      }

      public int size() {
         return TreeMap.this.size();
      }

      public boolean contains(Object var1) {
         return TreeMap.this.containsValue(var1);
      }

      public boolean remove(Object var1) {
         for(TreeMap.Entry var2 = TreeMap.this.getFirstEntry(); var2 != null; var2 = TreeMap.successor(var2)) {
            if (TreeMap.valEquals(var2.getValue(), var1)) {
               TreeMap.this.deleteEntry(var2);
               return true;
            }
         }

         return false;
      }

      public void clear() {
         TreeMap.this.clear();
      }

      public Spliterator<V> spliterator() {
         return new TreeMap.ValueSpliterator(TreeMap.this, (TreeMap.Entry)null, (TreeMap.Entry)null, 0, -1, 0);
      }
   }
}
