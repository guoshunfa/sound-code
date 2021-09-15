package java.lang.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class WeakCache<K, P, V> {
   private final ReferenceQueue<K> refQueue = new ReferenceQueue();
   private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map = new ConcurrentHashMap();
   private final ConcurrentMap<Supplier<V>, Boolean> reverseMap = new ConcurrentHashMap();
   private final BiFunction<K, P, ?> subKeyFactory;
   private final BiFunction<K, P, V> valueFactory;

   public WeakCache(BiFunction<K, P, ?> var1, BiFunction<K, P, V> var2) {
      this.subKeyFactory = (BiFunction)Objects.requireNonNull(var1);
      this.valueFactory = (BiFunction)Objects.requireNonNull(var2);
   }

   public V get(K var1, P var2) {
      Objects.requireNonNull(var2);
      this.expungeStaleEntries();
      Object var3 = WeakCache.CacheKey.valueOf(var1, this.refQueue);
      Object var4 = (ConcurrentMap)this.map.get(var3);
      if (var4 == null) {
         ConcurrentMap var5 = (ConcurrentMap)this.map.putIfAbsent(var3, var4 = new ConcurrentHashMap());
         if (var5 != null) {
            var4 = var5;
         }
      }

      Object var9 = Objects.requireNonNull(this.subKeyFactory.apply(var1, var2));
      Object var6 = (Supplier)((ConcurrentMap)var4).get(var9);
      WeakCache.Factory var7 = null;

      while(true) {
         if (var6 != null) {
            Object var8 = ((Supplier)var6).get();
            if (var8 != null) {
               return var8;
            }
         }

         if (var7 == null) {
            var7 = new WeakCache.Factory(var1, var2, var9, (ConcurrentMap)var4);
         }

         if (var6 == null) {
            var6 = (Supplier)((ConcurrentMap)var4).putIfAbsent(var9, var7);
            if (var6 == null) {
               var6 = var7;
            }
         } else if (((ConcurrentMap)var4).replace(var9, var6, var7)) {
            var6 = var7;
         } else {
            var6 = (Supplier)((ConcurrentMap)var4).get(var9);
         }
      }
   }

   public boolean containsValue(V var1) {
      Objects.requireNonNull(var1);
      this.expungeStaleEntries();
      return this.reverseMap.containsKey(new WeakCache.LookupValue(var1));
   }

   public int size() {
      this.expungeStaleEntries();
      return this.reverseMap.size();
   }

   private void expungeStaleEntries() {
      WeakCache.CacheKey var1;
      while((var1 = (WeakCache.CacheKey)this.refQueue.poll()) != null) {
         var1.expungeFrom(this.map, this.reverseMap);
      }

   }

   private static final class CacheKey<K> extends WeakReference<K> {
      private static final Object NULL_KEY = new Object();
      private final int hash;

      static <K> Object valueOf(K var0, ReferenceQueue<K> var1) {
         return var0 == null ? NULL_KEY : new WeakCache.CacheKey(var0, var1);
      }

      private CacheKey(K var1, ReferenceQueue<K> var2) {
         super(var1, var2);
         this.hash = System.identityHashCode(var1);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         Object var2;
         return var1 == this || var1 != null && var1.getClass() == this.getClass() && (var2 = this.get()) != null && var2 == ((WeakCache.CacheKey)var1).get();
      }

      void expungeFrom(ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> var1, ConcurrentMap<?, Boolean> var2) {
         ConcurrentMap var3 = (ConcurrentMap)var1.remove(this);
         if (var3 != null) {
            Iterator var4 = var3.values().iterator();

            while(var4.hasNext()) {
               Object var5 = var4.next();
               var2.remove(var5);
            }
         }

      }
   }

   private static final class CacheValue<V> extends WeakReference<V> implements WeakCache.Value<V> {
      private final int hash;

      CacheValue(V var1) {
         super(var1);
         this.hash = System.identityHashCode(var1);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         Object var2;
         return var1 == this || var1 instanceof WeakCache.Value && (var2 = this.get()) != null && var2 == ((WeakCache.Value)var1).get();
      }
   }

   private static final class LookupValue<V> implements WeakCache.Value<V> {
      private final V value;

      LookupValue(V var1) {
         this.value = var1;
      }

      public V get() {
         return this.value;
      }

      public int hashCode() {
         return System.identityHashCode(this.value);
      }

      public boolean equals(Object var1) {
         return var1 == this || var1 instanceof WeakCache.Value && this.value == ((WeakCache.Value)var1).get();
      }
   }

   private interface Value<V> extends Supplier<V> {
   }

   private final class Factory implements Supplier<V> {
      private final K key;
      private final P parameter;
      private final Object subKey;
      private final ConcurrentMap<Object, Supplier<V>> valuesMap;

      Factory(K var2, P var3, Object var4, ConcurrentMap<Object, Supplier<V>> var5) {
         this.key = var2;
         this.parameter = var3;
         this.subKey = var4;
         this.valuesMap = var5;
      }

      public synchronized V get() {
         Supplier var1 = (Supplier)this.valuesMap.get(this.subKey);
         if (var1 != this) {
            return null;
         } else {
            Object var2 = null;

            try {
               var2 = Objects.requireNonNull(WeakCache.this.valueFactory.apply(this.key, this.parameter));
            } finally {
               if (var2 == null) {
                  this.valuesMap.remove(this.subKey, this);
               }

            }

            assert var2 != null;

            WeakCache.CacheValue var3 = new WeakCache.CacheValue(var2);
            WeakCache.this.reverseMap.put(var3, Boolean.TRUE);
            if (!this.valuesMap.replace(this.subKey, this, var3)) {
               throw new AssertionError("Should not reach here");
            } else {
               return var2;
            }
         }
      }
   }
}
