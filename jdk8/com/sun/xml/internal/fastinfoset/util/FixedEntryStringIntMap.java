package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class FixedEntryStringIntMap extends StringIntMap {
   private StringIntMap.Entry _fixedEntry;

   public FixedEntryStringIntMap(String fixedEntry, int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      int hash = hashHash(fixedEntry.hashCode());
      int tableIndex = indexFor(hash, this._table.length);
      this._table[tableIndex] = this._fixedEntry = new StringIntMap.Entry(fixedEntry, hash, this._index++, (StringIntMap.Entry)null);
      if (this._size++ >= this._threshold) {
         this.resize(2 * this._table.length);
      }

   }

   public FixedEntryStringIntMap(String fixedEntry, int initialCapacity) {
      this(fixedEntry, initialCapacity, 0.75F);
   }

   public FixedEntryStringIntMap(String fixedEntry) {
      this(fixedEntry, 16, 0.75F);
   }

   public final void clear() {
      int tableIndex;
      for(tableIndex = 0; tableIndex < this._table.length; ++tableIndex) {
         this._table[tableIndex] = null;
      }

      this._lastEntry = NULL_ENTRY;
      if (this._fixedEntry != null) {
         tableIndex = indexFor(this._fixedEntry._hash, this._table.length);
         this._table[tableIndex] = this._fixedEntry;
         this._fixedEntry._next = null;
         this._size = 1;
         this._index = this._readOnlyMapSize + 1;
      } else {
         this._size = 0;
         this._index = this._readOnlyMapSize;
      }

   }

   public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
      if (!(readOnlyMap instanceof FixedEntryStringIntMap)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyMap}));
      } else {
         this.setReadOnlyMap((FixedEntryStringIntMap)readOnlyMap, clear);
      }
   }

   public final void setReadOnlyMap(FixedEntryStringIntMap readOnlyMap, boolean clear) {
      this._readOnlyMap = readOnlyMap;
      if (this._readOnlyMap != null) {
         readOnlyMap.removeFixedEntry();
         this._readOnlyMapSize = readOnlyMap.size();
         this._index = this._readOnlyMapSize + this._size;
         if (clear) {
            this.clear();
         }
      } else {
         this._readOnlyMapSize = 0;
      }

   }

   private final void removeFixedEntry() {
      if (this._fixedEntry != null) {
         int tableIndex = indexFor(this._fixedEntry._hash, this._table.length);
         StringIntMap.Entry firstEntry = this._table[tableIndex];
         if (firstEntry == this._fixedEntry) {
            this._table[tableIndex] = this._fixedEntry._next;
         } else {
            StringIntMap.Entry previousEntry;
            for(previousEntry = firstEntry; previousEntry._next != this._fixedEntry; previousEntry = previousEntry._next) {
            }

            previousEntry._next = this._fixedEntry._next;
         }

         this._fixedEntry = null;
         --this._size;
      }

   }
}
