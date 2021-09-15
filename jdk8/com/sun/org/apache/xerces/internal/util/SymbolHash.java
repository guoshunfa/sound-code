package com.sun.org.apache.xerces.internal.util;

public class SymbolHash {
   protected static final int TABLE_SIZE = 101;
   protected static final int MAX_HASH_COLLISIONS = 40;
   protected static final int MULTIPLIERS_SIZE = 32;
   protected static final int MULTIPLIERS_MASK = 31;
   protected int fTableSize;
   protected SymbolHash.Entry[] fBuckets;
   protected int fNum;
   protected int[] fHashMultipliers;

   public SymbolHash() {
      this(101);
   }

   public SymbolHash(int size) {
      this.fNum = 0;
      this.fTableSize = size;
      this.fBuckets = new SymbolHash.Entry[this.fTableSize];
   }

   public void put(Object key, Object value) {
      int collisionCount = 0;
      int hash = this.hash(key);
      int bucket = hash % this.fTableSize;

      SymbolHash.Entry entry;
      for(entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (key.equals(entry.key)) {
            entry.value = value;
            return;
         }

         ++collisionCount;
      }

      if (this.fNum >= this.fTableSize) {
         this.rehash();
         bucket = hash % this.fTableSize;
      } else if (collisionCount >= 40 && key instanceof String) {
         this.rebalance();
         bucket = this.hash(key) % this.fTableSize;
      }

      entry = new SymbolHash.Entry(key, value, this.fBuckets[bucket]);
      this.fBuckets[bucket] = entry;
      ++this.fNum;
   }

   public Object get(Object key) {
      int bucket = this.hash(key) % this.fTableSize;
      SymbolHash.Entry entry = this.search(key, bucket);
      return entry != null ? entry.value : null;
   }

   public int getLength() {
      return this.fNum;
   }

   public int getValues(Object[] elements, int from) {
      int i = 0;

      for(int j = 0; i < this.fTableSize && j < this.fNum; ++i) {
         for(SymbolHash.Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
            elements[from + j] = entry.value;
            ++j;
         }
      }

      return this.fNum;
   }

   public Object[] getEntries() {
      Object[] entries = new Object[this.fNum << 1];
      int i = 0;

      for(int j = 0; i < this.fTableSize && j < this.fNum << 1; ++i) {
         for(SymbolHash.Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
            entries[j] = entry.key;
            ++j;
            entries[j] = entry.value;
            ++j;
         }
      }

      return entries;
   }

   public SymbolHash makeClone() {
      SymbolHash newTable = new SymbolHash(this.fTableSize);
      newTable.fNum = this.fNum;
      newTable.fHashMultipliers = this.fHashMultipliers != null ? (int[])((int[])this.fHashMultipliers.clone()) : null;

      for(int i = 0; i < this.fTableSize; ++i) {
         if (this.fBuckets[i] != null) {
            newTable.fBuckets[i] = this.fBuckets[i].makeClone();
         }
      }

      return newTable;
   }

   public void clear() {
      for(int i = 0; i < this.fTableSize; ++i) {
         this.fBuckets[i] = null;
      }

      this.fNum = 0;
      this.fHashMultipliers = null;
   }

   protected SymbolHash.Entry search(Object key, int bucket) {
      for(SymbolHash.Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (key.equals(entry.key)) {
            return entry;
         }
      }

      return null;
   }

   protected int hash(Object key) {
      return this.fHashMultipliers != null && key instanceof String ? this.hash0((String)key) : key.hashCode() & Integer.MAX_VALUE;
   }

   private int hash0(String symbol) {
      int code = 0;
      int length = symbol.length();
      int[] multipliers = this.fHashMultipliers;

      for(int i = 0; i < length; ++i) {
         code = code * multipliers[i & 31] + symbol.charAt(i);
      }

      return code & Integer.MAX_VALUE;
   }

   protected void rehash() {
      this.rehashCommon((this.fBuckets.length << 1) + 1);
   }

   protected void rebalance() {
      if (this.fHashMultipliers == null) {
         this.fHashMultipliers = new int[32];
      }

      PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
      this.rehashCommon(this.fBuckets.length);
   }

   private void rehashCommon(int newCapacity) {
      int oldCapacity = this.fBuckets.length;
      SymbolHash.Entry[] oldTable = this.fBuckets;
      SymbolHash.Entry[] newTable = new SymbolHash.Entry[newCapacity];
      this.fBuckets = newTable;
      this.fTableSize = this.fBuckets.length;
      int i = oldCapacity;

      SymbolHash.Entry e;
      int index;
      while(i-- > 0) {
         for(SymbolHash.Entry old = oldTable[i]; old != null; newTable[index] = e) {
            e = old;
            old = old.next;
            index = this.hash(e.key) % newCapacity;
            e.next = newTable[index];
         }
      }

   }

   protected static final class Entry {
      public Object key;
      public Object value;
      public SymbolHash.Entry next;

      public Entry() {
         this.key = null;
         this.value = null;
         this.next = null;
      }

      public Entry(Object key, Object value, SymbolHash.Entry next) {
         this.key = key;
         this.value = value;
         this.next = next;
      }

      public SymbolHash.Entry makeClone() {
         SymbolHash.Entry entry = new SymbolHash.Entry();
         entry.key = this.key;
         entry.value = this.value;
         if (this.next != null) {
            entry.next = this.next.makeClone();
         }

         return entry;
      }
   }
}
