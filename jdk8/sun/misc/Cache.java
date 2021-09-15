package sun.misc;

import java.util.Dictionary;
import java.util.Enumeration;

public class Cache extends Dictionary {
   private CacheEntry[] table;
   private int count;
   private int threshold;
   private float loadFactor;

   private void init(int var1, float var2) {
      if (var1 > 0 && (double)var2 > 0.0D) {
         this.loadFactor = var2;
         this.table = new CacheEntry[var1];
         this.threshold = (int)((float)var1 * var2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Cache(int var1, float var2) {
      this.init(var1, var2);
   }

   public Cache(int var1) {
      this.init(var1, 0.75F);
   }

   public Cache() {
      try {
         this.init(101, 0.75F);
      } catch (IllegalArgumentException var2) {
         throw new Error("panic");
      }
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public synchronized Enumeration keys() {
      return new CacheEnumerator(this.table, true);
   }

   public synchronized Enumeration elements() {
      return new CacheEnumerator(this.table, false);
   }

   public synchronized Object get(Object var1) {
      CacheEntry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(CacheEntry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            return var5.check();
         }
      }

      return null;
   }

   protected void rehash() {
      int var1 = this.table.length;
      CacheEntry[] var2 = this.table;
      int var3 = var1 * 2 + 1;
      CacheEntry[] var4 = new CacheEntry[var3];
      this.threshold = (int)((float)var3 * this.loadFactor);
      this.table = var4;
      int var5 = var1;

      while(var5-- > 0) {
         CacheEntry var6 = var2[var5];

         while(var6 != null) {
            CacheEntry var7 = var6;
            var6 = var6.next;
            if (var7.check() != null) {
               int var8 = (var7.hash & Integer.MAX_VALUE) % var3;
               var7.next = var4[var8];
               var4[var8] = var7;
            } else {
               --this.count;
            }
         }
      }

   }

   public synchronized Object put(Object var1, Object var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         CacheEntry[] var3 = this.table;
         int var4 = var1.hashCode();
         int var5 = (var4 & Integer.MAX_VALUE) % var3.length;
         CacheEntry var6 = null;

         for(CacheEntry var7 = var3[var5]; var7 != null; var7 = var7.next) {
            if (var7.hash == var4 && var7.key.equals(var1)) {
               Object var8 = var7.check();
               var7.setThing(var2);
               return var8;
            }

            if (var7.check() == null) {
               var6 = var7;
            }
         }

         if (this.count >= this.threshold) {
            this.rehash();
            return this.put(var1, var2);
         } else {
            if (var6 == null) {
               var6 = new CacheEntry();
               var6.next = var3[var5];
               var3[var5] = var6;
               ++this.count;
            }

            var6.hash = var4;
            var6.key = var1;
            var6.setThing(var2);
            return null;
         }
      }
   }

   public synchronized Object remove(Object var1) {
      CacheEntry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;
      CacheEntry var5 = var2[var4];

      for(CacheEntry var6 = null; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            if (var6 != null) {
               var6.next = var5.next;
            } else {
               var2[var4] = var5.next;
            }

            --this.count;
            return var5.check();
         }

         var6 = var5;
      }

      return null;
   }
}
