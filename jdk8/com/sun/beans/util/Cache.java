package com.sun.beans.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class Cache<K, V> {
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private final boolean identity;
   private final Cache.Kind keyKind;
   private final Cache.Kind valueKind;
   private final ReferenceQueue<Object> queue;
   private volatile Cache<K, V>.CacheEntry<K, V>[] table;
   private int threshold;
   private int size;

   public abstract V create(K var1);

   public Cache(Cache.Kind var1, Cache.Kind var2) {
      this(var1, var2, false);
   }

   public Cache(Cache.Kind var1, Cache.Kind var2, boolean var3) {
      this.queue = new ReferenceQueue();
      this.table = this.newTable(8);
      this.threshold = 6;
      Objects.requireNonNull(var1, (String)"keyKind");
      Objects.requireNonNull(var2, (String)"valueKind");
      this.keyKind = var1;
      this.valueKind = var2;
      this.identity = var3;
   }

   public final V get(K var1) {
      Objects.requireNonNull(var1, "key");
      this.removeStaleEntries();
      int var2 = this.hash(var1);
      Cache.CacheEntry[] var3 = this.table;
      Object var4 = this.getEntryValue(var1, var2, var3[index(var2, var3)]);
      if (var4 != null) {
         return var4;
      } else {
         synchronized(this.queue) {
            var4 = this.getEntryValue(var1, var2, this.table[index(var2, this.table)]);
            if (var4 != null) {
               return var4;
            } else {
               Object var6 = this.create(var1);
               Objects.requireNonNull(var6, "value");
               int var7 = index(var2, this.table);
               this.table[var7] = new Cache.CacheEntry(var2, var1, var6, this.table[var7]);
               if (++this.size >= this.threshold) {
                  if (this.table.length == 1073741824) {
                     this.threshold = Integer.MAX_VALUE;
                  } else {
                     this.removeStaleEntries();
                     var3 = this.newTable(this.table.length << 1);
                     this.transfer(this.table, var3);
                     if (this.size >= this.threshold / 2) {
                        this.table = var3;
                        this.threshold <<= 1;
                     } else {
                        this.transfer(var3, this.table);
                     }

                     this.removeStaleEntries();
                  }
               }

               return var6;
            }
         }
      }
   }

   public final void remove(K var1) {
      if (var1 != null) {
         synchronized(this.queue) {
            this.removeStaleEntries();
            int var3 = this.hash(var1);
            int var4 = index(var3, this.table);
            Cache.CacheEntry var5 = this.table[var4];

            Cache.CacheEntry var7;
            for(Cache.CacheEntry var6 = var5; var6 != null; var6 = var7) {
               var7 = var6.next;
               if (var6.matches(var3, var1)) {
                  if (var6 == var5) {
                     this.table[var4] = var7;
                  } else {
                     var5.next = var7;
                  }

                  var6.unlink();
                  break;
               }

               var5 = var6;
            }
         }
      }

   }

   public final void clear() {
      synchronized(this.queue) {
         Cache.CacheEntry var4;
         for(int var2 = this.table.length; 0 < var2--; this.table[var2] = null) {
            for(Cache.CacheEntry var3 = this.table[var2]; var3 != null; var3 = var4) {
               var4 = var3.next;
               var3.unlink();
            }
         }

         while(null != this.queue.poll()) {
         }

      }
   }

   private int hash(Object var1) {
      int var2;
      if (this.identity) {
         var2 = System.identityHashCode(var1);
         return (var2 << 1) - (var2 << 8);
      } else {
         var2 = var1.hashCode();
         var2 ^= var2 >>> 20 ^ var2 >>> 12;
         return var2 ^ var2 >>> 7 ^ var2 >>> 4;
      }
   }

   private static int index(int var0, Object[] var1) {
      return var0 & var1.length - 1;
   }

   private Cache<K, V>.CacheEntry<K, V>[] newTable(int var1) {
      return (Cache.CacheEntry[])(new Cache.CacheEntry[var1]);
   }

   private V getEntryValue(K var1, int var2, Cache<K, V>.CacheEntry<K, V> var3) {
      while(var3 != null) {
         if (var3.matches(var2, var1)) {
            return var3.value.getReferent();
         }

         var3 = var3.next;
      }

      return null;
   }

   private void removeStaleEntries() {
      Reference var1 = this.queue.poll();
      if (var1 != null) {
         synchronized(this.queue) {
            while(true) {
               if (var1 instanceof Cache.Ref) {
                  Cache.Ref var3 = (Cache.Ref)var1;
                  Cache.CacheEntry var4 = (Cache.CacheEntry)var3.getOwner();
                  if (var4 != null) {
                     int var5 = index(var4.hash, this.table);
                     Cache.CacheEntry var6 = this.table[var5];

                     Cache.CacheEntry var8;
                     for(Cache.CacheEntry var7 = var6; var7 != null; var7 = var8) {
                        var8 = var7.next;
                        if (var7 == var4) {
                           if (var7 == var6) {
                              this.table[var5] = var8;
                           } else {
                              var6.next = var8;
                           }

                           var7.unlink();
                           break;
                        }

                        var6 = var7;
                     }
                  }
               }

               var1 = this.queue.poll();
               if (var1 == null) {
                  break;
               }
            }
         }
      }

   }

   private void transfer(Cache<K, V>.CacheEntry<K, V>[] var1, Cache<K, V>.CacheEntry<K, V>[] var2) {
      int var3 = var1.length;

      while(0 < var3--) {
         Cache.CacheEntry var4 = var1[var3];

         Cache.CacheEntry var5;
         for(var1[var3] = null; var4 != null; var4 = var5) {
            var5 = var4.next;
            if (!var4.key.isStale() && !var4.value.isStale()) {
               int var6 = index(var4.hash, var2);
               var4.next = var2[var6];
               var2[var6] = var4;
            } else {
               var4.unlink();
            }
         }
      }

   }

   public static enum Kind {
      STRONG {
         <T> Cache.Ref<T> create(Object var1, T var2, ReferenceQueue<? super T> var3) {
            return new Cache.Kind.Strong(var1, var2);
         }
      },
      SOFT {
         <T> Cache.Ref<T> create(Object var1, T var2, ReferenceQueue<? super T> var3) {
            return (Cache.Ref)(var2 == null ? new Cache.Kind.Strong(var1, var2) : new Cache.Kind.Soft(var1, var2, var3));
         }
      },
      WEAK {
         <T> Cache.Ref<T> create(Object var1, T var2, ReferenceQueue<? super T> var3) {
            return (Cache.Ref)(var2 == null ? new Cache.Kind.Strong(var1, var2) : new Cache.Kind.Weak(var1, var2, var3));
         }
      };

      private Kind() {
      }

      abstract <T> Cache.Ref<T> create(Object var1, T var2, ReferenceQueue<? super T> var3);

      // $FF: synthetic method
      Kind(Object var3) {
         this();
      }

      private static final class Weak<T> extends WeakReference<T> implements Cache.Ref<T> {
         private Object owner;

         private Weak(Object var1, T var2, ReferenceQueue<? super T> var3) {
            super(var2, var3);
            this.owner = var1;
         }

         public Object getOwner() {
            return this.owner;
         }

         public T getReferent() {
            return this.get();
         }

         public boolean isStale() {
            return null == this.get();
         }

         public void removeOwner() {
            this.owner = null;
         }

         // $FF: synthetic method
         Weak(Object var1, Object var2, ReferenceQueue var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static final class Soft<T> extends SoftReference<T> implements Cache.Ref<T> {
         private Object owner;

         private Soft(Object var1, T var2, ReferenceQueue<? super T> var3) {
            super(var2, var3);
            this.owner = var1;
         }

         public Object getOwner() {
            return this.owner;
         }

         public T getReferent() {
            return this.get();
         }

         public boolean isStale() {
            return null == this.get();
         }

         public void removeOwner() {
            this.owner = null;
         }

         // $FF: synthetic method
         Soft(Object var1, Object var2, ReferenceQueue var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static final class Strong<T> implements Cache.Ref<T> {
         private Object owner;
         private final T referent;

         private Strong(Object var1, T var2) {
            this.owner = var1;
            this.referent = var2;
         }

         public Object getOwner() {
            return this.owner;
         }

         public T getReferent() {
            return this.referent;
         }

         public boolean isStale() {
            return false;
         }

         public void removeOwner() {
            this.owner = null;
         }

         // $FF: synthetic method
         Strong(Object var1, Object var2, Object var3) {
            this(var1, var2);
         }
      }
   }

   private interface Ref<T> {
      Object getOwner();

      T getReferent();

      boolean isStale();

      void removeOwner();
   }

   private final class CacheEntry<K, V> {
      private final int hash;
      private final Cache.Ref<K> key;
      private final Cache.Ref<V> value;
      private volatile Cache<K, V>.CacheEntry<K, V> next;

      private CacheEntry(int var2, K var3, V var4, Cache<K, V>.CacheEntry<K, V> var5) {
         this.hash = var2;
         this.key = Cache.this.keyKind.create(this, var3, Cache.this.queue);
         this.value = Cache.this.valueKind.create(this, var4, Cache.this.queue);
         this.next = var5;
      }

      private boolean matches(int var1, Object var2) {
         if (this.hash != var1) {
            return false;
         } else {
            Object var3 = this.key.getReferent();
            return var3 == var2 || !Cache.this.identity && var3 != null && var3.equals(var2);
         }
      }

      private void unlink() {
         this.next = null;
         this.key.removeOwner();
         this.value.removeOwner();
         Cache.this.size--;
      }

      // $FF: synthetic method
      CacheEntry(int var2, Object var3, Object var4, Cache.CacheEntry var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
