package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;

public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, Cloneable {
   private static final int DEFAULT_CAPACITY = 32;
   private static final int MINIMUM_CAPACITY = 4;
   private static final int MAXIMUM_CAPACITY = 536870912;
   transient Object[] table;
   int size;
   transient int modCount;
   static final Object NULL_KEY = new Object();
   private transient Set<Map.Entry<K, V>> entrySet;
   private static final long serialVersionUID = 8188218128353913216L;

   private static Object maskNull(Object var0) {
      return var0 == null ? NULL_KEY : var0;
   }

   static final Object unmaskNull(Object var0) {
      return var0 == NULL_KEY ? null : var0;
   }

   public IdentityHashMap() {
      this.init(32);
   }

   public IdentityHashMap(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("expectedMaxSize is negative: " + var1);
      } else {
         this.init(capacity(var1));
      }
   }

   private static int capacity(int var0) {
      return var0 > 178956970 ? 536870912 : (var0 <= 2 ? 4 : Integer.highestOneBit(var0 + (var0 << 1)));
   }

   private void init(int var1) {
      this.table = new Object[2 * var1];
   }

   public IdentityHashMap(Map<? extends K, ? extends V> var1) {
      this((int)((double)(1 + var1.size()) * 1.1D));
      this.putAll(var1);
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   private static int hash(Object var0, int var1) {
      int var2 = System.identityHashCode(var0);
      return (var2 << 1) - (var2 << 8) & var1 - 1;
   }

   private static int nextKeyIndex(int var0, int var1) {
      return var0 + 2 < var1 ? var0 + 2 : 0;
   }

   public V get(Object var1) {
      Object var2 = maskNull(var1);
      Object[] var3 = this.table;
      int var4 = var3.length;
      int var5 = hash(var2, var4);

      while(true) {
         Object var6 = var3[var5];
         if (var6 == var2) {
            return var3[var5 + 1];
         }

         if (var6 == null) {
            return null;
         }

         var5 = nextKeyIndex(var5, var4);
      }
   }

   public boolean containsKey(Object var1) {
      Object var2 = maskNull(var1);
      Object[] var3 = this.table;
      int var4 = var3.length;
      int var5 = hash(var2, var4);

      while(true) {
         Object var6 = var3[var5];
         if (var6 == var2) {
            return true;
         }

         if (var6 == null) {
            return false;
         }

         var5 = nextKeyIndex(var5, var4);
      }
   }

   public boolean containsValue(Object var1) {
      Object[] var2 = this.table;

      for(int var3 = 1; var3 < var2.length; var3 += 2) {
         if (var2[var3] == var1 && var2[var3 - 1] != null) {
            return true;
         }
      }

      return false;
   }

   private boolean containsMapping(Object var1, Object var2) {
      Object var3 = maskNull(var1);
      Object[] var4 = this.table;
      int var5 = var4.length;
      int var6 = hash(var3, var5);

      while(true) {
         Object var7 = var4[var6];
         if (var7 == var3) {
            return var4[var6 + 1] == var2;
         }

         if (var7 == null) {
            return false;
         }

         var6 = nextKeyIndex(var6, var5);
      }
   }

   public V put(K var1, V var2) {
      Object var3 = maskNull(var1);

      Object[] var4;
      int var5;
      int var6;
      int var9;
      do {
         var4 = this.table;
         var5 = var4.length;

         Object var7;
         for(var6 = hash(var3, var5); (var7 = var4[var6]) != null; var6 = nextKeyIndex(var6, var5)) {
            if (var7 == var3) {
               Object var8 = var4[var6 + 1];
               var4[var6 + 1] = var2;
               return var8;
            }
         }

         var9 = this.size + 1;
      } while(var9 + (var9 << 1) > var5 && this.resize(var5));

      ++this.modCount;
      var4[var6] = var3;
      var4[var6 + 1] = var2;
      this.size = var9;
      return null;
   }

   private boolean resize(int var1) {
      int var2 = var1 * 2;
      Object[] var3 = this.table;
      int var4 = var3.length;
      if (var4 == 1073741824) {
         if (this.size == 536870911) {
            throw new IllegalStateException("Capacity exhausted.");
         } else {
            return false;
         }
      } else if (var4 >= var2) {
         return false;
      } else {
         Object[] var5 = new Object[var2];

         for(int var6 = 0; var6 < var4; var6 += 2) {
            Object var7 = var3[var6];
            if (var7 != null) {
               Object var8 = var3[var6 + 1];
               var3[var6] = null;
               var3[var6 + 1] = null;

               int var9;
               for(var9 = hash(var7, var2); var5[var9] != null; var9 = nextKeyIndex(var9, var2)) {
               }

               var5[var9] = var7;
               var5[var9 + 1] = var8;
            }
         }

         this.table = var5;
         return true;
      }
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      int var2 = var1.size();
      if (var2 != 0) {
         if (var2 > this.size) {
            this.resize(capacity(var2));
         }

         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            this.put(var4.getKey(), var4.getValue());
         }

      }
   }

   public V remove(Object var1) {
      Object var2 = maskNull(var1);
      Object[] var3 = this.table;
      int var4 = var3.length;
      int var5 = hash(var2, var4);

      while(true) {
         Object var6 = var3[var5];
         if (var6 == var2) {
            ++this.modCount;
            --this.size;
            Object var7 = var3[var5 + 1];
            var3[var5 + 1] = null;
            var3[var5] = null;
            this.closeDeletion(var5);
            return var7;
         }

         if (var6 == null) {
            return null;
         }

         var5 = nextKeyIndex(var5, var4);
      }
   }

   private boolean removeMapping(Object var1, Object var2) {
      Object var3 = maskNull(var1);
      Object[] var4 = this.table;
      int var5 = var4.length;
      int var6 = hash(var3, var5);

      while(true) {
         Object var7 = var4[var6];
         if (var7 == var3) {
            if (var4[var6 + 1] != var2) {
               return false;
            } else {
               ++this.modCount;
               --this.size;
               var4[var6] = null;
               var4[var6 + 1] = null;
               this.closeDeletion(var6);
               return true;
            }
         }

         if (var7 == null) {
            return false;
         }

         var6 = nextKeyIndex(var6, var5);
      }
   }

   private void closeDeletion(int var1) {
      Object[] var2 = this.table;
      int var3 = var2.length;

      Object var4;
      for(int var5 = nextKeyIndex(var1, var3); (var4 = var2[var5]) != null; var5 = nextKeyIndex(var5, var3)) {
         int var6 = hash(var4, var3);
         if (var5 < var6 && (var6 <= var1 || var1 <= var5) || var6 <= var1 && var1 <= var5) {
            var2[var1] = var4;
            var2[var1 + 1] = var2[var5 + 1];
            var2[var5] = null;
            var2[var5 + 1] = null;
            var1 = var5;
         }
      }

   }

   public void clear() {
      ++this.modCount;
      Object[] var1 = this.table;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = null;
      }

      this.size = 0;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof IdentityHashMap) {
         IdentityHashMap var6 = (IdentityHashMap)var1;
         if (var6.size() != this.size) {
            return false;
         } else {
            Object[] var3 = var6.table;

            for(int var4 = 0; var4 < var3.length; var4 += 2) {
               Object var5 = var3[var4];
               if (var5 != null && !this.containsMapping(var5, var3[var4 + 1])) {
                  return false;
               }
            }

            return true;
         }
      } else if (var1 instanceof Map) {
         Map var2 = (Map)var1;
         return this.entrySet().equals(var2.entrySet());
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this.table;

      for(int var3 = 0; var3 < var2.length; var3 += 2) {
         Object var4 = var2[var3];
         if (var4 != null) {
            Object var5 = unmaskNull(var4);
            var1 += System.identityHashCode(var5) ^ System.identityHashCode(var2[var3 + 1]);
         }
      }

      return var1;
   }

   public Object clone() {
      try {
         IdentityHashMap var1 = (IdentityHashMap)super.clone();
         var1.entrySet = null;
         var1.table = (Object[])this.table.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public Set<K> keySet() {
      Object var1 = this.keySet;
      if (var1 == null) {
         var1 = new IdentityHashMap.KeySet();
         this.keySet = (Set)var1;
      }

      return (Set)var1;
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new IdentityHashMap.Values();
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new IdentityHashMap.EntrySet());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size);
      Object[] var2 = this.table;

      for(int var3 = 0; var3 < var2.length; var3 += 2) {
         Object var4 = var2[var3];
         if (var4 != null) {
            var1.writeObject(unmaskNull(var4));
            var1.writeObject(var2[var3 + 1]);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new StreamCorruptedException("Illegal mappings count: " + var2);
      } else {
         int var3 = capacity(var2);
         SharedSecrets.getJavaOISAccess().checkArray(var1, Object[].class, var3);
         this.init(var3);

         for(int var4 = 0; var4 < var2; ++var4) {
            Object var5 = var1.readObject();
            Object var6 = var1.readObject();
            this.putForCreate(var5, var6);
         }

      }
   }

   private void putForCreate(K var1, V var2) throws StreamCorruptedException {
      Object var3 = maskNull(var1);
      Object[] var4 = this.table;
      int var5 = var4.length;

      int var6;
      Object var7;
      for(var6 = hash(var3, var5); (var7 = var4[var6]) != null; var6 = nextKeyIndex(var6, var5)) {
         if (var7 == var3) {
            throw new StreamCorruptedException();
         }
      }

      var4[var6] = var3;
      var4[var6 + 1] = var2;
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      Object[] var3 = this.table;

      for(int var4 = 0; var4 < var3.length; var4 += 2) {
         Object var5 = var3[var4];
         if (var5 != null) {
            var1.accept(unmaskNull(var5), var3[var4 + 1]);
         }

         if (this.modCount != var2) {
            throw new ConcurrentModificationException();
         }
      }

   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      Object[] var3 = this.table;

      for(int var4 = 0; var4 < var3.length; var4 += 2) {
         Object var5 = var3[var4];
         if (var5 != null) {
            var3[var4 + 1] = var1.apply(unmaskNull(var5), var3[var4 + 1]);
         }

         if (this.modCount != var2) {
            throw new ConcurrentModificationException();
         }
      }

   }

   static final class EntrySpliterator<K, V> extends IdentityHashMap.IdentityHashMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
      EntrySpliterator(IdentityHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public IdentityHashMap.EntrySpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1 & -2;
         return var2 >= var3 ? null : new IdentityHashMap.EntrySpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2;
            int var3;
            IdentityHashMap var5;
            Object[] var6;
            if ((var5 = this.map) != null && (var6 = var5.table) != null && (var2 = this.index) >= 0 && (this.index = var3 = this.getFence()) <= var6.length) {
               for(; var2 < var3; var2 += 2) {
                  Object var7 = var6[var2];
                  if (var7 != null) {
                     Object var8 = IdentityHashMap.unmaskNull(var7);
                     Object var9 = var6[var2 + 1];
                     var1.accept(new AbstractMap.SimpleImmutableEntry(var8, var9));
                  }
               }

               if (var5.modCount == this.expectedModCount) {
                  return;
               }
            }

            throw new ConcurrentModificationException();
         }
      }

      public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2 = this.map.table;
            int var3 = this.getFence();

            Object var4;
            Object var5;
            do {
               if (this.index >= var3) {
                  return false;
               }

               var4 = var2[this.index];
               var5 = var2[this.index + 1];
               this.index += 2;
            } while(var4 == null);

            Object var6 = IdentityHashMap.unmaskNull(var4);
            var1.accept(new AbstractMap.SimpleImmutableEntry(var6, var5));
            if (this.map.modCount != this.expectedModCount) {
               throw new ConcurrentModificationException();
            } else {
               return true;
            }
         }
      }

      public int characteristics() {
         return (this.fence >= 0 && this.est != this.map.size ? 0 : 64) | 1;
      }
   }

   static final class ValueSpliterator<K, V> extends IdentityHashMap.IdentityHashMapSpliterator<K, V> implements Spliterator<V> {
      ValueSpliterator(IdentityHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public IdentityHashMap.ValueSpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1 & -2;
         return var2 >= var3 ? null : new IdentityHashMap.ValueSpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2;
            int var3;
            IdentityHashMap var5;
            Object[] var6;
            if ((var5 = this.map) != null && (var6 = var5.table) != null && (var2 = this.index) >= 0 && (this.index = var3 = this.getFence()) <= var6.length) {
               for(; var2 < var3; var2 += 2) {
                  if (var6[var2] != null) {
                     Object var7 = var6[var2 + 1];
                     var1.accept(var7);
                  }
               }

               if (var5.modCount == this.expectedModCount) {
                  return;
               }
            }

            throw new ConcurrentModificationException();
         }
      }

      public boolean tryAdvance(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2 = this.map.table;
            int var3 = this.getFence();

            Object var4;
            Object var5;
            do {
               if (this.index >= var3) {
                  return false;
               }

               var4 = var2[this.index];
               var5 = var2[this.index + 1];
               this.index += 2;
            } while(var4 == null);

            var1.accept(var5);
            if (this.map.modCount != this.expectedModCount) {
               throw new ConcurrentModificationException();
            } else {
               return true;
            }
         }
      }

      public int characteristics() {
         return this.fence >= 0 && this.est != this.map.size ? 0 : 64;
      }
   }

   static final class KeySpliterator<K, V> extends IdentityHashMap.IdentityHashMapSpliterator<K, V> implements Spliterator<K> {
      KeySpliterator(IdentityHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public IdentityHashMap.KeySpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1 & -2;
         return var2 >= var3 ? null : new IdentityHashMap.KeySpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2;
            int var3;
            IdentityHashMap var6;
            Object[] var7;
            if ((var6 = this.map) != null && (var7 = var6.table) != null && (var2 = this.index) >= 0 && (this.index = var3 = this.getFence()) <= var7.length) {
               for(; var2 < var3; var2 += 2) {
                  Object var5;
                  if ((var5 = var7[var2]) != null) {
                     var1.accept(IdentityHashMap.unmaskNull(var5));
                  }
               }

               if (var6.modCount == this.expectedModCount) {
                  return;
               }
            }

            throw new ConcurrentModificationException();
         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2 = this.map.table;
            int var3 = this.getFence();

            Object var4;
            do {
               if (this.index >= var3) {
                  return false;
               }

               var4 = var2[this.index];
               this.index += 2;
            } while(var4 == null);

            var1.accept(IdentityHashMap.unmaskNull(var4));
            if (this.map.modCount != this.expectedModCount) {
               throw new ConcurrentModificationException();
            } else {
               return true;
            }
         }
      }

      public int characteristics() {
         return (this.fence >= 0 && this.est != this.map.size ? 0 : 64) | 1;
      }
   }

   static class IdentityHashMapSpliterator<K, V> {
      final IdentityHashMap<K, V> map;
      int index;
      int fence;
      int est;
      int expectedModCount;

      IdentityHashMapSpliterator(IdentityHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         this.map = var1;
         this.index = var2;
         this.fence = var3;
         this.est = var4;
         this.expectedModCount = var5;
      }

      final int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            this.est = this.map.size;
            this.expectedModCount = this.map.modCount;
            var1 = this.fence = this.map.table.length;
         }

         return var1;
      }

      public final long estimateSize() {
         this.getFence();
         return (long)this.est;
      }
   }

   private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return IdentityHashMap.this.new EntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return IdentityHashMap.this.containsMapping(var2.getKey(), var2.getValue());
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return IdentityHashMap.this.removeMapping(var2.getKey(), var2.getValue());
         }
      }

      public int size() {
         return IdentityHashMap.this.size;
      }

      public void clear() {
         IdentityHashMap.this.clear();
      }

      public boolean removeAll(Collection<?> var1) {
         Objects.requireNonNull(var1);
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      public Object[] toArray() {
         return this.toArray(new Object[0]);
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = IdentityHashMap.this.modCount;
         int var3 = this.size();
         if (var1.length < var3) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var3));
         }

         Object[] var4 = IdentityHashMap.this.table;
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; var6 += 2) {
            Object var7;
            if ((var7 = var4[var6]) != null) {
               if (var5 >= var3) {
                  throw new ConcurrentModificationException();
               }

               var1[var5++] = new AbstractMap.SimpleEntry(IdentityHashMap.unmaskNull(var7), var4[var6 + 1]);
            }
         }

         if (var5 >= var3 && var2 == IdentityHashMap.this.modCount) {
            if (var5 < var1.length) {
               var1[var5] = null;
            }

            return var1;
         } else {
            throw new ConcurrentModificationException();
         }
      }

      public Spliterator<Map.Entry<K, V>> spliterator() {
         return new IdentityHashMap.EntrySpliterator(IdentityHashMap.this, 0, -1, 0, 0);
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      public Iterator<V> iterator() {
         return IdentityHashMap.this.new ValueIterator();
      }

      public int size() {
         return IdentityHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return IdentityHashMap.this.containsValue(var1);
      }

      public boolean remove(Object var1) {
         Iterator var2 = this.iterator();

         do {
            if (!var2.hasNext()) {
               return false;
            }
         } while(var2.next() != var1);

         var2.remove();
         return true;
      }

      public void clear() {
         IdentityHashMap.this.clear();
      }

      public Object[] toArray() {
         return this.toArray(new Object[0]);
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = IdentityHashMap.this.modCount;
         int var3 = this.size();
         if (var1.length < var3) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var3));
         }

         Object[] var4 = IdentityHashMap.this.table;
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; var6 += 2) {
            if (var4[var6] != null) {
               if (var5 >= var3) {
                  throw new ConcurrentModificationException();
               }

               var1[var5++] = var4[var6 + 1];
            }
         }

         if (var5 >= var3 && var2 == IdentityHashMap.this.modCount) {
            if (var5 < var1.length) {
               var1[var5] = null;
            }

            return var1;
         } else {
            throw new ConcurrentModificationException();
         }
      }

      public Spliterator<V> spliterator() {
         return new IdentityHashMap.ValueSpliterator(IdentityHashMap.this, 0, -1, 0, 0);
      }

      // $FF: synthetic method
      Values(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public Iterator<K> iterator() {
         return IdentityHashMap.this.new KeyIterator();
      }

      public int size() {
         return IdentityHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return IdentityHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = IdentityHashMap.this.size;
         IdentityHashMap.this.remove(var1);
         return IdentityHashMap.this.size != var2;
      }

      public boolean removeAll(Collection<?> var1) {
         Objects.requireNonNull(var1);
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      public void clear() {
         IdentityHashMap.this.clear();
      }

      public int hashCode() {
         int var1 = 0;

         Object var3;
         for(Iterator var2 = this.iterator(); var2.hasNext(); var1 += System.identityHashCode(var3)) {
            var3 = var2.next();
         }

         return var1;
      }

      public Object[] toArray() {
         return this.toArray(new Object[0]);
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = IdentityHashMap.this.modCount;
         int var3 = this.size();
         if (var1.length < var3) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var3));
         }

         Object[] var4 = IdentityHashMap.this.table;
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; var6 += 2) {
            Object var7;
            if ((var7 = var4[var6]) != null) {
               if (var5 >= var3) {
                  throw new ConcurrentModificationException();
               }

               var1[var5++] = IdentityHashMap.unmaskNull(var7);
            }
         }

         if (var5 >= var3 && var2 == IdentityHashMap.this.modCount) {
            if (var5 < var1.length) {
               var1[var5] = null;
            }

            return var1;
         } else {
            throw new ConcurrentModificationException();
         }
      }

      public Spliterator<K> spliterator() {
         return new IdentityHashMap.KeySpliterator(IdentityHashMap.this, 0, -1, 0, 0);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private class EntryIterator extends IdentityHashMap<K, V>.IdentityHashMapIterator<Map.Entry<K, V>> {
      private IdentityHashMap<K, V>.EntryIterator.Entry lastReturnedEntry;

      private EntryIterator() {
         super(null);
      }

      public Map.Entry<K, V> next() {
         this.lastReturnedEntry = new IdentityHashMap.EntryIterator.Entry(this.nextIndex());
         return this.lastReturnedEntry;
      }

      public void remove() {
         this.lastReturnedIndex = null == this.lastReturnedEntry ? -1 : this.lastReturnedEntry.index;
         super.remove();
         this.lastReturnedEntry.index = this.lastReturnedIndex;
         this.lastReturnedEntry = null;
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }

      private class Entry implements Map.Entry<K, V> {
         private int index;

         private Entry(int var2) {
            this.index = var2;
         }

         public K getKey() {
            this.checkIndexForEntryUse();
            return IdentityHashMap.unmaskNull(EntryIterator.this.traversalTable[this.index]);
         }

         public V getValue() {
            this.checkIndexForEntryUse();
            return EntryIterator.this.traversalTable[this.index + 1];
         }

         public V setValue(V var1) {
            this.checkIndexForEntryUse();
            Object var2 = EntryIterator.this.traversalTable[this.index + 1];
            EntryIterator.this.traversalTable[this.index + 1] = var1;
            if (EntryIterator.this.traversalTable != IdentityHashMap.this.table) {
               IdentityHashMap.this.put(EntryIterator.this.traversalTable[this.index], var1);
            }

            return var2;
         }

         public boolean equals(Object var1) {
            if (this.index < 0) {
               return super.equals(var1);
            } else if (!(var1 instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry var2 = (Map.Entry)var1;
               return var2.getKey() == IdentityHashMap.unmaskNull(EntryIterator.this.traversalTable[this.index]) && var2.getValue() == EntryIterator.this.traversalTable[this.index + 1];
            }
         }

         public int hashCode() {
            return EntryIterator.this.lastReturnedIndex < 0 ? super.hashCode() : System.identityHashCode(IdentityHashMap.unmaskNull(EntryIterator.this.traversalTable[this.index])) ^ System.identityHashCode(EntryIterator.this.traversalTable[this.index + 1]);
         }

         public String toString() {
            return this.index < 0 ? super.toString() : IdentityHashMap.unmaskNull(EntryIterator.this.traversalTable[this.index]) + "=" + EntryIterator.this.traversalTable[this.index + 1];
         }

         private void checkIndexForEntryUse() {
            if (this.index < 0) {
               throw new IllegalStateException("Entry was removed");
            }
         }

         // $FF: synthetic method
         Entry(int var2, Object var3) {
            this(var2);
         }
      }
   }

   private class ValueIterator extends IdentityHashMap<K, V>.IdentityHashMapIterator<V> {
      private ValueIterator() {
         super(null);
      }

      public V next() {
         return this.traversalTable[this.nextIndex() + 1];
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeyIterator extends IdentityHashMap<K, V>.IdentityHashMapIterator<K> {
      private KeyIterator() {
         super(null);
      }

      public K next() {
         return IdentityHashMap.unmaskNull(this.traversalTable[this.nextIndex()]);
      }

      // $FF: synthetic method
      KeyIterator(Object var2) {
         this();
      }
   }

   private abstract class IdentityHashMapIterator<T> implements Iterator<T> {
      int index;
      int expectedModCount;
      int lastReturnedIndex;
      boolean indexValid;
      Object[] traversalTable;

      private IdentityHashMapIterator() {
         this.index = IdentityHashMap.this.size != 0 ? 0 : IdentityHashMap.this.table.length;
         this.expectedModCount = IdentityHashMap.this.modCount;
         this.lastReturnedIndex = -1;
         this.traversalTable = IdentityHashMap.this.table;
      }

      public boolean hasNext() {
         Object[] var1 = this.traversalTable;

         for(int var2 = this.index; var2 < var1.length; var2 += 2) {
            Object var3 = var1[var2];
            if (var3 != null) {
               this.index = var2;
               return this.indexValid = true;
            }
         }

         this.index = var1.length;
         return false;
      }

      protected int nextIndex() {
         if (IdentityHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (!this.indexValid && !this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.indexValid = false;
            this.lastReturnedIndex = this.index;
            this.index += 2;
            return this.lastReturnedIndex;
         }
      }

      public void remove() {
         if (this.lastReturnedIndex == -1) {
            throw new IllegalStateException();
         } else if (IdentityHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.expectedModCount = ++IdentityHashMap.this.modCount;
            int var1 = this.lastReturnedIndex;
            this.lastReturnedIndex = -1;
            this.index = var1;
            this.indexValid = false;
            Object[] var2 = this.traversalTable;
            int var3 = var2.length;
            int var4 = var1;
            Object var5 = var2[var1];
            var2[var1] = null;
            var2[var1 + 1] = null;
            if (var2 != IdentityHashMap.this.table) {
               IdentityHashMap.this.remove(var5);
               this.expectedModCount = IdentityHashMap.this.modCount;
            } else {
               --IdentityHashMap.this.size;

               Object var6;
               for(int var7 = IdentityHashMap.nextKeyIndex(var1, var3); (var6 = var2[var7]) != null; var7 = IdentityHashMap.nextKeyIndex(var7, var3)) {
                  int var8 = IdentityHashMap.hash(var6, var3);
                  if (var7 < var8 && (var8 <= var4 || var4 <= var7) || var8 <= var4 && var4 <= var7) {
                     if (var7 < var1 && var4 >= var1 && this.traversalTable == IdentityHashMap.this.table) {
                        int var9 = var3 - var1;
                        Object[] var10 = new Object[var9];
                        System.arraycopy(var2, var1, var10, 0, var9);
                        this.traversalTable = var10;
                        this.index = 0;
                     }

                     var2[var4] = var6;
                     var2[var4 + 1] = var2[var7 + 1];
                     var2[var7] = null;
                     var2[var7 + 1] = null;
                     var4 = var7;
                  }
               }

            }
         }
      }

      // $FF: synthetic method
      IdentityHashMapIterator(Object var2) {
         this();
      }
   }
}
