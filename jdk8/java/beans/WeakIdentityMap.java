package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class WeakIdentityMap<T> {
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final Object NULL = new Object();
   private final ReferenceQueue<Object> queue = new ReferenceQueue();
   private volatile WeakIdentityMap.Entry<T>[] table = this.newTable(8);
   private int threshold = 6;
   private int size = 0;

   public T get(Object var1) {
      this.removeStaleEntries();
      if (var1 == null) {
         var1 = NULL;
      }

      int var2 = var1.hashCode();
      WeakIdentityMap.Entry[] var3 = this.table;
      int var4 = getIndex(var3, var2);

      for(WeakIdentityMap.Entry var5 = var3[var4]; var5 != null; var5 = var5.next) {
         if (var5.isMatched(var1, var2)) {
            return var5.value;
         }
      }

      synchronized(NULL) {
         var4 = getIndex(this.table, var2);

         for(WeakIdentityMap.Entry var6 = this.table[var4]; var6 != null; var6 = var6.next) {
            if (var6.isMatched(var1, var2)) {
               return var6.value;
            }
         }

         Object var9 = this.create(var1);
         this.table[var4] = new WeakIdentityMap.Entry(var1, var2, var9, this.queue, this.table[var4]);
         if (++this.size >= this.threshold) {
            if (this.table.length == 1073741824) {
               this.threshold = Integer.MAX_VALUE;
            } else {
               this.removeStaleEntries();
               var3 = this.newTable(this.table.length * 2);
               this.transfer(this.table, var3);
               if (this.size >= this.threshold / 2) {
                  this.table = var3;
                  this.threshold *= 2;
               } else {
                  this.transfer(var3, this.table);
               }
            }
         }

         return var9;
      }
   }

   protected abstract T create(Object var1);

   private void removeStaleEntries() {
      Reference var1 = this.queue.poll();
      if (var1 != null) {
         synchronized(NULL) {
            while(true) {
               WeakIdentityMap.Entry var3 = (WeakIdentityMap.Entry)var1;
               int var4 = getIndex(this.table, var3.hash);
               WeakIdentityMap.Entry var5 = this.table[var4];

               WeakIdentityMap.Entry var7;
               for(WeakIdentityMap.Entry var6 = var5; var6 != null; var6 = var7) {
                  var7 = var6.next;
                  if (var6 == var3) {
                     if (var5 == var3) {
                        this.table[var4] = var7;
                     } else {
                        var5.next = var7;
                     }

                     var3.value = null;
                     var3.next = null;
                     --this.size;
                     break;
                  }

                  var5 = var6;
               }

               var1 = this.queue.poll();
               if (var1 == null) {
                  break;
               }
            }
         }
      }

   }

   private void transfer(WeakIdentityMap.Entry<T>[] var1, WeakIdentityMap.Entry<T>[] var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         WeakIdentityMap.Entry var4 = var1[var3];

         WeakIdentityMap.Entry var5;
         for(var1[var3] = null; var4 != null; var4 = var5) {
            var5 = var4.next;
            Object var6 = var4.get();
            if (var6 == null) {
               var4.value = null;
               var4.next = null;
               --this.size;
            } else {
               int var7 = getIndex(var2, var4.hash);
               var4.next = var2[var7];
               var2[var7] = var4;
            }
         }
      }

   }

   private WeakIdentityMap.Entry<T>[] newTable(int var1) {
      return (WeakIdentityMap.Entry[])(new WeakIdentityMap.Entry[var1]);
   }

   private static int getIndex(WeakIdentityMap.Entry<?>[] var0, int var1) {
      return var1 & var0.length - 1;
   }

   private static class Entry<T> extends WeakReference<Object> {
      private final int hash;
      private volatile T value;
      private volatile WeakIdentityMap.Entry<T> next;

      Entry(Object var1, int var2, T var3, ReferenceQueue<Object> var4, WeakIdentityMap.Entry<T> var5) {
         super(var1, var4);
         this.hash = var2;
         this.value = var3;
         this.next = var5;
      }

      boolean isMatched(Object var1, int var2) {
         return this.hash == var2 && var1 == this.get();
      }
   }
}
