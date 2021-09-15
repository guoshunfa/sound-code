package java.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
   WeakHashMap.Entry<K, V>[] table;
   private int size;
   private int threshold;
   private final float loadFactor;
   private final ReferenceQueue<Object> queue;
   int modCount;
   private static final Object NULL_KEY = new Object();
   private transient Set<Map.Entry<K, V>> entrySet;

   private WeakHashMap.Entry<K, V>[] newTable(int var1) {
      return (WeakHashMap.Entry[])(new WeakHashMap.Entry[var1]);
   }

   public WeakHashMap(int var1, float var2) {
      this.queue = new ReferenceQueue();
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Initial Capacity: " + var1);
      } else {
         if (var1 > 1073741824) {
            var1 = 1073741824;
         }

         if (var2 > 0.0F && !Float.isNaN(var2)) {
            int var3;
            for(var3 = 1; var3 < var1; var3 <<= 1) {
            }

            this.table = this.newTable(var3);
            this.loadFactor = var2;
            this.threshold = (int)((float)var3 * var2);
         } else {
            throw new IllegalArgumentException("Illegal Load factor: " + var2);
         }
      }
   }

   public WeakHashMap(int var1) {
      this(var1, 0.75F);
   }

   public WeakHashMap() {
      this(16, 0.75F);
   }

   public WeakHashMap(Map<? extends K, ? extends V> var1) {
      this(Math.max((int)((float)var1.size() / 0.75F) + 1, 16), 0.75F);
      this.putAll(var1);
   }

   private static Object maskNull(Object var0) {
      return var0 == null ? NULL_KEY : var0;
   }

   static Object unmaskNull(Object var0) {
      return var0 == NULL_KEY ? null : var0;
   }

   private static boolean eq(Object var0, Object var1) {
      return var0 == var1 || var0.equals(var1);
   }

   final int hash(Object var1) {
      int var2 = var1.hashCode();
      var2 ^= var2 >>> 20 ^ var2 >>> 12;
      return var2 ^ var2 >>> 7 ^ var2 >>> 4;
   }

   private static int indexFor(int var0, int var1) {
      return var0 & var1 - 1;
   }

   private void expungeStaleEntries() {
      Reference var1;
      while((var1 = this.queue.poll()) != null) {
         synchronized(this.queue) {
            WeakHashMap.Entry var3 = (WeakHashMap.Entry)var1;
            int var4 = indexFor(var3.hash, this.table.length);
            WeakHashMap.Entry var5 = this.table[var4];

            WeakHashMap.Entry var7;
            for(WeakHashMap.Entry var6 = var5; var6 != null; var6 = var7) {
               var7 = var6.next;
               if (var6 == var3) {
                  if (var5 == var3) {
                     this.table[var4] = var7;
                  } else {
                     var5.next = var7;
                  }

                  var3.value = null;
                  --this.size;
                  break;
               }

               var5 = var6;
            }
         }
      }

   }

   private WeakHashMap.Entry<K, V>[] getTable() {
      this.expungeStaleEntries();
      return this.table;
   }

   public int size() {
      if (this.size == 0) {
         return 0;
      } else {
         this.expungeStaleEntries();
         return this.size;
      }
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public V get(Object var1) {
      Object var2 = maskNull(var1);
      int var3 = this.hash(var2);
      WeakHashMap.Entry[] var4 = this.getTable();
      int var5 = indexFor(var3, var4.length);

      for(WeakHashMap.Entry var6 = var4[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var3 && eq(var2, var6.get())) {
            return var6.value;
         }
      }

      return null;
   }

   public boolean containsKey(Object var1) {
      return this.getEntry(var1) != null;
   }

   WeakHashMap.Entry<K, V> getEntry(Object var1) {
      Object var2 = maskNull(var1);
      int var3 = this.hash(var2);
      WeakHashMap.Entry[] var4 = this.getTable();
      int var5 = indexFor(var3, var4.length);

      WeakHashMap.Entry var6;
      for(var6 = var4[var5]; var6 != null && (var6.hash != var3 || !eq(var2, var6.get())); var6 = var6.next) {
      }

      return var6;
   }

   public V put(K var1, V var2) {
      Object var3 = maskNull(var1);
      int var4 = this.hash(var3);
      WeakHashMap.Entry[] var5 = this.getTable();
      int var6 = indexFor(var4, var5.length);

      WeakHashMap.Entry var7;
      for(var7 = var5[var6]; var7 != null; var7 = var7.next) {
         if (var4 == var7.hash && eq(var3, var7.get())) {
            Object var8 = var7.value;
            if (var2 != var8) {
               var7.value = var2;
            }

            return var8;
         }
      }

      ++this.modCount;
      var7 = var5[var6];
      var5[var6] = new WeakHashMap.Entry(var3, var2, this.queue, var4, var7);
      if (++this.size >= this.threshold) {
         this.resize(var5.length * 2);
      }

      return null;
   }

   void resize(int var1) {
      WeakHashMap.Entry[] var2 = this.getTable();
      int var3 = var2.length;
      if (var3 == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         WeakHashMap.Entry[] var4 = this.newTable(var1);
         this.transfer(var2, var4);
         this.table = var4;
         if (this.size >= this.threshold / 2) {
            this.threshold = (int)((float)var1 * this.loadFactor);
         } else {
            this.expungeStaleEntries();
            this.transfer(var4, var2);
            this.table = var2;
         }

      }
   }

   private void transfer(WeakHashMap.Entry<K, V>[] var1, WeakHashMap.Entry<K, V>[] var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         WeakHashMap.Entry var4 = var1[var3];

         WeakHashMap.Entry var5;
         for(var1[var3] = null; var4 != null; var4 = var5) {
            var5 = var4.next;
            Object var6 = var4.get();
            if (var6 == null) {
               var4.next = null;
               var4.value = null;
               --this.size;
            } else {
               int var7 = indexFor(var4.hash, var2.length);
               var4.next = var2[var7];
               var2[var7] = var4;
            }
         }
      }

   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      int var2 = var1.size();
      if (var2 != 0) {
         if (var2 > this.threshold) {
            int var3 = (int)((float)var2 / this.loadFactor + 1.0F);
            if (var3 > 1073741824) {
               var3 = 1073741824;
            }

            int var4;
            for(var4 = this.table.length; var4 < var3; var4 <<= 1) {
            }

            if (var4 > this.table.length) {
               this.resize(var4);
            }
         }

         Iterator var5 = var1.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            this.put(var6.getKey(), var6.getValue());
         }

      }
   }

   public V remove(Object var1) {
      Object var2 = maskNull(var1);
      int var3 = this.hash(var2);
      WeakHashMap.Entry[] var4 = this.getTable();
      int var5 = indexFor(var3, var4.length);
      WeakHashMap.Entry var6 = var4[var5];

      WeakHashMap.Entry var8;
      for(WeakHashMap.Entry var7 = var6; var7 != null; var7 = var8) {
         var8 = var7.next;
         if (var3 == var7.hash && eq(var2, var7.get())) {
            ++this.modCount;
            --this.size;
            if (var6 == var7) {
               var4[var5] = var8;
            } else {
               var6.next = var8;
            }

            return var7.value;
         }

         var6 = var7;
      }

      return null;
   }

   boolean removeMapping(Object var1) {
      if (!(var1 instanceof Map.Entry)) {
         return false;
      } else {
         WeakHashMap.Entry[] var2 = this.getTable();
         Map.Entry var3 = (Map.Entry)var1;
         Object var4 = maskNull(var3.getKey());
         int var5 = this.hash(var4);
         int var6 = indexFor(var5, var2.length);
         WeakHashMap.Entry var7 = var2[var6];

         WeakHashMap.Entry var9;
         for(WeakHashMap.Entry var8 = var7; var8 != null; var8 = var9) {
            var9 = var8.next;
            if (var5 == var8.hash && var8.equals(var3)) {
               ++this.modCount;
               --this.size;
               if (var7 == var8) {
                  var2[var6] = var9;
               } else {
                  var7.next = var9;
               }

               return true;
            }

            var7 = var8;
         }

         return false;
      }
   }

   public void clear() {
      while(this.queue.poll() != null) {
      }

      ++this.modCount;
      Arrays.fill(this.table, (Object)null);
      this.size = 0;

      while(this.queue.poll() != null) {
      }

   }

   public boolean containsValue(Object var1) {
      if (var1 == null) {
         return this.containsNullValue();
      } else {
         WeakHashMap.Entry[] var2 = this.getTable();
         int var3 = var2.length;

         while(var3-- > 0) {
            for(WeakHashMap.Entry var4 = var2[var3]; var4 != null; var4 = var4.next) {
               if (var1.equals(var4.value)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean containsNullValue() {
      WeakHashMap.Entry[] var1 = this.getTable();
      int var2 = var1.length;

      while(var2-- > 0) {
         for(WeakHashMap.Entry var3 = var1[var2]; var3 != null; var3 = var3.next) {
            if (var3.value == null) {
               return true;
            }
         }
      }

      return false;
   }

   public Set<K> keySet() {
      Object var1 = this.keySet;
      if (var1 == null) {
         var1 = new WeakHashMap.KeySet();
         this.keySet = (Set)var1;
      }

      return (Set)var1;
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new WeakHashMap.Values();
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new WeakHashMap.EntrySet());
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      WeakHashMap.Entry[] var3 = this.getTable();
      WeakHashMap.Entry[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WeakHashMap.Entry var7 = var4[var6];

         while(var7 != null) {
            Object var8 = var7.get();
            if (var8 != null) {
               var1.accept(unmaskNull(var8), var7.value);
            }

            var7 = var7.next;
            if (var2 != this.modCount) {
               throw new ConcurrentModificationException();
            }
         }
      }

   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      WeakHashMap.Entry[] var3 = this.getTable();
      WeakHashMap.Entry[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WeakHashMap.Entry var7 = var4[var6];

         while(var7 != null) {
            Object var8 = var7.get();
            if (var8 != null) {
               var7.value = var1.apply(unmaskNull(var8), var7.value);
            }

            var7 = var7.next;
            if (var2 != this.modCount) {
               throw new ConcurrentModificationException();
            }
         }
      }

   }

   static final class EntrySpliterator<K, V> extends WeakHashMap.WeakHashMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
      EntrySpliterator(WeakHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public WeakHashMap.EntrySpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new WeakHashMap.EntrySpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap var5 = this.map;
            WeakHashMap.Entry[] var6 = var5.table;
            int var3;
            int var4;
            if ((var3 = this.fence) < 0) {
               var4 = this.expectedModCount = var5.modCount;
               var3 = this.fence = var6.length;
            } else {
               var4 = this.expectedModCount;
            }

            int var2;
            if (var6.length >= var3 && (var2 = this.index) >= 0 && (var2 < (this.index = var3) || this.current != null)) {
               WeakHashMap.Entry var7 = this.current;
               this.current = null;

               do {
                  do {
                     if (var7 == null) {
                        var7 = var6[var2++];
                     } else {
                        Object var8 = var7.get();
                        Object var9 = var7.value;
                        var7 = var7.next;
                        if (var8 != null) {
                           Object var10 = WeakHashMap.unmaskNull(var8);
                           var1.accept(new AbstractMap.SimpleImmutableEntry(var10, var9));
                        }
                     }
                  } while(var7 != null);
               } while(var2 < var3);
            }

            if (var5.modCount != var4) {
               throw new ConcurrentModificationException();
            }
         }
      }

      public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap.Entry[] var3 = this.map.table;
            int var2;
            if (var3.length >= (var2 = this.getFence()) && this.index >= 0) {
               while(this.current != null || this.index < var2) {
                  if (this.current == null) {
                     this.current = var3[this.index++];
                  } else {
                     Object var4 = this.current.get();
                     Object var5 = this.current.value;
                     this.current = this.current.next;
                     if (var4 != null) {
                        Object var6 = WeakHashMap.unmaskNull(var4);
                        var1.accept(new AbstractMap.SimpleImmutableEntry(var6, var5));
                        if (this.map.modCount != this.expectedModCount) {
                           throw new ConcurrentModificationException();
                        }

                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }

      public int characteristics() {
         return 1;
      }
   }

   static final class ValueSpliterator<K, V> extends WeakHashMap.WeakHashMapSpliterator<K, V> implements Spliterator<V> {
      ValueSpliterator(WeakHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public WeakHashMap.ValueSpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new WeakHashMap.ValueSpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap var5 = this.map;
            WeakHashMap.Entry[] var6 = var5.table;
            int var3;
            int var4;
            if ((var3 = this.fence) < 0) {
               var4 = this.expectedModCount = var5.modCount;
               var3 = this.fence = var6.length;
            } else {
               var4 = this.expectedModCount;
            }

            int var2;
            if (var6.length >= var3 && (var2 = this.index) >= 0 && (var2 < (this.index = var3) || this.current != null)) {
               WeakHashMap.Entry var7 = this.current;
               this.current = null;

               do {
                  do {
                     if (var7 == null) {
                        var7 = var6[var2++];
                     } else {
                        Object var8 = var7.get();
                        Object var9 = var7.value;
                        var7 = var7.next;
                        if (var8 != null) {
                           var1.accept(var9);
                        }
                     }
                  } while(var7 != null);
               } while(var2 < var3);
            }

            if (var5.modCount != var4) {
               throw new ConcurrentModificationException();
            }
         }
      }

      public boolean tryAdvance(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap.Entry[] var3 = this.map.table;
            int var2;
            if (var3.length >= (var2 = this.getFence()) && this.index >= 0) {
               while(this.current != null || this.index < var2) {
                  if (this.current == null) {
                     this.current = var3[this.index++];
                  } else {
                     Object var4 = this.current.get();
                     Object var5 = this.current.value;
                     this.current = this.current.next;
                     if (var4 != null) {
                        var1.accept(var5);
                        if (this.map.modCount != this.expectedModCount) {
                           throw new ConcurrentModificationException();
                        }

                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }

      public int characteristics() {
         return 0;
      }
   }

   static final class KeySpliterator<K, V> extends WeakHashMap.WeakHashMapSpliterator<K, V> implements Spliterator<K> {
      KeySpliterator(WeakHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public WeakHashMap.KeySpliterator<K, V> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new WeakHashMap.KeySpliterator(this.map, var2, this.index = var3, this.est >>>= 1, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap var5 = this.map;
            WeakHashMap.Entry[] var6 = var5.table;
            int var3;
            int var4;
            if ((var3 = this.fence) < 0) {
               var4 = this.expectedModCount = var5.modCount;
               var3 = this.fence = var6.length;
            } else {
               var4 = this.expectedModCount;
            }

            int var2;
            if (var6.length >= var3 && (var2 = this.index) >= 0 && (var2 < (this.index = var3) || this.current != null)) {
               WeakHashMap.Entry var7 = this.current;
               this.current = null;

               do {
                  do {
                     if (var7 == null) {
                        var7 = var6[var2++];
                     } else {
                        Object var8 = var7.get();
                        var7 = var7.next;
                        if (var8 != null) {
                           Object var9 = WeakHashMap.unmaskNull(var8);
                           var1.accept(var9);
                        }
                     }
                  } while(var7 != null);
               } while(var2 < var3);
            }

            if (var5.modCount != var4) {
               throw new ConcurrentModificationException();
            }
         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            WeakHashMap.Entry[] var3 = this.map.table;
            int var2;
            if (var3.length >= (var2 = this.getFence()) && this.index >= 0) {
               while(this.current != null || this.index < var2) {
                  if (this.current == null) {
                     this.current = var3[this.index++];
                  } else {
                     Object var4 = this.current.get();
                     this.current = this.current.next;
                     if (var4 != null) {
                        Object var5 = WeakHashMap.unmaskNull(var4);
                        var1.accept(var5);
                        if (this.map.modCount != this.expectedModCount) {
                           throw new ConcurrentModificationException();
                        }

                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }

      public int characteristics() {
         return 1;
      }
   }

   static class WeakHashMapSpliterator<K, V> {
      final WeakHashMap<K, V> map;
      WeakHashMap.Entry<K, V> current;
      int index;
      int fence;
      int est;
      int expectedModCount;

      WeakHashMapSpliterator(WeakHashMap<K, V> var1, int var2, int var3, int var4, int var5) {
         this.map = var1;
         this.index = var2;
         this.fence = var3;
         this.est = var4;
         this.expectedModCount = var5;
      }

      final int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            WeakHashMap var2 = this.map;
            this.est = var2.size();
            this.expectedModCount = var2.modCount;
            var1 = this.fence = var2.table.length;
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
         return WeakHashMap.this.new EntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            WeakHashMap.Entry var3 = WeakHashMap.this.getEntry(var2.getKey());
            return var3 != null && var3.equals(var2);
         }
      }

      public boolean remove(Object var1) {
         return WeakHashMap.this.removeMapping(var1);
      }

      public int size() {
         return WeakHashMap.this.size();
      }

      public void clear() {
         WeakHashMap.this.clear();
      }

      private List<Map.Entry<K, V>> deepCopy() {
         ArrayList var1 = new ArrayList(this.size());
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            var1.add(new AbstractMap.SimpleEntry(var3));
         }

         return var1;
      }

      public Object[] toArray() {
         return this.deepCopy().toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.deepCopy().toArray(var1);
      }

      public Spliterator<Map.Entry<K, V>> spliterator() {
         return new WeakHashMap.EntrySpliterator(WeakHashMap.this, 0, -1, 0, 0);
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
         return WeakHashMap.this.new ValueIterator();
      }

      public int size() {
         return WeakHashMap.this.size();
      }

      public boolean contains(Object var1) {
         return WeakHashMap.this.containsValue(var1);
      }

      public void clear() {
         WeakHashMap.this.clear();
      }

      public Spliterator<V> spliterator() {
         return new WeakHashMap.ValueSpliterator(WeakHashMap.this, 0, -1, 0, 0);
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
         return WeakHashMap.this.new KeyIterator();
      }

      public int size() {
         return WeakHashMap.this.size();
      }

      public boolean contains(Object var1) {
         return WeakHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         if (WeakHashMap.this.containsKey(var1)) {
            WeakHashMap.this.remove(var1);
            return true;
         } else {
            return false;
         }
      }

      public void clear() {
         WeakHashMap.this.clear();
      }

      public Spliterator<K> spliterator() {
         return new WeakHashMap.KeySpliterator(WeakHashMap.this, 0, -1, 0, 0);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private class EntryIterator extends WeakHashMap<K, V>.HashIterator<Map.Entry<K, V>> {
      private EntryIterator() {
         super();
      }

      public Map.Entry<K, V> next() {
         return this.nextEntry();
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }
   }

   private class KeyIterator extends WeakHashMap<K, V>.HashIterator<K> {
      private KeyIterator() {
         super();
      }

      public K next() {
         return this.nextEntry().getKey();
      }

      // $FF: synthetic method
      KeyIterator(Object var2) {
         this();
      }
   }

   private class ValueIterator extends WeakHashMap<K, V>.HashIterator<V> {
      private ValueIterator() {
         super();
      }

      public V next() {
         return this.nextEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private abstract class HashIterator<T> implements Iterator<T> {
      private int index;
      private WeakHashMap.Entry<K, V> entry;
      private WeakHashMap.Entry<K, V> lastReturned;
      private int expectedModCount;
      private Object nextKey;
      private Object currentKey;

      HashIterator() {
         this.expectedModCount = WeakHashMap.this.modCount;
         this.index = WeakHashMap.this.isEmpty() ? 0 : WeakHashMap.this.table.length;
      }

      public boolean hasNext() {
         WeakHashMap.Entry[] var1 = WeakHashMap.this.table;

         while(this.nextKey == null) {
            WeakHashMap.Entry var2 = this.entry;

            int var3;
            for(var3 = this.index; var2 == null && var3 > 0; var2 = var1[var3]) {
               --var3;
            }

            this.entry = var2;
            this.index = var3;
            if (var2 == null) {
               this.currentKey = null;
               return false;
            }

            this.nextKey = var2.get();
            if (this.nextKey == null) {
               this.entry = this.entry.next;
            }
         }

         return true;
      }

      protected WeakHashMap.Entry<K, V> nextEntry() {
         if (WeakHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (this.nextKey == null && !this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.entry;
            this.entry = this.entry.next;
            this.currentKey = this.nextKey;
            this.nextKey = null;
            return this.lastReturned;
         }
      }

      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else if (WeakHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            WeakHashMap.this.remove(this.currentKey);
            this.expectedModCount = WeakHashMap.this.modCount;
            this.lastReturned = null;
            this.currentKey = null;
         }
      }
   }

   private static class Entry<K, V> extends WeakReference<Object> implements Map.Entry<K, V> {
      V value;
      final int hash;
      WeakHashMap.Entry<K, V> next;

      Entry(Object var1, V var2, ReferenceQueue<Object> var3, int var4, WeakHashMap.Entry<K, V> var5) {
         super(var1, var3);
         this.value = var2;
         this.hash = var4;
         this.next = var5;
      }

      public K getKey() {
         return WeakHashMap.unmaskNull(this.get());
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
            Object var3 = this.getKey();
            Object var4 = var2.getKey();
            if (var3 == var4 || var3 != null && var3.equals(var4)) {
               Object var5 = this.getValue();
               Object var6 = var2.getValue();
               if (var5 == var6 || var5 != null && var5.equals(var6)) {
                  return true;
               }
            }

            return false;
         }
      }

      public int hashCode() {
         Object var1 = this.getKey();
         Object var2 = this.getValue();
         return Objects.hashCode(var1) ^ Objects.hashCode(var2);
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
