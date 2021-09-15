package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringIntMap extends KeyIntMap {
   protected static final StringIntMap.Entry NULL_ENTRY = new StringIntMap.Entry((String)null, 0, -1, (StringIntMap.Entry)null);
   protected StringIntMap _readOnlyMap;
   protected StringIntMap.Entry _lastEntry;
   protected StringIntMap.Entry[] _table;
   protected int _index;
   protected int _totalCharacterCount;

   public StringIntMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this._lastEntry = NULL_ENTRY;
      this._table = new StringIntMap.Entry[this._capacity];
   }

   public StringIntMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public StringIntMap() {
      this(16, 0.75F);
   }

   public void clear() {
      for(int i = 0; i < this._table.length; ++i) {
         this._table[i] = null;
      }

      this._lastEntry = NULL_ENTRY;
      this._size = 0;
      this._index = this._readOnlyMapSize;
      this._totalCharacterCount = 0;
   }

   public void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
      if (!(readOnlyMap instanceof StringIntMap)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyMap}));
      } else {
         this.setReadOnlyMap((StringIntMap)readOnlyMap, clear);
      }
   }

   public final void setReadOnlyMap(StringIntMap readOnlyMap, boolean clear) {
      this._readOnlyMap = readOnlyMap;
      if (this._readOnlyMap != null) {
         this._readOnlyMapSize = this._readOnlyMap.size();
         this._index = this._size + this._readOnlyMapSize;
         if (clear) {
            this.clear();
         }
      } else {
         this._readOnlyMapSize = 0;
         this._index = this._size;
      }

   }

   public final int getNextIndex() {
      return this._index++;
   }

   public final int getIndex() {
      return this._index;
   }

   public final int obtainIndex(String key) {
      int hash = hashHash(key.hashCode());
      int tableIndex;
      if (this._readOnlyMap != null) {
         tableIndex = this._readOnlyMap.get(key, hash);
         if (tableIndex != -1) {
            return tableIndex;
         }
      }

      tableIndex = indexFor(hash, this._table.length);

      for(StringIntMap.Entry e = this._table[tableIndex]; e != null; e = e._next) {
         if (e._hash == hash && this.eq(key, e._key)) {
            return e._value;
         }
      }

      this.addEntry(key, hash, tableIndex);
      return -1;
   }

   public final void add(String key) {
      int hash = hashHash(key.hashCode());
      int tableIndex = indexFor(hash, this._table.length);
      this.addEntry(key, hash, tableIndex);
   }

   public final int get(String key) {
      return key == this._lastEntry._key ? this._lastEntry._value : this.get(key, hashHash(key.hashCode()));
   }

   public final int getTotalCharacterCount() {
      return this._totalCharacterCount;
   }

   private final int get(String key, int hash) {
      int i;
      if (this._readOnlyMap != null) {
         i = this._readOnlyMap.get(key, hash);
         if (i != -1) {
            return i;
         }
      }

      i = indexFor(hash, this._table.length);

      for(StringIntMap.Entry e = this._table[i]; e != null; e = e._next) {
         if (e._hash == hash && this.eq(key, e._key)) {
            this._lastEntry = e;
            return e._value;
         }
      }

      return -1;
   }

   private final void addEntry(String key, int hash, int bucketIndex) {
      StringIntMap.Entry e = this._table[bucketIndex];
      this._table[bucketIndex] = new StringIntMap.Entry(key, hash, this._index++, e);
      this._totalCharacterCount += key.length();
      if (this._size++ >= this._threshold) {
         this.resize(2 * this._table.length);
      }

   }

   protected final void resize(int newCapacity) {
      this._capacity = newCapacity;
      StringIntMap.Entry[] oldTable = this._table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1048576) {
         this._threshold = Integer.MAX_VALUE;
      } else {
         StringIntMap.Entry[] newTable = new StringIntMap.Entry[this._capacity];
         this.transfer(newTable);
         this._table = newTable;
         this._threshold = (int)((float)this._capacity * this._loadFactor);
      }
   }

   private final void transfer(StringIntMap.Entry[] newTable) {
      StringIntMap.Entry[] src = this._table;
      int newCapacity = newTable.length;

      for(int j = 0; j < src.length; ++j) {
         StringIntMap.Entry e = src[j];
         if (e != null) {
            src[j] = null;

            StringIntMap.Entry next;
            do {
               next = e._next;
               int i = indexFor(e._hash, newCapacity);
               e._next = newTable[i];
               newTable[i] = e;
               e = next;
            } while(next != null);
         }
      }

   }

   private final boolean eq(String x, String y) {
      return x == y || x.equals(y);
   }

   protected static class Entry extends KeyIntMap.BaseEntry {
      final String _key;
      StringIntMap.Entry _next;

      public Entry(String key, int hash, int value, StringIntMap.Entry next) {
         super(hash, value);
         this._key = key;
         this._next = next;
      }
   }
}
