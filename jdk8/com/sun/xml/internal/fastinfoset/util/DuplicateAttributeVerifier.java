package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;

public class DuplicateAttributeVerifier {
   public static final int MAP_SIZE = 256;
   public int _currentIteration;
   private DuplicateAttributeVerifier.Entry[] _map;
   public final DuplicateAttributeVerifier.Entry _poolHead;
   public DuplicateAttributeVerifier.Entry _poolCurrent;
   private DuplicateAttributeVerifier.Entry _poolTail;

   public DuplicateAttributeVerifier() {
      this._poolTail = this._poolHead = new DuplicateAttributeVerifier.Entry();
   }

   public final void clear() {
      this._currentIteration = 0;

      for(DuplicateAttributeVerifier.Entry e = this._poolHead; e != null; e = e.poolNext) {
         e.iteration = 0;
      }

      this.reset();
   }

   public final void reset() {
      this._poolCurrent = this._poolHead;
      if (this._map == null) {
         this._map = new DuplicateAttributeVerifier.Entry[256];
      }

   }

   private final void increasePool(int capacity) {
      if (this._map == null) {
         this._map = new DuplicateAttributeVerifier.Entry[256];
         this._poolCurrent = this._poolHead;
      } else {
         DuplicateAttributeVerifier.Entry tail = this._poolTail;

         for(int i = 0; i < capacity; ++i) {
            DuplicateAttributeVerifier.Entry e = new DuplicateAttributeVerifier.Entry();
            this._poolTail.poolNext = e;
            this._poolTail = e;
         }

         this._poolCurrent = tail.poolNext;
      }

   }

   public final void checkForDuplicateAttribute(int hash, int value) throws FastInfosetException {
      if (this._poolCurrent == null) {
         this.increasePool(16);
      }

      DuplicateAttributeVerifier.Entry newEntry = this._poolCurrent;
      this._poolCurrent = this._poolCurrent.poolNext;
      DuplicateAttributeVerifier.Entry head = this._map[hash];
      if (head != null && head.iteration >= this._currentIteration) {
         DuplicateAttributeVerifier.Entry e = head;

         do {
            if (e.value == value) {
               this.reset();
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
            }
         } while((e = e.hashNext) != null);

         newEntry.hashNext = head;
         this._map[hash] = newEntry;
         newEntry.iteration = this._currentIteration;
         newEntry.value = value;
      } else {
         newEntry.hashNext = null;
         this._map[hash] = newEntry;
         newEntry.iteration = this._currentIteration;
         newEntry.value = value;
      }

   }

   public static class Entry {
      private int iteration;
      private int value;
      private DuplicateAttributeVerifier.Entry hashNext;
      private DuplicateAttributeVerifier.Entry poolNext;
   }
}
