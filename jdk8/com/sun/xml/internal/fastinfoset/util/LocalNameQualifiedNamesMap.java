package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;

public class LocalNameQualifiedNamesMap extends KeyIntMap {
   private LocalNameQualifiedNamesMap _readOnlyMap;
   private int _index;
   private LocalNameQualifiedNamesMap.Entry[] _table;

   public LocalNameQualifiedNamesMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this._table = new LocalNameQualifiedNamesMap.Entry[this._capacity];
   }

   public LocalNameQualifiedNamesMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public LocalNameQualifiedNamesMap() {
      this(16, 0.75F);
   }

   public final void clear() {
      for(int i = 0; i < this._table.length; ++i) {
         this._table[i] = null;
      }

      this._size = 0;
      if (this._readOnlyMap != null) {
         this._index = this._readOnlyMap.getIndex();
      } else {
         this._index = 0;
      }

   }

   public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
      if (!(readOnlyMap instanceof LocalNameQualifiedNamesMap)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyMap}));
      } else {
         this.setReadOnlyMap((LocalNameQualifiedNamesMap)readOnlyMap, clear);
      }
   }

   public final void setReadOnlyMap(LocalNameQualifiedNamesMap readOnlyMap, boolean clear) {
      this._readOnlyMap = readOnlyMap;
      if (this._readOnlyMap != null) {
         this._readOnlyMapSize = this._readOnlyMap.size();
         this._index = this._readOnlyMap.getIndex();
         if (clear) {
            this.clear();
         }
      } else {
         this._readOnlyMapSize = 0;
         this._index = 0;
      }

   }

   public final boolean isQNameFromReadOnlyMap(QualifiedName name) {
      return this._readOnlyMap != null && name.index <= this._readOnlyMap.getIndex();
   }

   public final int getNextIndex() {
      return this._index++;
   }

   public final int getIndex() {
      return this._index;
   }

   public final LocalNameQualifiedNamesMap.Entry obtainEntry(String key) {
      int hash = hashHash(key.hashCode());
      if (this._readOnlyMap != null) {
         LocalNameQualifiedNamesMap.Entry entry = this._readOnlyMap.getEntry(key, hash);
         if (entry != null) {
            return entry;
         }
      }

      int tableIndex = indexFor(hash, this._table.length);

      for(LocalNameQualifiedNamesMap.Entry e = this._table[tableIndex]; e != null; e = e._next) {
         if (e._hash == hash && this.eq(key, e._key)) {
            return e;
         }
      }

      return this.addEntry(key, hash, tableIndex);
   }

   public final LocalNameQualifiedNamesMap.Entry obtainDynamicEntry(String key) {
      int hash = hashHash(key.hashCode());
      int tableIndex = indexFor(hash, this._table.length);

      for(LocalNameQualifiedNamesMap.Entry e = this._table[tableIndex]; e != null; e = e._next) {
         if (e._hash == hash && this.eq(key, e._key)) {
            return e;
         }
      }

      return this.addEntry(key, hash, tableIndex);
   }

   private final LocalNameQualifiedNamesMap.Entry getEntry(String key, int hash) {
      if (this._readOnlyMap != null) {
         LocalNameQualifiedNamesMap.Entry entry = this._readOnlyMap.getEntry(key, hash);
         if (entry != null) {
            return entry;
         }
      }

      int tableIndex = indexFor(hash, this._table.length);

      for(LocalNameQualifiedNamesMap.Entry e = this._table[tableIndex]; e != null; e = e._next) {
         if (e._hash == hash && this.eq(key, e._key)) {
            return e;
         }
      }

      return null;
   }

   private final LocalNameQualifiedNamesMap.Entry addEntry(String key, int hash, int bucketIndex) {
      LocalNameQualifiedNamesMap.Entry e = this._table[bucketIndex];
      this._table[bucketIndex] = new LocalNameQualifiedNamesMap.Entry(key, hash, e);
      e = this._table[bucketIndex];
      if (this._size++ >= this._threshold) {
         this.resize(2 * this._table.length);
      }

      return e;
   }

   private final void resize(int newCapacity) {
      this._capacity = newCapacity;
      LocalNameQualifiedNamesMap.Entry[] oldTable = this._table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1048576) {
         this._threshold = Integer.MAX_VALUE;
      } else {
         LocalNameQualifiedNamesMap.Entry[] newTable = new LocalNameQualifiedNamesMap.Entry[this._capacity];
         this.transfer(newTable);
         this._table = newTable;
         this._threshold = (int)((float)this._capacity * this._loadFactor);
      }
   }

   private final void transfer(LocalNameQualifiedNamesMap.Entry[] newTable) {
      LocalNameQualifiedNamesMap.Entry[] src = this._table;
      int newCapacity = newTable.length;

      for(int j = 0; j < src.length; ++j) {
         LocalNameQualifiedNamesMap.Entry e = src[j];
         if (e != null) {
            src[j] = null;

            LocalNameQualifiedNamesMap.Entry next;
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

   public static class Entry {
      final String _key;
      final int _hash;
      public QualifiedName[] _value;
      public int _valueIndex;
      LocalNameQualifiedNamesMap.Entry _next;

      public Entry(String key, int hash, LocalNameQualifiedNamesMap.Entry next) {
         this._key = key;
         this._hash = hash;
         this._next = next;
         this._value = new QualifiedName[1];
      }

      public void addQualifiedName(QualifiedName name) {
         if (this._valueIndex < this._value.length) {
            this._value[this._valueIndex++] = name;
         } else if (this._valueIndex == this._value.length) {
            QualifiedName[] newValue = new QualifiedName[this._valueIndex * 3 / 2 + 1];
            System.arraycopy(this._value, 0, newValue, 0, this._valueIndex);
            this._value = newValue;
            this._value[this._valueIndex++] = name;
         }

      }
   }
}
