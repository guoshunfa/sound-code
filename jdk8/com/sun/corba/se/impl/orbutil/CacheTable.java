package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CacheTable {
   private boolean noReverseMap;
   static final int INITIAL_SIZE = 16;
   static final int MAX_SIZE = 1073741824;
   int size;
   int entryCount;
   private CacheTable.Entry[] map;
   private CacheTable.Entry[] rmap;
   private ORB orb;
   private ORBUtilSystemException wrapper;

   private CacheTable() {
   }

   public CacheTable(ORB var1, boolean var2) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
      this.noReverseMap = var2;
      this.size = 16;
      this.entryCount = 0;
      this.initTables();
   }

   private void initTables() {
      this.map = new CacheTable.Entry[this.size];
      this.rmap = this.noReverseMap ? null : new CacheTable.Entry[this.size];
   }

   private void grow() {
      if (this.size != 1073741824) {
         CacheTable.Entry[] var1 = this.map;
         int var2 = this.size;
         this.size <<= 1;
         this.initTables();

         for(int var3 = 0; var3 < var2; ++var3) {
            for(CacheTable.Entry var4 = var1[var3]; var4 != null; var4 = var4.next) {
               this.put_table(var4.key, var4.val);
            }
         }

      }
   }

   private int moduloTableSize(int var1) {
      var1 += ~(var1 << 9);
      var1 ^= var1 >>> 14;
      var1 += var1 << 4;
      var1 ^= var1 >>> 10;
      return var1 & this.size - 1;
   }

   private int hash(Object var1) {
      return this.moduloTableSize(System.identityHashCode(var1));
   }

   private int hash(int var1) {
      return this.moduloTableSize(var1);
   }

   public final void put(Object var1, int var2) {
      if (this.put_table(var1, var2)) {
         ++this.entryCount;
         if (this.entryCount > this.size * 3 / 4) {
            this.grow();
         }
      }

   }

   private boolean put_table(Object var1, int var2) {
      int var3 = this.hash(var1);

      CacheTable.Entry var4;
      for(var4 = this.map[var3]; var4 != null; var4 = var4.next) {
         if (var4.key == var1) {
            if (var4.val != var2) {
               throw this.wrapper.duplicateIndirectionOffset();
            }

            return false;
         }
      }

      var4 = new CacheTable.Entry(var1, var2);
      var4.next = this.map[var3];
      this.map[var3] = var4;
      if (!this.noReverseMap) {
         int var5 = this.hash(var2);
         var4.rnext = this.rmap[var5];
         this.rmap[var5] = var4;
      }

      return true;
   }

   public final boolean containsKey(Object var1) {
      return this.getVal(var1) != -1;
   }

   public final int getVal(Object var1) {
      int var2 = this.hash(var1);

      for(CacheTable.Entry var3 = this.map[var2]; var3 != null; var3 = var3.next) {
         if (var3.key == var1) {
            return var3.val;
         }
      }

      return -1;
   }

   public final boolean containsVal(int var1) {
      return this.getKey(var1) != null;
   }

   public final boolean containsOrderedVal(int var1) {
      return this.containsVal(var1);
   }

   public final Object getKey(int var1) {
      int var2 = this.hash(var1);

      for(CacheTable.Entry var3 = this.rmap[var2]; var3 != null; var3 = var3.rnext) {
         if (var3.val == var1) {
            return var3.key;
         }
      }

      return null;
   }

   public void done() {
      this.map = null;
      this.rmap = null;
   }

   class Entry {
      Object key;
      int val;
      CacheTable.Entry next;
      CacheTable.Entry rnext;

      public Entry(Object var2, int var3) {
         this.key = var2;
         this.val = var3;
         this.next = null;
         this.rnext = null;
      }
   }
}
