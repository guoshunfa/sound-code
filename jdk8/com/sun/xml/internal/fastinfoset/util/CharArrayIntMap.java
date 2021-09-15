package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayIntMap extends KeyIntMap {
   private CharArrayIntMap _readOnlyMap;
   protected int _totalCharacterCount;
   private CharArrayIntMap.Entry[] _table;

   public CharArrayIntMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this._table = new CharArrayIntMap.Entry[this._capacity];
   }

   public CharArrayIntMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CharArrayIntMap() {
      this(16, 0.75F);
   }

   public final void clear() {
      for(int i = 0; i < this._table.length; ++i) {
         this._table[i] = null;
      }

      this._size = 0;
      this._totalCharacterCount = 0;
   }

   public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
      if (!(readOnlyMap instanceof CharArrayIntMap)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyMap}));
      } else {
         this.setReadOnlyMap((CharArrayIntMap)readOnlyMap, clear);
      }
   }

   public final void setReadOnlyMap(CharArrayIntMap readOnlyMap, boolean clear) {
      this._readOnlyMap = readOnlyMap;
      if (this._readOnlyMap != null) {
         this._readOnlyMapSize = this._readOnlyMap.size();
         if (clear) {
            this.clear();
         }
      } else {
         this._readOnlyMapSize = 0;
      }

   }

   public final int get(char[] ch, int start, int length) {
      int hash = hashHash(CharArray.hashCode(ch, start, length));
      return this.get(ch, start, length, hash);
   }

   public final int obtainIndex(char[] ch, int start, int length, boolean clone) {
      int hash = hashHash(CharArray.hashCode(ch, start, length));
      int tableIndex;
      if (this._readOnlyMap != null) {
         tableIndex = this._readOnlyMap.get(ch, start, length, hash);
         if (tableIndex != -1) {
            return tableIndex;
         }
      }

      tableIndex = indexFor(hash, this._table.length);

      for(CharArrayIntMap.Entry e = this._table[tableIndex]; e != null; e = e._next) {
         if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
            return e._value;
         }
      }

      if (clone) {
         char[] chClone = new char[length];
         System.arraycopy(ch, start, chClone, 0, length);
         ch = chClone;
         start = 0;
      }

      this.addEntry(ch, start, length, hash, this._size + this._readOnlyMapSize, tableIndex);
      return -1;
   }

   public final int getTotalCharacterCount() {
      return this._totalCharacterCount;
   }

   private final int get(char[] ch, int start, int length, int hash) {
      int i;
      if (this._readOnlyMap != null) {
         i = this._readOnlyMap.get(ch, start, length, hash);
         if (i != -1) {
            return i;
         }
      }

      i = indexFor(hash, this._table.length);

      for(CharArrayIntMap.Entry e = this._table[i]; e != null; e = e._next) {
         if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
            return e._value;
         }
      }

      return -1;
   }

   private final void addEntry(char[] ch, int start, int length, int hash, int value, int bucketIndex) {
      CharArrayIntMap.Entry e = this._table[bucketIndex];
      this._table[bucketIndex] = new CharArrayIntMap.Entry(ch, start, length, hash, value, e);
      this._totalCharacterCount += length;
      if (this._size++ >= this._threshold) {
         this.resize(2 * this._table.length);
      }

   }

   private final void resize(int newCapacity) {
      this._capacity = newCapacity;
      CharArrayIntMap.Entry[] oldTable = this._table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1048576) {
         this._threshold = Integer.MAX_VALUE;
      } else {
         CharArrayIntMap.Entry[] newTable = new CharArrayIntMap.Entry[this._capacity];
         this.transfer(newTable);
         this._table = newTable;
         this._threshold = (int)((float)this._capacity * this._loadFactor);
      }
   }

   private final void transfer(CharArrayIntMap.Entry[] newTable) {
      CharArrayIntMap.Entry[] src = this._table;
      int newCapacity = newTable.length;

      for(int j = 0; j < src.length; ++j) {
         CharArrayIntMap.Entry e = src[j];
         if (e != null) {
            src[j] = null;

            CharArrayIntMap.Entry next;
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

   static class Entry extends KeyIntMap.BaseEntry {
      final char[] _ch;
      final int _start;
      final int _length;
      CharArrayIntMap.Entry _next;

      public Entry(char[] ch, int start, int length, int hash, int value, CharArrayIntMap.Entry next) {
         super(hash, value);
         this._ch = ch;
         this._start = start;
         this._length = length;
         this._next = next;
      }

      public final boolean equalsCharArray(char[] ch, int start, int length) {
         if (this._length == length) {
            int n = this._length;
            int i = this._start;
            int var6 = start;

            do {
               if (n-- == 0) {
                  return true;
               }
            } while(this._ch[i++] == ch[var6++]);

            return false;
         } else {
            return false;
         }
      }
   }
}
