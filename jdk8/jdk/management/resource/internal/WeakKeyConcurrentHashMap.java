package jdk.management.resource.internal;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

final class WeakKeyConcurrentHashMap<K, V> {
   private final ConcurrentHashMap<WeakKeyConcurrentHashMap.WeakKey<K>, V> hashmap = new ConcurrentHashMap();
   private final ReferenceQueue<K> lastQueue = new ReferenceQueue();

   public WeakKeyConcurrentHashMap() {
   }

   public int size() {
      this.purgeStaleKeys();
      return this.hashmap.size();
   }

   public V get(K var1) {
      Objects.requireNonNull(var1, "key");
      this.purgeStaleKeys();
      WeakKeyConcurrentHashMap.WeakKey var2 = new WeakKeyConcurrentHashMap.WeakKey(var1, (ReferenceQueue)null);
      return this.hashmap.get(var2);
   }

   private boolean containsKey(K var1) {
      Objects.requireNonNull(var1, "key");
      WeakKeyConcurrentHashMap.WeakKey var2 = new WeakKeyConcurrentHashMap.WeakKey(var1, (ReferenceQueue)null);
      return this.hashmap.containsKey(var2);
   }

   public V put(K var1, V var2) {
      Objects.requireNonNull(var1, "key");
      this.purgeStaleKeys();
      WeakKeyConcurrentHashMap.WeakKey var3 = new WeakKeyConcurrentHashMap.WeakKey(var1, this.lastQueue);
      return this.hashmap.put(var3, var2);
   }

   public V remove(K var1) {
      Objects.requireNonNull(var1, "key");
      this.purgeStaleKeys();
      WeakKeyConcurrentHashMap.WeakKey var2 = new WeakKeyConcurrentHashMap.WeakKey(var1, (ReferenceQueue)null);
      return this.hashmap.remove(var2);
   }

   public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Objects.requireNonNull(var1, "key");
      Objects.requireNonNull(var2, (String)"mappingFunction");
      this.purgeStaleKeys();
      WeakKeyConcurrentHashMap.WeakKey var3 = new WeakKeyConcurrentHashMap.WeakKey(var1, this.lastQueue);
      return this.hashmap.computeIfAbsent(var3, (var2x) -> {
         return var2.apply(var1);
      });
   }

   public Stream<K> keysForValue(V var1) {
      return this.hashmap.entrySet().stream().filter((var1x) -> {
         return var1x.getValue() == var1;
      }).map((var0) -> {
         return ((WeakKeyConcurrentHashMap.WeakKey)var0.getKey()).get();
      }).filter((var0) -> {
         return var0 != null;
      });
   }

   public void purgeValue(V var1) {
      this.purgeStaleKeys();
      Objects.requireNonNull(var1, "value");
      this.hashmap.forEach((var2, var3) -> {
         if (var1.equals(var3)) {
            this.hashmap.remove(var2, var3);
         }

      });
   }

   private void purgeStaleKeys() {
      Reference var1;
      while((var1 = this.lastQueue.poll()) != null) {
         this.hashmap.remove(var1);
      }

   }

   static class WeakKey<K> extends WeakReference<K> {
      private final int hash;

      WeakKey(K var1, ReferenceQueue<K> var2) {
         super(var1, var2);
         this.hash = System.identityHashCode(var1);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof WeakKeyConcurrentHashMap.WeakKey)) {
            return false;
         } else {
            Object var2 = this.get();
            return var2 != null && var2 == ((WeakKeyConcurrentHashMap.WeakKey)var1).get();
         }
      }
   }
}
