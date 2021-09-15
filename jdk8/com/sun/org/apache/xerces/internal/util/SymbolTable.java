package com.sun.org.apache.xerces.internal.util;

public class SymbolTable {
   protected static final int TABLE_SIZE = 101;
   protected static final int MAX_HASH_COLLISIONS = 40;
   protected static final int MULTIPLIERS_SIZE = 32;
   protected static final int MULTIPLIERS_MASK = 31;
   protected SymbolTable.Entry[] fBuckets;
   protected int fTableSize;
   protected transient int fCount;
   protected int fThreshold;
   protected float fLoadFactor;
   protected final int fCollisionThreshold;
   protected int[] fHashMultipliers;

   public SymbolTable(int initialCapacity, float loadFactor) {
      this.fBuckets = null;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      } else if (loadFactor > 0.0F && !Float.isNaN(loadFactor)) {
         if (initialCapacity == 0) {
            initialCapacity = 1;
         }

         this.fLoadFactor = loadFactor;
         this.fTableSize = initialCapacity;
         this.fBuckets = new SymbolTable.Entry[this.fTableSize];
         this.fThreshold = (int)((float)this.fTableSize * loadFactor);
         this.fCollisionThreshold = (int)(40.0F * loadFactor);
         this.fCount = 0;
      } else {
         throw new IllegalArgumentException("Illegal Load: " + loadFactor);
      }
   }

   public SymbolTable(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public SymbolTable() {
      this(101, 0.75F);
   }

   public String addSymbol(String symbol) {
      int collisionCount = 0;
      int bucket = this.hash(symbol) % this.fTableSize;

      for(SymbolTable.Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (entry.symbol.equals(symbol)) {
            return entry.symbol;
         }

         ++collisionCount;
      }

      return this.addSymbol0(symbol, bucket, collisionCount);
   }

   private String addSymbol0(String symbol, int bucket, int collisionCount) {
      if (this.fCount >= this.fThreshold) {
         this.rehash();
         bucket = this.hash(symbol) % this.fTableSize;
      } else if (collisionCount >= this.fCollisionThreshold) {
         this.rebalance();
         bucket = this.hash(symbol) % this.fTableSize;
      }

      SymbolTable.Entry entry = new SymbolTable.Entry(symbol, this.fBuckets[bucket]);
      this.fBuckets[bucket] = entry;
      ++this.fCount;
      return entry.symbol;
   }

   public String addSymbol(char[] buffer, int offset, int length) {
      int collisionCount = 0;
      int bucket = this.hash(buffer, offset, length) % this.fTableSize;

      label28:
      for(SymbolTable.Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (length == entry.characters.length) {
            for(int i = 0; i < length; ++i) {
               if (buffer[offset + i] != entry.characters[i]) {
                  ++collisionCount;
                  continue label28;
               }
            }

            return entry.symbol;
         } else {
            ++collisionCount;
         }
      }

      return this.addSymbol0(buffer, offset, length, bucket, collisionCount);
   }

   private String addSymbol0(char[] buffer, int offset, int length, int bucket, int collisionCount) {
      if (this.fCount >= this.fThreshold) {
         this.rehash();
         bucket = this.hash(buffer, offset, length) % this.fTableSize;
      } else if (collisionCount >= this.fCollisionThreshold) {
         this.rebalance();
         bucket = this.hash(buffer, offset, length) % this.fTableSize;
      }

      SymbolTable.Entry entry = new SymbolTable.Entry(buffer, offset, length, this.fBuckets[bucket]);
      this.fBuckets[bucket] = entry;
      ++this.fCount;
      return entry.symbol;
   }

   public int hash(String symbol) {
      return this.fHashMultipliers == null ? symbol.hashCode() & Integer.MAX_VALUE : this.hash0(symbol);
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

   public int hash(char[] buffer, int offset, int length) {
      if (this.fHashMultipliers != null) {
         return this.hash0(buffer, offset, length);
      } else {
         int code = 0;

         for(int i = 0; i < length; ++i) {
            code = code * 31 + buffer[offset + i];
         }

         return code & Integer.MAX_VALUE;
      }
   }

   private int hash0(char[] buffer, int offset, int length) {
      int code = 0;
      int[] multipliers = this.fHashMultipliers;

      for(int i = 0; i < length; ++i) {
         code = code * multipliers[i & 31] + buffer[offset + i];
      }

      return code & Integer.MAX_VALUE;
   }

   protected void rehash() {
      this.rehashCommon(this.fBuckets.length * 2 + 1);
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
      SymbolTable.Entry[] oldTable = this.fBuckets;
      SymbolTable.Entry[] newTable = new SymbolTable.Entry[newCapacity];
      this.fThreshold = (int)((float)newCapacity * this.fLoadFactor);
      this.fBuckets = newTable;
      this.fTableSize = this.fBuckets.length;
      int i = oldCapacity;

      SymbolTable.Entry e;
      int index;
      while(i-- > 0) {
         for(SymbolTable.Entry old = oldTable[i]; old != null; newTable[index] = e) {
            e = old;
            old = old.next;
            index = this.hash(e.symbol) % newCapacity;
            e.next = newTable[index];
         }
      }

   }

   public boolean containsSymbol(String symbol) {
      int bucket = this.hash(symbol) % this.fTableSize;
      int length = symbol.length();

      label27:
      for(SymbolTable.Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (length == entry.characters.length) {
            for(int i = 0; i < length; ++i) {
               if (symbol.charAt(i) != entry.characters[i]) {
                  continue label27;
               }
            }

            return true;
         }
      }

      return false;
   }

   public boolean containsSymbol(char[] buffer, int offset, int length) {
      int bucket = this.hash(buffer, offset, length) % this.fTableSize;

      label27:
      for(SymbolTable.Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
         if (length == entry.characters.length) {
            for(int i = 0; i < length; ++i) {
               if (buffer[offset + i] != entry.characters[i]) {
                  continue label27;
               }
            }

            return true;
         }
      }

      return false;
   }

   protected static final class Entry {
      public final String symbol;
      public final char[] characters;
      public SymbolTable.Entry next;

      public Entry(String symbol, SymbolTable.Entry next) {
         this.symbol = symbol.intern();
         this.characters = new char[symbol.length()];
         symbol.getChars(0, this.characters.length, this.characters, 0);
         this.next = next;
      }

      public Entry(char[] ch, int offset, int length, SymbolTable.Entry next) {
         this.characters = new char[length];
         System.arraycopy(ch, offset, this.characters, 0, length);
         this.symbol = (new String(this.characters)).intern();
         this.next = next;
      }
   }
}
