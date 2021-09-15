package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LinkedHashMap<K, V> extends HashMap<K, V> implements Map<K, V> {
   private static final long serialVersionUID = 3801124242820219131L;
   transient LinkedHashMap.Entry<K, V> head;
   transient LinkedHashMap.Entry<K, V> tail;
   final boolean accessOrder;

   private void linkNodeLast(LinkedHashMap.Entry<K, V> var1) {
      LinkedHashMap.Entry var2 = this.tail;
      this.tail = var1;
      if (var2 == null) {
         this.head = var1;
      } else {
         var1.before = var2;
         var2.after = var1;
      }

   }

   private void transferLinks(LinkedHashMap.Entry<K, V> var1, LinkedHashMap.Entry<K, V> var2) {
      LinkedHashMap.Entry var3 = var2.before = var1.before;
      LinkedHashMap.Entry var4 = var2.after = var1.after;
      if (var3 == null) {
         this.head = var2;
      } else {
         var3.after = var2;
      }

      if (var4 == null) {
         this.tail = var2;
      } else {
         var4.before = var2;
      }

   }

   void reinitialize() {
      super.reinitialize();
      this.head = this.tail = null;
   }

   HashMap.Node<K, V> newNode(int var1, K var2, V var3, HashMap.Node<K, V> var4) {
      LinkedHashMap.Entry var5 = new LinkedHashMap.Entry(var1, var2, var3, var4);
      this.linkNodeLast(var5);
      return var5;
   }

   HashMap.Node<K, V> replacementNode(HashMap.Node<K, V> var1, HashMap.Node<K, V> var2) {
      LinkedHashMap.Entry var3 = (LinkedHashMap.Entry)var1;
      LinkedHashMap.Entry var4 = new LinkedHashMap.Entry(var3.hash, var3.key, var3.value, var2);
      this.transferLinks(var3, var4);
      return var4;
   }

   HashMap.TreeNode<K, V> newTreeNode(int var1, K var2, V var3, HashMap.Node<K, V> var4) {
      HashMap.TreeNode var5 = new HashMap.TreeNode(var1, var2, var3, var4);
      this.linkNodeLast(var5);
      return var5;
   }

   HashMap.TreeNode<K, V> replacementTreeNode(HashMap.Node<K, V> var1, HashMap.Node<K, V> var2) {
      LinkedHashMap.Entry var3 = (LinkedHashMap.Entry)var1;
      HashMap.TreeNode var4 = new HashMap.TreeNode(var3.hash, var3.key, var3.value, var2);
      this.transferLinks(var3, var4);
      return var4;
   }

   void afterNodeRemoval(HashMap.Node<K, V> var1) {
      LinkedHashMap.Entry var2 = (LinkedHashMap.Entry)var1;
      LinkedHashMap.Entry var3 = var2.before;
      LinkedHashMap.Entry var4 = var2.after;
      var2.before = var2.after = null;
      if (var3 == null) {
         this.head = var4;
      } else {
         var3.after = var4;
      }

      if (var4 == null) {
         this.tail = var3;
      } else {
         var4.before = var3;
      }

   }

   void afterNodeInsertion(boolean var1) {
      LinkedHashMap.Entry var2;
      if (var1 && (var2 = this.head) != null && this.removeEldestEntry(var2)) {
         Object var3 = var2.key;
         this.removeNode(hash(var3), var3, (Object)null, false, true);
      }

   }

   void afterNodeAccess(HashMap.Node<K, V> var1) {
      LinkedHashMap.Entry var2;
      if (this.accessOrder && (var2 = this.tail) != var1) {
         LinkedHashMap.Entry var3 = (LinkedHashMap.Entry)var1;
         LinkedHashMap.Entry var4 = var3.before;
         LinkedHashMap.Entry var5 = var3.after;
         var3.after = null;
         if (var4 == null) {
            this.head = var5;
         } else {
            var4.after = var5;
         }

         if (var5 != null) {
            var5.before = var4;
         } else {
            var2 = var4;
         }

         if (var2 == null) {
            this.head = var3;
         } else {
            var3.before = var2;
            var2.after = var3;
         }

         this.tail = var3;
         ++this.modCount;
      }

   }

   void internalWriteEntries(ObjectOutputStream var1) throws IOException {
      for(LinkedHashMap.Entry var2 = this.head; var2 != null; var2 = var2.after) {
         var1.writeObject(var2.key);
         var1.writeObject(var2.value);
      }

   }

   public LinkedHashMap(int var1, float var2) {
      super(var1, var2);
      this.accessOrder = false;
   }

   public LinkedHashMap(int var1) {
      super(var1);
      this.accessOrder = false;
   }

   public LinkedHashMap() {
      this.accessOrder = false;
   }

   public LinkedHashMap(Map<? extends K, ? extends V> var1) {
      this.accessOrder = false;
      this.putMapEntries(var1, false);
   }

   public LinkedHashMap(int var1, float var2, boolean var3) {
      super(var1, var2);
      this.accessOrder = var3;
   }

   public boolean containsValue(Object var1) {
      for(LinkedHashMap.Entry var2 = this.head; var2 != null; var2 = var2.after) {
         Object var3 = var2.value;
         if (var3 == var1 || var1 != null && var1.equals(var3)) {
            return true;
         }
      }

      return false;
   }

   public V get(Object var1) {
      HashMap.Node var2;
      if ((var2 = this.getNode(hash(var1), var1)) == null) {
         return null;
      } else {
         if (this.accessOrder) {
            this.afterNodeAccess(var2);
         }

         return var2.value;
      }
   }

   public V getOrDefault(Object var1, V var2) {
      HashMap.Node var3;
      if ((var3 = this.getNode(hash(var1), var1)) == null) {
         return var2;
      } else {
         if (this.accessOrder) {
            this.afterNodeAccess(var3);
         }

         return var3.value;
      }
   }

   public void clear() {
      super.clear();
      this.head = this.tail = null;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> var1) {
      return false;
   }

   public Set<K> keySet() {
      Object var1 = this.keySet;
      if (var1 == null) {
         var1 = new LinkedHashMap.LinkedKeySet();
         this.keySet = (Set)var1;
      }

      return (Set)var1;
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new LinkedHashMap.LinkedValues();
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set var1;
      return (var1 = this.entrySet) == null ? (this.entrySet = new LinkedHashMap.LinkedEntrySet()) : var1;
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var2 = this.modCount;

         for(LinkedHashMap.Entry var3 = this.head; var3 != null; var3 = var3.after) {
            var1.accept(var3.key, var3.value);
         }

         if (this.modCount != var2) {
            throw new ConcurrentModificationException();
         }
      }
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var2 = this.modCount;

         for(LinkedHashMap.Entry var3 = this.head; var3 != null; var3 = var3.after) {
            var3.value = var1.apply(var3.key, var3.value);
         }

         if (this.modCount != var2) {
            throw new ConcurrentModificationException();
         }
      }
   }

   final class LinkedEntryIterator extends LinkedHashMap<K, V>.LinkedHashIterator implements Iterator<Map.Entry<K, V>> {
      LinkedEntryIterator() {
         super();
      }

      public final Map.Entry<K, V> next() {
         return this.nextNode();
      }
   }

   final class LinkedValueIterator extends LinkedHashMap<K, V>.LinkedHashIterator implements Iterator<V> {
      LinkedValueIterator() {
         super();
      }

      public final V next() {
         return this.nextNode().value;
      }
   }

   final class LinkedKeyIterator extends LinkedHashMap<K, V>.LinkedHashIterator implements Iterator<K> {
      LinkedKeyIterator() {
         super();
      }

      public final K next() {
         return this.nextNode().getKey();
      }
   }

   abstract class LinkedHashIterator {
      LinkedHashMap.Entry<K, V> next;
      LinkedHashMap.Entry<K, V> current;
      int expectedModCount;

      LinkedHashIterator() {
         this.next = LinkedHashMap.this.head;
         this.expectedModCount = LinkedHashMap.this.modCount;
         this.current = null;
      }

      public final boolean hasNext() {
         return this.next != null;
      }

      final LinkedHashMap.Entry<K, V> nextNode() {
         LinkedHashMap.Entry var1 = this.next;
         if (LinkedHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            this.current = var1;
            this.next = var1.after;
            return var1;
         }
      }

      public final void remove() {
         LinkedHashMap.Entry var1 = this.current;
         if (var1 == null) {
            throw new IllegalStateException();
         } else if (LinkedHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.current = null;
            Object var2 = var1.key;
            LinkedHashMap.this.removeNode(HashMap.hash(var2), var2, (Object)null, false, false);
            this.expectedModCount = LinkedHashMap.this.modCount;
         }
      }
   }

   final class LinkedEntrySet extends AbstractSet<Map.Entry<K, V>> {
      public final int size() {
         return LinkedHashMap.this.size;
      }

      public final void clear() {
         LinkedHashMap.this.clear();
      }

      public final Iterator<Map.Entry<K, V>> iterator() {
         return LinkedHashMap.this.new LinkedEntryIterator();
      }

      public final boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getKey();
            HashMap.Node var4 = LinkedHashMap.this.getNode(HashMap.hash(var3), var3);
            return var4 != null && var4.equals(var2);
         }
      }

      public final boolean remove(Object var1) {
         if (var1 instanceof Map.Entry) {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            return LinkedHashMap.this.removeNode(HashMap.hash(var3), var3, var4, true, true) != null;
         } else {
            return false;
         }
      }

      public final Spliterator<Map.Entry<K, V>> spliterator() {
         return Spliterators.spliterator((Collection)this, 81);
      }

      public final void forEach(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2 = LinkedHashMap.this.modCount;

            for(LinkedHashMap.Entry var3 = LinkedHashMap.this.head; var3 != null; var3 = var3.after) {
               var1.accept(var3);
            }

            if (LinkedHashMap.this.modCount != var2) {
               throw new ConcurrentModificationException();
            }
         }
      }
   }

   final class LinkedValues extends AbstractCollection<V> {
      public final int size() {
         return LinkedHashMap.this.size;
      }

      public final void clear() {
         LinkedHashMap.this.clear();
      }

      public final Iterator<V> iterator() {
         return LinkedHashMap.this.new LinkedValueIterator();
      }

      public final boolean contains(Object var1) {
         return LinkedHashMap.this.containsValue(var1);
      }

      public final Spliterator<V> spliterator() {
         return Spliterators.spliterator((Collection)this, 80);
      }

      public final void forEach(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2 = LinkedHashMap.this.modCount;

            for(LinkedHashMap.Entry var3 = LinkedHashMap.this.head; var3 != null; var3 = var3.after) {
               var1.accept(var3.value);
            }

            if (LinkedHashMap.this.modCount != var2) {
               throw new ConcurrentModificationException();
            }
         }
      }
   }

   final class LinkedKeySet extends AbstractSet<K> {
      public final int size() {
         return LinkedHashMap.this.size;
      }

      public final void clear() {
         LinkedHashMap.this.clear();
      }

      public final Iterator<K> iterator() {
         return LinkedHashMap.this.new LinkedKeyIterator();
      }

      public final boolean contains(Object var1) {
         return LinkedHashMap.this.containsKey(var1);
      }

      public final boolean remove(Object var1) {
         return LinkedHashMap.this.removeNode(HashMap.hash(var1), var1, (Object)null, false, true) != null;
      }

      public final Spliterator<K> spliterator() {
         return Spliterators.spliterator((Collection)this, 81);
      }

      public final void forEach(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2 = LinkedHashMap.this.modCount;

            for(LinkedHashMap.Entry var3 = LinkedHashMap.this.head; var3 != null; var3 = var3.after) {
               var1.accept(var3.key);
            }

            if (LinkedHashMap.this.modCount != var2) {
               throw new ConcurrentModificationException();
            }
         }
      }
   }

   static class Entry<K, V> extends HashMap.Node<K, V> {
      LinkedHashMap.Entry<K, V> before;
      LinkedHashMap.Entry<K, V> after;

      Entry(int var1, K var2, V var3, HashMap.Node<K, V> var4) {
         super(var1, var2, var3, var4);
      }
   }
}
