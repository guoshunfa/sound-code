package java.lang;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassValue<T> {
   private static final ClassValue.Entry<?>[] EMPTY_CACHE = new ClassValue.Entry[]{null};
   final int hashCodeForCache;
   private static final AtomicInteger nextHashCode = new AtomicInteger();
   private static final int HASH_INCREMENT = 1640531527;
   static final int HASH_MASK = 1073741823;
   final ClassValue.Identity identity;
   private volatile ClassValue.Version<T> version;
   private static final Object CRITICAL_SECTION = new Object();

   protected ClassValue() {
      this.hashCodeForCache = nextHashCode.getAndAdd(1640531527) & 1073741823;
      this.identity = new ClassValue.Identity();
      this.version = new ClassValue.Version(this);
   }

   protected abstract T computeValue(Class<?> var1);

   public T get(Class<?> var1) {
      ClassValue.Entry[] var2;
      ClassValue.Entry var3 = ClassValue.ClassValueMap.probeHomeLocation(var2 = getCacheCarefully(var1), this);
      return this.match(var3) ? var3.value() : this.getFromBackup(var2, var1);
   }

   public void remove(Class<?> var1) {
      ClassValue.ClassValueMap var2 = getMap(var1);
      var2.removeEntry(this);
   }

   void put(Class<?> var1, T var2) {
      ClassValue.ClassValueMap var3 = getMap(var1);
      var3.changeEntry(this, var2);
   }

   private static ClassValue.Entry<?>[] getCacheCarefully(Class<?> var0) {
      ClassValue.ClassValueMap var1 = var0.classValueMap;
      if (var1 == null) {
         return EMPTY_CACHE;
      } else {
         ClassValue.Entry[] var2 = var1.getCache();
         return var2;
      }
   }

   private T getFromBackup(ClassValue.Entry<?>[] var1, Class<?> var2) {
      ClassValue.Entry var3 = ClassValue.ClassValueMap.probeBackupLocations(var1, this);
      return var3 != null ? var3.value() : this.getFromHashMap(var2);
   }

   ClassValue.Entry<T> castEntry(ClassValue.Entry<?> var1) {
      return var1;
   }

   private T getFromHashMap(Class<?> var1) {
      ClassValue.ClassValueMap var2 = getMap(var1);

      ClassValue.Entry var3;
      do {
         var3 = var2.startEntry(this);
         if (!var3.isPromise()) {
            return var3.value();
         }

         try {
            var3 = makeEntry(var3.version(), this.computeValue(var1));
         } finally {
            var3 = var2.finishEntry(this, var3);
         }
      } while(var3 == null);

      return var3.value();
   }

   boolean match(ClassValue.Entry<?> var1) {
      return var1 != null && var1.get() == this.version;
   }

   ClassValue.Version<T> version() {
      return this.version;
   }

   void bumpVersion() {
      this.version = new ClassValue.Version(this);
   }

   private static ClassValue.ClassValueMap getMap(Class<?> var0) {
      ClassValue.ClassValueMap var1 = var0.classValueMap;
      return var1 != null ? var1 : initializeMap(var0);
   }

   private static ClassValue.ClassValueMap initializeMap(Class<?> var0) {
      synchronized(CRITICAL_SECTION) {
         ClassValue.ClassValueMap var1;
         if ((var1 = var0.classValueMap) == null) {
            var0.classValueMap = var1 = new ClassValue.ClassValueMap(var0);
         }

         return var1;
      }
   }

   static <T> ClassValue.Entry<T> makeEntry(ClassValue.Version<T> var0, T var1) {
      return new ClassValue.Entry(var0, var1);
   }

   static class ClassValueMap extends WeakHashMap<ClassValue.Identity, ClassValue.Entry<?>> {
      private final Class<?> type;
      private ClassValue.Entry<?>[] cacheArray;
      private int cacheLoad;
      private int cacheLoadLimit;
      private static final int INITIAL_ENTRIES = 32;
      private static final int CACHE_LOAD_LIMIT = 67;
      private static final int PROBE_LIMIT = 6;

      ClassValueMap(Class<?> var1) {
         this.type = var1;
         this.sizeCache(32);
      }

      ClassValue.Entry<?>[] getCache() {
         return this.cacheArray;
      }

      synchronized <T> ClassValue.Entry<T> startEntry(ClassValue<T> var1) {
         ClassValue.Entry var2 = (ClassValue.Entry)this.get(var1.identity);
         ClassValue.Version var3 = var1.version();
         if (var2 == null) {
            var2 = var3.promise();
            this.put(var1.identity, var2);
            return var2;
         } else if (var2.isPromise()) {
            if (var2.version() != var3) {
               var2 = var3.promise();
               this.put(var1.identity, var2);
            }

            return var2;
         } else {
            if (var2.version() != var3) {
               var2 = var2.refreshVersion(var3);
               this.put(var1.identity, var2);
            }

            this.checkCacheLoad();
            this.addToCache(var1, var2);
            return var2;
         }
      }

      synchronized <T> ClassValue.Entry<T> finishEntry(ClassValue<T> var1, ClassValue.Entry<T> var2) {
         ClassValue.Entry var3 = (ClassValue.Entry)this.get(var1.identity);
         if (var2 == var3) {
            assert var2.isPromise();

            this.remove(var1.identity);
            return null;
         } else if (var3 != null && var3.isPromise() && var3.version() == var2.version()) {
            ClassValue.Version var4 = var1.version();
            if (var2.version() != var4) {
               var2 = var2.refreshVersion(var4);
            }

            this.put(var1.identity, var2);
            this.checkCacheLoad();
            this.addToCache(var1, var2);
            return var2;
         } else {
            return null;
         }
      }

      synchronized void removeEntry(ClassValue<?> var1) {
         ClassValue.Entry var2 = (ClassValue.Entry)this.remove(var1.identity);
         if (var2 != null) {
            if (var2.isPromise()) {
               this.put(var1.identity, var2);
            } else {
               var1.bumpVersion();
               this.removeStaleEntries(var1);
            }
         }

      }

      synchronized <T> void changeEntry(ClassValue<T> var1, T var2) {
         ClassValue.Entry var3 = (ClassValue.Entry)this.get(var1.identity);
         ClassValue.Version var4 = var1.version();
         if (var3 != null) {
            if (var3.version() == var4 && var3.value() == var2) {
               return;
            }

            var1.bumpVersion();
            this.removeStaleEntries(var1);
         }

         ClassValue.Entry var5 = ClassValue.makeEntry(var4, var2);
         this.put(var1.identity, var5);
         this.checkCacheLoad();
         this.addToCache(var1, var5);
      }

      static ClassValue.Entry<?> loadFromCache(ClassValue.Entry<?>[] var0, int var1) {
         return var0[var1 & var0.length - 1];
      }

      static <T> ClassValue.Entry<T> probeHomeLocation(ClassValue.Entry<?>[] var0, ClassValue<T> var1) {
         return var1.castEntry(loadFromCache(var0, var1.hashCodeForCache));
      }

      static <T> ClassValue.Entry<T> probeBackupLocations(ClassValue.Entry<?>[] var0, ClassValue<T> var1) {
         int var2 = var0.length - 1;
         int var3 = var1.hashCodeForCache & var2;
         ClassValue.Entry var4 = var0[var3];
         if (var4 == null) {
            return null;
         } else {
            int var5 = -1;

            for(int var6 = var3 + 1; var6 < var3 + 6; ++var6) {
               ClassValue.Entry var7 = var0[var6 & var2];
               if (var7 == null) {
                  break;
               }

               if (var1.match(var7)) {
                  var0[var3] = var7;
                  if (var5 >= 0) {
                     var0[var6 & var2] = ClassValue.Entry.DEAD_ENTRY;
                  } else {
                     var5 = var6;
                  }

                  var0[var5 & var2] = entryDislocation(var0, var5, var4) < 6 ? var4 : ClassValue.Entry.DEAD_ENTRY;
                  return var1.castEntry(var7);
               }

               if (!var7.isLive() && var5 < 0) {
                  var5 = var6;
               }
            }

            return null;
         }
      }

      private static int entryDislocation(ClassValue.Entry<?>[] var0, int var1, ClassValue.Entry<?> var2) {
         ClassValue var3 = var2.classValueOrNull();
         if (var3 == null) {
            return 0;
         } else {
            int var4 = var0.length - 1;
            return var1 - var3.hashCodeForCache & var4;
         }
      }

      private void sizeCache(int var1) {
         assert (var1 & var1 - 1) == 0;

         this.cacheLoad = 0;
         this.cacheLoadLimit = (int)((double)var1 * 67.0D / 100.0D);
         this.cacheArray = new ClassValue.Entry[var1];
      }

      private void checkCacheLoad() {
         if (this.cacheLoad >= this.cacheLoadLimit) {
            this.reduceCacheLoad();
         }

      }

      private void reduceCacheLoad() {
         this.removeStaleEntries();
         if (this.cacheLoad >= this.cacheLoadLimit) {
            ClassValue.Entry[] var1 = this.getCache();
            if (var1.length <= 1073741823) {
               this.sizeCache(var1.length * 2);
               ClassValue.Entry[] var2 = var1;
               int var3 = var1.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  ClassValue.Entry var5 = var2[var4];
                  if (var5 != null && var5.isLive()) {
                     this.addToCache(var5);
                  }
               }

            }
         }
      }

      private void removeStaleEntries(ClassValue.Entry<?>[] var1, int var2, int var3) {
         int var4 = var1.length - 1;
         int var5 = 0;

         for(int var6 = var2; var6 < var2 + var3; ++var6) {
            ClassValue.Entry var7 = var1[var6 & var4];
            if (var7 != null && !var7.isLive()) {
               ClassValue.Entry var8 = null;
               var8 = this.findReplacement(var1, var6);
               var1[var6 & var4] = var8;
               if (var8 == null) {
                  ++var5;
               }
            }
         }

         this.cacheLoad = Math.max(0, this.cacheLoad - var5);
      }

      private ClassValue.Entry<?> findReplacement(ClassValue.Entry<?>[] var1, int var2) {
         ClassValue.Entry var3 = null;
         byte var4 = -1;
         int var5 = 0;
         int var6 = var1.length - 1;

         for(int var7 = var2 + 1; var7 < var2 + 6; ++var7) {
            ClassValue.Entry var8 = var1[var7 & var6];
            if (var8 == null) {
               break;
            }

            if (var8.isLive()) {
               int var9 = entryDislocation(var1, var7, var8);
               if (var9 != 0) {
                  int var10 = var7 - var9;
                  if (var10 <= var2) {
                     if (var10 == var2) {
                        var4 = 1;
                        var5 = var7;
                        var3 = var8;
                     } else if (var4 <= 0) {
                        var4 = 0;
                        var5 = var7;
                        var3 = var8;
                     }
                  }
               }
            }
         }

         if (var4 >= 0) {
            if (var1[var5 + 1 & var6] != null) {
               var1[var5 & var6] = ClassValue.Entry.DEAD_ENTRY;
            } else {
               var1[var5 & var6] = null;
               --this.cacheLoad;
            }
         }

         return var3;
      }

      private void removeStaleEntries(ClassValue<?> var1) {
         this.removeStaleEntries(this.getCache(), var1.hashCodeForCache, 6);
      }

      private void removeStaleEntries() {
         ClassValue.Entry[] var1 = this.getCache();
         this.removeStaleEntries(var1, 0, var1.length + 6 - 1);
      }

      private <T> void addToCache(ClassValue.Entry<T> var1) {
         ClassValue var2 = var1.classValueOrNull();
         if (var2 != null) {
            this.addToCache(var2, var1);
         }

      }

      private <T> void addToCache(ClassValue<T> var1, ClassValue.Entry<T> var2) {
         ClassValue.Entry[] var3 = this.getCache();
         int var4 = var3.length - 1;
         int var5 = var1.hashCodeForCache & var4;
         ClassValue.Entry var6 = this.placeInCache(var3, var5, var2, false);
         if (var6 != null) {
            int var7 = entryDislocation(var3, var5, var6);
            int var8 = var5 - var7;

            for(int var9 = var8; var9 < var8 + 6; ++var9) {
               if (this.placeInCache(var3, var9 & var4, var6, true) == null) {
                  return;
               }
            }

         }
      }

      private ClassValue.Entry<?> placeInCache(ClassValue.Entry<?>[] var1, int var2, ClassValue.Entry<?> var3, boolean var4) {
         ClassValue.Entry var5 = this.overwrittenEntry(var1[var2]);
         if (var4 && var5 != null) {
            return var3;
         } else {
            var1[var2] = var3;
            return var5;
         }
      }

      private <T> ClassValue.Entry<T> overwrittenEntry(ClassValue.Entry<T> var1) {
         if (var1 == null) {
            ++this.cacheLoad;
         } else if (var1.isLive()) {
            return var1;
         }

         return null;
      }
   }

   static class Entry<T> extends WeakReference<ClassValue.Version<T>> {
      final Object value;
      static final ClassValue.Entry<?> DEAD_ENTRY = new ClassValue.Entry((ClassValue.Version)null, (Object)null);

      Entry(ClassValue.Version<T> var1, T var2) {
         super(var1);
         this.value = var2;
      }

      private void assertNotPromise() {
         assert !this.isPromise();

      }

      Entry(ClassValue.Version<T> var1) {
         super(var1);
         this.value = this;
      }

      T value() {
         this.assertNotPromise();
         return this.value;
      }

      boolean isPromise() {
         return this.value == this;
      }

      ClassValue.Version<T> version() {
         return (ClassValue.Version)this.get();
      }

      ClassValue<T> classValueOrNull() {
         ClassValue.Version var1 = this.version();
         return var1 == null ? null : var1.classValue();
      }

      boolean isLive() {
         ClassValue.Version var1 = this.version();
         if (var1 == null) {
            return false;
         } else if (var1.isLive()) {
            return true;
         } else {
            this.clear();
            return false;
         }
      }

      ClassValue.Entry<T> refreshVersion(ClassValue.Version<T> var1) {
         this.assertNotPromise();
         ClassValue.Entry var2 = new ClassValue.Entry(var1, this.value);
         this.clear();
         return var2;
      }
   }

   static class Version<T> {
      private final ClassValue<T> classValue;
      private final ClassValue.Entry<T> promise = new ClassValue.Entry(this);

      Version(ClassValue<T> var1) {
         this.classValue = var1;
      }

      ClassValue<T> classValue() {
         return this.classValue;
      }

      ClassValue.Entry<T> promise() {
         return this.promise;
      }

      boolean isLive() {
         return this.classValue.version() == this;
      }
   }

   static class Identity {
   }
}
