package sun.util.locale;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LocaleObjectCache<K, V> {
   private ConcurrentMap<K, LocaleObjectCache.CacheEntry<K, V>> map;
   private ReferenceQueue<V> queue;

   public LocaleObjectCache() {
      this(16, 0.75F, 16);
   }

   public LocaleObjectCache(int var1, float var2, int var3) {
      this.queue = new ReferenceQueue();
      this.map = new ConcurrentHashMap(var1, var2, var3);
   }

   public V get(K var1) {
      Object var2 = null;
      this.cleanStaleEntries();
      LocaleObjectCache.CacheEntry var3 = (LocaleObjectCache.CacheEntry)this.map.get(var1);
      if (var3 != null) {
         var2 = var3.get();
      }

      if (var2 == null) {
         Object var4 = this.createObject(var1);
         var1 = this.normalizeKey(var1);
         if (var1 == null || var4 == null) {
            return null;
         }

         LocaleObjectCache.CacheEntry var5 = new LocaleObjectCache.CacheEntry(var1, var4, this.queue);
         var3 = (LocaleObjectCache.CacheEntry)this.map.putIfAbsent(var1, var5);
         if (var3 == null) {
            var2 = var4;
         } else {
            var2 = var3.get();
            if (var2 == null) {
               this.map.put(var1, var5);
               var2 = var4;
            }
         }
      }

      return var2;
   }

   protected V put(K var1, V var2) {
      LocaleObjectCache.CacheEntry var3 = new LocaleObjectCache.CacheEntry(var1, var2, this.queue);
      LocaleObjectCache.CacheEntry var4 = (LocaleObjectCache.CacheEntry)this.map.put(var1, var3);
      return var4 == null ? null : var4.get();
   }

   private void cleanStaleEntries() {
      LocaleObjectCache.CacheEntry var1;
      while((var1 = (LocaleObjectCache.CacheEntry)this.queue.poll()) != null) {
         this.map.remove(var1.getKey());
      }

   }

   protected abstract V createObject(K var1);

   protected K normalizeKey(K var1) {
      return var1;
   }

   private static class CacheEntry<K, V> extends SoftReference<V> {
      private K key;

      CacheEntry(K var1, V var2, ReferenceQueue<V> var3) {
         super(var2, var3);
         this.key = var1;
      }

      K getKey() {
         return this.key;
      }
   }
}
